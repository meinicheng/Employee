package com.sdbnet.hywy.employee.ui.widget;

import com.sdbnet.hywy.employee.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class DialogPostOrders extends Dialog implements OnClickListener {
	private String mAddress = "";
	private TextView mTvTitle;
	private TextView mTvSubmit;
	private TextView mTvCancle;
	private TextView mTvFlush;
	private Context mContext;

	public static void fixBackgroundRepeatY(View view) {
		Drawable bg = view.getBackground();
		if (bg != null) {
			if (bg instanceof BitmapDrawable) {
				BitmapDrawable bmp = (BitmapDrawable) bg;
				bmp.mutate(); // make sure that we aren't sharing state anymore
				bmp.setTileModeXY(null, TileMode.REPEAT);
			}
		}
	}

	public DialogPostOrders(Context context) {
		super(context, R.style.DialogCommonStyle);
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.dialog_post_order, null);

		setContentView(view);
		// fixBackgroundRepeatY(view);
		initUI();
	}

	private void initUI() {

		mTvTitle = (TextView) findViewById(R.id.dialog_post_order_title_text);
		mTvSubmit = (TextView) findViewById(R.id.dialog_post_order_submit_text);
		mTvCancle = (TextView) findViewById(R.id.dialog_post_order_cancle_text);
		mTvFlush = (TextView) findViewById(R.id.dialog_post_order_flush_text);
		mTvSubmit.setOnClickListener(this);
		mTvCancle.setOnClickListener(this);
		mTvFlush.setOnClickListener(this);
		mTvTitle.setText(mAddress);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.dialog_post_order_submit_text:
			if (backInterface != null) {
				dismiss();
				backInterface.onSubmit();
			}
			break;
		case R.id.dialog_post_order_cancle_text:
			dismiss();
			break;
		case R.id.dialog_post_order_flush_text:

			if (backInterface != null) {
				dismiss();
				backInterface.onFlush();

			}
			break;

		default:
			dismiss();
			break;
		}
	}

	public void setTitle(String address) {
		mAddress = address;
		if (mTvTitle != null) {
			mTvTitle.setText(mAddress);
		}
	}

	private CallBackInterface backInterface;

	// 定义接口.且在接口中定义一个方法
	public interface CallBackInterface {
		public void onSubmit();

		public void onFlush();
	}

	public void setCallBackInterface(CallBackInterface backInterface) {
		this.backInterface = backInterface;
	}

}
