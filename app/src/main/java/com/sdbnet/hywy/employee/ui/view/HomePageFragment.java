package com.sdbnet.hywy.employee.ui.view;

import java.util.HashMap;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.db.db.DBCityHelper;
import com.sdbnet.hywy.employee.model.WeatherModel;
import com.sdbnet.hywy.employee.net.AsyncHttpService;
import com.sdbnet.hywy.employee.ui.MainActivity;
import com.sdbnet.hywy.employee.ui.OrderActionActivity;
import com.sdbnet.hywy.employee.ui.base.BaseFrament;
import com.sdbnet.hywy.employee.ui.widget.DialogLoading;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.ErrLogUtils;
import com.sdbnet.hywy.employee.utils.LogUtil;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.ToastUtil;
import com.sdbnet.hywy.employee.utils.UtilsAndroid;
import com.sdbnet.hywy.employee.utils.UtilsCommon;
import com.sdbnet.hywy.employee.utils.UtilsError;

public class HomePageFragment extends BaseFrament implements OnClickListener {
	private static final String TAG = "HomePageFragment";
	private static final int WORK_ON = 1;
	private static final int WORK_OFF = 0;

	private RelativeLayout mRlWeatherBg;
	private TextView mTextWeather;
	private TextView mTextCity;
	private TextView mTextTemp;

	private TextView mTextJokeNext;
	private LinearLayout mLlWorkStatus;
	private LinearLayout mLlOrderScan;
	private TextView mTextWorkMsg;
	private TextView mTextWorkStats;
	private ImageView mImgWorkStats;

	private BroadcastReceiver receiver;
	private boolean isGetCity;
	private boolean firstGetIn = true;
	private DialogLoading mDialogUpdate;
	private View view;

	private HashMap<String, Integer> weatherMap;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_homepage, null);
		getPermission();
		initWeatherData();
		registeBroadCastReceiver();
		initControls();

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		// bindService();
	}

	/**
	 * 接收定位城市广播
	 */
	private void registeBroadCastReceiver() {
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(
						Constants.Action.ACTION_CITY_CHANGED)) {
					// 根据后台定位到的城市获取天气
					MyTask task = new MyTask();
					String[] param = new String[] { "" };
					task.execute(param);
				} else {
					WeatherModel weather = (WeatherModel) intent
							.getSerializableExtra(Constants.IntentData.INTENT_WEATHER_BEAN);
					mTextTemp.setText(weather.temp + "℃");
					mTextWeather.setText(weather.weather);
					mTextCity.setText(weather.city);
				}

			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.Action.ACTION_CITY_CHANGED);
		filter.addAction(Constants.Action.ACTION_WEATHER_DATA);
		getActivity().registerReceiver(receiver, filter);
	}

	/**
	 * 初始化控件
	 */
	private void initControls() {

		mRlWeatherBg = (RelativeLayout) view
				.findViewById(R.id.fragment_home_page_weahter_bg_rl);
		mTextWeather = (TextView) view
				.findViewById(R.id.fragment_home_tv_weather_desc);
		mTextCity = (TextView) view
				.findViewById(R.id.fragment_home_tv_weather_city);
		mTextTemp = (TextView) view
				.findViewById(R.id.fragment_home_tv_weather_temp);
		mTextJokeNext = (TextView) view
				.findViewById(R.id.fragment_home_tv_next);
		mTextWorkStats = (TextView) view
				.findViewById(R.id.fragment_home_tv_work_stats);
		mTextWorkMsg = (TextView) view
				.findViewById(R.id.fragment_home_tv_work_stats_change);
		mImgWorkStats = (ImageView) view
				.findViewById(R.id.fragment_home_iv_work_stats);
		mLlWorkStatus = (LinearLayout) view
				.findViewById(R.id.fragment_home_lay_info);
		mLlOrderScan = (LinearLayout) view
				.findViewById(R.id.fragment_home_lay_order_action);

		mTextJokeNext.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

		setWorkStats(PreferencesUtil.getValue(PreferencesUtil.KEY_WORK_STATS,
				Constants.Value.WORK_STATUS_DEFAULT));
		mDialogUpdate = new DialogLoading(getActivity(),
				getString(R.string.being_updated_please_later));

		mLlOrderScan.setOnClickListener(this);
		Log.d(TAG, "workModel=" + mWorkModel);
		if (TextUtils.equals(mWorkModel, Constants.Value.STATUS_OK))
			mLlWorkStatus.setOnClickListener(this);
		else {
			mLlWorkStatus.setClickable(false);
			PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS,
					Constants.Value.WORKING);
			PreferencesUtil.putValue(PreferencesUtil.KEY_IS_WORKING, WORK_ON);
			mTextWorkMsg.setTextColor(Color.GRAY);
			setWorkStats(true);
		}
	}

	private void showDialogWork(String title, String message,
			final boolean isWorking) {
		if (!UtilsAndroid.Set.checkNetState(getActivity())) {
			Toast.makeText(getActivity(), getString(R.string.httpError),
					Toast.LENGTH_SHORT).show();
			return;
		}
		String msg = isWorking ? getString(R.string.i_want_work_off)
				: getString(R.string.i_want_work_on);
		saveOperateLog(msg);
		new AlertDialog.Builder(getActivity())
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								modifyWorkStatus(isWorking);
							}

						}).setNegativeButton(android.R.string.cancel, null)
				.show();

	}

	private void modifyWorkStatus(final boolean isWorking) {
		if (!UtilsAndroid.Set.checkNetState(getActivity())) {
			Toast.makeText(getActivity(), getString(R.string.httpError),
					Toast.LENGTH_SHORT).show();
			return;
		}
		int iswork = !isWorking ? WORK_ON : WORK_OFF;
		AsyncHttpService.modifyWorkStatus(iswork + "",
				new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						super.onStart();

						mDialogUpdate.show();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						showMsg(R.string.httpisNull);
						mDialogUpdate.dismiss();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						LogUtil.d("info: " + response.toString());

						super.onSuccess(statusCode, headers, response);
						// Context context =
						// getActivity().getApplicationContext();
						try {
							if (UtilsError.isErrorCode(getActivity(), response)) {
								return;
							}
							PreferencesUtil.putValue(
									PreferencesUtil.KEY_WORK_STATS, !isWorking);
							PreferencesUtil.workstatus = !isWorking;
							PreferencesUtil.putValue(
									PreferencesUtil.KEY_IS_WORKING,
									isWorking ? WORK_OFF : WORK_ON);
							showMsg(R.string.work_status_changed);
							if (isWorking) {
								// OperateLogUtil.operateLogUpload(getActivity());
								((MainActivity) getActivity()).mLocateService
										.stopGpsService();
								UtilsCommon.operateLogUpload(getActivity());
							} else {
								((MainActivity) getActivity()).mLocateService
										.startGpsService();
							}
							((MainActivity) getActivity()).mLocateService
									.requestLocation(null);
							setWorkStats(!isWorking);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							mDialogUpdate.dismiss();
						}

					}

				}, getActivity());
	}

	@Override
	public void onResume() {
		if (!isGetCity) {
			// 未成功获取天气，则再次访问网络
			MyTask task = new MyTask();
			String[] param = new String[] { "" };
			task.execute(param);
		} else {
			// 已成功获取，则从直接缓存中读取
			String weather = PreferencesUtil.getValue(
					PreferencesUtil.KEY_WEATHER, null);
			parseWeather(weather);

		}

		super.onResume();
	}

	/**
	 * 切换上下班工作状态
	 * 
	 * @param isWorking
	 */
	private void setWorkStats(boolean isWorking) {
		Log.d(TAG, "isWorking=" + isWorking);
		Log.e(TAG, isWorking + "");
		if (isWorking) {
			mTextWorkStats.setText(getString(R.string.working));
			mTextWorkMsg.setText(getString(R.string.i_want_work_off));
			mImgWorkStats.setBackgroundResource(R.drawable.working);
			UtilsAndroid.Set.openWifi(getActivity());
			if (!UtilsAndroid.Set.checkNetState(getActivity())) {
				UtilsAndroid.Set.openNetwork(getActivity());
			}

			if (UtilsAndroid.Set.hasGPSDevice(getActivity())
					&& !UtilsAndroid.Set.getGpsStatus(getActivity())) {
				showGpsDialog(getActivity());
			}

			// if (firstGetIn) {
			// firstGetIn = false;
			// } else {
			// // LocationServiceUtils.requestLocation();
			// }
		} else {
			mTextWorkStats.setText(getString(R.string.worked));
			mTextWorkMsg.setText(getString(R.string.i_want_work_on));
			mImgWorkStats.setBackgroundResource(R.drawable.workoff);
		}
	}

	private class MyTask extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {
			return getWeatherSituation();
		}

		@Override
		protected void onPostExecute(String result) {
			// 解析天气json字符串
			parseWeather(result);
		}
	}

	/**
	 * 由城市码设置天气情况,并将得到的信息保存在文件中
	 * 
	 * @return
	 */
	private String getWeatherSituation() {
		String weather = null;
		String cityCode = PreferencesUtil.getValue(
				PreferencesUtil.KEY_CITY_CODE,
				PreferencesUtil.KEY_CITY_CODE_DAFULT);
		String cityName = PreferencesUtil
				.getValue(PreferencesUtil.KEY_CITY, "").replace("市", "");

		if (TextUtils.isEmpty(cityName)) {
			isGetCity = false;
			return weather;
		}
		LogUtil.d(TAG, "cityName: " + cityName);
		try {
			// 根据城市名称获取相应城市码
			DBCityHelper dbHelper = new DBCityHelper(getActivity(),
					"db_weather.db");
			cityCode = dbHelper.getCityCodeByName(cityName);
		} catch (Exception e) {
			ErrLogUtils.uploadErrLog(getActivity(), ErrLogUtils.toString(e));
			e.printStackTrace();
		}

		if (!TextUtils.isEmpty(cityCode)) {
			// 访问网络更新天气
			String weatherUrl = String.format(
					Constants.NetApi.WEATHER_DATA_API, cityCode);
			if (UtilsAndroid.Set.checkNetState(getActivity())) {
				weather = UtilsCommon.getWebContent(weatherUrl);
				LogUtil.d(TAG, "weatherUrl: " + weatherUrl + ",\n,weather="
						+ weather);
			}
			if (this.getActivity() == null) {
				return weather;
			}
			if (!TextUtils.isEmpty(weather)
					&& !weather
							.contains(getString(R.string.very_sorry_web_canot_open))) {
				PreferencesUtil.putValue(PreferencesUtil.KEY_WEATHER, weather);
				isGetCity = true;
			} else {
				isGetCity = false;
			}

		}
		return weather;
	}

	/**
	 * 解析JSON得到天气
	 * 
	 * @param result
	 * @throws JSONException
	 */
	private void parseWeather(String result) {
		if (!TextUtils.isEmpty(result)) {
			String city = "";
			String wendu = "";
			String weather = "";
			try {
				JSONObject json = new JSONObject(result).getJSONObject("data");
				// 得到城市
				city = json.getString("city");
				// 得到温度
				wendu = json.getString("wendu");
				// 得到天气
				JSONArray forecast = json.getJSONArray("forecast");
				weather = forecast.getJSONObject(0).getString("type");
			} catch (JSONException e) {
				ToastUtil.show(getActivity(), "解析天气出错");
				ErrLogUtils
						.uploadErrLog(getActivity(), ErrLogUtils.toString(e));
				e.printStackTrace();
				return;
			}

			mTextTemp.setText(wendu + "℃");
			mTextWeather.setText(weather);
			mTextCity.setText(city);
			setWeatherBg(weather);
			// 设置一个有效日期为5小时
			// long validTime = System.currentTimeMillis();
			// validTime = validTime + 5 * 60 * 60 * 1000;
		}
	}

	private void setWeatherBg(String weather) {
		if (weatherMap.get(weather) != null) {
			int rsid = weatherMap.get(weather);
			try {
				mRlWeatherBg.setBackgroundResource(rsid);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (weather.contains(getString(R.string.rain))) {
			mRlWeatherBg.setBackgroundResource(R.drawable.weather_rain);
		} else if (weather.contains(getString(R.string.snow))) {
			mRlWeatherBg.setBackgroundResource(R.drawable.weather_snow);
		} else if (weather.contains(getString(R.string.fog))) {
			mRlWeatherBg.setBackgroundResource(R.drawable.weather_fog);
		} else if (weather.contains(getString(R.string.hail))) {
			mRlWeatherBg.setBackgroundResource(R.drawable.weather_rain);
		} else {
			mRlWeatherBg.setBackgroundResource(R.drawable.weather_sunshine);
		}

	}

	private void initWeatherData() {
		weatherMap = new HashMap<String, Integer>();
		weatherMap.put(getString(R.string.light_rain), R.drawable.weather_rain);
		weatherMap.put(getString(R.string.rain), R.drawable.weather_rain);
		weatherMap.put(getString(R.string.heavy_rain),
				R.drawable.weather_heavy_rain);
		weatherMap.put(getString(R.string.thunder_shower),
				R.drawable.weather_thunder);
		weatherMap.put(getString(R.string.shower), R.drawable.weather_rain);
		weatherMap.put(getString(R.string.cloudy), R.drawable.weather_cloudy);
		weatherMap.put(getString(R.string.sunny), R.drawable.weather_sun);
		weatherMap.put(getString(R.string.yin), R.drawable.weather_yin);
		weatherMap.put(getString(R.string.fog), R.drawable.weather_fog);
		weatherMap.put(getString(R.string.hail), R.drawable.weather_snow);
		weatherMap.put(getString(R.string.light_snow), R.drawable.weather_snow);
		weatherMap.put(getString(R.string.snow), R.drawable.weather_snow);
		weatherMap.put(getString(R.string.heavy_snow), R.drawable.weather_snow);
	}

	// /**
	// * 启动守护精灵
	// */
	// private void startupDemonService() {
	// Intent service = new Intent();
	// service.setAction(TimingUpLocateService.ACTION_START_GUARDIAN_SPIRIT);
	// getActivity().startService(service);
	// }

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		// unBindService();
		getActivity().unregisterReceiver(receiver);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onClick(View v) {
		boolean isWorking = PreferencesUtil.getValue(
				PreferencesUtil.KEY_WORK_STATS,
				Constants.Value.WORK_STATUS_DEFAULT);
		switch (v.getId()) {
		case R.id.fragment_home_lay_order_action:
			saveOperateLog(getString(R.string.order_scan));
			if (isWorking) {
				Intent intent = new Intent(getActivity(),
						OrderActionActivity.class);
				getActivity().startActivity(intent);
			} else {
				// 下班状态不能操作
				showMsg(R.string.tip_work);
			}
			break;
		case R.id.fragment_home_lay_info:
			PreferencesUtil.putValue(PreferencesUtil.KEY_ERROR_LOG,
					Constants.Step.CHWK);
			// startupDemonService();
			// 切换上下班状态
			String title = "";
			String message = "";
			if (isWorking) {
				title = getString(R.string.work_off_tip);
				message = getString(R.string.pro_confirm_work_off);// "亲，确认下班吗？";
			} else {
				title = getString(R.string.work_on_tip);
				message = getString(R.string.pro_confirm_work_on);// "亲，确认上班吗？";
			}

			showDialogWork(title, message, isWorking);
			break;
		default:
			break;
		}
	}

	// private Dialog mUploadLoadDialog;

	// private void operateLogUpload() {
	//
	// try {
	// DBManager manager = new DBManager(getActivity());
	// List<OperateLog> list = manager.getAllOpreate();
	// manager.putLockOpreate();
	// manager.closeDatabase();
	//
	// if (null == list || list.size() == 0) {
	// showMsg(R.string.operation_log_upload_success);
	// return;
	// }
	//
	// Log.i(TAG, list.toString());
	// StringEntity stringEntity = null;
	// stringEntity = new StringEntity(list.toString(), "utf-8");
	// // Log.i(TAG, stringEntity.toString());
	//
	// AsyncHttpClient client = new AsyncHttpClient();
	// client.post(getActivity(), AsyncHttpService.BASE_URL
	// + "/upBatchOpeLog", stringEntity, "application/json",
	// new JsonHttpResponseHandler() {
	// @Override
	// public void onStart() {
	// super.onStart();
	// // mUploadLoadDialog.show();
	// }
	//
	// @Override
	// public void onSuccess(int statusCode, Header[] headers,
	// JSONObject response) {
	// super.onSuccess(statusCode, headers, response);
	// LogUtil.d(response.toString());
	// try {
	// int errCode = response
	// .getInt(Constants.Feild.KEY_ERROR_CODE);
	// if (errCode == 0) {
	// // showMsg(R.string.operation_log_upload_success);
	// DBManager manager = new DBManager(
	// getActivity());
	// manager.deleteAllOpreate();
	// manager.closeDatabase();
	// } else {
	// // showMsg(R.string.operation_log_upload_failed);
	// }
	// } catch (JSONException e) {
	// // showMsg(R.string.operation_log_upload_failed);
	// e.printStackTrace();
	// } finally {
	// // mUploadLoadDialog.dismiss();
	// }
	//
	// }
	//
	// @Override
	// public void onCancel() {
	// // mUploadLoadDialog.dismiss();
	// super.onCancel();
	//
	// }
	// });
	// } catch (Exception ex) {
	// showMsg(R.string.operation_log_upload_failed);
	// ex.printStackTrace();
	// }
	// }

	private String mWorkModel = Constants.Value.STATUS_OK;

	private void getPermission() {
		String options = PreferencesUtil.getValue(Constants.Feild.KEY_OPTIONS);
		if (TextUtils.isEmpty(options)) {
			return;
		}
		try {
			JSONObject jsonObject = new JSONObject(options);
			if (!jsonObject.isNull("workModel"))
				mWorkModel = jsonObject.getString("workModel");
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
