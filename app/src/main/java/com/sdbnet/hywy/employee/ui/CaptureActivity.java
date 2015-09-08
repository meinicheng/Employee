package com.sdbnet.hywy.employee.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
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
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.album.AlbumHelper;
import com.sdbnet.hywy.employee.album.AlbumHelper.ImageItem;
import com.sdbnet.hywy.employee.model.ExecuteAction;
import com.sdbnet.hywy.employee.net.AsyncHttpService;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.ErrLogUtils;
import com.sdbnet.hywy.employee.utils.LogUtil;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.UtilsAndroid;
import com.sdbnet.hywy.employee.utils.UtilsCommon;
import com.sdbnet.hywy.employee.utils.UtilsError;
import com.sdbnet.hywy.employee.utils.UtilsJava;
import com.zbar.lib.ICaptureHandler;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

public class CaptureActivity extends BaseActivity implements Callback,
        ICaptureHandler, OnClickListener {
    private static final String TAG = "CaptureActivity";

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
    private ImageView mImgFlash;
    private TextView mTextOrderNum;
    private boolean isThirdParty = false;
    private int scanCount;
    private ArrayList<String> mThirdOrderList;

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

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ActivityStackManager.getStackManager().pushActivity(this);
        setContentView(R.layout.activity_bar_code_scan);
        initCamera();

        initBaseData();

        initControls();
        initAnimation();
    }

    private void initCamera() {
        // 初始化 CameraManager
        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

    }

    private String mScanModel;

    private void initBaseData() {
        // test is third party
        isThirdParty = false;
        mThirdOrderList = new ArrayList<String>();
        // init store data;
        if (TextUtils.isEmpty(PreferencesUtil.user_company)) {
            PreferencesUtil.initStoreData();
        }
        // get Order data , copy order data;
        mOrderModel = (ExecuteAction) getIntent().getExtras().getSerializable(
                Constants.Feild.KEY_ACTION);
        mNewOrder = (ExecuteAction) UtilsJava.depthClone(mOrderModel);
        // get Scan Model;
        String options = PreferencesUtil.getValue(Constants.Feild.KEY_OPTIONS);
        if (!TextUtils.isEmpty(options)) {
            try {
                JSONObject jsonObject = new JSONObject(options);
                if (!jsonObject.isNull("scanModel"))
                    mScanModel = jsonObject.getString("scanModel");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void initAnimation() {
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
        // init
        mContainer = (RelativeLayout) findViewById(R.id.activity_scan_code_capture_containter);
        mCropLayout = (RelativeLayout) findViewById(R.id.activity_scan_code_capture_crop_layout);
        mEditOrder = (EditText) findViewById(R.id.activity_scan_code_et_bar_code);
        Button mBtnAddOrder = (Button) findViewById(R.id.activity_scan_code_bt_add);
        Button mBtnLookOrder = (Button) findViewById(R.id.activity_scan_code_bt_look);
        Button mBtnOrderComplete = (Button) findViewById(R.id.activity_scan_code_bt_complete);
        mTextScanCount = (TextView) findViewById(R.id.activity_scan_code_tv_scan_order_count);
        mTextOrderNum = (TextView) findViewById(R.id.activity_scan_code_tv_scan_order_number);
        TextView mTextTitle = (TextView) findViewById(R.id.common_view_title_text);
        ImageView mImgBack = (ImageView) findViewById(R.id.common_view_title_img);
        mImgFlash = (ImageView) findViewById(R.id.common_view_title_img_menu);
        //init ui data;
        mEditOrder.setHint(String.format(getString(R.string.barcode_hint),
                PreferencesUtil.ordtitle));
        mEditOrder.setTransformationMethod(new AllCapTransformationMethod());
        mEditOrder.setKeyListener(new MyNumberKeyListener());
        mBtnLookOrder.setText(String.format(getString(R.string.barcode_record),
                PreferencesUtil.ordtitle));
        mTextTitle.setText(mNewOrder.getBtnname());
        mImgFlash.setVisibility(View.VISIBLE);
        LayoutParams lp = mImgFlash.getLayoutParams();
        lp.width = UtilsAndroid.UI.dip2px(this, 40);
        lp.height = UtilsAndroid.UI.dip2px(this, 40);
        mImgFlash.setLayoutParams(lp);
        mImgFlash.setImageResource(R.drawable.flash_light_white);
        mTextScanCount.setText(String.format(getString(R.string.barcode_total),
                PreferencesUtil.ordtitle, 0));
        //init ui listener;
        mImgBack.setOnClickListener(this);
        mBtnAddOrder.setOnClickListener(this);
        mImgFlash.setOnClickListener(this);
        mBtnLookOrder.setOnClickListener(this);
        mBtnOrderComplete.setOnClickListener(this);


    }

    /**
     * 监听用户输入，自动将小写字母转换为大写
     *
     * @author Administrator
     */
    public class AllCapTransformationMethod extends
            ReplacementTransformationMethod {

        @Override
        protected char[] getOriginal() {
            return new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                    'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
                    'w', 'x', 'y', 'z'};
        }

        @Override
        protected char[] getReplacement() {
            return new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                    'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
                    'W', 'X', 'Y', 'Z'};
        }
    }

    private class MyNumberKeyListener extends NumberKeyListener {

        @Override
        protected char[] getAcceptedChars() {
            return new char[]{'a', 'b', 'c', 'd', 'e', 'f',
                    'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
                    's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
                    'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
                    'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2',
                    '3', '4', '5', '6', '7', '8', '9', '0'};
        }

        @Override
        public int getInputType() {
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
        if (flag) {
            flag = false;
            // 开闪光灯
            mImgFlash.setImageResource(R.drawable.flash_light_red);
            CameraManager.get().openLight();
        } else {
            flag = true;
            // 关闪光灯
            mImgFlash.setImageResource(R.drawable.flash_light_white);
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
        if (isThirdParty && scanCount % 2 == 1) {
            //How to judge whether it is right????
            mThirdOrderList.add(result);
        } else if (result.length() == PreferencesUtil.code_length) {
            barCodeScan(result);
        } else {
            QRcodeScan(result);
        }
        scanCount++;


    }

    private void addOrderData(String orderId) {

        if (scanList.contains(orderId)) {
            showShortToast(R.string.order_alreader_add);
            // 延迟1秒，减少扫描的过分灵敏度
            handler.sendEmptyMessageDelayed(R.id.restart_preview, 1000);
        } else if (scanList.size() < Constants.Value.SCAN_COUNT) {

            scanList.add(0, orderId);
            saveOperateLog("扫描单号：" + orderId, null);
            Set<String> set = new HashSet<String>();
            set.addAll(scanList);

            String msg = String.format(getString(R.string.added_x_list),
                    PreferencesUtil.ordtitle);
            // "已添加到" + PreferencesUtil.ordtitle + "列表中",
            showShortToast(msg);
            // 延迟1秒，减少扫描的过分灵敏度
            handler.sendEmptyMessageDelayed(R.id.restart_preview, 1000);
        } else {
            showShortToast(R.string.one_time_add_100);
        }

        mTextScanCount.setText(String.format(getString(R.string.barcode_total),
                PreferencesUtil.ordtitle, scanList.size()));
        mTextOrderNum.setText(scanList.get(0));
//        mTextOrderNum.setBackgroundResource(R.drawable.bg_line);
        mTextOrderNum.setBackgroundColor(Color.parseColor("#313131"));
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

            int x = mCropLayout.getLeft() *  point.y / mContainer.getWidth();
            int y = mCropLayout.getTop() *  point.x / mContainer.getHeight();

            int cropWidth = mCropLayout.getWidth() *  point.y
                    / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() *  point.x
                    / mContainer.getHeight();

            setX(x);
            setY(y);
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);
            // 设置是否需要截图
            // setNeedCapture(true);

        } catch (IOException | RuntimeException ioe) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(CaptureActivity.this);
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
    private ExecuteAction mOrderModel;
    private ExecuteAction mNewOrder;

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
        saveOperateLog(((Button) view).getText().toString(), null);
        if (scanList.size() == 0) {
            String msg = PreferencesUtil.ordtitle
                    + getString(R.string.list_empty);
            showShortToast(msg);// "列表为空",);
            return;
        }
        // else if (scanList.size() == 1) {}
        else {
            Intent data = new Intent(this, ScanOrdersActivity.class);
            data.putStringArrayListExtra(ScanOrdersActivity.EXTRA_ORDER_LIST,
                    scanList);
            // startActivityForResult(data,
            // Constants.RequestCode.QUERY_ORDER_LIST);
            startActivityForResult(data,
                    ScanOrdersActivity.REQUEST_CODE_LOOK_ORDER);
        }
    }

    /**
     * 扫描完成后，用户点击下一步的响应事件
     *
     * @param view
     */
    public void completeScan(View view) {
        saveOperateLog(((Button) view).getText().toString(), null);
        if (scanList.size() == 0) {
            String msg = String.format(getString(R.string.please_add_x_commit),
                    PreferencesUtil.ordtitle);
            // "请添加" +PreferencesUtil.ordtitle + "后再提交",
            showShortToast(msg);
            return;
        }
        String fromType = getIntent().getStringExtra(
                OrderActionActivity.FILED_TYPE_FROM);
        PreferencesUtil.setSteps(Constants.Step.NEXT);

        // // 将List转化为Set，去除重复订单数据
        // Set<String> set = new HashSet<String>();
        // set.addAll(scanList);
        Log.e(TAG, mScanModel + ">>" + scanList.size() + ">>" + scanList);
        // if (scanList.size() == 1) {
        if (TextUtils.equals(fromType, OrderActionActivity.VALUE_FROM_ACCIDENT)) {
            startToAccident();
        } else if (scanList.size() == 1
                && TextUtils.equals(Constants.Value.STATUS_OK, mScanModel)) {
            requestIsUpdate();
        } else {
            startOrderExecute();
        }
    }

    public static final int REQUEST_CODE_ADD_REPORT = 12;
    public static final int RESULT_CODE_ADD_REPORT = 22;
    public static final String EXTRA_ORDER_LIST = "order_list";

    private void startToAccident() {
        // 跳转到添加Report
        Intent data = new Intent(this, AccidentAddActivity.class);
        data.putStringArrayListExtra(EXTRA_ORDER_LIST, scanList);
        startActivityForResult(data, REQUEST_CODE_ADD_REPORT);
    }

    private void startOrderExecute() {
        // 跳转到订单执行提交页面
        Log.i(TAG, scanList + "<" + scanList.get(0));
        mNewOrder.setActidx(UtilsCommon.list2Strs(scanList));
        Intent data = new Intent(this, OrderExecuteActivity.class);
        data.putExtra(Constants.Feild.KEY_ACTION, mNewOrder);
        data.putExtra(OrderExecuteActivity.FROM_WHERE, this.getClass().getSimpleName());
        startActivityForResult(data,
                OrderExecuteActivity.REQUEST_CODE_EXECUTE_ORDER);
    }

    private boolean isRequestService;

    private void requestIsUpdate() {
        // mNewOrder.setActidx(scanList.get(0));
        Log.d(TAG, "request update=" + scanList.get(0) + "," + mNewOrder.getAction() + "," + mNewOrder);
        AsyncHttpService.requestIsUpdate(scanList.get(0),
                mNewOrder.getAction(), new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoadDialog();
                        mDialogLoading.setCancelable(false);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.e(TAG, response.toString());
                        try {
                            if (!UtilsError.isErrorCode(CaptureActivity.this,
                                    response)) {
                                getOrderData(response);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            dismissLoadDialog();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                        dismissLoadDialog();
                    }
                }, this);
    }

    private void getOrderData(JSONObject response) throws JSONException {
        ArrayList<ImageItem> mImageItems = new ArrayList<AlbumHelper.ImageItem>();
        if (!response.isNull("traceInfo")) {

            String traceid = null, actmemo = null, actmemoinner = null;
            JSONObject jsonObject = response.getJSONObject("traceInfo");
            Log.e(TAG, mNewOrder.toString());
            if (!jsonObject.isNull("traceid"))
                traceid = jsonObject.getString("traceid");
            // mNewOrder.setActidx(traceid);
            if (!jsonObject.isNull("actmemo"))
                actmemo = jsonObject.getString("actmemo");
            mNewOrder.setActmemo(actmemo);
            if (!jsonObject.isNull("actmemoinner"))
                actmemoinner = jsonObject.getString("actmemoinner");
            mNewOrder.setActmemoinner(actmemoinner);
            Log.e(TAG, "" + traceid + "<" + actmemo + "<" + actmemoinner);
            if (jsonObject.isNull("pics"))
                return;
            JSONArray jsonArray = jsonObject.getJSONArray("pics");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                ImageItem imageItem = new ImageItem();
                String picid = object.getString("picid");
                String smallimg = object.getString("smallimg");
                String bigimg = object.getString("bigimg");
                imageItem.thumbnailPath = smallimg;
                imageItem.imagePath = bigimg;
                mImageItems.add(imageItem);

            }
            mNewOrder.setImageItems(mImageItems);

        }
        Log.e(TAG, mNewOrder.toString());
        startOrderExecute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ScanOrdersActivity.RESULT_CODE_LOOK_ORDER == resultCode) {
            // 从订单查看列表页返回
            scanList = data
                    .getStringArrayListExtra(ScanOrdersActivity.EXTRA_ORDER_LIST);
        } else if (OrderExecuteActivity.REQUEST_CODE_EXECUTE_ORDER == resultCode) {
            // 从订单执行提交页返回，清空扫描订单缓存
            scanList.clear();


        } else if (RESULT_CODE_ADD_REPORT == resultCode) {
            // 从异常报告提交页返回，清空扫描订单缓存
            scanList.clear();

        }
        mNewOrder = (ExecuteAction) UtilsJava.depthClone(mNewOrder);
        // 去除重复数据，更新界面提示信息
        Set<String> set = new HashSet<String>();
        set.addAll(scanList);
        mTextScanCount.setText(String.format(getString(R.string.barcode_total),
                PreferencesUtil.ordtitle, set.size()));
        if (scanList.size() == 0) {
            mTextOrderNum.setText("");
            mTextOrderNum.setBackgroundColor(Color.parseColor("#00000000"));

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String barCodeScan(String barCode) {
        barCode = barCode.replace("-", "").toUpperCase();

        addOrderData(barCode);
        // 延迟1秒，减少扫描的过分灵敏度
        handler.sendEmptyMessageDelayed(R.id.restart_preview, 1000);
        return barCode;
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
//            mTextOrderNum.setBackgroundResource(R.drawable.bg_line);
            mTextOrderNum.setBackgroundColor(Color.parseColor("#313131"));
            if (scanList.size() < Constants.Value.SCAN_COUNT) {
                // 延迟1秒，减少扫描的过分灵敏度
                handler.sendEmptyMessageDelayed(R.id.restart_preview, 1000);
            }

        } catch (Exception ex) {
            handler.sendEmptyMessageDelayed(R.id.restart_preview, 1000);
            String msg = String.format(getString(R.string.order_scan_error_msg),
                    PreferencesUtil.ordtitle + "",
                    PreferencesUtil.code_length + "");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_view_title_img:
                finish();
                break;
            case R.id.common_view_title_img_menu:
                light();
                break;
            case R.id.activity_scan_code_bt_complete:
                completeScan(v);
                break;
            case R.id.activity_scan_code_bt_look:
                lookForScanOrders(v);
                break;
            case R.id.activity_scan_code_bt_add:
                postOrderId(v);
                break;

            default:
                break;
        }

    }
}