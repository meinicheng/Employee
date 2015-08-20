package com.sdbnet.hywy.employee.utils;

import org.json.JSONObject;

import com.sdbnet.hywy.employee.service.TimeUploadService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class UtilsError {
	private static final String TAG = "UtilsError";

	public static boolean isErrorCode(Context context, JSONObject response)
			throws Exception {

		int errCode = response.getInt(Constants.Feild.KEY_ERROR_CODE);
		String msg = response.getString(Constants.Feild.KEY_MSG);
		if (errCode == 0) {
			return false;
		} else if (errCode == 41 || errCode == 42) {
			PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS,
					Constants.Value.WORKED);
			sendExitLogin(context, msg);
			return true;
		} else {
			Log.e(TAG, "isErrorCode msg=" + msg);
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}

		return true;

	}

	private static void sendExitLogin(Context context, String msg) {
		PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS,
				Constants.Value.WORKED);
		Intent intent = new Intent();
		intent.setAction(TimeUploadService.BROADCAST_SHOW_EXIT_DIALOG);
		intent.putExtra(TimeUploadService.BROADCAST_MSG_EXIT, msg);
		context.sendBroadcast(intent);
	}

	// public static void returnLogin(final Context context, String msg) {
	// PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS,
	// Constants.Value.WORKED);
	// if (context == null) {
	// ActivityStackManager.getStackManager().popAllActivitys();
	// return;
	// }
	// // Intent intent = new Intent(context, DialogTipActivity.class);
	// // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
	// // | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	// // intent.putExtra(DialogTipActivity.EXTRA_TITLE,
	// // context.getString(R.string.system_tip));
	// // intent.putExtra(DialogTipActivity.EXTRA_MSG, msg);
	// // context.startActivity(intent);
	//
	// new AlertDialog.Builder(context)
	// .setTitle(context.getString(R.string.system_tip))
	// // "系统提示")
	// .setMessage(msg)
	// .setPositiveButton(android.R.string.ok,
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// PreferencesUtil.putValue(
	// PreferencesUtil.KEY_WORK_STATS,
	// Constants.Value.WORKED);
	// // 点击ok，退出当前帐号
	// PreferencesUtil.clearLocalData(PreferenceManager
	// .getDefaultSharedPreferences(context));
	// Intent intent = new Intent(context,
	// UserLoginActivity.class);
	// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
	// | Intent.FLAG_ACTIVITY_NEW_TASK);
	// context.startActivity(intent);
	// }
	//
	// }).show();
	// }

	// public static boolean isSaveLocateLog() {
	// if (TextUtils.isEmpty(PreferencesUtil.user_company)
	// || TextUtils.isEmpty(PreferencesUtil.item_id)
	// || TextUtils.isEmpty(PreferencesUtil.user_id)
	// || TextUtils.isEmpty(PreferencesUtil.user_tel)) {
	// return false;
	// } else {
	// return true;
	// }
	// }

	// check error code
	// public static boolean checkErrorCode(JSONObject response, Context
	// context) {
	// int errCode;
	// try {
	// errCode = response.getInt(Constants.Feild.KEY_ERROR_CODE);
	// String msg = response.getString(Constants.Feild.KEY_MSG);
	// if (errCode == 0) {
	// return true;
	// } else if (errCode == 41 || errCode == 42) {
	// returnLogin(msg);
	// } else {
	// Log.e(TAG, "isErrorCode msg=" + msg);
	// Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	// }
	//
	// } catch (JSONException e) {
	// e.printStackTrace();
	// ErrLogUtils.uploadErrLog(context, (e));
	// return false;
	// }
	// // ErrLogUtils.uploadErrorCodeMsg(mContext,
	// // "upload locaitn return code="
	// // + errCode);
	// return false;
	// }
	//
	// private static void returnLogin(String msg) {
	// // 在其他地方登陆，不定位；
	// PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS,
	// Constants.Value.WORKED);
	// Activity activity = ActivityStackManager.getStackManager()
	// .currentActivity();
	// if (activity != null) {
	// showExitDialog(activity, msg);
	// }
	// }
	//
	// private static Builder mDialog;
	//
	// private static void showExitDialog(final Activity activity, String msg) {
	//
	// if (mDialog == null) {
	// mDialog = new AlertDialog.Builder(activity)
	// .setTitle(activity.getString(R.string.system_tip))
	// // "系统提示")
	// .setMessage(msg)
	// .setPositiveButton(android.R.string.ok,
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// exitApp(activity);
	// }
	//
	// }).setOnCancelListener(new OnCancelListener() {
	//
	// @Override
	// public void onCancel(DialogInterface dialog) {
	// exitApp(activity);
	// }
	// });
	//
	// }
	// mDialog.show();
	// }
	//
	// private static void exitApp(Activity activity) {
	// PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS,
	// Constants.Value.WORKED);
	// // 点击ok，退出当前帐号
	// PreferencesUtil.clearLocalData(PreferenceManager
	// .getDefaultSharedPreferences(activity));
	// Intent intent = new Intent(activity, UserLoginActivity.class);
	// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
	// | Intent.FLAG_ACTIVITY_NEW_TASK);
	// activity.startActivity(intent);
	// }
}
