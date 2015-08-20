package com.sdbnet.hywy.employee.db.old;

import java.util.List;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.sdbnet.hywy.employee.db.domin.LocateLog;

public interface LocateLogDao {
	//添加的操作
	public boolean insert(SQLiteDatabase db,ContentValues values);
	//执行更新的操作
	public boolean update(SQLiteDatabase db,ContentValues values,Integer id);
	//根据id删除操作
	public boolean delete(SQLiteDatabase db,Integer id);
	//根据id删除操作
	public boolean deleteAll(SQLiteDatabase db);
	//查询所有
	public List<LocateLog> findAll(SQLiteDatabase db);
	//条件查询操作
//	public List<LocateLog> findByName(SQLiteDatabase db,String[] selectionArgs);
	//获取当前页的信息
//	public List<LocateLog> getNowPageInfo(SQLiteDatabase db,String[] selectionArgs,String order,String limit);
}
