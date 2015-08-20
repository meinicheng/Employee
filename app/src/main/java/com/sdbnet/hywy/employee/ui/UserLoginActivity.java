package com.sdbnet.hywy.employee.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.employee.ActivityStackManager;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.db.db.DBManager;
import com.sdbnet.hywy.employee.net.AsyncHttpService;
import com.sdbnet.hywy.employee.ui.widget.DialogLoading;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.ErrLogUtils;
import com.sdbnet.hywy.employee.utils.LogUtil;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.UtilsAndroid;
import com.sdbnet.hywy.employee.utils.UtilsCommon;
import com.sdbnet.hywy.employee.utils.UtilsError;
import com.sdbnet.hywy.employee.utils.UtilsJava;

public class UserLoginActivity extends Activity implements OnClickListener, OnCheckedChangeListener {
    private final static String TAG = "UserLoginActivity";

    private EditText telEdt;
    private EditText pwdEdt;
    private Button btnLogin;

    private CheckBox pactCheck;
    private TextView tvPact;
    // private TimerTask task;

    public static List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

    private TextView tvReg;
    private TextView tvSeek;

    private String mUserTel;
    private String mUserPwd;

    public UserLoginActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStackManager.getStackManager().pushActivity(this);
        setContentView(R.layout.activity_login);
        initUI();
    }

    /**
     * 初始化控件
     */
    private void initUI() {
        //init ui;
        telEdt = (EditText) findViewById(R.id.activity_login_tel_edt);
        pwdEdt = (EditText) findViewById(R.id.activity_login_pwd_edt);
        tvReg = (TextView) findViewById(R.id.activity_login_tvReg);
        tvSeek = (TextView) findViewById(R.id.activity_login_tvSeek);
        btnLogin = (Button) findViewById(R.id.activity_login_bt);
        pactCheck = (CheckBox) findViewById(R.id.activity_login_pactCheck);
        tvPact = (TextView) findViewById(R.id.activity_login_tvPact);
        //init ui data;
        tvPact.setText(Html
                .fromHtml(getString(R.string.have_read_and_agreed_to_the_deal)));
        tvPact.setMovementMethod(LinkMovementMethod.getInstance());
        if (!TextUtils.isEmpty(PreferencesUtil.user_tel)) {
            telEdt.setText(PreferencesUtil.user_tel);
        }
        // 导入城市编码数据库
        importCityInitDatabase();
        // init ui Listener()
        btnLogin.setOnClickListener(this);
        tvReg.setOnClickListener(this);
        tvSeek.setOnClickListener(this);
        pactCheck.setOnCheckedChangeListener(this);

    }


    /**
     * 将res/raw中的城市数据库导入到安装的程序中的database目录下
     */
    private void importCityInitDatabase() {
        boolean isFirstRun = PreferencesUtil.getValue(
                PreferencesUtil.KEY_FIRST_RUN, true);
        if (!isFirstRun) {
            return;
        }

        // 数据库的目录
        String dirPath = String.format("/data/data/%s/databases/",
                getPackageName());
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        // 数据库文件
        File dbfile = new File(dir, "db_weather.db");
        try {
            if (!dbfile.exists()) {
                dbfile.createNewFile();
            }
            // 加载欲导入的数据库
            InputStream is = this.getApplicationContext().getResources()
                    .openRawResource(R.raw.db_weather);
            FileOutputStream fos = new FileOutputStream(dbfile);
            byte[] buffere = new byte[is.available()];
            is.read(buffere);
            fos.write(buffere);
            is.close();
            fos.close();

            PreferencesUtil.putValue(PreferencesUtil.KEY_FIRST_RUN, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录按钮响应事件
     *
     * @param view
     */
    public void onLogin(View view) {
        saveOperateLog(((Button) view).getText().toString(), null);
        String pwd = pwdEdt.getText().toString().trim();
        String tel = telEdt.getText().toString().trim();
        if (TextUtils.isEmpty(tel)) {
            showLongToast(getString(R.string.please_input_phone_num));
        } else if (!UtilsJava.isMobile(tel)) {
            showLongToast(getString(R.string.phone_number_illegal));
        } else if (TextUtils.isEmpty(pwd)) {
            showLongToast(getString(R.string.please_enter_pwd));
        } else if (pwd.length() < Constants.Login.PWD_LENGTH_MIN
                || pwd.length() > Constants.Login.PWD_LENGTH_MAX) {
            showLongToast(getString(R.string.please_enter_4_15_valid_pwd));
        } else if (!UtilsAndroid.Set.checkNetState(this)) {
            showLongToast(getString(R.string.httpisNull));
        } else {
            LogUtil.d(TAG, String.format("tel: %s; pwd: %s", tel, pwd));
            UtilsAndroid.Set.hideSoftInput(this);
            doLogin(tel, pwd);
        }
    }

    /**
     * 登录验证
     *
     * @param tel
     * @param pwd //	 * @param loginType
     */
    private void doLogin(final String tel, final String pwd) {
        AsyncHttpService.login(tel, pwd, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showLoadDialog(R.string.is_landing);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                dismissLoadDialog();
                showShortToast(getString(R.string.httpisNull));

            }

            @Override
            public void onCancel() {
                super.onCancel();
                dismissLoadDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  JSONObject response) {
                LogUtil.d(TAG, "login onSuccess: " + response.toString());
                super.onSuccess(statusCode, headers, response);

                try {
                    if (!UtilsError.isErrorCode(UserLoginActivity.this,
                            response)) {
                        analysisLoginJson(response, tel, pwd);
                    } else
                        dismissLoadDialog();
                } catch (Exception e) {
                    dismissLoadDialog();
                    showShortToast(getString(R.string.login_failed_please_retry_after_confirm_account_pwd));
                    ErrLogUtils.uploadErrLog(UserLoginActivity.this, e);
                }
            }
        }, this);
    }

    private void analysisLoginJson(JSONObject response, String userTel,
                                   String userPwd) throws JSONException {

        // 保存到缓存文件中
        PreferencesUtil.putValue(PreferencesUtil.KEY_USER_TEL, userTel);
        PreferencesUtil.putValue(PreferencesUtil.KEY_USER_PWD, userPwd);
        PreferencesUtil.putValue(PreferencesUtil.KEY_USER_TOKEN,
                response.getString(Constants.Feild.KEY_TOKEN));
        // 获取用户所属的项目
        JSONArray arrs = response.getJSONArray(Constants.Feild.KEY_ITEMS);
        PreferencesUtil.putValue(PreferencesUtil.KEY_ITEMS, arrs.toString());

        if (arrs.length() == 1) {
            JSONObject jsonObj = arrs.getJSONObject(0);
            String cmpid = jsonObj.getString(Constants.Feild.KEY_COMPANY_ID);
            String cmpname = jsonObj
                    .getString(Constants.Feild.KEY_COMPANY_NAME);
            String itemid = jsonObj.getString(Constants.Feild.KEY_ITEM_ID);
            String itemname = jsonObj.getString(Constants.Feild.KEY_ITEM_NAME);
            String pid = jsonObj.getString(Constants.Feild.KEY_STAFF_ID);
            String pname = jsonObj.getString(Constants.Feild.KEY_STAFF_NAME);

            // 将公司id、项目id、用户id保存到缓存文件中
            PreferencesUtil.putValue(PreferencesUtil.KEY_COMPANY_ID, cmpid);
            PreferencesUtil.putValue(PreferencesUtil.KEY_COMPANY_NAME, cmpname);
            PreferencesUtil.putValue(PreferencesUtil.KEY_ITEM_ID, itemid);
            PreferencesUtil.putValue(PreferencesUtil.KEY_ITEM_NAME, itemname);
            PreferencesUtil.putValue(PreferencesUtil.KEY_USER_NAME, pname);
            PreferencesUtil.putValue(PreferencesUtil.KEY_USER_ID, pid);

            if (Constants.Value.LOGIN_EMPLOYEE.equals("1")) {
                // 企业员工获取权限
                getPermission(cmpid, itemid, pid);
            } else {
                // 第三方直接进入
                enterMain();
            }
        } else if (arrs.length() != 0) {
            // 跳转到项目列表，选择项目
            dismissLoadDialog();
            pwdEdt.setText("");
            Intent intent = new Intent(UserLoginActivity.this,
                    SelectProjectActivity.class);
            startActivity(intent);

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
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                        dismissLoadDialog();
                        showShortToast(getString(R.string.httpError));
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        LogUtil.d(TAG, "getPermission:　" + response.toString());
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if (UtilsError.isErrorCode(UserLoginActivity.this,
                                    response)) {
                                dismissLoadDialog();
                            } else {
                                analyticalJsonPermission(response);
                                enterMain();
                            }
                        } catch (Exception e) {
                            showShortToast(getString(R.string.network_busy_please_try_again_later));
                            e.printStackTrace();
                            dismissLoadDialog();
                        }

                    }
                }, UserLoginActivity.this);
    }

    private void analyticalJsonPermission(JSONObject response)
            throws JSONException {
        // 获取用户相关权限信息
        // LogUtil.d(TAG, response + "");

        JSONObject jsonObj = response.getJSONObject(Constants.Feild.KEY_STAFF);
        String menu = jsonObj.getString(Constants.Feild.KEY_MENU);
        PreferencesUtil.putValue(PreferencesUtil.KEY_EXECUTE_MENU, menu);

        int codeLength = jsonObj.getInt(Constants.Feild.KEY_CODE_LENGTH);
        PreferencesUtil.putValue(PreferencesUtil.KEY_CODE_LENGTH, codeLength);

        String logo = jsonObj.getString(Constants.Feild.KEY_COMPANY_LOGO);
        PreferencesUtil.putValue(PreferencesUtil.KEY_COMPANY_LOGO, logo);

        // scan
        String executeActions = jsonObj
                .getString(Constants.Feild.KEY_EXECUTE_ACTION);
        PreferencesUtil.putValue(PreferencesUtil.KEY_EXECUTE_ACTION,
                executeActions);

        PreferencesUtil.putValue(PreferencesUtil.KEY_USER_IS_DRIVER,
                jsonObj.getString(Constants.Feild.KEY_STAFF_IS_DRIVER));
        PreferencesUtil.putValue(PreferencesUtil.KEY_ORDTITLE,
                jsonObj.getString(Constants.Feild.KEY_COMPANY_ORDTITLE));
        PreferencesUtil.putValue(PreferencesUtil.KEY_CNOTITLE,
                jsonObj.getString(Constants.Feild.KEY_COMPANY_CNOTITLE));

        int workStatus = jsonObj.getInt(Constants.Feild.KEY_IS_WORKING);
        PreferencesUtil.putValue(PreferencesUtil.KEY_IS_WORKING, workStatus);
        boolean isWorking = (workStatus == 1);
        PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS, isWorking);
        // options 1 数组 操作权限
        // ---data 1 字符串 是否第三方交互 目前用不上
        // ---scanModel 1 字符串 扫描模式 0：普通模式 1：更新模式
        // ---workModel 1 字符串 是否需要上下班功能 0：不需要 1：需要

        if (!jsonObj.isNull(Constants.Feild.KEY_OPTIONS)) {
            String options = jsonObj.getString(Constants.Feild.KEY_OPTIONS);
            PreferencesUtil.putValue(Constants.Feild.KEY_OPTIONS, options);
        }

    }

    /**
     * 进入主页
     */
    private void enterMain() {

        PreferencesUtil.initStoreData();
        uploadDeviceInfo();
        dismissLoadDialog();
        startActivity(new Intent(this, MainActivity.class));
        // finish();
        // openActivity(MainActivity.class);
        ActivityStackManager.getStackManager().popAllActivitys();
        // Intent intent = new Intent(this, MainActivity.class);
        // startActivity(intent);
        // defaultFinish();

    }

    /**
     * 上传设备号
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
                        showShortToast(getString(R.string.httpisNull));
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        LogUtil.d(TAG, "onsuccess: " + response.toString());
                        super.onSuccess(statusCode, headers, response);

                        try {
                            UtilsError.isErrorCode(UserLoginActivity.this,
                                    response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }, this);
    }

    // no way back
    // @Override
    // public boolean onKeyDown(int keyCode, KeyEvent event) {
    // if (keyCode == KeyEvent.KEYCODE_BACK) {
    // PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS,
    // Constants.Value.WORKED);
    // PreferencesUtil.clearLocalData(this);
    // finish();
    // return true;
    // }
    // return super.onKeyDown(keyCode, event);
    // }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_login_tvPact:

                break;
            case R.id.activity_login_bt:
                onLogin(v);
                break;
            case R.id.activity_login_tvReg:
                // UserResetPwdActivity.isRegist = true;
                // UtilsCommon.start_activity(UserLoginActivity.this,
                // UserValidationActivity.class);
                break;
            case R.id.activity_login_tvSeek:
                // UserResetPwdActivity.isRegist = false;
                // UtilsCommon.start_activity(UserLoginActivity.this,
                // UserValidationActivity.class);
                break;

            default:
                break;
        }
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            btnLogin.setEnabled(true);
            btnLogin.setBackgroundResource(R.drawable.blue_btn_selector);
        } else {
            btnLogin.setEnabled(false);
            btnLogin.setBackgroundResource(R.drawable.btn_validation_disable);
        }
    }
}
