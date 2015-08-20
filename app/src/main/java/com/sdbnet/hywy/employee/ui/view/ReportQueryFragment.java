//package com.sdbnet.hywy.employee.ui.view;
//
//import android.app.Dialog;
//import android.media.MediaPlayer;
//import android.os.Handler;
//import android.view.SurfaceHolder;
//import android.view.View;
//import android.view.SurfaceHolder.Callback;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import com.sdbnet.hywy.employee.ui.base.BaseFrament;
//import com.zbar.lib.ICaptureHandler;
//import com.zbar.lib.decode.CaptureActivityHandler;
//import com.zbar.lib.decode.InactivityTimer;
//
//public class ReportQueryFragment extends BaseFrament implements Callback,
//		ICaptureHandler {
//
//	private CaptureActivityHandler handler;
//	private boolean hasSurface;
//	private InactivityTimer inactivityTimer;
//	private MediaPlayer mediaPlayer;
//	private boolean playBeep;
//	private static final float BEEP_VOLUME = 0.50f;
//	private boolean vibrate;
//	private int x = 0;
//	private int y = 0;
//	private int cropWidth = 0;
//	private int cropHeight = 0;
//	private RelativeLayout mContainer = null;
//	private RelativeLayout mCropLayout = null;
//	private boolean isNeedCapture = false;
//	private EditText et_bar_code;
//	private View mBack;
//	private TextView tv_scan_order_number;
//	private LinearLayout lay_input_bar_code;
//	private Dialog dialog;
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
//	//
//	// private View view;
//	// @Override
//	// public View onCreateView(LayoutInflater inflater, ViewGroup container,
//	// Bundle savedInstanceState) {
//	// view =inflater.inflate(R.layout.activity_bar_code_scan, null);
//	// return super.onCreateView(inflater, container, savedInstanceState);
//	//
//	// }
//	// private void initUI(){
//	//
//	// // 初始化 CameraManager
//	// CameraManager.init(getApplication());
//	// hasSurface = false;
//	// inactivityTimer = new InactivityTimer(this);
//	//
//	// mContainer = (RelativeLayout)view. findViewById(R.id.capture_containter);
//	// mCropLayout = (RelativeLayout)view.
//	// findViewById(R.id.capture_crop_layout);
//	// lay_input_bar_code = (LinearLayout) view.
//	// findViewById(R.id.lay_input_bar_code);
//	// lay_input_bar_code.getBackground().setAlpha(80);
//	//
//	// // 设置扫描线动画
//	// ImageView mQrLineView = (ImageView)view.
//	// findViewById(R.id.capture_scan_line);
//	// TranslateAnimation mAnimation = new TranslateAnimation(
//	// TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE,
//	// 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f,
//	// TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
//	// mAnimation.setDuration(1500);
//	// mAnimation.setRepeatCount(-1);
//	// mAnimation.setRepeatMode(Animation.REVERSE);
//	// mAnimation.setInterpolator(new LinearInterpolator());
//	// mQrLineView.setAnimation(mAnimation);
//	//
//	// tv_scan_order_number = (TextView) view.
//	// findViewById(R.id.tv_scan_order_number);
//	// et_bar_code = (EditText) view. findViewById(R.id.et_bar_code);
//	// et_bar_code.setTransformationMethod(new AllCapTransformationMethod());
//	//
//	// mBack = (ImageView) view. findViewById(R.id.about_imageview_gohome);
//	// mBack.setOnClickListener(new View.OnClickListener() {
//	// @Override
//	// public void onClick(View v) {
//	// finish();
//	// }
//	// });
//	// dialog = createLoadingDialog(this, "正在加载...");
//	// }
//	// @Override
//	// public void onActivityCreated(Bundle savedInstanceState) {
//	// // TODO Auto-generated method stub
//	// super.onActivityCreated(savedInstanceState);
//	//
//	//
//	//
//	// }
//	//
//	// boolean flag = true;
//	// private String scanOrderNumber;
//	//
//	// protected void light() {
//	// if (flag == true) {
//	// flag = false;
//	// // 开闪光灯
//	// CameraManager.get().openLight();
//	// } else {
//	// flag = true;
//	// // 关闪光灯
//	// CameraManager.get().offLight();
//	// }
//	//
//	// }
//	//
//	// @Override
//	// protected void onStart() {
//	// super.onStart();
//	// tv_scan_order_number.setText("");
//	// tv_scan_order_number.setBackgroundDrawable(null);
//	// }
//	//
//	// @SuppressWarnings("deprecation")
//	// @Override
//	// protected void onResume() {
//	// super.onResume();
//	// SurfaceView surfaceView = (SurfaceView)
//	// findViewById(R.id.capture_preview);
//	// SurfaceHolder surfaceHolder = surfaceView.getHolder();
//	// if (hasSurface) {
//	// initCamera(surfaceHolder);
//	// } else {
//	// surfaceHolder.addCallback(this);
//	// surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//	// }
//	// playBeep = true;
//	// AudioManager audioService = (AudioManager)
//	// getSystemService(AUDIO_SERVICE);
//	// if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
//	// // 铃音非正常模式
//	// playBeep = false;
//	// }
//	// initBeepSound();
//	// vibrate = true;
//	// }
//	//
//	// @Override
//	// protected void onPause() {
//	// super.onPause();
//	// if (handler != null) {
//	// handler.quitSynchronously();
//	// handler = null;
//	// }
//	// CameraManager.get().closeDriver();
//	// }
//	//
//	// @Override
//	// public void onDestroy() {
//	// inactivityTimer.shutdown();
//	// super.onDestroy();
//	// }
//	//
//	// public void onBackPressed() {
//	// finish();
//	// }
//	//
//	// /**
//	// * 查看扫描订单
//	// *
//	// * @param view
//	// */
//	// public void lookForScanOrder(View view) {
//	// if (TextUtils.isEmpty(scanOrderNumber)) {
//	// if (TextUtils.isEmpty(et_bar_code.getText().toString().trim())) {
//	// Toast.makeText(this, "请先输入订单号", Toast.LENGTH_SHORT).show();
//	// return;
//	// } else {
//	// scanOrderNumber = et_bar_code.getText().toString().trim();
//	// }
//	// }
//	//
//	// if (scanOrderNumber.length() != PreferencesUtil.code_length) {
//	// scanOrderNumber = "";
//	// Toast.makeText(
//	// this,
//	// String.format("订单号长度不是%d个字符，不符合长度要求",
//	// PreferencesUtil.code_length), Toast.LENGTH_SHORT)
//	// .show();
//	// retryScan();
//	// return;
//	// }
//	//
//	// // scanOrderNumber =
//	// // scanOrderNumber.replace(PreferencesUtil.user_company +
//	// // PreferencesUtil.item_id, "");
//	// // scanOrderNumber =
//	// // scanOrderNumber.replace(PreferencesUtil.user_company.toLowerCase() +
//	// // PreferencesUtil.item_id.toLowerCase(), "");
//	// scanOrderNumber = scanOrderNumber.replace("-", "").toUpperCase();
//	// System.out.println("scanOrderNumber " + scanOrderNumber);
//	// tv_scan_order_number.setText(scanOrderNumber);
//	// tv_scan_order_number.setBackgroundResource(R.drawable.bg_line);
//	//
//	// String sysnox = getOriginalPlatformOrderId(); // 拼接平台订单号
//	// AsyncHttpService.traceOrder(sysnox, new JsonHttpResponseHandler() {
//	// private int errCode;
//	// private String msg;
//	//
//	// @Override
//	// public void onStart() {
//	// super.onStart();
//	// dialog.show();
//	// }
//	//
//	// @Override
//	// public void onFailure(int statusCode, Header[] headers,
//	// Throwable throwable, JSONObject errorResponse) {
//	// super.onFailure(statusCode, headers, throwable, errorResponse);
//	// dialog.dismiss();
//	// scanOrderNumber = "";
//	// retryScan();
//	// }
//	//
//	// @Override
//	// public void onSuccess(int statusCode, Header[] headers,
//	// JSONObject response) {
//	// System.out.println("response:" + response.toString());
//	// super.onSuccess(statusCode, headers, response);
//	// try {
//	// errCode = response.getInt(Constants.Feild.KEY_ERROR_CODE);
//	// if (errCode != 0) {
//	// String msg = response
//	// .getString(Constants.Feild.KEY_MSG);
//	// switch (response.getInt(Constants.Feild.KEY_ERROR_CODE)) {
//	// case 41:
//	// returnLogin(CaptureActivity.this, msg, dialog);
//	// break;
//	// case 42:
//	// returnLogin(CaptureActivity.this, msg, dialog);
//	// break;
//	// default:
//	// showLongToast(msg);
//	// scanOrderNumber = "";
//	// dialog.dismiss();
//	// retryScan();
//	// break;
//	// }
//	// return;
//	// }
//	//
//	// // JSONObject jsonObject =
//	// // response.getJSONObject(Constants.Feild.KEY_ORDER);
//	// PreferencesUtil.putValue(PreferencesUtil.KEY_SCAN_ACTIONS,
//	// response.getString(Constants.Feild.KEY_ORDER));
//	//
//	// // 跳转到订单跟踪日志界面
//	// Intent data = new Intent(CaptureActivity.this,
//	// OrderTraceListActivity.class);
//	// data.putExtra(Constants.Feild.KEY_ORDER_NO, scanOrderNumber);
//	// startActivity(data);
//	// } catch (Exception e) {
//	// e.printStackTrace();
//	// } finally {
//	// scanOrderNumber = "";
//	// dialog.dismiss();
//	// }
//	// }
//	// }, CaptureActivity.this);
//	//
//	// et_bar_code.setText("");
//	// // finish();
//	// }
//	//
//	// /**
//	// * 获取原始平台订单号
//	// *
//	// * @return
//	// */
//	// private String getOriginalPlatformOrderId() {
//	// return PreferencesUtil.user_company + PreferencesUtil.item_id + "-"
//	// + scanOrderNumber;
//	// }
//	//
//	// /**
//	// * 处理扫描结果
//	// */
//	// public void handleDecode(String result) {
//	// inactivityTimer.onActivity();
//	// playBeepSoundAndVibrate();
//	// scanOrderNumber = result;
//	// lookForScanOrder(null);
//	// }
//	//
//	// private void initCamera(SurfaceHolder surfaceHolder) {
//	// try {
//	// CameraManager.get().openDriver(surfaceHolder);
//	//
//	// Point point = CameraManager.get().getCameraResolution();
//	// int width = point.y;
//	// int height = point.x;
//	//
//	// int x = mCropLayout.getLeft() * width / mContainer.getWidth();
//	// int y = mCropLayout.getTop() * height / mContainer.getHeight();
//	//
//	// int cropWidth = mCropLayout.getWidth() * width
//	// / mContainer.getWidth();
//	// int cropHeight = mCropLayout.getHeight() * height
//	// / mContainer.getHeight();
//	//
//	// setX(x);
//	// setY(y);
//	// setCropWidth(cropWidth);
//	// setCropHeight(cropHeight);
//	// // 设置是否需要截图
//	// // setNeedCapture(true);
//	//
//	// } catch (IOException ioe) {
//	// return;
//	// } catch (RuntimeException e) {
//	// return;
//	// }
//	// if (handler == null) {
//	// handler = new CaptureActivityHandler(CaptureActivity.this);
//	// }
//	// }
//	//
//	// @Override
//	// public void surfaceChanged(SurfaceHolder holder, int format, int width,
//	// int height) {
//	//
//	// }
//	//
//	// @Override
//	// public void surfaceCreated(SurfaceHolder holder) {
//	// if (!hasSurface) {
//	// hasSurface = true;
//	// initCamera(holder);
//	// }
//	// }
//	//
//	// @Override
//	// public void surfaceDestroyed(SurfaceHolder holder) {
//	// hasSurface = false;
//	//
//	// }
//	//
//	// public Handler getHandler() {
//	// return handler;
//	// }
//	//
//	// private void initBeepSound() {
//	// if (playBeep && mediaPlayer == null) {
//	// setVolumeControlStream(AudioManager.STREAM_MUSIC);
//	// mediaPlayer = new MediaPlayer();
//	// mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//	// mediaPlayer.setOnCompletionListener(beepListener);
//	//
//	// AssetFileDescriptor file = getResources().openRawResourceFd(
//	// R.raw.beep);
//	// try {
//	// mediaPlayer.setDataSource(file.getFileDescriptor(),
//	// file.getStartOffset(), file.getLength());
//	// file.close();
//	// mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
//	// mediaPlayer.prepare();
//	// } catch (IOException e) {
//	// mediaPlayer = null;
//	// }
//	// }
//	// }
//	//
//	// private static final long VIBRATE_DURATION = 200L;
//	//
//	// private void playBeepSoundAndVibrate() {
//	// if (playBeep && mediaPlayer != null) {
//	// mediaPlayer.start();
//	// }
//	// if (vibrate) {
//	// Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//	// vibrator.vibrate(VIBRATE_DURATION);
//	// }
//	// }
//	//
//	// private void retryScan() {
//	// // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
//	// handler.sendEmptyMessageDelayed(R.id.restart_preview, 1000);
//	// }
//	//
//	// private final OnCompletionListener beepListener = new
//	// OnCompletionListener() {
//	// public void onCompletion(MediaPlayer mediaPlayer) {
//	// mediaPlayer.seekTo(0);
//	// }
//	// };
//	//
//	// public class AllCapTransformationMethod extends
//	// ReplacementTransformationMethod {
//	//
//	// @Override
//	// protected char[] getOriginal() {
//	// char[] aa = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
//	// 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
//	// 'w', 'x', 'y', 'z' };
//	// return aa;
//	// }
//	//
//	// @Override
//	// protected char[] getReplacement() {
//	// char[] cc = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
//	// 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
//	// 'W', 'X', 'Y', 'Z' };
//	// return cc;
//	// }
//
//	@Override
//	public Handler getHandler() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void handleDecode(String result) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void surfaceCreated(SurfaceHolder holder) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void surfaceChanged(SurfaceHolder holder, int format, int width,
//			int height) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void surfaceDestroyed(SurfaceHolder holder) {
//		// TODO Auto-generated method stub
//
//	}
//
//}
