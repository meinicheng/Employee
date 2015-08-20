package com.sdbnet.hywy.employee.ui.base;

import java.util.Date;

import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.db.db.DBManager;
import com.sdbnet.hywy.employee.utils.UtilsAndroid;
import com.sdbnet.hywy.employee.utils.UtilsCommon;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

public class BaseFrament extends Fragment {
	protected void showMsg(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
	}

	protected void showMsg(int resId) {
		showMsg(getString(resId));
	}

	protected void showMsgLong(String msg) {
		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}

	protected void showMsgLong(int resId) {
		showMsgLong(getString(resId));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

	}

	@Override
	public void onResume() {
		super.onResume();
		// registBroadCast();
	}

	@Override
	public void onPause() {
		super.onPause();
		// unRegistBroadCast();
	}

	// private MyBroadCastReceiver myBroadCastReceiver;
	//
	// private void registBroadCast() {
	// // 生成广播处理
	// myBroadCastReceiver = new MyBroadCastReceiver();
	// // 实例化过滤器并设置要过滤的广播
	// IntentFilter intentFilter = new IntentFilter();
	// intentFilter.addAction(TimingUpLocateService.BROADCAST_SHOW_GPS_DIALOG);
	// intentFilter
	// .addAction(TimingUpLocateService.BROADCAST_SHOW_NETWORK_DIALOG);
	// intentFilter
	// .addAction(TimingUpLocateService.BROADCAST_SHOW_EXIT_DIALOG);
	// // 注册广播
	// getActivity().registerReceiver(myBroadCastReceiver, intentFilter);
	// }
	//
	// private void unRegistBroadCast() {
	// if (myBroadCastReceiver != null) {
	// getActivity().unregisterReceiver(myBroadCastReceiver);
	// }
	// }
	//
	// protected class MyBroadCastReceiver extends BroadcastReceiver {
	//
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// String action = intent.getAction();
	// if (TimingUpLocateService.BROADCAST_SHOW_GPS_DIALOG.equals(action)) {
	// DialogUtil.showGpsDialog(getActivity());
	// } else if (TimingUpLocateService.BROADCAST_SHOW_NETWORK_DIALOG
	// .endsWith(action)) {
	// DialogUtil.showNetworkDialog(getActivity());
	// } else if (TimingUpLocateService.BROADCAST_SHOW_EXIT_DIALOG
	// .endsWith(action)) {
	// String msg = intent.getExtras().getString(
	// TimingUpLocateService.BROADCAST_MSG_EXIT);
	// showExitDialog(msg);
	// }
	// }
	// }
	//
	// private Builder mExitDialog;
	// private boolean isShowExitDialog = false;
	//
	// protected void showExitDialog(String msg) {
	// if (isShowExitDialog) {
	// return;
	// }
	//
	// if (mExitDialog == null) {
	// mExitDialog = new AlertDialog.Builder(getActivity())
	// .setTitle(this.getString(R.string.system_tip))
	// .setMessage(msg)
	// .setPositiveButton(android.R.string.ok,
	// new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// exitApp(getActivity());
	// mExitDialog = null;
	// isShowExitDialog = false;
	// }
	// }).setOnCancelListener(new OnCancelListener() {
	//
	// @Override
	// public void onCancel(DialogInterface dialog) {
	// exitApp(getActivity());
	// mExitDialog = null;
	// isShowExitDialog = false;
	// }
	// });
	// }
	// mExitDialog.show();
	// isShowExitDialog = true;
	//
	// }
	//
	// private void exitApp(Activity activity) {
	// PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS,
	// Constants.Value.WORKED);
	// // 点击ok，退出当前帐号
	// PreferencesUtil.clearLocalData(PreferenceManager
	// .getDefaultSharedPreferences(activity));
	// Intent intent = new Intent(activity, UserLoginActivity.class);
	// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
	// | Intent.FLAG_ACTIVITY_NEW_TASK);
	// activity.startActivity(intent);
	// }
	protected void saveOperateLog(String content) {
		saveOperateLog(content, null);

	}

	protected void saveOperateLog(String content, Date date) {

		if (!UtilsCommon.checkAccount()) {
			return;
		}
		DBManager manager = new DBManager(getActivity());
		manager.saveOperate(content, date);
		manager.closeDatabase();
	}

	/**
	 * 打开gps
	 */
	private  Builder mGpsDialog;
	private  boolean isShowGpsDialog = false;

	protected  void showGpsDialog(final Context context) {
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
	private  Builder mNetworkDialog;
	private  boolean isShowNetworkDialog = false;

	public  void showNetworkDialog(final Context context) {
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
