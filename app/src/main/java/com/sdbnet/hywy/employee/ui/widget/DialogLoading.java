package com.sdbnet.hywy.employee.ui.widget;

import com.sdbnet.hywy.employee.R;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class DialogLoading extends AlertDialog {

	private TextView tips_loading_msg;

	private String message = null;

	public DialogLoading(Context context) {
		super(context, R.style.MyDialogStyle);
		message = getContext().getResources().getString(R.string.loading);
	}

	public DialogLoading(Context context, boolean isCancleable) {
		super(context, R.style.MyDialogStyle);
		this.setCancelable(isCancleable);
	}

	public DialogLoading(Context context, String message, boolean isCancleable) {
		super(context, R.style.MyDialogStyle);
		this.message = message;
		this.setCancelable(isCancleable);
	}

	public DialogLoading(Context context, String message) {
		super(context, R.style.MyDialogStyle);
		this.message = message;
		this.setCancelable(false);
	}

	public DialogLoading(Context context, int theme, String message) {
		super(context, theme);
		this.message = message;
		this.setCancelable(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_loading);
		// private ProgressBar mProgressBar; mProgressBar = (ProgressBar)
		// findViewById(R.id.progress_bar);
		tips_loading_msg = (TextView) findViewById(R.id.dialog_load_tip_text);
		tips_loading_msg.setText(this.message);
	}

	public void setMsg(String message) {
		this.message = message;
		if (tips_loading_msg != null)
			tips_loading_msg.setText(message);
	}

	public void setMsg(int resId) {
		setMsg(getContext().getResources().getString(resId));
	}

}
