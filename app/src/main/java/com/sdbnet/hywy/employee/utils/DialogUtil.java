package com.sdbnet.hywy.employee.utils;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.sdbnet.hywy.employee.R;

public class DialogUtil {
	/**
	 * 得到自定义的progressDialog
	 * 
	 * @param context
	 * @param msg
	 * @return
	 */

//	public static Dialog createLoadingDialog(Context context, String msg,
//			final CallBackHandler handler) {
//		LayoutInflater inflater = LayoutInflater.from(context);
//		View v = inflater.inflate(R.layout.loading, null);// 得到加载view
//		RelativeLayout layout = (RelativeLayout) v
//				.findViewById(R.id.dialog_view);// 加载布局
//		TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
//		tipTextView.setText(msg);// 设置加载信息
//
//		if (handler != null) {
//			handler.onProgress(tipTextView);
//		}
//
//		ImageView iv_close = (ImageView) v.findViewById(R.id.iv_close);
//		// iv_close.getBackground().setAlpha(90);
//
//		final Dialog loadingDialog = new Dialog(context, R.style.MyDialogStyle);// 创建自定义样式dialog
//		loadingDialog.setCancelable(true);// 可以用“返回键”取消
//		loadingDialog.setContentView(layout, new RelativeLayout.LayoutParams(
//				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));// 设置布局
//
//		// 关闭按钮点击事件
//		iv_close.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (handler != null) {
//					handler.onClick(v);
//				} else {
//					loadingDialog.dismiss();
//				}
//			}
//		});
//		return loadingDialog;
//	}

	public static interface CallBackHandler {
		public void onClick(View v);

		public void onProgress(TextView tipTextView);
	}

	public static interface OnClickHandler {
		public void onClick(View v);
	}

//	/**
//	 * 清空草稿箱
//	 */
//	public static void clearDraftBox(final Context context) {
//		new AlertDialog.Builder(context)
//				.setTitle(context.getString(R.string.clear_tip))
//				.setMessage(
//						context.getString(R.string.sure_you_want_empty_draftbox))
//				.setPositiveButton(android.R.string.ok,
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								DBManager manager = new DBManager(context);
//								manager.deleteAllOrders();
//								int count = (int) manager.getOrderCount();
//								manager.closeDatabase();
//								// new DBManager(context).deleteAllOrders();
//								// 发送广播，以便草稿箱界面刷新
//								Intent intent = new Intent();
//								intent.setAction(DBCustomValue.ACTION_COUNT_CHANGED);
//								intent.putExtra(DBCustomValue.COUNT, count);
//								context.sendBroadcast(intent);
//								// currentFragment.onStart();
//							}
//						}).setNegativeButton(android.R.string.cancel, null)
//				.show();
//	}

	public static final int DIALOG_PHOTO_PHOTOZOOM = 11;
	public static final int DIALOG_PHOTO_CANMERA = 21;

	// public static final int String DIALOG_PHOTO_PHOTOZOOM
	// public static Dialog showPhotoDialog(final Activity context) {
	public static Dialog getPhotoDialog(final Activity context) {
		View view = context.getLayoutInflater().inflate(
				R.layout.photo_choose_dialog, null);
		final Dialog dialog = new Dialog(context,
				R.style.transparentFrameWindowStyle);
		dialog.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		Window window = dialog.getWindow();
		// 设置显示动画
		window.setWindowAnimations(R.style.main_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = 0;
		wl.y = context.getWindowManager().getDefaultDisplay().getHeight();
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

		// 设置显示位置
		dialog.onWindowAttributesChanged(wl);
		// 设置点击外围解散
		dialog.setCanceledOnTouchOutside(true);
		Button albumSelect = (Button) view
				.findViewById(R.id.button_dialog_photograph_photo_album_select);
		albumSelect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				context.startActivityForResult(intent, DIALOG_PHOTO_PHOTOZOOM);
				dialog.cancel();
			}
		});
		Button takePicture = (Button) view
				.findViewById(R.id.button_dialog_photograph_take_picture);
		takePicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用android自带的照相机
				File file = new File(Environment.getExternalStorageDirectory()
						+ "/PhotoCache/", String.valueOf(System
						.currentTimeMillis()) + ".jpg");
				// path = file.getPath();
				Uri imageUri = Uri.fromFile(file);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				context.startActivityForResult(intent, DIALOG_PHOTO_CANMERA);
				dialog.cancel();
			}
		});
		Button buttonCancle = (Button) view
				.findViewById(R.id.button_dialog_photograph_cancle);
		buttonCancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.cancel();
			}
		});
		return dialog;
		// dialog.show();
	}

}
