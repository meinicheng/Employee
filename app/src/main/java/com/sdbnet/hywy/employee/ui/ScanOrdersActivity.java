package com.sdbnet.hywy.employee.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.adapter.MyBaseAdapter;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;

public class ScanOrdersActivity extends BaseActivity {

	private ImageView mBack;
	private ArrayList<String> scanList;
	private ScanResultAdapter adapter;
	private ListView mLvOrder;
	private TextView mTextTitle;
	public static final int REQUEST_CODE_LOOK_ORDER = 11;
	public static final int RESULT_CODE_LOOK_ORDER = 12;
	public static final String EXTRA_ORDER_LIST = "order_list";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActivityStackManager.getStackManager().pushActivity(this);
		setContentView(R.layout.activity_scan_order_list);
		initData();
		initUI();

	}

	private void initData() {
		// 获取用户扫描的订单列表
		scanList = getIntent().getStringArrayListExtra(
				ScanOrdersActivity.EXTRA_ORDER_LIST);
		// // 去重复
		// Set<String> set = new HashSet<String>();
		// set.addAll(scanList);
		//
		// scanList.clear();
		// scanList.addAll(set);
	}

	private void initUI() {

		mBack = (ImageView) findViewById(R.id.common_view_title_img);

		adapter = new ScanResultAdapter(scanList);
		mLvOrder = (ListView) findViewById(R.id.activity_scan_order_lv_scan_bar_code);
		mLvOrder.setAdapter(adapter);

		mTextTitle = (TextView) findViewById(R.id.common_view_title_text);
		mTextTitle.setText(String.format(getString(R.string.scanned_barcode),
				PreferencesUtil.ordtitle));

		mBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	/**
	 * 当用户退出该界面时，返回订单扫描列表
	 */

	@Override
	public void onBackPressed() {
		Intent data = new Intent();
		data.putStringArrayListExtra(ScanOrdersActivity.EXTRA_ORDER_LIST,
				scanList);
		setResult(ScanOrdersActivity.RESULT_CODE_LOOK_ORDER, data);
		super.onBackPressed();
	}

	class ScanResultAdapter extends MyBaseAdapter<String> {

		public ScanResultAdapter(List<String> list) {
			super(list);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			ViewHolder viewHolder;
			if (null == convertView) {
				viewHolder = new ViewHolder();
				convertView = View.inflate(ScanOrdersActivity.this,
						R.layout.item_order_scan, null);

				viewHolder.tv_bar_code = (TextView) convertView
						.findViewById(R.id.item_order_code_tv_bar_code);
				viewHolder.tv_count = (TextView) convertView
						.findViewById(R.id.item_order_code_tv_count);
				viewHolder.iv_delete = (ImageView) convertView
						.findViewById(R.id.item_order_code_iv_delete);

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
			viewHolder.tv_bar_code.setText(scanList.get(position));
			viewHolder.tv_count.setText((scanList.size() - position) + "");

			return convertView;
		}

		class ViewHolder {
			TextView tv_bar_code;
			TextView tv_count;
			ImageView iv_delete;
		}
	}

	private void showDeleteDialog(final int position) {
		String title = getString(R.string.delete_tip);
		String msg = getString(R.string.delete_tip_msg);// "您确认删除这条记录吗？")
		new AlertDialog.Builder(ScanOrdersActivity.this)
				.setTitle(title)
				.setMessage(msg)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String operate = String.format(
										getString(R.string.delete_add_order_x),
										scanList.get(position));
								saveOperateLog(operate, null);
								scanList.remove(position);
								adapter.notifyDataSetChanged();
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
	public void onDestroy() {
		// ActivityStackManager.getStackManager().popActivity(this);
		super.onDestroy();

	}
}
