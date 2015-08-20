package com.sdbnet.hywy.employee.db.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sdbnet.hywy.employee.album.AlbumHelper;
import com.sdbnet.hywy.employee.album.AlbumHelper.ImageItem;
import com.sdbnet.hywy.employee.db.domin.LocateLog;
import com.sdbnet.hywy.employee.db.domin.OperateLog;
import com.sdbnet.hywy.employee.location.MyLocation;
import com.sdbnet.hywy.employee.model.ExecuteAction;
import com.sdbnet.hywy.employee.utils.LogUtil;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.UtilsAndroid;
import com.sdbnet.hywy.employee.utils.UtilsBean;
import com.sdbnet.hywy.employee.utils.UtilsCommon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class DBManager {
	private final static String TAG = "DBManager";
	private DBHelper mHelper;
	private SQLiteDatabase mDB;
	private Context mContext;

	public DBManager(Context context) {
		mHelper = new DBHelper(context);
		mContext = context;
		mDB = mHelper.getWritableDatabase();

	}

	public void closeDatabase() {
		mDB.close();
	}

	/**
	 * 插入记录
	 * 
	 * @param values
	 */

	public void insertOrder(ExecuteAction executeAction) {
		LogUtil.d(UtilsCommon.checkAccount() + ">>insertOrder<<"
				+ executeAction);
		if (!UtilsCommon.checkAccount()) {
			return;
		}
		String sql = "replace into " + DBCustomValue.TABLE_RECORD + "("
				+ DBCustomValue.COLUMN_COMPID + ","
				+ DBCustomValue.COLUMN_ITEMID + "," + DBCustomValue.COLUMN_TEL
				+ "," + DBCustomValue.COLUMN_ORDNOS + ","
				+ DBCustomValue.COLUMN_ACTION + ","
				+ DBCustomValue.COLUMN_ACTION_NAME + ","
				+ DBCustomValue.COLUMN_TIME + "," + DBCustomValue.COLUMN_PARAMS
				+ ") VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
		Object[] object = new Object[] {
				PreferencesUtil.getValue(PreferencesUtil.KEY_COMPANY_ID),
				PreferencesUtil.getValue(PreferencesUtil.KEY_ITEM_ID),
				PreferencesUtil.getValue(PreferencesUtil.KEY_USER_TEL),
				executeAction.getActidx(),
				executeAction.getAction(),
				executeAction.getActname(),
				(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"))
						.format(new Date()),
				UtilsBean.buildeParams(executeAction) };
		try {
			mDB.execSQL(sql, object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// mDB.close();
	}

	/**
	 * 删除记录
	 * 
	 * @param compid
	 * @param itemid
	 * @param ordnos
	 * @param action
	 */
	public void deleteOrder(String compid, String itemid, String ordnos,
			String action) {
		Log.i(TAG, "delete>>" + compid + "," + itemid + "," + ordnos + ","
				+ action);
		String sql = "delete from record where compid=? and itemid=? and ordnos=? and action=?";
		try {
			mDB.execSQL(sql, new Object[] { compid, itemid, ordnos, action });
		} catch (Exception e) {
			e.printStackTrace();
		}
		// mDB.close();

	}

	/**
	 * 删除所有记录
	 */
	public void deleteAllOrders() {
		try {
			mDB.execSQL("delete from record", new Object[] {});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// /**
	// * 查询记录
	// *
	// * @param columns
	// * @param whereStr
	// * @param whereArgs
	// * @return
	// */
	// public Cursor query(String[] columns, String whereStr, String[]
	// whereArgs) {
	// return mDB.query(DBCustomValue.TABLE_RECORD, columns, whereStr,
	// whereArgs, null, null, null);
	// }

	/**
	 * 获取所有记录数
	 * 
	 * @return
	 */
	public long getOrderCount() {
		LogUtil.d(UtilsCommon.checkAccount() + ">>getOrderCount");
		if (!UtilsCommon.checkAccount()) {
			return 0;
		}

		String sql = "select count(*) from record where compid = ? and itemid = ? and tel = ?";
		String[] selectionArgs = new String[] {
				PreferencesUtil.getValue(PreferencesUtil.KEY_COMPANY_ID),
				PreferencesUtil.getValue(PreferencesUtil.KEY_ITEM_ID),
				PreferencesUtil.getValue(PreferencesUtil.KEY_USER_TEL) };
		Cursor cursor = null;
		long count = 0;
		try {
			cursor = mDB.rawQuery(sql, selectionArgs);
			cursor.moveToFirst();
			count = cursor.getLong(0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}

		// mDB.close();
		return count;
	}

	/**
	 * 获取所有记录
	 * 
	 * @param compid
	 * @param itemid
	 * @param tel
	 * @return
	 */
	public List<ExecuteAction> getOrders() {
		return getRecords(
				PreferencesUtil.getValue(PreferencesUtil.KEY_COMPANY_ID),
				PreferencesUtil.getValue(PreferencesUtil.KEY_ITEM_ID),
				PreferencesUtil.getValue(PreferencesUtil.KEY_USER_TEL));
	}

	public List<ExecuteAction> getRecords(String compid, String itemid,
			String tel) {
		List<ExecuteAction> list = new ArrayList<ExecuteAction>();
		String sql = "select * from record where compid=? and itemid=? and tel=? order by stime desc";
		Cursor cursor = mDB.rawQuery(sql, new String[] { compid, itemid, tel });

		while (cursor.moveToNext()) {

			JSONObject json;
			try {
				String ordnos = cursor.getString(cursor
						.getColumnIndex(DBCustomValue.COLUMN_ORDNOS));
				String time = cursor.getString(cursor
						.getColumnIndex(DBCustomValue.COLUMN_TIME));
				String action_name = cursor.getString(cursor
						.getColumnIndex(DBCustomValue.COLUMN_ACTION_NAME));
				String params = cursor.getString(cursor
						.getColumnIndex(DBCustomValue.COLUMN_PARAMS));
				Log.e("getRecords", ordnos + "," + time + "," + action_name
						+ "," + params);

				json = new JSONObject(cursor.getString(cursor
						.getColumnIndex(DBCustomValue.COLUMN_PARAMS)));
				JSONObject jsonAct = json.getJSONObject("action");

				ExecuteAction action = UtilsBean.jsonToExecuteAction(jsonAct);
				action.setActidx(cursor.getString(cursor
						.getColumnIndex(DBCustomValue.COLUMN_ORDNOS)));
				action.setActTime(cursor.getString(cursor
						.getColumnIndex(DBCustomValue.COLUMN_TIME)));
				// action.setImsgs(json.getString("imgs"));
				Log.e(TAG, json.getString("imgs"));
				JSONArray jsonArray = json.getJSONArray("imgs");
				// JSONArray jsonArray = new JSONArray("imgs");
				ArrayList<ImageItem> imageItems = new ArrayList<AlbumHelper.ImageItem>();
				for (int i = 0; i < jsonArray.length(); i++) {
					ImageItem item = new ImageItem();
					JSONObject itemObject = jsonArray.getJSONObject(i);
					if (!itemObject.isNull("imageId"))
						item.imageId = itemObject.getString("imageId");
					if (!itemObject.isNull("thumbnailPath"))
						item.thumbnailPath = itemObject
								.getString("thumbnailPath");
					if (!itemObject.isNull("imagePath"))
						item.imagePath = itemObject.getString("imagePath");
					imageItems.add(item);
				}
				action.setImageItems(imageItems);

				list.add(action);
				// act
			} catch (JSONException e) {
				LogUtil.e(TAG, "get order data error");
				e.printStackTrace();
			}

		}
		cursor.close();
		// mDB.close();
		return list;
	}

	// public List<Map<String, String>> getRecords(String compid, String itemid,
	// String tel) {
	// List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	// SQLiteDatabase db = getReadableDatabase();
	// Cursor cursor = db
	// .rawQuery(
	// "select * from record where compid=? and itemid=? and tel=? order by stime desc",
	// new String[] { compid, itemid, tel });
	//
	// Map<String, String> map = null;
	// while (cursor.moveToNext()) {
	// map = new HashMap<String, String>();
	// map.put(DBHelper.COLUMN_ORDNOS, cursor.getString(cursor
	// .getColumnIndex(DBHelper.COLUMN_ORDNOS)));
	// map.put(DBHelper.COLUMN_TIME, cursor.getString(cursor
	// .getColumnIndex(DBHelper.COLUMN_TIME)));
	// map.put(DBHelper.COLUMN_ACTION_NAME, cursor.getString(cursor
	// .getColumnIndex(DBHelper.COLUMN_ACTION_NAME)));
	// map.put(DBHelper.COLUMN_PARAMS, cursor.getString(cursor
	// .getColumnIndex(DBHelper.COLUMN_PARAMS)));
	// list.add(map);
	// }
	// cursor.close();
	//
	// return list;
	// }

	// //////////////////////////////////////////////////////////////////////
	// City DATA
	// /**
	// * 得到所有支持的省份或直辖市名称的String类型数组
	// *
	// * @return 支持的省份或直辖市数组
	// */
	// public String[] getAllProvinces() {
	// String[] columns = { "name" };
	//
	// // SQLiteDatabase db = getReadableDatabase();
	// // 查询获得游标
	// Cursor cursor = mDB.query("provinces", columns, null, null, null, null,
	// null);
	// columns = null;
	// int count = cursor.getCount();
	// String[] provinces = new String[count];
	// count = 0;
	// while (!cursor.isLast()) {
	// cursor.moveToNext();
	// provinces[count] = cursor.getString(0);
	// count = count + 1;
	// }
	// cursor.close();
	// mDB.close();
	// return provinces;
	// }
	//
	// /**
	// * 根据省份数组来得到对应装有对应的城市名和城市编码的列表对象
	// *
	// * @param provinces
	// * 省份数组
	// * @return 索引0为对应的城市名的二维数组和索引1为对应城市名的二维数组
	// */
	// public List<String[][]> getAllCityAndCode(String[] provinces) {
	// int length = provinces.length;
	// String[][] city = new String[length][];
	// String[][] code = new String[length][];
	// int count = 0;
	// // SQLiteDatabase mDB = getReadableDatabase();
	// for (int i = 0; i < length; i++) {
	// Cursor cursor = mDB.query("citys", new String[] { "name",
	// "city_num" }, "province_id = ? ",
	// new String[] { String.valueOf(i) }, null, null, null);
	// count = cursor.getCount();
	// city[i] = new String[count];
	// code[i] = new String[count];
	// count = 0;
	// while (!cursor.isLast()) {
	// cursor.moveToNext();
	// city[i][count] = cursor.getString(0);
	// code[i][count] = cursor.getString(1);
	// count = count + 1;
	// }
	// cursor.close();
	// }
	// mDB.close();
	// List<String[][]> result = new ArrayList<String[][]>();
	// result.add(city);
	// result.add(code);
	// return result;
	// }
	//
	// /**
	// * 由城市名查询数据库来得到城市码
	// *
	// * @param cityName
	// * 城市名
	// * @return 城市码
	// */
	// public static final String DB_WEATHER="db_weather.db";
	// public String getCityCodeByName(String cityName) {
	// System.out.println("cityName:" + cityName);
	// SQLiteDatabase mDB = getReadableDatabase();
	// Cursor cursor = mDB.query("citys", new String[] { "city_num" },
	// "name = ? ", new String[] { cityName }, null, null, null);
	// String cityCode = null;
	// if (!cursor.isLast()) {
	// cursor.moveToNext();
	// cityCode = cursor.getString(0);
	// }
	// cursor.close();
	// mDB.close();
	// return cityCode;
	// }

	/**
	 * 删除记录
	 * 
	 * @param compid
	 * @param itemid
	 * @param ordnos
	 * @param action
	 */
	public void deleteOrder(String ordnos, String action) {
		deleteOrder(PreferencesUtil.getValue(PreferencesUtil.KEY_COMPANY_ID),
				PreferencesUtil.getValue(PreferencesUtil.KEY_ITEM_ID), ordnos,
				action);
	}

	// public List<Map<String, String>> getRecords(String compid, String itemid,
	// String tel) {
	// List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	// SQLiteDatabase db = getReadableDatabase();
	// Cursor cursor = db
	// .rawQuery(
	// "select * from record where compid=? and itemid=? and tel=? order by stime desc",
	// new String[] { compid, itemid, tel });
	//
	//
	// Map<String, String> map = null;
	// while (cursor.moveToNext()) {
	// map = new HashMap<String, String>();
	// map.put(DBHelper.COLUMN_ORDNOS, cursor.getString(cursor
	// .getColumnIndex(DBHelper.COLUMN_ORDNOS)));
	// map.put(DBHelper.COLUMN_TIME, cursor.getString(cursor
	// .getColumnIndex(DBHelper.COLUMN_TIME)));
	// map.put(DBHelper.COLUMN_ACTION_NAME, cursor.getString(cursor
	// .getColumnIndex(DBHelper.COLUMN_ACTION_NAME)));
	// map.put(DBHelper.COLUMN_PARAMS, cursor.getString(cursor
	// .getColumnIndex(DBHelper.COLUMN_PARAMS)));
	// list.add(map);
	// }
	// cursor.close();
	//
	// return list;
	// }
	// /////////////////////////////////////

	public boolean deleteLocateById(String id) {
		Log.v(TAG, "delete locate log id =" + id);
		boolean flag = false;
		if (mDB.isOpen()) {
			try {
				long l = mDB.delete("locate", "id = ?",
						new String[] { id + "" });
				if (l > 0) {
					flag = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return flag;
	}

	public boolean deleteLocateById(Integer id) {
		return deleteLocateById(id + "");
	}

	public void deleteAllLocate() {
		try {
			String sql = "delete from locate ";
			mDB.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// private List<LocateLog> getLocateByCursor(Cursor cursor) {
	// List<LocateLog> locateLogs = new ArrayList<LocateLog>();
	// while (cursor.moveToNext()) {
	// LocateLog locateLog = new LocateLog();
	// locateLog.setId(cursor.getString(cursor.getColumnIndex("id")));
	// locateLog
	// .setCmpid(cursor.getString(cursor.getColumnIndex("cmpid")));
	// locateLog.setItemid(cursor.getString(cursor
	// .getColumnIndex("itemid")));
	// locateLog.setPid(cursor.getString(cursor.getColumnIndex("pid")));
	// locateLog.setLoctel(cursor.getString(cursor
	// .getColumnIndex("loctel")));
	// locateLog.setLongitude(cursor.getFloat(cursor
	// .getColumnIndex("longitude")));
	// locateLog.setLatitude(cursor.getFloat(cursor
	// .getColumnIndex("latitude")));
	// locateLog.setAddress(cursor.getString(cursor
	// .getColumnIndex("address")));
	// locateLog.setLoctime(cursor.getString(cursor
	// .getColumnIndex("loctime")));
	// locateLog.setIsworking(cursor.getInt(cursor
	// .getColumnIndex("isworking")));
	// locateLog.setGpsstatus(cursor.getInt(cursor
	// .getColumnIndex("gpsstatus")));
	// locateLog.setGprsstatus(cursor.getInt(cursor
	// .getColumnIndex("gprsstatus")));
	// locateLog.setWifistatus(cursor.getInt(cursor
	// .getColumnIndex("wifistatus")));
	// locateLog.setElectricity(cursor.getInt(cursor
	// .getColumnIndex("electricity")));
	// locateLogs.add(locateLog);
	// }
	// // cursor.close();
	// return locateLogs;
	// }
	//
	// //
	// public List<LocateLog> getLocateNull() {
	// List<LocateLog> locateLogs = new ArrayList<LocateLog>();
	// if (mDB.isOpen()) {
	// String sql = "select * from " + DBCustomValue.TABLE_LOCATE
	// + "where address is  null ";
	// Cursor cursor = null;
	// try {
	// cursor = mDB.rawQuery(sql, null);
	// locateLogs = getLocateByCursor(cursor);
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// if (cursor != null)
	// cursor.close();
	// }
	// }
	// return locateLogs;
	// }
	//
	// //
	// public List<LocateLog> getLocateNoNull() {
	// List<LocateLog> locateLogs = new ArrayList<LocateLog>();
	// if (mDB.isOpen()) {
	// String sql = "select * from " + DBCustomValue.TABLE_LOCATE
	// + "where address is not null and datalength(address)<>0";
	//
	// Cursor cursor = null;
	// try {
	//
	// // cursor = mDB.rawQuery(sql, null);
	// cursor = mDB.query(DBCustomValue.TABLE_LOCATE, null, null,
	// null, null, null, null);// 查询并获得游标
	// locateLogs = getLocateByCursor(cursor);
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// if (cursor != null)
	// cursor.close();
	// }
	// }
	// return locateLogs;
	// }

	public List<LocateLog> getAllLocate() {
		List<LocateLog> locateLogs = new ArrayList<LocateLog>();
		if (mDB.isOpen()) {

			Cursor cursor = mDB.query("locate", new String[] { "id", "cmpid",
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

	public void saveLocateLog(MyLocation location) {
		saveLocateLog(location.longitude, location.latitude, location.address);
	}

	public void saveLocateLog(double longitude, double latitude, String address) {
		if (!UtilsCommon.checkAccount())
			return;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(new Date());

		String sql = "insert  into  " + DBCustomValue.TABLE_LOCATE + "("
				+ "cmpid" + "," + "itemid" + "," + "pid" + "," + "loctel" + ","
				+ "longitude" + "," + "latitude" + "," + "address" + ","
				+ "loctime" + "," + "isworking" + "," + "gpsstatus" + ","
				+ "gprsstatus" + "," + "wifistatus" + "," + "electricity"
				+ ") VALUES (?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?);";
		Object[] object = new Object[] {
				PreferencesUtil.getValue(PreferencesUtil.KEY_COMPANY_ID),
				PreferencesUtil.getValue(PreferencesUtil.KEY_ITEM_ID),
				PreferencesUtil.getValue(PreferencesUtil.KEY_USER_ID),
				PreferencesUtil.getValue(PreferencesUtil.KEY_USER_TEL),
				longitude, latitude, address, date,
				PreferencesUtil.getValue(PreferencesUtil.KEY_IS_WORKING, 1),
				UtilsAndroid.Set.getGpsStatus(mContext) ? 1 : 0,
				UtilsAndroid.Set.getGprsStatus(mContext) ? 1 : 0,
				UtilsAndroid.Set.getWifiStatus(mContext) ? 1 : 0,
				PreferencesUtil.getValue(PreferencesUtil.KEY_BATTERY, 100) };
		try {
			LogUtil.d("saveLocateLog=" + object + "\t" + longitude + ","
					+ latitude + "," + address + "," + date);
			mDB.execSQL(sql, object);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveLocateLog(LocateLog locateLog) {
		if (!UtilsCommon.checkLocateLog(locateLog))
			return;

		Log.i(TAG, "save locate log");
		String sql = "insert  into  " + DBCustomValue.TABLE_LOCATE + "("
				+ "cmpid" + "," + "itemid" + "," + "pid" + "," + "loctel" + ","
				+ "longitude" + "," + "latitude" + "," + "address" + ","
				+ "loctime" + "," + "isworking" + "," + "gpsstatus" + ","
				+ "gprsstatus" + "," + "wifistatus" + "," + "electricity"
				+ ") VALUES (?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?);";
		Object[] object = new Object[] { locateLog.getCmpid(),
				locateLog.getItemid(), locateLog.getPid(),
				locateLog.getLoctel(), locateLog.getLongitude(),
				locateLog.getLatitude(), locateLog.getAddress(),
				locateLog.getLoctime(), locateLog.getIsworking(),
				locateLog.getGpsstatus(), locateLog.getGprsstatus(),
				locateLog.getWifistatus(), locateLog.getElectricity() };
		try {
			LogUtil.d("saveLocateLog=" + sql);
			mDB.execSQL(sql, object);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateLocatelog(LocateLog locateLog) {
		Log.d(TAG, locateLog.getId() + ">>" + locateLog.toString());
		if (!UtilsCommon.checkAccount()) {
			return;
		}
		ContentValues values = new ContentValues();
		values.put("cmpid", PreferencesUtil.user_company);
		values.put("itemid", PreferencesUtil.item_id);
		values.put("pid", PreferencesUtil.user_id);
		values.put("loctel", PreferencesUtil.user_tel);

		values.put("longitude", locateLog.getLongitude());
		values.put("latitude", locateLog.getLatitude());
		values.put("address", locateLog.getAddress());
		values.put("loctime", locateLog.getLoctime());

		values.put("isworking", locateLog.getIsworking());
		values.put("gpsstatus", locateLog.getGpsstatus());
		values.put("gprsstatus", locateLog.getGprsstatus());
		values.put("wifistatus", locateLog.getWifistatus());
		values.put("electricity", locateLog.getElectricity());

		// Log.i("updateLocatelog", locateLog.getId()+"");
		try {
			mDB.update(DBCustomValue.TABLE_LOCATE, values, " id= ? ",
					new String[] { Integer.parseInt(locateLog.getId()) + "" });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// /////////////////
	public void saveOperate(String content) {
		saveOperate(content, null);
	}

	public void saveOperate(String content, Date date) {
		Log.i(TAG, "saveOperate=" + content);
		if (!UtilsCommon.checkAccount() || TextUtils.isEmpty(content)) {
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (date == null) {
			date = new Date();
		}

		String sql = "insert  into  " + DBCustomValue.TABLE_OPERATE + "("
				+ "cmpid" + "," + "itemid" + "," + "pid" + "," + "loctel" + ","
				+ "opecont" + "," + "opetime" + "," + "isworking" + ","
				+ "gpsstatus" + "," + "gprsstatus" + "," + "wifistatus" + ","
				+ "electricity" + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?);";

		Object[] object = new Object[] {
				PreferencesUtil.getValue(PreferencesUtil.KEY_COMPANY_ID),
				PreferencesUtil.getValue(PreferencesUtil.KEY_ITEM_ID),
				PreferencesUtil.getValue(PreferencesUtil.KEY_USER_ID),
				PreferencesUtil.getValue(PreferencesUtil.KEY_USER_TEL),
				content, sdf.format(date),
				PreferencesUtil.getValue(PreferencesUtil.KEY_IS_WORKING, 1),
				UtilsAndroid.Set.getGpsStatus(mContext) ? 1 : 0,
				UtilsAndroid.Set.getGprsStatus(mContext) ? 1 : 0,
				UtilsAndroid.Set.getWifiStatus(mContext) ? 1 : 0,
				PreferencesUtil.getValue(PreferencesUtil.KEY_BATTERY, 100) };
		try {
			// LogUtil.d("saveOperate=" + sql);
			mDB.execSQL(sql, object);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void saveOperate(OperateLog log) {
		// Log.i(TAG, "save opreate log");
		LogUtil.d("saveOperate=" + log);
		String sql = "insert  into  " + DBCustomValue.TABLE_OPERATE + "("
				+ "cmpid" + "," + "itemid" + "," + "pid" + "," + "loctel" + ","
				+ "opecont" + "," + "opetime" + "," + "isworking" + ","
				+ "gpsstatus" + "," + "gprsstatus" + "," + "wifistatus" + ","
				+ "electricity" + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?);";

		Object[] object = new Object[] { log.getCmpid(), log.getItemid(),
				log.getPid(), log.getLoctel(), log.getOpecont(),
				log.getOpetime(), log.getIsworking(), log.getGpsstatus(),
				log.getGprsstatus(), log.getWifistatus(), log.getElectricity() };
		try {
			// LogUtil.d("saveOperate=" + sql);
			mDB.execSQL(sql, object);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void putLockOpreate() {
		if (mDB.isOpen()) {
			String sql = "UPDATE  " + DBCustomValue.TABLE_OPERATE
					+ " SET lock=1 ";
			try {
				mDB.execSQL(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void clearLockOpreate() {
		if (mDB.isOpen()) {
			String sql = "UPDATE  " + DBCustomValue.TABLE_OPERATE
					+ " SET lock=0 ";
			try {
				mDB.execSQL(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public List<OperateLog> getAllOpreate() {
		List<OperateLog> locateLogs = new ArrayList<OperateLog>();
		if (mDB.isOpen()) {
			Cursor cursor = mDB.query(DBCustomValue.TABLE_OPERATE,
					new String[] { "id", "cmpid", "itemid", "pid", "loctel",
							"opecont", "opetime", "isworking", "gpsstatus",
							"gprsstatus", "wifistatus", "electricity" },
					"lock=?", new String[] { "0" }, null, null, null);
			while (cursor.moveToNext()) {
				OperateLog operateLog = new OperateLog();
				operateLog.setId(cursor.getString(cursor.getColumnIndex("id")));
				operateLog.setCmpid(cursor.getString(cursor
						.getColumnIndex("cmpid")));
				operateLog.setItemid(cursor.getString(cursor
						.getColumnIndex("itemid")));
				operateLog
						.setPid(cursor.getString(cursor.getColumnIndex("pid")));
				operateLog.setLoctel(cursor.getString(cursor
						.getColumnIndex("loctel")));
				operateLog.setOpecont(cursor.getString(cursor
						.getColumnIndex("opecont")));
				operateLog.setOpetime(cursor.getString(cursor
						.getColumnIndex("opetime")));
				operateLog.setIsworking(cursor.getInt(cursor
						.getColumnIndex("isworking")));
				operateLog.setGpsstatus(cursor.getInt(cursor
						.getColumnIndex("gpsstatus")));
				operateLog.setGprsstatus(cursor.getInt(cursor
						.getColumnIndex("gprsstatus")));
				operateLog.setWifistatus(cursor.getInt(cursor
						.getColumnIndex("wifistatus")));
				operateLog.setElectricity(cursor.getInt(cursor
						.getColumnIndex("electricity")));
				locateLogs.add(operateLog);
			}
			cursor.close();
		}
		return locateLogs;
	}

	public void deleteAllOpreate() {
		try {
			String sql = "delete from " + DBCustomValue.TABLE_OPERATE;
			mDB.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
