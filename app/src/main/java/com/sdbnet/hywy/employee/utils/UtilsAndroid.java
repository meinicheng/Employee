package com.sdbnet.hywy.employee.utils;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class UtilsAndroid {
	private final static String TAG = "UtilsAndroid";

	public final static class Set {
		private static String LOG_TAG = "NetWorkHelper";

		public static Uri uri = Uri.parse("content://telephony/carriers");

		/**
		 * 判断是否有网络连接
		 */
		public static boolean isNetworkAvailable(Context context) {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			if (connectivity == null) {
				Log.w(LOG_TAG, "couldn't get connectivity manager");
			} else {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].isAvailable()) {
							Log.d(LOG_TAG, "network is available");
							return true;
						}
					}
				}
			}
			Log.d(LOG_TAG, "network is not available");
			return false;
		}

		/**
		 * 检测网络状态
		 * 
		 * @param context
		 * @return
		 */
		public static boolean checkNetState(Context context) {
			boolean netstate = false;
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null) {
					for (int i = 0; i < info.length; i++) {
						if (info[i].getState() == NetworkInfo.State.CONNECTED) {
							netstate = true;
							break;
						}
					}
				}
			}
			return netstate;
		}

		/**
		 * 是否是飞行模式
		 */
		public static boolean IsAirModeOn(Context context) {
			return (Settings.System.getInt(context.getContentResolver(),
					Settings.System.AIRPLANE_MODE_ON, 0) == 1 ? true : false);
		}

		/**
		 * 判断网络是否为漫游
		 */
		public static boolean isNetworkRoaming(Context context) {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity == null) {
				Log.w(LOG_TAG, "couldn't get connectivity manager");
			} else {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null
						&& info.getType() == ConnectivityManager.TYPE_MOBILE) {
					TelephonyManager tm = (TelephonyManager) context
							.getSystemService(Context.TELEPHONY_SERVICE);
					if (tm != null && tm.isNetworkRoaming()) {
						Log.d(LOG_TAG, "network is roaming");
						return true;
					} else {
						Log.d(LOG_TAG, "network is not roaming");
					}
				} else {
					Log.d(LOG_TAG, "not using mobile network");
				}
			}
			return false;
		}

		/**
		 * 判断MOBILE网络是否可用
		 * 
		 * @param context
		 * @return
		 * @throws Exception
		 */
		public static boolean isMobileDataEnable(Context context) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			boolean isMobileDataEnable = false;

			isMobileDataEnable = connectivityManager.getNetworkInfo(
					ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();

			return isMobileDataEnable;
		}

		// 获取wifi状态
		public static boolean getWifiStatus(Context ctx) {
			ConnectivityManager cm = (ConnectivityManager) ctx
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			return cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ? true
					: false;
		}

		// 获取gprs状态
		public static boolean getGprsStatus(Context context) {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			return cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
					.getState() == NetworkInfo.State.CONNECTED ? true : false;
		}

		/**
		 * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
		 * 
		 * @param context
		 * @return true 表示开启
		 */
		public static boolean getGpsStatus(Context context) {
			LocationManager lm = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
			boolean gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
			// //
			// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
			// boolean network = lm
			// .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			// if (gps || network) {
			// return true;
			// }
			// return false;
			return gps;
		}

		/**
		 * 判断是否存在APP
		 * 
		 * @param packageName
		 * @return
		 */
		public static boolean checkAppExist(Context context, String packageName) {
			if (TextUtils.isEmpty(packageName)) {
				return false;
			}

			try {
				PackageInfo packageInfo = context.getPackageManager()
						.getPackageInfo(packageName, 0);
			} catch (NameNotFoundException e) {
				return false;
			}
			return true;
		}

		/**
		 * 获得屏幕高度
		 * 
		 * @param context
		 * @return
		 */
		public static int getScreenWidth(Context context) {
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics outMetrics = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(outMetrics);
			return outMetrics.widthPixels;
		}

		// 获取版本号
		public static int getVersionCode(Context context) {
			PackageManager packageManager = context.getPackageManager();
			// 0代表是获取版本信息
			PackageInfo packInfo;
			int versionCode = 0;
			try {
				packInfo = packageManager.getPackageInfo(
						context.getPackageName(), 0);
				versionCode = packInfo.versionCode;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return versionCode;
		}

		// 获取版本的名字
		public static String getVersionName(Context ctx) {
			try {
				PackageManager manager = ctx.getPackageManager();
				PackageInfo info = manager.getPackageInfo(ctx.getPackageName(),
						0);
				String version = info.versionName;
				return version;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		public static boolean isRunningService(Context context, Class clazz) {
			boolean isRunning = false;
			// 检查Service状态
			android.app.ActivityManager manager = (android.app.ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			if (manager == null)
				return false;
			for (RunningServiceInfo service : manager
					.getRunningServices(Integer.MAX_VALUE)) {
				if (clazz.getName().equals(service.service.getClassName())) {
					isRunning = true;
					break;
				}
			}
			return isRunning;
		}

		/**
		 * 判断wifi 是否可用
		 * 
		 * @param context
		 * @return
		 * @throws Exception
		 */
		public static boolean isWifiDataEnable(Context context) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifiInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifiInfo != null)
				return wifiInfo.isConnectedOrConnecting();
			else
				return false;

		}

		/**
		 * 设置Mobile网络开关
		 * 
		 * @param context
		 * @param enabled
		 * @throws Exception
		 */
		// public static void setMobileDataEnabled(Context context, boolean
		// enabled)
		// throws Exception {
		// APNManager apnManager = APNManager.getInstance(context);
		// List<APN> list = apnManager.getAPNList();
		// if (enabled) {
		// for (APN apn : list) {
		// ContentValues cv = new ContentValues();
		// cv.put("apn", apnManager.matchAPN(apn.apn));
		// cv.put("type", apnManager.matchAPN(apn.type));
		// context.getContentResolver().update(uri, cv, "_id=?",
		// new String[] { apn.apnId });
		// }
		// } else {
		// for (APN apn : list) {
		// ContentValues cv = new ContentValues();
		// cv.put("apn", apnManager.matchAPN(apn.apn) + "mdev");
		// cv.put("type", apnManager.matchAPN(apn.type) + "mdev");
		// context.getContentResolver().update(uri, cv, "_id=?",
		// new String[] { apn.apnId });
		// }
		// }
		// }
		// /**
		// * 打开移动网络
		// *
		// * @param context
		// */
		// public static void openMobileNetWork(Context context) {
		// ConnectivityManager connectivityManager = (ConnectivityManager)
		// context
		// .getSystemService(Context.CONNECTIVITY_SERVICE);
		//
		// Class<?> connectivityManagerClass = null;
		// Field connectivityManagerField = null;
		//
		// Class<?> iConnectivityManagerClass = null;
		// Object iConnectivityManagerObject = null;
		// Method setMobileDataEnabledMethod = null;
		//
		// try {
		// // 取得ConnectivityManager类
		// connectivityManagerClass = Class.forName(connectivityManager
		// .getClass().getName());
		//
		// // 取得ConnectivityManager类中的字段mService
		// connectivityManagerField = connectivityManagerClass
		// .getDeclaredField("mService");
		// // 取消访问私有字段的合法性检查
		// connectivityManagerField.setAccessible(true);
		//
		// // 实例化mService
		// iConnectivityManagerObject = connectivityManagerField
		// .get(connectivityManager);
		//
		// iConnectivityManagerClass = Class
		// .forName(iConnectivityManagerObject.getClass()
		// .getName());
		// setMobileDataEnabledMethod = iConnectivityManagerClass
		// .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
		// setMobileDataEnabledMethod.setAccessible(true);
		// setMobileDataEnabledMethod.invoke(iConnectivityManagerObject,
		// true);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		/**
		 * 是否含有gps模块
		 * 
		 * @param context
		 * @return
		 */
		public static boolean hasGPSDevice(Context context) {
			final LocationManager mgr = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			if (mgr == null)
				return false;
			final List<String> providers = mgr.getAllProviders();
			if (providers == null)
				return false;
			return providers.contains(LocationManager.GPS_PROVIDER);
		}

		// /**
		// * GPS是否可用
		// */
		// public static boolean isGPSEnable(Context context) {
		// String str = Settings.Secure.getString(
		// context.getContentResolver(),
		// Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		// if (str != null) {
		// // System.out.println("gps: " + str);
		// LogUtil.d(TAG, "gps:" + str);
		// return str.contains("gps");
		// } else {
		// return false;
		// }
		// }

		// /**
		// * 强制帮用户打开GPS
		// *
		// * @param context
		// */
		// public static final void openGPSFroce(Context context) {
		// if(getGpsStatus(context)){
		// return ;
		// }
		// Intent GPSIntent = new Intent();
		// GPSIntent.setClassName("com.android.settings",
		// "com.android.settings.widget.SettingsAppWidgetProvider");
		// GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
		// GPSIntent.setData(Uri.parse("custom:3"));
		// try {
		// PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
		// } catch (CanceledException e) {
		// e.printStackTrace();
		// }
		// LogUtil.d("openGPSFroce");
		// }

		/**
		 * 打开gps
		 */
		public static void openGPSSettings(Context context) {
			if (!hasGPSDevice(context) || getGpsStatus(context)) {
				return;
			}

			LocationManager locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			if (locationManager
					.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
				return;
			}

			Toast.makeText(context, "请开启GPS！", Toast.LENGTH_SHORT).show();

			// 跳转到gps系统设置界面
			Intent intent = new Intent();
			intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			try {
				context.startActivity(intent);
			} catch (ActivityNotFoundException ex) {
				ErrLogUtils.uploadErrLog(context, ErrLogUtils.toString(ex));
				intent.setAction(Settings.ACTION_SETTINGS);
				try {
					context.startActivity(intent);
				} catch (Exception e) {
					ErrLogUtils.uploadErrLog(context, ErrLogUtils.toString(e));
				}
			}
		}

		/**
		 * 打开wifi
		 */
		public static void openWifi(Context context) {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (!wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(true);
			}
		}

		/**
		 * 打开网络
		 */
		public static void openNetwork(Context context) {
			if (checkNetState(context)) {
				return;
			}
			Toast.makeText(context, "请开启网络！", Toast.LENGTH_SHORT).show();
			try {
				Intent intent = null;
				// 判断手机系统的版本 即API大于10 就是3.0或以上版本
				if (android.os.Build.VERSION.SDK_INT > 10) {
					intent = new Intent(
							android.provider.Settings.ACTION_WIRELESS_SETTINGS);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
				// } else {
				// intent = new Intent();
				// ComponentName component = new ComponentName(
				// "com.android.settings",
				// "com.android.settings.WirelessSettings");
				// intent.setComponent(component);
				// intent.setAction("android.intent.action.VIEW");
				// }
				// intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			} catch (Exception e) {
				e.printStackTrace();
				ErrLogUtils.uploadErrLog(context, e);
			}

		}

		/**
		 * 
		 * @Description : 这个包名的程序是否在运行
		 * @Method_Name : isRunningApp
		 * @param context
		 *            上下文
		 * @param packageName
		 *            判断程序的包名
		 * @return 必须加载的权限 <uses-permission
		 *         android:name="android.permission.GET_TASKS">
		 * @return : boolean
		 * @Creation Date : 2014-10-31 下午1:14:15
		 * @version : v1.00
		 * @Author : JiaBin
		 * 
		 * @Update Date :
		 * @Update Author : JiaBin
		 */
		// public static boolean isRunningApp(Context context, String
		// packageName) {
		// boolean isAppRunning = false;
		// ActivityManager am = (ActivityManager) context
		// .getSystemService(Context.ACTIVITY_SERVICE);
		// List<runningtaskinfo> list = am.getRunningTasks(100);
		// for (RunningTaskInfo info : list) {
		// if (info.topActivity.getPackageName().equals(packageName)
		// && info.baseActivity.getPackageName().equals(
		// packageName)) {
		// isAppRunning = true;
		// // find it, break
		// break;
		// }
		// }
		// return isAppRunning;
		// }
		public static long getTotalRxBytes() { // 获取总的接受字节数，包含Mobile和WiFi等
			return TrafficStats.getTotalRxBytes() == TrafficStats.UNSUPPORTED ? 0
					: (TrafficStats.getTotalRxBytes() / 1024);
		}

		public static long getTotalTxBytes() { // 总的发送字节数，包含Mobile和WiFi等
			return TrafficStats.getTotalTxBytes() == TrafficStats.UNSUPPORTED ? 0
					: (TrafficStats.getTotalTxBytes() / 1024);
		}

		public static long getMobileRxBytes() { // 获取通过Mobile连接收到的字节总数，不包含WiFi
			return TrafficStats.getMobileRxBytes() == TrafficStats.UNSUPPORTED ? 0
					: (TrafficStats.getMobileRxBytes() / 1024);
		}

		public static long getUidRxBytes(int uid) {
			return TrafficStats.getUidRxBytes(uid);
		}

		public static int getAppUid(Context context) {
			int uid = 0;
			ActivityManager am = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			ApplicationInfo appinfo = context.getApplicationInfo();
			List<RunningAppProcessInfo> run = am.getRunningAppProcesses();
			for (RunningAppProcessInfo runningProcess : run) {
				if ((runningProcess.processName != null)
						&& runningProcess.processName
								.equals(appinfo.processName)) {
					uid = runningProcess.uid;
					break;
				}
			}
			return uid;
		}
		/**
		 * 隐藏软键盘
		 * 
		 * @param activity 当前Activity
		 */
		public static void hideSoftInput(Activity activity) {
			((InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(activity.getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			// (activity是当前的Activity)
		}
		/**
		 * 返回当前的应用是否处于前台显示状态
		 * 
		 * @param $packageName
		 * @return
		 */
		public static boolean isTopActivity(Context context) {
			String $packageName = context.getPackageName();
			// _context是一个保存的上下文
			ActivityManager __am = (ActivityManager) context
					.getApplicationContext().getSystemService(
							Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningAppProcessInfo> __list = __am
					.getRunningAppProcesses();
			if (__list.size() == 0)
				return false;
			for (ActivityManager.RunningAppProcessInfo __process : __list) {
				Log.d(TAG, Integer.toString(__process.importance));
				Log.d(TAG, __process.processName);
				if (__process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
						&& __process.processName.equals($packageName)) {
					return true;
				}
			}
			return false;
		}
	}

	// public static isBackgroundRunning(Context context) {
	// String processName = "match.android.activity";
	//
	// ActivityManager activityManager = (ActivityManager)
	// getSystemService(ACTIVITY_SERVICE);
	// KeyguardManager keyguardManager = (KeyguardManager)
	// getSystemService(KEYGUARD_SERVICE);
	//
	// if (activityManager == null)
	// return false;
	// // get running application processes
	// List<ActivityManager.RunningAppProcessInfo> processList = activityManager
	// .getRunningAppProcesses();
	// for (ActivityManager.RunningAppProcessInfo process : processList) {
	// if (process.processName.startsWith(processName)) {
	// boolean isBackground = process.importance != IMPORTANCE_FOREGROUND
	// && process.importance != IMPORTANCE_VISIBLE;
	// boolean isLockedState = keyguardManager
	// .inKeyguardRestrictedInputMode();
	// if (isBackground || isLockedState)
	// return true;
	// else
	// return false;
	// }
	// }
	// return false;
	// }
	public static final class UI {
		/**
		 * 将px值转换为dip或dp值，保证尺寸大小不变
		 * 
		 * @param pxValue
		 * @param scale
		 *            （DisplayMetrics类中属性density）
		 * @return
		 */
		public static int px2dip(Context context, float pxValue) {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (pxValue / scale + 0.5f);
		}

		/**
		 * 将dip或dp值转换为px值，保证尺寸大小不变
		 * 
		 * @param dipValue
		 * @param scale
		 *            （DisplayMetrics类中属性density）
		 * @return
		 */
		public static int dip2px(Context context, float dipValue) {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (dipValue * scale + 0.5f);
		}

		/**
		 * 将px值转换为sp值，保证文字大小不变
		 * 
		 * @param pxValue
		 * @param fontScale
		 *            （DisplayMetrics类中属性scaledDensity）
		 * @return
		 */
		public static int px2sp(Context context, float pxValue) {
			final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
			return (int) (pxValue / fontScale + 0.5f);
		}

		/**
		 * 将sp值转换为px值，保证文字大小不变
		 * 
		 * @param spValue
		 * @param fontScale
		 *            （DisplayMetrics类中属性scaledDensity）
		 * @return
		 */
		public static int sp2px(Context context, float spValue) {
			final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
			return (int) (spValue * fontScale + 0.5f);
		}

		public static int sp2dp(Context context, float spValue) {
			int px = sp2px(context, spValue);
			return px2dip(context, px);
		}

		public static int dp2sp(Context context, float spValue) {
			int px = dip2px(context, spValue);
			return px2sp(context, px);
		}

		public static int getScreenWidth(Context context) {
			return context.getResources().getDisplayMetrics().widthPixels;
		}

		public static int getScreenHeight(Context context) {
			return context.getResources().getDisplayMetrics().heightPixels;
		}

		/**
		 * 获取Listview的高度，然后设置ViewPager的高度
		 * 
		 * @param listView
		 * @return
		 */
		public static int setListViewHeightBasedOnChildren(ListView listView) {
			// 获取ListView对应的Adapter
			ListAdapter listAdapter = listView.getAdapter();
			if (listAdapter == null) {
				// pre-condition
				return 0;
			}

			int totalHeight = 0;
			for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
				View listItem = listAdapter.getView(i, null, listView);
				listItem.measure(0, 0); // 计算子项View 的宽高
				// Log.i("len", len+"");
				totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度

			}

			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight
					+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
			// listView.getDividerHeight()获取子项间分隔符占用的高度
			// params.height最后得到整个ListView完整显示需要的高度
			listView.setLayoutParams(params);
			return params.height;
		}
	}

	public static final class Common {

		private static final long TIME_INTERVAL = 1000;
		private static long last_click_time;
		private static long current_click_time;

		public static boolean isFastDoubleClick() {
			current_click_time = System.currentTimeMillis();
			if (current_click_time - last_click_time > TIME_INTERVAL) {
				last_click_time = current_click_time;
				return false;
			} else {
				return true;
			}
		}

		private static long lastRxTrafficStats;

		public static long getRxTrafficStats(int mAppUid) {
			long l = TrafficStats.getUidRxBytes(mAppUid);
			long t = l - lastRxTrafficStats;
			lastRxTrafficStats = l;
			return t;
		}

		private static long lastTxTrafficStats;

		public static long getTxTrafficStats(int mAppUid) {
			long l = TrafficStats.getUidTxBytes(mAppUid);
			long t = l - lastTxTrafficStats;
			lastTxTrafficStats = l;
			return t;
		}

		public static void initTrafficStats(int mAppUid) {
			lastRxTrafficStats = TrafficStats.getUidRxBytes(mAppUid);
			lastTxTrafficStats = TrafficStats.getUidTxBytes(mAppUid);
		}

		public static float getTrafficStats(int mAppUid) {
			return (getRxTrafficStats(mAppUid) + getTxTrafficStats(mAppUid)) / 1024f;
		}

	}

	public static class Sdcard {

		// public static String SDPATH_FORMATS =
		// Constants.SDBNET.SDPATH_FORMATS;
		public static void toFile(File f, String s) throws IOException {
			// 只有手机rom有足够的空间才写入本地缓存
			if (UtilsAndroid.Sdcard.enoughSpaceOnPhone(s.getBytes().length)) {
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(s.getBytes());
				fos.close();
			}
		}

		/**
		 * 保存图片
		 * 
		 * @param bm
		 * @param picName
		 */
		public static String saveBitmap(Bitmap bm, String picName) {
			Log.d(TAG, "保存图片");
			String path = null;
			try {
				File file = new File(Constants.SDBNET.SDPATH_FORMATS);
				if (!file.exists()) {
					file.mkdirs();
				}
				File f = new File(Constants.SDBNET.SDPATH_FORMATS, picName
						+ ".JPEG");
				if (f.exists()) {
					f.delete();
				}
				FileOutputStream out = new FileOutputStream(f);
				bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
				out.flush();
				out.close();
				path = f.getAbsolutePath();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return path;
		}

		public static String saveBitmap(Bitmap bm) {
			String picName = System.currentTimeMillis() + "";
			return saveBitmap(bm, picName);
		}

		/**
		 * 创建目录
		 * 
		 * @param dirName
		 * @return
		 * @throws IOException
		 */
		// public static File createSDDir(String dirPath) throws IOException {
		// File dir = new File(SDPATH_FORMATS + dirPath);
		// if (Environment.getExternalStorageState().equals(
		// Environment.MEDIA_MOUNTED)) {
		//
		// System.out.println("createSDDir:" + dir.getAbsolutePath());
		// System.out.println("createSDDir:" + dir.mkdir());
		// }
		// return dir;
		// }

		/**
		 * 判断文件是否存在
		 * 
		 * @param fileName
		 * @return
		 */
		public static boolean isFileExist(String filePath) {
			File file = new File(filePath);
			// file.isFile();
			return (file != null && file.exists());
		}

		/**
		 * 删除文件
		 * 
		 * @param fileName
		 */
		public static void deleteFile(String filePath) {
			File file = new File(filePath);
			if (file != null && file.exists() && file.isFile()) {
				file.delete();
			}
			// file.exists();
		}

		/**
		 * 删除目录下的所有文件不包括目录
		 */
		public static void deleteDir(String dirPath) {
			File dir = new File(dirPath);
			if (dir == null || !dir.exists() || !dir.isDirectory())
				return;

			for (File file : dir.listFiles()) {
				if (file.isFile())
					file.delete(); // 删除所有文件
				else if (file.isDirectory())
					deleteDir(file.getAbsolutePath()); // 递规的方式删除文件夹
			}
			// dir.delete();// 删除目录本身
		}

		/**
		 * 判断指定的文件是否存在
		 * 
		 * @param path
		 * @return
		 */
		public static boolean fileIsExists(String path) {
			try {
				File f = new File(path);
				if (!f.exists()) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
			return true;
		}

		/**
		 * 复制文件；
		 * 
		 * @param file
		 * @param newPath
		 */
		public static void copyFile(File file, String newPath) {

			int byteread = 0;

			InputStream is = null;
			FileOutputStream fos = null;
			if (file != null && file.exists()) {
				try {
					is = new FileInputStream(file);
					fos = new FileOutputStream(newPath);
					byte[] buffer = new byte[1024];
					int length;
					while ((byteread = is.read(buffer)) != -1) {
						fos.write(buffer, 0, byteread);
					}
				} catch (FileNotFoundException e) {
					// delFile(newPath);
					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				} finally {
					try {
						if (is != null)
							is.close();
						if (fos != null)
							fos.close();
					} catch (IOException e) {

						e.printStackTrace();
					}

				}
			}
		}

		public static void copyFile(String oldPath, String newPath) {
			copyFile(new File(oldPath), newPath);
		}

		// /** 缓存文件目录 */
		// public static File mCacheDir;
		// static{
		//
		//
		// // Find the dir to save cached images
		// if (android.os.Environment.getExternalStorageState().equals(
		// android.os.Environment.MEDIA_MOUNTED))
		// mCacheDir = new File(cacheDir, dir);
		// else
		// mCacheDir = context.getCacheDir();
		// if (!mCacheDir.exists())
		// mCacheDir.mkdirs();
		// }
		// /**
		// * 创建缓存文件目录，如果有SD卡，则使用SD，如果没有则使用系统自带缓存目录
		// *
		// * @param context
		// * @param cacheDir
		// * 图片缓存的一级目录
		// * @param dir
		// */
		// public static FileCache(Context context, File cacheDir, String dir) {
		//
		// }
		//
		// public static File getFile(String url) {
		// // I identify images by hashcode. Not a perfect solution, good for
		// // the demo.
		// // String filename=String.valueOf(url.hashCode());
		// // Another possible solution (thanks to grantland)
		// String filename = URLEncoder.encode(url);
		// File f = new File(mCacheDir, filename);
		// return f;
		//
		// }
		//
		// /**
		// * 清除缓存文件
		// */
		// public static void clear() {
		// File[] files = mCacheDir.listFiles();
		// for (File f : files)
		// f.delete();
		// }

		public static String SDPATH_GPS = Environment
				.getExternalStorageDirectory() + "/gps/";

		private final static String name = System.currentTimeMillis() + ".txt";

		public static void appendFiledata(String message) {
			appendFileData(SDPATH_GPS + name, message);
		}

		public static void appendFileData(String fileName, String message) {
			try {

				File file = new File(fileName);
				// 判断目标文件所在的目录是否存在
				if (!file.getParentFile().exists()) {
					// 如果目标文件所在的目录不存在，则创建父目录
					file.getParentFile().mkdirs();
				}
				if (!file.exists()) {
					file.createNewFile();
				}
				// true = append file
				FileWriter fileWritter = new FileWriter(file, true);
				BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
				bufferWritter.write(message);
				bufferWritter.close();
				// System.out.println("Done");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 将数据保存到本地
		 * 
		 * @param data
		 */
		public static void save2SDCard(String data) {
			// 获取扩展SD卡设备状态
			String sDStateString = android.os.Environment
					.getExternalStorageState();

			// 拥有可读可写权限
			if (sDStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {
				try {

					// 获取扩展存储设备的文件目录
					File SDFile = android.os.Environment
							.getExternalStorageDirectory();
					// 打开文件
					File myFile = new File(SDFile, "locdata.txt");
					// 判断是否存在,不存在则创建
					if (!myFile.exists()) {
						myFile.createNewFile();
					}
					FileWriter writer = new FileWriter(myFile, true);
					// 写数据
					writer.write(data);
					writer.close();
					// FileOutputStream outputStream = new
					// FileOutputStream(myFile);
					// outputStream.write(data.getBytes());
					// outputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}// end of try
			}// end of if(MEDIA_MOUNTED)
		}

		/**
		 * 将数据保存到本地
		 * 
		 * @param data
		 */
		public static void save2SDCard(String data, File file) {
			// 获取扩展SD卡设备状态
			String sDStateString = android.os.Environment
					.getExternalStorageState();

			// 拥有可读可写权限
			if (sDStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {
				try {
					if (!file.exists()) {
						file.createNewFile();
					}
					FileWriter writer = new FileWriter(file, true);
					// 写数据
					writer.write(data);
					writer.close();

				} catch (Exception e) {
					e.printStackTrace();
				}// end of try
				Log.d(TAG, "save date success");
			}// end of if(MEDIA_MOUNTED)
		}

		// /**
		// * 检测sdcard是否可用
		// *
		// * @return true为可用，否则为不可用
		// */
		// public static boolean sdCardIsAvailable() {
		// String status = Environment.getExternalStorageState();
		// if (!status.equals(Environment.MEDIA_MOUNTED))
		// return false;
		// return true;
		// }
		/**
		 * 判断手机是否有SD卡。
		 * 
		 * @return 有SD卡返回true，没有返回false。
		 */
		public static boolean hasSDCard() {
			return Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState());
		}

		/**
		 * Checks if there is enough Space on SDCard
		 * 
		 * @param updateSize
		 *            Size to Check
		 * @return True if the Update will fit on SDCard, false if not enough
		 *         space on SDCard Will also return false, if the SDCard is not
		 *         mounted as read/write
		 */
		public static boolean enoughSpaceOnSdCard(long updateSize) {
			String status = Environment.getExternalStorageState();
			if (!status.equals(Environment.MEDIA_MOUNTED))
				return false;
			return (updateSize < getRealSizeOnSdcard());
		}

		/**
		 * get the space is left over on sdcard
		 */
		public static long getRealSizeOnSdcard() {
			File path = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath());
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			return availableBlocks * blockSize;
		}

		/**
		 * Checks if there is enough Space on phone self
		 * 
		 */
		public static boolean enoughSpaceOnPhone(long updateSize) {
			return getRealSizeOnPhone() > updateSize;
		}

		/**
		 * get the space is left over on phone self
		 */
		public static long getRealSizeOnPhone() {
			File path = Environment.getDataDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			long realSize = blockSize * availableBlocks;
			return realSize;
		}

		/**
		 * 获得程序在sd开上的cahce目录
		 * 
		 * @param context
		 *            The context to use
		 * @return The external cache dir
		 */
		@SuppressLint("NewApi")
		public static String getExternalCacheDir(Context context) {
			// android 2.2 以后才支持的特性
			if (hasExternalCacheDir()) {
				return context.getExternalCacheDir().getPath() + File.separator
						+ "img";
			}

			// Before Froyo we need to construct the external cache dir
			// ourselves
			// 2.2以前我们需要自己构造
			final String cacheDir = "/Android/data/" + context.getPackageName()
					+ "/cache/img/";
			return Environment.getExternalStorageDirectory().getPath()
					+ cacheDir;
		}

		public static boolean hasExternalCacheDir() {
			return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
		}

		/**
		 * 每次打开含有大量图片的activity时,开一个新线程,检查并清理缓存
		 * 
		 * @param context
		 */
		public static void checkCache(final Context context) {
			new Thread() {
				@Override
				public void run() {
					int state = 0;// 记录清除结果 0为都没清除, 1为只清除了sd卡, 2为只清除了rom Cache
									// ,3
									// 为都清除了
					String cacheS = "0M";
					String cacheD = "0M";
					File sdCache = new File(getExternalCacheDir(context)); // sd卡"mnt/sdcard/android/data/cn.tule.app/cache/";
					File cacheDir = context.getCacheDir(); // 手机data/data/com.mengniu.app/cache
					try {
						if (sdCache != null && sdCache.exists()) {
							long sdFileSize = getFileSize(sdCache);
							if (sdFileSize > 1024 * 1024 * 50) {
								// SD需要清理
								long clearFileSize = clear(sdCache);
								state += 1;
								cacheS = clearFileSize + "";
							}
						}
						if (cacheDir != null && cacheDir.exists()) {
							long cacheFileSize = getFileSize(cacheDir);
							if (cacheFileSize > 1024 * 1024 * 50) {
								// ROM需要清理
								long clearFileSize = clear(cacheDir);
								state += 2;
								cacheD = clearFileSize + "";
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				};
			}.start();
		}

		/**
		 * 清除路径
		 * 
		 * @param cacheDir
		 * @return
		 */
		private static int DayCount = 15;// 天数
		private static final long CLEARTIME = DayCount * 24 * 60 * 60 * 1000;

		public static long clear(File cacheDir) {
			long clearFileSize = 0;
			File[] files = cacheDir.listFiles();
			if (files == null)
				return 0;
			for (File f : files) {
				if (f.isFile()) {
					if (System.currentTimeMillis() - f.lastModified() > CLEARTIME) {
						long fileSize = f.length();
						if (f.delete()) {
							clearFileSize += fileSize;
						}
					}
				} else {
					clear(f);
				}
			}
			return clearFileSize;
		}

		/**
		 * 取得文件大小
		 * 
		 * @param f
		 * @return
		 * @throws Exception
		 */
		public static long getFileSize(File f) {
			long size = 0;
			File flist[] = f.listFiles();
			for (int i = 0; i < flist.length; i++) {
				if (flist[i].isDirectory()) {
					size = size + getFileSize(flist[i]);
				} else {
					size = size + flist[i].length();
				}
			}
			return size;
		}

		/**
		 * 转换文件大小
		 * 
		 * @param fileS
		 * @return
		 */
		public static String FormetFileSize(long fileS) {
			DecimalFormat df = new DecimalFormat("#.00");
			String fileSizeString = "";
			if (fileS < 1024) {
				fileSizeString = df.format((double) fileS) + "B";
			} else if (fileS < 1048576) {
				fileSizeString = df.format((double) fileS / 1024) + "K";
			} else if (fileS < 1073741824) {
				fileSizeString = df.format((double) fileS / 1048576) + "M";
			} else {
				fileSizeString = df.format((double) fileS / 1073741824) + "G";
			}
			return fileSizeString;
		}

	}

	public static class Media {
		// 图片sd地址 上传服务器时把图片调用下面方法压缩后 保存到临时文件夹 图片压缩后小于100KB，失真度不明显
		public static Bitmap revitionImageSize(String path) throws IOException {
			File file = new File(path);
			Bitmap bitmap = null;
			if (file.exists()) {
				BufferedInputStream in = new BufferedInputStream(
						new FileInputStream(file));
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(in, null, options);
				in.close();
				int i = 0;
				while (true) {

					// if ((options.outWidth >> i <= 1000)
					// && (options.outHeight >> i <= 1000)) {

					if ((options.outWidth >> i) * (options.outHeight >> i) <= 500 * 500) {
						in = new BufferedInputStream(new FileInputStream(
								new File(path)));
						options.inSampleSize = (int) Math.pow(2.0D, i);
						options.inJustDecodeBounds = false;
						bitmap = BitmapFactory.decodeStream(in, null, options);
						in.close();
						break;
					}
					i += 1;
				}
			}
			return bitmap;
		}

		public static Bitmap compressImage(Bitmap image) {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			int options = 100;
			while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
				baos.reset();// 重置baos即清空baos
				options -= 10;// 每次都减少10
				image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中

			}

			ByteArrayInputStream isBm = new ByteArrayInputStream(
					baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片

			try {
				baos.close();
				isBm.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return bitmap;
		}

		public static Bitmap getimage(String srcPath) {
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
			newOpts.inJustDecodeBounds = true;
			Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

			newOpts.inJustDecodeBounds = false;
			int w = newOpts.outWidth;
			int h = newOpts.outHeight;
			// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
			float hh = 800f;// 这里设置高度为800f
			float ww = 480f;// 这里设置宽度为480f
			// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
			int be = 1;// be=1表示不缩放
			if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
				be = (int) (newOpts.outWidth / ww);
			} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
				be = (int) (newOpts.outHeight / hh);
			}
			if (be <= 0)
				be = 1;
			newOpts.inSampleSize = be;// 设置缩放比例
			// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
			bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
			return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
		}

		/**
		 * 保存图片到SD卡
		 * 
		 * @param imagePath
		 * @param buffer
		 * @throws IOException
		 */
		public static void saveImage(String imagePath, byte[] buffer)
				throws IOException {
			File f = new File(imagePath);
			if (f.exists()) {
				return;
			} else {
				File parentFile = f.getParentFile();
				if (!parentFile.exists()) {
					parentFile.mkdirs();
				}
				f.createNewFile();
				FileOutputStream fos = new FileOutputStream(imagePath);
				fos.write(buffer);
				fos.flush();
				fos.close();
			}
		}

		/**
		 * 保存图片到缓存
		 * 
		 * @param imagePath
		 * @param bm
		 */
		public static boolean saveImage(String imagePath, Bitmap bm) {

			if (bm == null || imagePath == null || "".equals(imagePath)) {
				return false;
			}

			File f = new File(imagePath);
			FileOutputStream fos = null;
			if (f.exists()) {
				return false;
			} else {
				try {
					File parentFile = f.getParentFile();
					if (!parentFile.exists()) {
						parentFile.mkdirs();
					}
					f.createNewFile();
					fos = new FileOutputStream(f);
					bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
				} catch (FileNotFoundException e) {
					f.delete();
					e.printStackTrace();
					return false;
				} catch (IOException e) {
					e.printStackTrace();
					f.delete();
					return false;
				} finally {
					try {
						if (fos != null)
							fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return true;
		}

		public static String saveImage(Bitmap bm) {
			File picFile = new File(Constants.SDBNET.PATH_PHOTO
					+ System.currentTimeMillis() + "jpg");
			if (!picFile.exists()) {
				try {
					picFile.createNewFile();
					if (saveImage(picFile.getAbsolutePath(), bm)) {
						return picFile.getAbsolutePath();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		/**
		 * 从SD卡加载图片
		 * 
		 * @param imagePath
		 * @return
		 */
		public static Bitmap getImageFromLocal(String imagePath) {
			File file = new File(imagePath);
			if (file.exists()) {
				Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
				file.setLastModified(System.currentTimeMillis());
				return bitmap;
			}
			return null;
		}
	}

	public static final class SqliteWrapper {
		private static final String SQLITE_EXCEPTION_DETAIL_MESSAGE = "unable to open database file";

		private SqliteWrapper() {
			// Forbidden being instantiated.
		}

		// FIXME: need to optimize this method.
		private static boolean isLowMemory(SQLiteException e) {
			return e.getMessage().equals(SQLITE_EXCEPTION_DETAIL_MESSAGE);
		}

		public static void checkSQLiteException(Context context,
				SQLiteException e) {
			if (isLowMemory(e)) {
				Toast.makeText(context,
						"com.android.internal.R.string.low_memory",
						Toast.LENGTH_SHORT).show();
			} else {
				throw e;
			}
		}

		public static Cursor query(Context context, ContentResolver resolver,
				Uri uri, String[] projection, String selection,
				String[] selectionArgs, String sortOrder) {
			try {
				return resolver.query(uri, projection, selection,
						selectionArgs, sortOrder);
			} catch (SQLiteException e) {
				LogUtil.e("Catch a SQLiteException when query: ",
						e.getMessage());
				checkSQLiteException(context, e);
				return null;
			}
		}

		public static boolean requery(Context context, Cursor cursor) {
			try {
				return cursor.requery();
			} catch (SQLiteException e) {
				LogUtil.e("Catch a SQLiteException when requery: ",
						e.getMessage());
				checkSQLiteException(context, e);
				return false;
			}
		}

		public static int update(Context context, ContentResolver resolver,
				Uri uri, ContentValues values, String where,
				String[] selectionArgs) {
			try {
				return resolver.update(uri, values, where, selectionArgs);
			} catch (SQLiteException e) {
				LogUtil.e("Catch a SQLiteException when update: ",
						e.getMessage());
				checkSQLiteException(context, e);
				return -1;
			}
		}

		public static int delete(Context context, ContentResolver resolver,
				Uri uri, String where, String[] selectionArgs) {
			try {
				return resolver.delete(uri, where, selectionArgs);
			} catch (SQLiteException e) {
				LogUtil.e("Catch a SQLiteException when delete: ",
						e.getMessage());
				checkSQLiteException(context, e);
				return -1;
			}
		}

		public static Uri insert(Context context, ContentResolver resolver,
				Uri uri, ContentValues values) {
			try {
				return resolver.insert(uri, values);
			} catch (SQLiteException e) {
				LogUtil.e("Catch a SQLiteException when insert: ",
						e.getMessage());
				checkSQLiteException(context, e);
				return null;
			}
		}
	}

}
