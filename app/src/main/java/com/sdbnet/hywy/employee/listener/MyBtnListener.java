package com.sdbnet.hywy.employee.listener;

import java.util.Date;


import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public class MyBtnListener implements OnClickListener,IBtnCallListener {
	private Context mContext;
	private String msg;
	private Date date;

	public MyBtnListener(Context context, String msg) {
		this(context, msg, null);
	}

	public MyBtnListener(Context context, int resId) {
		this(context, resId, null);
	}

	public MyBtnListener(Context context, String msg, Date date) {
		this.mContext = context;
		this.msg = msg;
		this.date = date;
	}

	public MyBtnListener(Context context, int resId, Date date) {
		this(context, context.getString(resId),date);
	}

	@Override
	public void onClick(View arg0) {
//		OperateLogUtil.saveOperate(mContext, msg, date);
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public void onClickHandler() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBackPressedHandler() {
		// TODO Auto-generated method stub
		
	}

}
