package com.sdbnet.hywy.employee.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;

public class DialogTipActivity extends BaseActivity implements OnClickListener {

	public static final String EXTRA_TITLE = "extra_title";
	public static final String EXTRA_MSG = "extra_msg";
	private TextView mTextTitel;
	private TextView mTextMsg;
	private TextView mTextOk;
	private TextView mTextCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActivityStackManager.getStackManager().pushActivity(this);
		setContentView(R.layout.activity_dialog_tip);
		initUI();
		initData();
	}

	private void initUI() {
		mTextTitel = (TextView) this
				.findViewById(R.id.activity_dialog_tip_title_text);
		mTextMsg = (TextView) this
				.findViewById(R.id.activity_dialog_tip_msg_text);
		mTextOk = (TextView) this
				.findViewById(R.id.activity_dialog_tip_ok_text);
		mTextCancel = (TextView) this
				.findViewById(R.id.activity_dialog_tip_cancel_text);

		// 添加按钮监听
		mTextOk.setOnClickListener(this);
		mTextCancel.setOnClickListener(this);
	}

	private void initData() {
		String title = getIntent().getStringExtra(EXTRA_TITLE);
		if (!TextUtils.isEmpty(title)) {
			mTextTitel.setText(title);
		}
		String msg = getIntent().getStringExtra(EXTRA_MSG);
		if (!TextUtils.isEmpty(msg)) {
			mTextMsg.setText(msg);
		}
	}

	// 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_dialog_tip_ok_text: // 确认
			exit();
			break;
		case R.id.activity_dialog_tip_cancel_text: // 撤销
			finish();
			break;
		default:
			finish();
			break;
		}
	}

	private void exit() {
		PreferencesUtil.getValue(PreferencesUtil.KEY_WORK_STATS,
				Constants.Value.WORKED);
		// 点击ok，退出当前帐号
		PreferencesUtil.clearLocalData(PreferenceManager
				.getDefaultSharedPreferences(this));
		Intent intent = new Intent(this, UserLoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(intent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// ActivityStackManager.getStackManager().popActivity(this);
	}
}
