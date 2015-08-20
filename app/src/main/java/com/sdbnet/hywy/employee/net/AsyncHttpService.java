package com.sdbnet.hywy.employee.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.album.AlbumHelper.ImageItem;
import com.sdbnet.hywy.employee.db.db.DBManager;
import com.sdbnet.hywy.employee.db.domin.LocateLog;
import com.sdbnet.hywy.employee.location.MyLocation;
import com.sdbnet.hywy.employee.model.ExecuteAction;
import com.sdbnet.hywy.employee.model.ReportModel;
import com.sdbnet.hywy.employee.model.UserModel;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.LogUtil;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.ToastUtil;
import com.sdbnet.hywy.employee.utils.UtilsAndroid;
import com.sdbnet.hywy.employee.utils.UtilsCommon;
import com.sdbnet.hywy.employee.utils.UtilsJava;

public class AsyncHttpService {
//	public static final String BASE_URL = "http://182.18.31.50/HYWY";// 正式
	 public static final String BASE_URL = "http://192.168.1.111:8080/HYWY";
	// private static final String BASE_URL = "http://172.28.33.1:8080/HYWY";
	// public static final String BASE_URL = "http://192.168.1.115:8080/HYWY";//
	// 本地TEST

	private static AsyncHttpClient client = new AsyncHttpClient();
	private static final String TAG = "AsyncHttpService";
	static {
		// 设置超时
		client.setTimeout(20000);
	}

	/**
	 * 提交get请求
	 * 
	 * @param url
	 * @param params
	 * @param responseHandler
	 * @param context
	 */
	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler, Context context) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	/**
	 * 提交post请求
	 * 
	 * @param url
	 * @param params
	 * @param responseHandler
	 * @param context
	 */
	private static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler, Context context) {
		if (!UtilsAndroid.Set.checkNetState(context)) {
			Toast.makeText(context,
					context.getResources().getString(R.string.httpError),
					Toast.LENGTH_LONG).show();
			return;
		}
		try {
			locateUpload(context);
		} catch (Exception ex) {
			ToastUtil.show(context, "批量上传失败！");
			Log.i("upBatchLocate",
					"批量上传失败!  Exception:" + ex.getLocalizedMessage());
		}
		if ("/userLogin".equals(url)) {
			params.add(Constants.Feild.KEY_USER_TYPE, PreferencesUtil.user_type);
		} else if ("/choosePro".equals(url)) {
			PreferencesUtil.putValue(PreferencesUtil.KEY_SESSION_ID,
					UtilsJava.generateString(15));
			PreferencesUtil.session_id = PreferencesUtil.getValue(
					PreferencesUtil.KEY_SESSION_ID, null);
			params.add(Constants.Feild.KEY_SESSION_ID,
					PreferencesUtil.session_id);
		} else {
			if (!UtilsCommon.checkAccount()) {
				ToastUtil.show(context, "AsyncService post account error");
				return;
			}
			params.add(Constants.Feild.KEY_COMPANY_ID,
					PreferencesUtil.user_company);
			params.add(Constants.Feild.KEY_ITEM_ID, PreferencesUtil.item_id);
			params.add(Constants.Feild.KEY_STAFF_ID, PreferencesUtil.user_id);
			params.add(Constants.Feild.KEY_USER_TYPE, PreferencesUtil.user_type);
			String sessionId = PreferencesUtil
					.getValue(PreferencesUtil.KEY_SESSION_ID);
			if (TextUtils.isEmpty(sessionId)) {
				ToastUtil.show(context, "session id ==null");
				return;
			} else {
				params.add(Constants.Feild.KEY_SESSION_ID, sessionId);
			}
		}
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	private static class MyTask extends AsyncTask<String, Integer, String> {
		private LocateLog locateLog;
		private Context context;

		public MyTask(LocateLog locateLog, Context context) {
			this.locateLog = locateLog;
			this.context = context;
		}

		@Override
		protected String doInBackground(String... params) {
			BufferedReader in = null;

			String content = null;
			try {
				// 定义HttpClient
				HttpClient client = new DefaultHttpClient();

				HttpClientParams.setCookiePolicy(client.getParams(),
						CookiePolicy.BROWSER_COMPATIBILITY);
				// 实例化HTTP方法
				HttpGet request = new HttpGet();
				request.setURI(new URI(params[0]));
				HttpResponse response = client.execute(request);

				if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					LogUtil.d(TAG, "result: 请求失败");
				} else {

					in = new BufferedReader(new InputStreamReader(response
							.getEntity().getContent()));
					StringBuffer sb = new StringBuffer("");
					String line = "";
					String NL = System.getProperty("line.separator");
					while ((line = in.readLine()) != null) {
						sb.append(line + NL);
					}
					content = sb.toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (in != null) {
					try {
						in.close();// 最后要关闭BufferedReader
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return content;
		}

		@Override
		protected void onPostExecute(String result) {

			if (result == null) {
				return;
			}
			result = result.substring(result.indexOf("(") + 1,
					result.lastIndexOf(")"));
			String address = "";
			try {
				// 解析返回数据
				JSONObject object = new JSONObject(result);
				JSONObject jsonObject = object.getJSONObject("result");
				address = jsonObject.getString("formatted_address");
				System.out.println(address);
				if (TextUtils.isEmpty(address)) {
					// Log.e(TAG, "BDAddress=" + address + ";" +
					// result);
				} else {
					locateLog.setAddress(address);
					DBManager manager = new DBManager(context);
					manager.updateLocatelog(locateLog);
					manager.closeDatabase();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private static void locateUpload(final Context context) {
		if (!UtilsCommon.checkAccount()) {
			ToastUtil.show(context, "locateUpload  account null");
			return;
		}

		DBManager manager = new DBManager(context);
		final List<LocateLog> list = manager.getAllLocate();//
		// Log.e(TAG, "No Null="+manager.getLocateNoNull().toString());
		// Log.e(TAG, "No Null="+manager.getLocateNull().toString());
		if (null == list || list.size() == 0) {
			ToastUtil.show(context, "locatelog empty");
			manager.closeDatabase();
			return;
		}

		for (int i = 0; i < list.size(); i++) {
			// Log.e(TAG, list.get(i).getId() + ">>" + list.get(i).toString());
			Log.i(TAG, list.get(i).getId() + "," + list.get(i).getAddress()
					+ "," + list.get(i).getLatitude() + ","
					+ list.get(i).getLongitude() + ","
					+ list.get(i).getLoctime() + ",");

		}

		for (int i = 0; i < list.size(); i++) {
			LocateLog locate = list.get(i);
			if (TextUtils.isEmpty(locate.getAddress())) {
				// 根据坐标地理反查
				String url = String.format(Constants.NetApi.BD_LOCATION_API,
						locate.getLatitude(), locate.getLongitude());
				try {
					String[] param = new String[] { url };
					MyTask task = new AsyncHttpService.MyTask(locate, context);
					task.execute(param);
				} catch (Exception e) {
					e.printStackTrace();
				}
				list.remove(i);
				i--;
			} else if (locate.getCmpid().length() > 4) {
				Log.d(TAG,
						locate.getCmpid().length() + ">>" + locate.getCmpid());
				// 解决有单引号的BUG --'8888'
				locate.setCmpid(locate.getCmpid().replace("'", "").trim());
				if (locate.getCmpid().length() > 4) {
					// DBManager manager = new DBManager(context);
					manager.deleteLocateById(locate.getId());
					// manager.closeDatabase();
				}
			}

			if (!UtilsCommon.checkLocateLog(locate)) {
				Log.w(TAG, locate.toString());
				list.remove(i);
				manager.deleteLocateById(locate.getId());
				i--;
			}
		}
		manager.closeDatabase();
		LogUtil.d(TAG, list.size() + "<<<<" + list.toString() + ">>>");
		if (list.size() > 0) {
			// JSONArray arrs = new JSONArray(list2);
			upBatchLocate(context, list);
		}

	}

	private static void upBatchLocate(final Context context,
			final List<LocateLog> list) {
		StringEntity stringEntity = null;
		JSONArray jsonArray = getJsonArrayLocate(list);
		try {
			stringEntity = new StringEntity(jsonArray.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			ToastUtil.show(context, "StringEntity==null");
			e.printStackTrace();
			return;
		}

		AsyncHttpClient client = new AsyncHttpClient();
		client.post(context, getAbsoluteUrl("/upBatchLocate"), stringEntity,
				"application/json", new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						LogUtil.d(response.toString());
						try {
							int errCode = response
									.getInt(Constants.Feild.KEY_ERROR_CODE);
							if (errCode == 0) {
								DBManager manager = new DBManager(context);
								for (int i = 0; i < list.size(); i++) {
									manager.deleteLocateById(list.get(i)
											.getId());
								}
								manager.closeDatabase();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	private static JSONArray getJsonArrayLocate(List<LocateLog> list) {
		JSONArray jsonArray = new JSONArray();
		for (LocateLog log : list) {
			JSONObject jsonObject = new JSONObject();
			// private String id;
			try {
				jsonObject.put("id", log.getId());
				// private String cmpid;
				jsonObject.put("cmpid", log.getCmpid());

				// private String itemid;
				jsonObject.put("itemid", log.getItemid());
				// private String pid;
				jsonObject.put("pid", log.getPid());
				// private String loctel;
				jsonObject.put("loctel", log.getLoctel());
				// private double longitude;
				jsonObject.put("longitude", log.getLongitude());
				// private double latitude;
				jsonObject.put("latitude", log.getLatitude());
				// private String address;
				jsonObject.put("address", log.getAddress());
				// private String loctime;
				jsonObject.put("loctime", log.getLoctime());
				// private Integer isworking;
				jsonObject.put("isworking", log.getIsworking());
				// private Integer gpsstatus;
				jsonObject.put("gpsstatus", log.getGpsstatus());
				// private Integer gprsstatus;
				jsonObject.put("gprsstatus", log.getGprsstatus());
				// private Integer wifistatus;
				jsonObject.put("wifistatus", log.getWifistatus());
				// private Integer electricity;
				jsonObject.put("electricity", log.getElectricity());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			jsonArray.put(jsonObject);
		}
		return jsonArray;
	}

	/**
	 * 获取url
	 * 
	 * @param relativeUrl
	 * @return
	 */
	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}

	/**
	 * 取消请求
	 * 
	 * @param context
	 */
	public static void cancelRequests(Context context) {
		client.cancelRequests(context, true);
	}

	/**
	 * 获取验证码
	 * 
	 * @param tel
	 * @param userType
	 * @param responseHandler
	 * @param context
	 */
	public static void getValidationCode(String tel, int userType,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		params.add("tel", tel);
		params.add("userType", userType + "");
		post("/getValidationCode", params, responseHandler, context);
	}

	/**
	 * 用户验证
	 * 
	 * @param tel
	 * @param code
	 * @param userType
	 * @param responseHandler
	 * @param context
	 */
	public static void validation(String tel, String code, int userType,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		params.add("tel", tel);
		params.add("code", code);
		params.add("userType", "" + userType);
		post("/validation", params, responseHandler, context);
	}

	/**
	 * 用户注册
	 * 
	 * @param tel
	 * @param pwd
	 * @param userType
	 * @param responseHandler
	 * @param context
	 */
	public static void registUser(String tel, String pwd, int userType,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		params.add("tel", tel);
		params.add("pwd", pwd);
		params.add("userType", userType + "");
		post("/userRegist", params, responseHandler, context);
	}

	/**
	 * 用户登录验证
	 * 
	 * @param tel
	 *            注册手机号
	 * @param pwd
	 *            密码
	 * @param loginType
	 *            登录类型（个人用户或企业员工）
	 * @param responseHandler
	 * @param context
	 */
	public static void login(String tel, String pwd,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		params.add("tel", tel);
		params.add("pwd", pwd);
		post("/userLogin", params, responseHandler, context);
	}

	/**
	 * 密码重设
	 * 
	 * @param tel
	 * @param pwd
	 * @param responseHandler
	 * @param context
	 */
	public static void resetPwd(String tel, String pwd,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		params.add("tel", tel);
		params.add("pwd", pwd);
		post("/resetDriverPwd", params, responseHandler, context);
	}

	/**
	 * 获得项目的相关操作权限
	 * 
	 * @param cmpid
	 *            公司编码
	 * @param itemid
	 *            项目特征码
	 * @param pid
	 *            用户ID
	 * @param responseHandler
	 * @param context
	 */
	public static void getPermissionWithProject(String cmpid, String itemid,
			String pid, AsyncHttpResponseHandler responseHandler,
			Context context) {
		RequestParams params = new RequestParams();
		params.add("cmpid", cmpid);
		params.add("itemid", itemid);
		params.add("pid", pid);
		params.add("userType", "1");
		post("/choosePro", params, responseHandler, context);
	}

	/**
	 * 上传设备号
	 * 
	 * @param pid
	 *            用户ID
	 * @param cmpid
	 *            企业ID
	 * @param itemid
	 *            项目特征码
	 * @param tel
	 *            手机号
	 * @param registrationid
	 *            推送RegistrationID
	 * @param userType
	 *            用户类型
	 * @param responseHandler
	 * @param context
	 */
	public static void upDeviceInfo(String tel, String registrationid,
			String imei, AsyncHttpResponseHandler responseHandler,
			Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_STAFF_ID, pid);
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		// params.add("userType", userType);
		params.add(Constants.Feild.KEY_STAFF_TEL, tel);
		params.add("registrationid", registrationid);
		params.add("imei", imei);
		params.add("deviceType", "3");
		params.add("token", PreferencesUtil.user_token);
		post("/upDeviceInfo", params, responseHandler, context);
	}

	/**
	 * 返回用户信息
	 * 
	 * @param cmpid
	 *            企业ID
	 * @param itemid
	 *            项目特征码
	 * @param pid
	 *            用户ID
	 * @param responseHandler
	 * @param context
	 */
	public static void getUserInfo(AsyncHttpResponseHandler responseHandler,
			Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_STAFF_ID, pid);
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		post("/userInfo", params, responseHandler, context);
	}

	/**
	 * 上传订单扫描信息
	 * 
	 * @param cmpid
	 *            企业ID
	 * @param itemid
	 *            项目特征码
	 * @param ordno
	 *            企业订单号
	 * @param acttime
	 *            动作发生时间
	 * @param actidx
	 *            动作序号
	 * @param pid
	 *            动作触发人
	 * @param action
	 *            动作码
	 * @param actname
	 *            动作名称
	 * @param actmemo
	 *            动作说明
	 * @param sign
	 *            是否更新订单状态
	 * @param lineno
	 *            轨迹编号
	 * @param linename
	 *            轨迹名称
	 * @param loctime
	 *            定位时间
	 * @param locaddress定位地点
	 * @param longitude
	 *            经度
	 * @param latitude
	 *            纬度
	 * @param imgList
	 *            附件图片集合
	 * @param responseHandler
	 * @param context
	 */
	public static void postScanOrderInfo(String ordno, String acttime,
			String actidx, String pid, String action, String actname,
			String actmemo, String sign, String lineno, String linename,
			String loctime, String locaddress, Double longitude,
			Double latitude, List<String> imgList,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_STAFF_ID, pid);
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		params.add(Constants.Feild.KEY_ORDER_NO, ordno);
		params.add(Constants.Feild.KEY_ACTION_TIME, acttime);
		params.add(Constants.Feild.KEY_ACTION_ID, actidx);
		params.add(Constants.Feild.KEY_ACTION, action);
		params.add(Constants.Feild.KEY_ACTION_NAME, actname);
		params.add(Constants.Feild.KEY_ACTION_CONTENT, actmemo);
		params.add(Constants.Feild.KEY_SIGN, sign);
		params.add(Constants.Feild.KEY_LINE_ID, lineno);
		params.add(Constants.Feild.KEY_LINE_NAME, linename);
		params.add(Constants.Feild.KEY_LOCA_TIME, loctime);
		params.add(Constants.Feild.KEY_LOCA_ADDRESS, locaddress);
		params.add(Constants.Feild.KEY_LOCA_LATITUDE, String.valueOf(latitude));
		params.add(Constants.Feild.KEY_LOCA_LONGITUDE,
				String.valueOf(longitude));
		params.add("actype", "0");

		File f = null;
		for (int i = 1; i <= imgList.size(); i++) {
			f = new File(imgList.get(i - 1));
			if (f.exists() && f.length() > 0) {
				try {
					params.put("image" + i, f);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		post("/orderScanning", params, responseHandler, context);
	}

	/**
	 * 上传订单扫描信息
	 * 
	 * @param cmpid
	 *            企业ID
	 * @param itemid
	 *            项目特征码
	 * @param ordno
	 *            企业订单号
	 * @param workflow
	 *            工作流程
	 * @param startnode
	 *            是否为发起节点
	 * @param acttime
	 *            动作发生时间
	 * @param actidx
	 *            动作序号
	 * @param pid
	 *            动作触发人
	 * @param action
	 *            动作码
	 * @param actname
	 *            动作名称
	 * @param actmemo
	 *            动作说明
	 * @param sign
	 *            是否更新订单状态
	 * @param lineno
	 *            轨迹编号
	 * @param linename
	 *            轨迹名称
	 * @param loctime
	 *            定位时间
	 * @param locaddress定位地点
	 * @param longitude
	 *            经度
	 * @param latitude
	 *            纬度
	 * @param imgList
	 *            附件图片集合
	 * @param responseHandler
	 * @param context
	 */
	public static void postScanOrderInfo(String ordno, String loctel,
			String workflow, String startnode, String acttime, String actidx,
			String pid, String action, String actname, String actmemo,
			String actmemoinner, String sign, String iscall, String islocate,
			String isscan, String lineno, String linename, String loctime,
			String locaddress, Double longitude, Double latitude,
			List<String> imgList, AsyncHttpResponseHandler responseHandler,
			Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_STAFF_ID, pid);
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		params.add(Constants.Feild.KEY_ORDER_NO, ordno);
		params.add(Constants.Feild.KEY_WORKFLOW, workflow);
		params.add(Constants.Feild.KEY_START_NODE, startnode);
		params.add(Constants.Feild.KEY_ACTION_TIME, acttime);
		params.add(Constants.Feild.KEY_ACTION_ID, actidx);
		params.add(Constants.Feild.KEY_ACTION, action);
		params.add(Constants.Feild.KEY_ACTION_NAME, actname);
		params.add(Constants.Feild.KEY_ACTION_CONTENT, actmemo);
		params.add(Constants.Feild.KEY_ACTMEMOINNER, actmemoinner);
		params.add(Constants.Feild.KEY_SIGN, sign);
		params.add(Constants.Feild.KEY_ACTION_CALL, iscall);
		params.add(Constants.Feild.KEY_ACTION_LOCAT, islocate);
		params.add(Constants.Feild.KEY_ACTION_IS_SCAN, isscan);
		params.add(Constants.Feild.KEY_LINE_ID, lineno);
		params.add(Constants.Feild.KEY_LINE_NAME, linename);
		params.add(Constants.Feild.KEY_LOCA_TIME, loctime);
		params.add(Constants.Feild.KEY_LOCA_TEL, loctel);
		params.add(Constants.Feild.KEY_LOCA_ADDRESS, locaddress);
		params.add(Constants.Feild.KEY_LOCA_LATITUDE, String.valueOf(latitude));
		params.add(Constants.Feild.KEY_LOCA_LONGITUDE,
				String.valueOf(longitude));
		params.add("actype", "0");

		// 添加图片附件
		File f = null;
		for (int i = 1; i <= imgList.size(); i++) {
			f = new File(imgList.get(i - 1));
			if (f.exists() && f.length() > 0) {
				try {
					params.put("image" + i, f);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		post("/orderScanning", params, responseHandler, context);
	}

	public static void postScanOrderInfo(ExecuteAction orderAction,
			String address, double longitude, double latitude,
			AsyncHttpResponseHandler responseHandler, Context context) {
		String time = (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"))
				.format(new Date());
		// 去掉重复的图片
		ArrayList<String> imgs = new ArrayList<String>();
		if (orderAction.getImageItems() != null)
			for (ImageItem imageItem : orderAction.getImageItems()) {
				String path = imageItem.imagePath;
				File file = new File(path);
				if (file.exists()) {
					imgs.add(path);
				}
			}
		postScanOrderInfo(orderAction.getActidx(), PreferencesUtil.user_tel,
				orderAction.getWorkflow(), orderAction.getStartnode(), time,
				orderAction.getActidx(), PreferencesUtil.user_id,
				orderAction.getAction(), orderAction.getActname(),
				orderAction.getActmemo(), orderAction.getActmemoinner(),
				orderAction.getSign(), orderAction.getIscall(),
				orderAction.getIslocate(), orderAction.getIsscan(),
				orderAction.getLineno(), orderAction.getLinename(), time,
				address, longitude, latitude,
				// UtilsCommon.strs2List(orderAction.getImsgs()),
				// responseHandler,
				imgs, responseHandler, context);
	}

	public static void postScanOrderInfo(ExecuteAction orderAction,
			MyLocation location, AsyncHttpResponseHandler responseHandler,
			Context context) {
		postScanOrderInfo(orderAction, location.address, location.longitude,
				location.latitude, responseHandler, context);
	}

	public static void uploadReportExection(ReportModel reportModel,
			AsyncHttpResponseHandler responseHandler, Context context) {
		// ordno 企业订单号 ○ 当前企业的订单号,","隔开
		// accdesc 异常描述 ○ (必填)
		// pname 用户名称 ○ (必填)
		// loctel 用户电话 ○ (必填)
		// acctime 异常发生时间 ○
		// locaddr 定位地址
		// image1…image5 图片
		// Log.i("uploadReportExection", reportModel.toString());
		PreferencesUtil.initStoreData();
		RequestParams params = new RequestParams();
		if (TextUtils.isEmpty(reportModel.orders)) {
			ToastUtil.show(context, "orders == null");
			return;
		}
		params.add("ordno", reportModel.orders);

		if (TextUtils.isEmpty(reportModel.explain)) {
			ToastUtil.show(context, "explain == null");
			return;
		}
		params.add("accdesc", reportModel.explain);

		String name = PreferencesUtil.getValue(PreferencesUtil.KEY_USER_NAME);
		if (TextUtils.isEmpty(name)) {
			ToastUtil.show(context, "user name == null");
			return;
		}
		params.add("pname", name);

		String tel = PreferencesUtil.getValue(PreferencesUtil.KEY_USER_TEL);
		if (TextUtils.isEmpty(tel)) {
			ToastUtil.show(context, "Tellphone == null");
			return;
		}
		params.add("loctel", tel);

		// Unnecessary
		params.add("acctime", reportModel.date);
		// Unnecessary
		params.add("locaddr", reportModel.place);

		File f;
		for (int i = 0; i < reportModel.imgList.size(); i++) {
			f = new File(reportModel.imgList.get(i).imagePath);
			if (f.exists() && f.length() > 0) {
				try {
					params.put("image" + (i + 1), f);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		LogUtil.d(params.toString());
		post("/accideReport", params, responseHandler, context);
	}

	/**
	 * 上传用户定位信息
	 * 
	 * @param cmpid
	 *            企业ID
	 * @param itemid
	 *            项目特征码
	 * @param pid
	 *            用户ID
	 * @param loctel
	 *            用户电话
	 * @param longitude
	 *            经度
	 * @param latitude
	 *            纬度
	 * @param locaddress
	 *            当前地址
	 * @param responseHandler
	 * @param context
	 */
	public static void uploadLocation(String loctel, Double longitude,
			Double latitude, String locaddress,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		params.add(Constants.Feild.KEY_LOCA_TEL, loctel);
		params.add(Constants.Feild.KEY_LOCA_LATITUDE, String.valueOf(latitude));
		params.add(Constants.Feild.KEY_LOCA_LONGITUDE,
				String.valueOf(longitude));
		params.add("address", locaddress);

		params.add("isworking",
				PreferencesUtil.getValue(PreferencesUtil.KEY_IS_WORKING, 1)
						+ "");
		params.add("gpsstatus",
				(UtilsAndroid.Set.getGpsStatus(context) ? 1 : 0) + "");
		params.add("gprsstatus", (UtilsAndroid.Set.getGprsStatus(context) ? 1
				: 0) + "");
		params.add("wifistatus", (UtilsAndroid.Set.getWifiStatus(context) ? 1
				: 0) + "");
		params.add("electricity",
				PreferencesUtil.getValue(PreferencesUtil.KEY_BATTERY, 100) + "");

		// client.post(getAbsoluteUrl("/upCurrentLocate"), params,
		// responseHandler);
		post("/upCurrentLocate", params, responseHandler, context);
	}

	public static void uploadLocation(Double longitude, Double latitude,
			String locaddress, AsyncHttpResponseHandler responseHandler,
			Context context) {
		uploadLocation(
				PreferencesUtil.getValue(PreferencesUtil.KEY_USER_TEL, ""),
				longitude, latitude, locaddress, responseHandler, context);
	}

	public static void uploadLocation(MyLocation location,
			AsyncHttpResponseHandler responseHandler, Context context) {
		uploadLocation(
				PreferencesUtil.getValue(PreferencesUtil.KEY_USER_TEL, ""),
				location.longitude, location.latitude, location.address,
				responseHandler, context);
	}

	/**
	 * 上传日志
	 * 
	 * @param cmpid
	 * @param itemid
	 * @param pid
	 * @param loctel
	 * @param traceinfo
	 * @param tracetime
	 * @param op_step
	 * @param tel_brand
	 * @param tel_model
	 * @param tel_system
	 * @param tel_version
	 * @param tel_status_gps
	 * @param tel_status_wifi
	 * @param tel_status_net
	 * @param responseHandler
	 * @param context
	 */
	public static void addFeedBack(String loctel, String traceinfo,
			String tracetime, String op_step, String tel_brand,
			String tel_model, String tel_system, String tel_version,
			String tel_status_gps, String tel_status_wifi,
			String tel_status_net, AsyncHttpResponseHandler responseHandler,
			Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_STAFF_ID, pid);
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		params.add(Constants.Feild.KEY_LOCA_TEL, loctel);
		params.add(Constants.Feild.KEY_TRACEINFO, traceinfo);
		params.add(Constants.Feild.KEY_TRACETIME, tracetime);
		params.add(Constants.Feild.KEY_OP_STEP, op_step);
		params.add(Constants.Feild.KEY_TEL_BRAND, tel_brand);
		params.add(Constants.Feild.KEY_TEL_MODELP, tel_model);
		params.add(Constants.Feild.KEY_TEL_SYSTEM, tel_system);
		params.add(Constants.Feild.KEY_TEL_VERSION, tel_version);
		params.add(Constants.Feild.KEY_TEL_STATUS_GPS, tel_status_gps);
		params.add(Constants.Feild.KEY_TEL_STATUS_NET, tel_status_net);
		params.add(Constants.Feild.KEY_TEL_STATUS_WIFI, tel_status_wifi);

		post("/addFeedBack", params, responseHandler, context);
	}

	/**
	 * 修改用户资料
	 * 
	 * @param cmpid
	 * @param itemid
	 * @param pid
	 * @param pname
	 * @param sex
	 * @param truckno
	 * @param trucktype
	 * @param trucklength
	 * @param truckweigth
	 * @param imgList
	 * @param responseHandler
	 * @param context
	 */
	public static void modifyUserInfo(String pname, String sex, String truckno,
			String trucktype, String trucklength, String truckweigth,
			List<String> imgList, AsyncHttpResponseHandler responseHandler,
			Context context) {
		RequestParams params = new RequestParams();
		// params.add(Constants.Feild.KEY_STAFF_ID, pid);
		// params.add(Constants.Feild.KEY_COMPANY_ID, cmpid);
		// params.add(Constants.Feild.KEY_ITEM_ID, itemid);
		params.add("conpid", PreferencesUtil.user_id);
		params.add(Constants.Feild.KEY_STAFF_NAME, pname);
		params.add(Constants.Feild.KEY_STAFF_SEX, sex);
		params.add(Constants.Feild.KEY_STAFF_TRUCK_NO, truckno);
		params.add(Constants.Feild.KEY_STAFF_TRUCK_TYPE, trucktype);
		params.add(Constants.Feild.KEY_STAFF_TRUCK_LENGTH, trucklength);
		params.add(Constants.Feild.KEY_STAFF_TRUCK_WEIGHT, truckweigth);

		File f = null;
		for (int i = 1; i <= imgList.size(); i++) {
			f = new File(imgList.get(i - 1));
			if (f.exists() && f.length() > 0) {
				try {
					params.put("image" + i, f);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		post("/modifyUserInfo", params, responseHandler, context);
	}

	/**
	 * 修改用户资料
	 * 
	 * @param userBean
	 * @param responseHandler
	 * @param context
	 */
	public static void modifyUserInfo(UserModel userBean, List<String> imgList,
			AsyncHttpResponseHandler responseHandler, Context context) {
		modifyUserInfo(userBean.userName, userBean.sex, userBean.truckNum,
				userBean.truckType, userBean.truckLength + "",
				userBean.truckWeight + "", imgList, responseHandler, context);
	}

	public static void modifyUserInfo(UserModel userBean,
			AsyncHttpResponseHandler responseHandler, Context context) {
		List<String> imgList = new ArrayList<String>();
		for (int i = 0; i < userBean.imgList.size(); i++) {
			imgList.add(userBean.imgList.get(i).imagePath);
		}
		modifyUserInfo(userBean.userName, userBean.sex, userBean.truckNum,
				userBean.truckType, userBean.truckLength + "",
				userBean.truckWeight + "", imgList, responseHandler, context);
	}

	/**
	 * 修改当前用户帐号密码
	 * 
	 * @param cmpid
	 *            企业ID
	 * @param itemid
	 *            项目特征码
	 * @param pid
	 *            用户ID
	 * @param pwd
	 *            密码
	 * @param responseHandler
	 * @param context
	 */
	public static void modifyUserPwd(String pwd,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		params.add(Constants.Feild.KEY_STAFF_PWD, pwd);
		post("/modifyUserPwd", params, responseHandler, context);
	}

	/**
	 * 修改当前用户上下班状态
	 * 
	 * @param cmpid
	 *            企业ID
	 * @param itemid
	 *            项目特征码
	 * @param pid
	 *            用户ID
	 * @param iswork
	 *            状态
	 * @param responseHandler
	 * @param context
	 */
	public static void modifyWorkStatus(String iswork,
			AsyncHttpResponseHandler responseHandler, Context context) {
		RequestParams params = new RequestParams();
		params.add("iswork", iswork);
		post("/modifyWorkStatus", params, responseHandler, context);
	}

	public static void requestIsUpdate(String ordno, String action,
			AsyncHttpResponseHandler responseHandler, Context context) {
		// ordno 订单号
		// action 动作编码
		Log.d(TAG, "ordno=" + ordno + "<" + action);
		RequestParams params = new RequestParams();
		params.add("ordno", ordno);
		params.add("action", action);
		post("/orderAction", params, responseHandler, context);
	}
}
