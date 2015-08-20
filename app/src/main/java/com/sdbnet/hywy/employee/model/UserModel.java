package com.sdbnet.hywy.employee.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.sdbnet.hywy.employee.album.AlbumHelper.ImageItem;

public class UserModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String userName;
	public String locTel;

	public String sex;
	public String truckNum;
	public String truckType;
	public double truckLength;
	public double truckWeight;
	public ArrayList<ImageItem> imgList;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((locTel == null) ? 0 : locTel.hashCode());
		result = prime * result + ((sex == null) ? 0 : sex.hashCode());
		long temp;
		temp = Double.doubleToLongBits(truckLength);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((truckNum == null) ? 0 : truckNum.hashCode());
		result = prime * result
				+ ((truckType == null) ? 0 : truckType.hashCode());
		temp = Double.doubleToLongBits(truckWeight);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserModel other = (UserModel) obj;
		if (locTel == null) {
			if (other.locTel != null)
				return false;
		} else if (!locTel.equals(other.locTel))
			return false;
		if (sex == null) {
			if (other.sex != null)
				return false;
		} else if (!sex.equals(other.sex))
			return false;
		if (Double.doubleToLongBits(truckLength) != Double
				.doubleToLongBits(other.truckLength))
			return false;
		if (truckNum == null) {
			if (other.truckNum != null)
				return false;
		} else if (!truckNum.equals(other.truckNum))
			return false;
		if (truckType == null) {
			if (other.truckType != null)
				return false;
		} else if (!truckType.equals(other.truckType))
			return false;
		if (Double.doubleToLongBits(truckWeight) != Double
				.doubleToLongBits(other.truckWeight))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

}
