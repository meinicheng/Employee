package com.sdbnet.hywy.employee.db.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * 一个访问数据库得到省份与城市
 */
public class DBCityHelper extends SQLiteOpenHelper {

	/**
	 * 构建一个数据库操作对象
	 * 
	 * @param context
	 *            当前程序的上下文对象
	 * @param dataname
	 *            数据库名
	 */
	public DBCityHelper(Context context, String dataname) {
		super(context, dataname, null, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	/**
	 * 得到所有支持的省份或直辖市名称的String类型数组
	 * 
	 * @return 支持的省份或直辖市数组
	 */
	public String[] getAllProvinces() {
		String[] columns = { "name" };

		SQLiteDatabase db = getReadableDatabase();
		// 查询获得游标
		Cursor cursor = db.query("provinces", columns, null, null, null, null,
				null);
		columns = null;
		int count = cursor.getCount();
		String[] provinces = new String[count];
		count = 0;
		while (!cursor.isLast()) {
			cursor.moveToNext();
			provinces[count] = cursor.getString(0);
			count = count + 1;
		}
		cursor.close();
		db.close();
		return provinces;
	}

	/**
	 * 根据省份数组来得到对应装有对应的城市名和城市编码的列表对象
	 * 
	 * @param provinces
	 *            省份数组
	 * @return 索引0为对应的城市名的二维数组和索引1为对应城市名的二维数组
	 */
	public List<String[][]> getAllCityAndCode(String[] provinces) {
		int length = provinces.length;
		String[][] city = new String[length][];
		String[][] code = new String[length][];
		int count = 0;
		SQLiteDatabase db = getReadableDatabase();
		for (int i = 0; i < length; i++) {
			Cursor cursor = db.query("citys",
					new String[] { "name", "city_num" }, "province_id = ? ",
					new String[] { String.valueOf(i) }, null, null, null);
			count = cursor.getCount();
			city[i] = new String[count];
			code[i] = new String[count];
			count = 0;
			while (!cursor.isLast()) {
				cursor.moveToNext();
				city[i][count] = cursor.getString(0);
				code[i][count] = cursor.getString(1);
				count = count + 1;
			}
			cursor.close();
		}
		db.close();
		List<String[][]> result = new ArrayList<String[][]>();
		result.add(city);
		result.add(code);
		return result;
	}

	/**
	 * 由城市名查询数据库来得到城市码
	 * 
	 * @param cityName
	 *            城市名
	 * @return 城市码
	 */
	public String getCityCodeByName(String cityName) {
		System.out.println("cityName:" + cityName);
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query("citys", new String[] { "city_num" },
				"name = ? ", new String[] { cityName }, null, null, null);
		String cityCode = null;
		if (!cursor.isLast()) {
			cursor.moveToNext();
			cityCode = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return cityCode;
	}
}
