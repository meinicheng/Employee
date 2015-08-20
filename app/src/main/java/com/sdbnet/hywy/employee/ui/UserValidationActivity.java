//package com.sdbnet.hywy.employee.ui;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import org.apache.http.Header;
//import org.apache.http.message.BasicNameValuePair;
//import org.json.JSONObject;
//
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//
//import com.loopj.android.http.JsonHttpResponseHandler;
//import com.sdbnet.hywy.employee.R;
//import com.sdbnet.hywy.employee.net.AsyncHttpService;
//import com.sdbnet.hywy.employee.ui.base.BaseActivity;
//import com.sdbnet.hywy.employee.utils.Constants;
//import com.sdbnet.hywy.employee.utils.UtilsAndroid;
//import com.sdbnet.hywy.employee.utils.UtilsCommon;
//import com.sdbnet.hywy.employee.utils.UtilsJava;
//
//public class UserValidationActivity extends BaseActivity {
//
//	private ImageView mBack;
//	private EditText telEdt;
//	private EditText codeEdt;
//	SharedPreferences share;
//
//	private Button btnValidation;
//
//	private int time = 120;
//	private Timer timer = new Timer();
//	TimerTask task;
//
//	public static List<Map<String, Object>> list = null;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		// ActivityStackManager.getStackManager().pushActivity(this);
//		setContentView(R.layout.activity_regist);
//		initControl();
//	}
//
//	private void initControl() {
//		mBack = (ImageView) findViewById(R.id.activity_regist_img_back);
//		mBack.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});
//
//		telEdt = (EditText) findViewById(R.id.activity_regist_tel_edt);
//		codeEdt = (EditText) findViewById(R.id.activity_regist_code_edt);
//
//		btnValidation = (Button) findViewById(R.id.activity_regist_get_code_btn);
//	}
//
//	public void onRegist(View view) {
//		String code = codeEdt.getText().toString().trim();
//		String nTel = telEdt.getText().toString().trim();
//		if (TextUtils.isEmpty(nTel)) {
//			showLongToast(getString(R.string.please_fill_you_phone_num));
//			return;
//		} else if (!UtilsJava.isMobile(nTel)) {
//			showLongToast(getString(R.string.phone_number_illegal));
//		} else if (TextUtils.isEmpty(code)) {
//			showLongToast(getResources().getString(R.string.user_verifycode));
//			return;
//		} else if (!UtilsAndroid.Set.checkNetState(this)) {
//			showLongToast(getResources().getString(R.string.httpisNull));
//			return;
//		}
//		doRegist(nTel, code);
//	}
//
//	public void getCode(View view) {
//		final String nTel = telEdt.getText().toString().trim();
//		if (TextUtils.isEmpty(nTel)) {
//			showLongToast(getString(R.string.please_fill_you_phone_num));
//			return;
//		} else if (!UtilsJava.isMobile(nTel)) {
//			showLongToast(getString(R.string.phone_number_illegal));
//			return;
//		} else if (!UtilsAndroid.Set.checkNetState(this)) {
//			showShortToast(getResources().getString(R.string.httpisNull));
//			return;
//		} else {
//			getValidationCode(nTel);
//		}
//	}
//
//	private void getValidationCode(final String nTel) {
//		AsyncHttpService.getValidationCode(nTel, 1,
//				new JsonHttpResponseHandler() {
//					private int errCode;
//					private String msg;
//
//					@Override
//					public void onStart() {
//
//						super.onStart();
//						showLoadDialog(R.string.sending_verification_code_ellipsis);
//					}
//
//					@Override
//					public void onFailure(int statusCode, Header[] headers,
//							Throwable throwable, JSONObject errorResponse) {
//						super.onFailure(statusCode, headers, throwable,
//								errorResponse);
//						dismissLoadDialog();
//						showShortToast(getResources().getString(
//								R.string.httpError));
//
//					}
//
//					@Override
//					public void onSuccess(int statusCode, Header[] headers,
//							JSONObject response) {
//						super.onSuccess(statusCode, headers, response);
//						try {
//							errCode = response.getInt("errcode");
//							if (errCode != 0) {
//								String msg = response
//										.getString(Constants.Feild.KEY_MSG);
//								switch (response
//										.getInt(Constants.Feild.KEY_ERROR_CODE)) {
//								case 41:
//									returnLogin(UserValidationActivity.this,
//											msg, null);
//									dismissLoadDialog();
//									break;
//								case 42:
//									returnLogin(UserValidationActivity.this,
//											msg, null);
//									dismissLoadDialog();
//									break;
//								default:
//									showLongToast(msg);
//									dismissLoadDialog();
//									break;
//								}
//								return;
//							}
//							msg = getString(R.string.verification_code_sent_success_please_check_sms);
//							initTimer();
//						} catch (Exception e) {
//							e.printStackTrace();
//							msg = getString(R.string.verification_code_sent_failed);
//						} finally {
//							dismissLoadDialog();
//							showShortToast(msg);
//
//						}
//					}
//				}, this);
//	}
//
//	private void initTimer() {
//		btnValidation.setEnabled(false);
//		btnValidation.setBackgroundResource(R.drawable.btn_validation_disable);
//		task = new TimerTask() {
//			@Override
//			public void run() {
//				runOnUiThread(new Runnable() { // UI thread
//					@Override
//					public void run() {
//						if (time <= 0) {
//							btnValidation.setEnabled(true);
//							btnValidation
//									.setBackgroundResource(R.drawable.validation_btn_selector);
//							btnValidation
//									.setText(getString(R.string.get_verification_code));
//							task.cancel();
//						} else {
//							btnValidation.setText("" + time);
//						}
//						time--;
//					}
//				});
//			}
//		};
//		time = 120;
//		timer.schedule(task, 0, 1000);
//	}
//
//	private void doRegist(final String tel, String code) {
//		AsyncHttpService.validation(tel, code, 1,
//				new JsonHttpResponseHandler() {
//					private int errCode;
//					private String msg;
//
//					@Override
//					public void onStart() {
//						super.onStart();
//						showLoadDialog(R.string.validating_user_elipsis);
//
//					}
//
//					@Override
//					public void onFailure(int statusCode, Header[] headers,
//							Throwable throwable, JSONObject errorResponse) {
//						super.onFailure(statusCode, headers, throwable,
//								errorResponse);
//						dismissLoadDialog();
//						showLongToast(getResources().getString(
//								R.string.httpError));
//
//					}
//
//					@Override
//					public void onSuccess(int statusCode, Header[] headers,
//							JSONObject response) {
//						super.onSuccess(statusCode, headers, response);
//						try {
//							errCode = response.getInt("errcode");
//							if (errCode != 0) {
//								String msg = response
//										.getString(Constants.Feild.KEY_MSG);
//								switch (response
//										.getInt(Constants.Feild.KEY_ERROR_CODE)) {
//								case 41:
//									dismissLoadDialog();
//									returnLogin(UserValidationActivity.this,
//											msg, null);
//									break;
//								case 42:
//									dismissLoadDialog();
//									returnLogin(UserValidationActivity.this,
//											msg, null);
//									break;
//								default:
//									dismissLoadDialog();
//
//									break;
//								}
//								return;
//							}
//							BasicNameValuePair bav = new BasicNameValuePair(
//									"tel", tel);
////							UtilsCommon.start_activity(
////									UserValidationActivity.this,
////									UserResetPwdActivity.class, bav);
//						} catch (Exception ex) {
//							ex.printStackTrace();
//							msg = getString(R.string.verification_not_successful);
//						} finally {
//							dismissLoadDialog();
//							if (!TextUtils.isEmpty(msg))
//								showShortToast(msg);
//						}
//					}
//				}, this);
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		// ActivityStackManager.getStackManager().popActivity(this);
//	}
//}
