package com.sdbnet.hywy.employee.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableCoordinate implements Parcelable {
	private double latitude;
	private double longitude;

	public ParcelableCoordinate(){
	}
	public ParcelableCoordinate(double latitude, double longitude){
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public static final Parcelable.Creator<ParcelableCoordinate> CREATOR = new Creator<ParcelableCoordinate>() {
		@Override
		public ParcelableCoordinate createFromParcel(Parcel source) {
			ParcelableCoordinate mCoordinate = new ParcelableCoordinate();
			mCoordinate.latitude = source.readDouble();
			mCoordinate.longitude = source.readDouble();
			return mCoordinate;
		}

		@Override
		public ParcelableCoordinate[] newArray(int size) {
			// TODO Auto-generated method stub
			return new ParcelableCoordinate[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
	}
}