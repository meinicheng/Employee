package com.sdbnet.hywy.employee.ui;

import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.sdbnet.hywy.employee.ActivityStackManager;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.db.db.DBManager;
import com.sdbnet.hywy.employee.db.domin.LocateLog;
import com.sdbnet.hywy.employee.db.domin.OperateLog;
import com.sdbnet.hywy.employee.db.old.LocateLogUtil;
import com.sdbnet.hywy.employee.db.old.OperateLogUtil;
import com.sdbnet.hywy.employee.ui.widget.DialogLoading;
import com.sdbnet.hywy.employee.ui.widget.DialogUpdateApp;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.ErrLogUtils;
import com.sdbnet.hywy.employee.utils.LogUtil;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.UtilsAndroid;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.UmengOnlineConfigureListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class SplashActivity extends Activity {
    private final static String TAG = SplashActivity.class.getSimpleName();// Log TAG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityStackManager.getStackManager().pushActivity(this);
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        initData();
        initUI();
        // 创建桌面快捷图标
        installShortcut();
        // update 数据库
        updateDB();
        // uninstallApp();
    }

    private void initData() {
        if (null == mLoadDialog) {
            String msg = getString(R.string.downloading_update_please_later_ellipsis);
            mLoadDialog = new DialogLoading(this, msg);// "正在下载更新,请稍后...");
        }

    }

    private void initUI() {
        View mActivityLayout = View.inflate(this, R.layout.activity_splash, null);
        setContentView(mActivityLayout);

        // LogUtil.e(TAG, "initAnim");
        // 设置动画
        Animation mAnimaAlpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
        mAnimaAlpha.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                // LogUtil.e(TAG, "onAnimationStart");
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                // LogUtil.e(TAG, "onAnimationEnd");
                if (!UtilsAndroid.Set.checkNetState(SplashActivity.this)) {
                    Toast.makeText(SplashActivity.this, R.string.httpisNull,
                            Toast.LENGTH_SHORT).show();
                    // enterMain();
                    goMain();
                } else {
                    initUpdate();
                }
            }
        });
        mActivityLayout.startAnimation(mAnimaAlpha);
    }

    /**
     * 创建桌面快捷图标
     */

    private void installShortcut() {
        boolean isShortcut = PreferencesUtil.getValue(
                PreferencesUtil.KEY_SHORTCUT + "_22", false);
        Log.i(TAG, "shortcut=" + isShortcut);
        if (isShortcut)
            return;
        // First, set up the shortcut intent. For this example, we simply create
        // an intent that
        // will bring us directly back to this activity. A more typical
        // implementation would use a
        // data Uri in order to display a more specific result, or a custom
        // action in order to
        // launch a specific operation.
        // Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
        // shortcutIntent.setClassName(this, this.getClass().getName());
        Intent intent = new Intent();
        intent.setClass(this, this.getClass());
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        // Then, set up the container intent (the response to the caller)

        Intent shortcutIntent = new Intent(
                "com.android.launcher.action.INSTALL_SHORTCUT");
        Parcelable icon = Intent.ShortcutIconResource.fromContext(this,
                R.drawable.logo);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                getString(R.string.app_name));

        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        // duplicate created
        shortcutIntent.putExtra("duplicate", false);
        sendBroadcast(shortcutIntent);
        PreferencesUtil.putValue(PreferencesUtil.KEY_SHORTCUT + "_22", true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume");

        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ActivityStackManager.getStackManager().popActivity(this);
    }

    // private void goHome() {
    // // LogUtil.e(TAG, "enterMain");
    // // if (!UtilsAndroid.Set.checkAppExist(this, WATCH_APP_PACKAGE_NAME)) {
    // // // 无忧精灵不存在，则提示用户安装
    // // installDemonApk();
    // // } else {
    // // 跳转至主界面
    // goMain();
    // // }
    // }

    /**
     * 进入主界面
     */
    private void goMain() {
        PreferencesUtil.initStoreData();
        // LogUtil.e(TAG, "goMain");
        // String options =
        // PreferencesUtil.getValue(Constants.Feild.KEY_OPTIONS);
        if (!PreferencesUtil.hasKey(Constants.Feild.KEY_OPTIONS)) {
            // 更新用户权限……。
            startActivity(new Intent(this, UserLoginActivity.class));

        } else

            // 判断是否保存了token，如果保存则进入主页，未保存则跳转至登录界面
            if (PreferencesUtil.hasKey(PreferencesUtil.KEY_SESSION_ID)
                    && PreferencesUtil.hasKey(PreferencesUtil.KEY_COMPANY_ID)
                    && PreferencesUtil.hasKey(PreferencesUtil.KEY_ITEM_ID)
                    && PreferencesUtil.hasKey(PreferencesUtil.KEY_USER_ID)
                    && PreferencesUtil.hasKey(PreferencesUtil.KEY_USER_TOKEN)
                    && PreferencesUtil.hasKey(PreferencesUtil.KEY_USER_TEL)) {
                // 进入主页

                // Intent intent = new Intent(SplashActivity.this,
                // MainActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                // | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(new Intent(this, MainActivity.class));

            } else {
                // UtilsCommon.openActivity(UserLoginActivity.class);
                startActivity(new Intent(this, UserLoginActivity.class));
            }
        // ActivityStackManager.getStackManager().popAllActivitys();
        finish();
    }

    // /**
    // * 安装守护精灵
    // *
    // * @throws IOException
    // */
    // private void installDemonApk() {
    // FileOutputStream fos = null;
    // InputStream is = null;
    // File t = null;
    // try {
    // is = getAssets().open(WATCH_APP_NAME);
    // File sd = Environment.getExternalStorageDirectory();
    // if (!sd.canWrite()) {
    //
    // }
    // String newPath = Environment.getExternalStorageDirectory()
    // .getAbsolutePath() + "/hywy_demon.apk";
    // t = new File(newPath);
    // if (!t.exists()) {
    // t.createNewFile();
    // }
    // fos = new FileOutputStream(t);
    // byte[] buffer = new byte[1024];
    // int byteCount = 0;
    // while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取 buffer字节
    // fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
    // }
    // } catch (Exception e) {
    // ErrLogUtils.uploadErrLog(this, ErrLogUtils.toString(e));
    // e.printStackTrace();
    // } finally {
    // try {
    // if (fos != null)
    // fos.flush();
    // if (is != null)
    // is.close();
    // fos.close();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }// 刷新缓冲区
    //
    // Intent intent = new Intent();
    // intent.setAction(Intent.ACTION_VIEW);
    // intent.addCategory(Intent.CATEGORY_DEFAULT);
    // intent.setDataAndType(Uri.fromFile(t),
    // "application/vnd.android.package-archive");
    // startActivityForResult(intent, 0);
    // }
    // }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "code=" + requestCode + "<" + resultCode + "<" + data);
        // goHome();
        goMain();
    }

    // /**
    // * 检查更新
    // */
    // private void checkUpdate() {
    // new Thread(new Runnable() {
    //
    // @Override
    // public void run() {
    // initUpdate();
    // }
    // }).start();
    // }
    // private static final int MSG_START_UPDATE = 11;
    // private static final int MSG_SHOW_UPDATE = 12;
    // private static final int MSG_CONFIRM_UPDATE = 13;
    // private static final int MSG_FORCE_UPDATE = 14;
    // private static final int MSG_UPDATE_FAILE = 15;
    // private static final int MSG_UPDATE_SUCCES = 16;

    private DialogLoading mLoadDialog;
    private DialogUpdateApp mDialogUpdateApp;

    /**
     * 在线更新
     */
    private void initUpdate() {
        Log.e(TAG, "initUpdate");
        mDialogUpdateApp = new DialogUpdateApp(this);

        UmengUpdateAgent.setUpdateOnlyWifi(false);
        // UmengUpdateAgent.forceUpdate(this);
        UmengUpdateAgent.setUpdateAutoPopup(false);

//        MobclickAgent.updateOnlineConfig(this);
//        String update_mode = MobclickAgent
//                .getConfigParams(this, "upgrade_mode");
//        LogUtil.d(TAG, update_mode);
//        MobclickAgent
//                .setOnlineConfigureListener(new UmengOnlineConfigureListener() {
//
//                    @Override
//                    public void onDataReceived(JSONObject arg0) {
//                        // TODO Auto-generated method stub
//                        LogUtil.d(TAG, arg0 + "");
//                    }
//                });
//        if (!TextUtils.isEmpty(update_mode)) {
//            String[] params = update_mode.split(",");
//            if (params.length == 2 && "F".equalsIgnoreCase(params[1])) {
//            }
//        }

        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus,
                                         UpdateResponse updateInfo) {
                LogUtil.d("updateStatus=" + updateStatus + "");
                switch (updateStatus) {
                    case UpdateStatus.Yes: // has update
                        mDialogUpdateApp.setMsg(updateInfo);
                        mDialogUpdateApp.show();
                        break;
                    case UpdateStatus.No: // has no update
                        // enterMain();
                        goMain();
                        break;
                    default:
                        // enterMain();
                        goMain();
                        break;
                }
            }
        });
        UmengUpdateAgent.update(this);
    }


    private void updateDB() {
        try {
            DBManager manager = new DBManager(this);

            List<LocateLog> mLocateLogs = LocateLogUtil.getIntance(this)
                    .findAllLocate();
            LocateLogUtil.getIntance(this).deleteAllLocate();
            if (mLocateLogs != null && mLocateLogs.size() > 0) {
                for (int i = 0; i < mLocateLogs.size(); i++) {
                    manager.saveLocateLog(mLocateLogs.get(i));
                }
            }

            List<OperateLog> mOperateLogs = OperateLogUtil
                    .getAllOperateLog(this);
            // operateLog.deleteAll(OperateLogDBHelper.dbHelper.getReadableDatabase());
            OperateLogUtil.deleteAllOperateLog(this);
            if (mOperateLogs != null && mOperateLogs.size() > 0) {
                for (int i = 0; i < mOperateLogs.size(); i++) {
                    manager.saveOperate(mOperateLogs.get(i));
                }
            }

            manager.closeDatabase();
        } catch (Exception e) {
            ErrLogUtils.uploadErrLog(this, e);
        }
    }

    private void uninstallApp() {

        if (UtilsAndroid.Set.checkAppExist(this, ("com.android.hywy.demon"))) {
            Uri uri = Uri.parse("package:" + ("com.android.hywy.demon"));
            Intent intent = new Intent(Intent.ACTION_DELETE, uri);
            this.startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
