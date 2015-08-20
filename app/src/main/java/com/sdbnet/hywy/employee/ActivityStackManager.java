package com.sdbnet.hywy.employee;

import java.util.Stack;

import android.app.Activity;
import android.util.Log;

/*
 * @描述 自定义Activity管理器
 * @author hubenwei
 */

public class ActivityStackManager {

	private final static String TAG = "ActivityStackManager";
	private static Stack<Activity> mActivityStack;
	private static ActivityStackManager mInstance;

	/**
	 * @描述 获取栈管理工具
	 * @return ActivityManager
	 */
	public static ActivityStackManager getStackManager() {
		if (mInstance == null) {
			mInstance = new ActivityStackManager();
		}
		if (mActivityStack == null) {
			mActivityStack = new Stack<Activity>();
		}
		return mInstance;
	}

	/**
	 * 推出栈顶Activity
	 */
	public void popActivity(Activity activity) {
		if (activity != null) {
			Log.d(TAG, "Activity=" + activity.getClass().getName() + "出栈");
			activity.finish();
			mActivityStack.remove(activity);
			activity = null;
		}
	}

	// public void popActivityClass(Class<?> activity) {
	// if (activity != null) {
	// Log.d(TAG, "Activity=" + activity.getClass().getName() + "出栈");
	// // activity.finish();
	// try {
	// // Class<?> managerClass =
	// // Class.forName("android.telephony.TelephonyManager");
	// Method methodDefault = activity.getMethod("test");
	// Object manager = methodDefault.invoke(activity);
	// mActivityStack.remove(activity);
	// } catch (Exception e) {
	// e.printStackTrace();
	//
	// }
	//
	// activity = null;
	// }
	// }

	/**
	 * 获得当前栈顶Activity
	 */
	public Activity currentActivity() {
		// lastElement()获取最后个子元素，这里是栈顶的Activity
		if (mActivityStack == null || mActivityStack.size() == 0) {
			return null;
		}
		Activity activity = mActivityStack.lastElement();
		return activity;
	}

	/**
	 * 将当前Activity推入栈中
	 */
	public void pushActivity(Activity activity) {
		if (mActivityStack == null) {
			mActivityStack = new Stack<Activity>();
		}
		mActivityStack.add(activity);

		Log.d(TAG, "Activity=" + activity.getClass().getName() + "入栈");
	}

	/**
	 * 弹出指定的clsss所在栈顶部的中所有Activity
	 * 
	 * @clsss : 指定的类
	 */
	public void popTopActivitys(Class<?> clsss) {
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
			if (activity.getClass().equals(clsss)) {
				break;
			}
			popActivity(activity);
		}
	}

	/**
	 * 弹出栈中所有Activity
	 */
	public void popAllActivitys() {
		while (true) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
			popActivity(activity);
		}
	}

	public void removeActivity(Activity activity) {
		if (activity != null) {
			mActivityStack.remove(activity);
		}
	}

	public void verseAllActivity() {
		Log.d(TAG, "遍历Activity栈开始");
		for (Activity x : mActivityStack) {
			Log.d(TAG, "Activity=" + x.getClass().getName());
		}
		Log.d(TAG, "遍历Activity栈结束");
	}

	public int getActivityCount() {
		int count = 0;
		if (mActivityStack != null) {
			for (int i = 0; i < mActivityStack.size(); i++) {
				if (mActivityStack.get(i) != null) {
					count++;
				} else {
					mActivityStack.remove(mActivityStack.get(i));
				}
			}
		}
		return count;
	}
}
