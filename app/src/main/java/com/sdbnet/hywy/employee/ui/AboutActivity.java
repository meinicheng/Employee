package com.sdbnet.hywy.employee.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdbnet.hywy.employee.ActivityStackManager;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;
import com.sdbnet.hywy.employee.utils.UtilsAndroid;

public class AboutActivity extends BaseActivity {
	private ImageView mBack;
	private TextView mTextTitle;
	private TextView tvVersion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActivityStackManager.getStackManager().pushActivity(this);
		setContentView(R.layout.activity_about);
		initControls();
	}

	private void initControls() {
//		mBack = (ImageView) findViewById(R.id.activity_about_img_back);
		mTextTitle=(TextView) findViewById(R.id.common_view_title_text);
		mTextTitle.setText(R.string.about_us);
		mBack = (ImageView) findViewById(R.id.common_view_title_img);
		mBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		tvVersion = (TextView) findViewById(R.id.activity_about_version);
		tvVersion.setText(UtilsAndroid.Set.getVersionName(this));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ActivityStackManager.getStackManager().popActivity(this);

	}
}
