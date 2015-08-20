package com.sdbnet.hywy.employee.ui.widget;

import java.io.File;
import java.text.DecimalFormat;

import com.sdbnet.hywy.employee.ActivityStackManager;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.utils.UtilsAndroid;
import com.umeng.update.UmengDownloadListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DialogUpdateApp extends Dialog {

	public DialogUpdateApp(Context context) {
		// super(context, android.R.style.Theme_Dialog);
		super(context, R.style.DialogCommonStyle);
	}

	public DialogUpdateApp(Context context, int theme) {
		super(context, theme);
	}

	private void init() {

	}

	private TextView mTextTitle;
	private ImageView mImg;
	private TextView mTextMsg;
	private ProgressBar mPbarDown;
	private Button mBtnOk;

	private UpdateResponse updateInfo;
	private File file;
	private boolean forceUpdate = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_update_app);
		initUI();
		initListener();
	}

	private void initUI() {
		mImg = (ImageView) findViewById(R.id.dialog_update_wifi_indicator);
		mTextMsg = (TextView) findViewById(R.id.dialog_update_content);
		mPbarDown = (ProgressBar) findViewById(R.id.dialog_update_progress);
		mBtnOk = (Button) findViewById(R.id.dialog_update_id_ok);
		if (UtilsAndroid.Set.isWifiDataEnable(getContext())) {
			mImg.setVisibility(View.INVISIBLE);
		}
		initData();

	}

	private void initData() {

		if (updateInfo != null) {
			String msg;
			if (file != null) {
				msg = String.format(
						getContext().getString(R.string.update_msg_1),
						updateInfo.version, updateInfo.updateLog);
				if (mPbarDown != null) {
					mPbarDown.setVisibility(View.INVISIBLE);
				}

			} else {
				msg = String.format(
						getContext().getString(R.string.update_msg_2),
						updateInfo.version,
						formetFileSize(updateInfo.target_size),
						updateInfo.updateLog);

			}
			Log.e("initData", file + "\n" + msg);
			if (mTextMsg != null) {
				mTextMsg.setText(msg);
			}

		}
	}

	private String formetFileSize(String size) {
		// 7427269
		long fileS = Long.parseLong(size);
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		String wrongSize = "0B";
		if (fileS == 0) {
			return wrongSize;
		}
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "GB";
		}
		return fileSizeString;
	}

	private void initListener() {
		mBtnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startUmengUpdate();
			}
		});
		setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {

				Log.e("setOnCancelListener", "Cancle=" + forceUpdate);
				if (forceUpdate) {
					Toast.makeText(
							getContext(),
							getContext().getString(
									R.string.umeng_update_tip_msg),
							Toast.LENGTH_SHORT).show();
					ActivityStackManager.getStackManager().popAllActivitys();
				} else {
					// enterMain();
					// goMain();
					dismiss();
				}
			}
		});
	}

	private void startUmengUpdate() {
		Log.e("start update", updateInfo + "");
		UmengUpdateAgent.setDownloadListener(new UmengDownloadListener() {

			@Override
			public void OnDownloadUpdate(int arg0) {
				Log.e("OnDownloadUpdate", "OnDownloadUpdate" + arg0 + "");
				mPbarDown.setProgress(arg0);
			}

			@Override
			public void OnDownloadStart() {
				Log.e("OnDownloadStart", "OnDownloadStart" + "");
				mPbarDown.setProgress(0);
			}

			@Override
			public void OnDownloadEnd(int arg0, String arg1) {
				Log.e("OnDownloadEnd,", arg0 + "," + arg1);
				dismiss();
				switch (arg0) {
				case UpdateStatus.DOWNLOAD_COMPLETE_FAIL:
					Toast.makeText(getContext(),
							getContext().getString(R.string.download_fail),
							Toast.LENGTH_SHORT).show();
					ActivityStackManager.getStackManager().popAllActivitys();
					break;
				case UpdateStatus.DOWNLOAD_COMPLETE_SUCCESS:
					// Toast.makeText(getContext(),
					// getContext().getString(R.string.download_success),
					// Toast.LENGTH_SHORT).show();
					startInstall(new File(arg1));
					break;
				case UpdateStatus.DOWNLOAD_NEED_RESTART:
					// enterMain();
					// dismiss();
					break;
				default:
					break;
				}
			}

		});
		if (file == null)
			UmengUpdateAgent.startDownload(getContext(), updateInfo);
		else {
			dismiss();
			startInstall(file);
		}

	}

	private void startInstall(File file) {
		if (getContext() != null && file != null)
			UmengUpdateAgent.startInstall(getContext(), file);
		ActivityStackManager.getStackManager().popAllActivitys();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Log.e("onBackPress", "back");
	}

	public void setMsg(UpdateResponse updateInfo) {
		this.updateInfo = updateInfo;
		if (updateInfo != null) {
			file = UmengUpdateAgent.downloadedFile(getContext(), updateInfo);
			initData();
		}
		// Log.e("MSG", "new_md5=" + updateInfo.new_md5 + ",origin="
		// + updateInfo.origin + ",patch_md5=" + updateInfo.patch_md5
		// + ",path=" + updateInfo.path + ",proto_ver="
		// + updateInfo.proto_ver + ",size=" + updateInfo.size
		// + ",target_size=" + updateInfo.target_size + ",updateLog="
		// + updateInfo.updateLog + ",version=" + updateInfo.version
		// + ",delta=" + updateInfo.delta + ",display_ads="
		// + updateInfo.display_ads + ",hasUpdate=" + updateInfo.hasUpdate
		// + ",");
		// 07-21 11:10:06.574: E/MSG(29354):
		// new_md5=c847b823b763fad674a5fbff18bd37d3,
		// origin=null,patch_md5=null,
		// path=http://au.apk.umeng.com/uploads/apps/54c6e985fd98c5bd21000409/_umeng_%40_21_%40_c847b823b763fad674a5fbff18bd37d3.apk,
		// proto_ver=null,size=null,target_size=7427269,
		// updateLog=1：优化修复；,
		// version=release5.7,delta=false,display_ads=false,hasUpdate=true,

	}

}
