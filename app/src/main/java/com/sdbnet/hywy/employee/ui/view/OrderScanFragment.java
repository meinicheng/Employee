//package com.sdbnet.hywy.employee.ui.view;
//
//import java.io.IOException;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.AssetFileDescriptor;
//import android.graphics.Point;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.media.MediaPlayer.OnCompletionListener;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.LayoutInflater;
//import android.view.SurfaceHolder;
//import android.view.SurfaceHolder.Callback;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.LinearInterpolator;
//import android.view.animation.TranslateAnimation;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//
//import com.sdbnet.hywy.employee.R;
//import com.sdbnet.hywy.employee.ui.OrderExecuteActivity;
//import com.sdbnet.hywy.employee.ui.base.BaseFrament;
//import com.zbar.lib.ICaptureHandler;
//import com.zbar.lib.camera.CameraManager;
//import com.zbar.lib.decode.CaptureActivityHandler;
//import com.zbar.lib.decode.InactivityTimer;
//
//public class OrderScanFragment extends BaseFrament implements Callback,
//		ICaptureHandler {
//
//	private CaptureActivityHandler handler;
//	private boolean hasSurface;
//	private InactivityTimer inactivityTimer;
//	private MediaPlayer mediaPlayer;
//	private boolean playBeep;
//	private static final float BEEP_VOLUME = 0.50f;
//	private int x = 0;
//	private int y = 0;
//	private int cropWidth = 0;
//	private int cropHeight = 0;
//	private FrameLayout mContainer = null;
//	private RelativeLayout mCropLayout = null;
//	private boolean isNeedCapture = false;
//	private EditText et_bar_code;
//	private View view;
//
//	@Override
//	public boolean isNeedCapture() {
//		return isNeedCapture;
//	}
//
//	@Override
//	public void setNeedCapture(boolean isNeedCapture) {
//		this.isNeedCapture = isNeedCapture;
//	}
//
//	@Override
//	public int getX() {
//		return x;
//	}
//
//	@Override
//	public void setX(int x) {
//		this.x = x;
//	}
//
//	@Override
//	public int getY() {
//		return y;
//	}
//
//	@Override
//	public void setY(int y) {
//		this.y = y;
//	}
//
//	@Override
//	public int getCropWidth() {
//		return cropWidth;
//	}
//
//	@Override
//	public void setCropWidth(int cropWidth) {
//		this.cropWidth = cropWidth;
//	}
//
//	@Override
//	public int getCropHeight() {
//		return cropHeight;
//	}
//
//	@Override
//	public void setCropHeight(int cropHeight) {
//		this.cropHeight = cropHeight;
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		view = inflater.inflate(R.layout.activity_bar_code_scan, null);
//
//		// 初始化 CameraManager
//		CameraManager.init(getActivity().getApplication());
//		hasSurface = false;
//		inactivityTimer = new InactivityTimer(getActivity());
//
//		mContainer = (FrameLayout) view
//				.findViewById(R.id.activity_scan_code_capture_containter);
//		mCropLayout = (RelativeLayout) view
//				.findViewById(R.id.activity_scan_code_capture_crop_layout);
//
//		ImageView mQrLineView = (ImageView) view
//				.findViewById(R.id.activity_scan_code_capture_scan_line);
//		TranslateAnimation mAnimation = new TranslateAnimation(
//				Animation.ABSOLUTE, 0f, Animation.ABSOLUTE,
//				0f, Animation.RELATIVE_TO_PARENT, 0f,
//				Animation.RELATIVE_TO_PARENT, 0.9f);
//		mAnimation.setDuration(1500);
//		mAnimation.setRepeatCount(-1);
//		mAnimation.setRepeatMode(Animation.REVERSE);
//		mAnimation.setInterpolator(new LinearInterpolator());
//		mQrLineView.setAnimation(mAnimation);
//
//		et_bar_code = (EditText) view
//				.findViewById(R.id.activity_scan_code_et_bar_code);
//		bt_post = (Button) view.findViewById(R.id.activity_scan_code_bt_post);
//		bt_post.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				et_bar_code.setText("");
//				Intent intent = new Intent(getActivity(),
//						OrderExecuteActivity.class);
//				getActivity().startActivity(intent);
//			}
//		});
//
//		return view;
//	}
//
//	boolean flag = true;
//
//	protected void light() {
//		if (flag == true) {
//			flag = false;
//			// 开闪光灯
//			CameraManager.get().openLight();
//		} else {
//			flag = true;
//			// 关闪光灯
//			CameraManager.get().offLight();
//		}
//
//	}
//
//	@SuppressWarnings("deprecation")
//	@Override
//	public void onResume() {
//		super.onResume();
//		SurfaceView surfaceView = (SurfaceView) view
//				.findViewById(R.id.activity_scan_code_capture_preview);
//		SurfaceHolder surfaceHolder = surfaceView.getHolder();
//		if (hasSurface) {
//			initCamera(surfaceHolder);
//		} else {
//			surfaceHolder.addCallback(this);
//			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//		}
//		playBeep = true;
//		AudioManager audioService = (AudioManager) getActivity()
//				.getSystemService(Context.AUDIO_SERVICE);
//		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
//			// 铃音非正常模式
//			playBeep = false;
//		}
//		initBeepSound();
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		if (handler != null) {
//			handler.quitSynchronously();
//			handler = null;
//		}
//		CameraManager.get().closeDriver();
//	}
//
//	@Override
//	public void onDestroy() {
//		inactivityTimer.shutdown();
//		super.onDestroy();
//	}
//
//	@Override
//	public void handleDecode(String result) {
//		inactivityTimer.onActivity();
//		playBeepSoundAndVibrate();
//
//		et_bar_code.setText(result);
//	}
//
//	private void initCamera(SurfaceHolder surfaceHolder) {
//		try {
//			CameraManager.get().openDriver(surfaceHolder);
//
//			Point point = CameraManager.get().getCameraResolution();
//			int width = point.y;
//			int height = point.x;
//
//			int x = mCropLayout.getLeft() * width / mContainer.getWidth();
//			int y = mCropLayout.getTop() * height / mContainer.getHeight();
//
//			int cropWidth = mCropLayout.getWidth() * width
//					/ mContainer.getWidth();
//			int cropHeight = mCropLayout.getHeight() * height
//					/ mContainer.getHeight();
//
//			setX(x);
//			setY(y);
//			setCropWidth(cropWidth);
//			setCropHeight(cropHeight);
//			// 设置是否需要截图
//			setNeedCapture(true);
//
//		} catch (IOException ioe) {
//			return;
//		} catch (RuntimeException e) {
//			return;
//		}
//		if (handler == null) {
//			handler = new CaptureActivityHandler(this);
//		}
//	}
//
//	@Override
//	public void surfaceCreated(SurfaceHolder holder) {
//		if (!hasSurface) {
//			hasSurface = true;
//			initCamera(holder);
//		}
//	}
//
//	@Override
//	public void surfaceDestroyed(SurfaceHolder holder) {
//		hasSurface = false;
//
//	}
//
//	@Override
//	public Handler getHandler() {
//		return handler;
//	}
//
//	private void initBeepSound() {
//		if (playBeep && mediaPlayer == null) {
//			getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
//			mediaPlayer = new MediaPlayer();
//			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//			mediaPlayer.setOnCompletionListener(beepListener);
//
//			AssetFileDescriptor file = getResources().openRawResourceFd(
//					R.raw.beep);
//			try {
//				mediaPlayer.setDataSource(file.getFileDescriptor(),
//						file.getStartOffset(), file.getLength());
//				file.close();
//				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
//				mediaPlayer.prepare();
//			} catch (IOException e) {
//				mediaPlayer = null;
//			}
//		}
//	}
//
//	private void playBeepSoundAndVibrate() {
//		if (playBeep && mediaPlayer != null) {
//			mediaPlayer.start();
//		}
//	}
//
//	private final OnCompletionListener beepListener = new OnCompletionListener() {
//		@Override
//		public void onCompletion(MediaPlayer mediaPlayer) {
//			mediaPlayer.seekTo(0);
//		}
//	};
//	private Button bt_post;
//
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//	}
//
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//	}
//
//	@Override
//	public void surfaceChanged(SurfaceHolder holder, int format, int width,
//			int height) {
//
//	}
//
//}
