package com.sdbnet.hywy.employee.utils;

import java.io.File;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.sdbnet.hywy.employee.ActivityStackManager;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.ui.DialogTipActivity;
import com.sdbnet.hywy.employee.ui.widget.DialogLoading;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.UmengOnlineConfigureListener;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengDownloadListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class UmengUtils {
	private static final String TAG = "UmengUtils";
	private static boolean forceUpdate;

	/**
	 * 
	 * 开启更新线程
	 * 
	 * @param context
	 */
	public static void startUpdateThread(final Context context) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				checkUpdate(context);
			}
		}).start();
	}

	/**
	 * 检查更新
	 */
	private static void checkUpdate(final Context context) {
		initDialog(context);
		forceUpdate = false;
		MobclickAgent.updateOnlineConfig(context);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		String update_mode = MobclickAgent.getConfigParams(context,
				"upgrade_mode");
		LogUtil.d(TAG, update_mode);
		MobclickAgent
				.setOnlineConfigureListener(new UmengOnlineConfigureListener() {

					@Override
					public void onDataReceived(JSONObject arg0) {
						LogUtil.d(TAG, arg0 + "");
					}
				});
		if (!TextUtils.isEmpty(update_mode)) {
			String[] params = update_mode.split(",");
			if (params.length == 2 && "F".equalsIgnoreCase(params[1])) {
				forceUpdate = true;
			}
		}

		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				LogUtil.d("updateStatus=" + updateStatus + "");
				if (context == null) {
					return;
				}
				switch (updateStatus) {
				case UpdateStatus.Yes: // has update

					if ("v1.0.1".equals(updateInfo.version)) {
						mLoadDialog.show();
						UmengUpdateAgent.startDownload(context, updateInfo);
					} else {
						UmengUpdateAgent.showUpdateDialog(context, updateInfo);
					}
					if (mListener != null) {
						mListener.onUpdateYes();
					}
					break;
				case UpdateStatus.No: // has no update
					if (mListener != null) {
						mListener.onUpdateNo();
					}
					break;
				default:
					if (mListener != null) {
						mListener.onUpdateNo();
					}
					break;
				}
			}

		});
		UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {
			@Override
			public void onClick(int status) {
				if (context == null) {
					return;
				}
				switch (status) {
				case UpdateStatus.Update: // 用户点击确认同意更新
					if (mLoadDialog != null)
						mLoadDialog.show();
					break;
				default: // 用户点击取消，强制更新
					if (forceUpdate) {
						Toast.makeText(
								context,
								context.getString(R.string.umeng_update_tip_msg),
								Toast.LENGTH_SHORT).show();
						ActivityStackManager.getStackManager()
								.popAllActivitys();

					} else {

					}
					break;
				}
			}
		});
		UmengUpdateAgent.setDownloadListener(new UmengDownloadListener() {

			@Override
			public void OnDownloadUpdate(int arg0) {
				// LogUtil.e(TAG, "OnDownloadUpdate" + arg0 + "");
			}

			@Override
			public void OnDownloadStart() {
				// LogUtil.e(TAG, "OnDownloadStart" + "");
			}

			@Override
			public void OnDownloadEnd(int arg0, String arg1) {
				LogUtil.d("OnDownloadEnd," + arg0 + "," + arg1);
				mLoadDialog.dismiss();
				if (context == null) {
					return;
				}
				switch (arg0) {
				case UpdateStatus.DOWNLOAD_COMPLETE_FAIL:
					Toast.makeText(context,
							context.getString(R.string.download_fail),
							Toast.LENGTH_SHORT).show();
					// showLongToast(getString(R.string.download_fail));
					break;
				case UpdateStatus.DOWNLOAD_COMPLETE_SUCCESS:
					Toast.makeText(context,
							context.getString(R.string.download_success),
							Toast.LENGTH_SHORT).show();

					// showLongToast(getString(R.string.download_success));
					UmengUpdateAgent.startInstall(context, new File(arg1));

					// System.exit(0);
					ActivityStackManager.getStackManager().popAllActivitys();
					break;
				// case UpdateStatus.DOWNLOAD_NEED_RESTART:
				// enterMain();
				// break;
				// default:
				// break;
				}
			}
		});

		UmengUpdateAgent.update(context);
	}

	private static IUpdateListener mListener;

	public static interface IUpdateListener {
		void onUpdateYes();

		void onUpdateNo();

		void onUpdateSuccess();

		void onUpdateFailed();

		void onUpdateFouce();

		void onUpdateCancel();
	}

	public static void setOnUpdateListener(IUpdateListener listener) {
		mListener = listener;
	}

	public static void stopUpdateListener() {
		mListener = null;
	}

	private static DialogLoading mLoadDialog;

	private static void initDialog(Context context) {
		if (null == mLoadDialog) {
			String msg = context
					.getString(R.string.downloading_update_please_later_ellipsis);
			mLoadDialog = new DialogLoading(context, msg);// "正在下载更新,请稍后...");
		}
	}

	private static boolean showDialog() {
		Activity activity = ActivityStackManager.getStackManager()
				.currentActivity();
		if (activity != null) {
			Intent intent = new Intent(activity, DialogTipActivity.class);
			String msg = activity
					.getString(R.string.downloading_update_please_later_ellipsis);
			intent.putExtra(DialogTipActivity.EXTRA_MSG, msg);
			activity.startActivity(intent);
			return true;
		} else {

		}
		return false;
	}
}
