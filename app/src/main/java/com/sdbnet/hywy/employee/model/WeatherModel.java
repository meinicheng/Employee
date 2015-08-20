package com.sdbnet.hywy.employee.model;

import java.io.Serializable;

public class WeatherModel implements Serializable {
	public String city;
	public String temp;
	public String windDir;
	public String windPower;
	public String humidity;
	public String time;
	public String weather;

	@Override
	public String toString() {
		return "WeatherModel [city=" + city + ", temp=" + temp + ", wind="
				+ windDir + ", windPower=" + windPower + ", humidity="
				+ humidity + ", time=" + time + ", weather=" + weather + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result
				+ ((humidity == null) ? 0 : humidity.hashCode());
		result = prime * result + ((temp == null) ? 0 : temp.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((weather == null) ? 0 : weather.hashCode());
		result = prime * result + ((windDir == null) ? 0 : windDir.hashCode());
		result = prime * result
				+ ((windPower == null) ? 0 : windPower.hashCode());
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
		WeatherModel other = (WeatherModel) obj;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (humidity == null) {
			if (other.humidity != null)
				return false;
		} else if (!humidity.equals(other.humidity))
			return false;
		if (temp == null) {
			if (other.temp != null)
				return false;
		} else if (!temp.equals(other.temp))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (weather == null) {
			if (other.weather != null)
				return false;
		} else if (!weather.equals(other.weather))
			return false;
		if (windDir == null) {
			if (other.windDir != null)
				return false;
		} else if (!windDir.equals(other.windDir))
			return false;
		if (windPower == null) {
			if (other.windPower != null)
				return false;
		} else if (!windPower.equals(other.windPower))
			return false;
		return true;
	}

}
