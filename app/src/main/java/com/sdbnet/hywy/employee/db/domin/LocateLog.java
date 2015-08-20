package com.sdbnet.hywy.employee.db.domin;

import java.io.Serializable;

public class LocateLog implements Serializable {
	/**
	 * LocateLog
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private String cmpid;
	private String itemid;
	private String pid;
	private String loctel;
	private double longitude;
	private double latitude;
	private String address;
	private String loctime;
	private Integer isworking;
	private Integer gpsstatus;
	private Integer gprsstatus;
	private Integer wifistatus;
	private Integer electricity;

	public String getCmpid() {
		return cmpid;
	}

	public void setCmpid(String cmpid) {
		this.cmpid = cmpid;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getLoctel() {
		return loctel;
	}

	public void setLoctel(String loctel) {
		this.loctel = loctel;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLoctime() {
		return loctime;
	}

	public void setLoctime(String loctime) {
		this.loctime = loctime;
	}

	public Integer getIsworking() {
		return isworking;
	}

	public void setIsworking(Integer isworking) {
		this.isworking = isworking;
	}

	public Integer getGpsstatus() {
		return gpsstatus;
	}

	public void setGpsstatus(Integer gpsstatus) {
		this.gpsstatus = gpsstatus;
	}

	public Integer getGprsstatus() {
		return gprsstatus;
	}

	public void setGprsstatus(Integer gprsstatus) {
		this.gprsstatus = gprsstatus;
	}

	public Integer getWifistatus() {
		return wifistatus;
	}

	public void setWifistatus(Integer wifistatus) {
		this.wifistatus = wifistatus;
	}

	public Integer getElectricity() {
		return electricity;
	}

	public void setElectricity(Integer electricity) {
		this.electricity = electricity;
	}

	@Override
	public String toString() {
		return "{'cmpid':'" + cmpid + "', 'itemid':'" + itemid + "', 'pid':'"
				+ pid + "', 'loctel':'" + loctel + "', 'longitude':'"
				+ longitude + "', 'latitude':'" + latitude + "', 'address':'"
				+ address + "', 'loctime':'" + loctime + "', 'isworking':'"
				+ isworking + "', 'gpsstatus':'" + gpsstatus
				+ "', 'gprsstatus':'" + gprsstatus + "', 'wifistatus':'"
				+ wifistatus + "', 'electricity':'" + electricity + "'}";
	}

	public LocateLog() {
		super();
	}

	public LocateLog(double longitude,double latitude) {
		this.longitude=longitude;
		this.latitude=latitude;
	}

	public LocateLog(String cmpid, String itemid, String pid, String loctel,
			float longitude, float latitude, String address, String loctime,
			int isworking, int gpsstatus, int gprsstatus, int wifistatus,
			int electricity) {
		super();
		this.cmpid = cmpid;
		this.itemid = itemid;
		this.pid = pid;
		this.loctel = loctel;
		this.longitude = longitude;
		this.latitude = latitude;
		this.address = address;
		this.loctime = loctime;
		this.isworking = isworking;
		this.gpsstatus = gpsstatus;
		this.gprsstatus = gprsstatus;
		this.wifistatus = wifistatus;
		this.electricity = electricity;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
