package com.sdbnet.hywy.employee.utils;

import com.sdbnet.hywy.employee.MainApplication;

import android.text.TextUtils;
import android.util.Log;

/**
 * LogUtils工具说明: 1 只输出等级大于等于LEVEL的日志 所以在开发和产品发布后通过修改LEVEL来选择性输出日志.
 * 当LEVEL=NOTHING则屏蔽了所有的日志. 2 v,d,i,w,e均对应两个方法. 若不设置TAG或者TAG为空则为设置默认TAG
 * 
 */
public class LogUtil {

	public static final int VERBOSE = 1;
	public static final int DEBUG = 2;
	public static final int INFO = 3;
	public static final int WARN = 4;
	public static final int ERROR = 5;
	public static final int NOTHING = 6;
	public static final int LEVEL = VERBOSE;
	public static final String SEPARATOR = ",";

	public static boolean isDebug = false;

	public static void openLog() {
		isDebug = MainApplication.DEVELOPER_MODE;
		String tel = PreferencesUtil.getValue(PreferencesUtil.KEY_USER_TEL);

		if (MainApplication.mAccounts.contains(tel)) {
			isDebug = true;
		}

	}

	public static void v(String message) {
		if (!isDebug) {
			return;
		}
		if (LEVEL <= VERBOSE) {
			StackTraceElement stackTraceElement = Thread.currentThread()
					.getStackTrace()[3];
			String tag = getDefaultTag(stackTraceElement);
			Log.v(tag, getLogInfo(stackTraceElement) + message);
		}
	}

	public static void v(String tag, String message) {
		if (!isDebug) {
			return;
		}
		if (LEVEL <= VERBOSE) {
			StackTraceElement stackTraceElement = Thread.currentThread()
					.getStackTrace()[3];
			if (TextUtils.isEmpty(tag)) {
				tag = getDefaultTag(stackTraceElement);
			}
			Log.v(tag, getLogInfo(stackTraceElement) + message);
		}
	}

	public static void d(String message) {
		if (!isDebug) {
			return;
		}
		if (LEVEL <= DEBUG) {
			StackTraceElement stackTraceElement = Thread.currentThread()
					.getStackTrace()[3];
			String tag = getDefaultTag(stackTraceElement);
			Log.d(tag, getLogInfo(stackTraceElement) + message);
		}
	}

	public static void d(String tag, String message) {
		if (!isDebug) {
			return;
		}
		if (LEVEL <= DEBUG) {
			StackTraceElement stackTraceElement = Thread.currentThread()
					.getStackTrace()[3];
			if (TextUtils.isEmpty(tag)) {
				tag = getDefaultTag(stackTraceElement);
			}
			Log.d(tag, getLogInfo(stackTraceElement) + message);
		}
	}

	public static void i(String message) {
		if (!isDebug) {
			return;
		}
		if (LEVEL <= INFO) {
			StackTraceElement stackTraceElement = Thread.currentThread()
					.getStackTrace()[3];
			String tag = getDefaultTag(stackTraceElement);
			Log.i(tag, getLogInfo(stackTraceElement) + message);
		}
	}

	public static void i(String tag, String message) {
		if (!isDebug) {
			return;
		}
		if (LEVEL <= INFO) {
			StackTraceElement stackTraceElement = Thread.currentThread()
					.getStackTrace()[3];
			if (TextUtils.isEmpty(tag)) {
				tag = getDefaultTag(stackTraceElement);
			}
			Log.i(tag, getLogInfo(stackTraceElement) + message);
		}
	}

	public static void w(String message) {
		if (!isDebug) {
			return;
		}
		if (LEVEL <= WARN) {
			StackTraceElement stackTraceElement = Thread.currentThread()
					.getStackTrace()[3];
			String tag = getDefaultTag(stackTraceElement);
			Log.w(tag, getLogInfo(stackTraceElement) + message);
		}
	}

	public static void w(String tag, String message) {
		if (!isDebug) {
			return;
		}
		if (LEVEL <= WARN) {
			StackTraceElement stackTraceElement = Thread.currentThread()
					.getStackTrace()[3];
			if (TextUtils.isEmpty(tag)) {
				tag = getDefaultTag(stackTraceElement);
			}
			Log.w(tag, getLogInfo(stackTraceElement) + message);
		}
	}

	public static void e(String tag, String message) {
		if (!isDebug) {
			return;
		}
		if (LEVEL <= ERROR) {
			StackTraceElement stackTraceElement = Thread.currentThread()
					.getStackTrace()[3];
			if (TextUtils.isEmpty(tag)) {
				tag = getDefaultTag(stackTraceElement);
			}
			Log.e(tag, getLogInfo(stackTraceElement) + message);
		}
	}

	/**
	 * 获取默认的TAG名称. 比如在MainActivity.java中调用了日志输出. 则TAG为MainActivity
	 */
	public static String getDefaultTag(StackTraceElement stackTraceElement) {
		String fileName = stackTraceElement.getFileName();
		String stringArray[] = fileName.split("\\.");
		String tag = stringArray[0];
		return tag;
	}

	/**
	 * 输出日志所包含的信息
	 */
	public static String getLogInfo(StackTraceElement stackTraceElement) {

		StringBuilder logInfoStringBuilder = new StringBuilder();
		// 获取线程名
		String threadName = Thread.currentThread().getName();
		// 获取线程ID
		long threadID = Thread.currentThread().getId();
		// 获取文件名.即xxx.java
		String fileName = stackTraceElement.getFileName();
		// 获取类名.即包名+类名
		String className = stackTraceElement.getClassName();
		// 获取方法名称
		String methodName = stackTraceElement.getMethodName();
		// 获取生日输出行数
		int lineNumber = stackTraceElement.getLineNumber();

		logInfoStringBuilder.append("[ ");
		logInfoStringBuilder.append("threadID=" + threadID).append(SEPARATOR);
		logInfoStringBuilder.append("threadName=" + threadName).append(
				SEPARATOR);
		logInfoStringBuilder.append("fileName=" + fileName).append(SEPARATOR);
		logInfoStringBuilder.append("className=" + className).append(SEPARATOR);
		logInfoStringBuilder.append("methodName=" + methodName).append(
				SEPARATOR);
		logInfoStringBuilder.append("lineNumber=" + lineNumber);
		logInfoStringBuilder.append(" ] ");
		// return logInfoStringBuilder.toString() + "\n";
		return logInfoStringBuilder.toString();
	}

}
