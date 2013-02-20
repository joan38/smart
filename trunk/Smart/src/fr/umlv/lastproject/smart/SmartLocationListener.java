package fr.umlv.lastproject.smart;

import java.util.Date;
import java.util.List;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class SmartLocationListener implements LocationListener {

	private final List<IGPSListener> gpsListeners;

	public SmartLocationListener( List<IGPSListener> listeners) {
		
		this.gpsListeners = listeners;
	}

	@Override
	public void onLocationChanged(Location location) {

		final double longitude = location.getLongitude();
		final double latitude = location.getLatitude();
		final double altitude = location.getAltitude();
		final float accuracy = location.getAccuracy();
		final float bearing = location.getBearing();
		final float speed = location.getSpeed();
		final Date time = new Date(location.getTime());

		for (int i = 0; i < gpsListeners.size(); i++) {
			gpsListeners.get(i).actionPerformed(
					new GPSEvent(latitude, longitude, altitude, accuracy,
							bearing, speed, time));
		}

	}

	@Override
	public void onProviderDisabled(String provider) {
		
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
		
	}



}
