package com.sdbnet.hywy.employee.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;

public class ChooseModelActivity extends BaseActivity {
	private static final String TAG = "ActivityChooseModel";
	private ListView mListModel;
	private ImageView mBack;
	private final String MAP_KEY_CAR = "car";
	private final String MAP_KEY_IMG = "img";

	// public static boolean isChooseAll = false;

	private ArrayList<HashMap<String, Object>> itemsList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActivityStackManager.getStackManager().pushActivity(this);

		setContentView(R.layout.activity_choose_model);
		initItemList();
		initControls();
		initListener();
	}

	private void initControls() {
		mBack = (ImageView) findViewById(R.id.common_view_title_img);
		((TextView) findViewById(R.id.common_view_title_text))
				.setText(R.string.select_vehicle_model);
		// 取得ListView实例
		mListModel = (ListView) findViewById(R.id.activity_choose_model_list);
		// 要在ListView中显示的数据集合

		SimpleAdapter adapter = new SimpleAdapter(this, itemsList,
				R.layout.item_choose_model, new String[] { "img", "car" },
				new int[] { R.id.img, R.id.car });
		// 位ListView设置Adapter
		mListModel.setAdapter(adapter);

	}

	private void initListener() {
		mListModel.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// String model = (String) ((TextView)
				// view.findViewById(R.id.car))
				// .getText();
				String model = (String) itemsList.get(position)
						.get(MAP_KEY_CAR);
				saveOperateLog(model, null);
				Log.i(TAG, "position=" + position + ";" + "model=" + model);
				Intent intent = new Intent();
				// if (isChooseAll) {
				// intent.setClass(ActivityChooseModel.this,
				// ActivityChooseLength.class);
				// startActivityForResult(intent,
				// Constants.ResultCode.QUERY_CHOOSE_MODEL);
				// return;
				// }
				intent.putExtra(EditInfoActivity.EXTRA_MODEL, model);// 取得是获得数据的item里面的值传过去
				setResult(EditInfoActivity.RESULT_CHOOSE_MODEL, intent);
				finish();

			}

		});

		mBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	/**
	 * 取得用于ListView的数据
	 * 
	 * @return
	 */
	private void initItemList() {
		itemsList = new ArrayList<HashMap<String, Object>>();
		String[] name = { getString(R.string.flat_car),
				getString(R.string.ladder_truck), getString(R.string.wall_car),
				getString(R.string.dumper), getString(R.string.premium_car),
				getString(R.string.machinery_car), getString(R.string.van),
				getString(R.string.pallet_carrier),
				getString(R.string.market_car_truck),
				getString(R.string.fly_car), getString(R.string.thermos_van),
				getString(R.string.refrigerator_car),
				getString(R.string.tanker), getString(R.string.other) };
		int[] a = { R.drawable.ping_ban_che, R.drawable.pa_ti_che,
				R.drawable.lan_ban_che, R.drawable.zi_xie_che,
				R.drawable.gao_lan_che, R.drawable.gong_cheng_che,
				R.drawable.xiang_shi_huo_che, R.drawable.ji_zhuang_xiang_che,
				R.drawable.shang_pin_yun_shu, R.drawable.fei_yi_che,
				R.drawable.bao_wen_che, R.drawable.leng_cang_che,
				R.drawable.you_guan_che, R.drawable.xiang_shi_huo_che, };

		for (int i = 0; i < name.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(MAP_KEY_IMG, a[i]);
			map.put(MAP_KEY_CAR, name[i]);
			itemsList.add(map);

		}

	}

	@Override
	public void onDestroy() {
		// ActivityStackManager.getStackManager().popActivity(this);
		super.onDestroy();
	}
	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// inte) {
	// super.onActivityResult(requestCode, resultCode, inte);
	// if (resultCode > 0) {
	// switch (requestCode) {
	// case CHOOSE_LENGTH:
	// inte.putExtra("model", model);
	// setResult(10, inte);
	// finish();
	// break;
	//
	// default:
	// break;
	// }
	// }
	// }

}
