package com.sdbnet.hywy.employee.receiver;

import java.io.File;
import com.sdbnet.hywy.employee.db.db.DBManager;
import com.sdbnet.hywy.employee.service.TimeUploadService;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.UtilsAndroid;
import com.sdbnet.hywy.employee.utils.UtilsCommon;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.util.Log;

public class SystemBroadcastReceiver extends BroadcastReceiver {
	private final static String TAG = "SystemBroadcastReceiver";
	private final String ACTION_BATTERY_CHANGED = "android.intent.action.BATTERY_CHANGED";
	private final String ACTION_NET_CON_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
	private final String ACTION_NET_WIFI_CHANGED = "android.net.wifi.WIFI_STATE_CHANGED";
	private final String ACTION_NET_WIFI_CHANGE = "android.net.wifi.STATE_CHANGE";

	private final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
	private final String ACTION_SHUT_DOWN = "android.intent.action.ACTION_SHUTDOWN";
	private int lastBattery;
	private File file = new File(Constants.SDBNET.BASE_PATH + "battery.txt");

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.e(TAG, "action=" + action);

		if (ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
			saveBattery(context, intent);
		} else if (ACTION_NET_CON_CHANGE.equals(action)) {
			receiverNetStatu(context);
		} else if (ACTION_BOOT_COMPLETED.equals(action)) {
			checkService(context);
		} else if (ACTION_SHUT_DOWN.equals(action)) {
			saveTrafficStats(context);
		}
	}

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
		}

	}


	private void saveTrafficStats(Context context) {
		Log.e("ShutdownReceiver", "exit");
		int mAppUid = UtilsAndroid.Set.getAppUid(context);
		long lastRxTrafficStats = TrafficStats.getUidRxBytes(mAppUid);
		long lastTxTrafficStats = TrafficStats.getUidTxBytes(mAppUid);
		long recordTrafficStats = PreferencesUtil.getValue(
				PreferencesUtil.KEY_TRAFFIC_STATS, 0l);
		PreferencesUtil.putValue(PreferencesUtil.KEY_TRAFFIC_STATS,
				lastRxTrafficStats + lastTxTrafficStats + recordTrafficStats);

	}

	private void checkService(Context context) {
		boolean isLocationRunning = false;
		boolean isGuardRunning = false;
		// 检查Service状态
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (TimeUploadService.class.getName().equals(
					service.service.getClassName())) {
				isLocationRunning = true;
			}
			// if
			// (GuardService.class.getName().equals(service.service.getClassName()))
			// {
			// isGuardRunning = true;
			// }
		}
		if (!isLocationRunning) {
			// 启动定位服务
			Intent service = new Intent(context, TimeUploadService.class);
			context.startService(service);
		}
		// if (!isGuardRunning) {
		// // 启动守护进程
		// Intent service2 = new Intent(context, GuardService.class);
		// context.startService(service2);
		// }
	}

	private void receiverNetStatu(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo gprs = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi = manager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		saveOpreateGprs(context, gprs.isConnected());
		saveOpreateWifi(context, wifi.isConnected());
		if (wifi.isConnected()) {
			Log.e("receiverNetStatu", "wifi connected ");
			// OperateLogUtil.localLogUpload(context);
			UtilsCommon.operateLogUpload(context);
		} else if (!gprs.isConnected() && !wifi.isConnected()) {
			// network closed
			Log.e("receiverNetStatu", "network closed ");
		} else {
			// network opend[java] view plaincopyprint?
			Log.e("receiverNetStatu", "network opend ");
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

	private void saveOpreateGprs(Context context, boolean isConGprs) {

		int gprsStatus = PreferencesUtil.getValue(
				PreferencesUtil.KEY_GPRS_STATUS, PreferencesUtil.STATUS_ON);
		if (isConGprs) {
			if (gprsStatus == PreferencesUtil.STATUS_OFF) {
				saveOpreateLog(context, "open gprs");
			}
		} else {
			if (gprsStatus == PreferencesUtil.STATUS_ON) {
				saveOpreateLog(context, "close gprs");
			}
		}
		PreferencesUtil.putValue(PreferencesUtil.KEY_GPRS_STATUS,
				isConGprs ? PreferencesUtil.STATUS_ON
						: PreferencesUtil.STATUS_OFF);
	}

	private void saveOpreateWifi(Context context, boolean isConWifi) {

		int wifiStatus = PreferencesUtil.getValue(
				PreferencesUtil.KEY_WIFI_STATUS, PreferencesUtil.STATUS_ON);
		if (isConWifi) {
			if (wifiStatus == PreferencesUtil.STATUS_OFF) {
				saveOpreateLog(context, "open wifi");
			}
		} else {
			if (wifiStatus == PreferencesUtil.STATUS_ON) {
				saveOpreateLog(context, "close wifi");
			}
		}
		PreferencesUtil.putValue(PreferencesUtil.KEY_WIFI_STATUS,
				isConWifi ? PreferencesUtil.STATUS_ON
						: PreferencesUtil.STATUS_OFF);
	}

	private void saveOpreateLog(Context context, String opreate) {
		DBManager manager = new DBManager(context);
		manager.saveOperate(opreate);
		manager.closeDatabase();
	}
}
