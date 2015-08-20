package com.sdbnet.hywy.employee.ui.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.sdbnet.hywy.employee.R;

import com.sdbnet.hywy.employee.utils.Constants;

public class DialogOrderOperate extends Dialog {

	private Context context;
	private SimpleAdapter lvAdapter;
	public static final String LIST_TEXT = "tvMotorCont";
	public static final String LIST_IMAGEVIEW = "ibMotorImg";
	private ListView lvMotorDia;
	private List<Map<String, Object>> items;
	private ICallBackListener listener;

	// private Intent intent;

	// private List<Map<String, String>> list = new ArrayList<Map<String,
	// String>>();

	public DialogOrderOperate(Context context, ICallBackListener listener) {
		super(context, R.style.MotorDialogStyle);
		this.context = context;
		this.listener = listener;
	}

	public DialogOrderOperate(Context context) {
		super(context, R.style.MotorDialogStyle);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_order_operate);

		lvMotorDia = (ListView) findViewById(R.id.lvDialog);
		initListView();
	}

	private void initListView() {
		items = getData();
		lvAdapter = new SimpleAdapter(context, items,
				R.layout.item_order_operate_dialog, new String[] { LIST_TEXT,
						LIST_IMAGEVIEW }, new int[] { R.id.tvMotorCont,
						R.id.ibMotorImg });
		lvMotorDia.setAdapter(lvAdapter);
		lvMotorDia.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (listener != null) {
					listener.onItemListener(position);
				}
				DialogOrderOperate.this.dismiss();

			}
		});
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> iteMap = null;

		iteMap = new HashMap<String, Object>();
		iteMap.put(LIST_TEXT, Constants.Value.TEXT_EXEC);
		iteMap.put(LIST_IMAGEVIEW, R.drawable.white_business);
		list.add(iteMap);

		iteMap = new HashMap<String, Object>();
		iteMap.put(LIST_TEXT, Constants.Value.TEXT_CLEAR);
		iteMap.put(LIST_IMAGEVIEW, R.drawable.white_close);
		list.add(iteMap);

		return list;
	}

	public boolean isShowTitle() {
		return isShowTitle;
	}

	private boolean isShowTitle = true;

	public void setTitleStatu(boolean statu) {
		isShowTitle = statu;
	}

	/**
	 * 删除后的回调监听，便于刷新界面
	 * 
	 * @author Administrator
	 * 
	 */
	public static interface ICallBackListener {
		// void onDeleted();
		void onItemListener(int position);
	}

	public void setOnItemListener(ICallBackListener listener) {
		this.listener = listener;
	}
}
