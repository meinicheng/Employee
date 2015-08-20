package com.sdbnet.hywy.employee;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.content.Context;
import android.os.StrictMode;
import android.graphics.Bitmap;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.frontia.FrontiaApplication;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.LogUtil;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.ToastUtil;

/**
 * 主Application
 */
public class MainApplication extends FrontiaApplication {

    public final static boolean DEVELOPER_MODE = true;

    public DisplayImageOptions options;

    @Override
    public void onCreate() {
        super.onCreate();

        PreferencesUtil.initialize(this);
        SDKInitializer.initialize(this);

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        setStyleBasic();
        // 初始化数据库
        // DBHelper.initialize(getApplicationContext());

        initImageLoader(this);

        initStrictMode();
        // 捕获全局异常
        uncaughtException();
        initDebug();
        initSdFile();
        // checkUpdate();

    }

    public static List<String> mAccounts = new ArrayList<String>();

    private void initDebug() {
        mAccounts.add("18802691909");
        // mAccounts.add("13632600403");
        ToastUtil.openShow();
        LogUtil.openLog();

    }

    private void initStrictMode() {
        if (DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectCustomSlowCalls().detectDiskReads()
                    .detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                    .penaltyLog().penaltyDeath().build());
        }
    }

    /**
     * 全局异常捕获
     */
    private void uncaughtException() {
        if (!DEVELOPER_MODE) {
            CrashHandler.getInstance().init(this);
        }

    }

    private void initImageLoader(Context context) {
        File cacheDir = StorageUtils.getIndividualCacheDirectory(context, Constants.SDBNET.PATH_PHOTO);//"UniversalImageLoader/Cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
//                .diskCache(new UnlimitedDiskCache(cacheDir)) // 你可以传入自己的磁盘缓存
                .diskCacheSize(50 * 1024 * 1024)
                        // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.no_photo)
                .showImageForEmptyUri(R.drawable.no_photo)
                .showImageOnFail(R.drawable.no_photo).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        // options = new DisplayImageOptions.Builder()
        // .showImageOnLoading(R.drawable.no_photo) // resource or
        // // drawable
        // .showImageForEmptyUri(R.drawable.no_photo) // resource or
        // // drawable
        // .showImageOnFail(R.drawable.no_photo) // resource or
        // // drawable
        // .resetViewBeforeLoading(false) // default
        // .delayBeforeLoading(1000).cacheInMemory(false) // default
        // .cacheOnDisk(false) // default
        // .considerExifParams(false) // default
        // .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
        // .bitmapConfig(Bitmap.Config.ARGB_8888) // default
        // .displayer(new SimpleBitmapDisplayer()) // default
        // .handler(new Handler()) // default
        // .build();
    }

    private void initSdFile() {
        File base = new File(Constants.SDBNET.BASE_PATH);
        if (!base.exists()) {
            base.mkdir();
        }
        File picFile = new File(Constants.SDBNET.PATH_PHOTO);

        if (!picFile.exists()) {
            picFile.mkdir();
        }
        File dir = new File(Constants.SDBNET.SDPATH_FORMATS);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    /**
     * 设置通知提示方式 - 基础属性
     */
    private void setStyleBasic() {
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(
                this);
        builder.statusBarDrawable = R.drawable.ic_launcher;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL; // 设置为点击后自动消失
        builder.notificationDefaults = Notification.DEFAULT_SOUND; // 设置为铃声（
        // Notification.DEFAULT_SOUND）或者震动（
        // Notification.DEFAULT_VIBRATE）
        JPushInterface.setPushNotificationBuilder(1, builder);
        // Toast.makeText(this, "Basic Builder - 1", Toast.LENGTH_SHORT).show();
    }

    // /**
    // *设置通知栏样式 - 定义通知栏Layout
    // */
    // private void setStyleCustom(){
    // CustomPushNotificationBuilder builder = new
    // CustomPushNotificationBuilder(PushSetActivity.this,R.layout.customer_notitfication_layout,R.id.icon,
    // R.id.title, R.id.text);
    // builder.layoutIconDrawable = R.drawable.ic_launcher;
    // builder.developerArg0 = "developerArg2";
    // JPushInterface.setPushNotificationBuilder(2, builder);
    // Toast.makeText(PushSetActivity.this,"Custom Builder - 2",
    // Toast.LENGTH_SHORT).show();
    // }

}
