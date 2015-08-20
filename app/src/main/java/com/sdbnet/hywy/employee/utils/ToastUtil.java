package com.sdbnet.hywy.employee.utils;

import com.sdbnet.hywy.employee.MainApplication;
import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	private static boolean isDebug = false;

	public static void openShow() {
		isDebug = MainApplication.DEVELOPER_MODE;
		String tel = PreferencesUtil.getValue(PreferencesUtil.KEY_USER_TEL);
		if (MainApplication.mAccounts.contains(tel)) {
			isDebug = true;
		}
	}
	public static boolean getMode(){
		return isDebug;
	}

	public static void show(Context context, String msg) {
		if (isDebug) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
	}

	public static void show(Context context, int rid) {
		if (isDebug) {
			Toast.makeText(context, context.getString(rid), Toast.LENGTH_SHORT)
					.show();
		}
	}
}
