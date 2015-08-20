package com.sdbnet.hywy.employee.ui;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.album.Bimp;
import com.sdbnet.hywy.employee.album.ImageGridShowAdapter;
import com.sdbnet.hywy.employee.location.MyLocation;
import com.sdbnet.hywy.employee.model.ReportModel;
import com.sdbnet.hywy.employee.net.AsyncHttpService;
import com.sdbnet.hywy.employee.service.TimeUploadService.IReceiveLocationHandler;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;
import com.sdbnet.hywy.employee.ui.widget.CustomGridView;
import com.sdbnet.hywy.employee.ui.widget.DialogDateTimePick;
import com.sdbnet.hywy.employee.utils.LogUtil;
import com.sdbnet.hywy.employee.utils.ToastUtil;
import com.sdbnet.hywy.employee.utils.UtilsError;
import com.sdbnet.hywy.employee.utils.UtilsJava;

public class AccidentAddActivity extends BaseActivity {
	private static final String TAG = "ReportAddActivity";

	private ImageView mImgBack;
	private TextView mTextTitle;

	private TextView mTextDate;
	private EditText mEditPlace;
	private EditText mEditExplain;
	private CustomGridView mGridPic;
	private Button mBtnSubmit;
	private ImageGridShowAdapter mGridAdapter;
	private ReportModel mReportModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActivityStackManager.getStackManager().pushActivity(this);
		setContentView(R.layout.activity_report_add);
		initBaseData();
		initUI();
		// initListView();
		initService();
	}

	private void initBaseData() {
		mReportModel = new ReportModel();
		ArrayList<String> mOrders = getIntent().getStringArrayListExtra(
				CaptureActivity.EXTRA_ORDER_LIST);
		if (mOrders != null && mOrders.size() > 0) {
			for (String order : mOrders) {
				mReportModel.orders += order + ",";
			}
		}
		mReportModel.orders = mReportModel.orders.substring(0,
				mReportModel.orders.length() - 1);
	}

	private void initUI() {

		mTextDate = (TextView) findViewById(R.id.activity_report_add__date_text);
		mEditPlace = (EditText) findViewById(R.id.activity_report_add_place_text);
		mEditExplain = (EditText) findViewById(R.id.activity_report_add__explain_msg_edit);
		mGridPic = (CustomGridView) findViewById(R.id.activity_report_add_pic_cgrid);
		mBtnSubmit = (Button) findViewById(R.id.activity_report_add_submit_btn);

		// 初始化GridImage适配器
		mGridAdapter = new ImageGridShowAdapter(this, null, true);
		mGridAdapter.setShowEvery(true);
		mGridPic.setAdapter(mGridAdapter);
		// Bimp.IMAGE_COUNT=5;

		mTextTitle = (TextView) findViewById(R.id.common_view_title_text);
		mTextTitle.setText(R.string.accident_report);
		mImgBack = (ImageView) findViewById(R.id.common_view_title_img);
		mImgBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTextDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDateDialog(mTextDate);
			}
		});

		mBtnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveOperateLog(mBtnSubmit.getText().toString());
				submitReport();
			}
		});
		mTextDate.setText(UtilsJava.getCurrentlyTime());
	}

	// @Override
	// protected void onResume() {
	// super.onResume();
	// getPlaceData();
	// }
	//
	// @Override
	// protected void onStart() {
	// super.onStart();
	// getPlaceData();
	// }
	private void initService() {
		setOnBindServiceListener(new IBindServiceBack() {

			@Override
			public void onBindeFailed() {
				ToastUtil.show(AccidentAddActivity.this, "bind service failed");
			}

			@Override
			public void onBindSuccess() {
				getPlaceData();
			}
		});

	}

	private void getPlaceData() {

		if (mLocateService != null) {
			mLocateService.requestLocation(new IReceiveLocationHandler() {

				@Override
				public void onReceiveHandler(MyLocation location) {
					mReportModel.place = location.address;
					if (mEditPlace != null) {
						mEditPlace.setText(location.address);
					}
					ToastUtil.show(AccidentAddActivity.this, "Address="
							+ location.address);
					if (mLocateService != null) {
						mLocateService.stopReceiveLocation();
					}
				}

				@Override
				public void onReceiveFail() {
					if (mLocateService != null) {
						mLocateService.stopReceiveLocation();
					}
					ToastUtil.show(AccidentAddActivity.this,
							"Get location failed");
					// getPlaceData();
				}
			});
		} else {
			ToastUtil.show(this, "mLocateService==null");
		}
	}

	// /**
	// * 添加用户需要上传的图片
	// */
	// private void addUploadImages() {
	// // mReportModel.imgList = new ArrayList<String>();
	// // // mReportModel.imgList.clear();
	// // // 去掉重复的图片
	// //
	// //
	// // for (ImageItem imageItem : Bimp.imgPath) {
	// // String path = imageItem.imagePath;
	// // File file = new File(path);
	// // if (file.exists()) {
	// // mReportModel.imgList.add(path);
	// // // mReportModel.imgs += path + ",";
	// // }
	// // }
	// mReportModel.imgList = new ArrayList<AlbumHelper.ImageItem>();
	// // 去掉重复的图片
	//
	// for (ImageItem imageItem : Bimp.imgPath) {
	// String path = imageItem.imagePath;
	// File file = new File(path);
	// if (file.exists() && !mReportModel.imgList.contains(imageItem)) {
	// mReportModel.imgList.add(imageItem);
	// }
	// }
	// }

	private boolean checkReport() {
		// mReportModel.title = mTextTitle.getText().toString();
		mReportModel.date = mTextDate.getText().toString();
		mReportModel.explain = mEditExplain.getText().toString();
		mReportModel.place = mEditPlace.getText().toString();
		// mReportModel.theme = mEditTitle.getText().toString();
		mReportModel.imgList = Bimp.getUploadImg();
		// addUploadImages();
		// if (TextUtils.isEmpty(mReportModel.theme)) {
		// showShortToast(R.string.please_input_title);
		// return false;
		// }
		if (TextUtils.isEmpty(mReportModel.date)) {
			showShortToast(R.string.please_select_date);
			return false;
		}
		if (TextUtils.isEmpty(mReportModel.explain)) {
			showShortToast(R.string.please_explain_msg);
			return false;
		}
		return true;
	}

	private void submitReport() {

		if (checkReport()) {
			AsyncHttpService.uploadReportExection(mReportModel,
					new JsonHttpResponseHandler() {
						@Override
						public void onStart() {
							showLoadDialog(getString(R.string.is_submitted_ellipsis));
							super.onStart();
						}

						@Override
						public void onProgress(int bytesWritten, int totalSize) {
							super.onProgress(bytesWritten, totalSize);
							Log.i("onProgress", "written=" + bytesWritten
									+ ",totalSize=" + totalSize);
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							dismissLoadDialog();
							// errcode 1 数字 错误编号
							// msg 1 字符串 错误消息
							LogUtil.d(response.toString());
							super.onSuccess(statusCode, headers, response);
							try {
								if (UtilsError.isErrorCode(
										AccidentAddActivity.this, response)) {
									return;
								}
								showShortToast(R.string.accident_report_upload_success);
								// 先上传数据，然后再返回
								returnReport();
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								dismissLoadDialog();
							}

						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONObject errorResponse) {
							super.onFailure(statusCode, headers, throwable,
									errorResponse);
							dismissLoadDialog();
							showShortToast(R.string.accident_report_upload_failed);
						}

						@Override
						public void onCancel() {
							dismissLoadDialog();
							super.onCancel();

						}
					}, this);

		}
	}

	// success
	private void returnReport() {
		// ReportModel test = new ReportModel();
		Bimp.clearCache();
		Intent intent = new Intent();
		// intent.putExtra(Re.EXTRA_UPLOAD_REPORT, mReportModel);
		setResult(CaptureActivity.RESULT_CODE_ADD_REPORT, intent);
		finish();
	}

	private String getItemTitle(String date) {
		return date.replace("-", "") + "情况反馈";
	}

	private void showDateDialog(TextView editText) {
		DialogDateTimePick dateTimePicKDialog = new DialogDateTimePick(this,
				editText.getText().toString().trim());
		dateTimePicKDialog.dateTimePicKDialog(editText);
	}

	@Override
	public void onDestroy() {
		Bimp.clearCache();
		super.onDestroy();
		// ActivityStackManager.getStackManager().popActivity(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, "data=" + data);
		// Bimp.loading();
		mGridAdapter.notifyDataSetChanged();

	}

}
