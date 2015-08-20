package com.sdbnet.hywy.employee.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.db.db.DBManager;
import com.sdbnet.hywy.employee.db.db.DBCustomValue;
import com.sdbnet.hywy.employee.model.ExecuteAction;
import com.sdbnet.hywy.employee.ui.OrderExecuteActivity;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;
import com.sdbnet.hywy.employee.ui.widget.DialogOrderOperate;
import com.sdbnet.hywy.employee.ui.widget.RTPullListView;
import com.sdbnet.hywy.employee.ui.widget.RTPullListView.OnRefreshListener;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.LogUtil;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;

public class DraftBoxActivtiy extends BaseActivity {
	private final String TAG = "DraftBoxFragment";
	private static final int LOAD_NEW_INFO = 15;
	private static final int REFLUSH_DATA = 16;

	private RTPullListView mPlvOrder;
	private LinearLayout mLlNoRecord;
	private List<ExecuteAction> mOrderList = new ArrayList<ExecuteAction>();
	private BaseAdapter mListAdapter;

	private Handler myHandler;
	private LinearLayout mSlidLayImg;

	// private ICallBackListener cbListener;

	private ImageView mImgBack;
	private TextView mTextTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_draft_box);
		// setContentView(R.layout.activity_draft_box);
		initHandler();
		initControls();
	}

	@Override
	public void onStart() {
		mSlidLayImg.setVisibility(View.VISIBLE);
		// loadData();
		super.onStart();
	}

	@Override
	public void onResume() {
		if (myHandler == null) {
			initHandler();
		}
		// myHandler.sendEmptyMessageDelayed(REFLUSH_DATA, 1000);
		loadData();
		super.onResume();
	}

	@Override
	public void onStop() {
		mSlidLayImg.setVisibility(View.GONE);
		super.onStop();
	}

	private void initHandler() {
		myHandler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				switch (msg.what) {
				case LOAD_NEW_INFO: // 更新下拉刷新列表
					pullFlush();
					break;
				case REFLUSH_DATA:
					loadData();
					break;
				default:
					break;
				}
				return false;
			}
		});
	}

	// 初始化控件
	private void initControls() {

		mTextTitle = (TextView) findViewById(R.id.activity_draft_box_title_text);
		mTextTitle.setText(getString(R.string.menuDraftBox));
		mImgBack = (ImageView) findViewById(R.id.activity_draft_box_img_back);
		mImgBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		mPlvOrder = (RTPullListView) findViewById(R.id.activity_draft_box_plv_order_list);
		mLlNoRecord = (LinearLayout) findViewById(R.id.activity_draft_box_no_record_ll);

		mListAdapter = new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				ViewHolder mHolder;
				ExecuteAction executeAction = mOrderList.get(position);

				if (convertView == null) {
					convertView = getLayoutInflater().inflate(
							R.layout.item_order_draft_box, null);// .getView(position,

					mHolder = new ViewHolder();
					mHolder.tvOrderNum = (TextView) convertView
							.findViewById(R.id.tv_ordnos);
					mHolder.tvOrderTime = (TextView) convertView
							.findViewById(R.id.tv_scan_time);
					mHolder.tvOrderAction = (TextView) convertView
							.findViewById(R.id.tv_exec_action);
					mHolder.tvOrderAction.setTextColor(getResources().getColor(
							R.color.green));

					convertView.setTag(mHolder);
				} else {
					mHolder = (ViewHolder) convertView.getTag();
				}
				mHolder.tvOrderNum.setText(executeAction.getActidx());
				mHolder.tvOrderTime.setText(executeAction.getActTime());
				mHolder.tvOrderAction.setText(executeAction.getActname());
				return convertView;
			}

			class ViewHolder {

				TextView tvOrderNum;
				TextView tvOrderTime;
				TextView tvOrderAction;

			}

			@Override
			public int getCount() {
				return mOrderList.size();
			}

			@Override
			public Object getItem(int position) {
				return mOrderList.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}
		};

		mPlvOrder.setAdapter(mListAdapter);
		mListAdapter.notifyDataSetChanged();
		mPlvOrder.onRefreshComplete();
		mSlidLayImg = (LinearLayout) findViewById(R.id.activity_draft_box_clear_draft_ll);
		mSlidLayImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clearDraftBox();
			}
		});

		// 下拉刷新接口实现
		mPlvOrder.setonRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				loadData();
			}
		});
		mPlvOrder.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Map<String, String> map = (Map<String, String>)
				// arg1.getTag();
				ExecuteAction order = mOrderList.get((int) arg3);
				showExecDialog(order);
			}
		});

	}

	private void showExecDialog(final ExecuteAction order) {

		DialogOrderOperate dialogOrder = new DialogOrderOperate(this,
				new DialogOrderOperate.ICallBackListener() {

					@Override
					public void onItemListener(int position) {
						switch (position) {
						case 0:
							// 执行订单
							execOrder(order);
							break;
						case 1:
							// 清除记录
							clearCurrentRecord(order.getActidx(),
									order.getAction());
							break;
						default:
							break;
						}

					}
				});
		dialogOrder.show();
	}

	private void execOrder(ExecuteAction order) {
		saveOperateLog("草稿箱订单号：" + order.getActidx(), null);
		// // 解析json字符串，封装为ExecuteAction对象
		// JSONObject json = new JSONObject(map.get(DBHelper.COLUMN_PARAMS));
		// JSONObject jsonAct = json.getJSONObject("action");
		// Log.i(TAG, "jsonObj: " + jsonAct.toString());
		// ExecuteAction action = UtilsBean.jsonToExecuteAction(jsonAct);

		// 点击跳转到订单执行提交界面
		Intent intent = new Intent(this, OrderExecuteActivity.class);
		Bundle bundle = new Bundle();
		// 封装页面所需传递的参数
		bundle.putSerializable(Constants.Feild.KEY_ACTION, order);
		intent.putExtras(bundle);
		intent.putExtra(Constants.Feild.KEY_ORDERS, order.getActidx());
		// intent.putExtra("imgs", json.getString("imgs"));
		intent.putExtra("fromDB", "fromDB"); // 表示此订单记录从草稿箱的数据库中来
		intent.putExtra(OrderExecuteActivity.FROM_WHERE, this.getClass().getSimpleName());
		PreferencesUtil.setSteps(order.getAction()); // 设置执行动作码
		startActivityForResult(intent,
				OrderExecuteActivity.REQUEST_CODE_EXECUTE_ORDER);
	}

	private void pullFlush() {
		if (mOrderList.size() == 0) {
			mLlNoRecord.setVisibility(View.VISIBLE);
			mPlvOrder.setVisibility(View.GONE);
		} else {
			mLlNoRecord.setVisibility(View.GONE);
			mPlvOrder.setVisibility(View.VISIBLE);
		}
		mListAdapter.notifyDataSetChanged();
		mPlvOrder.onRefreshComplete();
	}

	/**
	 * 从草稿箱数据库中加载数据
	 */
	private void loadData() {
		// mOrderList.clear();
		DBManager manager = new DBManager(this);
		mOrderList = manager.getRecords(PreferencesUtil.user_company,
				PreferencesUtil.item_id, PreferencesUtil.user_tel);
		manager.closeDatabase();

		Log.d(TAG, "mOrderList.size=" + mOrderList.size());
		for (int i = 0; i < mOrderList.size(); i++) {
			LogUtil.d(TAG, "order==" + mOrderList.get(i));
		}
		// myHandler.sendEmptyMessage(LOAD_NEW_INFO);
		pullFlush();
	}

	/**
	 * 清空草稿箱
	 */
	private void clearDraftBox() {
		saveOperateLog(getString(R.string.clear_up_draft_box), null);
		DBManager manager = new DBManager(this);
		int count = (int) manager.getOrderCount();
		manager.closeDatabase();

		if (count == 0) {
			Toast.makeText(this, getString(R.string.draft_box_no_data),
					Toast.LENGTH_SHORT).show();
			return;
		}
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.clear_tip))
				.setMessage(getString(R.string.delete_tip_msg))

				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								DBManager manager = new DBManager(
										DraftBoxActivtiy.this);
								manager.deleteAllOrders();
								int count = (int) manager.getOrderCount();
								manager.closeDatabase();

								Intent intent = new Intent();
								intent.setAction(DBCustomValue.ACTION_COUNT_CHANGED);
								intent.putExtra(DBCustomValue.COUNT, count);
								sendBroadcast(intent);
								loadData();
							}
						}).setNegativeButton(android.R.string.cancel, null)
				.show();
	}

	/**
	 * 清除草稿箱中的所选记录
	 */
	private void clearCurrentRecord(final String orders, final String orderName) {
		saveOperateLog("delete order ：" + orders + ",name=" + orderName);
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.delete_tip))
				.setMessage(getString(R.string.delete_tip_msg))
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// OperateLogUtil.saveOperate(
								// "delete order ：" + orders + ",OK");
								DBManager manager = new DBManager(
										DraftBoxActivtiy.this);
								manager.deleteOrder(orders, orderName);
								int count = (int) manager.getOrderCount();
								manager.closeDatabase();
								// new DBManager( ).deleteOrder(
								// PreferencesUtil.user_company,
								// PreferencesUtil.item_id, orders,
								// orderName);
								// 发送广播，以便草稿箱界面刷新
								Intent intent = new Intent();
								intent.setAction(DBCustomValue.ACTION_COUNT_CHANGED);
								intent.putExtra(DBCustomValue.COUNT, count);
								sendBroadcast(intent);
								loadData();
							}
						}).setNegativeButton(android.R.string.cancel, null)
				.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == OrderExecuteActivity.RESULT_CODE_CHANGED_ORDER) {

		}
	}
}
