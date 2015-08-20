package com.sdbnet.hywy.employee.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.employee.ActivityStackManager;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.db.db.DBManager;
import com.sdbnet.hywy.employee.model.ItemModel;
import com.sdbnet.hywy.employee.net.AsyncHttpService;
import com.sdbnet.hywy.employee.ui.widget.DialogLoading;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.ErrLogUtils;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.UtilsCommon;
import com.sdbnet.hywy.employee.utils.UtilsError;

public class SelectProjectActivity extends Activity {
    private final String TAG = "SelectProjectActivity";
    //    private List<String> list = new ArrayList<String>();
    private List<ItemModel> mItemList = new ArrayList<>();
    //    private List<Map<String, String>> maps = new ArrayList<Map<String, String>>();
    private ProjectsAdapter mAdapter;

    private ListView mLvProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivityStackManager.getStackManager().pushActivity(this);
        setContentView(R.layout.activity_choose_projects);
        initUI();
        loadDatas();
    }

    private void initUI() {
        findViewById(R.id.common_view_title_img).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.common_view_title_text))
                .setText(R.string.select_company_project);
        mLvProject = (ListView) findViewById(R.id.activity_choose_project_lv);
        mAdapter = new ProjectsAdapter();
        mLvProject.setAdapter(mAdapter);
        mLvProject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG,position+"-"+id+">"+mItemList.get(position));
                confirmSelected(mItemList.get(position));
            }
        });
    }

    /**
     * 加载用户所属的项目
     */
    private void loadDatas() {
        String items = PreferencesUtil.getValue(PreferencesUtil.KEY_ITEMS, "");
//        Log.e(TAG,items);
        if (TextUtils.isEmpty(items)) {
            Log.e(TAG, "get items error");
            return;
        }
        JSONObject jsonObj;
        ItemModel itemModel;
        try {
            JSONArray arrs = new JSONArray(items);
            for (int i = 0; i < arrs.length(); i++) {
                jsonObj = arrs.getJSONObject(i);
                itemModel = new ItemModel();
                itemModel.cmpid = jsonObj.getString(Constants.Feild.KEY_COMPANY_ID);
                itemModel.cmpname = jsonObj.getString(Constants.Feild.KEY_COMPANY_NAME);
                itemModel.itemid = jsonObj.getString(Constants.Feild.KEY_ITEM_ID);
                itemModel.itemName = jsonObj.getString(Constants.Feild.KEY_ITEM_NAME);
                itemModel.pid = jsonObj.getString(Constants.Feild.KEY_STAFF_ID);
                itemModel.pname = jsonObj.getString(Constants.Feild.KEY_STAFF_NAME);
                mItemList.add(itemModel);
            }
        } catch (JSONException e) {
            ErrLogUtils.uploadErrLog(SelectProjectActivity.this,
                    ErrLogUtils.toString(e));
            e.printStackTrace();
        }
        mAdapter.notifyDataSetChanged();

    }

    /**
     * 获取用户所选择的项目
     *
     * @param itemModel
     */
    private void confirmSelected(ItemModel itemModel) {
        Log.e(TAG, itemModel + "");
        if (itemModel != null) {


            PreferencesUtil.putValue(PreferencesUtil.KEY_COMPANY_ID, itemModel.cmpid);
            PreferencesUtil.putValue(PreferencesUtil.KEY_COMPANY_NAME, itemModel.cmpname);
            PreferencesUtil.putValue(PreferencesUtil.KEY_ITEM_ID, itemModel.itemid);
            PreferencesUtil.putValue(PreferencesUtil.KEY_ITEM_NAME, itemModel.itemName);
            PreferencesUtil.putValue(PreferencesUtil.KEY_USER_ID, itemModel.pid);
            PreferencesUtil.putValue(PreferencesUtil.KEY_USER_NAME, itemModel.pname);

            PreferencesUtil.initStoreData();

            getPermission(itemModel.cmpid, itemModel.itemid, itemModel.pid);
        }
    }

    /**
     * 获取对项目的操作权限
     *
     * @param cmpid  公司编码
     * @param itemid 项目特征码
     * @param pid    用户ID
     */
    private void getPermission(String cmpid, String itemid, String pid) {
        AsyncHttpService.getPermissionWithProject(cmpid, itemid, pid,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        showLoadDialog(R.string.xlistview_header_hint_loading);
                        super.onStart();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                        dismissLoadDialog();
                        showShortToast(getString(R.string.httpisNull));
                        // mDialogLoading.dismiss();

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        Log.d(TAG, "onsuccess: " + response.toString());
                        super.onSuccess(statusCode, headers, response);
                        try {

                            if (UtilsError.isErrorCode(
                                    SelectProjectActivity.this, response)) {
                                return;
                            }

                            JSONObject jsonObj = response
                                    .getJSONObject(Constants.Feild.KEY_STAFF);
                            String menu = jsonObj
                                    .getString(Constants.Feild.KEY_MENU);
                            if (TextUtils.isEmpty(menu)) {
                                showLongToast(getString(R.string.no_permission_tip_msg));
                                return;
                            }
                            PreferencesUtil.putValue(
                                    PreferencesUtil.KEY_EXECUTE_MENU, menu);

                            int codeLength = jsonObj
                                    .getInt(Constants.Feild.KEY_CODE_LENGTH);
                            PreferencesUtil
                                    .putValue(PreferencesUtil.KEY_CODE_LENGTH,
                                            codeLength);

                            String scan = jsonObj
                                    .getString(Constants.Feild.KEY_EXECUTE_ACTION);
                            PreferencesUtil.putValue(
                                    PreferencesUtil.KEY_EXECUTE_ACTION, scan);

                            String logo = jsonObj
                                    .getString(Constants.Feild.KEY_COMPANY_LOGO);
                            PreferencesUtil.putValue(
                                    PreferencesUtil.KEY_COMPANY_LOGO, logo);

                            PreferencesUtil.putValue(
                                    PreferencesUtil.KEY_USER_IS_DRIVER,
                                    jsonObj.getString(Constants.Feild.KEY_STAFF_IS_DRIVER));
                            PreferencesUtil.putValue(
                                    PreferencesUtil.KEY_ORDTITLE,
                                    jsonObj.getString(Constants.Feild.KEY_COMPANY_ORDTITLE));
                            PreferencesUtil.putValue(
                                    PreferencesUtil.KEY_CNOTITLE,
                                    jsonObj.getString(Constants.Feild.KEY_COMPANY_CNOTITLE));
                            int workStatus = jsonObj
                                    .getInt(Constants.Feild.KEY_IS_WORKING);
                            PreferencesUtil.putValue(
                                    PreferencesUtil.KEY_IS_WORKING, workStatus);
                            boolean isWorking = (workStatus == 1);
                            PreferencesUtil.putValue(
                                    PreferencesUtil.KEY_WORK_STATS, isWorking);

                            // options
                            // --data
                            // --scanModel
                            if (!jsonObj.isNull(Constants.Feild.KEY_OPTIONS)) {
                                String options = jsonObj
                                        .getString(Constants.Feild.KEY_OPTIONS);
                                PreferencesUtil.putValue(
                                        Constants.Feild.KEY_OPTIONS, options);
                            }

                            enterMain();

                        } catch (Exception ex) {

                            showShortToast(getString(R.string.network_busy_please_try_again_later));
                            ex.printStackTrace();

                        } finally {
                            dismissLoadDialog();
                        }
                    }
                }, SelectProjectActivity.this);
    }

    private void enterMain() {

        PreferencesUtil.initStoreData();
        uploadDeviceInfo();

        // openActivity(MainActivity.class);
        // defaultFinish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
//        finish();
        ActivityStackManager.getStackManager().popAllActivitys();
        // uploadDeviceInfo();
        // PreferencesUtil.initStoreData();
        // Intent intent = new Intent(
        // SelectProjectActivity.this,
        // MainActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
        // | intent.FLAG_ACTIVITY_NEW_TASK);
        // startActivity(intent);
    }

    /**
     * 上传设备信息
     */
    private void uploadDeviceInfo() {
        TelephonyManager tm = (TelephonyManager) this
                .getSystemService(TELEPHONY_SERVICE);
        AsyncHttpService.upDeviceInfo(PreferencesUtil.user_tel, "",
                tm.getDeviceId(), new JsonHttpResponseHandler() {

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                        showShortToast(getResources().getString(
                                R.string.httpisNull));
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        System.out.println("onsuccess: " + response.toString());
                        super.onSuccess(statusCode, headers, response);
                        try {
                            UtilsError.isErrorCode(SelectProjectActivity.this,
                                    response);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showShortToast(getString(R.string.network_busy_please_try_again_later));
                        }
                    }

                }, this);
    }

    class ProjectsAdapter extends BaseAdapter {


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            final Map<String, String> map = maps.get(position);
            ItemModel itemModel = mItemList.get(position);
            ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(SelectProjectActivity.this,
                        R.layout.item_projects, null);
                viewHolder.tv_project = (TextView) convertView
                        .findViewById(R.id.tv_project);
                viewHolder.layout = (LinearLayout) convertView
                        .findViewById(R.id.layout_AutoPlay);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
//            viewHolder.tv_project.setText(list.get(position));
            viewHolder.tv_project.setText(itemModel.cmpname + "-" + itemModel.itemName);
            return convertView;

        }

        @Override
        public int getCount() {
            return mItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return mItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    }

    static class ViewHolder {
        TextView tv_project;
        LinearLayout layout;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onDestroy() {
        ActivityStackManager.getStackManager().popActivity(this);
        super.onDestroy();

    }

    protected void showShortToast(int pResId) {
        showShortToast(getString(pResId));
    }

    protected void showLongToast(int pResId) {
        showLongToast(getString(pResId));
    }

    protected void showLongToast(String pMsg) {
        Toast.makeText(this, pMsg, Toast.LENGTH_LONG).show();
    }

    protected void showShortToast(String pMsg) {
        Toast.makeText(this, pMsg, Toast.LENGTH_SHORT).show();
    }

    protected void saveOperateLog(String content) {
        saveOperateLog(content, null);

    }

    protected void saveOperateLog(String content, Date date) {

        if (!UtilsCommon.checkAccount()) {
            return;
        }
        DBManager manager = new DBManager(this);
        manager.saveOperate(content, date);
        manager.closeDatabase();
    }

    private DialogLoading mDialogLoading;

    protected void initDialog() {
        if (mDialogLoading == null)
            mDialogLoading = new DialogLoading(this);
    }

    protected void showLoadDialog(int resId) {
        showLoadDialog(getString(resId));
    }

    protected void showLoadDialog(String msg) {
        initDialog();
        mDialogLoading.setMessage(msg);
        mDialogLoading.show();
    }

    protected void showLoadDialog() {
        initDialog();
        mDialogLoading.show();
    }

    protected void dismissLoadDialog() {
        initDialog();
        if (mDialogLoading.isShowing())
            mDialogLoading.dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS,
                    Constants.Value.WORKED);
            PreferencesUtil.clearLocalData(this);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
