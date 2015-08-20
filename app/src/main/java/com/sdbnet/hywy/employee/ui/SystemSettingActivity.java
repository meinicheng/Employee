package com.sdbnet.hywy.employee.ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sdbnet.hywy.employee.ActivityStackManager;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.service.TimeUploadService;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;
import com.sdbnet.hywy.employee.ui.widget.DialogUpdateApp;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.DataCleanManager;
import com.sdbnet.hywy.employee.utils.LogUtil;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.UtilsAndroid;
import com.sdbnet.hywy.employee.utils.UtilsCommon;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class SystemSettingActivity extends BaseActivity {

    private ImageView mBack;
    private TextView mTextTitle;
    private ListView lvSettings;
    private SimpleAdapter sAdapter;
    private String SETTING_NAME = "setting_name";
    private String SETTING_MSG = "seting_msg";
    private List<Map<String, String>> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ActivityStackManager.getStackManager().pushActivity(this);
        setContentView(R.layout.activity_system_setting);

        initBaseDatas();
        initControls();
        // test error
        // Log.e(TAG, test.toString());
        // System.out.print(0/0);
    }

    // private UserModel test;

    /**
     * 初始化控件
     */
    private void initControls() {
        // mBack = (ImageView)
        // findViewById(R.id.activity_system_setting_img_back);
        mTextTitle = (TextView) findViewById(R.id.common_view_title_text);
        mTextTitle.setText(R.string.set);
        mBack = (ImageView) findViewById(R.id.common_view_title_img);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sAdapter = new SimpleAdapter(this, mDataList,
                R.layout.item_system_setting, new String[]{SETTING_NAME,
                SETTING_MSG}, new int[]{
                R.id.item_system_setting_tvSettingName,
                R.id.item_system_set_text_msg});
        lvSettings = (ListView) findViewById(R.id.activity_system_setting_lvSetting);
        lvSettings.setAdapter(sAdapter);

        lvSettings.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Map<String, String> map = mDataList.get(position);
                saveOperateLog(map.get(SETTING_NAME), null);
                switch (position) {
                    case 0:
                        mDataList.get(0).put(SETTING_MSG, initTrafficStats());
                        sAdapter.notifyDataSetChanged();
                        break;
                    case 1:
                        // UmengUpdateAgent.forceUpdate(SystemSettingActivity.this);
                        // showShortToast(R.string.updated_latest_version);
                        initUpdate();

                        break;
                    case 2:
                        // restartApplication();
                        break;
                    case 3:

                        break;
                    case 4:
                        openActivity(SystemResetPwdActivity.class);
                        break;
                    case 5:
                        uploadExection();
                        break;

                    default:
                        break;
                }
            }
        });
    }

    /**
     * 点击退出按钮响应事件
     *
     * @param v
     */
    public void existSystem(View v) {
        saveOperateLog(((Button) v).getText().toString(), null);
        showAlertDialog(getString(R.string.warm_prompt),
                getString(R.string.confirm_exit_current_account),// "是否确认退出当前帐号？",
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // 点击ok，退出当前帐号
                        existApp();
                    }

                }, new OnClickListener() {
                    // 点击cancel，取消操作
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                    }
                }, new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface arg0) {
                    }
                });
    }

    private void existApp() {
        UtilsCommon.operateLogUpload(this);
        // clear preferences
        PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS,
                Constants.Value.WORKED);
        PreferencesUtil.clearLocalData(PreferenceManager
                .getDefaultSharedPreferences(SystemSettingActivity.this));
        // Stop Service;
        Intent service = new Intent(this, TimeUploadService.class);
        stopService(service);

        // existApp
        Intent intent = new Intent(SystemSettingActivity.this,
                UserLoginActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
        // | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        ActivityStackManager.getStackManager().popAllActivitys();
//		finish();

    }

    public void finishLocationBroadCast() {
        if (mLocateService != null)
            mLocateService.abortLocation();
    }

    /**
     * 加载设置选项
     *
     * @return
     */
    private void initBaseDatas() {
        mDataList = new ArrayList<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        map.put(SETTING_NAME, getString(R.string.traffic_statistics));
        map.put(SETTING_MSG, initTrafficStats());
        mDataList.add(map);
        map = new HashMap<String, String>();
        map.put(SETTING_NAME, getString(R.string.version_update));

        mDataList.add(map);
        map = new HashMap<String, String>();
        map.put(SETTING_NAME, getString(R.string.suggestion_feedback));
        mDataList.add(map);
        map = new HashMap<String, String>();
        map.put(SETTING_NAME, getString(R.string.clear_cache));
        mDataList.add(map);
        map = new HashMap<String, String>();
        map.put(SETTING_NAME, getString(R.string.change_pwd));
        mDataList.add(map);
        map = new HashMap<String, String>();
        map.put(SETTING_NAME, getString(R.string.opreation_log));
        mDataList.add(map);
    }

    private void uploadExection() {
        showAlertDialog(getString(R.string.warm_prompt),
                getString(R.string.is_upload_opreation_log),
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        UtilsCommon
                                .operateLogUpload(SystemSettingActivity.this);
                        showShortToast(R.string.operation_log_upload_success);
                        // OperateLogUtil
                        // .operateLogUpload(SystemSettingActivity.this);

                    }
                }, new OnClickListener() {
                    // 点击cancel，取消操作
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                    }
                }, new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface arg0) {
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // ActivityStackManager.getStackManager().popActivity(this);

    }

    // ImageLoader.getInstance().clearDiskCache();
    // ImageLoader.getInstance().clearMemoryCache();

    /**
     * 自动更新
     */
    private boolean forceUpdate;
    private DialogUpdateApp mDialogUpdateApp;

    private void initUpdate() {
        if (!UtilsAndroid.Set.checkNetState(this)) {
            showShortToast(R.string.httpError);
            return;
        }
        if (mDialogUpdateApp == null)
            mDialogUpdateApp = new DialogUpdateApp(this);
        forceUpdate = false;
        MobclickAgent.updateOnlineConfig(this);
        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.setUpdateAutoPopup(false);
        // 从友盟读取在线参数配置信息
        String update_mode = MobclickAgent
                .getConfigParams(this, "upgrade_mode");
        LogUtil.d(TAG, "upgrade_mode:" + update_mode);
        if (!TextUtils.isEmpty(update_mode)) {
            String[] params = update_mode.split(",");
            if (params.length == 2 && "F".equalsIgnoreCase(params[1])) {
                // F 表示强制更新
                forceUpdate = true;
            }
        }

        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

            @Override
            public void onUpdateReturned(int updateStatus,
                                         UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case UpdateStatus.Yes: // has update
                        mDialogUpdateApp.setMsg(updateInfo);
                        mDialogUpdateApp.show();
                        break;
                    case UpdateStatus.No: // has no update
                        showShortToast(R.string.updated_latest_version);
                        break;
                    default:
                        showShortToast(R.string.updated_latest_version);
                        break;
                }
            }

        });

        UmengUpdateAgent.update(this);
    }

    private String initTrafficStats() {

        long recordTrafficStats = PreferencesUtil.getValue(
                PreferencesUtil.KEY_TRAFFIC_STATS, 0l);
        int mAppUid = UtilsAndroid.Set.getAppUid(this);
        long lastRxTrafficStats = TrafficStats.getUidRxBytes(mAppUid);
        long lastTxTrafficStats = TrafficStats.getUidTxBytes(mAppUid);
        return formetFileSize(lastRxTrafficStats + lastTxTrafficStats
                + recordTrafficStats);

    }

    private String formetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        Log.e("formetFileSize", fileSizeString);
        return fileSizeString;
    }

    // private void operateLogUpload() {
    //
    // try {
    // DBManager manager = new DBManager(this);
    // List<OperateLog> list = manager.getAllOpreate();
    // manager.putLockOpreate();
    // manager.closeDatabase();
    //
    // if (null == list || list.size() == 0) {
    // showShortToast(R.string.operation_log_upload_success);
    // return;
    // }
    //
    // Log.i(TAG, list.toString());
    // StringEntity stringEntity = null;
    // stringEntity = new StringEntity(list.toString(), "utf-8");
    // // Log.i(TAG, stringEntity.toString());
    //
    // AsyncHttpClient client = new AsyncHttpClient();
    // client.post(this, AsyncHttpService.BASE_URL + "/upBatchOpeLog",
    // stringEntity, "application/json",
    // new JsonHttpResponseHandler() {
    // @Override
    // public void onStart() {
    // super.onStart();
    // showLoadDialog(R.string.is_submitted_ellipsis);
    // }
    //
    // @Override
    // public void onSuccess(int statusCode, Header[] headers,
    // JSONObject response) {
    // super.onSuccess(statusCode, headers, response);
    // LogUtil.d(response.toString());
    // try {
    // int errCode = response
    // .getInt(Constants.Feild.KEY_ERROR_CODE);
    // if (errCode == 0) {
    // showShortToast(R.string.operation_log_upload_success);
    // DBManager manager = new DBManager(
    // SystemSettingActivity.this);
    // manager.deleteAllOpreate();
    // manager.closeDatabase();
    // } else {
    // showShortToast(R.string.operation_log_upload_failed);
    // }
    // } catch (JSONException e) {
    // showShortToast(R.string.operation_log_upload_failed);
    // e.printStackTrace();
    // } finally {
    // dismissLoadDialog();
    // }
    //
    // }
    //
    // @Override
    // public void onCancel() {
    // dismissLoadDialog();
    // super.onCancel();
    //
    // }
    // });
    // } catch (Exception ex) {
    // showShortToast(R.string.operation_log_upload_failed);
    // ex.printStackTrace();
    // }
    // }

    private void restartApplication() {

        ActivityStackManager.getStackManager().popAllActivitys();
        Intent intent = this.getPackageManager().getLaunchIntentForPackage(
                this.getPackageName());
        // intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }

    private void clearCache() {
        ImageLoader.getInstance().clearMemoryCache();
        // ImageLoader.getInstance().clearDiskCache();
        DataCleanManager.deleteFilesByDirectory(Constants.SDBNET.SDPATH_CRASH);
        DataCleanManager.deleteFilesByDirectory(Constants.SDBNET.SDPATH_TEST);
        DataCleanManager
                .deleteFilesByDirectory(Constants.SDBNET.SDPATH_FORMATS);
        DataCleanManager.deleteFilesByDirectory(Constants.SDBNET.PATH_PHOTO);
    }
}
