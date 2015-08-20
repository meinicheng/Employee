package com.sdbnet.hywy.employee.receiver;

import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.ToastUtil;
import com.sdbnet.hywy.employee.utils.UtilsCommon;
import com.sdbnet.hywy.employee.utils.UtilsJava;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
	private final static String TAG = "MyBroadcastReceiver";
	private final String ACTION_BATTERY_CHANGED = "android.intent.action.BATTERY_CHANGED";

	public final String ACTION_OPERATELOG_UPLOAD = "android.alarm.action.OperateLog";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d(TAG, "action=" + action);
		if (ACTION_OPERATELOG_UPLOAD.equals(action)) {
			// OperateLogUtil.localLogUpload(context);
			UtilsCommon.operateLogUpload(context);
		} else if (ACTION_BATTERY_CHANGED.equals(action)) {
			saveBattery(context, intent);
		}
	}

	private int lastBattery;

	private void saveBattery(Context context, Intent intent) {
		// 获取当前电量
		int level = intent.getIntExtra("level", 0);
		// 电量的总刻度
		int scale = intent.getIntExtra("scale", 100);
		// 把它转成百分比
		int battery = Math.round(((level * 100) / scale));
		Log.i(TAG, "battery=" + battery + "<lastBattery=" + lastBattery);
		if (battery != lastBattery) {
			PreferencesUtil.putValue(PreferencesUtil.KEY_BATTERY, battery);
			PreferencesUtil.battery = battery;
			lastBattery = battery;
			showMsg(battery, context);
		}

	}

//	private void operateLogUpload(final Context context) {
//		DBManager manager = new DBManager(context);
//		List<OperateLog> list = manager.getAllOpreate();
//		manager.putLockOpreate();
//		manager.closeDatabase();
//
//		if (null == list || list.size() == 0) {
//			return;
//		}
//
//		Log.i(TAG, list.toString());
//		StringEntity stringEntity = null;
//		try {
//			stringEntity = new StringEntity(list.toString(), "utf-8");
//		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
//		}
//		// Log.i(TAG, stringEntity.toString());
//
//		AsyncHttpClient client = new AsyncHttpClient();
//		client.post(context, AsyncHttpService.BASE_URL + "/upBatchOpeLog",
//				stringEntity, "application/json",
//				new JsonHttpResponseHandler() {
//
//					@Override
//					public void onSuccess(int statusCode, Header[] headers,
//							JSONObject response) {
//						super.onSuccess(statusCode, headers, response);
//						LogUtil.d(response.toString());
//						try {
//							int errCode = response
//									.getInt(Constants.Feild.KEY_ERROR_CODE);
//							if (errCode == 0) {
//								deleteAllOpreate(context);
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					}
//				});
//	}
//
//	private void deleteAllOpreate(Context context) {
//		DBManager manager = new DBManager(context);
//		manager.deleteAllOpreate();
//		manager.closeDatabase();
//	}

	private void showMsg(int battery, Context context) {
		String data = "battery="
				+ battery
				+ ",\ntime="
				+ UtilsJava.translate2SessionMessageData(System
						.currentTimeMillis());
		// UtilsAndroid.Sdcard.save2SDCard(data, file);
		ToastUtil.show(context, data);
	}
}
