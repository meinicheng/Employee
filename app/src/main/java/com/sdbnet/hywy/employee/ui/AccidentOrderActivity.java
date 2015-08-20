package com.sdbnet.hywy.employee.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.text.method.ReplacementTransformationMethod;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.ErrLogUtils;
import com.sdbnet.hywy.employee.utils.LogUtil;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.UtilsJava;
import com.zbar.lib.ICaptureHandler;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

public class AccidentOrderActivity extends BaseActivity implements Callback,
		ICaptureHandler {
	private static final String TAG = "ReportQueryActivity";

	private CaptureActivityHandler handler;
	private boolean hasSurface;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.50f;
	private boolean vibrate;
	private int x = 0;
	private int y = 0;
	private int cropWidth = 0;
	private int cropHeight = 0;
	private RelativeLayout mContainer = null;
	private RelativeLayout mCropLayout = null;
	private boolean isNeedCapture = false;
	private ArrayList<String> scanList = new ArrayList<String>();
	private EditText mEditOrder;
	private ImageView mImgBack;
	private TextView mTextTitle;
	private TextView mTextOrderNum;
	private Button mBtnLookOrder;
	private static final int DELAY_SCAN_TIME = 1000;

	// public static final int REQUEST_CODE_LOOK_ORDER = 11;
	public static final int REQUEST_CODE_ADD_REPORT = 12;
	// public static final int RESULT_CODE_LOOK_ORDER = 21;
	public static final int RESULT_CODE_ADD_REPORT = 22;
	public static final String EXTRA_ORDER_LIST = "order_list";

	@Override
	public boolean isNeedCapture() {
		return isNeedCapture;
	}

	@Override
	public void setNeedCapture(boolean isNeedCapture) {
		this.isNeedCapture = isNeedCapture;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public void setX(int x) {
		this.x = x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public void setY(int y) {
		this.y = y;
	}

	@Override
	public int getCropWidth() {
		return cropWidth;
	}

	@Override
	public void setCropWidth(int cropWidth) {
		this.cropWidth = cropWidth;
	}

	@Override
	public int getCropHeight() {
		return cropHeight;
	}

	@Override
	public void setCropHeight(int cropHeight) {
		this.cropHeight = cropHeight;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActivityStackManager.getStackManager().pushActivity(this);
		setContentView(R.layout.activity_bar_code_scan);
		initBaseData();
		initControls();
	}

	private void initBaseData() {
		if (TextUtils.isEmpty(PreferencesUtil.user_company)) {
			PreferencesUtil.initStoreData();
		}

		// 初始化 CameraManager
		CameraManager.init(getApplication());
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		// 设置扫描线动作
		ImageView mQrLineView = (ImageView) findViewById(R.id.activity_scan_code_capture_scan_line);
		TranslateAnimation mAnimation = new TranslateAnimation(
				Animation.ABSOLUTE, 0f, Animation.ABSOLUTE, 0f,
				Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT,
				0.9f);
		mAnimation.setDuration(1500);
		mAnimation.setRepeatCount(-1);
		mAnimation.setRepeatMode(Animation.REVERSE);
		mAnimation.setInterpolator(new LinearInterpolator());
		mQrLineView.setAnimation(mAnimation);
	}

	private void initControls() {


		mContainer = (RelativeLayout) findViewById(R.id.activity_scan_code_capture_containter);
		mCropLayout = (RelativeLayout) findViewById(R.id.activity_scan_code_capture_crop_layout);

		mEditOrder = (EditText) findViewById(R.id.activity_scan_code_et_bar_code);
		mEditOrder.setHint(String.format(getString(R.string.barcode_hint),
				PreferencesUtil.ordtitle));
		mEditOrder.setTransformationMethod(new AllCapTransformationMethod());
		mEditOrder.setKeyListener(new MyNumberKeyListener());

		mBtnLookOrder = (Button) findViewById(R.id.activity_scan_code_bt_complete);
		mBtnLookOrder.setText(String.format(getString(R.string.barcode_record),
				PreferencesUtil.ordtitle));

		mTextScanCount = (TextView) findViewById(R.id.activity_scan_code_tv_scan_order_count);
		mTextScanCount.setText(String.format(getString(R.string.barcode_total),
				PreferencesUtil.ordtitle, 0));
		mTextOrderNum = (TextView) findViewById(R.id.activity_scan_code_tv_scan_order_number);


//		mImgBack = (ImageView) findViewById(R.id.activity_scan_code_about_imageview_gohome);
		// ((TextView) findViewById(R.id.activity_scan_code_tv_details_title))
		// .setText(getString(R.string.accident_report));
		mTextTitle=(TextView) findViewById(R.id.common_view_title_text);
		mTextTitle.setText(R.string.accident_report);
		mImgBack = (ImageView) findViewById(R.id.common_view_title_img);
		mImgBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * 监听用户输入，自动将小写字母转换为大写
	 * 
	 * @author Administrator
	 * 
	 */
	public class AllCapTransformationMethod extends
			ReplacementTransformationMethod {

		@Override
		protected char[] getOriginal() {
			char[] aa = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
					'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
					'w', 'x', 'y', 'z' };
			return aa;
		}

		@Override
		protected char[] getReplacement() {
			char[] cc = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
					'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
					'W', 'X', 'Y', 'Z' };
			return cc;
		}
	}

	private class MyNumberKeyListener extends NumberKeyListener {

		@Override
		protected char[] getAcceptedChars() {
			char[] numberChars = new char[] { 'a', 'b', 'c', 'd', 'e', 'f',
					'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
					's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
					'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
					'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2',
					'3', '4', '5', '6', '7', '8', '9', '0' };
			return numberChars;
		}

		@Override
		public int getInputType() {
			// TODO Auto-generated method stub
			return InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
		}

	}

	// @Override
	// protected void onStart() {
	// super.onStart();
	// // scanOrderNumber = "";
	// mTextOrderNum.setText("");
	// mTextOrderNum.setBackground(null);
	// }

	boolean flag = true;

	protected void light() {
		if (flag == true) {
			flag = false;
			// 开闪光灯
			CameraManager.get().openLight();
		} else {
			flag = true;
			// 关闪光灯
			CameraManager.get().offLight();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.activity_scan_code_capture_preview);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			// surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			// 铃音非正常模式
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	public void onDestroy() {
		// ActivityStackManager.getStackManager().popActivity(this);
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * 处理扫描结果
	 */
	@Override
	public void handleDecode(String result) {
		LogUtil.e(TAG, "scan result=" + result);
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();

		// if (TextUtils.isEmpty(PreferencesUtil.user_company)) {
		// PreferencesUtil.initStoreData();
		// }
		// 判断订单号长度
		if (result.length() != PreferencesUtil.code_length) {
			QRcodeScan(result);
		} else {
			barCodeScan(result);
		}
	}

	private void addOrderData(String orderId) {

		if (scanList.contains(orderId)) {
			showShortToast(R.string.order_alreader_add);
			// 延迟1秒，减少扫描的过分灵敏度
			handler.sendEmptyMessageDelayed(R.id.restart_preview,
					DELAY_SCAN_TIME);
		} else if (scanList.size() < Constants.Value.SCAN_COUNT) {

			scanList.add(0, orderId);
			saveOperateLog("扫描单号：" + orderId);
			Set<String> set = new HashSet<String>();
			set.addAll(scanList);

			String msg = String.format(getString(R.string.added_x_list),
					PreferencesUtil.ordtitle);
			// "已添加到" + PreferencesUtil.ordtitle + "列表中",
			showShortToast(msg);
			// 延迟1秒，减少扫描的过分灵敏度
			handler.sendEmptyMessageDelayed(R.id.restart_preview,
					DELAY_SCAN_TIME);
		} else {
			showShortToast(R.string.one_time_add_100);
		}

		mTextScanCount.setText(String.format(getString(R.string.barcode_total),
				PreferencesUtil.ordtitle, scanList.size()));
		mTextOrderNum.setText(scanList.get(0));
		mTextOrderNum.setBackgroundResource(R.drawable.bg_line);

	}

	/**
	 * 初始化摄像头
	 * 
	 * @param surfaceHolder
	 */
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);

			Point point = CameraManager.get().getCameraResolution();
			int width = point.y;
			int height = point.x;

			int x = mCropLayout.getLeft() * width / mContainer.getWidth();
			int y = mCropLayout.getTop() * height / mContainer.getHeight();

			int cropWidth = mCropLayout.getWidth() * width
					/ mContainer.getWidth();
			int cropHeight = mCropLayout.getHeight() * height
					/ mContainer.getHeight();

			setX(x);
			setY(y);
			setCropWidth(cropWidth);
			setCropHeight(cropHeight);
			// 设置是否需要截图
			// setNeedCapture(true);

		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(AccidentOrderActivity.this);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	@Override
	public Handler getHandler() {
		return handler;
	}

	/**
	 * 扫描提示音
	 */
	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	/**
	 * 扫描振动
	 */
	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
	private TextView mTextScanCount;

	// private ExecuteAction action;
	// private ExecuteAction mOrderModel;

	/**
	 * 用户手动输入订单号
	 * 
	 * @param view
	 */
	public void postOrderId(View view) {
		String barCode = mEditOrder.getText().toString().trim();

		if (TextUtils.isEmpty(barCode)) {
			String msg = String.format(getString(R.string.please_input_x_num),
					PreferencesUtil.ordtitle);
			showShortToast(msg);

		} else if (barCode.length() != PreferencesUtil.code_length) {
			String msg = String.format(
					getString(R.string.scan_order_result_msg_error),
					PreferencesUtil.ordtitle, PreferencesUtil.code_length);
			showShortToast(msg);
		} else {
			barCode = barCode.replace("-", "").toUpperCase();
			mEditOrder.setText("");
			addOrderData(barCode);

		}

	}

	/**
	 * 查看已扫描订单
	 * 
	 * @param view
	 */
	public void lookForScanOrders(View view) {

		saveOperateLog(((Button) view).getText().toString());
		if (scanList.size() == 0) {
			String msg = PreferencesUtil.ordtitle
					+ getString(R.string.list_empty);
			showShortToast(msg);// "列表为空",);
			return;
		}
		Intent data = new Intent(this, ScanOrdersActivity.class);
		data.putStringArrayListExtra(ScanOrdersActivity.EXTRA_ORDER_LIST,
				scanList);
		startActivityForResult(data, ScanOrdersActivity.REQUEST_CODE_LOOK_ORDER);
	}

	/**
	 * 扫描完成后，用户点击下一步的响应事件
	 * 
	 * @param view
	 */
	public void completeScan(View view) {
		saveOperateLog(((Button) view).getText().toString());
		if (scanList.size() == 0) {
			String msg = String.format(getString(R.string.please_add_x_commit),
					PreferencesUtil.ordtitle);
			// "请添加" +PreferencesUtil.ordtitle + "后再提交",
			showShortToast(msg);
			return;
		}

		PreferencesUtil.setSteps(Constants.Step.NEXT);

		// 跳转到添加Report
		Intent data = new Intent(this, AccidentAddActivity.class);
		data.putStringArrayListExtra(EXTRA_ORDER_LIST, scanList);
		startActivityForResult(data, REQUEST_CODE_ADD_REPORT);
		// finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (ScanOrdersActivity.RESULT_CODE_LOOK_ORDER == resultCode) {
			// 从订单查看列表页返回
			scanList = data
					.getStringArrayListExtra(ScanOrdersActivity.EXTRA_ORDER_LIST);
		} else if (RESULT_CODE_ADD_REPORT == resultCode) {
			// 从异常报告提交页返回，清空扫描订单缓存
			scanList.clear();
		}
		// 去除重复数据，更新界面提示信息
		Set<String> set = new HashSet<String>();
		set.addAll(scanList);
		mTextScanCount.setText(String.format(getString(R.string.barcode_total),
				PreferencesUtil.ordtitle, set.size()));
		if (scanList.size() == 0) {
			mTextOrderNum.setText("");
			mTextOrderNum.setBackground(null);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void barCodeScan(String barCode) {
		barCode = barCode.replace("-", "").toUpperCase();
		addOrderData(barCode);
		// 延迟1秒，减少扫描的过分灵敏度
		handler.sendEmptyMessageDelayed(R.id.restart_preview, DELAY_SCAN_TIME);
	}

	// 二维码判断;
	private boolean QRcodeScan(String jsonQR) {
		// {"cmpid":"8888","itemid":"01",
		// "ordernos":[{"orderno":"1234567890123"},
		// {"orderno":"1234567890123"},
		// {"orderno":"1234567890123"},..]}

		try {
			JSONObject jsonObject = new JSONObject(jsonQR);
			String cmpid = jsonObject.getString("cmpid");
			String itemid = jsonObject.getString("itemid");
			JSONArray jsonOrdernos = jsonObject.getJSONArray("ordernos");
			if (scanList.size() + jsonOrdernos.length() > Constants.Value.SCAN_COUNT) {
				showShortToast(R.string.one_time_add_100);
			} else {
				for (int i = 0; i < jsonOrdernos.length(); i++) {
					JSONObject jsonOrder = (JSONObject) jsonOrdernos.get(i);
					String orderno = jsonOrder.getString("orderno")
							.toUpperCase();
					if (!scanList.contains(orderno.toUpperCase())) {
						scanList.add(0, orderno.toUpperCase());
					}
				}
			}
			mTextScanCount.setText(String.format(
					getString(R.string.barcode_total),
					PreferencesUtil.ordtitle, scanList.size()));
			mTextOrderNum.setText(scanList.get(0));
			mTextOrderNum.setBackgroundResource(R.drawable.bg_line);
			// if (scanList.size() < Constants.Value.SCAN_COUNT) {
			// 延迟1秒，减少扫描的过分灵敏度
			handler.sendEmptyMessageDelayed(R.id.restart_preview,
					DELAY_SCAN_TIME);
		} catch (Exception ex) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview,
					DELAY_SCAN_TIME);
			String msg = "不是正确的二维码格式或"
					+ String.format("%s号长度不是%d个字符，不符合长度要求",
							PreferencesUtil.ordtitle,
							PreferencesUtil.code_length);
			showShortToast(msg);
			ex.printStackTrace();
			String time = UtilsJava.translate2SessionMessageData(System
					.currentTimeMillis());
			String errorMsg = "Time=" + time + "\n" + jsonQR.toString() + "\n"
					+ ErrLogUtils.toString(ex);
			ErrLogUtils.uploadErrLog(this, errorMsg);
			return false;
		}
		return true;

	}
}