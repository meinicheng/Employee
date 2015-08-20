package com.sdbnet.hywy.employee.db.old;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sdbnet.hywy.employee.db.domin.LocateLog;
import com.sdbnet.hywy.employee.db.old.LocateLogDao;

public class LocateLogDaoImpl implements LocateLogDao {

	@Override
	public boolean insert(SQLiteDatabase db, ContentValues values) {
		boolean flag = false;
		if (db.isOpen()) {
			long l = db.insert("locate", null, values);
			/**
			 * table 表名 nullColumnHack: insert into
			 * LocateLog(name,phone)values('chenhj','123'); //insert into
			 * LocateLog(name) values(null); //insert into LocateLog() values();
			 * values 参数
			 * 
			 */
			if (l > 0) {
				flag = true;
			}
			db.close();
		}
		return flag;
	}

	@Override
	public boolean update(SQLiteDatabase db, ContentValues values, Integer id) {
		boolean flag = false;
		if (db.isOpen()) {
			long l = db.update("locate", values, "id = ?", new String[] { id
					+ "" });
			if (l > 0) {
				flag = true;
			}
			db.close();
		}
		return flag;
	}

	@Override
	public boolean delete(SQLiteDatabase db, Integer id) {
		boolean flag = false;
		if (db.isOpen()) {
			long l = db.delete("locate", "id = ?", new String[] { id + "" });
			if (l > 0) {
				flag = true;
			}
		}
		return flag;
	}

	@Override
	public boolean deleteAll(SQLiteDatabase db) {
		boolean flag = false;
		if (db.isOpen()) {
			long l = db.delete("locate", null, null);
			if (l > 0) {
				flag = true;
			}
		}
		return flag;
	}

	@Override
	public List<LocateLog> findAll(SQLiteDatabase db) {
		List<LocateLog> locateLogs = new ArrayList<LocateLog>();
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

	// @Override
	// public List<LocateLog> findByName(SQLiteDatabase db, String[]
	// selectionArgs) {
	// List<LocateLog> LocateLogs = new ArrayList<LocateLog>();
	// if (db.isOpen()) {
	// // 执行查询
	// Cursor cursor = db.query("locate", new String[] { "id", "name",
	// "age" }, "name like ?", selectionArgs, null, null,
	// "id desc");
	// while (cursor.moveToNext()) {
	// // 创建LocateLog对象
	// LocateLog LocateLog = new LocateLog();
	// // 设置LocateLog的属性
	// LocateLog.setId(cursor.getInt(cursor.getColumnIndex("id")));
	// LocateLog.setName(cursor.getString(cursor.getColumnIndex("name")));
	// LocateLog.setAge(cursor.getInt(cursor.getColumnIndex("age")));
	// // 添加到集合众
	// LocateLogs.add(LocateLog);
	// }
	// }
	// return LocateLogs;
	// }

	// @Override
	// public List<LocateLog> getNowPageInfo(SQLiteDatabase db,
	// String[] selectionArgs, String order, String limit) {
	// List<LocateLog> LocateLogs = new ArrayList<LocateLog>();
	// if (db.isOpen()) {
	// // 执行查询
	// Cursor cursor = db.query("locate", new String[] { "id", "name",
	// "age" }, "name like ?", selectionArgs, null, null, order,
	// limit);
	// while (cursor.moveToNext()) {
	// // 创建LocateLog对象
	// LocateLog LocateLog = new LocateLog();
	// // 设置LocateLog的属性
	// LocateLog.setId(cursor.getInt(cursor.getColumnIndex("id")));
	// LocateLog.setName(cursor.getString(cursor.getColumnIndex("name")));
	// LocateLog.setAge(cursor.getInt(cursor.getColumnIndex("age")));
	// // 添加到集合众
	// LocateLogs.add(LocateLog);
	// }
	// }
	// return LocateLogs;
	// }

}
