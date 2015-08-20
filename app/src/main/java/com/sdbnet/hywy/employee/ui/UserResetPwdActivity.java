//package com.sdbnet.hywy.employee.ui;
//
//import java.util.TimerTask;
//
//import org.apache.http.Header;
//import org.json.JSONObject;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.loopj.android.http.JsonHttpResponseHandler;
//import com.sdbnet.hywy.employee.R;
//import com.sdbnet.hywy.employee.net.AsyncHttpService;
//import com.sdbnet.hywy.employee.service.TimingUpLocateService;
//import com.sdbnet.hywy.employee.ui.base.BaseActivity;
//import com.sdbnet.hywy.employee.utils.Constants;
//import com.sdbnet.hywy.employee.utils.PreferencesUtil;
//import com.sdbnet.hywy.employee.utils.UtilsAndroid;
//
//public class UserResetPwdActivity extends BaseActivity {
//
//	private ImageView mBack;
//	public static String SharedName = "login";
//	public static String UID = "uid";// 用户名
//	public static String PWD = "pwd";// 密码
//	public static String KEY = "key";// key
//
//	private EditText pwdEdt;
//	private EditText repectPwdEdt;
//	SharedPreferences share;
//
//	private String tel;
//	private String token;
//	TimerTask task;
//
//	public static boolean isRegist = true;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		// ActivityStackManager.getStackManager().pushActivity(this);
//		setContentView(R.layout.activity_user_login_pwd_reset);
//		tel = getIntent().getStringExtra("tel");
//		initControl();
//	}
//
//	private void initControl() {
//		mBack = (ImageView) findViewById(R.id.activity_pwd_reset_img_back);
//		mBack.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});
//
//		pwdEdt = (EditText) findViewById(R.id.activity_pwd_reset_pwdEdt);
//		repectPwdEdt = (EditText) findViewById(R.id.activity_pwd_reset_repectPwdEdt);
//
//		if (isRegist)
//			((TextView) findViewById(R.id.textview_details_title))
//					.setText("设置密码");
//		else
//			((TextView) findViewById(R.id.textview_details_title))
//					.setText("重置密码");
//	}
//
//	public void onLogin(View view) {
//		String pwd = pwdEdt.getText().toString().trim();
//		String repectPwd = repectPwdEdt.getText().toString().trim();
//		if (TextUtils.isEmpty(pwd)) {
//			showShortToast("请设置您的登录密码！");
//			return;
//		} else if (TextUtils.isEmpty(repectPwd)) {
//			showShortToast("请再次输入您的登录密码！");
//			return;
//		} else if (!pwd.equals(repectPwd)) {
//			showShortToast("两次输入密码不一致，请重新输入");
//			return;
//		} else if (!UtilsAndroid.Set.checkNetState(this)) {
//			showShortToast(getResources().getString(R.string.httpisNull));
//			return;
//		}
//
//		if (isRegist) {
//			doSetPwd(tel, pwd);
//		} else {
//			doResetPwd(tel, pwd);
//		}
//	}
//
//	private void doSetPwd(final String tel, final String pwd) {
//		AsyncHttpService.registUser(tel, pwd, 1, new JsonHttpResponseHandler() {
//			private int errCode;
//			private String msg;
//
//			@Override
//			public void onStart() {
//				super.onStart();
//				showLoadDialog(R.string.is_validation);
//			}
//
//			@Override
//			public void onFailure(int statusCode, Header[] headers,
//					Throwable throwable, JSONObject errorResponse) {
//				super.onFailure(statusCode, headers, throwable, errorResponse);
//				dismissLoadDialog();
//				showLongToast(getResources().getString(R.string.httpError));
//
//			}
//
//			@Override
//			public void onSuccess(int statusCode, Header[] headers,
//					JSONObject response) {
//				super.onSuccess(statusCode, headers, response);
//				try {
//					errCode = response.getInt("errcode");
//					if (errCode != 0) {
//						msg = response.getString(Constants.Feild.KEY_MSG);
//						switch (response.getInt(Constants.Feild.KEY_ERROR_CODE)) {
//						case 41:
//							returnLogin(UserResetPwdActivity.this, msg, null);
//							break;
//						case 42:
//							returnLogin(UserResetPwdActivity.this, msg, null);
//							break;
//						default:
//							showLongToast(msg);
//							dismissLoadDialog();
//							break;
//						}
//						return;
//					}
//					doLogin(tel, pwd);
//
//				} catch (Exception ex) {
//					ex.printStackTrace();
//
//				} finally {
//					dismissLoadDialog();
//				}
//			}
//		}, this);
//	}
//
//	private void doResetPwd(final String tel, final String pwd) {
//		AsyncHttpService.resetPwd(tel, pwd, new JsonHttpResponseHandler() {
//			private int errCode;
//			private String msg;
//
//			@Override
//			public void onStart() {
//				super.onStart();
//				showLoadDialog(R.string.is_validation);
//			}
//
//			@Override
//			public void onFailure(int statusCode, Header[] headers,
//					Throwable throwable, JSONObject errorResponse) {
//				super.onFailure(statusCode, headers, throwable, errorResponse);
//				dismissLoadDialog();
//				showLongToast(getResources().getString(R.string.httpError));
//
//			}
//
//			@Override
//			public void onSuccess(int statusCode, Header[] headers,
//					JSONObject response) {
//				super.onSuccess(statusCode, headers, response);
//				try {
//					errCode = response.getInt("errcode");
//					if (errCode != 0) {
//						msg = response.getString(Constants.Feild.KEY_MSG);
//						switch (response.getInt(Constants.Feild.KEY_ERROR_CODE)) {
//						case 41:
//							returnLogin(UserResetPwdActivity.this, msg, null);
//							dismissLoadDialog();
//							break;
//						case 42:
//							returnLogin(UserResetPwdActivity.this, msg, null);
//							dismissLoadDialog();
//							break;
//						default:
//							showLongToast(msg);
//
//							break;
//						}
//						return;
//					}
//					doLogin(tel, pwd);
//
//				} catch (Exception ex) {
//					ex.printStackTrace();
//
//				} finally {
//					dismissLoadDialog();
//				}
//			}
//		}, this);
//	}
//
//	private void doLogin(final String tel, final String pwd) {
//		// AsyncHttpService.login(tel, pwd, new JsonHttpResponseHandler() {
//		// private int errCode;
//		// private String msg;
//		//
//		// @Override
//		// public void onStart() {
//		// super.onStart();
//		// dia = createLoadingDialog(ResetPasswordActivity.this, "正在登录...");
//		// dia.show();
//		// }
//		//
//		// @Override
//		// public void onFailure(int statusCode, Header[] headers, Throwable
//		// throwable, JSONObject errorResponse) {
//		// super.onFailure(statusCode, headers, throwable, errorResponse);
//		// showLongToast(getResources().getString(R.string.httpError));
//		// dia.dismiss();
//		// }
//		//
//		// @Override
//		// public void onSuccess(int statusCode, Header[] headers, JSONObject
//		// response) {
//		// super.onSuccess(statusCode, headers, response);
//		// try {
//		// errCode = response.getInt("errcode");
//		// if (errCode != 0) {
//		// msg = response.getString("msg");
//		// showShortToast(msg);
//		// dia.dismiss();
//		// return;
//		// }
//		// JSONObject jsonObj = response.getJSONObject("driver");
//		// PreferencesUtil.setPrefString(PreferencesUtil.KEY_USER_TEL, tel);
//		// PreferencesUtil.setPrefString(PreferencesUtil.KEY_USER_PWD, pwd);
//		// PreferencesUtil.setPrefString(PreferencesUtil.KEY_USER_TOKEN,
//		// jsonObj.getString("token"));
//		// PreferencesUtil.setPrefString(PreferencesUtil.KEY_USER_ID,
//		// jsonObj.getString("hdiId"));
//		// PreferencesUtil.setPrefString(PreferencesUtil.KEY_USER_NAME,
//		// jsonObj.getString("hdiName"));
//		// inToHome(tel);
//		// } catch (Exception ex) {
//		// ex.printStackTrace();
//		// dia.dismiss();
//		// }
//		// }
//		// }, this);
//	}
//
//	private void inToHome(String tel) {
//		String version = "";
//		try {
//			version = UtilsAndroid.Set.getVersionName(this);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		PreferencesUtil.putValue(PreferencesUtil.KEY_SYSTEM_VERSION, version);
//		PreferencesUtil.initStoreData();
//		initWithApiKey();
//		// // 后台定位进程
//		startService(new Intent(UserResetPwdActivity.this,
//				TimingUpLocateService.class));
//		openActivity(MainActivity.class);
//		finish();
//	}
//
//	private void initWithApiKey() {
//		// Push: 无账号初始化，用api key绑定
//		// PushManager.startWork(getApplicationContext(),
//		// PushConstants.LOGIN_TYPE_API_KEY,
//		// PushUtils.getMetaValue(ResetPasswordActivity.this, "api_key"));
//	}
//
//	@Override
//	public void onDestroy() {
//		// ActivityStackManager.getStackManager().popActivity(this);
//		super.onDestroy();
//
//	}
//}
