//package com.sdbnet.hywy.employee.ui.view;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.View.OnClickListener;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.TextView;
//import com.sdbnet.hywy.employee.R;
//import com.sdbnet.hywy.employee.model.ExecuteAction;
//import com.sdbnet.hywy.employee.ui.CaptureActivity;
//import com.sdbnet.hywy.employee.ui.base.BaseFrament;
//import com.sdbnet.hywy.employee.utils.Constants;
//import com.sdbnet.hywy.employee.utils.ErrLogUtils;
//import com.sdbnet.hywy.employee.utils.LogUtil;
//import com.sdbnet.hywy.employee.utils.PreferencesUtil;
//import com.sdbnet.hywy.employee.utils.UtilsAndroid;
//import com.sdbnet.hywy.employee.utils.UtilsBean;
//
//public class OrderActionFragment extends BaseFrament {
//	private static final String TAG = "OrderActionFragment";
//	// private LinearLayout lay_base_action;
//	// private LinearLayout lay_sub_action;
//	private static final int LEVEL_FIRST = 1;
//	private static final int LEVEL_SECOND = 2;
//	private static final int LEVEL_THIRD = 3;
//	private int mCurrentLevel = LEVEL_FIRST;
//
//	private ImageView mImgBack;
//	private List<ExecuteAction> parentActions = new ArrayList<ExecuteAction>(); // 保存一级扫描按钮
//	private List<ExecuteAction> childActions = new ArrayList<ExecuteAction>(); // 保存子级扫描按钮
//	private List<ExecuteAction> childSubActions = new ArrayList<ExecuteAction>();// 保存子级的子级
//	private String subLevel = "00";
//	private String parentLevel;
//
//	private ListView mListView;
//	private List<ExecuteAction> mCurrentExecuteAction = new ArrayList<ExecuteAction>();
//	private TextView mTextTitle;
//	private String returnText;
//	private BaseAdapter mListAdapter;
//	private View orderView;
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		orderView = inflater.inflate(R.layout.activity_action_base, null);
//		initControls();
//		return orderView;
//	}
//
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		initBaseData();
//		// TODO Auto-generated method stub
//		super.onActivityCreated(savedInstanceState);
//		UtilsAndroid.Set.openWifi(getActivity());
//		// UtilsAndroid.Set.openMobileNetWork(this);
//		UtilsAndroid.Set.openGPSSettings(getActivity());
//	}
//
//	private void initBaseData() {
//		// TODO Auto-generated method stub
//		// 获取扫描权限
//		String execActions = PreferencesUtil.getValue(
//				PreferencesUtil.KEY_EXECUTE_ACTION, null);
//		LogUtil.d(TAG, "execActions=" + execActions);
//		if (!TextUtils.isEmpty(execActions)) {
//			try {
//				JSONArray array = new JSONArray(execActions);
//
//				// 解析扫描按钮
//				for (int i = 0; i < array.length(); i++) {
//					JSONObject jsonObj = array.getJSONObject(i);
//					final ExecuteAction action = UtilsBean
//							.jsonToExecuteAction(jsonObj);
//					// LogUtil.d(TAG, "ExecuteAction=" + action.toString());
//					if (action.getAction().length() == 2) {
//						// 保存到父动作集合中
//						parentActions.add(action);
//					} else if (action.getAction().length() == 4) {
//						// 保存到子动作集合中
//						childActions.add(action);
//					} else {
//						childSubActions.add(action);
//					}
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//				ErrLogUtils
//						.uploadErrLog(getActivity(), ErrLogUtils.toString(e));
//			}
//		}
//		mCurrentExecuteAction = parentActions;
//
//	}
//
//	/**
//	 * 初始化控件
//	 */
//	private void initControls() {
//		mImgBack = (ImageView) orderView
//				.findViewById(R.id.activity_action_base_iv_go_back);
//		mImgBack.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				onBackPressed();
//			}
//		});
//
//		returnText = String.format(Constants.Value.SCAN_ORDER,
//				PreferencesUtil.ordtitle);
//		mTextTitle = (TextView) orderView
//				.findViewById(R.id.activity_action_base_textview_details_title);
//		mTextTitle.setText(returnText);
//
//		mListView = (ListView) orderView
//				.findViewById(R.id.activity_action_base_list);
//		mListView.setAdapter(mListAdapter);
//		UtilsAndroid.UI.setListViewHeightBasedOnChildren(mListView);
//		mListAdapter.notifyDataSetChanged();
//	}
//
//	private void getSecondData(String action) {
//		mCurrentExecuteAction = new ArrayList<ExecuteAction>();
//		;
//		for (ExecuteAction sub : childActions) {
//			if (sub.getAction().startsWith(action)
//					&& sub.getAction().length() == action.length() + 2) { // 从子动作集合中取得当前动作的子动作
//				mCurrentExecuteAction.add(sub);
//				subLevel = sub.getAction(); // 子动作对应的级别
//			}
//		}
//	}
//
//	private void initListView() {
//		mListAdapter = new BaseAdapter() {
//
//			@Override
//			public View getView(int position, View convertView, ViewGroup parent) {
//				// TODO Auto-generated method stub
//				Button button = new Button(getActivity());
//				final ExecuteAction action = (ExecuteAction) getItem(position);
//				button.setText(action.getBtnname());
//				button.setTextColor(getResources().getColor(R.color.white));
//				button.setBackgroundResource(R.drawable.back_submit_style);
//				int pad = UtilsAndroid.UI.dip2px(getActivity(), 15);
//				button.setPadding(pad, pad, pad, pad);
//				button.setTag(action);
//				button.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						String viewAction = action.getAction();
//						saveOpSteps(action);
//						switch (mCurrentLevel) {
//						case LEVEL_FIRST:
//							parentLevel = viewAction;
//							getSecondData(viewAction);
//							UtilsAndroid.UI
//									.setListViewHeightBasedOnChildren(mListView);
//							mListAdapter.notifyDataSetChanged();
//							mCurrentLevel = LEVEL_SECOND;
//							mTextTitle.setText(action.getBtnname());
//							break;
//						case LEVEL_SECOND:
//							subLevel = viewAction;
//							if (childSubActions.get(0).getAction()
//									.startsWith(viewAction)) {
//								mCurrentExecuteAction = childSubActions;
//								UtilsAndroid.UI
//										.setListViewHeightBasedOnChildren(mListView);
//								mListAdapter.notifyDataSetChanged();
//								mCurrentLevel = LEVEL_THIRD;
//							} else {
//								executeAction(v);
//							}
//
//							break;
//						case LEVEL_THIRD:
//							executeAction(v);
//							break;
//						default:
//							break;
//						}
//
//					}
//				});
//				return button;
//			}
//
//			@Override
//			public long getItemId(int position) {
//				// TODO Auto-generated method stub
//				return position;
//			}
//
//			@Override
//			public Object getItem(int position) {
//				// TODO Auto-generated method stub
//				if (mCurrentExecuteAction == null) {
//					return null;
//				} else {
//					try {
//						return mCurrentExecuteAction.get(position);
//					} catch (Exception e) {
//						ErrLogUtils.uploadErrLog(getActivity(),
//								ErrLogUtils.toString(e));
//					}
//					return null;
//				}
//			}
//
//			@Override
//			public int getCount() {
//				if (mCurrentExecuteAction == null) {
//					return 0;
//				} else {
//					return mCurrentExecuteAction.size();
//				}
//			}
//		};
//
//	}
//
//	/**
//	 * 执行扫描动作
//	 *
//	 * @param view
//	 */
//	public void executeAction(View view) {
//		Intent intent = new Intent(getActivity(), CaptureActivity.class);
//		Bundle bundle = new Bundle();
//		bundle.putSerializable(Constants.Feild.KEY_ACTION,
//				(ExecuteAction) view.getTag());
//		intent.putExtras(bundle);
//		startActivity(intent);
//	}
//
////	@Override
//	public void onBackPressed() {
//		System.out.println("subLevel:" + subLevel + ":mCurrentLevel="
//				+ mCurrentLevel);
//		if (mCurrentLevel <= LEVEL_FIRST) {
//			// 如果当前页显示的是一级按钮，直接返回
////			super.onBackPressed();
//		} else {
//			recoveryOpSteps();
//			mCurrentLevel--;
//			if (mCurrentLevel == LEVEL_SECOND) {
//				getSecondData(parentLevel);
//			} else if (mCurrentLevel == LEVEL_FIRST) {
//				mCurrentExecuteAction = parentActions;
//			}
//			UtilsAndroid.UI.setListViewHeightBasedOnChildren(mListView);
//			mListAdapter.notifyDataSetChanged();
//		}
//
//	}
//
//	/**
//	 * 保存用户执行的动作码
//	 *
//	 * @param tempAction
//	 */
//	private void saveOpSteps(ExecuteAction tempAction) {
//		String steps = PreferencesUtil.getValue(PreferencesUtil.KEY_STEPS,
//				"");
//		if (TextUtils.isEmpty(steps)) {
//			steps += tempAction.getAction();
//		} else {
//			steps += "," + tempAction.getAction();
//		}
//		PreferencesUtil.putValue(PreferencesUtil.KEY_STEPS, steps);
//	}
//
//	private void recoveryOpSteps() {
//		String steps = PreferencesUtil.getValue(PreferencesUtil.KEY_STEPS,
//				"");
//		int index = steps.lastIndexOf(",");
//		if (!TextUtils.isEmpty(steps) && index > 0) {
//			steps = steps.substring(0, index);
//		} else if (index < 0) {
//			steps = "";
//		}
//		PreferencesUtil.putValue(PreferencesUtil.KEY_STEPS, steps);
//	}
//}
