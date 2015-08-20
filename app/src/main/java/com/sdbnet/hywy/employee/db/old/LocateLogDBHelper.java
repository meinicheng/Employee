package com.sdbnet.hywy.employee.db.old;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocateLogDBHelper extends SQLiteOpenHelper {

	private static final String tag = "DBSQLiteHelper";
	private static final String name = "locate_db";
	private static final String table_name = "locate";
	private static final int version = 1;

	public static LocateLogDBHelper dbHelper;

	public LocateLogDBHelper(Context context) {
		super(context, name, null, version);
		Log.v(tag, name + "创建");
	}

	public static void initialize(Context context) {
		if (dbHelper == null) {
			dbHelper = new LocateLogDBHelper(context);
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ table_name
				+ "(id integer primary key,cmpid varchar(4),itemid varchar(2),pid varchar(10),loctel varchar(11),longitude double,latitude double,address text,loctime text,isworking integer,gpsstatus integer,gprsstatus integer,wifistatus integer,electricity integer)");
		Log.v(tag, "table operate创建");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + table_name);
		Log.v(tag, "table operate删除");
	}

}
