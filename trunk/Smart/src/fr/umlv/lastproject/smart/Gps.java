package fr.umlv.lastproject.smart;

import java.util.ArrayList;
import java.util.List;

import android.location.Criteria;
import android.location.LocationManager;

/**
 * This class is used to listen to GPS events If GPS is not activated, user is
 * warned and invited to activate it
 * 
 * @author Marc
 * 
 */
public class Gps {

	private final LocationManager locationManager;
	private final Criteria criteria;
	private final List<GpsListener> gpsListeners;

	/**
	 * GPS Constructor
	 * 
	 * @param lm
	 *            : LocationManager of the GPS
	 */
	public Gps(LocationManager lm) {
		if (lm == null) {
			throw new IllegalArgumentException();
		}
		gpsListeners = new ArrayList<GpsListener>();
		this.locationManager = lm;
		this.criteria = new Criteria();
		this.criteria.setAltitudeRequired(false);
		this.criteria.setBearingRequired(true);
		this.criteria.setCostAllowed(true);
		this.criteria.setSpeedRequired(true);
	}

	/**
	 * Is location manager enabled
	 * 
	 * @param lm
	 *            : the location manager to test
	 * @return true if GPS is enable
	 */
	public boolean isEnabled() {
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	public LocationManager getLocationManager() {
		return locationManager;
	}

	/**
	 * Function which start the GPS Location Updates
	 * 
	 * Warning : Test if the gps isEnabled() first
	 * 
	 * @param ms
	 *            : time to refresh location
	 * @param meter
	 *            : distance to refresh location
	 */
	public void start(int ms, int meter) {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				ms, meter, new SmartLocationListener(gpsListeners));
	}

	/**
	 * Function which add the listener to the list
	 * 
	 * @param listener
	 *            : listener to add
	 */
	public void addGpsListener(GpsListener listener) {
		gpsListeners.add(listener);
	}

	public void removeGPSListener(GpsListener listener) {
		gpsListeners.remove(listener);
	}

}
