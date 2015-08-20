package com.sdbnet.hywy.employee.ui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.album.Bimp;
import com.sdbnet.hywy.employee.album.ImageGridShowAdapter;
import com.sdbnet.hywy.employee.db.db.DBManager;
import com.sdbnet.hywy.employee.db.db.DBCustomValue;
import com.sdbnet.hywy.employee.location.MyLocation;
import com.sdbnet.hywy.employee.model.ExecuteAction;
import com.sdbnet.hywy.employee.net.AsyncHttpService;
import com.sdbnet.hywy.employee.service.TimeUploadService.IReceiveLocationHandler;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;
import com.sdbnet.hywy.employee.ui.widget.CustomGridView;
import com.sdbnet.hywy.employee.ui.widget.DialogPostOrders;
import com.sdbnet.hywy.employee.ui.widget.DialogPostOrders.CallBackInterface;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.ErrLogUtils;
import com.sdbnet.hywy.employee.utils.LogUtil;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.ToastUtil;
import com.sdbnet.hywy.employee.utils.UtilsAndroid;
import com.sdbnet.hywy.employee.utils.UtilsCommon;
import com.sdbnet.hywy.employee.utils.UtilsError;
import com.sdbnet.hywy.employee.utils.UtilsJava;

public class OrderExecuteActivity extends BaseActivity implements
        android.view.View.OnClickListener {

    private static final String TAG = "OrderExecuteActivity";

    private ImageView mImgBack;
    private EditText mEditMilestone;

    // private Dialog mDialog;
    // private ViewPager mPager;
    // private ArrayList<View> listViews;
    private ImageGridShowAdapter mImagGridAdapter;
    // private List<AlbumHelper.ImageItem> mImageItems = new
    // ArrayList<AlbumHelper.ImageItem>();
    private CustomGridView gridView;
    // private boolean isLoading = false;
    private TextView mTextOrderCount;

    private ArrayList<String> scanList = new ArrayList<String>();
    // private List<String> imgList = new ArrayList<String>();
    private boolean postSuccess = false;

    // private ContentValues values;
    public final static String FROM_WHERE = "from_where";
    private String from;
    //    private String fromDB;
    private Button mBtnComplete;
    private Button mBtnLookOrder;

    private LinearLayout mLlRemarkInner;
    private EditText mEditRemarkInner;

    // private LinearLayout mLlRemark;
    // private EditText mEditRemark;

    public static final int REQUEST_CODE_EXECUTE_ORDER = 21;
    public static final int RESULT_CODE_POST_ORDER = 31;
    public static final int RESULT_CODE_CHANGED_ORDER = 32;
    public static final String EXTRA_IS_UPDATE = "extra_is_update";
    public static final String EXTRA_IMG_LIST = "extra_img_list";
    private ExecuteAction mOrderAction;
    private ExecuteAction mNewOrder;
    private int mAppUid;
    private static final int MSG_FLUSH_LOCATION = 100;
    private android.os.Handler mHandler = new android.os.Handler(new android.os.Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FLUSH_LOCATION:
                    requestLocation(getString(R.string.flush));
                    break;
            }
            return false;
        }

    });

    private boolean isAutoCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_execute);
        // ActivityStackManager.getStackManager().pushActivity(this);
        // getPermission();

        initDialog();
        initControls();
        // initViewPager();
        initData();

    }

    private void initData() {
        isAutoCode = isAutoCode();
        mAppUid = UtilsAndroid.Set.getAppUid(this);
        // 获取扫描执行的动作
        mOrderAction = (ExecuteAction) getIntent().getSerializableExtra(
                Constants.Feild.KEY_ACTION);
        // 是否是单条数据 更新
        // isUpdate = getIntent().getExtras().getString(EXTRA_IS_UPDATE);
        // TextUtils.equals(a, b);
//        if (mOrderAction == null) {
//            finish();
//        }
        mNewOrder = (ExecuteAction) UtilsJava.depthClone(mOrderAction);

        scanList = UtilsCommon.strs2List(mNewOrder.getActidx());

        if (mNewOrder != null
                && Constants.Value.YES.equals(mNewOrder.getShowinner())) {
            mLlRemarkInner.setVisibility(View.VISIBLE);
            mEditRemarkInner.setText(mNewOrder.getActmemoinner());
        } else {
            mLlRemarkInner.setVisibility(View.GONE);
        }
        // if (TextUtils.equals(mScanModel, Constants.Value.STATUS_OK)) {
        // mLlRemark.setVisibility(View.VISIBLE);
        // if (mNewOrder.getActmemoinner() != null)
        // mEditRemark.setText(mNewOrder.getActmemoinner());
        // }
        int size = scanList.size();
        if (isAutoCode) {
            mBtnLookOrder.setVisibility(View.INVISIBLE);
            if (size == 0) {
                size = 1;
            }
        }
        mTextOrderCount.setText(String.valueOf(size));
        mEditMilestone.setText(mNewOrder.getActmemo());

        from = getIntent().getStringExtra(FROM_WHERE);
//        Bimp.imgPath = mNewOrder.getImageItems();
//        mImagGridAdapter = new ImageGridShowAdapter(OrderExecuteActivity.this, Bimp.imgPath, true);
//        fromDB = getIntent().getStringExtra("fromDB");
//        if (TextUtils.isEmpty(fromDB)) {
        if (TextUtils.equals(from, DraftBoxActivtiy.class.getSimpleName())) {
            mImagGridAdapter = new ImageGridShowAdapter(
                    OrderExecuteActivity.this, mNewOrder.getImageItems(), true);
        } else {
            Bimp.imgPath = mNewOrder.getImageItems();
            mImagGridAdapter = new ImageGridShowAdapter(
                    OrderExecuteActivity.this, null, true);
        }
        // Bimp.loading();

        mImagGridAdapter.setShowEvery(true);
        gridView.setAdapter(mImagGridAdapter);
        mImagGridAdapter.notifyDataSetChanged();

        Log.i(TAG, "Order>>" + mNewOrder + ";from=" + from);

    }

    private void initControls() {
        //init
        mImgBack = (ImageView) findViewById(R.id.common_view_title_img);

        mTextOrderCount = (TextView) findViewById(R.id.activity_order_execute_tv_order_count);
        mEditMilestone = (EditText) findViewById(R.id.activity_order_execute_edt_remark_outer);

        mLlRemarkInner = (LinearLayout) findViewById(R.id.activity_order_execute_lay_actmemoinner);
        mEditRemarkInner = (EditText) findViewById(R.id.activity_order_execute_edt_actmemoinner);

        // mLlRemark = (LinearLayout)
        // findViewById(R.id.activity_order_exectue_ll_reamrk);
        // mEditRemark = (EditText)
        // findViewById(R.id.activity_order_execute_edt_remark_2);

        mBtnComplete = (Button) findViewById(R.id.activity_order_execute_btn_complete);
        mBtnLookOrder = (Button) findViewById(R.id.activity_order_execute_bt_look_order);


        gridView = (CustomGridView) findViewById(R.id.noScrollgridview);
        //set listener
        mImgBack.setOnClickListener(this);
        mBtnComplete.setOnClickListener(this);
        mBtnLookOrder.setOnClickListener(this);
        //set rules
        mDialogLoading.setCancelable(false);
        //set data
        mBtnLookOrder.setText(String.format(getString(R.string.barcode_record),
                PreferencesUtil.ordtitle));
        ((TextView) findViewById(R.id.common_view_title_text))
                .setText(R.string.order_execution);
    }

    // /**
    // * 初始化ViewPager
    // */
    // private void initViewPager() {
    // // mPager = (ViewPager) findViewById(R.id.activity_order_execute_pager);
    // // listViews = new ArrayList<View>();
    // LayoutInflater mInflater = getLayoutInflater();
    // // listViews.add(mInflater.inflate(R.layout.page_order_execute, null));
    // // mPager.setAdapter(new MyPagerAdapter(listViews));
    // // mPager.setCurrentItem(0);
    // // mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    // }

    private void requestLocation(String msg) {
        saveOperateLog(msg, null);
        if (!isAutoCode && scanList.size() == 0) {
            showShortToast(R.string.no_order_msg);
            return;
        }
        // locationListener();
        if (UtilsAndroid.Set.checkNetState(this)) {

            // 一次定位
            locationListener();
            // 两次定位
            // startDoubleLocation();
        } else {
            showShortToast(R.string.httpError);
        }

    }

    private void executeActionForOrder(View view) {
        saveOperateLog(((Button) view).getText().toString(), null);
        requestLocation(((Button) view).getText().toString());
    }

    private void locationListener() {
        if (mLocateService == null) {
            ToastUtil.show(getApplicationContext(), "bind service failed");
            // bindService();
            return;
        }
        showLoadDialog(R.string.please_later_ellipsis);
        // 获取位置信息
        mLocateService.requestLocation(new IReceiveLocationHandler() {

            @Override
            public void onReceiveHandler(MyLocation location) {
                LogUtil.d("MyLocation " + location + ",");
                if (mLocateService != null) {
                    mLocateService.stopReceiveLocation();
                }
                dismissLoadDialog();

                switch (location.errorCode) {
                    case Constants.Location.ERROR_CODE_OK:
                        showDialogOrders(location);
                        break;
                    case Constants.Location.ERROR_CODE_NO_WORK:
                        showShortToast(R.string.tip_work);
                        break;
                    case Constants.Location.ERROR_CODE_NET_ERROR:
                        showShortToast(R.string.httpError);
                        break;
                    case Constants.Location.ERROR_CODE_BASE_STATION_WIFI:
                        showShortToast(R.string.base_wifi_empty_fail);
                        break;
                    default:
                        showShortToast(R.string.get_location_fail);
                        break;
                }
            }

            @Override
            public void onReceiveFail() {
                dismissLoadDialog();
                if (mLocateService != null) {
                    mLocateService.stopReceiveLocation();
                }
                showShortToast(R.string.get_location_fail);
            }
        });
    }

    /**
     * 添加需要上传的图片 load 定单数据
     */
    private void loadOrderData(boolean isUpload) {

        String imgs = ",";
        LogUtil.d(Bimp.imgPath + ",,," + Bimp.imgPath.size());
        if (isUpload)
            mNewOrder.setImageItems(Bimp.getUploadImg());
        else
            mNewOrder.setImageItems(Bimp.imgPath);

        mNewOrder.setActmemo(mEditMilestone.getText().toString() + "");
        mNewOrder.setActmemoinner(mEditRemarkInner.getText().toString());
        // mNewOrder.setActmemoinner(mEditRemark.getText().toString());
    }

    //    /**
//     * 显示图片
//     *
//     * @param imgs
//     * @param type
//     */
    // private void fillDatas(List<ImageItem> imgs, int type) {
    // // View v = listViews.get(type);
    //
    // // if (isLoading) {
    // // dismissLoadDialog();
    // // } else {
    // // isLoading = true;
    // // }
    // // loading();
    // Bimp.loading();
    // mImagGridAdapter = new ImageGridShowAdapter(OrderExecuteActivity.this,
    // mImageItems, true);
    // mImagGridAdapter.setShowEvery(true);
    // gridView.setAdapter(mImagGridAdapter);
    // mImagGridAdapter.notifyDataSetChanged();
    // }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ScanOrdersActivity.RESULT_CODE_LOOK_ORDER == resultCode) {
            // 从查看订单列表页返回，获取订单列表页返回的订单
            scanList = data
                    .getStringArrayListExtra(ScanOrdersActivity.EXTRA_ORDER_LIST);
            mTextOrderCount.setText(String.valueOf(scanList.size()));

            // 将订单列表集合转换为字符串
            mNewOrder.setActidx(UtilsCommon.list2Strs(scanList));
        } else {
            // loading();
            // Bimp.loading();
            mImagGridAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    ;

    /**
     * 按返回键
     */
    @Override
    public void onBackPressed() {
        if (mDialogLoading.isShowing()) {
            // 取消请求
            mDialogLoading.dismiss();
            AsyncHttpService.cancelRequests(this);
        } else {
            // 退出当前页
            exit();
        }
    }

    /**
     * 退出当前页
     */
    private void exit() {
        loadOrderData(false);
        if (isAutoCode) {
            destroy();
        } else if (postSuccess) {
            destroy();
        } else if (scanList.size() <= 0) {
            showDeleteDialog();
        } else if (TextUtils.equals(from, CaptureActivity.class.getSimpleName()) || !mNewOrder.equals(mOrderAction)) {
//            showSaveDialog();
//        } else if (!mNewOrder.equals(mOrderAction)) {

            LogUtil.d(from + "," + mNewOrder + ">>>" + mOrderAction);
            showSaveDialog();
        } else {
            destroy();
        }
    }

    ;

    private void showSaveDialog() {
        // 如果未提交成功 且不是来自草稿箱中的记录 且订单不为空，提示用户是否保存到草稿箱
        String title = getString(R.string.confirm_tip);
        String msg = getString(R.string.save_failed_orders_tip_msg);
        showAlertDialog(title, msg, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteOrder();
                saveOrder();
                destroy();
            }
        }, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                destroy();
            }
        }, null);
    }

    private void showDeleteDialog() {
        // 如果定单数为0 ,删除已添加的定单
        String title = getString(R.string.confirm_tip);
        String msg = getString(R.string.delete_order_tip_msg);
        showAlertDialog(title, msg, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteOrder();
                destroy();
            }
        }, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                destroy();
            }
        }, null);
    }

    /**
     * 清除缓存
     */
    private void destroy() {
        setResult(REQUEST_CODE_EXECUTE_ORDER);
        Bimp.clearCache();
        finish();
    }

    private void showDialogOrders(final MyLocation location) {
        String title = location.address
                + "\n"
                + UtilsJava.translate2SessionMessageData(System
                .currentTimeMillis());

        final DialogPostOrders dialog = new DialogPostOrders(this);
        dialog.setTitle(title);
        dialog.setCallBackInterface(new CallBackInterface() {

            @Override
            public void onSubmit() {

                postScanOrders(location);
                // postScanOrders(location.address, location.longitude,
                // location.latitude);
            }

            @Override
            public void onFlush() {
                showLoadDialog();
                mHandler.sendEmptyMessageDelayed(MSG_FLUSH_LOCATION, Constants.Value.DELAY_TIME_LOW);
//                requestLocation(getString(R.string.flush));
            }
        });
        dialog.show();
    }

    /**
     * 执行订单
     * <p>
     * //     * @param address
     * //     * @param longitude
     * //     * @param latitude
     *
     * @param location
     */
    // private void postScanOrders(String address, Double longitude,
    // Double latitude) {
    private void postScanOrders(final MyLocation location) {
        UtilsAndroid.Common.initTrafficStats(mAppUid);

        showLoadDialog(R.string.is_submitted_ellipsis);
        loadOrderData(true);
        PreferencesUtil.setSteps(Constants.Step.POST);
        // PreferencesUtil.initStoreData();
        // 提交扫描订单的信息
        AsyncHttpService.postScanOrderInfo(mNewOrder, location,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onProgress(int bytesWritten, int totalSize) {

                        // 显示上传进度
                        float num = (float) bytesWritten / totalSize;
                        BigDecimal b = new BigDecimal(num);
                        num = b.setScale(2, BigDecimal.ROUND_HALF_UP)
                                .floatValue();
                        final int pro = (int) (num * 100);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mDialogLoading != null) {
                                    String msg = getString(R.string.submission_schedule_x) + pro + "%";
                                    mDialogLoading.setMsg(msg);
                                }
                            }
                        });
                        super.onProgress(bytesWritten, totalSize);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        // mLocateService.setReceLocationHandler(null); //
                        // 取消定位监听
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                        String msg = getString(R.string.httpisNull);
                        dismissLoadDialog();
                        showShortToast(msg);

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.i(TAG, "post: " + response.toString());
                        // mLocateService.setReceLocationHandler(null);
                        // 取消定位监听

                        try {

                            if (UtilsError.isErrorCode(
                                    OrderExecuteActivity.this, response)) {
                                return;
                            }
                            Log.e(TAG, isAutoCode + "," + response.getString("ordno"));
                            if (!response.isNull("ordno") && isAutoCode) {
                                String ordno = response.getString("ordno");
                                PreferencesUtil.putValue(PreferencesUtil.KEY_AUTO_CODE, ordno);
                            }
//                            ordno		1		字符串		操作订单号(返回当前操作的订单号,仅限销售账户)

                            postSuccess = true;
                            // 提交成功后，删除草稿箱中的记录
                            deleteOrder();
                            // upLocationData(location);
                            String msg = getString(R.string.submitted_successfully);
                            showLongToast(msg);// "提交成功",
                            destroy();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            dismissLoadDialog();
                        }

                    }

                }, OrderExecuteActivity.this);
    }

    /**
     * 查看添加订单列表
     *
     * @param view
     */
    public void lookForScanOrders(View view) {
        saveOperateLog(((Button) view).getText().toString());
        if (scanList.size() == 0) {
            showLongToast(PreferencesUtil.ordtitle
                    + getString(R.string.list_empty));
            return;
        }
        Intent data = new Intent(this, ScanOrdersActivity.class);
        data.putStringArrayListExtra(ScanOrdersActivity.EXTRA_ORDER_LIST,
                scanList);
        startActivityForResult(data, ScanOrdersActivity.REQUEST_CODE_LOOK_ORDER);
    }

    // // 双定位请求
    // private boolean isGetBDLocation;
    // private boolean isGetGpsLocation;
    // private String bdAddress;
    // private String gpsAddress;
    // private LatLng bdLatlng;
    // private LatLng gpsLatlng;
    // private final int MAX_DOUBLE_DISTANCE = 10 * 1000;// 十公里；
    // private static final int BD_DISTANCE_ERROR = -1;// 百度计算错误 ;
    // private static final int BD_DISTANCE_NO = -2;// 无法计算
    //
    // private void startDoubleLocation() {
    // initDoubleData();
    // LocationServiceUtils
    // .setReceLocationHandler(new IReceiveLocationHandler() {
    // @Override
    // public void onReceiveHandler(double longitude,
    // double latitude, String address, String locationType) {
    // LogUtil.d("onReceiveHandler " + longitude + ","
    // + latitude + "," + address + "," + locationType);
    //
    // // postScanOrders(address, longitude, latitude);
    // if (locationType
    // .equals(LocationServiceUtils.LOCATION_TYPE_GPS_REVERSE_GEO_CODE)) {
    // isGetGpsLocation = true;
    // gpsAddress = address;
    // gpsLatlng = new LatLng(longitude, latitude);
    // int bdResult = LocationServiceUtils
    // .requestBDLocation();
    // } else {
    // bdAddress = address;
    // bdLatlng = new LatLng(longitude, latitude);
    // isGetBDLocation = true;
    // }
    // if (isGetBDLocation && isGetGpsLocation) {
    // Log.d(TAG, getDoubleDistance() + "");
    //
    // mDialog.dismiss();
    // if (isRightDistance()) {
    // String title = "\n"
    // + UtilsJava
    // .translate2SessionMessageData(System
    // .currentTimeMillis());
    // if (gpsLatlng != null) {
    // showDialogOrders(gpsAddress + title,
    // gpsLatlng.longitude,
    // gpsLatlng.latitude);
    // } else if (bdLatlng != null) {
    // showDialogOrders(bdAddress + title,
    // gpsLatlng.longitude,
    // gpsLatlng.latitude);
    // } else {
    //
    // }
    // }
    // }
    // }
    // });
    // boolean gpsResult = LocationServiceUtils.requestGpsLocation();
    // if (gpsResult == false) {
    // isGetGpsLocation = true;
    // gpsLatlng = null;
    // int bdResutl = LocationServiceUtils.requestBDLocation();
    // if (bdResutl == 6) {
    //
    // }
    // }
    //
    // }
    //
    // private void initDoubleData() {
    // isGetBDLocation = false;
    // isGetGpsLocation = false;
    // bdLatlng = null;
    // gpsLatlng = null;
    // }
    //
    // private boolean isRightDistance() {
    // int distance = (int) getDoubleDistance();
    // appendDistace();
    // if (distance == BD_DISTANCE_ERROR) {
    // Toast.makeText(this,
    // getString(R.string.get_location_file_plese_again),
    // Toast.LENGTH_SHORT).show();
    // return false;
    // } else if (distance == BD_DISTANCE_NO) {
    // Toast.makeText(this,
    // getString(R.string.get_location_file_plese_again),
    // Toast.LENGTH_SHORT).show();
    // return false;
    // } else if (distance > MAX_DOUBLE_DISTANCE) {
    // Toast.makeText(this,
    // getString(R.string.get_location_file_plese_again),
    // Toast.LENGTH_SHORT).show();
    // return false;
    // } else {
    //
    // return true;
    // }
    // }
    //
    // private double getDoubleDistance() {
    // if (bdLatlng != null && gpsLatlng != null) {
    // double distance = DistanceUtil.getDistance(bdLatlng, gpsLatlng);
    // if (distance == BD_DISTANCE_ERROR) {
    // return distance;
    // } else {
    // return Math.abs(distance);
    // }
    // } else {
    // return BD_DISTANCE_NO;
    // }
    // }

//    /**
//     * ViewPager适配器
//     */
//    public class MyPagerAdapter extends PagerAdapter {
//        public List<View> mListViews;
//
//        public MyPagerAdapter(List<View> mListViews) {
//            this.mListViews = mListViews;
//        }
//
//        @Override
//        public void destroyItem(View arg0, int arg1, Object arg2) {
//            ((ViewPager) arg0).removeView(mListViews.get(arg1));
//        }
//
//        @Override
//        public void finishUpdate(View arg0) {
//        }
//
//        @Override
//        public int getCount() {
//            return mListViews.size();
//        }
//
//        @Override
//        public Object instantiateItem(View arg0, int arg1) {
//            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
//            return mListViews.get(arg1);
//        }
//
//        @Override
//        public boolean isViewFromObject(View arg0, Object arg1) {
//            return arg0 == (arg1);
//        }
//
//        @Override
//        public void restoreState(Parcelable arg0, ClassLoader arg1) {
//        }
//
//        @Override
//        public Parcelable saveState() {
//            return null;
//        }
//
//        @Override
//        public void startUpdate(View arg0) {
//        }
//    }

    @Override
    public void onDestroy() {
        if (mLocateService != null) {
            mLocateService.stopReceiveLocation();
        }
        super.onDestroy();
        // unBindService();
        // ActivityStackManager.getStackManager().popActivity(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_view_title_img:
                exit();
                break;
            case R.id.activity_order_execute_btn_complete:
                executeActionForOrder(v);
                break;
            case R.id.activity_order_execute_bt_look_order:
                lookForScanOrders(v);
                break;

            default:
                break;
        }

    }

    private void saveOrder() {
        DBManager manager = new DBManager(OrderExecuteActivity.this);
        manager.insertOrder(mNewOrder);
        int count = (int) manager.getOrderCount();
        manager.closeDatabase();

        // 发送广播，以便草稿箱界面刷新
        Intent intent = new Intent();
        intent.setAction(DBCustomValue.ACTION_COUNT_CHANGED);
        intent.putExtra(DBCustomValue.COUNT, count);
        OrderExecuteActivity.this.sendBroadcast(intent);
    }

    private void deleteOrder() {
        DBManager manager = new DBManager(OrderExecuteActivity.this);
        manager.deleteOrder(mOrderAction.getActidx(), mOrderAction.getAction());
        // manager.deleteOrder(mNewOrder.getActidx(), mNewOrder.getAction());
        int count = (int) manager.getOrderCount();
        manager.closeDatabase();
        // 发送广播，以便草稿箱界面刷新
        Intent intent = new Intent();
        intent.setAction(DBCustomValue.ACTION_COUNT_CHANGED);
        intent.putExtra(DBCustomValue.COUNT, count);
        OrderExecuteActivity.this.sendBroadcast(intent);

    }

    // // Test
    // private void appendDistace() {
    // double distance = getDoubleDistance();
    // UtilsAndroid.Sdcard.appendFiledata("BD_Latlng=" + bdLatlng
    // + ",GPS_Latlng=" + gpsLatlng + ",Distance=" + distance
    // + "\nBD_location=" + bdAddress + ",gps_location=" + gpsAddress);
    // }

    private void upLocationData(final MyLocation location) {
        LogUtil.d(TAG, "MyLocation=" + location.toString());

        if (!UtilsCommon.checkAccount()
                || !UtilsAndroid.Set.checkNetState(this)) {
            saveLocationLog(location);
            return;
        }

        AsyncHttpService.uploadLocation(location,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable,
                                errorResponse);
                        ToastUtil.show(getApplicationContext(),
                                R.string.network_connect_timeout + ",");
                        saveLocationLog(location);

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        LogUtil.d(response.toString());
                        try {
                            if (UtilsError.isErrorCode(
                                    OrderExecuteActivity.this, response)) {
                            } else {
                                saveLocationLog(location);
                                ErrLogUtils.uploadErrorCodeMsg(
                                        OrderExecuteActivity.this, location);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            saveLocationLog(location);
                            ErrLogUtils.uploadErrLog(OrderExecuteActivity.this,
                                    ErrLogUtils.toString(e));
                        }
                    }
                }, this);
    }

    private void saveLocationLog(MyLocation location) {
        DBManager manager = new DBManager(this);
        manager.saveLocateLog(location);
        manager.closeDatabase();
    }

    private boolean isAutoCode() {
//		isAutoCode
        String isAutoCode = "";
        String options = PreferencesUtil.getValue(Constants.Feild.KEY_OPTIONS);
        if (!TextUtils.isEmpty(options)) {
            try {
                JSONObject jsonObject = new JSONObject(options);
                if (!jsonObject.isNull("isAutoCode"))
                    isAutoCode = jsonObject.getString("isAutoCode");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (TextUtils.equals(isAutoCode, Constants.Value.STATUS_OK)) {
            return true;
        } else {
            return false;
        }
    }
    // private String mScanModel;
    //
    // private void getPermission() {
    // String options = PreferencesUtil.getValue(Constants.Feild.KEY_OPTIONS);
    // Log.e(TAG, "options=" + options);
    // if (TextUtils.isEmpty(options)) {
    // return;
    // }
    // try {
    // JSONObject jsonObject = new JSONObject(options);
    // if (!jsonObject.isNull("scanModel"))
    // mScanModel = jsonObject.getString("scanModel");
    // } catch (JSONException e) {
    // e.printStackTrace();
    // }
    //
    // }
    //
    // private void requestIsUpdate() {
    //
    //
    // // TEST
    // // mOrderModel.setActidx(UtilsCommon.list2Strs(scanList));
    // // Intent data = new Intent(this, OrderExecuteActivity.class);
    // // data.putExtra(Constants.Feild.KEY_ACTION, mOrderModel);
    // // data.putExtra(OrderExecuteActivity.EXTRA_IS_UPDATE,
    // // Constants.Value.STATUS_OK);
    // // startActivityForResult(data,
    // // OrderExecuteActivity.REQUEST_CODE_EXECUTE_ORDER);
    // Log.i(TAG, "requestIsUpdate="+scanList.toString());
    // if (scanList.size() != 1) {
    // return;
    // }
    //
    // AsyncHttpService.requestIsUpdate(scanList.get(0),
    // mOrderAction.getAction(), new JsonHttpResponseHandler() {
    // @Override
    // public void onStart() {
    // super.onStart();
    // showLoadDialog();
    // }
    //
    // @Override
    // public void onSuccess(int statusCode, Header[] headers,
    // JSONObject response) {
    // super.onSuccess(statusCode, headers, response);
    // Log.e(TAG, response.toString());
    //
    // try {
    // if (!UtilsError.isErrorCode(
    // OrderExecuteActivity.this, response)) {
    // }
    //
    // getOrderData(response);
    // } catch (JSONException e) {
    // e.printStackTrace();
    // } finally {
    // dismissLoadDialog();
    // }
    //
    // }
    //
    // @Override
    // public void onFailure(int statusCode, Header[] headers,
    // Throwable throwable, JSONObject errorResponse) {
    // Log.e(TAG, errorResponse+">>>"+throwable.getMessage());
    // super.onFailure(statusCode, headers, throwable,
    // errorResponse);
    // dismissLoadDialog();
    // }
    //
    // @Override
    // public void onCancel() {
    // super.onCancel();
    // dismissLoadDialog();
    // }
    // }, this);
    // }
    //
    // private void getOrderData(JSONObject response) throws JSONException {
    // // traceInfo 1 对象 订单信息
    // // --traceid 1 字符串 追溯流水号
    // // --actmemo 1 字符串 动作说明
    // // --actmemoinner 1 字符串 内部备注
    // // --pics 1 数组 动作节点数组
    // // - ----picid 1 字符串 动作码
    // // -----smallimg 1 字符串 动作名称
    // // ----bigimg 1 字符串 动作名称
    // if (response.isNull("traceInfo")) {
    // return;
    // }
    // String traceid = null, actmemo = null, actmemoinner = null;
    //
    // JSONObject jsonObject = response.getJSONObject("traceInfo");
    // if (!jsonObject.isNull("traceid"))
    // traceid = jsonObject.getString("traceid");
    // mOrderAction.setActidx(traceid);
    // if (!jsonObject.isNull("actmemo"))
    // actmemo = jsonObject.getString("actmemo");
    // // mOrderAction.setActidx(traceid);
    // if (!jsonObject.isNull("actmemoinner"))
    // actmemoinner = jsonObject.getString("actmemoinner");
    // // mOrderAction.setActidx(traceid);
    // Log.e(TAG, "" + traceid + "<" + actmemo + "<" + actmemoinner);
    // if (jsonObject.isNull("pics"))
    // return;
    // JSONArray jsonArray = jsonObject.getJSONArray("pics");
    // for (int i = 0; i < jsonArray.length(); i++) {
    // JSONObject object = jsonArray.getJSONObject(i);
    // ImageItem imageItem = new ImageItem();
    // String picid = object.getString("picid");
    // String smallimg = object.getString("smallimg");
    // String bigimg = object.getString("bigimg");
    // imageItem.thumbnailPath = smallimg;
    // imageItem.imagePath = bigimg;
    // mImageItems.add(imageItem);
    // }
    //
    // Log.e(TAG, mImageItems.toString());
    // mImagGridAdapter.notifyDataSetChanged();
    // }
}
