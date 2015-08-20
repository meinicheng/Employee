package com.sdbnet.hywy.employee.db.old;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sdbnet.hywy.employee.db.domin.OperateLog;
import com.sdbnet.hywy.employee.db.old.OperateLogDao;

public class OperateLogDaoImpl implements OperateLogDao {

	@Override
	public boolean insert(SQLiteDatabase db, ContentValues values) {
		boolean flag = false;
		if (db.isOpen()) {
			long l = db.insert("operate", null, values);
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
			long l = db.update("operate", values, "id = ?", new String[] { id
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
			long l = db.delete("operate", "id = ?", new String[] { id + "" });
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
			long l = db.delete("operate", null, null);
			if (l > 0) {
				flag = true;
			}
		}
		return flag;
	}

	@Override
	public List<OperateLog> findAll(SQLiteDatabase db) {
		List<OperateLog> locateLogs = new ArrayList<OperateLog>();
		if (db.isOpen()) {
			Cursor cursor = db.query("operate", new String[] { "id", "cmpid",
					"itemid", "pid", "loctel", "opecont", "opetime",
					"isworking", "gpsstatus", "gprsstatus", "wifistatus",
					"electricity" },null,null , null, null, null);
			while (cursor.moveToNext()) {
				// Map operateLog = new HashMap();
				// operateLog.put("cmpid",
				// cursor.getString(cursor.getColumnIndex("cmpid")));
				// operateLog.put("itemid",
				// cursor.getString(cursor.getColumnIndex("itemid")));
				// operateLog.put("pid",
				// cursor.getString(cursor.getColumnIndex("pid")));
				// operateLog.put("loctel",
				// cursor.getString(cursor.getColumnIndex("loctel")));
				// operateLog.put("isworking",
				// cursor.getString(cursor.getColumnIndex("opecont")));
				// operateLog.put("isworking",
				// cursor.getString(cursor.getColumnIndex("opetime")));
				// operateLog.put("isworking",
				// cursor.getInt(cursor.getColumnIndex("isworking")));
				// operateLog.put("gpsstatus",
				// cursor.getInt(cursor.getColumnIndex("gpsstatus")));
				// operateLog.put("gprsstatus",
				// cursor.getInt(cursor.getColumnIndex("gprsstatus")));
				// operateLog.put("wifistatus",
				// cursor.getInt(cursor.getColumnIndex("wifistatus")));
				// operateLog.put("electricity",
				// cursor.getInt(cursor.getColumnIndex("electricity")));
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

	// @Override
	// public List<LocateLog> findByName(SQLiteDatabase db, String[]
	// selectionArgs) {
	// List<LocateLog> LocateLogs = new ArrayList<LocateLog>();
	// if (db.isOpen()) {
	// // 执行查询
	// Cursor cursor = db.query("operate", new String[] { "id", "name",
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
	// Cursor cursor = db.query("operate", new String[] { "id", "name",
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
