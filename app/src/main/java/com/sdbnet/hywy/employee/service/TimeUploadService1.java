package com.sdbnet.hywy.employee.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.baidu.mapapi.utils.DistanceUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.db.db.DBManager;
import com.sdbnet.hywy.employee.listener.MyAmpListener;
import com.sdbnet.hywy.employee.location.GpsSatelliteListener;
import com.sdbnet.hywy.employee.location.LBSServiceListener;
import com.sdbnet.hywy.employee.location.MyLocation;
import com.sdbnet.hywy.employee.location.ScreenLockLocation;
import com.sdbnet.hywy.employee.model.WeatherModel;
import com.sdbnet.hywy.employee.net.AsyncHttpService;
import com.sdbnet.hywy.employee.ui.MainActivity;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.ErrLogUtils;
import com.sdbnet.hywy.employee.utils.LogUtil;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.ToastUtil;
import com.sdbnet.hywy.employee.utils.UtilsAndroid;
import com.sdbnet.hywy.employee.utils.UtilsBD;
import com.sdbnet.hywy.employee.utils.UtilsCommon;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimeUploadService1 extends Service implements
        AMapLocalWeatherListener {
    private static final String TAG = "TimingUpLocateService";

    public static final String ACTION_LOCATION = "com.sdbnet.hywy.employee.action.start.location";

    // private static String guardAction = "cn.hywy.center.guard";
    // public static final String ACTION_START_GUARDIAN_SPIRIT =
    // "com.android.hywy.action.demon";
    // private static final String SERVICE_GUARDIAN_SPIRIT =
    // "com.android.hywy.demon.DemonService";

    private static final int SERVICE_CHECK_INTERVAL = 60 * 1000; // 设置Server检测间隔时间

    private static final int MIN_DISTANCE = 15;
    private Thread watchThread = null;
    // private AlarmManager alarmManger;
    // private PendingIntent pi = null;
    // private TimerReceiver timerReceiver;
    private WakeLock wakeLock = null;

    // private Notification notification;
    private NotificationManager mNotifyManager;

    private List<MyLocation> mLocationList = new ArrayList<MyLocation>();
    private boolean isStartWatch;
    private int mAppUid;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "CoreService onCreate");
        mAppUid = UtilsAndroid.Set.getAppUid(this);
        initExecptionMsg();
        // initNotify();

        // startGpsService();

        openScreenLockLocatioin();

        // startGuardianSpirit();
        startWatchThread();

        initGeoCode();
        // initBDService();
        // startBDLocation();
        // initAmapLocation();
        startAmpThread();

    }

    public class LocalBinder extends Binder {
        public TimeUploadService1 getService() {
            Log.d(TAG, "getService");
            return TimeUploadService1.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind service");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind service");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind service");
    }

    // private void stopGuardianSpirit() {
    // Log.d(TAG, "stopGuardianSpirit");
    // // 停止守护精灵;
    // Intent service = new Intent(this, GuardianService.class);
    // stopService(service);
    // }

    private void initNotify() {

        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        PendingIntent p_intent = PendingIntent.getActivity(this, 0, intent,
                Notification.FLAG_ONGOING_EVENT);

        mBuilder.setContentTitle(getString(R.string.user_title))
                // 设置通知栏标题
                .setContentText(getString(R.string.notify_msg))
                        // 设置通知栏显示内容
                .setContentIntent(p_intent)
                .setTicker(getString(R.string.user_title)) // 通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setAutoCancel(true)// 设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)// ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                        // .setDefaults(Notification.DEFAULT_VIBRATE)//
                        // 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                        // Notification.DEFAULT_ALL Notification.DEFAULT_SOUND 添加声音 //
                        // requires VIBRATE permission
                .setSmallIcon(R.drawable.logo);// 设置通知小ICON

        Notification notification = mBuilder.build();
        mNotifyManager.notify(0x1982, notification);
        // startForeground(0x1982, notification);
    }

    /**
     * 开启锁屏定位，确保后台服务在用户锁屏后依然可用
     */
    private ScreenLockLocation mScreenLocak;

    private void openScreenLockLocatioin() {
        if (mScreenLocak == null) {
            mScreenLocak = ScreenLockLocation.getInstance().init(
                    TimeUploadService1.this, new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            Log.i(TAG, "wakeup screenLock");
                            if (!isRuningAmp()) {
                                Log.e(TAG, "AmapThread Die Restart...");
                                startAmpThread();
                            }
                        }
                    });
            mScreenLocak.start();
        } else if (!mScreenLocak.isStartScrrenLock()) {
            mScreenLocak.start();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, " onStartCommand");
        // acquireWakeLock();

        return START_STICKY;
    }

    public void startWatchThread() {
        isStartWatch = true;
        if (null == watchThread) {
            // 开启线程，监视服务运行状态
            watchThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    while (isStartWatch) {
                        try {
                            // 监控守护精灵运行状态
                            checkAndWakeUpService(GuardianService.class);
                            // 休眠
                            Thread.sleep(SERVICE_CHECK_INTERVAL);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
            watchThread.start();
        } else if (!watchThread.isAlive()) {
            watchThread.start();
        }
    }

    public void stopWatchThread() {
        isStartWatch = false;
        watchThread = null;
    }

    @Override
    public void onDestroy() {
        // releaseWakeLock();
        Log.e(TAG, "CoreService onDestroy");
        stopWatchThread();
        // stopGuardianSpirit();
        abortLocation();
        clearNotify();
        super.onDestroy();
    }

    private void clearNotify() {
        if (mNotifyManager != null) {
            // mNotifyManager.cancel(id);
            mNotifyManager.cancelAll();
        }
    }

    /**
     * 检查Service运行状态
     *
     * @param serviceName
     * @param action
     */
    private void checkAndWakeUpService(Class<?> clazz) throws Exception {
        // 检查Service状态
        boolean isRunning = UtilsAndroid.Set.isRunningService(this, clazz);
        Log.e("checkAndWakeUpService", " service isRunning=" + isRunning + ","
                + clazz.getSimpleName());
        if (!isRunning) {
            Intent service = new Intent(this, clazz);
            service.setAction(GuardianService.ACTION_GUARDIAN);
            startService(service);
        }
    }

    /**
     * Amap Location
     */
    // public static final int LOCATION_INTERVAL = 60 * 1000;// Test
    public static final int LOCATION_INTERVAL = 5 * 60 * 1000;//
    private IReceiveLocationHandler locationHandler;
    private int DELAY_TIME = DELAY_TIME_MIDDLE;
    // private static final int DELAY_TIME_HIGH = 10 * 1000;
    private static final int DELAY_TIME_MIDDLE = 5 * 1000;
    // private static final int DELAY_TIME_LOW = 1000;

    private LocationManagerProxy mLocationManagerProxy;
    private String amapLocaType = LocationProviderProxy.AMapNetwork;
    private Thread mAmapThread;
    private boolean isSartAmpThread;

    private final int MSG_AGAIN_REQUEST_LOCATION = 11;
    private final int MSG_AGAIN_REQUEST_BD_LOCATION = 21;
    private final int MSG_AGAIN_REQUEST_AMAP_LOCATION = 31;
    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AGAIN_REQUEST_LOCATION:
                    requestLocation();
                    break;
                case MSG_AGAIN_REQUEST_BD_LOCATION:
                    // requestBDLocation();
                    break;
                case MSG_AGAIN_REQUEST_AMAP_LOCATION:
                    requestAmapLocation();
                    break;
                default:
                    break;
            }

            return false;
        }
    });
    private BroadcastReceiver mGPSLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 接受广播
            if (intent.getAction().equals(GPSLOCATION_BROADCAST_ACTION)) {
                Bundle bundle = intent.getExtras();
                // 获取bundle里的数据
                Parcelable parcelable = bundle
                        .getParcelable(LocationManagerProxy.KEY_LOCATION_CHANGED);
                Location location = (Location) parcelable;
                // Geocoder geocoder=new Geocoder(TimingUpLocateService.this);
                Log.e("mGPSLocationReceiver", location + "");
                if (location == null) {
                    if (UtilsAndroid.Set
                            .checkNetState(TimeUploadService1.this)) {
                        requestAmapLocation(LocationProviderProxy.AMapNetwork);
                    } else {
                        callBackFail();
                    }
                } else {
                    // getAmapGeoResult(location.getLatitude(),
                    // location.getLongitude());
                    LatLng latlng = UtilsBD.convertToBD(location.getLatitude(),
                            location.getLongitude(), CoordType.GPS);
                    getGeoAddress(latlng);
                }

            }

        }
    };
    /**
     * 初始化定位
     */
    public static final String GPSLOCATION_BROADCAST_ACTION = "com.location.apis.gpslocationdemo.broadcast";
    private PendingIntent mPendingIntent;

    private void initAmapLocation() {
        mLocationManagerProxy = LocationManagerProxy.getInstance(this);
        mLocationManagerProxy.setGpsEnable(true);

        IntentFilter fliter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        fliter.addAction(GPSLOCATION_BROADCAST_ACTION);
        registerReceiver(mGPSLocationReceiver, fliter);
        Intent intent = new Intent(GPSLOCATION_BROADCAST_ACTION);
        mPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0,
                intent, 0);
        // mLocationManagerProxy = LocationManagerProxy.getInstance(this);
        // mLocationManagerProxy.requestLocationUpdates(
        // LocationManagerProxy.GPS_PROVIDER, -1, 15, mPendingIntent);

    }

    public void startAmpThread() {
        isSartAmpThread = true;
        if (mAmapThread == null) {
            mAmapThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    while (isSartAmpThread) {
                        if (mLocationManagerProxy == null) {
                            initAmapLocation();
                        }

                        requestLocation();
                        try {
                            Thread.sleep(LOCATION_INTERVAL);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        if (!mAmapThread.isAlive()) {
            mAmapThread.start();
        }

    }

    public boolean isRuningAmp() {

        if (mAmapThread == null) {
            return false;
        } else if (mAmapThread.isAlive()) {
            return true;
        } else {
            return false;
        }
    }

    private void requestAmapLocation() {
        ToastUtil.show(this, "Request Amap =" + amapLocaType);
        if (mLocationManagerProxy == null) {
            initAmapLocation();
        }
        if (UtilsAndroid.Set.checkNetState(this)) {
            mLocationManagerProxy.requestLocationData(amapLocaType, -1,
                    MIN_DISTANCE, mAMapLocationListener);
            LogUtil.i(TAG, "requestLocation>>" + amapLocaType);
        } else {
            Toast.makeText(this, "网络未开启", Toast.LENGTH_LONG).show();
        }
    }

    private void requestAmapLocation(String type) {
        if (type.equals(LocationManagerProxy.GPS_PROVIDER)
                || type.equals(LocationManagerProxy.NETWORK_PROVIDER)
                || type.equals(LocationProviderProxy.AMapNetwork)) {
            amapLocaType = type;
        } else {
            amapLocaType = (LocationProviderProxy.AMapNetwork);
        }
        requestAmapLocation();
    }

    // public void awakenAmpThread(){
    // if(mAmapThread!=null){
    // mAmapThread.notify();
    // }
    // }
    // public void sleepAmapThread(long time){
    //
    // if(mAmapThread!=null){
    // try {
    // mAmapThread.sleep(time);
    // } catch (InterruptedException e) {
    // e.printStackTrace();
    // }
    // }
    // }
    private void requestLocation() {
        uploadOpearteLog();
        boolean workStatus = PreferencesUtil.getValue(
                PreferencesUtil.KEY_WORK_STATS,
                Constants.Value.WORK_STATUS_DEFAULT);
        if (!workStatus) {
            ToastUtil.show(this, "no work!");
            callBack(Constants.Location.ERROR_CODE_NO_WORK);
        } else {
            UtilsAndroid.Set.openWifi(this);
            if (!UtilsAndroid.Set.getGpsStatus(this)) {
                sendSetBroadCast(BROADCAST_SHOW_GPS_DIALOG);
            } else if (!UtilsAndroid.Set.checkNetState(this)) {
                sendSetBroadCast(BROADCAST_SHOW_NETWORK_DIALOG);
            }
            UtilsAndroid.Common.initTrafficStats(mAppUid);
            saveOpreateGps();
            // mLocationManagerProxy.requestLocationUpdates(
            // LocationManagerProxy.GPS_PROVIDER, -1, 15, mPendingIntent);
            if (!requestGpsLocation()) {
                requestAmapLocation(LocationProviderProxy.AMapNetwork);
            }
            // if (UtilsAndroid.Set.checkNetState(this)) {
            // mLocationManagerProxy.requestLocationData(amapLocaType, -1,
            // MIN_DISTANCE, mAMapLocationListener);
            // LogUtil.i(TAG, "requestLocation>>" + amapLocaType);
            // } else {
            // requestGpsLocation();
            //
            // }
        }

    }

    public void requestLocation(IReceiveLocationHandler receiveLocationHandler) {
        this.locationHandler = receiveLocationHandler;
        requestLocation();
    }

    public void stopReceiveLocation() {
        locationHandler = null;
    }

    public void stopAmpLocation() {
        // 移除定位请求
        if (mLocationManagerProxy != null) {
            mLocationManagerProxy.removeUpdates(mPendingIntent);
            mLocationManagerProxy.removeUpdates(mAMapLocationListener);
            // 销毁定位
            mLocationManagerProxy.destroy();
        }
        if (mGPSLocationReceiver != null)
            unregisterReceiver(mGPSLocationReceiver);
        isSartAmpThread = false;
        // mAmapThread=null;
        isStartWeatherThread = false;
        mLocationManagerProxy = null;
    }

    private void againRequestAmap() {
        Log.d(TAG, "againRequestAmap");
        ToastUtil.show(getApplicationContext(), "againRequestAmap");
        if (UtilsAndroid.Set.checkNetState(this)) {
            mHandler.sendEmptyMessageDelayed(MSG_AGAIN_REQUEST_AMAP_LOCATION,
                    DELAY_TIME);
        } else {
            callBack(Constants.Location.ERROR_CODE_NET_ERROR);
        }
    }

    private void checkAmapLocation(AMapLocation amapLocation) {
        ToastUtil.show(this, "checkAmapLocation");
        if (amapLocation == null) {
            againRequestAmap();
            return;
        }
        if (amapLocation.getAMapException() == null) {
            againRequestAmap();
            return;
        }
        LogUtil.d(TAG, amapLocation + ";" + amapLocation.getAMapException());
        int errorCode = amapLocation.getAMapException().getErrorCode();
        if (errorCode == 0) {
            // 获取位置信息 // 定位成功回调信息，设置相关消息
            if (amapLocation.getProvider().equals(
                    LocationProviderProxy.AMapNetwork)) {
                LatLng latlng = UtilsBD.convertToBD(amapLocation.getLatitude(),
                        amapLocation.getLongitude(), CoordType.COMMON);
                upLocationData(latlng.longitude, latlng.latitude,
                        getAddress(amapLocation.getAddress()));
                sendCityBroadcast(amapLocation.getCity());
            } else if (amapLocation.getProvider().equals(
                    LocationManagerProxy.GPS_PROVIDER)) {
                LatLng latlng = UtilsBD.convertToBD(amapLocation.getLatitude(),
                        amapLocation.getLongitude(), CoordType.GPS);
                getGeoAddress(latlng);
            } else if (amapLocation.getProvider().equals(
                    LocationManagerProxy.NETWORK_PROVIDER)) {
                // 定位成功回调信息，设置相关消息
                LatLng latlng = UtilsBD.convertToBD(amapLocation.getLatitude(),
                        amapLocation.getLongitude(), CoordType.COMMON);
                upLocationData(latlng.longitude, latlng.latitude,
                        getAddress(amapLocation.getAddress()));
                sendCityBroadcast(amapLocation.getCity());
            } else {
                againRequestAmap();
            }
        } else {
            showAmapError(errorCode);
            callBackFail();
            ErrLogUtils.uploadErrLog(this, "Amap error code=" + errorCode);
            Log.e(TAG, "Location ERR:" + errorCode);
        }

    }

    private MyAmpListener mAMapLocationListener = new MyAmpListener() {

        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            checkAmapLocation(amapLocation);
        }

    };

    public void requestWeather() {
        if (mLocationManagerProxy == null) {
            initAmapLocation();
        }

        // 获取实时天气预报
        mLocationManagerProxy.requestWeatherUpdates(
                LocationManagerProxy.WEATHER_TYPE_LIVE, this);
    }

    private Thread mWeatherThread;
    private static final int WEATHER_INTERVAL = 60 * 60 * 1000;
    private boolean isStartWeatherThread;

    public void startWeatherThread() {
        isStartWeatherThread = true;
        mWeatherThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (isStartWeatherThread) {
                    requestWeather();
                    try {
                        Thread.sleep(WEATHER_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        ErrLogUtils.uploadErrLog(getApplicationContext(), e);
                    }
                }
            }
        });
        mWeatherThread.start();
    }

    /**
     * 停止定位服务
     */
    public void abortLocation() {
        stopReceiveLocation();
        stopAmpLocation();
        stopBDLocation();
        stopGpsService();

        stopScreenLock();// ScreenLock 是否停止（待定）

    }

    private void stopScreenLock() {
        if (mScreenLocak != null) {
            mScreenLocak.stop();
        }
        mScreenLocak = null;
    }

    public void stopBDLocation() {
        // 停止Geo
        try {
            if (mGeoCoder != null)
                mGeoCoder.destroy();
        } catch (Exception e) {
            Log.e(TAG, "GeoCoder 异常  在abortLocation()");
            e.printStackTrace();
            ErrLogUtils.uploadErrLog(getApplicationContext(), e);
        }
        if (locationClient != null) {
            locationClient.stop();
        }
        locationClient = null;
        locationListener = null;

    }

    // /**
    // * 设置定位接收回调
    // *
    // * @param handler
    // */
    // public void setReceLocationHandler(IReceiveLocationHandler handler) {
    // locationHandler = null;
    // locationHandler = handler;
    // }

    private void sendCityBroadcast(String newCity) {
        // 从定位信息中获取当前所在城市，便于获取天气信息
        String oldCity = PreferencesUtil.getValue(PreferencesUtil.KEY_CITY, "");
        if (!TextUtils.isEmpty(newCity) && !newCity.equals(oldCity)) {
            PreferencesUtil.putValue(PreferencesUtil.KEY_CITY, newCity);
            Intent intent = new Intent();
            intent.setAction(Constants.Action.ACTION_CITY_CHANGED);
            this.sendBroadcast(intent);
        }
    }

    private void sendWeatherBroadcast(WeatherModel weatherModel) {

        PreferencesUtil.putValue(PreferencesUtil.KEY_CITY, weatherModel.city);
        Intent intent = new Intent();
        intent.putExtra(Constants.IntentData.INTENT_WEATHER_BEAN, weatherModel);
        intent.setAction(Constants.Action.ACTION_WEATHER_DATA);
        this.sendBroadcast(intent);

    }

    private void callBack(MyLocation location) {
        // 回调数据
        // 获取定位后的回调接口，供订单提交时上传定位信息使用
        if (locationHandler != null) {
            locationHandler.onReceiveHandler(location);
            locationHandler = null;
        }

    }

    private void callBack(int errorType) {
        MyLocation location = new MyLocation();
        location.errorCode = errorType;
        callBack(location);
    }

    private void callBackFail() {
        if (locationHandler != null) {
            locationHandler.onReceiveFail();
            locationHandler = null;
        }

    }

    // private void callBack(boolean flag) {
    // if (locationHandler != null) {
    // locationHandler.onReceiveFail();
    // }
    // }

    private boolean checkErrorCode(JSONObject response) {
        int errCode;
        try {
            errCode = response.getInt(Constants.Feild.KEY_ERROR_CODE);

            String msg = response.getString(Constants.Feild.KEY_MSG);
            if (errCode == 0) {
                return true;
            } else if (errCode == 41 || errCode == 42) {
                sendSetBroadCast(BROADCAST_SHOW_EXIT_DIALOG, msg);
            } else {
                Log.e(TAG, "isErrorCode msg=" + msg);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ErrLogUtils.uploadErrLog(TimeUploadService1.this, (e));
            return false;
        }
        return false;
    }

    /**
     * 上传定位信息
     *
     * @param location
     */
    public void upLocationData(final MyLocation location) {
        LogUtil.d(TAG, "MyLocation=" + location.toString());
        ToastUtil.show(
                getApplicationContext(),
                location.address + "," + getTime() + "\n"
                        + UtilsAndroid.Common.getTrafficStats(mAppUid));

        // addLocation(location);
        // saveLocateLog(location);
        if (locationHandler != null) {
            callBack(location);
            return;
        }

        if (!UtilsCommon.checkAccount()
                || !UtilsAndroid.Set.checkNetState(this)) {
            saveLocationLog(location);
            return;
        }

        AsyncHttpService.uploadLocation(location,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                        ToastUtil.show(
                                getApplicationContext(),
                                R.string.network_connect_timeout
                                        + "\n"
                                        + UtilsAndroid.Common
                                        .getTrafficStats(mAppUid));
                        saveLocationLog(location);

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        LogUtil.d(response.toString());
                        try {
                            if (checkErrorCode(response)) {
                                ToastUtil
                                        .show(TimeUploadService1.this,
                                                "Location upload success!"
                                                        + "\n"
                                                        + UtilsAndroid.Common
                                                        .getTrafficStats(mAppUid));
                            } else {
                                saveLocationLog(location);
                                ErrLogUtils.uploadErrorCodeMsg(
                                        TimeUploadService1.this, location);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            saveLocationLog(location);
                            ErrLogUtils.uploadErrLog(
                                    TimeUploadService1.this,
                                    ErrLogUtils.toString(e));
                        }
                    }
                }, this);
    }

    private void upLocationData(final double longitude, final double latitude,
                                final String address) {
        upLocationData(new MyLocation(longitude, latitude, address,
                System.currentTimeMillis()));
    }

    public interface IReceiveLocationHandler {
        void onReceiveHandler(MyLocation location);

        void onReceiveFail();
    }

    /**
     * 百度地图反地理编码 获取地址信息
     *
     * @param latLng
     */
    private void getGeoAddress(final LatLng latLng) {

        if (UtilsAndroid.Set.checkNetState(this)) {
            boolean isReverseGeoCodeSuccess = mGeoCoder
                    .reverseGeoCode(new ReverseGeoCodeOption().location(latLng));

            ToastUtil.show(
                    this,
                    "Geo Reverse=" + isReverseGeoCodeSuccess + "\n" + latLng
                            + "\n"
                            + UtilsAndroid.Common.getTrafficStats(mAppUid));
            if (!isReverseGeoCodeSuccess) {
                callBack(Constants.Location.ERROR_CODE_REVERSE_GEO);
            }

        } else {
            callBack(Constants.Location.ERROR_CODE_NET_ERROR);
            saveLocationLog(latLng.longitude, latLng.latitude, "");
        }

    }

    private MyGeoListener mGeoListener;
    private GeoCoder mGeoCoder;

    private void initGeoCode() {
        mGeoCoder = GeoCoder.newInstance();
        mGeoListener = new MyGeoListener();
        mGeoCoder.setOnGetGeoCodeResultListener(mGeoListener);
    }

    private class MyGeoListener implements OnGetGeoCoderResultListener {

        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
            LogUtil.d(TAG, "GeoCodeResult>>" + geoCodeResult.getAddress() + ":"
                    + geoCodeResult.getLocation());
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseResult) {

            String address = reverseResult.getAddress();
            ToastUtil.show(TimeUploadService1.this,
                    "Geo reverse address=" + address + "\n"
                            + UtilsAndroid.Common.getTrafficStats(mAppUid));
            if (TextUtils.isEmpty(address)) {
                saveLocationLog(reverseResult.getLocation().longitude,
                        reverseResult.getLocation().latitude, "");
                callBack(Constants.Location.ERROR_CODE_REVERSE_GEO);
                String msg = String.format(TimeUploadService1.this
                                .getString(R.string.gps_location_empty_msg),
                        reverseResult.getLocation().latitude + "",
                        reverseResult.getLocation().longitude + "");
                ErrLogUtils.uploadErrLog(TimeUploadService1.this, msg);
                Log.e(TAG, "GPS异常地址>>>Exception:" + msg);
            } else {
                // MyLocation location = new MyLocation(
                // latLng.longitude, latLng.latitude, address);
                upLocationData(reverseResult.getLocation().longitude,
                        reverseResult.getLocation().latitude, address);
                sendCityBroadcast(reverseResult.getAddressDetail().city);
                LogUtil.d(TAG,
                        "GetReverseGeoCodeResult>>" + address + ":"
                                + reverseResult.getLocation() + ":"
                                + reverseResult.getAddressDetail().city);
            }

        }
    }

    private void saveLocationLog(MyLocation location) {
        saveLocationLog(location.longitude, location.latitude, location.address);
    }

    private void saveLocationLog(double longitude, double latitude,
                                 String address) {
        DBManager manager = new DBManager(this);
        manager.saveLocateLog(longitude, latitude, address);
        manager.closeDatabase();
    }

    // public void testData() {
    // saveLocationLog(113.723829, 41.136418, "");
    // saveLocationLog(113.723829, 37.136418, "");
    // saveLocationLog(113.723829, 33.136418, "");
    // saveLocationLog(113.723829, 30.136418, "");
    // saveLocationLog(113.723829, 27.136418, "");
    // saveLocationLog(113.723829, 23.136418, "");
    //
    // // saveLocationLog(116.417492, 39.908515,"");
    // // saveLocationLog(115.264564, 39.429982,"");
    // // saveLocationLog(114.473481, 38.903651,"");
    // // saveLocationLog(113.18567, 38.871298,"");
    // // saveLocationLog(112.671251, 37.784379,"");
    // // saveLocationLog(111.912363, 36.614383,"");
    // // saveLocationLog(110.013548, 35.872951,"");
    // // saveLocationLog(109.79738, 34.380389,"");
    // // saveLocationLog(108.79738, 34.380389,"");
    //
    // // 116.417492,39.908515
    // // 116.264564,39.429982
    // // 115.473481,38.903651
    // // 114.18567,38.871298
    // // 115.671251,37.784379
    // // 114.912363,36.614383
    // // 115.013548,35.872951
    // // 114.79738,34.380389
    // // 114.79738,34.380389
    // }

    // //////////////////////////////////////////////////////////
    private LocationClient locationClient;
    private MyLocationListener locationListener;
    private BDLocation bdLocation;
    private LocationMode tempMode = LocationMode.Hight_Accuracy;
    private String tempcoor = "gcj02";//BDGeofence.COORD_TYPE_GCJ;//

    private void initBDService() {

        Log.i(TAG, "iniBDService");
        //
        // 高精度LocationMode.Hight_Accuracy，低功耗LocationMode.Battery_Saving，LocationMode.Device_Sensors
//		tempMode = LocationMode.Hight_Accuracy;
        // BDGeofence. COORD_TYPE_BD09 坐标类型bd09（百度摩卡托坐标）
        // BDGeofence.COORD_TYPE_BD09LL 坐标类型bd09ll（百度经纬度坐标，可以在百度系统产品直接使用）
        // BDGeofence. COORD_TYPE_GCJ 坐标类型gcj02（国测局加密经纬度坐标
//		tempcoor = BDGeofence.COORD_TYPE_BD09LL;// "gcj02";

        locationClient = new LocationClient(this);
        locationListener = new MyLocationListener();
        locationClient.registerLocationListener(locationListener);

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setLocationMode(tempMode);// 设置定位模式
        option.setCoorType(tempcoor);// 返回的定位结果是百度经纬度，默认值gcj02
        // option.setScanSpan(5 * 1000);// 设置间隔时间小于1000ms，让它不自动上传定位
        option.setScanSpan(100);// 设置间隔时间小于1000ms，让它不自动上传定位
        option.setIsNeedAddress(true);
        locationClient.setLocOption(option);
    }

    /**
     * 判断百度定位服务是否开启
     *
     * @return
     */
    public boolean isBDLocationStarted() {
        return (locationClient != null && locationClient.isStarted());
    }

    private int requestBDLocation() {
        int result;
        if (locationClient == null) {
            initBDService();
        }
        if (isBDLocationStarted()) {
            result = locationClient.requestLocation();
        } else {
            locationClient.start();
            result = -1;
        }
        return result;
    }

    /**
     * 0：离线定位请求成功 1:service没有启动 2：无监听函数 6：两次请求时间太短 -1:起动客户端/zhangyu 请求BD定位
     */

    public int requestBDLocation(IReceiveLocationHandler callBack) {
        this.locationHandler = callBack;
        return requestBDLocation();

    }

    public void startBDLocation() {
        if (locationClient == null) {
            initBDService();
        }
        if (!locationClient.isStarted()) {
            locationClient.start();
        }
    }

    private void againRequestBD() {
        if (UtilsAndroid.Set.checkNetState(this)) {
            mHandler.sendEmptyMessageDelayed(MSG_AGAIN_REQUEST_BD_LOCATION,
                    DELAY_TIME);
        } else {
            Toast.makeText(this, getString(R.string.httpError),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkBDLocation(BDLocation location) {
        boolean flag = false;
        if (location == null) {
            againRequestBD();
        } else {
            if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                // TypeNetWorkLocation// 定位结果描述：网络定位结果
                flag = true;
            } else if (location.getLocType() == BDLocation.TypeGpsLocation) {
                // TypeGpsLocation// 定位结果描述：GPS定位结果
                flag = true;
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                // TypeOffLineLocation// 定位结果描述：离线定位结果
                flag = true;
            } else if (location.getLocType() == BDLocation.TypeNone) {
                // TypeNone// 定位结果描述：无效定位结果
                againRequestBD();
            } else if (location.getLocType() == BDLocation.TypeOffLineLocationFail) {
                // TypeOffLineLocationFail// 定位结果描述：离线定位失败
                againRequestBD();
            } else if (location.getLocType() == BDLocation.TypeOffLineLocationNetworkFail) {
                // TypeOffLineLocationNetworkFail// 定位结果描述：网络请求失败,基站离线定位结果
                againRequestBD();
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                // TypeNetWorkException// 定位结果描述：网络连接失败
                ToastUtil.show(getApplicationContext(), "网络连接失败");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                // TypeServerError// 定位结果描述：server定位失败，没有对应的位置信息
                ToastUtil.show(getApplicationContext(), "定位失败");
            } else {
                againRequestBD();
            }

        }
        return flag;
    }

    /**
     * 百度定位监听
     *
     * @author Administrator
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            LogUtil.d(
                    TAG,
                    "\nBDLocation=" + location + ">>\t"
                            + UtilsBD.getLocationData(location));
            if (!checkBDLocation(location)) {
                return;
            }
            bdLocation = location;

            if (TextUtils.isEmpty(location.getAddrStr())) {
                // 将硬件common坐标转换为百度坐标
                LatLng latlng = UtilsBD.convertToBD(location.getLatitude(),
                        location.getLongitude(), CoordType.COMMON);
                getGeoAddress(latlng);
            } else {
                // 上传定位
                upLocationData(location.getLongitude(), location.getLatitude(),
                        location.getAddrStr());
                sendCityBroadcast(location.getCity());
            }
            // UtilsAndroid.Sdcard.appendFiledata(UtilsBD.location2Json(
            // TimingUpLocateService.this, location, locationType)
            // + "\t\n\n");
        }
    }

    /**
     * gps 定位
     */
    // 30000ms --minimum time interval between location updates, in milliseconds
    private final long GPS_MIN_TIME = 60 * 1000;
    // 最小变更距离 10m --minimum distance between location updates, in meters
    private final float GPS_MIN_DISTANCE = 10;
    private LBSServiceListener lbsLocationListener;
    private GpsSatelliteListener gpsSatelliteListener;
    private Location gpsLocation;
    private LocationManager locationManager;
    private String provider = LocationManager.GPS_PROVIDER;
    private GpsStatus gpsStatus;
    private boolean isGpsLocationStart = false;

    public void startGpsService() {
        ToastUtil.show(this, "start gps service");
        // UtilsAndroid.Set.openGPSSettings(this);

        // 获取系统硬件gps定位服务.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            locationManager = (LocationManager) this
                    .getSystemService(Context.LOCATION_SERVICE);
        }
        if (lbsLocationListener == null)
            lbsLocationListener = new LBSServiceListener();
        if (gpsSatelliteListener == null)
            gpsSatelliteListener = new GpsSatelliteListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                GPS_MIN_TIME, GPS_MIN_DISTANCE, lbsLocationListener);
        locationManager.addGpsStatusListener(gpsSatelliteListener);

        isGpsLocationStart = true;
        Log.i(TAG, "startGpsService");
    }

    /**
     * 请求GPS定位
     */
    public boolean requestGpsLocation() {

        if (locationManager == null || lbsLocationListener == null
                || gpsSatelliteListener == null || !isGpsLocationStart) {
            startGpsService();
            // return false;
        }

        // UtilsAndroid.Set.openGPSSettings(this);
        // 通过GPS获取位置LocationManager.GPS_PROVIDER
        gpsLocation = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        gpsStatus = locationManager.getGpsStatus(null);
        if (gpsLocation != null) {
            LatLng latlng = UtilsBD.convertToBD(gpsLocation.getLatitude(),
                    gpsLocation.getLongitude(), CoordType.GPS);
            Log.i(TAG, "requestGpsLocation>>" + latlng);
            getGeoAddress(latlng);
            // upLocationData(latlng.longitude, latlng.latitude, "");
            return true;
        } else {
            // stopGpsService();
            // startGpsService();
            // callBackFail();
            ToastUtil.show(this, "GPS Fail");
            Log.e(TAG, "onReceiveLocation:"
                    + "\n硬件gps未获取到有效定位信息，gpsLocation is null.");
            return false;
        }

    }

    public void stopGpsService() {
        if (locationManager != null && lbsLocationListener != null) {
            locationManager.removeUpdates(lbsLocationListener);
        }

        if (locationManager != null && gpsSatelliteListener != null) {
            locationManager.removeGpsStatusListener(gpsSatelliteListener);
        }
        Log.i(TAG, "in stopGpsService method.");
        isGpsLocationStart = false;
    }

    // 获取天气数据;
    @Override
    public void onWeatherForecaseSearched(AMapLocalWeatherForecast arg0) {

    }

    @Override
    public void onWeatherLiveSearched(AMapLocalWeatherLive aMapLocalWeatherLive) {

        if (aMapLocalWeatherLive != null
                && aMapLocalWeatherLive.getAMapException().getErrorCode() == 0) {
            WeatherModel weatherModel = new WeatherModel();

            // 天气预报成功回调 设置天气信息
            weatherModel.city = (aMapLocalWeatherLive.getCity());
            weatherModel.weather = (aMapLocalWeatherLive.getWeather());
            weatherModel.temp = (aMapLocalWeatherLive.getTemperature());// "℃");
            weatherModel.windDir = (aMapLocalWeatherLive.getWindDir());// "风");
            weatherModel.windPower = (aMapLocalWeatherLive.getWindPower());// "级");
            weatherModel.humidity = (aMapLocalWeatherLive.getHumidity());// "%");
            weatherModel.time = (aMapLocalWeatherLive.getReportTime());
            sendWeatherBroadcast(weatherModel);
        } else {
            // 获取天气预报失败
            ToastUtil
                    .show(getApplicationContext(), "Get weather failed:"
                            + aMapLocalWeatherLive.getAMapException()
                            .getErrorMessage());

        }
    }

    private static final int SPEED_MIN = 5;
    private static final int SPEED_MAX = 40;
    private static final int SPEED_TOP = 400;

    private void addLocation(MyLocation location) {
        int size = mLocationList.size();
        LatLng latLng = new LatLng(location.latitude, location.longitude);
        if (size > 0) {
            int timeInterval = (int) ((location.time - mLocationList
                    .get(size - 1).time) / 1000);
            LatLng lastLatlng = new LatLng(
                    mLocationList.get(size - 1).latitude,
                    mLocationList.get(size - 1).longitude);
            double distance = DistanceUtil.getDistance(latLng, lastLatlng);
            if (distance > timeInterval * SPEED_TOP) {
                ToastUtil.show(this, "火箭＝" + distance);
            } else if (distance < timeInterval * SPEED_MIN) {
                ToastUtil.show(this, "距离太短＝" + distance);
            } else if (distance > timeInterval * SPEED_MAX) {
                ToastUtil.show(this, "距离太远＝" + distance);
            } else {
                mLocationList.add(location);
            }
        }

        if (mLocationList.size() >= 100) {
            mLocationList.clear();
            mLocationList.add(location);
        }
    }

    // ////////////////////////////////////////////////

    private long lastTime;

    private int getTime() {
        long time = System.currentTimeMillis();
        int t = (int) ((time - lastTime) / 1000);
        lastTime = time;
        return t;
    }

    private SparseArray<String> execptionMap;

    private void initExecptionMsg() {
        execptionMap = new SparseArray<String>();

        execptionMap.put(0, "正常 amp");
        // 0 正常
        execptionMap.put(21, "IO 操作异常 amp");
        // 21 IO 操作异常
        execptionMap.put(22, "连接异常 amp");
        // 22 连接异常
        execptionMap.put(23, "连接超时 amp");
        // 23 连接超时
        execptionMap.put(24, "无效的参数 amp");
        // 24 无效的参数
        execptionMap.put(25, "空指针异常 amp");
        // 25 空指针异常
        execptionMap.put(26, "url 异常 amp");
        // 26 url 异常
        execptionMap.put(27, "未知主机 amp");
        // 27 未知主机
        execptionMap.put(28, "服务器连接失败 amp");
        // 28 服务器连接失败
        execptionMap.put(29, "协议解析错误 amp");
        // 29 协议解析错误
        execptionMap.put(30, "http 连接失败 amp");
        // 30 http 连接失败
        execptionMap.put(31, "未知的错误 amp");
        // 31 未知的错误
        execptionMap.put(32, "key 鉴权失败 amp");
        // 32 key 鉴权失败
        execptionMap.put(33, "获取基站/WiFi信息为空或失败 amp");
        // 33 获取基站/WiFi信息为空或失败
        execptionMap.put(34, "定位失败无法获取城市信息 amp");
        // 34 定位失败无法获取城市信息

    }

    private void showAmapError(int errorCode) {
        if (execptionMap.get(errorCode) != null)
            ToastUtil.show(getApplicationContext(), execptionMap.get(errorCode)
                    + "  (code=" + errorCode + ")");
        else
            ToastUtil.show(getApplicationContext(), "Unknow Error" + "  (code="
                    + errorCode + ")");
    }

    private GeocodeSearch geocoderSearch;
    private MyGeoAmapListener myGeoAmpListener;

    private void intiAmapGeo() {
        geocoderSearch = new GeocodeSearch(this);
        myGeoAmpListener = new MyGeoAmapListener();
    }

    private void getAmapGeoResult(double latitude, double longtitude) {
        if (geocoderSearch == null || myGeoAmpListener == null) {
            intiAmapGeo();
        }
        LatLonPoint point = new LatLonPoint(latitude, longtitude);

        geocoderSearch.setOnGeocodeSearchListener(myGeoAmpListener);
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(point, 50, GeocodeSearch.GPS);
        geocoderSearch.getFromLocationAsyn(query);

    }

    private class MyGeoAmapListener implements OnGeocodeSearchListener {

        @Override
        public void onGeocodeSearched(GeocodeResult arg0, int arg1) {

        }

        @Override
        public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
            String addressName;
            if (rCode == 0) {
                if (result != null
                        && result.getRegeocodeAddress() != null
                        && result.getRegeocodeAddress().getFormatAddress() != null) {
                    addressName = result.getRegeocodeAddress()
                            .getFormatAddress() + "附近";
                    ToastUtil.show(
                            TimeUploadService1.this,
                            addressName
                                    + "\n"
                                    + UtilsAndroid.Common
                                    .getTrafficStats(mAppUid));
                } else {
                    ToastUtil.show(TimeUploadService1.this, "无地址");
                }
            } else {
                ToastUtil.show(TimeUploadService1.this, "无效" + rCode);
            }
        }
    }

    public static final String BROADCAST_SHOW_GPS_DIALOG = "broadcast_show_gps_dialog";
    public static final String BROADCAST_SHOW_NETWORK_DIALOG = "broadcast_show_network_dialog";
    public static final String BROADCAST_SHOW_EXIT_DIALOG = "broadcast_show_eixt_dialog";
    public static final String BROADCAST_MSG_EXIT = "broadcast_msg_exit";

    private void sendSetBroadCast(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        sendBroadcast(intent);
    }

    private void sendSetBroadCast(String action, String msg) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(BROADCAST_MSG_EXIT, msg);
        sendBroadcast(intent);
    }

    private void saveOpreateGps() {
        boolean isOpenGps = UtilsAndroid.Set.getGpsStatus(this);
        int gpsStatus = PreferencesUtil.getValue(
                PreferencesUtil.KEY_GPS_STATUS, PreferencesUtil.STATUS_ON);
        if (isOpenGps) {
            if (gpsStatus == PreferencesUtil.STATUS_OFF) {
                saveOpreateLog("open gps");
            }
        } else {
            if (gpsStatus == PreferencesUtil.STATUS_ON) {
                saveOpreateLog("close gps");
            }
        }
        PreferencesUtil.putValue(PreferencesUtil.KEY_GPS_STATUS,
                isOpenGps ? PreferencesUtil.STATUS_ON
                        : PreferencesUtil.STATUS_OFF);
    }

    private void saveOpreateLog(String opreate) {
        DBManager manager = new DBManager(this);
        manager.saveOperate(opreate);
        manager.closeDatabase();
    }

    // 最多50个length 地址
    private String getAddress(String address) {
        if (TextUtils.isEmpty(address)) {
            return "";
        }
        int length = address.length();
        if (length <= 50) {
            return address;
        } else {
            return address.substring(0, 50);
        }

    }

    private void uploadOpearteLog() {
        if (UtilsAndroid.Set.isWifiDataEnable(this)) {
            UtilsCommon.operateLogUpload(this);
        }
    }

    private void startTimer() {

    }
}