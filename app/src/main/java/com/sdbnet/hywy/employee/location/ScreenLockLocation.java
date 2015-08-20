package com.sdbnet.hywy.employee.location;

import com.sdbnet.hywy.employee.service.TimeUploadService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class ScreenLockLocation {
	private final static String TAG = "ScreenLockLocation";
	private AlarmManager alarmManger = null;
	private PendingIntent pi = null;
	private Context context = null;
	private BroadcastReceiver timerReceiver;
	private static String action = "cn.hywy.center.request.location";

	private static ScreenLockLocation location;
	private boolean isStart;

	private ScreenLockLocation() {
	}

	public static ScreenLockLocation getInstance() {
		if (location == null) {
			location = new ScreenLockLocation();
		}
		return location;
	}

	public ScreenLockLocation init(Context context,
			BroadcastReceiver timerReceiver) {
		this.context = context.getApplicationContext();
		this.timerReceiver = timerReceiver;
		return location;
	}

	public boolean isStartScrrenLock() {
		return isStart && location != null;
	}

	/**
	 * 启动后台闹钟服务
	 */
	public void start() {
		if (context != null) {
			context.registerReceiver(timerReceiver, new IntentFilter(action));
			pi = PendingIntent.getBroadcast(context, 0, new Intent(action),
					PendingIntent.FLAG_UPDATE_CURRENT);
			alarmManger = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarmManger.setRepeating(AlarmManager.RTC_WAKEUP, 0,
					TimeUploadService.LOCATION_INTERVAL, pi);
			isStart = true;
		} else {
			Log.e(TAG, "please again init ScreenLocakLocation");
		}
	}

	/**
	 * 停止后台闹钟服务
	 */
	public void stop() {
		if (context != null && timerReceiver != null) {
			context.unregisterReceiver(timerReceiver);
			context = null;
			timerReceiver = null;
		}
		if (alarmManger != null && pi != null) {
			alarmManger.cancel(pi);
		}
		alarmManger = null;
		pi = null;
		isStart = false;
	}

}