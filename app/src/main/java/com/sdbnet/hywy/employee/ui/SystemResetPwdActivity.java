package com.sdbnet.hywy.employee.ui;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.net.AsyncHttpService;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.UtilsError;

/**
 * 修改当前用户帐号密码
 * 
 * @author Arron.Zhang
 *
 */
public class SystemResetPwdActivity extends BaseActivity {

	private EditText mEditOldPwd;
	private EditText mEditNewPwd;
	private EditText mEditRepeadPwd;
	private Button bt_info_save;
	// private Dialog dia;
	private View mImgBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActivityStackManager.getStackManager().pushActivity(this);
		setContentView(R.layout.activity_system_reset_pwd);
		initControls();
	}

	/**
	 * 初始化控件
	 */
	private void initControls() {
		mImgBack = findViewById(R.id.common_view_title_img);
		mImgBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		((TextView) findViewById(R.id.common_view_title_text))
				.setText(R.string.new_pwd);

		mEditOldPwd = (EditText) findViewById(R.id.activity_view_reset_pwd_edt_old_pwd);
		mEditNewPwd = (EditText) findViewById(R.id.activity_view_reset_pwd_edt_new_pwd);
		mEditRepeadPwd = (EditText) findViewById(R.id.activity_view_reset_pwd_edt_repeat_pwd);
		bt_info_save = (Button) findViewById(R.id.activity_view_reset_pwd_bt_info_save);
		bt_info_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				saveInfo();
			}

		});
	}

	private void saveInfo() {
		String oldPwd = mEditOldPwd.getText().toString().trim();
		final String newPwd = mEditNewPwd.getText().toString().trim();
		String repeatPwd = mEditRepeadPwd.getText().toString().trim();

		if (TextUtils.isEmpty(oldPwd)) {
			showLongToast("请输入您的当前密码");
			return;
		}
		if (TextUtils.isEmpty(newPwd)) {
			showLongToast("请输入您的新密码");
			return;
		}
		if (TextUtils.isEmpty(repeatPwd)) {
			showLongToast("请重复输入您的新密码");
			return;
		}
		if (oldPwd.length() < 6 || newPwd.length() < 6
				|| repeatPwd.length() < 6) {
			showLongToast("请输入密码长度大于6位的密码");
			mEditOldPwd.setText("");
			return;
		}
		if (oldPwd.length() > 13 || newPwd.length() > 13
				|| repeatPwd.length() > 13) {
			showLongToast("请输入密码长度小于13位的密码");
			mEditOldPwd.setText("");
			return;
		}
		if (!PreferencesUtil.user_pwd.equals(oldPwd)) {
			showLongToast("当前密码输入错误");
			mEditOldPwd.setText("");
			return;
		}
		if (!newPwd.equals(repeatPwd)) {
			showLongToast("两次输入的新密码不一致,请重新输入");
			mEditNewPwd.setText("");
			mEditRepeadPwd.setText("");
			return;
		}

		// dia = createLoadingDialog(this, );
		// dia.show();
		AsyncHttpService.modifyUserPwd(newPwd, new JsonHttpResponseHandler() {

			private int errCode;
			private String msg;

			@Override
			public void onStart() {
				super.onStart();
				showLoadDialog("正在保存，请稍候...");
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				dismissLoadDialog();
				showShortToast(getResources().getString(R.string.httpisNull));
				// dia.dismiss();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				System.out.println("info: " + response.toString());
				super.onSuccess(statusCode, headers, response);
				try {
					errCode = response.getInt(Constants.Feild.KEY_ERROR_CODE);
					if (UtilsError.isErrorCode(SystemResetPwdActivity.this,
							response)) {
						return;
					}
					PreferencesUtil.putValue(PreferencesUtil.KEY_USER_PWD,
							newPwd);
					PreferencesUtil.user_pwd = newPwd;
					showLongToast("密码修改成功");
					SystemResetPwdActivity.this.finish();
					// dia.dismiss();
				} catch (JSONException e) {
					// dia.dismiss();
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					dismissLoadDialog();
				}
			}

		}, this);
	}

	@Override
	public void onDestroy() {
		// ActivityStackManager.getStackManager().popActivity(this);
		super.onDestroy();
	}

}
