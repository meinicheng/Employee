package com.sdbnet.hywy.employee.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;

public class ChooseWeightActivity extends BaseActivity {
	private ImageView mBack;
	private ListView mLvWeight;
	private String[] mWeights;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActivityStackManager.getStackManager().pushActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choose_weight);
		initData();
		initControls();
		initListener();
	}

	private void initData() {
		mWeights = new String[] { getString(R.string.unlimited), "2", "3", "4",
				"5", "6", "7", "8", "10", "11", "12", "13", "14", "15", "16",
				"17", "18", "19", "20", "25", "28", "30", "32", "34", "35",
				"36", "37", "38", "40", "42", "45", "48", "50", "55", "60",
				"65", "70", "75", "80", "85", "90", "95", "100", "300", "400",
				"500", "600", "700", "800", "900", "1000", "1500" };

	}

	private void initControls() {
		mLvWeight = (ListView) findViewById(R.id.activity_choose_weight_list);
		mBack = (ImageView) findViewById(R.id.common_view_title_img);
		((TextView) findViewById(R.id.common_view_title_text))
				.setText(R.string.select_vehicle_load_t);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.item_choose_length, R.id.content, mWeights);
		mLvWeight.setAdapter(adapter);
	}

	private void initListener() {

		mLvWeight.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (0 != position)
					saveOperateLog(mWeights[position], null);

				Intent intent = new Intent();
				intent.putExtra(EditInfoActivity.EXTRA_LOAD, mWeights[position]);// 取得是获得数据的item里面的值传过去
				setResult(EditInfoActivity.RESULT_CHOOSE_LOAD, intent);
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

	@Override
	public void onDestroy() {
		// ActivityStackManager.getStackManager().popActivity(this);
		super.onDestroy();

	}
}
