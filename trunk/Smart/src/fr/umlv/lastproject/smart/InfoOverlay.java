package fr.umlv.lastproject.smart;

import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import fr.umlv.lastproject.smart.utils.SmartLogger;

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

	private boolean isLatitudeVisible;
	private boolean isLongitudeVisible;
	private boolean isAltitudeVisible;
	private boolean isAccuracyVisible;
	private boolean isBearingVisible;
	private boolean isSpeedVisible;

	private final View infoView;
	private int nbInfoVisible = 4;
	private boolean isVisible = true;

	private final Logger logger = SmartLogger.getLocator().getLogger();

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
	 * Function which update the locations infos
	 */
	public void updateInfos(GPSEvent event) {
		latitude = event.getLatitude();
		longitude = event.getLongitude();
		altitude = event.getAltitude();
		accuracy = event.getAccuracy();
		bearing = event.getBearing();
		speed = event.getSpeed();

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
			isVisible = false;
		} else {
			infoView.setVisibility(View.VISIBLE);
			isVisible = true;
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
		if (view.getVisibility() != View.VISIBLE && isVisible) {
			isVisible = false;
			item.setTitle(R.string.hideInfoZone);
			view.setVisibility(View.VISIBLE);
			logger.log(Level.INFO, "Info zone set visible");
		} else {
			isVisible = true;
			item.setTitle(R.string.showInfoZone);
			view.setVisibility(View.INVISIBLE);
			logger.log(Level.INFO, "Info zone set invisible");
		}
	}
}
