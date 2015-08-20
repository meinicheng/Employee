package com.sdbnet.hywy.employee.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlarmManager;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sdbnet.hywy.employee.ActivityStackManager;
import com.sdbnet.hywy.employee.MainApplication;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.db.db.DBManager;
import com.sdbnet.hywy.employee.db.db.DBCustomValue;
import com.sdbnet.hywy.employee.listener.IBtnCallListener;
import com.sdbnet.hywy.employee.receiver.MyReceiver;
import com.sdbnet.hywy.employee.service.GuardianService;
import com.sdbnet.hywy.employee.service.TimeUploadService;
import com.sdbnet.hywy.employee.service.TimeUploadService.LocalBinder;
import com.sdbnet.hywy.employee.slidingmenu.SlidingMenu;
import com.sdbnet.hywy.employee.ui.base.BaseSlidingFragmentActivity;
import com.sdbnet.hywy.employee.ui.view.HomePageFragment;
import com.sdbnet.hywy.employee.ui.widget.CustomButton;
import com.sdbnet.hywy.employee.ui.widget.DialogLoading;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.ErrLogUtils;
import com.sdbnet.hywy.employee.utils.LogUtil;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.ToastUtil;
import com.sdbnet.hywy.employee.utils.UtilsAndroid;
import com.sdbnet.hywy.employee.utils.UtilsCommon;
import com.sdbnet.hywy.employee.utils.UtilsJava;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengDownloadListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class MainActivity extends BaseSlidingFragmentActivity implements
		OnClickListener, AnimationListener, OnDateSetListener {
	private static final String TAG = "MainActivity";
	private static final String LIST_TEXT = "text";
	private static final String LIST_IMAGEVIEW = "img";

	/**
	 * 数字代表列表顺序
	 */
	private int fragmentCurrentIndex = 0;
	// private static final int FRAGMENT_HOME_PAGE = 0;
	// private static final int FRAGMENT_INFO = 1;
	// private static final int FRAGMENT_ORDER_ACTION = 2;
	// private static final int FRAGMENT_DRAFT_BOX = 3;
	// private static final int FRAGMENT_ORDER_QUERY = 4;
	private CustomButton cbSet;
	private CustomButton cbAbove;
	private View title;

	private LinearLayout ll_main_container;
	private List<String> navs;

	private LinearLayout loadLayout;
	private FragmentManager mFragMgr;

	private ListView lvTitle;
	private SimpleAdapter lvAdapter;
	private LinearLayout llGoHome;
	private ImageView iv_login;

	private TextView mAboveTitle;
	private SlidingMenu mSlidingMenu;
	private boolean mIsTitleHide = false;
	private boolean mIsAnim = false;
	private TextView totalName;
	private Map<String, Object> map;
	private List<Map<String, Object>> data;

	private HomePageFragment mHomePageFragment;

	// private Fragment currentFragment;

	private DialogLoading mDialogLoading;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slidingmenu);
		startTimingLocationService();
		startGuardianService();

		bindService();

		mDialogLoading = new DialogLoading(this);
		LogUtil.openLog();
		ToastUtil.openShow();
		PreferencesUtil.initStoreData();

		initSlidingMenu();
		initMenuNav();
		initMenuData();

		initControls();

		initFramentPage();

		initListView();
		// 注册草稿箱数据变化监听
		registeDraftBoxReceiver();
		// 注册
		registeMyReceiver();
		// 注册电量变化监听
		// registeBatteryReceiver();
		// 启动更新线程
		startUpdateThread();
		// 设置闹钟
		setAlarmTime();
		// 上传错误日志；
		uploadCarshLogFile();
		setJpushAliasAndTags();
		// installShortcut();
		unInstallApp();
	}

	/**
	 * 注册广播，监听草稿箱的变化
	 */
	private MyDraftBoxReceiver mDraftBoxReceiver;

	private void registeDraftBoxReceiver() {
		mDraftBoxReceiver = new MyDraftBoxReceiver();
		registerReceiver(mDraftBoxReceiver, new IntentFilter(
				DBCustomValue.ACTION_COUNT_CHANGED));
	}

	class MyDraftBoxReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			long count = intent.getIntExtra(DBCustomValue.COUNT, 0);
			for (Map<String, Object> map : data) {
				if (map.get(LIST_TEXT).toString()
						.contains(getString(R.string.menuDraftBox))) {
					map.put(LIST_TEXT,
							String.format(getString(R.string.menuDraftBox)
									+ "(%d)", (int) count));
				}
			}
			lvAdapter.notifyDataSetInvalidated();
		}

	};

	// private void registeBatteryReceiver() {
	// // 注册广播接受者java代码
	// IntentFilter intentFilter = new IntentFilter();
	// intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
	// intentFilter.addAction("android.alarm.action.OperateLog");
	//
	// batteryReceiver = new BatteryReceiver();
	// // 注册receiver
	// registerReceiver(batteryReceiver, intentFilter);
	// }

	private MyReceiver mySdbetReceiver;

	private void registeMyReceiver() {
		mySdbetReceiver = new MyReceiver();

		// 注册广播接受者java代码
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		intentFilter.addAction("android.alarm.action.OperateLog");

		// 注册receiver
		registerReceiver(mySdbetReceiver, intentFilter);
	}

	/**
	 * 启动定位服务
	 */
	private void startTimingLocationService() {
		// Log.e(TAG, "startTimingLocationService");
		Intent service = new Intent(this, TimeUploadService.class);
		startService(service);
	}

	/**
	 * 启动守护服务
	 */
	private void startGuardianService() {
		Intent service = new Intent(this, GuardianService.class);
		service.setAction(GuardianService.ACTION_GUARDIAN);
		startService(service);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver();
		unBindService();
		// unRegistBroadCast();
		super.onDestroy();

	}

	private void unregisterReceiver() {
		unregisterReceiver(mDraftBoxReceiver);
		mDraftBoxReceiver = null;
		// unregisterReceiver(batteryReceiver);
		// batteryReceiver = null;

		unregisterReceiver(mySdbetReceiver);
		mySdbetReceiver = null;

	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		unRegistBroadCast();
	}

	/**
	 * 初始化SlidingMenu
	 */
	private void initSlidingMenu() {
		setBehindContentView(R.layout.view_slidingmenu);
		// customize the SlidingMenu
		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		mSlidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
		// mSlidingMenu.setShadowWidth(20);
		mSlidingMenu.setBehindScrollScale(0);
	}

	/**
	 * 初始化控件
	 */
	private void initControls() {
		// 进度条(loading图标layout)
		loadLayout = (LinearLayout) findViewById(R.id.view_loading);
		// 顶部导航栏标题文本
		mAboveTitle = (TextView) findViewById(R.id.sliding_menu_tv_above_title);
		// 标题初始化
		mAboveTitle.setText(navs.get(fragmentCurrentIndex));

		totalName = (TextView) findViewById(R.id.totalName);
		// // 顶部导航栏查询按钮
		// lay_above_image = (LinearLayout) findViewById(R.id.lay_above_image);
		// lay_above_image.setOnClickListener(this);

		lvTitle = (ListView) findViewById(R.id.behind_list_show);
		llGoHome = (LinearLayout) findViewById(R.id.sliding_menu_ll_above_toHome);

		iv_login = (ImageView) findViewById(R.id.iv_login);
		iv_login.setOnClickListener(this);

		// 获取公司logo
		if (UtilsAndroid.Sdcard.hasSDCard()) {
			LogUtil.d(TAG, "logo: " + PreferencesUtil.company_logo);
			// try {
			// ImageLoader.getInstance().asynLoadImage(iv_login,
			// PreferencesUtil.company_logo);
			// } catch (Throwable e) {
			// ErrLogUtils.uploadErrLog(MainActivity.this,
			// ErrLogUtils.toString(e));
			// e.printStackTrace();
			// }
			ImageLoader.getInstance().displayImage(
					PreferencesUtil.company_logo, iv_login,
					((MainApplication) getApplication()).options);
		}

		cbSet = (CustomButton) findViewById(R.id.sliding_menu_set_cb);
		cbSet.setOnClickListener(this);

		cbAbove = (CustomButton) findViewById(R.id.sliding_menu_cbAbove);
		cbAbove.setOnClickListener(this);

		title = findViewById(R.id.main_title);
		ll_main_container = (LinearLayout) findViewById(R.id.ll_main_container);
		llGoHome.setOnClickListener(this);

	}

	/**
	 * 获取菜单列表
	 * 
	 * @return
	 */
	private void initMenuData() {
		data = new ArrayList<Map<String, Object>>();

		for (String menu : navs) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(LIST_TEXT, menu);
			if (menu.equals(getString(R.string.menuHomepage))) {
				map.put(LIST_IMAGEVIEW, R.drawable.homepage_menu_selector);
			} else if (menu.equals(getString(R.string.menuInfo))) {
				map.put(LIST_IMAGEVIEW, R.drawable.info_menu_selector);
			} else if (menu.equals(getString(R.string.menuScan))) {
				map.put(LIST_IMAGEVIEW, R.drawable.status_menu_selector);
			} else if (menu.contains(getString(R.string.menuDraftBox))) {
				map.put(LIST_IMAGEVIEW, R.drawable.order_menu_selector);
			}
			data.add(map);
		}
	}

	/**
	 * 初始化菜单
	 */
	private void initListView() {

		lvAdapter = new SimpleAdapter(this, data, R.layout.item_slidingmenu,
				new String[] { LIST_TEXT, LIST_IMAGEVIEW }, new int[] {
						R.id.tv_menu_title, R.id.iv_menu_icon }) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				if (position == fragmentCurrentIndex) {
					lvTitle.setTag(view);
				}
				return view;
			}
		};
		lvTitle.setAdapter(lvAdapter);

		// 给菜单添加单击事件
		lvTitle.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				saveOperateLog(
						((TextView) view.findViewById(R.id.tv_menu_title))
								.getText().toString(), null);
				String navTtile = navs.get(position);
				final boolean isWorking = PreferencesUtil
						.getValue(PreferencesUtil.KEY_WORK_STATS,
								Constants.Value.WORKING);
				if (lvTitle.getTag() != null) {
					if (lvTitle.getTag() == view) {
						MainActivity.this.showContent();
						return;
					}
				}
				Log.e(TAG, "navTtile=" + navTtile);
				if (navTtile.equals(getString(R.string.menuScan))) {
					if (isWorking) {
						UtilsCommon.start_activity(MainActivity.this,
								OrderActionActivity.class);
					} else {
						showMsg(R.string.tip_work);
					}
					return;
				} else if (navTtile.contains(getString(R.string.menuDraftBox))) {

					if (isWorking) {
						// lvTitle.setTag(view);
						// mAboveTitle.setText(getString(R.string.menuDraftBox));
						UtilsCommon.start_activity(MainActivity.this,
								DraftBoxActivtiy.class);
					} else {
						showMsg(R.string.tip_work);

					}
					return;
				} else if (navTtile.contains(getString(R.string.menuInfo))) {
					UtilsCommon.start_activity(MainActivity.this,
							InfoActivity.class);
					return;
				} else {
					lvTitle.setTag(view);
					mAboveTitle.setText(navTtile);
				}
				Log.e(TAG, "position=" + position);
				// new MyTask().execute(position);
				showFragments(mHomePageFragment);
			}
		});

	}

	/**
	 * 根据菜单权限初始化导航按钮
	 */
	private void initMenuNav() {
		// 获取菜单权限
		String execMenus = PreferencesUtil.getValue(
				PreferencesUtil.KEY_EXECUTE_MENU, null);
		LogUtil.d(execMenus);
		if (TextUtils.isEmpty(execMenus)) {
			return;
		}
		navs = new ArrayList<String>();
		String[] menus = execMenus.split(",");
		for (String menu : menus) {
			if (Constants.MENU_MAP.get(menu) == null) {
				continue;
			}
			DBManager manager = new DBManager(this);
			int count = (int) manager.getOrderCount();
			manager.closeDatabase();
			if ("HYWY004".equals(menu)) {
				navs.add(String.format(getString(Constants.MENU_MAP.get(menu)),
						count));
			} else {
				navs.add(getString(Constants.MENU_MAP.get(menu)));
			}
		}

	}

	private void initFramentPage() {
		mFragMgr = getSupportFragmentManager();
		mHomePageFragment = new HomePageFragment();
		// new MyTask().execute(fragmentCurrentIndex);
		showFragments(mHomePageFragment);
	}

	@Override
	protected void onStart() {
		super.onStart();
		PreferencesUtil.initStoreData();
		totalName.setText(PreferencesUtil.user_name);
	}

	// public class MyTask extends AsyncTask<Integer, String, Map<String,
	// Object>> {
	//
	// @Override
	// protected void onPreExecute() {
	// loadLayout.setVisibility(View.VISIBLE);
	// MainActivity.this.showContent();
	// super.onPreExecute();
	// }
	//
	// @Override
	// protected Map<String, Object> doInBackground(Integer... params) {
	// fragmentCurrentIndex = params[0];
	// if (null == map) {
	// map = new HashMap<String, Object>();
	// map.put(FRAGMENT_HOME_PAGE + "", new HomePageFragment());
	// map.put(FRAGMENT_INFO + "", new InfoFragment());
	// map.put(FRAGMENT_ORDER_ACTION + "", new OrderActionFragment());
	// map.put(FRAGMENT_DRAFT_BOX + "", new DraftBoxFragment());
	// // map.put(FRAGMENT_ORDER_QUERY + "", new
	// // ReportQueryFragment());
	// }
	// return map;
	// }
	//
	// @Override
	// protected void onPostExecute(Map<String, Object> result) {
	// super.onPostExecute(result);
	// loadLayout.setVisibility(View.GONE);
	// if (result != null && !result.isEmpty()) {
	// Fragment fragment = (Fragment) result.get(String
	// .valueOf(fragmentCurrentIndex));
	// if (fragment != null) {
	// showFragments(fragment);
	// }
	// }
	// }
	// }

	/**
	 * 切换fragment
	 * 
	 * @param fragment
	 */
	public void showFragments(Fragment fragment) {
		loadLayout.setVisibility(View.GONE);
		// initUpdate();
		FragmentTransaction trans = mFragMgr.beginTransaction();
		// if (fragment instanceof OrderActionFragment) {
		// UtilsCommon.start_activity(MainActivity.this,
		// OrderActionActivity.class);
		// return;
		// }
		// if (fragment instanceof ReportQueryFragment) {
		// UtilsCommon.start_activity(MainActivity.this,
		// ReportQueryActivity.class);
		// return;
		// }
		// currentFragment = fragment;
		ll_main_container.setGravity(Gravity.TOP);
		trans.replace(R.id.ll_main_container, fragment);
		trans.commit();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.sliding_menu_ll_above_toHome:
			showMenu();
			break;
		case R.id.sliding_menu_set_cb: // 点击设置
			saveOperateLog(getString(R.string.set));// 点击设置
			UtilsCommon.start_activity(this, SystemSettingActivity.class);
			break;
		case R.id.sliding_menu_cbAbove: // 点击关于
			saveOperateLog(getString(R.string.about));// 点击关于
			UtilsCommon.start_activity(this, AboutActivity.class);
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(MainActivity.this);
		registBroadCast();
		if (null != totalName
				& !totalName.getText().toString()
						.equals(PreferencesUtil.user_name)) {
			totalName.setText(PreferencesUtil.user_name);
		}
		// UmengUpdateAgent.update(MainActivity.this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mSlidingMenu.isMenuShowing()) {

			} else if (UtilsAndroid.Common.isFastDoubleClick()) {
				// 返回桌面
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
			} else {
				// 连续按两次返回键就退出
				if (2 == fragmentCurrentIndex && btnCallListener != null) {
					btnCallListener.onBackPressedHandler();
				}
				showMsg(R.string.press_again_exit);
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (mSlidingMenu.isMenuShowing()) {
				toggle();
			} else {
				showMenu();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		super.dispatchTouchEvent(event);
		if (mIsAnim) {
			return false;
		}
		final int action = event.getAction();

		float x = event.getX();
		float y = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			lastY = y;
			lastX = x;
			return false;
		case MotionEvent.ACTION_MOVE:
			float dY = Math.abs(y - lastY);
			float dX = Math.abs(x - lastX);
			boolean down = y > lastY ? true : false;
			lastY = y;
			lastX = x;
			if (dX < 8 && dY > 8 && !mIsTitleHide && !down) {
				Animation anim = AnimationUtils.loadAnimation(
						MainActivity.this, R.anim.push_top_in);
				anim.setAnimationListener(MainActivity.this);
			} else if (dX < 8 && dY > 8 && mIsTitleHide && down) {
				Animation anim = AnimationUtils.loadAnimation(
						MainActivity.this, R.anim.push_top_out);
				anim.setAnimationListener(MainActivity.this);
			} else {
				return false;
			}
			mIsTitleHide = !mIsTitleHide;
			mIsAnim = true;
			break;
		default:
			return false;
		}
		return false;
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (mIsTitleHide) {
			title.setVisibility(View.GONE);
		}
		mIsAnim = false;
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	@Override
	public void onAnimationStart(Animation animation) {
		title.setVisibility(View.VISIBLE);
		if (mIsTitleHide) {
			FrameLayout.LayoutParams lp = (LayoutParams) ll_main_container
					.getLayoutParams();
			lp.setMargins(0, 0, 0, 0);
			ll_main_container.setLayoutParams(lp);
		} else {
			FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) title
					.getLayoutParams();
			lp.setMargins(0, 0, 0, 0);
			title.setLayoutParams(lp);
			FrameLayout.LayoutParams lp1 = (LayoutParams) ll_main_container
					.getLayoutParams();
			lp1.setMargins(0,
					getResources().getDimensionPixelSize(R.dimen.title_height),
					0, 0);
			ll_main_container.setLayoutParams(lp1);
		}
	}

	private float lastX = 0;
	private float lastY = 0;
	private IBtnCallListener btnCallListener;

	// private BatteryReceiver batteryReceiver;
	private boolean forceUpdate;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (60 == requestCode) {
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
	}

	public void executeAction(View view) {
		Intent intent = new Intent(this, CaptureActivity.class);
		startActivity(intent);
	}

	public void toSubAction(View view) {
		if (btnCallListener != null) {
			btnCallListener.onClickHandler();
		}
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		if (fragment instanceof IBtnCallListener) {
			btnCallListener = (IBtnCallListener) fragment;
		}
		super.onAttachFragment(fragment);
	}

	private void startUpdateThread() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				initUpdate();
			}
		});
		thread.start();
	}

	/**
	 * 自动更新
	 */
	private void initUpdate() {
		mDialogLoading
				.setMsg(R.string.downloading_update_please_later_ellipsis);
		forceUpdate = false;
		MobclickAgent.updateOnlineConfig(this);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		// 从友盟读取在线参数配置信息
		String update_mode = MobclickAgent
				.getConfigParams(this, "upgrade_mode");

		// System.out.println("upgrade_mode:" + update_mode);
		LogUtil.d(TAG, "upgrade_mode:" + update_mode);

		if (!TextUtils.isEmpty(update_mode)) {
			String[] params = update_mode.split(",");
			if (params.length == 2 && "F".equalsIgnoreCase(params[1])) {
				// F 表示强制更新
				forceUpdate = true;
			}
		}

		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case UpdateStatus.Yes: // has update
					if ("v1.0.1".equals(updateInfo.version)) {

						mDialogLoading.show();
						UmengUpdateAgent.startDownload(MainActivity.this,
								updateInfo);
					} else {
						UmengUpdateAgent.showUpdateDialog(MainActivity.this,
								updateInfo);
					}
					break;
				case UpdateStatus.No: // has no update
					break;
				default:
					break;
				}
			}

		});

		// 对话框监听
		UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {
			@Override
			public void onClick(int status) {
				LogUtil.d("update status=" + status + ",fouce=" + forceUpdate);
				switch (status) {
				case UpdateStatus.Update:
					mDialogLoading.show();
					break;
				default:
					if (forceUpdate) {
						showMsgLong(R.string.umeng_update_tip_msg);
						ActivityStackManager.getStackManager()
								.popAllActivitys();
					} else {

					}
					break;
				}
			}
		});

		// 设置下载监听
		UmengUpdateAgent.setDownloadListener(new UmengDownloadListener() {

			@Override
			public void OnDownloadUpdate(int arg0) {

			}

			@Override
			public void OnDownloadStart() {

			}

			@Override
			public void OnDownloadEnd(int arg0, String arg1) {
				switch (arg0) {
				case UpdateStatus.DOWNLOAD_COMPLETE_FAIL:
					showMsgLong(R.string.download_fail);
					mDialogLoading.dismiss();
					break;
				case UpdateStatus.DOWNLOAD_COMPLETE_SUCCESS:
					showMsg(R.string.download_success);
					UmengUpdateAgent.startInstall(MainActivity.this, new File(
							arg1));
					// System.exit(0);
					ActivityStackManager.getStackManager().popAllActivitys();
					break;
				case UpdateStatus.DOWNLOAD_NEED_RESTART:

					break;
				default:
					break;
				}
			}
		});
		UmengUpdateAgent.update(this);
	}

	/**
	 * zhangyu
	 */

	private void showMsg(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	private void showMsg(int resId) {
		showMsg(getString(resId));
	}

	private void showMsgLong(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	private void showMsgLong(int resId) {
		showMsgLong(getString(resId));
	}

	public TimeUploadService mLocateService;

	private void bindService() {
		Intent intent = new Intent(MainActivity.this,
				TimeUploadService.class);
		this.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}

	private void unBindService() {
		if (mServiceConnection != null) {
			this.unbindService(mServiceConnection);
			mServiceConnection = null;
		}
	}

	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {

			mLocateService = ((LocalBinder) service).getService();
//			mLocateService.requestWeather();

			// mLocateService.testData();
		}
	};
	private MyBroadCastReceiver myBroadCastReceiver;

	private void registBroadCast() {
		// 生成广播处理
		myBroadCastReceiver = new MyBroadCastReceiver();
		// 实例化过滤器并设置要过滤的广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(TimeUploadService.BROADCAST_SHOW_GPS_DIALOG);
		intentFilter
				.addAction(TimeUploadService.BROADCAST_SHOW_NETWORK_DIALOG);
		intentFilter
				.addAction(TimeUploadService.BROADCAST_SHOW_EXIT_DIALOG);
		// 注册广播
		registerReceiver(myBroadCastReceiver, intentFilter);
	}

	private void unRegistBroadCast() {
		if (myBroadCastReceiver != null) {
			unregisterReceiver(myBroadCastReceiver);
		}
	}

	protected class MyBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (TimeUploadService.BROADCAST_SHOW_GPS_DIALOG.equals(action)) {
				showGpsDialog(MainActivity.this);
			} else if (TimeUploadService.BROADCAST_SHOW_NETWORK_DIALOG
					.endsWith(action)) {
				showNetworkDialog(MainActivity.this);
			} else if (TimeUploadService.BROADCAST_SHOW_EXIT_DIALOG
					.endsWith(action)) {
				String msg = intent.getExtras().getString(
						TimeUploadService.BROADCAST_MSG_EXIT);
				showExitDialog(msg);
			}
		}
	}

	private Builder mExitDialog;
	private boolean isShowExitDialog = false;

	protected void showExitDialog(String msg) {
		PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS,
				Constants.Value.WORKED);
		if (isShowExitDialog) {
			return;
		}

		if (mExitDialog == null) {
			mExitDialog = new AlertDialog.Builder(MainActivity.this)
					.setTitle(this.getString(R.string.system_tip))
					.setMessage(msg)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									exitApp(MainActivity.this);
									mExitDialog = null;
									isShowExitDialog = false;
								}
							}).setOnCancelListener(new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							exitApp(MainActivity.this);
							mExitDialog = null;
							isShowExitDialog = false;
						}
					});
		}
		mExitDialog.show();
		isShowExitDialog = true;

	}

	private void exitApp(Activity activity) {
		PreferencesUtil.putValue(PreferencesUtil.KEY_WORK_STATS,
				Constants.Value.WORKED);
		// 点击ok，退出当前帐号
		PreferencesUtil.clearLocalData(PreferenceManager
				.getDefaultSharedPreferences(activity));
		Intent intent = new Intent(activity, UserLoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(intent);
	}

	private void setAlarmTime() {
		AlarmManager am = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent("android.alarm.action.OperateLog");
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 24);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		int interval = 24 * 60 * 60 * 1000;// 上传间隔
		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				interval, sender);

		// // TEST
		// calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)+1);
		// int interval = 60*1000;//test
		// am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
		// interval, sender);

	}

	private void uploadCarshLogFile() {
		if (!UtilsAndroid.Set.checkNetState(this)) {
			return;
		}
		File file = new File(Constants.SDBNET.SDPATH_CRASH);
		File[] files = file.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i] != null && files[i].isFile()) {
					String msg = UtilsJava.getFileStrs(files[i]
							.getAbsolutePath());
					if (TextUtils.isEmpty(msg)) {
						continue;
					}
					Log.i(TAG, "ERROR UPLOAD");
					ErrLogUtils.uploadErrLog(MainActivity.this, msg);
					files[i].delete();
				}
			}
		}
	}

	private void saveOperateLog(String content) {
		saveOperateLog(content, null);

	}

	private void saveOperateLog(String content, Date date) {

		if (!UtilsCommon.checkAccount()) {
			return;
		}
		DBManager manager = new DBManager(this);
		manager.saveOperate(content, date);
		manager.closeDatabase();
	}

	/**
	 * 打开gps
	 */
	private static Builder mGpsDialog;
	private static boolean isShowGpsDialog = false;

	public static void showGpsDialog(final Context context) {
		if (isShowGpsDialog) {
			return;
		}
		if (mGpsDialog == null) {
			mGpsDialog = new AlertDialog.Builder(context)
					.setTitle(context.getString(R.string.confirm_tip))
					.setMessage(context.getString(R.string.is_open_gps))
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 开启GPS
									UtilsAndroid.Set.openGPSSettings(context);
									mGpsDialog = null;
									isShowGpsDialog = false;
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mGpsDialog = null;
									isShowGpsDialog = false;
								}
							}).setOnCancelListener(new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							mGpsDialog = null;
							isShowGpsDialog = false;

						}
					});
		}

		mGpsDialog.show();
		isShowGpsDialog = true;
		Toast.makeText(context, "请开启GPS！", Toast.LENGTH_SHORT).show();
		// Intent intent = new Intent(Settings.ACTION_SETTINGS);
		// startActivityForResult(intent, 0); // 此为设置完成后返回到获取界面
	}

	/**
	 * 打开network
	 */
	private static Builder mNetworkDialog;
	private static boolean isShowNetworkDialog = false;

	public static void showNetworkDialog(final Context context) {
		if (isShowNetworkDialog) {
			return;
		}
		if (mNetworkDialog == null) {
			mNetworkDialog = new AlertDialog.Builder(context)
					.setTitle(context.getString(R.string.confirm_tip))
					.setMessage(context.getString(R.string.is_open_network))
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 开启GPS
									UtilsAndroid.Set.openNetwork(context);
									mNetworkDialog = null;
									isShowNetworkDialog = false;
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mNetworkDialog = null;
									isShowNetworkDialog = false;
								}
							}).setOnCancelListener(new OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {
							mNetworkDialog = null;
							isShowNetworkDialog = false;

						}
					});
		}

		mNetworkDialog.show();
		isShowNetworkDialog = true;
		Toast.makeText(context, "请开启网络！", Toast.LENGTH_SHORT).show();
	}

	// 校验Tag Alias 只能是数字,英文字母和中文
	private boolean isValidTagAndAlias(String s) {
		if (TextUtils.isEmpty(s)) {
			return false;
		}
		Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$");
		Matcher m = p.matcher(s);
		return m.matches();
	}

	private void setJpushAliasAndTags() {
		boolean isSetAliasTags = PreferencesUtil.getValue(
				PreferencesUtil.KEY_ALIAS_TAGS, false);
		if (isSetAliasTags)
			return;

		String alias = PreferencesUtil.getValue(PreferencesUtil.KEY_USER_TEL);
		if (!isValidTagAndAlias(alias))
			return;

		String userPrem = PreferencesUtil.getValue(
				PreferencesUtil.KEY_EXECUTE_MENU, null);
		Set<String> tags = new HashSet<String>();
		String[] prems = userPrem.split(",");
		for (int i = 0; i < prems.length && i < 100; i++) {
			if (!TextUtils.isEmpty(prems[i].trim()))
				tags.add(prems[i].trim());
		}

		JPushInterface.setAliasAndTags(this, alias, tags,
				new TagAliasCallback() {

					@Override
					public void gotResult(int responseCode, String alias,
							Set<String> tags) {
						Log.d(TAG, responseCode + ":" + alias + "\n" + tags);
						if (responseCode == 0) {
							PreferencesUtil.putValue(
									PreferencesUtil.KEY_ALIAS_TAGS, true);
						}
					}
				});
	}

	private void unInstallApp() {

		// List<PackageInfo> packageInfos =
		// getPackageManager().getInstalledPackages(0);
		// for(PackageInfo info:packageInfos){
		// Log.e(TAG,info.packageName+"<"+info.versionCode+"<"+info.versionName);
		// }
		// // Intent intent = new Intent();
		// // intent.setAction(Intent.ACTION_DELETE);
		// //
		// //// 07-20 11:54:56.386: E/MainActivity(9882):
		// com.youdao.note<42<4.2.0
		// // intent.setData(Uri.parse("com.youdao.note"));
		// //// intent.setData(Uri.parse("com.android.hywy.demon"));
		// // startActivity(intent);
		// Uri uri = Uri.parse("package:" + ("com.android.hywy.demon"));
		// Intent intent = new Intent(Intent.ACTION_DELETE, uri);
		// this.startActivity(intent);
	}

}
