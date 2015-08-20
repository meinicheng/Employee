package com.sdbnet.hywy.employee.utils;

import com.sdbnet.hywy.employee.utils.PreferencesUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferencesUtil {
	private static SharedPreferences share;

	public static final String KEY_USER_TEL = "userTelKey"; // 当前用户电话号码
	public static final String KEY_USER_PWD = "userPwdKey"; // 当前用户密码
	public static final String KEY_USER_ID = "userIdKey"; // 当前用户id
	public static final String KEY_USER_TYPE = "userTypeKey"; // 当前用户类型
	public static final String KEY_USER_ROLE = "userRoleKey"; // 当前用户角色
	public static final String KEY_USER_IS_DRIVER = "userisdriverKey"; // 当前用户是否为车辆用户
	public static final String KEY_COMPANY_ID = "userParentKey";// 当前用户所属公司id
	public static final String KEY_COMPANY_NAME = "userParentNameKey";// 当前用户所属公司id
	public static final String KEY_COMPANY_LOGO = "userParentLogoKey";// 当前用户所属公司logo
	public static final String KEY_ORDTITLE = "userOrdKey";// 当前用户所属公司订单号标题(服务器配置)
	public static final String KEY_CNOTITLE = "userCnoKey";// 当前用户所属公司客户单号标题(服务器配置)
	public static final String KEY_ITEM_ID = "itemKey";// 当前用户所属项目id
	public static final String KEY_ITEM_NAME = "itemNameKey";// 当前用户所属项目id
	public static final String KEY_USER_NAME = "userNameKey"; // 当前用户名
	public static final String KEY_USER_PARENT_NAME = "userParentNameKey";// 当前用户所属用户名
	public static final String KEY_USER_ADMIN = "userAdminKey"; // 当前用户是否为管理员
	public static final String KEY_USER_TOKEN = "userTokenKey"; // access token
	public static final String KEY_SYSTEM_VERSION = "systemVersionKey"; // 系统当前版本号
	public static final String KEY_SHORTCUT = "shortcutKey"; // 桌面图标
	public static final String KEY_ITEMS = "items"; // 桌面图标
	public static final String KEY_CITY_CODE = "cityCode"; // 城市编码
	public static final String KEY_CITY_CODE_DAFULT = "101010100";// 默认城市编码；
	public static final String KEY_CITY = "city"; // 城市
	public static final String KEY_VALID_TIME = "validTime"; // 有效时间
	public static final String KEY_WEATHER = "weather"; // 天气
	public static final String KEY_CODE_LENGTH = "codelength"; // 扫码长度
	public static final String KEY_WORK_STATS = "workstats"; // 工作状态
	public static final String KEY_IS_USING = "isusing"; // 是否正常使用状态
	public static final String KEY_FIRST_RUN = "firstRun"; // 是否第一次运行

	public static final String KEY_EXECUTE_MENU = "menu";
	public static final String KEY_EXECUTE_ACTION = "scan";
	public static final String KEY_ERROR_LOG = "errlog";

	public static final String KEY_STEPS = "steps";

	public static final String DEFAULT_ORDTITLE = "订单";
	public static final String DEFAULT_CNOTITLE = "客户单号";

	public static final String KEY_SESSION_ID = "sessionid";
	public static final String KEY_IS_WORKING = "isworking";
	public static final String KEY_BATTERY = "battery";

	public static String user_tel = "";
	public static String user_pwd = "";
	public static String user_token = "";
	public static String user_id = "";
	public static String is_driver = "";
	public static String user_name = "";
	public static String user_type = "1";
	public static String user_company = "";
	public static String company_logo = "";
	public static String ordtitle = "";
	public static String cnotitle = "";
	public static String user_parent_name = "";
	public static int user_admin;
	public static int code_length;
	public static String item_id = "";
	public static String system_version = "";
	public static String session_id = "";
	public static int isworking;
	public static boolean workstatus;
	public static int battery;

	// zhangyu

	public static final String KEY_TRAFFIC_STATS = "key_traffic_stats";
	public static long trafficStats;// 流量单位B;

	public static final int STATUS_ON = 0;
	public static final int STATUS_OFF = 1;

	public static final String KEY_GPS_STATUS = "key_gps_status";
	public static int gpsStatus;// gps 的状态; 0->开；1->关；
	public static final String KEY_GPRS_STATUS = "key_gprs_status";
	public static int gprsStatus;// gprs 的状态; 0->开；1->关；
	public static final String KEY_WIFI_STATUS = "key_wifi_status";
	public static int wifiStatus;// wifi 的状态; 0->开；1->关；
	public static final String KEY_ALIAS_TAGS = "key_alias_tags";
	public static boolean aliasTagsStatus;
	public static final String KEY_AUTO_CODE="key_auto_code";
	public static boolean autoCode;

	public static void initialize(Context context) {
		if (share == null) {
			share = PreferenceManager.getDefaultSharedPreferences(context);
		}
	}

	/**
	 * 初始化缓存信息
	 */
	public static void initStoreData() {

		PreferencesUtil.user_id = getValue(PreferencesUtil.KEY_USER_ID, null);
		PreferencesUtil.user_pwd = getValue(PreferencesUtil.KEY_USER_PWD, null);
		PreferencesUtil.user_name = getValue(PreferencesUtil.KEY_USER_NAME,
				null);
		PreferencesUtil.user_company = getValue(PreferencesUtil.KEY_COMPANY_ID,
				null);
		PreferencesUtil.user_parent_name = getValue(
				PreferencesUtil.KEY_USER_PARENT_NAME, null);
		PreferencesUtil.user_tel = getValue(PreferencesUtil.KEY_USER_TEL, null);
		PreferencesUtil.user_token = getValue(PreferencesUtil.KEY_USER_TOKEN,
				null);
		PreferencesUtil.item_id = getValue(PreferencesUtil.KEY_ITEM_ID, null);
		PreferencesUtil.user_admin = getValue(PreferencesUtil.KEY_USER_ADMIN,
				10);
		PreferencesUtil.code_length = getValue(PreferencesUtil.KEY_CODE_LENGTH,
				10);
		PreferencesUtil.system_version = getValue(
				PreferencesUtil.KEY_SYSTEM_VERSION, null);
		PreferencesUtil.company_logo = getValue(
				PreferencesUtil.KEY_COMPANY_LOGO, null);
		PreferencesUtil.ordtitle = getValue(PreferencesUtil.KEY_ORDTITLE,
				DEFAULT_ORDTITLE);
		PreferencesUtil.cnotitle = getValue(PreferencesUtil.KEY_CNOTITLE,
				DEFAULT_CNOTITLE);
		PreferencesUtil.is_driver = getValue(
				PreferencesUtil.KEY_USER_IS_DRIVER, null);
		PreferencesUtil.session_id = getValue(PreferencesUtil.KEY_SESSION_ID,
				null);
		PreferencesUtil.isworking = getValue(PreferencesUtil.KEY_IS_WORKING, 1);
		PreferencesUtil.workstatus = getValue(PreferencesUtil.KEY_WORK_STATS,
				Constants.Value.WORK_STATUS_DEFAULT);

		PreferencesUtil.battery = getValue(PreferencesUtil.KEY_BATTERY, 100);
	}

	public static boolean hasKey(final String key) {
		return share.contains(key);
	}

	public static void putValue(final String key, final String value) {
		share.edit().putString(key, value).commit();
	}

	public static void putValue(final String key, final long value) {
		share.edit().putLong(key, value).commit();
	}

	public static void putValue(final String key, final float value) {
		share.edit().putFloat(key, value).commit();
	}

	public static void putValue(final String key, final boolean value) {
		share.edit().putBoolean(key, value).commit();
	}

	public static void putValue(final String key, final int value) {
		share.edit().putInt(key, value).commit();
	}

	public static boolean getValue(final String key, final boolean defaultValue) {
		return share.getBoolean(key, defaultValue);
	}

	public static String getValue(String key) {
		return getValue(key, "");
	}

	public static String getValue(String key, final String defaultValue) {
		return share.getString(key, defaultValue);
	}

	public static int getValue(final String key, final int invalid) {
		return share.getInt(key, invalid);
	}

	public static float getValue(final String key, final float defaultValue) {
		return share.getFloat(key, defaultValue);
	}

	public static long getValue(final String key, final long defaultValue) {
		return share.getLong(key, defaultValue);
	}

	public static void clearPreference(final SharedPreferences p) {
		final Editor editor = p.edit();
		editor.clear();
		editor.commit();
	}

	/**
	 * 清除本地缓存
	 * 
	 * @param p
	 */
	public static void clearLocalData(final SharedPreferences p) {
		final Editor editor = p.edit();
		editor.remove(KEY_USER_ID);
		editor.remove(KEY_USER_PWD);
		editor.remove(KEY_USER_NAME);
		editor.remove(KEY_COMPANY_ID);
		editor.remove(KEY_USER_PARENT_NAME);
		// 手机号码不清除
		// editor.remove(KEY_USER_TEL);
		editor.remove(KEY_USER_TOKEN);

		editor.remove(KEY_ITEM_ID);
		editor.remove(KEY_USER_ADMIN);
		editor.remove(KEY_CODE_LENGTH);
		editor.remove(KEY_SYSTEM_VERSION);
		editor.remove(KEY_COMPANY_LOGO);
		editor.remove(KEY_ORDTITLE);
		editor.remove(KEY_CNOTITLE);
		editor.remove(KEY_USER_IS_DRIVER);
		editor.remove(KEY_SESSION_ID);
		editor.remove(KEY_IS_WORKING);
		//移出新增权限20150727
		editor.remove(KEY_ORDTITLE);
		
		editor.commit();

		PreferencesUtil.user_id = null;
		PreferencesUtil.user_pwd = null;
		PreferencesUtil.user_name = null;
		PreferencesUtil.user_company = null;
		PreferencesUtil.user_parent_name = null;
		PreferencesUtil.user_token = null;
		// PreferencesUtil.user_tel = null;
		PreferencesUtil.item_id = null;
		PreferencesUtil.user_admin = 10;
		PreferencesUtil.code_length = 10;
		PreferencesUtil.system_version = null;
		PreferencesUtil.company_logo = null;
		PreferencesUtil.ordtitle = null;
		PreferencesUtil.cnotitle = null;
		PreferencesUtil.is_driver = null;
		PreferencesUtil.session_id = null;
		PreferencesUtil.isworking = 1;
	}

	public static void clearLocalData(Context context) {
		clearLocalData(PreferenceManager.getDefaultSharedPreferences(context));
	}

	/**
	 * 将操作步骤保存在缓存中
	 * 
	 * @param step
	 */
	public static void setSteps(String step) {
		String steps = PreferencesUtil.getValue(PreferencesUtil.KEY_STEPS, "");
		steps += "," + step;
		PreferencesUtil.putValue(PreferencesUtil.KEY_STEPS, steps);
	}

}
