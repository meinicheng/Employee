package com.sdbnet.hywy.employee.db.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	private static final String TAG = "DBHelper";
	private static final String DB_NAME = "sdbnet.db";// drafts.db"; // 数据库名称

	private static final int DB_VERSION = 2; // 数据库版本

	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建草稿箱记录表
		db.execSQL(DBCustomValue.TABLE_USER_CREATE_SQL);
		db.execSQL(DBCustomValue.TABLE_RECORD_CREATE_SQL);
		db.execSQL(DBCustomValue.TABLE_OPERATE_CREATE_SQL);
		db.execSQL(DBCustomValue.TABLE_LOCATE_CREATE_SQL);
		Log.v(TAG, "数据库表创建");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		 db.execSQL("DROP TABLE IF EXISTS " + DBCustomValue.TABLE_USER);
//		 db.execSQL("DROP TABLE IF EXISTS " + DBCustomValue.TABLE_RECORD);
//		 db.execSQL("DROP TABLE IF EXISTS " + DBCustomValue.TABLE_OPERATE);
//		 db.execSQL("DROP TABLE IF EXISTS " + DBCustomValue.TABLE_LOCATE);
		// if (oldVersion == 1 && newVersion == 2){
		if (oldVersion < 2) {
			db.execSQL("ALTER TABLE " + DBCustomValue.TABLE_OPERATE
					+ " ADD lock integer default 0 ");
			db.execSQL("ALTER TABLE " + DBCustomValue.TABLE_LOCATE
					+ " ADD lock integer default 0 ");
		}
		Log.v(TAG, "数据库表删除");
	}

}
