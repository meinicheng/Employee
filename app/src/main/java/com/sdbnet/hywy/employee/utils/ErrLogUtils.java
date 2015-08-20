package com.sdbnet.hywy.employee.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.employee.location.MyLocation;
import com.sdbnet.hywy.employee.net.AsyncHttpService;
import com.umeng.analytics.MobclickAgent;

public class ErrLogUtils {
	private static final String TAG = "ErrLogUtils";

	/**
	 * 上传日志到友盟
	 * 
	 * @param context
	 * @param traceinfo
	 */
	public static boolean uploadErrLog(Context context, String traceinfo) {
		if (context == null) {
			return false;
		}
		String tel_status_gps = UtilsAndroid.Set.getGpsStatus(context) ? "1"
				: "0"; // 获取gps状态
		String tel_status_wifi = UtilsAndroid.Set.isWifiDataEnable(context) ? "1"
				: "0"; // 获取wifi状态
		String tel_status_net = UtilsAndroid.Set.isMobileDataEnable(context) ? "1"
				: "0"; // 获取流量状态

		StringBuilder sb = new StringBuilder();
		sb.append(traceinfo)
				.append("\n")
				.append("cmpid:")
				.append(PreferencesUtil.getValue(
						PreferencesUtil.KEY_COMPANY_ID, ""))
				.append(", itemid:")
				.append(PreferencesUtil.getValue(PreferencesUtil.KEY_ITEM_ID,
						""))
				.append(", pid:")
				.append(PreferencesUtil.getValue(PreferencesUtil.KEY_USER_ID,
						""))
				.append(", loctel:")
				.append(PreferencesUtil.getValue(PreferencesUtil.KEY_USER_TEL,
						""))
				.append(", op_step:")
				.append(PreferencesUtil.getValue(PreferencesUtil.KEY_STEPS, ""))
				.append(", tel_status_gps:").append(tel_status_gps)
				.append(", tel_status_wifi:").append(tel_status_wifi)
				.append(", tel_status_net:").append(tel_status_net);

		try {
			MobclickAgent.reportError(context, sb.toString());
		} catch (Exception e) {
			return false;
		}
		return true;

	}

	public static void uploadErrLog(Context context, Exception e) {
		String msg = toString(e);
		uploadErrLog(context, msg);
	}

	/**
	 * 上传日志到服务器
	 * 
	 * @param context
	 * @param traceinfo
	 */
	private static void uploadErrLog2(Context context, String traceinfo) {
		String tel_status_gps = UtilsAndroid.Set.getGpsStatus(context) ? "1"
				: "0";
		String tel_status_wifi = UtilsAndroid.Set.isWifiDataEnable(context) ? "1"
				: "0";
		String tel_status_net = UtilsAndroid.Set.isMobileDataEnable(context) ? "1"
				: "0";
		if (traceinfo.length() > 500) {
			traceinfo = traceinfo.substring(0, 499);
		}

		AsyncHttpService.addFeedBack(PreferencesUtil.getValue(
				PreferencesUtil.KEY_USER_TEL, ""), traceinfo,
				(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"))
						.format(new Date()), PreferencesUtil.getValue(
						PreferencesUtil.KEY_STEPS, ""), android.os.Build.BRAND,
				android.os.Build.MODEL, "android",
				android.os.Build.VERSION.RELEASE, tel_status_gps,
				tel_status_wifi, tel_status_net, new JsonHttpResponseHandler() {
					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							System.out.println("logupdate: "
									+ response.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, context);
	}

	/**
	 * 获取异常堆栈信息
	 * 
	 * @param e
	 * @return
	 */
	public static String toString(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		e.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}

	public static void uploadErrorCodeMsg(Context context, MyLocation location) {
		uploadErrorCodeMsg(context, location.longitude, location.latitude,
				location.address);
	}

	public static void uploadErrorCodeMsg(Context context, double longitude,
			double latitude, String address) {
		// 服务器传回错误码，将相关参数 日志上传
		PreferencesUtil
				.putValue(PreferencesUtil.KEY_STEPS, Constants.Step.LOCA);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("cmpid",
					PreferencesUtil.getValue(PreferencesUtil.KEY_COMPANY_ID));
			jsonObject.put("itemid",
					PreferencesUtil.getValue(PreferencesUtil.KEY_ITEM_ID));
			jsonObject.put("pid",
					PreferencesUtil.getValue(PreferencesUtil.KEY_USER_ID));
			jsonObject.put("tel",
					PreferencesUtil.getValue(PreferencesUtil.KEY_USER_TEL));
			jsonObject.put("cmpid",
					PreferencesUtil.getValue(PreferencesUtil.KEY_COMPANY_ID));

			jsonObject.put("longitude", longitude + "");
			jsonObject.put("latitude", latitude + "");
			jsonObject.put("address", address + "");
			LogUtil.e(TAG, jsonObject.toString());
			ErrLogUtils.uploadErrLog(context, jsonObject.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			LogUtil.e(TAG, jsonObject.toString());
			String jsonExce = ErrLogUtils.toString(e);
			ErrLogUtils.uploadErrLog(context, jsonExce
					+ ":\nJsonObject.toString=" + jsonObject + "");
		}
	}
}
