package fr.umlv.lastproject.smart;

import java.text.DecimalFormat;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * This class is
 * 
 * @author Fad's
 * 
 */
public class InfoOverlay {
	private static final DecimalFormat LOCATION_FORMAT = new DecimalFormat(
			"####0.00000");
	private static final DecimalFormat ACCURACY_FORMAT = new DecimalFormat(
			"####0.00");

	private double latitude;
	private double longitude;
	private double altitude;
	private float accuracy;
	private float bearing;
	private float speed;

	private final View infoView;
	private int nbInfoVisible = 4;

	/**
	 * InfoOverlay constructor
	 * 
	 * @param view
	 *            : Info view
	 */
	public InfoOverlay(final View view) {
		this.infoView = view;
	}

	/**
	 * Set the latitude
	 * 
	 * @param latitude
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * Set the longitude
	 * 
	 * @param longitude
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Set the altitude
	 * 
	 * @param altitude
	 */
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	/**
	 * Set the accuracy
	 * 
	 * @param accuracy
	 */
	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	/**
	 * Set the bearing
	 * 
	 * @param bearing
	 */
	private void setBearing(float bearing) {
		this.bearing = bearing;
	}

	/**
	 * Set the speed
	 * 
	 * @param speed
	 */
	private void setSpeed(float speed) {
		this.speed = speed;
	}

	/**
	 * Function which update the locations infos
	 */
	public void updateInfo(GPSEvent event) {

		setLatitude(event.getLatitude());
		setLongitude(event.getLongitude());
		setAltitude(event.getAltitude());
		setAccuracy(event.getAccuracy());
		setBearing(event.getBearing());
		setSpeed(event.getSpeed());

		((TextView) infoView.findViewById(R.id.findGPS))
				.setVisibility(View.GONE);

		((TextView) infoView.findViewById(R.id.latitude))
				.setText(R.string.latitude);
		((TextView) infoView.findViewById(R.id.latitudeValue))
				.setText(LOCATION_FORMAT.format(latitude));

		((TextView) infoView.findViewById(R.id.longitude))
				.setText(R.string.longitude);
		((TextView) infoView.findViewById(R.id.longitudeValue))
				.setText(LOCATION_FORMAT.format(longitude));

		((TextView) infoView.findViewById(R.id.altitude))
				.setText(R.string.altitude);
		((TextView) infoView.findViewById(R.id.altitudeValue))
				.setText(LOCATION_FORMAT.format(altitude));

		((TextView) infoView.findViewById(R.id.precision))
				.setText(R.string.accuracy);
		((TextView) infoView.findViewById(R.id.precisionValue))
				.setText(ACCURACY_FORMAT.format(accuracy) + "m");

		((TextView) infoView.findViewById(R.id.bearing))
				.setText(R.string.bearing);
		((TextView) infoView.findViewById(R.id.bearingValue))
				.setText(ACCURACY_FORMAT.format(bearing) + "°");

		((TextView) infoView.findViewById(R.id.speed)).setText(R.string.speed);
		((TextView) infoView.findViewById(R.id.speedValue))
				.setText(ACCURACY_FORMAT.format(speed) + "m/s");

	}

	/**
	 * This function set the visibility of informations based on the state of
	 * checkbox. If number of visible informations is 0, the information zone
	 * became invisible.
	 * 
	 * @param view
	 *            : textview (name)
	 * @param viewValue
	 *            : textview (value)
	 * @param visibility
	 */
	public void setVisibility(View view, View viewValue, boolean visibility) {
		if (visibility) {
			view.setVisibility(View.VISIBLE);
			viewValue.setVisibility(View.VISIBLE);
			nbInfoVisible++;
		} else {
			view.setVisibility(View.GONE);
			viewValue.setVisibility(View.GONE);
			nbInfoVisible--;
		}

		if (nbInfoVisible == 0) {
			infoView.setVisibility(View.GONE);
		} else {
			infoView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * This function hide / display the informations zone
	 * 
	 * @param view
	 *            : the view to change
	 * @param item
	 *            : the menu item to change name
	 */
	public void hideInfoZone(View view, MenuItem item) {
		if (view.getVisibility() == View.INVISIBLE && nbInfoVisible > 0) {
			item.setTitle(R.string.hideInfoZone);
			view.setVisibility(View.VISIBLE);
		} else {
			item.setTitle(R.string.showInfoZone);
			view.setVisibility(View.INVISIBLE);

		}
	}
}