package com.sdbnet.hywy.employee.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.model.ExecuteAction;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.ErrLogUtils;
import com.sdbnet.hywy.employee.utils.LogUtil;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.UtilsAndroid;
import com.sdbnet.hywy.employee.utils.UtilsBean;

public class OrderActionActivity extends BaseActivity implements
        OnClickListener,OnItemClickListener {
    private static final String TAG = "OrderActionActivity";
    // private LinearLayout lay_base_action;
    // private LinearLayout lay_sub_action;
    private static final int LEVEL_FIRST = 1;
    private static final int LEVEL_SECOND = 2;
    private static final int LEVEL_THIRD = 3;
    private int mCurrentLevel = LEVEL_FIRST;

    public static final String FILED_TYPE_FROM = "filed_type_from";
    public static final String VALUE_FROM_ACTION = "value_from_action";
    public static final String VALUE_FROM_ACCIDENT = "value_from_accident";

    private ImageView mImgBack;
    private List<ExecuteAction> mFirstActionList = new ArrayList<ExecuteAction>(); // 保存一级扫描按钮
    private List<ExecuteAction> mSecondActionList = new ArrayList<ExecuteAction>(); // 保存子级扫描按钮
    private List<ExecuteAction> mThirdActionList = new ArrayList<ExecuteAction>();// 保存子级的子级
    // private String subLevel = "00";
    // private String parentLevel;

    private String mFirstBtnName;
    private String firstAction;
    // private String mSecondBtnName;
    // private String sccondAction;
    // private String thirdBtName;
    // private String thirdAction;

    private ListView mListView;
    private List<ExecuteAction> mCurrentExecuteAction = new ArrayList<ExecuteAction>();
    private TextView mTextTitle;
    private String mTitle;

    private Button mBtnReport;
    private Button mBtnThrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ActivityStackManager.getStackManager().pushActivity(this);
        setContentView(R.layout.activity_action_base);
        UtilsAndroid.Set.openWifi(this);
        // UtilsAndroid.Set.openMobileNetWork(this);
        UtilsAndroid.Set.openGPSSettings(this);
        initBaseData();
        initUI();
    }

    private void initBaseData() {
        // TODO Auto-generated method stub
        // 获取扫描权限
        String execActions = PreferencesUtil.getValue(
                PreferencesUtil.KEY_EXECUTE_ACTION, null);
        LogUtil.d(TAG, "execActions=" + execActions);
        if (!TextUtils.isEmpty(execActions)) {
            try {
                JSONArray array = new JSONArray(execActions);
                // 解析扫描按钮
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObj = array.getJSONObject(i);
                    final ExecuteAction action = UtilsBean
                            .jsonToExecuteAction(jsonObj);
                    // LogUtil.d(TAG, "ExecuteAction=" + action.toString());
                    if (action.getAction().length() == 2) {
                        // 保存到父动作集合中
                        mFirstActionList.add(action);
                    } else if (action.getAction().length() == 4) {
                        // 保存到子动作集合中
                        mSecondActionList.add(action);
                    } else {
                        mThirdActionList.add(action);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ErrLogUtils.uploadErrLog(this, e);
                finish();
            }
        }
        mCurrentExecuteAction = mFirstActionList;

    }

    /**
     * 初始化控件
     */
    private void initUI() {
        mBtnReport = (Button) findViewById(R.id.activity_action_base_report_add_btn);
        mBtnReport.setOnClickListener(this);
        if (checkPermissions()) {
            mBtnReport.setVisibility(View.VISIBLE);
        }
        mBtnThrid= (Button) findViewById(R.id.activity_action_base_report_third_btn);
        mBtnThrid.setOnClickListener(this);
        if (isThridPermissons()) {
            mBtnThrid.setVisibility(View.VISIBLE);
        }
        // This Version
        mBtnReport.setVisibility(View.VISIBLE);// ????/

        mImgBack = (ImageView) findViewById(R.id.common_view_title_img);
        mImgBack.setOnClickListener(this);

        mTitle = String.format(Constants.Value.SCAN_ORDER,
                PreferencesUtil.ordtitle);
        mTextTitle = (TextView) findViewById(R.id.common_view_title_text);
        mTextTitle.setText(mTitle);

        mListView = (ListView) findViewById(R.id.activity_action_base_list);
        mListView.setAdapter(mAdapter);
        UtilsAndroid.UI.setListViewHeightBasedOnChildren(mListView);
        mAdapter.notifyDataSetChanged();

        mListView.setOnItemClickListener(this);
//        mListView.setOnItemClickListener(new OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//
//                ExecuteAction action = (ExecuteAction) parent.getAdapter()
//                        .getItem(position);
//                Log.i(TAG, action + "<" + position + "<" + id);
//                String viewAction = action.getAction();
//
//                switch (mCurrentLevel) {
//                    case LEVEL_FIRST:
//                        firstAction = viewAction;// 一级菜单 点击后的 action;
//                        mFirstBtnName = action.getBtnname();// 一级菜单 点击后的 name;
//                        getSecondData(viewAction);
//                        PreferencesUtil.putValue(PreferencesUtil.KEY_STEPS,
//                                action.getAction());
//                        if (mCurrentExecuteAction.size() == 0) {
//                            mCurrentExecuteAction = mFirstActionList;
//                            executeAction(view, mFirstActionList.get(0).getBtnname());
//                        } else {
//                            mCurrentLevel = LEVEL_SECOND;
//                            UtilsAndroid.UI
//                                    .setListViewHeightBasedOnChildren(mListView);
//                            mAdapter.notifyDataSetChanged();
//                            mTextTitle.setText(action.getBtnname());
//
//                        }
//                        break;
//                    case LEVEL_SECOND:
//                        saveOpSteps(action);
//                        // sccondAction = viewAction;// 二级菜单的 点击后 Action;
//                        // mSecondBtnName = action.getBtnname();
//                        getThridData(viewAction);
//                        if (mCurrentExecuteAction.size() == 0) {
//                            getSecondData(firstAction);
//                            executeAction(view, mSecondActionList.get(0).getBtnname());
//                        } else {
//                            // mCurrentExecuteAction = mThirdActionList;
//                            mCurrentLevel = LEVEL_THIRD;
//                            UtilsAndroid.UI
//                                    .setListViewHeightBasedOnChildren(mListView);
//                            mAdapter.notifyDataSetChanged();
//                            mTextTitle.setText(action.getBtnname());
//
//                        }
//
//                        break;
//                    case LEVEL_THIRD:
//                        // thirdAction = viewAction;//三级菜单的 点击后 Action;
//                        saveOpSteps(action);
//                        executeAction(view, mThirdActionList.get(0).getBtnname());
//                        break;
//                    default:
//                        break;
//                }
//
//            }
//        });
    }

    private boolean checkPermissions() {
        // 检查权限
        String execMenus = PreferencesUtil.getValue(
                PreferencesUtil.KEY_EXECUTE_MENU, null);

        if (TextUtils.isEmpty(execMenus)) {
            return false;
        }
        Log.d("checkPermissions", execMenus);
        String[] menus = execMenus.split(",");
        for (String menu : menus) {
            // if (Constants.MENU_MAP.get(menu) == null) {
            // continue;
            // }
            Log.d("checkPermissions", menu);
            if ("HYWY005".equals(menu)) {
                return true;
            }
        }
        return false;
    }
    private boolean isThridPermissons() {
        // 检查权限

        return true;
    }

    private void getActionData(int level, String action) {
        switch (level) {
            case LEVEL_SECOND:
                getSecondData(action);
                break;
            case LEVEL_THIRD:
                getThridData(action);
                break;
        }
    }

    private void getSecondData(String action) {
        mCurrentExecuteAction = new ArrayList<ExecuteAction>();
        for (ExecuteAction sub : mSecondActionList) {
            if (sub.getAction().startsWith(action)
                    && sub.getAction().length() == action.length() + 2) { // 从二级动作集合中取得当前动作的子动作
                mCurrentExecuteAction.add(sub);

            }
        }

    }

    private void getThridData(String action) {
        mCurrentExecuteAction = new ArrayList<ExecuteAction>();
        for (ExecuteAction sub : mThirdActionList) {
            if (sub.getAction().startsWith(action)
                    && sub.getAction().length() == action.length() + 2) { // 从三级动作集合中取得当前动作的子动作
                mCurrentExecuteAction.add(sub);

            }
        }
    }

    private BaseAdapter mAdapter = new BaseAdapter() {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(OrderActionActivity.this).inflate(
                    android.R.layout.simple_list_item_1, null);
            TextView textView = (TextView) view
                    .findViewById(android.R.id.text1);

            final ExecuteAction action = (ExecuteAction) getItem(position);
            textView.setText(action.getBtnname());
            textView.setTextColor(getResources().getColor(R.color.white));
            // textView.setTextSize(UtilsAndroid.UI.sp2px(OrderActionActivity.this,
            // 11));
            textView.setTextSize(16);
            // button.setBackgroundResource(R.drawable.back_submit_style);
            textView.setBackgroundResource(R.drawable.btn_bg_blue_selector);
            int pad = UtilsAndroid.UI.dip2px(OrderActionActivity.this, 15);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(pad, pad, pad, pad);
            textView.setTag(action);

            // Button button = new Button(OrderActionActivity.this);
            // final ExecuteAction action = (ExecuteAction) getItem(position);
            // button.setText(action.getBtnname());
            // button.setTextColor(getResources().getColor(R.color.white));
            // // button.setBackgroundResource(R.drawable.back_submit_style);
            // button.setBackgroundResource(R.drawable.btn_bg_blue_selector);
            // int pad = UtilsAndroid.UI.dip2px(OrderActionActivity.this, 15);
            // button.setPadding(pad, pad, pad, pad);
            // button.setTag(action);
            // button.setOnClickListener(new OnClickListener() {
            //
            // @Override
            // public void onClick(View v) {
            // String viewAction = action.getAction();
            //
            // switch (mCurrentLevel) {
            // case LEVEL_FIRST:
            // firstAction = viewAction;// 一级菜单 点击后的 action;
            // mFirstBtnName = action.getBtnname();// 一级菜单 点击后的 name;
            // getSecondData(viewAction);
            // PreferencesUtil.putValue(PreferencesUtil.KEY_STEPS,
            // action.getAction());
            // if (mCurrentExecuteAction.size() == 0) {
            // mCurrentExecuteAction = mFirstActionList;
            // executeAction(v);
            // } else {
            // mCurrentLevel = LEVEL_SECOND;
            // UtilsAndroid.UI
            // .setListViewHeightBasedOnChildren(mListView);
            // mAdapter.notifyDataSetChanged();
            // mTextTitle.setText(action.getBtnname());
            //
            // }
            // break;
            // case LEVEL_SECOND:
            // saveOpSteps(action);
            // // sccondAction = viewAction;// 二级菜单的 点击后 Action;
            // // mSecondBtnName = action.getBtnname();
            // getThridData(viewAction);
            // if (mCurrentExecuteAction.size() == 0) {
            // getSecondData(firstAction);
            // executeAction(v);
            // } else {
            // // mCurrentExecuteAction = mThirdActionList;
            // mCurrentLevel = LEVEL_THIRD;
            // UtilsAndroid.UI
            // .setListViewHeightBasedOnChildren(mListView);
            // mAdapter.notifyDataSetChanged();
            // mTextTitle.setText(action.getBtnname());
            //
            // }
            //
            // break;
            // case LEVEL_THIRD:
            // // thirdAction = viewAction;//三级菜单的 点击后 Action;
            // saveOpSteps(action);
            // executeAction(v);
            // break;
            // default:
            // break;
            // }
            //
            // }
            // });
            return textView;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            if (mCurrentExecuteAction == null) {
                return null;
            } else {
                try {
                    return mCurrentExecuteAction.get(position);
                } catch (Exception e) {
                    ErrLogUtils.uploadErrLog(OrderActionActivity.this,
                            ErrLogUtils.toString(e));
                }
                return null;
            }
        }

        @Override
        public int getCount() {
            if (mCurrentExecuteAction == null) {
                return 0;
            } else {
                return mCurrentExecuteAction.size();
            }
        }
    };

    @Override
    public void onBackPressed() {

        if (mCurrentLevel <= LEVEL_FIRST) {
            // 如果当前页显示的是一级按钮，直接返回
            super.onBackPressed();
        } else {
            // recoveryOpSteps();
            mCurrentLevel--;
            if (mCurrentLevel == LEVEL_SECOND) {
                getSecondData(firstAction);
                mTextTitle.setText(mFirstBtnName);
            } else if (mCurrentLevel == LEVEL_FIRST) {
                mTextTitle.setText(mTitle);
                mCurrentExecuteAction = mFirstActionList;
            }

            UtilsAndroid.UI.setListViewHeightBasedOnChildren(mListView);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 保存用户执行的动作码
     *
     * @param tempAction
     */
    private void saveOpSteps(ExecuteAction tempAction) {
        String steps = PreferencesUtil.getValue(PreferencesUtil.KEY_STEPS, "");
        steps += "," + tempAction.getAction();
        PreferencesUtil.putValue(PreferencesUtil.KEY_STEPS, steps);
    }

    // private void recoveryOpSteps() {
    // String steps = PreferencesUtil.getValue(PreferencesUtil.KEY_STEPS, "");
    // int index = steps.lastIndexOf(",");
    // if (!TextUtils.isEmpty(steps) && index > 0) {
    // steps = steps.substring(0, index);
    // } else if (index < 0) {
    // steps = "";
    // }
    // PreferencesUtil.putValue(PreferencesUtil.KEY_STEPS, steps);
    // }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // ActivityStackManager.getStackManager().popActivity(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_view_title_img:
                onBackPressed();
                break;
            case R.id.activity_action_base_report_add_btn:
                startAccidentScan();
                break;
            case R.id.activity_action_base_report_third_btn:
                startThirdScan();
                break;
            default:
                break;
        }
    }

    private void startAccidentScan() {
        ExecuteAction action = new ExecuteAction();
        action.setBtnname(getString(R.string.accident_report));
        saveOperateLog(mBtnReport.getText().toString());
        Intent intent = new Intent(OrderActionActivity.this,
                CaptureActivity.class);
        intent.putExtra(Constants.Feild.KEY_ACTION, action);
        intent.putExtra(FILED_TYPE_FROM, VALUE_FROM_ACCIDENT);
        startActivity(intent);
    }
    private void startThirdScan() {
//        ExecuteAction action = new ExecuteAction();
//        action.setBtnname(getString(R.string.accident_report));
        saveOperateLog(mBtnThrid.getText().toString());
        Intent intent = new Intent(OrderActionActivity.this,
                ThridScanActivity.class);
//        intent.putExtra(Constants.Feild.KEY_ACTION, action);
//        intent.putExtra(FILED_TYPE_FROM, VALUE_FROM_ACCIDENT);
        startActivity(intent);
    }

    /**
     * 执行扫描动作
     *
     * @param view
     */
    private void executeAction(View view, String firstBtnName) {
        ExecuteAction executeAction = (ExecuteAction) view.getTag();
        String content = PreferencesUtil.getValue(PreferencesUtil.KEY_STEPS);
        saveOperateLog(content);
        if (isAutoCode()) {
            String autoCode = PreferencesUtil.getValue(PreferencesUtil.KEY_AUTO_CODE) + "";
            executeAction.setActidx(autoCode);
            if (executeAction.getStartnode().equals(Constants.Value.YES)) {
                if (!TextUtils.isEmpty(autoCode)) {
                    showDialogAutoCode(executeAction);
                    return;
                }
            } else if (TextUtils.isEmpty(autoCode)) {
                showShortToast(getString(R.string.please_click_colon) + firstBtnName);
                return;
            }

            startOrderExecute(executeAction);
        } else {
            Intent intent = new Intent(this, CaptureActivity.class);
            intent.putExtra(Constants.Feild.KEY_ACTION, executeAction);
            intent.putExtra(FILED_TYPE_FROM, VALUE_FROM_ACTION);
            startActivity(intent);
        }
    }

    private void startOrderExecute(ExecuteAction executeAction) {
        Intent intent = new Intent(this, OrderExecuteActivity.class);
        intent.putExtra(Constants.Feild.KEY_ACTION, executeAction);
        intent.putExtra(OrderExecuteActivity.FROM_WHERE, this.getClass().getSimpleName());
        startActivity(intent);
    }

    private boolean isAutoCode() {
//		isAutoCode
        String isAutoCode = "";
        String options = PreferencesUtil.getValue(Constants.Feild.KEY_OPTIONS);
        if (!TextUtils.isEmpty(options)) {
            try {
                JSONObject jsonObject = new JSONObject(options);
                if (!jsonObject.isNull("isAutoCode"))
                    isAutoCode = jsonObject.getString("isAutoCode");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (TextUtils.equals(isAutoCode, Constants.Value.STATUS_OK)) {
            return true;
        } else {
            return false;
        }
    }

    private AlertDialog.Builder mAutoDialog;

    private void showDialogAutoCode(final ExecuteAction executeAction) {

        if (mAutoDialog == null) {
            mAutoDialog = new AlertDialog.Builder(this)
                    .setTitle(this.getString(R.string.system_tip))
                    .setMessage(this.getString(R.string.auto_code_tip_msg))
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    PreferenceManager
                                            .getDefaultSharedPreferences(OrderActionActivity.this).
                                            edit().remove(PreferencesUtil.KEY_AUTO_CODE);
                                    startOrderExecute(executeAction);
                                }
                            })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
        }
        mAutoDialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ExecuteAction action = (ExecuteAction) parent.getAdapter()
                .getItem(position);
        Log.i(TAG, action + "<" + position + "<" + id);
        String viewAction = action.getAction();

        switch (mCurrentLevel) {
            case LEVEL_FIRST:
                firstAction = viewAction;// 一级菜单 点击后的 action;
                mFirstBtnName = action.getBtnname();// 一级菜单 点击后的 name;
                getSecondData(viewAction);
                PreferencesUtil.putValue(PreferencesUtil.KEY_STEPS,
                        action.getAction());
                if (mCurrentExecuteAction.size() == 0) {
                    mCurrentExecuteAction = mFirstActionList;
                    executeAction(view, mFirstActionList.get(0).getBtnname());
                } else {
                    mCurrentLevel = LEVEL_SECOND;
                    UtilsAndroid.UI
                            .setListViewHeightBasedOnChildren(mListView);
                    mAdapter.notifyDataSetChanged();
                    mTextTitle.setText(action.getBtnname());

                }
                break;
            case LEVEL_SECOND:
                saveOpSteps(action);
                // sccondAction = viewAction;// 二级菜单的 点击后 Action;
                // mSecondBtnName = action.getBtnname();
                getThridData(viewAction);
                if (mCurrentExecuteAction.size() == 0) {
                    getSecondData(firstAction);
                    executeAction(view, mSecondActionList.get(0).getBtnname());
                } else {
                    // mCurrentExecuteAction = mThirdActionList;
                    mCurrentLevel = LEVEL_THIRD;
                    UtilsAndroid.UI
                            .setListViewHeightBasedOnChildren(mListView);
                    mAdapter.notifyDataSetChanged();
                    mTextTitle.setText(action.getBtnname());

                }

                break;
            case LEVEL_THIRD:
                // thirdAction = viewAction;//三级菜单的 点击后 Action;
                saveOpSteps(action);
                executeAction(view, mThirdActionList.get(0).getBtnname());
                break;
            default:
                break;
        }
    }
}
