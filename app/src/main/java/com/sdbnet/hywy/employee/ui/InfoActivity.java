package com.sdbnet.hywy.employee.ui;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.album.Bimp;
import com.sdbnet.hywy.employee.album.ImageGridShowAdapter;
import com.sdbnet.hywy.employee.model.CompanyModel;
import com.sdbnet.hywy.employee.model.UserModel;
import com.sdbnet.hywy.employee.net.AsyncHttpService;
import com.sdbnet.hywy.employee.ui.EditInfoActivity;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;
import com.sdbnet.hywy.employee.ui.widget.CustomGridView;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.ErrLogUtils;
import com.sdbnet.hywy.employee.utils.LogUtil;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.ToastUtil;
import com.sdbnet.hywy.employee.utils.UtilsBean;
import com.sdbnet.hywy.employee.utils.UtilsError;

public class InfoActivity extends BaseActivity {
	private static final String TAG = "InfoFragment";
	public static final int REQUEST_CODE_EDIT_INFO = 11;
	public static final int RESUULT_CODE_EDIT_INFO = 21;

	private ImageView mImgBack;
	private TextView mTextTitle;

	private Button mBtInfoEdit;
	private ImageGridShowAdapter mImgAdapter;

	private UserModel mUserBean;
	private CompanyModel mCompanyBean;
	private TextView mTvProjectName;
	private TextView mTvCompanyEmail;
	private TextView mTvCompanyUrl;
	private TextView mTvCompanyDescribe;
	private TextView mTvCompanyAddress;
	private TextView mTvCompanyTel;
	private TextView mTvContactor;
	private TextView mTvCompanyName;
	private TextView mTvPlateNum;
	private TextView mTvUserTel;
	private TextView mTvUserName;
	private TextView mTvUserSex;
	private LinearLayout lay_driver;
	private TextView mTvVehicleModel;
	private TextView mTvVehiclelength;
	private TextView mTvVehicleLoad;
	private CustomGridView mGridShow;

	private ScrollView mScroll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// view = inflater.inflate(R.layout.fragment_show_info, null);
		// setContentView(R.layout.fragment_show_info);
		setContentView(R.layout.activity_info);
		initControls();
		loadDatas();
	}

	/**
	 * 加载资料信息
	 */
	private void loadDatas() {
		AsyncHttpService.getUserInfo(new JsonHttpResponseHandler() {

			@Override
			public void onStart() {
				showLoadDialog((R.string.struggling_loading_ellipsis));
				super.onStart();
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				dismissLoadDialog();
				showLongToast(R.string.httpisNull);

			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				// System.out.println("info:" + response.toString());
				LogUtil.d(TAG, "info:" + response.toString());
				super.onSuccess(statusCode, headers, response);
				try {
					if (UtilsError.isErrorCode(InfoActivity.this, response)) {
						return;
					}

					// 解析企业资料
					JSONObject jsonComp = response
							.getJSONObject(Constants.Feild.KEY_ENTERPRISE);
					mCompanyBean = UtilsBean.jsonToCmpBean(jsonComp);

					// 解析用户资料
					JSONObject jsonStaff = response
							.getJSONObject(Constants.Feild.KEY_STAFF);
					mUserBean = UtilsBean.jsonToUserBean(jsonStaff);

					fillDatas();
				} catch (JSONException e) {
					ToastUtil.show(InfoActivity.this, "解析用户数据失败");
					ErrLogUtils.uploadErrLog(InfoActivity.this,
							ErrLogUtils.toString(e));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dismissLoadDialog();
				}
			}

		}, this);

	}

	/**
	 * 初始化控件
	 */
	private void initControls() {
//		mTextTitle = (TextView) findViewById(R.id.activity_info_title_text);
		mTextTitle = (TextView) findViewById(R.id.common_view_title_text);
		mTextTitle.setText(getString(R.string.menuInfo));

		mImgBack = (ImageView) findViewById(R.id.common_view_title_img);
		mImgBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		mBtInfoEdit = (Button) findViewById(R.id.activity_info_bt_info_edit);

		mTvCompanyName = (TextView) findViewById(R.id.activity_info_tv_company_name);
		mTvContactor = (TextView) findViewById(R.id.activity_info_tv_contactor);
		mTvCompanyTel = (TextView) findViewById(R.id.activity_info_tv_company_tel);
		mTvCompanyAddress = (TextView) findViewById(R.id.activity_info_tv_company_address);
		mTvCompanyDescribe = (TextView) findViewById(R.id.activity_info_tv_company_describe);
		mTvCompanyUrl = (TextView) findViewById(R.id.activity_info_tv_company_url);
		mTvCompanyEmail = (TextView) findViewById(R.id.activity_info_tv_company_email);
		mTvProjectName = (TextView) findViewById(R.id.activity_info_tv_project_name);

		mTvUserName = (TextView) findViewById(R.id.activity_info_tv_user_name);
		mTvUserTel = (TextView) findViewById(R.id.activity_info_tv_user_tel);
		mTvUserSex = (TextView) findViewById(R.id.activity_info_tv_user_sex);
		mGridShow = (CustomGridView) findViewById(R.id.activity_info_cgv_show);

		mScroll = (ScrollView) findViewById(R.id.activity_info_sv_content);
		mScroll.fullScroll(View.FOCUS_UP);

		// 用户为车辆用户，显示相关信息
		if (Constants.Value.YES.equals(PreferencesUtil.is_driver)) {
			lay_driver = (LinearLayout) findViewById(R.id.activity_info_lay_driver);
			lay_driver.setVisibility(View.VISIBLE);
			mTvPlateNum = (TextView) findViewById(R.id.activity_info_tv_plate_number);
			mTvVehicleModel = (TextView) findViewById(R.id.activity_info_tv_vehicle_model);
			mTvVehiclelength = (TextView) findViewById(R.id.activity_info_tv_vehicle_length);
			mTvVehicleLoad = (TextView) findViewById(R.id.activity_info_tv_vehicle_load);

		}

		mBtInfoEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				saveOperateLog(mBtInfoEdit.getText().toString(), null);
				startEditInfo();
			}
		});
	}

	/**
	 * 将用户资料传递到编辑界面
	 */
	private void startEditInfo() {
		if (mUserBean == null) {
			return;
		}
		PreferencesUtil.putValue(PreferencesUtil.KEY_ERROR_LOG,
				Constants.Step.EDIT);
		Intent intent = new Intent(this, EditInfoActivity.class);
		intent.putExtra(Constants.IntentData.INTENT_USER_BEAN, mUserBean);
		startActivityForResult(intent, REQUEST_CODE_EDIT_INFO);
	}

	/**
	 * 显示加载企业用户资料
	 */
	private void fillDatas() {
		if (mCompanyBean != null) {
			mTvCompanyName.setText(mCompanyBean.cmpName);
			mTvContactor.setText(mCompanyBean.linkman);
			mTvCompanyTel.setText(mCompanyBean.telephone1);
			mTvCompanyAddress.setText(mCompanyBean.telephone2);
			mTvCompanyDescribe.setText(mCompanyBean.remark);
			mTvCompanyUrl.setText(mCompanyBean.url);
			mTvCompanyEmail.setText(mCompanyBean.email);
			mTvProjectName.setText(mCompanyBean.itemName);
		}
		if (mUserBean != null) {
			mTvUserName.setText(mUserBean.userName);
			mTvUserTel.setText(mUserBean.locTel);
			mTvUserSex
					.setText(mUserBean.sex.equals("0") ? getString(R.string.man)
							: getString(R.string.woman));

			// 用户为车辆用户，显示相关信息
			if (Constants.Value.YES.equals(PreferencesUtil.is_driver)) {

				mTvVehicleModel.setText(mUserBean.truckType);
				mTvPlateNum.setText(mUserBean.truckNum);
				mTvVehicleLoad.setText(mUserBean.truckWeight
						+ getString(R.string.t));
				mTvVehiclelength.setText(mUserBean.truckLength
						+ getString(R.string.m));
			}
		}
		// 显示用户图片
		mImgAdapter = new ImageGridShowAdapter(this, mUserBean.imgList, false);
		mGridShow.setAdapter(mImgAdapter);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Bimp.clearCache();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESUULT_CODE_EDIT_INFO) {
			loadDatas();
		}
	}
}
