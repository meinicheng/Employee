package com.sdbnet.hywy.employee.service;

import com.sdbnet.hywy.employee.utils.UtilsAndroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class GuardianService extends Service {
	private final String TAG = getClass().getSimpleName();
	private AlarmManager alarmManger;
	private PendingIntent pi = null;
	public static final String ACTION_GUARDIAN = "com.sdbnet.hywy.employee.action.start.guardian";
	private static final int CHECK_INTERVAL = 60 * 1000;
	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			return false;
		}
	});

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "GuardService created...");
		// startAlarmListener();
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				startWatchThread();

			}
		}, CHECK_INTERVAL / 2);
	}

	// private TimerReceiver timerReceiver;
	// private void startAlarmListener() {
	// timerReceiver = new TimerReceiver();
	// registerReceiver(timerReceiver, new IntentFilter(action));
	// alarmManger = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
	// pi = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(
	// action), PendingIntent.FLAG_UPDATE_CURRENT);
	// alarmManger.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0,
	// CHECK_INTERVAL, pi);
	// }
	//
	// class TimerReceiver extends BroadcastReceiver {
	//
	// @Override
	// public void onReceive(Context arg0, Intent arg1) {
	// checkAndWakeUpService(TimingUpLocateService.class);
	// }
	//
	// }
	private void stopAlarmListener() {
		// if (timerReceiver != null)
		// unregisterReceiver(timerReceiver);
		// timerReceiver = null;
		// alarmManger = null;
		// pi = null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "GuardService onDestroy...");
		stopAlarmListener();
		stopWatchThread();
		super.onDestroy();
	}

	private void checkAndWakeUpService(Class<?> clazz) throws Exception {
		// 检查Service状态
		boolean isRunning = UtilsAndroid.Set.isRunningService(this, clazz);

		Log.e("checkAndWakeUpService", "check service isRunning=" + isRunning
				+ "," + clazz.getSimpleName());
		if (!isRunning) {
			Intent service = new Intent(this, clazz);
			service.setAction(TimeUploadService.ACTION_LOCATION);
			startService(service);
		}
	}

	private boolean isStartWatch;
	private Thread watchThread;

	private void startWatchThread() {
		Log.d(TAG, "start watch thread");
		isStartWatch = true;
		if (null == watchThread) {
			watchThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (isStartWatch) {

						try {
							checkAndWakeUpService(TimeUploadService.class);
							// 休眠
							Thread.sleep(CHECK_INTERVAL);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
			});
			watchThread.start();

		}
	}

	private void stopWatchThread() {
		isStartWatch = false;
		watchThread = null;
	}

}
