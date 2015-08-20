package com.sdbnet.hywy.employee.location;

import android.location.GpsStatus;
import android.location.GpsStatus.Listener;

public class GpsSatelliteListener implements Listener {
	private final String TAG = getClass().getSimpleName();

	@Override
	public void onGpsStatusChanged(int event) {
		// Log.v(TAG, "event=" + event);
		switch (event) {

		case GpsStatus.GPS_EVENT_FIRST_FIX:
			break;

		case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
			break;

		case GpsStatus.GPS_EVENT_STARTED:
			break;

		case GpsStatus.GPS_EVENT_STOPPED:
			break;
		}
	}

}
