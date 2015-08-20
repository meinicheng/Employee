package com.sdbnet.hywy.employee.db.old;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.employee.db.domin.OperateLog;
import com.sdbnet.hywy.employee.db.old.OperateLogDao;
import com.sdbnet.hywy.employee.net.AsyncHttpService;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.LogUtil;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.ToastUtil;
import com.sdbnet.hywy.employee.utils.UtilsAndroid;
import com.sdbnet.hywy.employee.utils.UtilsCommon;

public class OperateLogUtil {
	public static final String DELETE_ADDED_ORDER_X = "删除已添加订单：%s";
	public static final String ORDER_SCAN = "订单扫描";
	public static final String DELETE_ORDER_X = "删除订单：%s";
	// private static Dialog mLoadDialog;

	private static boolean isUploadLocalLog;

	public static synchronized void localLogUpload(final Context context) {
		Log.i("localLogUpload", "localLogUpload＝" + isUploadLocalLog);
		if (isUploadLocalLog) {
			return;
		}
		isUploadLocalLog = true;
		try {
			OperateLogDBHelper.initialize(context);
			final OperateLogDao operateLog = new OperateLogDaoImpl();
			List<OperateLog> list = operateLog
					.findAll(OperateLogDBHelper.dbHelper.getReadableDatabase());
			if (null == list || list.size() == 0) {
				ToastUtil.show(context, "opreate log ==null");
				isUploadLocalLog = false;
				return;
			}

			Log.d("localLogUpload", list.toString());

			StringEntity stringEntity = new StringEntity(list.toString(),
					"utf-8");

			AsyncHttpClient client = new AsyncHttpClient();
			client.post(context, AsyncHttpService.BASE_URL + "/upBatchOpeLog",
					stringEntity, "application/json",
					new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							super.onSuccess(statusCode, headers, response);
							LogUtil.d(response.toString());
							try {
								int errCode = response
										.getInt(Constants.Feild.KEY_ERROR_CODE);
								if (errCode == 0) {
									operateLog
											.deleteAll(OperateLogDBHelper.dbHelper
													.getReadableDatabase());
									ToastUtil.show(context,
											"opreate log upload success");
								} else {
									ToastUtil.show(context,
											"opreate log upload failed？");
								}
							} catch (JSONException e) {
								e.printStackTrace();
								ToastUtil.show(context,
										"opreate log upload failed？");
							} finally {
								isUploadLocalLog = false;

							}
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONObject errorResponse) {
							super.onFailure(statusCode, headers, throwable,
									errorResponse);
							ToastUtil
									.show(context, "opreate log upload failed");
							isUploadLocalLog = false;
						}

						@Override
						public void onCancel() {
							ToastUtil
									.show(context, "opreate log upload cancle");
							super.onCancel();
							isUploadLocalLog = false;
						}
					});
		} catch (Exception ex) {
			isUploadLocalLog = false;
			ex.printStackTrace();
		}
	}

	public static void saveOperate(Context context, String content) {
		saveOperate(context, content, null);
		// DBManager manager=new DBManager(context);
		// manager.saveOperate(context, content);
		// manager.closeDatabase();
	}

	public static void saveOperate(Context context, String content, Date date) {
		try {
			if (!UtilsCommon.checkAccount()) {
				return;
			}
			// if (TextUtils.isEmpty(PreferencesUtil.user_company)
			// || TextUtils.isEmpty(PreferencesUtil.item_id)
			// || TextUtils.isEmpty(PreferencesUtil.user_id)
			// || TextUtils.isEmpty(PreferencesUtil.user_tel)) {
			// return;
			// }
			OperateLogDBHelper.initialize(context);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (date == null) {
				date = new Date();
			}
			final OperateLogDao operateLog = new OperateLogDaoImpl();
			ContentValues values = new ContentValues();
			values.put("cmpid", PreferencesUtil.user_company);
			values.put("itemid", PreferencesUtil.item_id);
			values.put("pid", PreferencesUtil.user_id);
			values.put("loctel", PreferencesUtil.user_tel);
			values.put("opecont", content);
			values.put("opetime", sdf.format(date));
			values.put("isworking", PreferencesUtil.isworking);
			values.put("gpsstatus", UtilsAndroid.Set.getGpsStatus(context) ? 1
					: 0);
			values.put("gprsstatus",
					UtilsAndroid.Set.getGprsStatus(context) ? 1 : 0);
			values.put("wifistatus",
					UtilsAndroid.Set.getWifiStatus(context) ? 1 : 0);
			values.put("electricity", PreferencesUtil.battery);
			if (!operateLog.insert(
					OperateLogDBHelper.dbHelper.getReadableDatabase(), values)) {
				return;
			}
		} catch (Exception ex) {
			Log.i("upBatchOpeLog", ex.getLocalizedMessage());
			ex.printStackTrace();
		}
	}

	public static List<OperateLog> getAllOperateLog(Context context) {
		OperateLogDBHelper.initialize(context);
		OperateLogDao operateLog = new OperateLogDaoImpl();
		return operateLog.findAll(OperateLogDBHelper.dbHelper
				.getReadableDatabase());
	}

	public static void deleteAllOperateLog(Context context) {
		OperateLogDBHelper.initialize(context);
		OperateLogDao operateLog = new OperateLogDaoImpl();
		operateLog.deleteAll(OperateLogDBHelper.dbHelper.getReadableDatabase());
	}

	// public static void putLock() {
	//
	// if (!UtilsCommon.checkAccount()) {
	// return;
	// }
	//
	// OperateLogDBHelper.initialize(context);
	//
	// final OperateLogDao operateLog = new OperateLogDaoImpl();
	//
	// }

	// public static void putLock(Context context) {
	//
	// if (!UtilsCommon.checkAccount()) {
	// return;
	// }
	//
	// OperateLogDBHelper.initialize(context);
	// OperateLogDao operateLog = new OperateLogDaoImpl();
	// SQLiteDatabase db = OperateLogDBHelper.dbHelper.getReadableDatabase();
	//
	// if (db.isOpen()) {
	// String sql=""
	// db.rawQuery(sql, selectionArgs);
	//
	// db.close();
	// }
	//
	// }
}
