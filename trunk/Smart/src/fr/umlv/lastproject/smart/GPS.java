package fr.umlv.lastproject.smart;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.location.Criteria;
import android.location.LocationManager;

public class GPS {
	private LocationManager locationManager;
	private Criteria criteria;


	private final List<IGPSListener> gpsListeners;

	/**
	 * GPS Constructor
	 * 
	 * @param lm
	 *            : LocationManager of the GPS
	 */
	public GPS(LocationManager lm) {
		if(lm==null){
			throw new IllegalArgumentException();
		}
		gpsListeners = new ArrayList<IGPSListener>();
		this.locationManager = lm;
		this.criteria = new Criteria();
		this.criteria.setAltitudeRequired(false);
		this.criteria.setBearingRequired(true);
		this.criteria.setCostAllowed(true);
		this.criteria.setSpeedRequired(true);
	}

	/**
	 * Function which try if the LocationManager is enable
	 * 
	 * @param lm
	 *            : the location manager to try
	 * @return true if GPS is enable
	 */
	public boolean isEnabled(LocationManager lm) {
		return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/**
	 * Function which return the list of compatible GPS with criteria
	 * 
	 * @return the list of compatible GPS
	 */
	public List<String> getValidateGPS() {
		return  locationManager.getProviders(criteria, true);
	}

	/**
	 * Function which start the GPS Location Updates
	 * 
	 * @param ms
	 *            : time to refresh location
	 * @param meter
	 *            : distance to refresh location
	 */
	public void start(int ms, int meter) {
		// TODO Attention: test isEnabled a faire dans la classe qui appel le
		// start !
		this.locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, ms, meter,
				new SmartLocationListener(gpsListeners));

					
	}

	/**
	 * Function which add the listener to the list
	 * 
	 * @param listener
	 *            : listener to add
	 */
	public void addGPSListener(IGPSListener listener) {
		gpsListeners.add(listener);
	}
	
	public void removeGPSListener(IGPSListener listener){
		gpsListeners.remove(listener);
	}


}
