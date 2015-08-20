package com.sdbnet.hywy.employee.ui;

import java.io.File;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sdbnet.hywy.employee.ActivityStackManager;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.album.AlbumHelper.ImageItem;
import com.sdbnet.hywy.employee.album.Bimp;
import com.sdbnet.hywy.employee.utils.Constants;

public class PopWindowActivity extends Activity implements OnClickListener {

	private Button mBtnTackPic, mBtnPickPhoto, mBtnCancle;
	private LinearLayout mPopLayout;
	private String path;
	private static final int TAKE_PICTURE = 0x000000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityStackManager.getStackManager().pushActivity(this);
		setContentView(R.layout.activity_pop_dialog);
		initUI();
	}

	private void initUI() {
		mBtnTackPic = (Button) this
				.findViewById(R.id.activity_dialog_pop_btn_take_photo);
		mBtnPickPhoto = (Button) this
				.findViewById(R.id.activity_dialog_pop_btn_pick_photo);
		mBtnCancle = (Button) this
				.findViewById(R.id.activity_dialog_pop_btn_cancel);

		mPopLayout = (LinearLayout) findViewById(R.id.activity_dialog_pop_layout);

		// 添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
		mPopLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(PopWindowActivity.this,
						getString(R.string.pop_window_tip_msg),
						Toast.LENGTH_SHORT).show();
			}
		});
		// 添加按钮监听
		mBtnCancle.setOnClickListener(this);
		mBtnPickPhoto.setOnClickListener(this);
		mBtnTackPic.setOnClickListener(this);
	}

	// 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		onBackPressed();
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_dialog_pop_btn_take_photo: // 拍照
			photo();
			break;
		case R.id.activity_dialog_pop_btn_pick_photo: // 选择相册图片
			selectAlbum();
			break;
		case R.id.activity_dialog_pop_btn_cancel:
			onBackPressed();
			break;
		default:
			break;
		}

	}

	private void selectAlbum() {
		Intent intent = new Intent(PopWindowActivity.this,
				AlbumPicActivity.class);
		startActivity(intent);
		onBackPressed();
	}

	/**
	 * 打开摄像头拍照
	 */
	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File f = new File(Constants.SDBNET.PATH_PHOTO);
		if (!f.exists())
			f.mkdirs();
		File file = new File(Constants.SDBNET.PATH_PHOTO, String.valueOf(System
				.currentTimeMillis()) + ".jpg");
		path = file.getPath();
		Uri imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
//		onBackPressed();
		// overridePendingTransition(R.anim.push_bottom_in,
		// R.anim.push_bottom_out);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("SelectPicPopupWindow", "onActivityResult>>"+String.format(
				"Bimp.drr.size(): %s; RESULT_OK: %s; resultCode: %s",
				Bimp.imgPath.size(), RESULT_OK + "", resultCode));
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case TAKE_PICTURE: // 保存摄像头拍下的图片
			if (Bimp.imgPath.size() < Bimp.IMAGE_COUNT) {
				ImageItem imageItem = new ImageItem();
				imageItem.imagePath = path;
				Bimp.imgPath.add(imageItem);
			}

			break;
		}
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// overridePendingTransition(R.anim.push_bottom_in,
		// R.anim.push_bottom_out);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBackPressed();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ActivityStackManager.getStackManager().popActivity(this);
	}
}
