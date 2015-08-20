package com.sdbnet.hywy.employee.model;

public class WeatherAreaModel
{
	private String areaid;
	private String areaname1;
	private String areaname2;
	private String areaname3;

	public WeatherAreaModel()
	{
		super();
	}

	public WeatherAreaModel(String areaid, String areaname3, String areaname2, String areaname1)
	{
		this.areaid = areaid;
		this.areaname1 = areaname1;
		this.areaname2 = areaname2;
		this.areaname3 = areaname3;
	}

	/**
	 * 地区ID
	 * 
	 * @return
	 */
	public String areaid()
	{
		return areaid;
	}

	public void Setareaid(String areaid)
	{
		this.areaid = areaid;
	}
	
	/**
	 * 一级城市名称
	 * 
	 * @return
	 */
	public String areaname1()
	{
		return areaname1;
	}

	public void Setareaname1(String areaname1)
	{
		this.areaname1 = areaname1;
	}
	
	/**
	 * 二级城市名称
	 * 
	 * @return
	 */
	public String areaname2()
	{
		return areaname2;
	}

	public void Setareaname2(String areaname2)
	{
		this.areaname2 = areaname2;
	}

	/**
	 * 三级城市名称
	 * 
	 * @return
	 */
	public String areaname3()
	{
		return areaname3;
	}

	public void Setareaname3(String areaname3)
	{
		this.areaname3 = areaname3;
	}
}
