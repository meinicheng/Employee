package com.sdbnet.hywy.employee.location;

import java.io.Serializable;

public class MyLocation  implements Serializable{
	public static final String LOCATION_TYPE_GPS="locatin_type_gps";
	public static final String LOCATION_TYPE_AMAP="locatin_type_amap";
	public static final String LOCATION_TYPE_BD="locatin_type_bd";
	public static final int ERROR_CODE_OK=0;
	public static final int ERROR_CODE_NO_NETWORK=10;
	public static final int ERROR_CODE_NO_ADDRESS=11;
	public MyLocation() {

	}

	public MyLocation(double longitude, double latitude, String address) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
	}
	public MyLocation(double longitude, double latitude, String address,String locType) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
		this.locType=locType;
	}
	public MyLocation(double longitude, double latitude, String address,
			long time) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
		this.time = time;
	}

	public String locType;
	public double latitude;
	public double longitude;
	public String address;
	public String city;
	public long time;
	public int errorCode;

	
	@Override
	public String toString() {
		return "MyLocation [locType=" + locType + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", address=" + address
				+ ", city=" + city + ", time=" + time + ", errorCode="
				+ errorCode + "]";
	}

}
