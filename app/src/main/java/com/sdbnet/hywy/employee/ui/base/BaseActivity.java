package com.sdbnet.hywy.employee.ui.base;

import java.lang.reflect.Field;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Toast;

import com.sdbnet.hywy.employee.ActivityStackManager;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.db.db.DBManager;
import com.sdbnet.hywy.employee.service.TimeUploadService;
import com.sdbnet.hywy.employee.service.TimeUploadService.LocalBinder;
import com.sdbnet.hywy.employee.ui.UserLoginActivity;
import com.sdbnet.hywy.employee.ui.widget.DialogLoading;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.UtilsAndroid;
import com.sdbnet.hywy.employee.utils.UtilsCommon;

public class BaseActivity extends Activity {
    protected final String TAG = getClass().getSimpleName();
    protected AlertDialog alertDialog;

    protected DialogLoading mDialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // PreferencesUtil.initStoreData();
        bindService();

        ActivityStackManager.getStackManager().pushActivity(this);
        // 对静态变量初始化赋值
        if (TextUtils.isEmpty(PreferencesUtil.user_id)) {
            PreferencesUtil.initStoreData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unBindService();
        // unRegistBroadCast();
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        ActivityStackManager.getStackManager().popActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    protected void showShortToast(int pResId) {
        showShortToast(getString(pResId));
    }

    protected void showLongToast(int pResId) {
        showLongToast(getString(pResId));
    }

    protected void showLongToast(String pMsg) {
        Toast.makeText(this, pMsg, Toast.LENGTH_LONG).show();
    }

    protected void showShortToast(String pMsg) {
        Toast.makeText(this, pMsg, Toast.LENGTH_SHORT).show();
    }

    protected void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }

    protected void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    protected void openActivity(String pAction) {
        openActivity(pAction, null);
    }

    protected void openActivity(String pAction, Bundle pBundle) {
        Intent intent = new Intent(pAction);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }

    /**
     * 通过反射来设置对话框是否要关闭，在表单校验时很管用， 因为在用户填写出错时点确定时默认Dialog会消失， 所以达不到校验的效果
     * 而mShowing字段就是用来控制是否要消失的，而它在Dialog中是私有变量， 所有只有通过反射去解决此问题
     *
     * @param pDialog
     * @param pIsClose
     */
    protected void setAlertDialogIsClose(DialogInterface pDialog,
                                         Boolean pIsClose) {
        try {
            Field field = pDialog.getClass().getSuperclass()
                    .getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(pDialog, pIsClose);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected AlertDialog showAlertDialog(String TitleID, String Message) {
        alertDialog = new AlertDialog.Builder(this).setTitle(TitleID)
                .setMessage(Message).show();
        return alertDialog;
    }

    protected AlertDialog showAlertDialog(String pTitel, String pMessage,
                                          DialogInterface.OnClickListener pOkClickListener) {
        return showAlertDialog(pTitel, pMessage, pOkClickListener, null, null);
    }

    protected AlertDialog showAlertDialog(String pTitle, String pMessage,
                                          DialogInterface.OnClickListener pOkClickListener,
                                          DialogInterface.OnClickListener pCancelClickListener,
                                          DialogInterface.OnDismissListener pDismissListener) {
        alertDialog = new AlertDialog.Builder(this)
                .setTitle(pTitle)
                .setMessage(pMessage)
                .setPositiveButton(android.R.string.ok, pOkClickListener)
                .setNegativeButton(android.R.string.cancel,
                        pCancelClickListener).show();
        if (pDismissListener != null) {
            alertDialog.setOnDismissListener(pDismissListener);
        }
        return alertDialog;
    }

    protected AlertDialog showAlertDialog(String pTitle, String pMessage,
                                          String pPositiveButtonLabel, String pNegativeButtonLabel,
                                          DialogInterface.OnClickListener pOkClickListener,
                                          DialogInterface.OnClickListener pCancelClickListener,
                                          DialogInterface.OnDismissListener pDismissListener) {
        alertDialog = new AlertDialog.Builder(this).setTitle(pTitle)
                .setMessage(pMessage)
                .setPositiveButton(pPositiveButtonLabel, pOkClickListener)
                .setNegativeButton(pNegativeButtonLabel, pCancelClickListener)
                .show();
        if (pDismissListener != null) {
            alertDialog.setOnDismissListener(pDismissListener);
        }
        return alertDialog;
    }

    protected ProgressDialog showProgressDialog(int pTitelResID,
                                                String pMessage,
                                                DialogInterface.OnCancelListener pCancelClickListener) {
        String title = getResources().getString(pTitelResID);
        return showProgressDialog(title, pMessage, pCancelClickListener);
    }

    protected ProgressDialog showProgressDialog(String pTitle, String pMessage,
                                                DialogInterface.OnCancelListener pCancelClickListener) {
        alertDialog = ProgressDialog.show(this, pTitle, pMessage, true, true);
        alertDialog.setOnCancelListener(pCancelClickListener);
        return (ProgressDialog) alertDialog;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public void defaultFinish() {
        super.finish();
    }

    protected void returnLogin(final Context context, String msg,
                               final Dialog dia) {
        PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS, false);
        new AlertDialog.Builder(context)
                .setTitle(getString(R.string.logoff_notification))
                .setMessage(msg)
                .setPositiveButton(getString(R.string.restart_login),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // 点击ok，退出当前帐号
                                PreferencesUtil.clearLocalData(PreferenceManager
                                        .getDefaultSharedPreferences(context));
                                Intent intent = new Intent(context,
                                        UserLoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                if (dia != null && dia.isShowing())
                                    dia.dismiss();
                            }

                        }).show();
    }

    // /**
    // * 得到自定义的progressDialog
    // *
    // * @param context
    // * @param msg
    // * @return
    // */
    // protected Dialog createLoadingDialog(final Context context, String msg,
    // final CallBackHandler handler) {
    // LayoutInflater inflater = LayoutInflater.from(context);
    // View v = inflater.inflate(R.layout.loading, null);// 得到加载view
    // RelativeLayout layout = (RelativeLayout) v
    // .findViewById(R.id.dialog_view);// 加载布局
    // TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);//
    // 提示文字
    // tipTextView.setText(msg);// 设置加载信息
    //
    // if (handler != null) {
    // handler.onProgress(tipTextView);
    // }
    //
    // ImageView iv_close = (ImageView) v.findViewById(R.id.iv_close);
    // // iv_close.getBackground().setAlpha(90);
    //
    // final Dialog loadingDialog = new Dialog(context,
    // R.style.MyDialogStyle);// 创建自定义样式dialog
    // loadingDialog.setCancelable(true);// 可以用“返回键”取消
    // loadingDialog.setContentView(layout, new RelativeLayout.LayoutParams(
    // RelativeLayout.LayoutParams.MATCH_PARENT,
    // RelativeLayout.LayoutParams.MATCH_PARENT));// 设置布局
    //
    // // 关闭按钮点击事件
    // iv_close.setOnClickListener(new OnClickListener() {
    //
    // @Override
    // public void onClick(View v) {
    // if (handler != null) {
    // handler.onClick(v);
    // } else {
    // loadingDialog.dismiss();
    // }
    // }
    // });
    // return loadingDialog;
    // }
    // protected interface CallBackHandler {
    // public void onClick(View v);
    //
    // public void onProgress(TextView tipTextView);
    // }
    //
    // protected interface OnClickHandler {
    // public void onClick(View v);
    // }

    protected TimeUploadService mLocateService;

    protected void bindService() {
        Intent intent = new Intent(this, TimeUploadService.class);
        this.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    protected void unBindService() {
        if (mServiceConnection != null) {
            this.unbindService(mServiceConnection);
            mServiceConnection = null;
        }
    }

    protected ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mLocateService = null;
            if (mBindServiceBack != null) {
                mBindServiceBack.onBindeFailed();
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mLocateService = ((LocalBinder) service).getService();
            if (mBindServiceBack != null) {
                mBindServiceBack.onBindSuccess();
            }
        }
    };

    protected void setOnBindServiceListener(IBindServiceBack mBindServiceBack) {
        this.mBindServiceBack = mBindServiceBack;
    }

    protected interface IBindServiceBack {
        void onBindSuccess();

        void onBindeFailed();
    }

    private IBindServiceBack mBindServiceBack;

    @Override
    protected void onResume() {
        super.onResume();
        registBroadCast();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegistBroadCast();
    }

    private MyBroadCastReceiver myBroadCastReceiver;

    private void registBroadCast() {
        // 生成广播处理
        myBroadCastReceiver = new MyBroadCastReceiver();
        // 实例化过滤器并设置要过滤的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TimeUploadService.BROADCAST_SHOW_GPS_DIALOG);
        intentFilter
                .addAction(TimeUploadService.BROADCAST_SHOW_NETWORK_DIALOG);
        intentFilter
                .addAction(TimeUploadService.BROADCAST_SHOW_EXIT_DIALOG);
        // 注册广播
        registerReceiver(myBroadCastReceiver, intentFilter);
    }

    private void unRegistBroadCast() {
        if (myBroadCastReceiver != null) {
            unregisterReceiver(myBroadCastReceiver);
        }
    }

    protected class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TimeUploadService.BROADCAST_SHOW_GPS_DIALOG.equals(action)) {
                showGpsDialog(BaseActivity.this);
            } else if (TimeUploadService.BROADCAST_SHOW_NETWORK_DIALOG
                    .endsWith(action)) {
                showNetworkDialog(BaseActivity.this);
            } else if (TimeUploadService.BROADCAST_SHOW_EXIT_DIALOG
                    .endsWith(action)) {
                String msg = intent.getExtras().getString(
                        TimeUploadService.BROADCAST_MSG_EXIT);
                showExitDialog(msg);
            }
        }
    }

    private Builder mExitDialog;
    private boolean isShowExitDialog = false;

    protected void showExitDialog(String msg) {
        PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS,
                Constants.Value.WORKED);
        if (isShowExitDialog) {
            return;
        }

        if (mExitDialog == null) {
            mExitDialog = new AlertDialog.Builder(this)
                    .setTitle(this.getString(R.string.system_tip))
                    .setMessage(msg)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    exitApp(BaseActivity.this);
                                    mExitDialog = null;
                                    isShowExitDialog = false;
                                }
                            }).setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            exitApp(BaseActivity.this);
                            mExitDialog = null;
                            isShowExitDialog = false;
                        }
                    });
        }
        mExitDialog.show();
        isShowExitDialog = true;

    }

    private void exitApp(Activity activity) {
        PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS,
                Constants.Value.WORKED);
        // 点击ok，退出当前帐号
        PreferencesUtil.clearLocalData(PreferenceManager
                .getDefaultSharedPreferences(activity));
        Intent intent = new Intent(activity, UserLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    protected void saveOperateLog(String content) {
        saveOperateLog(content, null);

    }

    protected void saveOperateLog(String content, Date date) {

        if (!UtilsCommon.checkAccount()) {
            return;
        }
        DBManager manager = new DBManager(this);
        manager.saveOperate(content, date);
        manager.closeDatabase();
    }

    protected void initDialog() {
        if (mDialogLoading == null)
            mDialogLoading = new DialogLoading(this);
    }

    protected void showLoadDialog(int resId) {
        showLoadDialog(getString(resId));
    }

    protected void showLoadDialog(String msg) {
        initDialog();
        mDialogLoading.setMessage(msg);
        showLoadDialog();
    }

    protected void showLoadDialog() {
        initDialog();
        if (!mDialogLoading.isShowing())
            mDialogLoading.show();
    }

    protected void dismissLoadDialog() {
        initDialog();
        if (mDialogLoading.isShowing())
            mDialogLoading.dismiss();
    }

    /**
     * 打开gps
     */
    private static Builder mGpsDialog;
    private static boolean isShowGpsDialog = false;

    public static void showGpsDialog(final Context context) {
        if (isShowGpsDialog) {
            return;
        }
        if (mGpsDialog == null) {
            mGpsDialog = new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.confirm_tip))
                    .setMessage(context.getString(R.string.is_open_gps))
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // 开启GPS
                                    UtilsAndroid.Set.openGPSSettings(context);
                                    mGpsDialog = null;
                                    isShowGpsDialog = false;
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    mGpsDialog = null;
                                    isShowGpsDialog = false;
                                }
                            }).setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            mGpsDialog = null;
                            isShowGpsDialog = false;

                        }
                    });
        }

        mGpsDialog.show();
        isShowGpsDialog = true;
        Toast.makeText(context, "请开启GPS！", Toast.LENGTH_SHORT).show();
        // Intent intent = new Intent(Settings.ACTION_SETTINGS);
        // startActivityForResult(intent, 0); // 此为设置完成后返回到获取界面
    }

    /**
     * 打开network
     */
    private static Builder mNetworkDialog;
    private static boolean isShowNetworkDialog = false;

    public static void showNetworkDialog(final Context context) {
        if (isShowNetworkDialog) {
            return;
        }
        if (mNetworkDialog == null) {
            mNetworkDialog = new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.confirm_tip))
                    .setMessage(context.getString(R.string.is_open_network))
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // 开启GPS
                                    UtilsAndroid.Set.openNetwork(context);
                                    mNetworkDialog = null;
                                    isShowNetworkDialog = false;
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    mNetworkDialog = null;
                                    isShowNetworkDialog = false;
                                }
                            }).setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            mNetworkDialog = null;
                            isShowNetworkDialog = false;

                        }
                    });
        }

        mNetworkDialog.show();
        isShowNetworkDialog = true;
        Toast.makeText(context, "请开启网络！", Toast.LENGTH_SHORT).show();
    }

}
