package com.sdbnet.hywy.employee.db.db;

public class DBCustomValue {
	// public static final String DB_NAME = "sdbnet.db"; // 数据库名称
	// public static final int DB_VERSION = 1; // 数据库版本

	public static final String COLUMN_COMPID = "compid"; // 公司id
	public static final String COLUMN_ITEMID = "itemid"; // 项目id
	public static final String COLUMN_TEL = "tel"; // 电话
	public static final String COLUMN_ORDNOS = "ordnos"; // 订单号
	public static final String COLUMN_ACTION = "action"; // 订单执行动作
	public static final String COLUMN_IMGS = "imgs"; // 图片列表
	public static final String COLUMN_PARAMS = "params"; // 订单执行的相关参数
	public static final String COLUMN_TIME = "stime"; // 订单执行时间
	public static final String COLUMN_ACTION_NAME = "actname"; // 订单执行的动作名称

	public static final String ACTION_COUNT_CHANGED = "com.sdbnet.hywy.employee.db.changed";
	public static final String COUNT = "count";

	// User data
	public final static String TABLE_USER = "user";
	public final static String TABLE_USER_CREATE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_USER
			+ " (compid varchar(4) not null, itemid varchar(2) not null, tel varchar(11) not null, pwd text not null, sex varchar(10) not null, plat_num varchar(30),type varchar(20), length text not null, weight varchar(20), imgs text, PRIMARY KEY (compid,itemid,tel) );";

	// Table record
	public final static String TABLE_RECORD = "record";
	public final static String TABLE_RECORD_CREATE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_RECORD
			+ " (compid varchar(4) not null, itemid varchar(2) not null, tel varchar(11) not null, ordnos text not null, action varchar(10) not null, actname varchar(30), params text not null, stime varchar(20), PRIMARY KEY (compid,itemid,ordnos,action) );";
	// Table Operate;
	public static final String TABLE_OPERATE = "operate";
	public final static String TABLE_OPERATE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_OPERATE
			+ "(id integer primary key,cmpid varchar(4),itemid varchar(2),pid varchar(10),loctel varchar(11),opecont text,opetime text,isworking integer,gpsstatus integer,gprsstatus integer,wifistatus integer,electricity integer,lock integer default 0)";

	// Table locate
	public static final String TABLE_LOCATE = "locate";
	public final static String TABLE_LOCATE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_LOCATE
			+ "(id integer primary key,cmpid varchar(4),itemid varchar(2),pid varchar(10),loctel varchar(11),longitude double,latitude double,address text,loctime text,isworking integer,gpsstatus integer,gprsstatus integer,wifistatus integer,electricity integer,lock integer default 0)";

	// 错误信息DB
	// public final static String TABLE_ERROR = "sd_error";
	// public final static String TABLE_ERROR_CREATE_SQL =
	// "CREATE TABLE IF NOT EXISTS "
	// + TABLE_ERROR
	// +
	// " (compid varchar(4) not null, itemid varchar(2) not null, tel varchar(11) not null, ordnos text not null, action varchar(10) not null, actname varchar(30), params text not null, stime varchar(20), PRIMARY KEY (compid,itemid,ordnos,action) );";

	// db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATE);

}
