package fr.umlv.lastproject.smart;

import java.text.DecimalFormat;

import android.view.View;
import android.widget.TextView;

/**
 * This class is
 * 
 * @author Fad's
 * 
 */
public class InfoOverlay {
	private static final DecimalFormat LOCATION_FORMAT = new DecimalFormat("####0.00000");
	private static final DecimalFormat ACCURACY_FORMAT = new DecimalFormat("####0.00");

	private double latitude;
	private double longitude;
	private double altitude;
	private float accuracy;

	private final View infoView;

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
	 * Function which update the locations infos
	 */
	public void updateInfo(GPSEvent event) {

		setLatitude(event.getLatitude());
		setLongitude(event.getLongitude());
		setAltitude(event.getAltitude());
		setAccuracy(event.getAccuracy());

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
				.setText(ACCURACY_FORMAT.format(accuracy));

	}
}