package com.sdbnet.hywy.employee.utils;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sdbnet.hywy.employee.album.AlbumHelper;
import com.sdbnet.hywy.employee.album.AlbumHelper.ImageItem;
import com.sdbnet.hywy.employee.model.CompanyModel;
import com.sdbnet.hywy.employee.model.ExecuteAction;
import com.sdbnet.hywy.employee.model.UserModel;

public class UtilsBean {
	private static final String TAG = "UtilsBean";

	public static UserModel jsonToUserBean(JSONObject jsonStaff) {
		UserModel userBean;
		try {
			userBean = new UserModel();
			userBean.userName = jsonStaff
					.getString(Constants.Feild.KEY_STAFF_NAME);
			userBean.locTel = jsonStaff.getString(Constants.Feild.KEY_LOCA_TEL);
			userBean.sex = jsonStaff.getString(Constants.Feild.KEY_STAFF_SEX);
			userBean.truckNum = jsonStaff
					.getString(Constants.Feild.KEY_STAFF_TRUCK_NO);
			userBean.truckType = jsonStaff
					.getString(Constants.Feild.KEY_STAFF_TRUCK_TYPE);
			userBean.truckLength = jsonStaff
					.getDouble(Constants.Feild.KEY_STAFF_TRUCK_LENGTH);
			userBean.truckWeight = jsonStaff
					.getDouble(Constants.Feild.KEY_STAFF_TRUCK_WEIGHT);

			ArrayList<ImageItem> imgList = new ArrayList<ImageItem>();
			JSONArray array = jsonStaff.getJSONArray(Constants.Feild.KEY_IMGS);
			for (int i = 0; i < array.length(); i++) {
				JSONObject jsonImg = array.getJSONObject(i);
				ImageItem imgItem = new AlbumHelper.ImageItem();
				imgItem.imagePath = (jsonImg
						.getString(Constants.Feild.KEY_PIC_BIG));
				imgItem.thumbnailPath = (jsonImg
						.getString(Constants.Feild.KEY_PIC_SMALL));
				imgList.add(imgItem);
			}
			userBean.imgList = imgList;
			return userBean;
		} catch (JSONException e) {
			// e.printStackTrace();
			return null;
		}

	}

	public static UserModel jsonToUserBean(String jsonData) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsonData);
			return jsonToUserBean(jsonObject);
		} catch (JSONException e) {
			// e.printStackTrace();
			// return ErrLogUtils.toString(e);
			return null;
		}
	}

	public static CompanyModel jsonToCmpBean(JSONObject jsonComp) {
		CompanyModel cmpBean;
		try {

			cmpBean = new CompanyModel();
			cmpBean.cmpAddress = jsonComp
					.getString(Constants.Feild.KEY_COMPANY_NAME);
			cmpBean.linkman = jsonComp
					.getString(Constants.Feild.KEY_COMPANY_LINKMAN);
			cmpBean.telephone1 = jsonComp
					.getString(Constants.Feild.KEY_COMPANY_TEL_1);
			cmpBean.telephone2 = jsonComp
					.getString(Constants.Feild.KEY_COMPANY_TEL_2);
			cmpBean.cmpAddress = jsonComp
					.getString(Constants.Feild.KEY_COMPANY_ADDRESS);
			cmpBean.logo = jsonComp.getString(Constants.Feild.KEY_COMPANY_LOGO);
			cmpBean.remark = jsonComp
					.getString(Constants.Feild.KEY_COMPANY_REMARK);
			cmpBean.email = jsonComp
					.getString(Constants.Feild.KEY_COMPANY_EMAIL);
			cmpBean.url = jsonComp.getString(Constants.Feild.KEY_COMPANY_URL);
			cmpBean.itemName = jsonComp
					.getString(Constants.Feild.KEY_ITEM_NAME);
			return cmpBean;
		} catch (JSONException e) {
			// e.printStackTrace();
			return null;
		}

	}

	public static CompanyModel jsonToCmpBean(String jsonData) {
		try {
			JSONObject jsonComp = new JSONObject(jsonData);

			return jsonToCmpBean(jsonComp);
		} catch (JSONException e) {
			// e.printStackTrace();
			// return ErrLogUtils.toString(e);
			return null;
		}
	}

	public static ExecuteAction jsonToExecuteAction(JSONObject jsonObj) {
		ExecuteAction action = new ExecuteAction();

		try {
			action.setActidx(jsonObj.getString(Constants.Feild.KEY_ACTION_ID));
			action.setAction(jsonObj.getString(Constants.Feild.KEY_ACTION));
			action.setActmemo(jsonObj
					.getString(Constants.Feild.KEY_ACTION_CONTENT));
			action.setActname(jsonObj
					.getString(Constants.Feild.KEY_ACTION_NAME));
			action.setLinename(jsonObj.getString(Constants.Feild.KEY_LINE_NAME));
			action.setLineno(jsonObj.getString(Constants.Feild.KEY_LINE_ID));
			action.setSign(jsonObj.getString(Constants.Feild.KEY_SIGN));
			if (!jsonObj.isNull(Constants.Feild.KEY_THIRD_PARTY)) {
				action.setActype(jsonObj
						.getString(Constants.Feild.KEY_THIRD_PARTY));
			}

			if (!jsonObj.isNull(Constants.Feild.KEY_ACTION_BTN)) {
				action.setBtnname(jsonObj
						.getString(Constants.Feild.KEY_ACTION_BTN));
			}

			action.setWorkflow(jsonObj.getString(Constants.Feild.KEY_WORKFLOW));
			action.setStartnode(jsonObj
					.getString(Constants.Feild.KEY_START_NODE));

			action.setIscall(jsonObj.getString(Constants.Feild.KEY_ACTION_CALL));
			action.setIslocate(jsonObj
					.getString(Constants.Feild.KEY_ACTION_LOCAT));
			action.setIsscan(jsonObj
					.getString(Constants.Feild.KEY_ACTION_IS_SCAN));
			action.setShowinner(jsonObj
					.getString(Constants.Feild.KEY_SHOWINNER));

			if (!jsonObj.isNull(Constants.Feild.KEY_ACTMEMOINNER)) {
				action.setActmemoinner(jsonObj
						.getString(Constants.Feild.KEY_ACTMEMOINNER));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return action;

	}

	public static ExecuteAction jsonToExecuteAction(String jsonData) {
		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			return jsonToExecuteAction(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将订单执行的相关信息转换为json字符串，以便保存到草稿箱
	 * 
	 * @param action
	 * @param imgList
	 * @return
	 */
	// public static String buildeParams(ExecuteAction action, List<String>
	// imgList) {
	// String imgString = imgList.toString();
	// imgString = imgString.replace("[", "");
	// imgString = imgString.replace("]", "");
	// action.setImsgs(imgString);
	// return buildeParams(action);
	// }

	public static String buildeParams(ExecuteAction action) {
		JSONObject jsonObject = new JSONObject();
		JSONObject actionJson = new JSONObject();
		try {
			actionJson.put(Constants.Feild.KEY_ACTION_ID, action.getActidx());
			actionJson.put(Constants.Feild.KEY_ACTION, action.getAction());
			actionJson.put(Constants.Feild.KEY_ACTION_CONTENT,
					action.getActmemo());
			actionJson
					.put(Constants.Feild.KEY_ACTION_NAME, action.getActname());
			actionJson.put(Constants.Feild.KEY_SIGN, action.getSign());
			actionJson.put(Constants.Feild.KEY_ACTION_CALL, action.getIscall());
			actionJson.put(Constants.Feild.KEY_ACTION_LOCAT,
					action.getIslocate());
			actionJson.put(Constants.Feild.KEY_ACTION_IS_SCAN,
					action.getIsscan());
			actionJson.put(Constants.Feild.KEY_LINE_NAME, action.getLinename());
			actionJson.put(Constants.Feild.KEY_LINE_ID, action.getLineno());
			actionJson.put(Constants.Feild.KEY_WORKFLOW, action.getWorkflow());
			actionJson.put(Constants.Feild.KEY_START_NODE,
					action.getStartnode());
			actionJson
					.put(Constants.Feild.KEY_SHOWINNER, action.getShowinner());
			actionJson.put(Constants.Feild.KEY_ACTMEMOINNER,
					action.getActmemoinner());
			jsonObject.put("action", actionJson);
			// jsonObject.put("imgs", action.getImsgs());
			JSONArray jsonArray = new JSONArray();
			if (action.getImageItems() != null)
				for (int i = 0; i < action.getImageItems().size(); i++) {
					JSONObject imgObject = new JSONObject();
					imgObject.put("imageId",
							action.getImageItems().get(i).imageId);
					imgObject.put("thumbnailPath", action.getImageItems()
							.get(i).thumbnailPath);
					imgObject.put("imagePath",
							action.getImageItems().get(i).imagePath);
					jsonArray.put(imgObject);
				}
			jsonObject.put("imgs", jsonArray);
		} catch (JSONException e) {

			e.printStackTrace();
		}
		Log.d(TAG, jsonObject.toString());
		return jsonObject.toString();
	}
}
