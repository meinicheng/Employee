package com.sdbnet.hywy.employee.db.old;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.baidu.mapapi.model.LatLng;
import com.sdbnet.hywy.employee.db.domin.LocateLog;
import com.sdbnet.hywy.employee.db.old.LocateLogDao;
import com.sdbnet.hywy.employee.location.MyLocation;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.UtilsAndroid;

public class LocateLogUtil {
	private static LocateLogUtil mLocateLogUtil;
	private static Context mContext;

	private LocateLogUtil() {
		// TODO Auto-generated constructor stub
	}

	public static LocateLogUtil getIntance(Context context) {
		if (mLocateLogUtil == null) {
			mLocateLogUtil = new LocateLogUtil();
		}
		mContext = context;
		return mLocateLogUtil;
	}

	public void saveLocateLog(MyLocation location) {
		// DBManager manager=new DBManager(mContext);
		// manager.saveLocateLog(location);
		// manager.closeDatabase();
		saveLocateLog(location.longitude, location.latitude, location.address);
	}

	public void saveLocateLog(LatLng latLng, String address) {
		saveLocateLog(latLng.longitude, latLng.latitude, address);
	}

	public void saveLocateLog(final double longitude, final double latitude,
			final String address) {
		if (TextUtils.isEmpty(PreferencesUtil.user_company)
				|| TextUtils.isEmpty(PreferencesUtil.item_id)
				|| TextUtils.isEmpty(PreferencesUtil.user_id)
				|| TextUtils.isEmpty(PreferencesUtil.user_tel)) {
			return;
		}
		LocateLogDao locateDao = new LocateLogDaoImpl();
		LocateLogDBHelper db = new LocateLogDBHelper(mContext);
		SQLiteDatabase sdb = db.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("cmpid", PreferencesUtil.user_company);
		values.put("itemid", PreferencesUtil.item_id);
		values.put("pid", PreferencesUtil.user_id);
		values.put("loctel", PreferencesUtil.user_tel);
		values.put("longitude", longitude);
		values.put("latitude", latitude);
		values.put("address", address);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		values.put("loctime", sdf.format(new Date()));
		values.put("isworking", PreferencesUtil.isworking);
		values.put("gpsstatus", UtilsAndroid.Set.getGpsStatus(mContext) ? 1 : 0);
		values.put("gprsstatus", UtilsAndroid.Set.getGprsStatus(mContext) ? 1
				: 0);
		values.put("wifistatus", UtilsAndroid.Set.getWifiStatus(mContext) ? 1
				: 0);
		values.put("electricity", PreferencesUtil.battery);

		locateDao.insert(sdb, values);
	}

	public void updateLocatelog(LocateLog locateLog) {
		if (TextUtils.isEmpty(PreferencesUtil.user_company)
				|| TextUtils.isEmpty(PreferencesUtil.item_id)
				|| TextUtils.isEmpty(PreferencesUtil.user_id)
				|| TextUtils.isEmpty(PreferencesUtil.user_tel)) {
			return;
		}
		LocateLogDao locateDao = new LocateLogDaoImpl();
		LocateLogDBHelper db = new LocateLogDBHelper(mContext);
		SQLiteDatabase sdb = db.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("cmpid", PreferencesUtil.user_company);
		values.put("itemid", PreferencesUtil.item_id);
		values.put("pid", PreferencesUtil.user_id);
		values.put("loctel", PreferencesUtil.user_tel);

		values.put("longitude", locateLog.getLongitude());
		values.put("latitude", locateLog.getLatitude());
		values.put("address", locateLog.getAddress());
		values.put("loctime", locateLog.getLoctime());

		values.put("isworking",locateLog.getIsworking());
		values.put("gpsstatus", locateLog.getGpsstatus());
		values.put("gprsstatus", locateLog.getGprsstatus());
		values.put("wifistatus", locateLog.getWifistatus());
		values.put("electricity", locateLog.getElectricity());
		
//		Log.i("updateLocatelog", locateLog.getId()+"");
		locateDao.update(sdb, values, Integer.parseInt(locateLog.getId()));// (sdb,
																			// values);
	}

	public List<LocateLog> findAllLocate() {

		List<LocateLog> locateLogs = new ArrayList<LocateLog>();
		LocateLogDBHelper.initialize(mContext);
		LocateLogDao locateLogd = new LocateLogDaoImpl();
		SQLiteDatabase db = LocateLogDBHelper.dbHelper.getReadableDatabase();

		if (db.isOpen()) {
			Cursor cursor = db.query("locate", new String[] { "id", "cmpid",
					"itemid", "pid", "loctel", "longitude", "latitude",
					"address", "loctime", "isworking", "gpsstatus",
					"gprsstatus", "wifistatus", "electricity" }, null, null,
					null, null, null);
			while (cursor.moveToNext()) {
				LocateLog locateLog = new LocateLog();
				locateLog.setId(cursor.getString(cursor.getColumnIndex("id")));
				locateLog.setCmpid(cursor.getString(cursor
						.getColumnIndex("cmpid")));
				locateLog.setItemid(cursor.getString(cursor
						.getColumnIndex("itemid")));
				locateLog
						.setPid(cursor.getString(cursor.getColumnIndex("pid")));
				locateLog.setLoctel(cursor.getString(cursor
						.getColumnIndex("loctel")));
				locateLog.setLongitude(cursor.getFloat(cursor
						.getColumnIndex("longitude")));
				locateLog.setLatitude(cursor.getFloat(cursor
						.getColumnIndex("latitude")));
				locateLog.setAddress(cursor.getString(cursor
						.getColumnIndex("address")));
				locateLog.setLoctime(cursor.getString(cursor
						.getColumnIndex("loctime")));
				locateLog.setIsworking(cursor.getInt(cursor
						.getColumnIndex("isworking")));
				locateLog.setGpsstatus(cursor.getInt(cursor
						.getColumnIndex("gpsstatus")));
				locateLog.setGprsstatus(cursor.getInt(cursor
						.getColumnIndex("gprsstatus")));
				locateLog.setWifistatus(cursor.getInt(cursor
						.getColumnIndex("wifistatus")));
				locateLog.setElectricity(cursor.getInt(cursor
						.getColumnIndex("electricity")));
				locateLogs.add(locateLog);
			}
			cursor.close();
		}
		return locateLogs;
	}

	public void deleteLocate(int id) {
		LocateLogDBHelper.initialize(mContext);
		LocateLogDao locateLog = new LocateLogDaoImpl();
		SQLiteDatabase db = LocateLogDBHelper.dbHelper.getReadableDatabase();
		locateLog.delete(db, id);
	}
	public void deleteAllLocate() {
		LocateLogDBHelper.initialize(mContext);
		LocateLogDao locateLog = new LocateLogDaoImpl();
		SQLiteDatabase db = LocateLogDBHelper.dbHelper.getReadableDatabase();
		locateLog.deleteAll(db);
	}
	public void deleteLocate(String sid) {
		int id;
		try {
			id = Integer.parseInt(sid);
		} catch (Exception e) {
			return;
		}
		LocateLogDBHelper.initialize(mContext);
		LocateLogDao locateLog = new LocateLogDaoImpl();
		SQLiteDatabase db = LocateLogDBHelper.dbHelper.getReadableDatabase();
		locateLog.delete(db, id);
	}
}
