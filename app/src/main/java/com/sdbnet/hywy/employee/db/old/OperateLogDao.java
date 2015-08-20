package com.sdbnet.hywy.employee.db.old;

import java.util.List;

import com.sdbnet.hywy.employee.db.domin.OperateLog;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public interface OperateLogDao {
	//添加的操作
	public boolean insert(SQLiteDatabase db,ContentValues values);
	//执行更新的操作
	public boolean update(SQLiteDatabase db,ContentValues values,Integer id);
	//根据id删除操作
	public boolean delete(SQLiteDatabase db,Integer id);
	//根据id删除操作
	public boolean deleteAll(SQLiteDatabase db);
	//查询所有
	public List<OperateLog> findAll(SQLiteDatabase db);
	//条件查询操作
//	public List<OperateLog> findByName(SQLiteDatabase db,String[] selectionArgs);
	//获取当前页的信息
//	public List<OperateLog> getNowPageInfo(SQLiteDatabase db,String[] selectionArgs,String order,String limit);
}
