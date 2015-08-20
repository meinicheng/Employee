package com.sdbnet.hywy.employee.model;

import java.io.Serializable;
import java.util.ArrayList;
import com.sdbnet.hywy.employee.album.AlbumHelper.ImageItem;

public class ReportModel implements Serializable {
	// public String title;
	// public String theme;
	public String date = "";
	public String place = "";
	public String explain = "";
	public String orders = "";
//	public ArrayList<String> imgList;
	public ArrayList<ImageItem> imgList;
	// public String imgs;
	// public String [] imgs=new String[4];
	// public ArrayList<String> imgList ;
	// public ArrayList<HashMap<String, Object>> imgList;
	// public ArrayList<Bitmap> thumpBitmp;
	@Override
	public String toString() {
		return "ReportModel [date=" + date + ", place=" + place + ", explain="
				+ explain + ", orders=" + orders + ", imgList=" + imgList + "]";
	}

}
