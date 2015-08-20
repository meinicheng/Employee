package com.sdbnet.hywy.employee.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;

public class ChooseLengthActivity extends BaseActivity {
	private final String TAG = "ActivityChooseLength";
	private ImageView mBack;
	private ListView mLvLength;

	private final int CHOOSE_WEIGHT = 20;
	private String[] mLenghts;

	// private List<String> mLenghtList;
	// private static final float MIX_LENGTH=4.0f;
	// private static final float MAX_LENGTH=25;
	// private static final float
	// private static final int SIZE=(MAX_LENGTH-MIX_LENGTH)/0.5f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActivityStackManager.getStackManager().pushActivity(this);

		setContentView(R.layout.activity_choose_length);

		initData();
		initControls();
		initListener();
	}

	private void initData() {
		mLenghts = new String[] { getString(R.string.unlimited), "4.2", "4.5",
				"5", "5.5", "5.8", "6", "6.2", "6.5", "6.8", "7", "7.2", "7.5",
				"8", "8.5", "8.6", "9", "9.5", "9.6", "10", "10.5", "11",
				"11.5", "12", "12.5", "13", "13.5", "14", "14.5", "15", "15.5",
				"16", "16.5", "17", "17.5", "18", "18.5", "19", "19.5", "20",
				"20.5", "21", "21.5", "22", "22.5", "23", "23.5", "24", "24.5",
				"25" };
		// mLenghtList=new ArrayList<String>();
		// for(int i=0;i<20;i++){
		// mLenghtList.add(object)
		// }

	}

	private void initControls() {
		mLvLength = (ListView) findViewById(R.id.activity_choose_length_list);
		mBack = (ImageView) findViewById(R.id.common_view_title_img);
		((TextView) findViewById(R.id.common_view_title_text))
				.setText(R.string.select_vehicle_length_m);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.item_choose_length, R.id.content, mLenghts);
		mLvLength.setAdapter(adapter);
	}

	private void initListener() {

		mLvLength.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (0 != position)
					saveOperateLog(mLenghts[position], null);
				Log.i(TAG, "position=" + position);
				Intent intent = new Intent();
				// if (ActivityChooseModel.isChooseAll) {
				// intent.setClass(ActivityChooseLength.this,
				// ActivityChooseWeight.class);
				// startActivityForResult(intent, CHOOSE_WEIGHT);
				// return;
				// }
				intent.putExtra(EditInfoActivity.EXTRA_LENGTH,
						mLenghts[position]);// 取得是获得数据的item里面的值传过去
				setResult(EditInfoActivity.RESULT_CHOOSE_LENGTH, intent);
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
	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// inte) {
	// super.onActivityResult(requestCode, resultCode, inte);
	// if (resultCode > 0) {
	// switch (requestCode) {
	// case CHOOSE_WEIGHT:
	// inte.setClass(ActivityChooseLength.this,
	// ActivityChooseModel.class);
	// inte.putExtra("length", length);
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
