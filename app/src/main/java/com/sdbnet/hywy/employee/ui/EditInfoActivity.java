package com.sdbnet.hywy.employee.ui;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.album.Bimp;
import com.sdbnet.hywy.employee.album.ImageGridShowAdapter;
import com.sdbnet.hywy.employee.model.UserModel;
import com.sdbnet.hywy.employee.net.AsyncHttpService;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;
import com.sdbnet.hywy.employee.ui.widget.CustomGridView;
import com.sdbnet.hywy.employee.ui.widget.SegmentedRadioGroup;
import com.sdbnet.hywy.employee.utils.Constants;
import com.sdbnet.hywy.employee.utils.ErrLogUtils;
import com.sdbnet.hywy.employee.utils.PreferencesUtil;
import com.sdbnet.hywy.employee.utils.UtilsError;
import com.sdbnet.hywy.employee.utils.UtilsJava;

public class EditInfoActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "EditInfoActivity";
	private TextView mEditTel;
	private EditText mEditPlate;
	private EditText mEditName;

	private Button mBtnSave;

	private ImageGridShowAdapter adapter;
	// private List<String> imgList = new ArrayList<String>();

	private ImageView mImgBack;
	private TextView mTextTitle;

	private SegmentedRadioGroup segmentStatus;
	private LinearLayout mLlDriverEdit;
	private TextView mEidtLoad;
	private TextView mEditLength;
	private TextView mEditModel;
	private CustomGridView mGridView;
	private RelativeLayout mRlSelectLoad;
	private RelativeLayout mRlSelectLength;
	private RelativeLayout mSelectModel;

	private UserModel mOldUserBean;
	private UserModel mNewUserBean;

	// private int selectedCount;

	public static final String EXTRA_MODEL = "vehicl_model";
	public static final String EXTRA_LENGTH = "vehicl_length";
	public static final String EXTRA_LOAD = "vehicl_load";

	public static final int REQUEST_CHOOSE_MODEL = 50;
	public static final int REQUEST_CHOOSE_LENGTH = 60;
	public static final int REQUEST_CHOOSE_LOAD = 70;

	public static final int RESULT_CHOOSE_MODEL = 51;
	public static final int RESULT_CHOOSE_LENGTH = 61;
	public static final int RESULT_CHOOSE_LOAD = 71;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActivityStackManager.getStackManager().pushActivity(this);
		setContentView(R.layout.activity_edit_info);
		initOperateStep();
		initDatas();
		initControls();
		initListener();
		initUIData();
		mEditName.clearFocus();
		mEditName.setSelection(mNewUserBean.userName.length());
	}

	/**
	 * 初始化数据显示
	 */
	private void initDatas() {

		try {
			mOldUserBean = (UserModel) getIntent().getSerializableExtra(
					Constants.IntentData.INTENT_USER_BEAN);
			mNewUserBean = (UserModel) UtilsJava.depthClone(mOldUserBean);
		} catch (Exception e) {
			ErrLogUtils.uploadErrLog(this, ErrLogUtils.toString(e));
		}

	}

	/**
	 * 初始化控件
	 */
	private void initControls() {
		// mImgBack = findViewById(R.id.activity_edit_info_iv_go_back);
		mImgBack = (ImageView) findViewById(R.id.common_view_title_img);
		mTextTitle = (TextView) findViewById(R.id.common_view_title_text);
		mTextTitle.setText(R.string.data_editor);

		mBtnSave = (Button) findViewById(R.id.activity_edit_info_bt_info_save);

		mSelectModel = (RelativeLayout) findViewById(R.id.activity_edit_info_lay_choose_model);
		mRlSelectLength = (RelativeLayout) findViewById(R.id.activity_edit_info_lay_choose_length);
		mRlSelectLoad = (RelativeLayout) findViewById(R.id.activity_edit_info_lay_choose_load);

		mGridView = (CustomGridView) findViewById(R.id.activity_edit_info_cgv_edit);

		mLlDriverEdit = (LinearLayout) findViewById(R.id.activity_edit_info_lay_driver_edit);

		mEditName = (EditText) findViewById(R.id.activity_edit_info_et_user_name);

		// 性别单选控件
		segmentStatus = (SegmentedRadioGroup) findViewById(R.id.activity_edit_info_segment_sex);

		if (Constants.Value.MAN.equals(mNewUserBean.sex)) {
			((RadioButton) findViewById(R.id.activity_edit_info_rb_man))
					.setChecked(true);
		} else {
			((RadioButton) findViewById(R.id.activity_edit_info_rb_woman))
					.setChecked(true);
		}

		mEditTel = (TextView) findViewById(R.id.activity_edit_info_et_tel);

		mEditPlate = (EditText) findViewById(R.id.activity_edit_info_et_plate);

		mEditModel = (TextView) findViewById(R.id.activity_edit_info_et_vehicle_model);
		mEditLength = (TextView) findViewById(R.id.activity_edit_info_et_vehicle_length);
		mEidtLoad = (TextView) findViewById(R.id.activity_edit_info_et_vehicle_load);

		// 用户为车辆用户，显示相关信息
		if (Constants.Value.YES.equals(PreferencesUtil.is_driver)) {
			mLlDriverEdit.setVisibility(View.VISIBLE);
		}

		// 初始化GridImage适配器
		adapter = new ImageGridShowAdapter(this, mNewUserBean.imgList, true);
		adapter.setShowEvery(true);
		mGridView.setAdapter(adapter);
	}

	private void initListener() {

		// 点击事件
		mSelectModel.setOnClickListener(this);
		mRlSelectLength.setOnClickListener(this);
		mRlSelectLoad.setOnClickListener(this);
		mBtnSave.setOnClickListener(this);
		mImgBack.setOnClickListener(this);

		segmentStatus.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.activity_edit_info_rb_man) {
					// 男
					mNewUserBean.sex = Constants.Value.MAN;
				} else if (checkedId == R.id.activity_edit_info_rb_woman) {
					// 女
					mNewUserBean.sex = Constants.Value.WOMAN;
				}
			}
		});
		// 名字变化

	}

	/**
	 * 初始化UI数据
	 */

	private void initUIData() {
		mEditPlate.setText(mNewUserBean.truckNum);
		mEditModel.setText(mNewUserBean.truckType);
		mEditLength.setText(mNewUserBean.truckLength + "");
		mEidtLoad.setText(mNewUserBean.truckWeight + "");
		mEditName.setText(mNewUserBean.userName);
		mEditTel.setText(mNewUserBean.locTel);
	}

	private void getUserData() {
		mNewUserBean.userName = mEditName.getText().toString().trim();
		mNewUserBean.truckNum = mEditPlate.getText().toString().trim();
		// 如果用户为车辆用户，则获取相应信息
		if (Constants.Value.YES.equals(PreferencesUtil.is_driver)) {
			mNewUserBean.truckNum = mEditPlate.getText().toString().trim();
			mNewUserBean.truckType = mEditModel.getText().toString().trim();
			try {
				mNewUserBean.truckLength = Double.parseDouble(mEditLength
						.getText().toString());
				mNewUserBean.truckWeight = Double.parseDouble(mEidtLoad
						.getText().toString());
			} catch (Exception e) {
				ErrLogUtils.uploadErrLog(this, ErrLogUtils.toString(e));
				finish();
			}

		}

		// // 去掉重复的图片
		// Set<String> set = new HashSet<String>();
		// set.addAll(Bimp.drr);
		//
		// for (String path : set) {
		// path = path.substring(path.lastIndexOf("/") + 1,
		// path.lastIndexOf("."));
		// path = UtilsAndroid.Sdcard.SDPATH_FORMATS + path + ".JPEG";
		// File file = new File(path);
		// System.out.println("ipath:" + path);
		// if (file.exists()) {
		// imgList.add(path);
		// }
		// }
	}

	/**
	 * 更新资料编辑
	 */
	protected void updateInfoData() {
		getUserData();
		// 添加要上传的图片
		// addUploadImages();

		// 判断用户是否修改了资料
		if (mNewUserBean.equals(mOldUserBean) && Bimp.imgPath.size() == 0) {
			showLongToast(getString(R.string.eidt_info_tip_msg));
			return;
		}
		// 设置用户操作的动作码
		PreferencesUtil.setSteps(Constants.Step.SAVE);
		mNewUserBean.imgList = Bimp.getUploadImg();
		// 提交编辑请求
		// AsyncHttpService.modifyUserInfo(mNewUserBean, imgList,
		AsyncHttpService.modifyUserInfo(mNewUserBean,
				new JsonHttpResponseHandler() {

					@Override
					public void onStart() {
						showLoadDialog((R.string.is_submitted_ellipsis));
						super.onStart();
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						showLongToast(getString(R.string.httpisNull));
						mDialogLoading.dismiss();
					}

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						Log.d(TAG, "info: " + response.toString());
						dismissLoadDialog();
						super.onSuccess(statusCode, headers, response);
						try {
							if (UtilsError.isErrorCode(EditInfoActivity.this,
									response)) {
								return;
							}

							PreferencesUtil.putValue(
									PreferencesUtil.KEY_USER_NAME,
									mNewUserBean.userName);
							showLongToast(R.string.data_editor_success);
							thisFinish();

						} catch (Exception e) {
							dismissLoadDialog();
							e.printStackTrace();
							ErrLogUtils.uploadErrLog(EditInfoActivity.this,
									ErrLogUtils.toString(e));
						}
					}

				}, this);
	}

	// /**
	// * 添加用户需要上传的图片
	// */
	// private void addUploadImages() {
	// imgList.clear();
	// // 去掉重复的图片
	//
	// for (ImageItem imageItem : Bimp.imgPath) {
	// String path = imageItem.imagePath;
	// File file = new File(path);
	// if (file.exists()) {
	// imgList.add(path);
	// }
	// // path = path.substring(path.lastIndexOf("/") + 1,
	// // path.lastIndexOf("."));
	// // path = ImageLoader.IMAGE_CACHE_PATH + path + ".JPEG";
	// // File file = new File(path);
	// // Log.i(TAG, "ipath:" + path);
	// // if (file.exists()) {
	// // imgList.add(path);
	// // }
	// }
	// }

	private void thisFinish() {
		Bimp.clearCache();
		setResult(RESUULT_CODE_EDIT_INFO);
		finish();
	}

	@Override
	public void onDestroy() {
		Bimp.clearCache();
		super.onDestroy();
		// ActivityStackManager.getStackManager().popActivity(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CHOOSE_MODEL) {
			// 获取所选车型
			mEditModel.setText(data.getStringExtra(EXTRA_MODEL));
		} else if (resultCode == RESULT_CHOOSE_LENGTH) {
			// 获取所选车长
			mEditLength.setText(data.getStringExtra(EXTRA_LENGTH));
		} else if (resultCode == RESULT_CHOOSE_LOAD) {
			// 获取所选车载重
			mEidtLoad.setText(data.getStringExtra(EXTRA_LOAD));
		} else {
			// Bimp.loading();
			adapter.notifyDataSetChanged();
		}
	}

	private SparseArray<String> mOpreateSparse = new SparseArray<String>();

	private void initOperateStep() {
		mOpreateSparse.put(R.id.activity_edit_info_bt_info_save,
				getString(R.string.save));
		mOpreateSparse.put(R.id.activity_edit_info_lay_choose_model,
				getString(R.string.select_vehicle_model));
		mOpreateSparse.put(R.id.activity_edit_info_lay_choose_length,
				getString(R.string.select_vehicle_length));
		mOpreateSparse.put(R.id.activity_edit_info_lay_choose_load,
				getString(R.string.select_vehicle_load));
	}

	@Override
	public void onClick(View view) {
		String step = mOpreateSparse.get(view.getId());
		if (!TextUtils.isEmpty(step)) {
			saveOperateLog(step, null);
		}
		switch (view.getId()) {
		case R.id.activity_edit_info_bt_info_save: // 点击保存
			updateInfoData();
			break;
		case R.id.activity_edit_info_lay_choose_model: // 选择车型
			// ActivityChooseModel.isChooseAll = false;
			Intent model = new Intent(EditInfoActivity.this,
					ChooseModelActivity.class);
			startActivityForResult(model, REQUEST_CHOOSE_MODEL);
			break;
		case R.id.activity_edit_info_lay_choose_length: // 选择车长
			// ActivityChooseModel.isChooseAll = false;
			Intent length = new Intent(EditInfoActivity.this,
					ChooseLengthActivity.class);
			startActivityForResult(length, REQUEST_CHOOSE_LENGTH);
			break;
		case R.id.activity_edit_info_lay_choose_load: // 选择载重
			// ActivityChooseModel.isChooseAll = false;
			Intent weight = new Intent(EditInfoActivity.this,
					ChooseWeightActivity.class);
			startActivityForResult(weight, REQUEST_CHOOSE_LOAD);
			break;
		// case R.id.activity_edit_info_iv_go_back:// 返回
		case R.id.common_view_title_img:// 返回
			finish();
			break;
		default:
			break;
		}

	}

	public static final int REQUEST_CODE_EDIT_INFO = 11;
	public static final int RESUULT_CODE_EDIT_INFO = 21;
}
