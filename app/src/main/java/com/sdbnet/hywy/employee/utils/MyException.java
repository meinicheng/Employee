package com.sdbnet.hywy.employee.utils;

import android.content.Context;

public class MyException extends Exception {

	private static final long serialVersionUID = 8355283422114834280L;
	private Context mContext;

	/**
	 * 自定义异常信息
	 * 
	 * @param msg
	 */
	public MyException(String msg, Context mContext) {
		super(msg);
		this.mContext = mContext;
	}

	public MyException(Context mContext) {
		super();
		this.mContext = mContext;
	}

	public void uploadExection() {
		ErrLogUtils.uploadErrLog(mContext, ErrLogUtils.toString(this));
	}
	@Override 
	public void printStackTrace() {
		super.printStackTrace();
		uploadExection();
	}

}
