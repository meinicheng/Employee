package com.sdbnet.hywy.employee.utils;

import java.util.HashMap;
import java.util.Map;

import android.os.Environment;

import com.sdbnet.hywy.employee.R;

public class Constants {

	public static final Map<String, Integer> MENU_MAP = new HashMap<String, Integer>();
	// public static final Map<String, String> MENU_MAP = new HashMap<String,
	// String>();

	/**
	 * 菜单码
	 */
	static {
		// MENU_MAP.put("HYWY001", "首页");
		// MENU_MAP.put("HYWY002", "个人资料");
		// MENU_MAP.put("HYWY003", "订单扫描");
		// MENU_MAP.put("HYWY004", "草稿箱(%d)");
		MENU_MAP.put("HYWY001", R.string.menuHomepage);// "首页");
		MENU_MAP.put("HYWY002", R.string.menuInfo);// "个人资料");
		MENU_MAP.put("HYWY003", R.string.menuScan);// "订单扫描");
		MENU_MAP.put("HYWY004", R.string.menuDraftBox_x);// "草稿箱(%d)");
		// MENU_MAP.put("HYWY005", R.string.menu_report_execption);// 异常申报;

	}

	public static final class Action {
		// 后台定位时，若城市发生改变，发送此广播，便于取得相应城市的天气
		public static final String ACTION_CITY_CHANGED = "action_city_change";
		public static final String ACTION_WEATHER_DATA = "action_weather_data";
		public static final String ACTION_GET_MYLOCATION = "action_get_mylocation";
	}

	/**
	 * 操作步骤的动作码
	 * 
	 * @author Administrator
	 * 
	 */
	public static final class Step {
		public static final String CHWK = "CHWK"; // 切换上下班
		public static final String NEXT = "NEXT"; // 扫描完成点下一步
		public static final String POST = "POST"; // 订单执行提交
		public static final String LOCA = "LOCA"; // 定位
		public static final String EDIT = "EDIT"; // 编辑用户资料
		public static final String SAVE = "SAVE"; // 保存编辑后的用户资料
		public static final String DRAFT = "DRAFT"; // 草稿箱操作
	}

	public static final class Value {
		public static final String STATUS_OK = "1";
		public static final String STATUS_NO = "0";

		public static final String LOGIN_EMPLOYEE = "1";
		public static final String LOGIN_PU = "2";
		public static final String YES = "1";
		public static final String NO = "0";
		public static final String WOMAN = "1";
		public static final String MAN = "0";
		public static final int ORDER_STATU_SIGNIN = 1;
		public static final int ORDER_STATU_UNSIGNIN = 2;
		public static final int ORDER_STATU_ABSENT = 3;
		// public static final int IMAGE_COUNT = 10;
		public static final int SCAN_COUNT = 100;
		public static final String TEXT_EXEC = "执行订单";
		public static final String TEXT_CLEAR = "清除记录";
		public static final String SCAN_ORDER = "%s扫描";
		public static final boolean WORK_STATUS_DEFAULT = false;
		public static final boolean WORKING = true;
		public static final boolean WORKED = false;
		public static final boolean USING = true;

		public static final int DELAY_TIME_HIGH = 10 * 1000;
		public static final int DELAY_TIME_MIDDLE = 5 * 1000;
		public static final int DELAY_TIME_LOW = 1000;
	}

	// public static final class RequestCode {
	// public static final int QUERY_ORDER = 1;
	// public static final int CHOOSE_GROUP = 2;
	// public static final int QUERY_ORDER_LIST = 3;
	// public static final int QUERY_ORDER_POST = 4;
	// public static final int QUERY_CHOOSE_MODEL = 5;
	// public static final int QUERY_CHOOSE_LENGTH = 6;
	// public static final int QUERY_CHOOSE_LOAD = 7;
	// }
	//
	// public static final class ResultCode {
	// public static final int QUERY_ORDER = 1;
	// public static final int CHOOSE_GROUP = 2;
	// public static final int QUERY_ORDER_LIST = 3;
	// public static final int QUERY_ORDER_POST = 4;
	// public static final int QUERY_CHOOSE_MODEL = 5;
	// public static final int QUERY_CHOOSE_LENGTH = 6;
	// public static final int QUERY_CHOOSE_LOAD = 7;
	// }

	/**
	 * 服务器返回的字段
	 * 
	 * @author Administrator
	 * 
	 */
	public static final class Feild {
		// common feild
		public static final String KEY_ERROR_CODE = "errcode";
		public static final String KEY_MSG = "msg";
		public static final String KEY_TOKEN = "token";
		public static final String KEY_ITEMS = "cmpItems";
		public static final String KEY_COMPANY_ID = "cmpid";

		public static final String KEY_COMPANY_NAME = "cmpname";
		public static final String KEY_COMPANY_TEL_1 = "telephone1";
		public static final String KEY_COMPANY_TEL_2 = "telephone2";
		public static final String KEY_COMPANY_ADDRESS = "cmpaddress";
		public static final String KEY_COMPANY_LOGO = "logo";
		public static final String KEY_COMPANY_ORDTITLE = "ordtitle";
		public static final String KEY_COMPANY_CNOTITLE = "cnotitle";
		public static final String KEY_COMPANY_REMARK = "remark";
		public static final String KEY_COMPANY_EMAIL = "email";
		public static final String KEY_COMPANY_URL = "url";
		public static final String KEY_COMPANY_LINKMAN = "linkman";
		public static final String KEY_ITEM_ID = "itemid";
		public static final String KEY_ITEM_NAME = "itemname";
		public static final String KEY_STAFF = "staff";
		public static final String KEY_STAFF_ID = "pid";
		public static final String KEY_STAFF_PWD = "pwd";
		public static final String KEY_STAFF_TEL = "tel";
		public static final String KEY_STAFF_NAME = "pname";
		public static final String KEY_STAFF_SEX = "sex";
		public static final String KEY_STAFF_ROLE = "role";
		public static final String KEY_ACTION_CALL = "iscall";
		public static final String KEY_ACTION_LOCAT = "islocate";
		public static final String KEY_ACTION_IS_SCAN = "isscan";
		public static final String KEY_LOCA_TEL = "loctel";
		public static final String KEY_LOCA_TIME = "loctime";
		public static final String KEY_LOCA_ADDRESS = "locaddress";
		public static final String KEY_LOCA_LONGITUDE = "longitude";
		public static final String KEY_LOCA_LATITUDE = "latitude";
		public static final String KEY_LOCA_COORDINATES = "coordinates";
		public static final String KEY_STAFF_IS_DRIVER = "isdriver";
		public static final String KEY_STAFF_TRUCK_NO = "truckno";
		public static final String KEY_STAFF_TRUCK_TYPE = "trucktype";
		public static final String KEY_STAFF_TRUCK_LENGTH = "trucklength";
		public static final String KEY_STAFF_TRUCK_WEIGHT = "truckweight";
		public static final String KEY_CITY = "city";
		public static final String KEY_IS_ADMIN = "isadm";
		public static final String KEY_MENU = "menu";
		// scan array feild
		public static final String KEY_EXECUTE_ACTION = "scan";
		public static final String KEY_ACTION_ID = "actidx";
		public static final String KEY_ACTION = "action";
		public static final String KEY_ACTION_NAME = "actname";
		public static final String KEY_ACTION_CONTENT = "actmemo";
		public static final String KEY_ACTION_TIME = "acttime";
		public static final String KEY_ACTION_BTN = "btnname";
		public static final String KEY_WORKFLOW = "workflow";
		public static final String KEY_START_NODE = "startnode";
		public static final String KEY_LINE_ID = "lineno";
		public static final String KEY_LINE_NAME = "linename";
		public static final String KEY_SIGN = "sign";
		//
		public static final String KEY_THIRD_PARTY = "acttype";
		public static final String KEY_WORK_GROUPS = "workgroups";
		public static final String KEY_WORK_GROUP = "workgroup";
		public static final String KEY_GROUP_ID = "grpid";
		public static final String KEY_GROUP_NAME = "grpname";
		public static final String KEY_GROUP_LOCATION = "location";
		public static final String KEY_STAFFS = "staffs";
		public static final String KEY_ORDERS = "orders";
		public static final String KEY_ORDER_NO = "ordno";
		public static final String KEY_PLATFORM_NO = "sysno";
		public static final String KEY_PLATFORM_ORIGINAL_NO = "sysnox";
		public static final String KEY_ORDER_CREATE_TIME = "ordtime";
		public static final String KEY_TRACES = "traces";
		public static final String KEY_TRACE_ID = "traceid";
		public static final String KEY_ENTERPRISE = "enterprise";
		public static final String KEY_PIC_ID = "picid";
		public static final String KEY_PIC_SMALL = "smallimg";
		public static final String KEY_PIC_BIG = "bigimg";
		public static final String KEY_IMGS = "imgArr";
		public static final String KEY_ROLE_NAME = "rname";
		public static final String KEY_CODE_LENGTH = "codelength";
		public static final String KEY_SHOWINNER = "showinner";
		public static final String KEY_TRACEINFO = "traceinfo";
		public static final String KEY_TRACETIME = "tracetime";
		public static final String KEY_OP_STEP = "op_step";
		public static final String KEY_TEL_BRAND = "tel_brand";
		public static final String KEY_TEL_MODELP = "tel_model";
		public static final String KEY_TEL_SYSTEM = "tel_system";
		public static final String KEY_TEL_VERSION = "tel_version";
		public static final String KEY_TEL_STATUS_WIFI = "tel_status_wifi";
		public static final String KEY_TEL_STATUS_NET = "tel_status_net";
		public static final String KEY_TEL_STATUS_GPS = "tel_status_gps";
		public static final String KEY_ACTMEMOINNER = "actmemoinner";
		public static final String KEY_USER_TYPE = "userType";
		public static final String KEY_SESSION_ID = "sessionid";
		public static final String KEY_IS_WORKING = "isworking";
		// zhangyu
		// options array feild
		public static final String KEY_OPTIONS = "options";
		public static final String KEY_SCAN_MODEL = "scanModel";
	}

	public static final class Login {
		public static final int PWD_LENGTH_MIN = 4;
		public static final int PWD_LENGTH_MAX = 15;
	}

	public static final class NetApi {
		// "http://api.map.baidu.com/geocoder/v2/?ak=uDE7VtRAZ92n6yIzfim6Gskg&callback=renderReverse&location=%s,%s&output=json&pois=1&mcode=06:88:C6:5C:22:9A:63:BE:69:74:7E:0C:C6:35:4A:5D:55:90:79:4D;cn.hywy.center",
		public static final String BD_LOCATION_API = "http://api.map.baidu.com/geocoder/v2/?ak=uDE7VtRAZ92n6yIzfim6Gskg&callback=renderReverse&location=%s,%s&output=json&pois=1&mcode=06:88:C6:5C:22:9A:63:BE:69:74:7E:0C:C6:35:4A:5D:55:90:79:4D;cn.hywy.center";
		public static final String WEATHER_DATA_API = "http://wthrcdn.etouch.cn/weather_mini?citykey=%s";
	}

	public static final class IntentData {
		public static final String INTENT_USER_BEAN = "intent_user_bean";
		public static final String INTENT_COMPANY_BEAN = "intent_company_bean";
		public static final String INTENT_WEATHER_BEAN = "intent_weather_bean";
	}

	public static final class DB {
		public static final String DB_NAME_LOCATE = "locate_db";
		public static final String DB_NAME_OPERATE = "operate_db";
		public static final String DB_TABLE_NAME_LOCATE = "locate";
		public static final String DB_TABLE_NAME_OPERATE = "operate";
	}

	public static final class Location {
		public static final int ERROR_CODE_OK = 0;
		public static final int ERROR_CODE_NO_WORK = 1;// 不是工作状态
		public static final int ERROR_CODE_NET_ERROR = 11;// 没有网络;
		public static final int ERROR_CODE_BASE_STATION_WIFI = 12;// 33
																	// 获取基站/WiFi信息为空或失败
		public static final int ERROR_CODE_NO_CITY_INFO = 13;// // 34
																// 定位失败无法获取城市信息
		public static final int ERROR_CODE_REVERSE_GEO = 14;// // 34
															// 定位失败无法获取城市信息

		// 28 服务器连接失败
		// 29 协议解析错误
		// 30 http 连接失败
		// 31 未知的错误
		// 32 key 鉴权失败

	}

	public static final class SDBNET {
		public static final String BASE_PATH = Environment
				.getExternalStorageDirectory().getPath() + "/sdbnet/";
		public static final String PATH_PHOTO = BASE_PATH + "PhotoCache/";
		public static final String SDPATH_FORMATS = PATH_PHOTO + ".formats/";
		public static final String SDPATH_CRASH = BASE_PATH + ".crash/";
		public static final String SDPATH_TEST = BASE_PATH + ".test/";
	}
	// public static class Config {
	// public static final boolean DEVELOPER_MODE = false;
	// }
	// public static final class ERRORCODE{
	// public static final int
	// }
}
