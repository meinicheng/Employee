package com.sdbnet.hywy.employee.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.adapter.MyBaseAdapter;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.ErrLogUtils;
import com.sdbnet.hywy.employee.utils.LogUtil;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.ToastUtil;
import com.sdbnet.hywy.employee.utils.UtilsAndroid;
import com.sdbnet.hywy.employee.utils.UtilsJava;
import com.zbar.lib.ICaptureHandler;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

import com.sdbnet.hywy.employee.ui.base.BaseActivity;

/**
 * Created by Administrator on 2015/8/24.
 */
public class ThridScanActivity extends BaseActivity implements Callback,
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
    private SurfaceView surfaceView;
    private ImageView mQrLineView;
    private LinearLayout mLlListContainer;
    private ImageView mImgListBack;
    private TextView mTextListTitle;
    private ListView mListOrder;
    private boolean isNeedCapture = false;
    //    private ArrayList<String> mScanList;
    //    private EditText mEditOrder;
    private ImageView mImgFlash;
    private TextView mTextOrderNum;
    private boolean isThirdParty = false;
    //    private int scanCount;
//    private ArrayList<String> mThirdOrderList;
    private List<Map<String, String>> mOrderListMap;
    private ScanResultAdapter mListAdapter;
    private Map mMap;

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
        setContentView(R.layout.activity_thrid_scan);
        initCamera();

        initBaseData();

        initUI();
        initOrderListPage();
        initAnimation();
    }


    private void initListener() {

    }

    private void initCamera() {
        // 初始化 CameraManager
        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

    }


    private void initBaseData() {
        // test is third party
        isThirdParty = false;
//        mScanList = new ArrayList<String>();
//        mThirdOrderList = new ArrayList<String>();
        mOrderListMap = new ArrayList<>();
        mMap = new HashMap();
        // init store data;
        if (TextUtils.isEmpty(PreferencesUtil.user_company)) {
            PreferencesUtil.initStoreData();
        }


    }


    private void initAnimation() {
        // 设置扫描线动作

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

    private void initUI() {
        // init
        surfaceView = (SurfaceView) findViewById(R.id.activity_third_scan_capture_preview);
        mQrLineView = (ImageView) findViewById(R.id.activity_third_scan_capture_scan_line);

        mContainer = (RelativeLayout) findViewById(R.id.activity_third_scan_capture_containter);
        mCropLayout = (RelativeLayout) findViewById(R.id.activity_third_scan_capture_crop_layout);


//        mEditOrder = (EditText) findViewById(R.id.activity_scan_code_et_bar_code);
//        Button mBtnAddOrder = (Button) findViewById(R.id.activity_third_scan_bt_add);
        Button mBtnLookOrder = (Button) findViewById(R.id.activity_third_scan_bt_look);
        Button mBtnOrderComplete = (Button) findViewById(R.id.activity_third_scan_bt_complete);
        mTextScanCount = (TextView) findViewById(R.id.activity_third_scan_tv_scan_order_count);
        mTextOrderNum = (TextView) findViewById(R.id.activity_third_scan_tv_scan_order_number);
        TextView mTextTitle = (TextView) findViewById(R.id.common_view_title_text);
        ImageView mImgBack = (ImageView) findViewById(R.id.common_view_title_img);
        mImgFlash = (ImageView) findViewById(R.id.common_view_title_img_menu);
        //init ui data;
//        mEditOrder.setHint(String.format(getString(R.string.barcode_hint),
//                PreferencesUtil.ordtitle));
//        mEditOrder.setTransformationMethod(new AllCapTransformationMethod());
//        mEditOrder.setKeyListener(new MyNumberKeyListener());
        mBtnLookOrder.setText(String.format(getString(R.string.barcode_record),
                PreferencesUtil.ordtitle));
        mTextTitle.setText(getString(R.string.third_scan));
        mImgFlash.setVisibility(View.VISIBLE);
        LayoutParams lp = mImgFlash.getLayoutParams();
        lp.width = UtilsAndroid.UI.dip2px(this, 40);
        lp.height = UtilsAndroid.UI.dip2px(this, 40);
        mImgFlash.setLayoutParams(lp);
//        mImgFlash.setImageResource(R.drawable.flash_light_white);
        mImgFlash.setImageResource(R.drawable.menu_icon);
        mTextScanCount.setText(String.format(getString(R.string.barcode_total),
                PreferencesUtil.ordtitle, 0));
        //init ui listener;
        mImgBack.setOnClickListener(this);
//        mBtnAddOrder.setOnClickListener(this);
        mImgFlash.setOnClickListener(this);
        mBtnLookOrder.setOnClickListener(this);
        mBtnOrderComplete.setOnClickListener(this);


    }

    private void initOrderListPage() {
        mLlListContainer = (LinearLayout) findViewById(R.id.activity_third_scan_ll_list);
        mImgListBack = (ImageView) findViewById(R.id.activity_third_scan_ll_list_back);
        mTextListTitle = (TextView) findViewById(R.id.activity_third_scan_ll_list_title);
        mListOrder = (ListView) findViewById(R.id.activity_third_scan_list);
        mLlListContainer.setVisibility(View.GONE);
        mTextListTitle.setText(String.format(getString(R.string.scanned_barcode),
                PreferencesUtil.ordtitle));
        mImgListBack.setOnClickListener(this);
        mListAdapter = new ScanResultAdapter(mOrderListMap);
        mListOrder.setAdapter(mListAdapter);
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
//            mImgFlash.setImageResource(R.drawable.flash_light_red);
            CameraManager.get().openLight();
        } else {
            flag = true;
            // 关闪光灯
//            mImgFlash.setImageResource(R.drawable.flash_light_white);
            CameraManager.get().offLight();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

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
//        if (isThirdParty && scanCount % 2 == 1) {
//        if (scanCount % 2 == 1) {
        if (mScanModel.equals(MODEL_THIRD)) {
            //How to judge whether it is right????
//            mThirdOrderList.add(0, result);
            switchScanMode();
            if (mMap.get(MODEL_CLIENT) == null || mMap.get(MODEL_THIRD) != null) {
                ToastUtil.show(this, "map value error:" + mMap.get(MODEL_CLIENT) + "," + mMap.get(MODEL_THIRD));
            }
            mMap.put(MODEL_THIRD, result);
            mOrderListMap.add(0, mMap);
//            scanCount++;
        } else if (result.length() == PreferencesUtil.code_length) {
            mMap = new HashMap();
            barCodeScan(result);
        } else {
            String msg = String.format(getString(R.string.scan_order_result_msg_error),
                    PreferencesUtil.ordtitle + "",
                    PreferencesUtil.code_length + "");

//            QRcodeScan(result);
        }


    }

    private void addOrderData(String orderId) {

//        if (mScanList.contains(orderId)) {
        if (isContainsOrder(orderId, MODEL_CLIENT)) {
            showShortToast(R.string.order_alreader_add);
//            // 延迟1秒，减少扫描的过分灵敏度
//            handler.sendEmptyMessageDelayed(R.id.restart_preview, 1000);
//        } else if (mScanList.size() < Constants.Value.SCAN_COUNT) {
        } else if (mOrderListMap.size() < Constants.Value.SCAN_COUNT) {

//            mScanList.add(0, orderId);
            saveOperateLog("扫描单号：" + orderId, null);
//            Set<String> set = new HashSet<String>();
//            set.addAll(mScanList);
            mMap.put(MODEL_CLIENT, orderId);
            String msg = String.format(getString(R.string.added_x_list),
                    PreferencesUtil.ordtitle);
            // "已添加到" + PreferencesUtil.ordtitle + "列表中",
//            scanCount++;
            switchScanMode();
            showShortToast(msg);
//            // 延迟1秒，减少扫描的过分灵敏度
//            handler.sendEmptyMessageDelayed(R.id.restart_preview, 1000);
        } else {
            showShortToast(R.string.one_time_add_100);
        }

//        mTextScanCount.setText(String.format(getString(R.string.barcode_total),
//                PreferencesUtil.ordtitle, mScanList.size()));
//        mTextOrderNum.setText(mScanList.get(0));
        mTextScanCount.setText(String.format(getString(R.string.barcode_total),
                PreferencesUtil.ordtitle, mOrderListMap.size()));
        mTextOrderNum.setText(mMap.get(MODEL_CLIENT) + "");
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

            int x = mCropLayout.getLeft() * point.y / mContainer.getWidth();
            int y = mCropLayout.getTop() * point.x / mContainer.getHeight();

            int cropWidth = mCropLayout.getWidth() * point.y
                    / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * point.x
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
            handler = new CaptureActivityHandler(ThridScanActivity.this);
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


    /**
     * 用户手动输入订单号
     *
     * @param view
     */
//    public void postOrderId(View view) {
    private void postOrderId(String barCode) {
//        String barCode = mEditOrder.getText().toString().trim();

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
//            mEditOrder.setText("");
            addOrderData(barCode);

        }

    }

    /**
     * 查看已扫描订单
     *
     * @param view
     */
    private void lookForScanOrders(View view) {
        saveOperateLog(((Button) view).getText().toString(), null);
//        if (mScanList.size() == 0) {
        if (mOrderListMap.size() == 0) {
            String msg = PreferencesUtil.ordtitle
                    + getString(R.string.list_empty);
            showShortToast(msg);// "列表为空",);
            return;
        }
        // else if (mScanList.size() == 1) {}
        else {
            Intent data = new Intent(this, ScanOrdersActivity.class);
//            data.putStringArrayListExtra(ScanOrdersActivity.EXTRA_ORDER_LIST,
//                    mScanList);
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
    private void completeScan(View view) {
        saveOperateLog(((Button) view).getText().toString(), null);
//        if (mScanList.size() == 0) {
        if (mOrderListMap.size() == 0) {
            String msg = String.format(getString(R.string.please_add_x_commit),
                    PreferencesUtil.ordtitle);
            // "请添加" +PreferencesUtil.ordtitle + "后再提交",
            showShortToast(msg);
            return;
        }
        String fromType = getIntent().getStringExtra(
                OrderActionActivity.FILED_TYPE_FROM);
        PreferencesUtil.setSteps(Constants.Step.NEXT);


        // 跳转到订单执行提交页面
//        mNewOrder.setActidx(UtilsCommon.list2Strs(mScanList));
        Intent data = new Intent(this, OrderExecuteActivity.class);
//        data.putExtra(Constants.Feild.KEY_ACTION, mNewOrder);
        data.putExtra(OrderExecuteActivity.FROM_WHERE, this.getClass().getSimpleName());
        startActivityForResult(data,
                OrderExecuteActivity.REQUEST_CODE_EXECUTE_ORDER);


//        // // 将List转化为Set，去除重复订单数据
//        // Set<String> set = new HashSet<String>();
//        // set.addAll(mScanList);
//        Log.e(TAG, mScanModel + ">>" + mScanList.size() + ">>" + mScanList);
//        // if (mScanList.size() == 1) {
//        if (TextUtils.equals(fromType, OrderActionActivity.VALUE_FROM_ACCIDENT)) {
//            startToAccident();
//        } else if (mScanList.size() == 1
//                && TextUtils.equals(Constants.Value.STATUS_OK, mScanModel)) {
////            requestIsUpdate();
//        } else {
//            startOrderExecute();
//        }
    }

    public static final int REQUEST_CODE_ADD_REPORT = 12;
    public static final int RESULT_CODE_ADD_REPORT = 22;
    public static final String EXTRA_ORDER_LIST = "order_list";

    private void startToAccident() {
        // 跳转到添加Report
        Intent data = new Intent(this, AccidentAddActivity.class);
//        data.putStringArrayListExtra(EXTRA_ORDER_LIST, mScanList);
        startActivityForResult(data, REQUEST_CODE_ADD_REPORT);
    }

    private void startOrderExecute() {
        // 跳转到订单执行提交页面
//        Log.i(TAG, mScanList + "<" + mScanList.get(0));
//        mNewOrder.setActidx(UtilsCommon.list2Strs(mScanList));
        Intent data = new Intent(this, OrderExecuteActivity.class);
//        data.putExtra(Constants.Feild.KEY_ACTION, mNewOrder);
        data.putExtra(OrderExecuteActivity.FROM_WHERE, this.getClass().getSimpleName());
        startActivityForResult(data,
                OrderExecuteActivity.REQUEST_CODE_EXECUTE_ORDER);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (ScanOrdersActivity.RESULT_CODE_LOOK_ORDER == resultCode) {
//            // 从订单查看列表页返回
//            mScanList = data
//                    .getStringArrayListExtra(ScanOrdersActivity.EXTRA_ORDER_LIST);
//        } else if (OrderExecuteActivity.REQUEST_CODE_EXECUTE_ORDER == resultCode) {
//            // 从订单执行提交页返回，清空扫描订单缓存
//            mScanList.clear();
//        } else if (RESULT_CODE_ADD_REPORT == resultCode) {
//            // 从异常报告提交页返回，清空扫描订单缓存
//            mScanList.clear();
//        }
////        mNewOrder = (ExecuteAction) UtilsJava.depthClone(mNewOrder);
//        // 去除重复数据，更新界面提示信息
//        Set<String> set = new HashSet<String>();
//        set.addAll(mScanList);
//        mTextScanCount.setText(String.format(getString(R.string.barcode_total),
//                PreferencesUtil.ordtitle, set.size()));
//        if (mScanList.size() == 0) {
//            mTextOrderNum.setText("");
//            mTextOrderNum.setBackgroundColor(Color.parseColor("#00000000"));
//
//        }
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
//        // {"orderno":"1234567890123"},..]}
//
//        try {
//            JSONObject jsonObject = new JSONObject(jsonQR);
//            String cmpid = jsonObject.getString("cmpid");
//            String itemid = jsonObject.getString("itemid");
//            JSONArray jsonOrdernos = jsonObject.getJSONArray("ordernos");
//            if (mScanList.size() + jsonOrdernos.length() > Constants.Value.SCAN_COUNT) {
//                showShortToast(R.string.one_time_add_100);
//            } else {
//                for (int i = 0; i < jsonOrdernos.length(); i++) {
//                    JSONObject jsonOrder = (JSONObject) jsonOrdernos.get(i);
//                    String orderno = jsonOrder.getString("orderno")
//                            .toUpperCase();
//                    if (!mScanList.contains(orderno.toUpperCase())) {
//                        mScanList.add(0, orderno.toUpperCase());
//                    }
//                }
//            }
//            mTextScanCount.setText(String.format(
//                    getString(R.string.barcode_total),
//                    PreferencesUtil.ordtitle, mScanList.size()));
//            mTextOrderNum.setText(mScanList.get(0));
//            mTextOrderNum.setBackgroundResource(R.drawable.bg_line);
//            if (mScanList.size() < Constants.Value.SCAN_COUNT) {
//                // 延迟1秒，减少扫描的过分灵敏度
//                handler.sendEmptyMessageDelayed(R.id.restart_preview, 1000);
//            }
//
//        } catch (Exception ex) {
//            handler.sendEmptyMessageDelayed(R.id.restart_preview, 1000);
//            String msg = String.format(getString(R.string.order_scan_error_msg),
//                    PreferencesUtil.ordtitle + "",
//                    PreferencesUtil.code_length + "");
//            showShortToast(msg);
//            ex.printStackTrace();
//            String time = UtilsJava.translate2SessionMessageData(System
//                    .currentTimeMillis());
//            String errorMsg = "Time=" + time + "\n" + jsonQR.toString() + "\n"
//                    + ErrLogUtils.toString(ex);
//            ErrLogUtils.uploadErrLog(this, errorMsg);
//            return false;
//        }
        return true;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_view_title_img:
                finish();
                break;
            case R.id.common_view_title_img_menu:
//                light();
                showPopupWindow(v);
                break;
            case R.id.activity_third_scan_bt_complete:
                completeScan(v);
                break;
            case R.id.activity_third_scan_bt_look:
//                lookForScanOrders(v);
                showOrderListPage();
                break;
            case R.id.activity_third_scan_ll_list_back:
                hideOrderListPage();
                break;

            case R.id.activity_scan_code_bt_add:
//                postOrderId(v);
                break;
            default:
                break;
        }
    }

    private String mScanModel = MODEL_CLIENT;
    private static final String MODEL_CLIENT = "model_client";
    private static final String MODEL_THIRD = "model_third";

    private void showPopupWindow(View view) {
        if (popupWindow == null) {
            initPop();
        }
        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.popmenu_bg));
        // 设置好参数之后再show
        if (!popupWindow.isShowing())
            popupWindow.showAsDropDown(view);
        else
            popupWindow.dismiss();
    }

    private PopupWindow popupWindow;

    private void initPop() {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.pop_menu, null);
        // 设置按钮的点击事件
        contentView.findViewById(R.id.pop_menu_text_flash).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                light();
            }
        });
        contentView.findViewById(R.id.pop_menu_text_switch).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                if (mScanModel.equals(MODEL_CLIENT)) {
////                    if (mThirdOrderList.size() > 0)
////                        mThirdOrderList.remove(0);
//                    if (mOrderListMap.size() > 0)
//                        mOrderListMap.remove(0);
//                    else
//                        mMap.remove(MODEL_THIRD);
//                } else {
////                    if (mScanList.size() > 0) {
////                        mScanList.remove(0);
////                    }
//                    if (mOrderListMap.size() > 0)
//                        mOrderListMap.get(0).remove(MODEL_CLIENT);
//                    else
//                        mMap.remove(MODEL_CLIENT);
//                }
                if (mScanModel.equals(MODEL_THIRD))
                    switchScanMode();
                else
                    Toast.makeText(ThridScanActivity.this,R.string.scan_client_msg,Toast.LENGTH_SHORT).show();
            }
        });
        contentView.findViewById(R.id.pop_menu_text_add).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showInputOrderDialog();
            }
        });

        popupWindow = new PopupWindow(contentView,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.i("mengdd", "onTouch : ");

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

    }

    private void showInputOrderDialog() {
        final EditText inputServer = new EditText(this);
        inputServer.setHint(String.format(getString(R.string.barcode_hint),
                PreferencesUtil.ordtitle));
        inputServer.setTransformationMethod(new AllCapTransformationMethod());
        inputServer.setKeyListener(new MyNumberKeyListener());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Server").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                .setNegativeButton(getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String order = inputServer.getText().toString();
                postOrderId(order);
            }
        });
        builder.show();
    }

    private void switchScanMode() {
        if (mScanModel == null) {
            mScanModel = MODEL_THIRD;
        } else if (mScanModel.equals(MODEL_CLIENT)) {
            mScanModel = MODEL_THIRD;
        } else {
            mScanModel = MODEL_CLIENT;
        }


    }

    private boolean isContainsOrder(String orderNum, String type) {
        boolean traverseType = false;
        if (orderNum == null) {
            return false;
        }
        if (TextUtils.equals(type, MODEL_CLIENT)) {
            traverseType = true;
        } else if (TextUtils.equals(type, MODEL_THIRD)) {
            traverseType = false;
        } else {
            return false;
        }

        for (Map map : mOrderListMap) {
            if (traverseType && map.get(MODEL_CLIENT).equals(orderNum))
                return true;
            else if (!traverseType && map.get(MODEL_THIRD).equals(orderNum))
                return true;
        }
        return false;
    }

    private void showOrderListPage() {
        if (mOrderListMap.size() == 0) {
            String msg = PreferencesUtil.ordtitle + getString(R.string.list_empty);
            showShortToast(msg);// "列表为空",);
            return;
        }
        mLlListContainer.setVisibility(View.VISIBLE);

//初始化
        Animation translateAnimation = new TranslateAnimation(0.0f, 0.0f, UtilsAndroid.Set.getScreenHeight(this), 0.0f);
//设置动画时间
        translateAnimation.setDuration(500);
        mLlListContainer.startAnimation(translateAnimation);
    }

    private void hideOrderListPage() {
        //初始化
        Animation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, UtilsAndroid.Set.getScreenHeight(this));
        //设置动画时间
        translateAnimation.setDuration(500);
        mLlListContainer.startAnimation(translateAnimation);
        mLlListContainer.setVisibility(View.GONE);
    }


    class ScanResultAdapter extends MyBaseAdapter<Map<String, String>> {
        private List<Map<String, String>> mList;

        public ScanResultAdapter(List<Map<String, String>> list) {
            super(list);
            mList = list;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(ThridScanActivity.this,
                        R.layout.item_order_third_scan, null);

                viewHolder.tv_bar_code = (TextView) convertView
                        .findViewById(R.id.item_order_third_code_tv_bar_code1);
                viewHolder.tv_bar_code_third = (TextView) convertView
                        .findViewById(R.id.item_order_third_code_tv_bar_code2);
                viewHolder.iv_delete = (ImageView) convertView
                        .findViewById(R.id.item_order_third_code_iv_delete);

                // 点击删除
                viewHolder.iv_delete.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        showDeleteDialog(position);
                    }

                });
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String clientOrder = mList.get(position).get(MODEL_CLIENT);
            String thirdOrder = mList.get(position).get(MODEL_THIRD);
            viewHolder.tv_bar_code.setText("上家："+clientOrder  );
            viewHolder.tv_bar_code_third.setText("下家："+thirdOrder + "");

            return convertView;
        }

        class ViewHolder {
            TextView tv_bar_code;
            TextView tv_bar_code_third;
            ImageView iv_delete;
        }
    }

    private void showDeleteDialog(final int position) {
        String title = getString(R.string.delete_tip);
        String msg = getString(R.string.delete_tip_msg);// "您确认删除这条记录吗？")
        new AlertDialog.Builder(ThridScanActivity.this)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String clientOrder = mOrderListMap.get(position).get(MODEL_CLIENT);
                                String thirdOrder = mOrderListMap.get(position).get(MODEL_THIRD);
                                String operate = String.format(
                                        getString(R.string.delete_add_order_x),
                                        clientOrder + "---" + thirdOrder);
                                saveOperateLog(operate, null);
                                mOrderListMap.remove(position);
                                mListAdapter.notifyDataSetChanged();

                                mTextScanCount.setText(String.format(getString(R.string.barcode_total),
                                        PreferencesUtil.ordtitle, mOrderListMap.size()));
                                if (mOrderListMap.size() == 0) {
                                    mTextOrderNum.setText("");
                                    mTextOrderNum.setBackgroundColor(Color.parseColor("#00000000"));

                                }
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                            }
                        }).show();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mLlListContainer.getVisibility() == View.VISIBLE) {
                hideOrderListPage();
            } else {
                finish();
            }
        }
//        return super.onKeyDown(keyCode, event);
        return false;
    }

}
