package com.sdbnet.hywy.employee.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.sdbnet.hywy.employee.db.domin.LocateLog;
import com.sdbnet.hywy.employee.net.CustomerHttpClient;
import com.sdbnet.hywy.employee.net.CustomerHttpClient.ICallBackHandler;

public class UtilsBD {
	private final static String TAG = "UtilsBD";
	private static final String BD_LOCATION_API = "http://api.map.baidu.com/geocoder/v2/?ak=uDE7VtRAZ92n6yIzfim6Gskg&callback=renderReverse&location=%s,%s&output=json&pois=1&mcode=06:88:C6:5C:22:9A:63:BE:69:74:7E:0C:C6:35:4A:5D:55:90:79:4D;cn.hywy.center";

	public static LatLng convertToBD(double latitude, double longitude,
			CoordType coordType) {
		// 坐标转换
		CoordinateConverter converter = new CoordinateConverter();
		converter.from(coordType);
		LatLng sourceLatLng = new LatLng(latitude, longitude);
		converter.coord(sourceLatLng);
		LatLng desLatLng = converter.convert();
		return desLatLng;
	}

	/**
	 * 将硬件gps坐标转换为百度坐标
	 * 
	 * @param location
	 */

	public static LatLng gpsToBD(Location location) {
		if (location == null) {
			Log.e(TAG, "location==null");
			return null;
		}
		return convertToBD(location.getLatitude(), location.getLongitude(),
				CoordinateConverter.CoordType.GPS);
	}

	/**
	 * 将其他坐标转换为百度坐标
	 * 
	 * @param location
	 */

	public static LatLng commonToBD(Location location) {
		if (location == null) {
			Log.e(TAG, "location==null");
			return null;
		}
		// 坐标转换
		return convertToBD(location.getLatitude(), location.getLongitude(),
				CoordinateConverter.CoordType.COMMON);
	}

	public static String geo2Json(Context mContext,
			ReverseGeoCodeResult reverseResult, String locationType) {
		if (reverseResult == null) {
			return "";
		}
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("Latitude", reverseResult.getLocation().latitude);
			jsonObject.put("Longitude", reverseResult.getLocation().longitude);
			jsonObject.put("Time", UtilsJava
					.translate2SessionMessageData(System.currentTimeMillis()));
			jsonObject.put("Address", reverseResult.getAddress());

			jsonObject.put("Provider",
					reverseResult.getAddressDetail().province);
			jsonObject.put("City", reverseResult.getAddressDetail().city);

			jsonObject.put("describeContents", locationType);

			jsonObject.put("cmpid", PreferencesUtil.user_company);
			jsonObject.put("itemid", PreferencesUtil.item_id);
			jsonObject.put("pid", PreferencesUtil.user_id);
			jsonObject.put("loctel", PreferencesUtil.user_tel);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			jsonObject.put("loctime", sdf.format(new Date()));
			jsonObject.put("isworking", PreferencesUtil.isworking);
			jsonObject.put("gpsstatus",
					UtilsAndroid.Set.getGpsStatus(mContext) ? 1 : 0);
			jsonObject.put("gprsstatus",
					UtilsAndroid.Set.getGprsStatus(mContext) ? 1 : 0);
			jsonObject.put("wifistatus",
					UtilsAndroid.Set.getWifiStatus(mContext) ? 1 : 0);
			jsonObject.put("electricity", PreferencesUtil.battery);
			// jsonObject.put("时间差",
			// System.currentTimeMillis() - mLocation.getTime());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();

	}

	public static String getLocationData(BDLocation location) {

		final StringBuffer sb = new StringBuffer(256);
		sb.append("time : ");
		sb.append(location.getTime());
		sb.append("\nerror code : ");
		sb.append(location.getLocType());
		sb.append("\nlatitude : ");
		sb.append(location.getLatitude());
		sb.append("\nlontitude : ");
		sb.append(location.getLongitude());
		sb.append("\nradius : ");
		sb.append(location.getRadius());
		sb.append("\naddress : ");
		sb.append(location.getAddrStr());
		sb.append("\ncity : ");
		sb.append(location.getCity());

		if (location.getLocType() == BDLocation.TypeGpsLocation) {
			sb.append("\nspeed : ");
			sb.append(location.getSpeed());
			sb.append("\nsatellite : ");
			sb.append(location.getSatelliteNumber());
			sb.append("\ndirection : ");
			sb.append("\naddr : ");
			sb.append(location.getAddrStr());
			sb.append(location.getDirection());
		} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
			sb.append("\naddr : ");
			sb.append(location.getAddrStr());
			// 运营商信息
			sb.append("\noperationers : ");
			sb.append(location.getOperators());
		}

		return sb.toString();
	}

	public static void logLocationData(BDLocation location) {
		LogUtil.d("BaiduLocationApiDem", getLocationData(location));
	}

	public static String location2Json(Context mContext, BDLocation location) {
		if (location == null) {
			return "";
		}
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("Latitude", location.getLatitude());
			jsonObject.put("Longitude", location.getLongitude());
			jsonObject.put("Time", UtilsJava
					.translate2SessionMessageData(System.currentTimeMillis()));
			jsonObject.put("Address", location.getAddrStr());

			jsonObject.put("Provider", location.getProvince());
			jsonObject.put("City", location.getCity());

			jsonObject.put("describeContents", location.getLocType());

			jsonObject.put("cmpid", PreferencesUtil.user_company);
			jsonObject.put("itemid", PreferencesUtil.item_id);
			jsonObject.put("pid", PreferencesUtil.user_id);
			jsonObject.put("loctel", PreferencesUtil.user_tel);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			jsonObject.put("loctime", sdf.format(new Date()));
			jsonObject.put("isworking", PreferencesUtil.isworking);
			jsonObject.put("gpsstatus",
					UtilsAndroid.Set.getGpsStatus(mContext) ? 1 : 0);
			jsonObject.put("gprsstatus",
					UtilsAndroid.Set.getGprsStatus(mContext) ? 1 : 0);
			jsonObject.put("wifistatus",
					UtilsAndroid.Set.getWifiStatus(mContext) ? 1 : 0);
			jsonObject.put("electricity", PreferencesUtil.battery);
			// jsonObject.put("时间差",
			// System.currentTimeMillis() - mLocation.getTime());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}

	public static void updateBDAddress(final Context context, LocateLog locate) {
			
	}

	public static void getAddress(Context context, LatLng latLng) {
		getAddress(context, latLng.latitude, latLng.longitude);
	}

	
	public static void getAddress(Context context, double latitude,
			double longtitude) {
		if (!UtilsAndroid.Set.checkNetState(context)) {
			if (mReceiveAddress != null) {
				mReceiveAddress.onReceiveFailed();
			}
			return;
		}
		final String url = String.format(BD_LOCATION_API, latitude, longtitude);
		CustomerHttpClient.registrationListener(new ICallBackHandler() {
			@Override
			public void onReceived(String result) {
				// LogUtil.d(result);
				if (result == null) {
					if (mReceiveAddress != null) {
						mReceiveAddress.onReceiveFailed();
					}
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
					if (TextUtils.isEmpty(address)) {
						// Log.e(TAG, "BDAddress=" + address + ";" + result);
					} else {

						// locate.setAddress(address);
						// Log.d(TAG, "update address=" + address);
						// LocateLogUtil.getIntance(context).updateLocatelog(
						// locate);

					}
				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					if (mReceiveAddress != null) {
						mReceiveAddress.onReceiveSuccess(address);
					}
				}
			}
		});
		try {
			CustomerHttpClient.executeGet(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static IReceiveAddress mReceiveAddress;

	public static interface IReceiveAddress {
		void onReceiveSuccess(String address);

		void onReceiveFailed();
	}

	public static void setOnAddressListener(IReceiveAddress receiveAddress) {
		mReceiveAddress = receiveAddress;
	}
	// if (!UtilsAndroid.Set.checkNetState(context)) {
	// return;
	// }
	// final String url = String.format(Constants.NetApi.BD_LOCATION_API,
	// locate.getLatitude(), locate.getLongitude());
	// CustomerHttpClient.registrationListener(new ICallBackHandler() {
	// @Override
	// public void onReceived(String result) {
	// if (result == null) {
	// return;
	// }
	// result = result.substring(result.indexOf("(") + 1,
	// result.lastIndexOf(")"));
	// try {
	// // 解析返回数据
	// JSONObject object = new JSONObject(result);
	// JSONObject jsonObject = object.getJSONObject("result");
	// String address = jsonObject.getString("formatted_address");
	//
	// if (TextUtils.isEmpty(address)) {
	// String msg = String
	// .format("硬件gps反编译坐标地址信息为 null，latitude:%s, lontitude:%s",
	// locate.getLatitude(),
	// locate.getLongitude());
	// ErrLogUtils.uploadErrLog(context, msg);
	// Log.e(TAG, "GPS异常地址>>>Exception:" + msg);
	//
	// } else {
	// // 解析成功后，覆盖源数据
	// locate.setAddress(address);
	// LocateLogUtil.getIntance(context).updateLocatelog(
	// locate);
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// try {
	// CustomerHttpClient.executeGet(url);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
}
