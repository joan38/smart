package fr.umlv.lastproject.smart;

import java.util.Date;
import java.util.List;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;

public class SmartLocationListener implements LocationListener {

	private final List<GpsListener> gpsListeners;

	/**
	 * 
	 * @param listeners
	 *            list of listeners
	 */
	public SmartLocationListener(List<GpsListener> listeners) {
		this.gpsListeners = listeners;
	}

	@Override
	public void onLocationChanged(Location location) {
		GpsEvent event = new GpsEvent(location.getLatitude(),
				location.getLongitude(), location.getAltitude(),
				location.getAccuracy(), location.getBearing(),
				location.getSpeed(), new Date(location.getTime()));

		for (GpsListener listener : gpsListeners) {
			listener.locationUpdated(event);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		for (GpsListener listener : gpsListeners) {
			listener.gpsUnavailable();
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		for (GpsListener listener : gpsListeners) {
			switch (status) {
			case LocationProvider.AVAILABLE:
				listener.gpsAvailable();
				break;

			case LocationProvider.TEMPORARILY_UNAVAILABLE:
			case LocationProvider.OUT_OF_SERVICE:
				listener.gpsUnavailable();
				break;
			}
		}
	}
}
