package fr.umlv.lastproject.smart;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.logging.Logger;

import android.view.View;
import android.widget.TextView;
import fr.umlv.lastproject.smart.utils.SmartLogger;

/**
 * Informations shown on the bottom of the map.
 * 
 * @author Fad's
 */
public class InfosOverlay implements GpsListener {

	private static final DecimalFormat LOCATION_FORMAT = new DecimalFormat(
			"####0.00000");
	private static final DecimalFormat ACCURACY_FORMAT = new DecimalFormat(
			"####0.00");

	private final View infoView;
	private final InfosState infosState;

	public static class InfosState implements Serializable {

		private static final long serialVersionUID = -2908326354631855577L;

		private boolean isVisible = true;
		private boolean isLatitudeVisible = true;
		private boolean isLongitudeVisible = true;
		private boolean isAltitudeVisible = true;
		private boolean isAccuracyVisible = true;
		private boolean isBearingVisible;
		private boolean isSpeedVisible;
	}

	private static final Logger LOGGER = SmartLogger.getLocator().getLogger();

	/**
	 * InfoOverlay constructor
	 * 
	 * @param infoView
	 *            : Info view
	 * @param infosState
	 *            : The state of the infos
	 */
	public InfosOverlay(final View infoView, InfosState infosState) {
		if (infoView == null || infosState == null) {
			throw new IllegalArgumentException("infosState must be non null");
		}

		this.infoView = infoView;
		this.infosState = infosState;

		setLatitudeVisibility(infosState.isLatitudeVisible);
		setLongitudeVisibility(infosState.isLongitudeVisible);
		setAccuracyVisibility(infosState.isAccuracyVisible);
		setBearingVisibility(infosState.isBearingVisible);
		setSpeedVisibility(infosState.isSpeedVisible);
		setAltitudeVisibility(infosState.isAltitudeVisible);
	}

	/**
	 * Function which update the locations infos
	 */
	@Override
	public void locationUpdated(GpsEvent event) {
		((TextView) infoView.findViewById(R.id.findGPS))
				.setVisibility(View.GONE);

		((TextView) infoView.findViewById(R.id.latitudeValue))
				.setText(LOCATION_FORMAT.format(event.getLatitude()));
		((TextView) infoView.findViewById(R.id.longitudeValue))
				.setText(LOCATION_FORMAT.format(event.getLongitude()));
		((TextView) infoView.findViewById(R.id.altitudeValue))
				.setText(LOCATION_FORMAT.format(event.getAltitude()));
		((TextView) infoView.findViewById(R.id.accuracyValue))
				.setText(ACCURACY_FORMAT.format(event.getAccuracy()) + "m");
		((TextView) infoView.findViewById(R.id.bearingValue))
				.setText(ACCURACY_FORMAT.format(event.getBearing()) + "°");
		((TextView) infoView.findViewById(R.id.speedValue))
				.setText(ACCURACY_FORMAT.format(event.getSpeed()) + "m/s");
	}

	public void setLatitudeVisibility(boolean visibility) {
		((TextView) infoView.findViewById(R.id.latitude))
				.setVisibility((visibility ? View.VISIBLE : View.GONE));

		((TextView) infoView.findViewById(R.id.latitudeValue))
				.setVisibility((visibility ? View.VISIBLE : View.GONE));

		infosState.isLatitudeVisible = visibility;
		updateInfOverlayVisibility();
	}

	public boolean isLatitudeVisible() {
		return infosState.isLatitudeVisible;
	}

	public void setLongitudeVisibility(boolean visibility) {
		((TextView) infoView.findViewById(R.id.longitude))
				.setVisibility((visibility ? View.VISIBLE : View.GONE));

		((TextView) infoView.findViewById(R.id.longitudeValue))
				.setVisibility((visibility ? View.VISIBLE : View.GONE));

		infosState.isLongitudeVisible = visibility;
		updateInfOverlayVisibility();
	}

	public boolean isLongitudeVisible() {
		return infosState.isLongitudeVisible;
	}

	public void setAltitudeVisibility(boolean visibility) {
		((TextView) infoView.findViewById(R.id.altitude))
				.setVisibility((visibility ? View.VISIBLE : View.GONE));

		((TextView) infoView.findViewById(R.id.altitudeValue))
				.setVisibility((visibility ? View.VISIBLE : View.GONE));

		infosState.isAltitudeVisible = visibility;
		updateInfOverlayVisibility();
	}

	public boolean isAltitudeVisible() {
		return infosState.isAltitudeVisible;
	}

	public void setAccuracyVisibility(boolean visibility) {
		((TextView) infoView.findViewById(R.id.accuracy))
				.setVisibility((visibility ? View.VISIBLE : View.GONE));

		((TextView) infoView.findViewById(R.id.accuracyValue))
				.setVisibility((visibility ? View.VISIBLE : View.GONE));

		infosState.isAccuracyVisible = visibility;
		updateInfOverlayVisibility();
	}

	public boolean isAccuracyVisible() {
		return infosState.isAccuracyVisible;
	}

	public void setBearingVisibility(boolean visibility) {
		((TextView) infoView.findViewById(R.id.bearing))
				.setVisibility((visibility ? View.VISIBLE : View.GONE));

		((TextView) infoView.findViewById(R.id.bearingValue))
				.setVisibility((visibility ? View.VISIBLE : View.GONE));

		infosState.isBearingVisible = visibility;
		updateInfOverlayVisibility();
	}

	public boolean isBearingVisible() {
		return infosState.isBearingVisible;
	}

	public void setSpeedVisibility(boolean visibility) {
		((TextView) infoView.findViewById(R.id.speed))
				.setVisibility((visibility ? View.VISIBLE : View.GONE));

		((TextView) infoView.findViewById(R.id.speedValue))
				.setVisibility((visibility ? View.VISIBLE : View.GONE));

		infosState.isSpeedVisible = visibility;
		updateInfOverlayVisibility();
	}

	public boolean isSpeedVisible() {
		return infosState.isSpeedVisible;
	}

	private boolean updateInfOverlayVisibility() {
		if (infosState.isVisible
				&& (infosState.isAccuracyVisible
						|| infosState.isAltitudeVisible
						|| infosState.isBearingVisible
						|| infosState.isLatitudeVisible
						|| infosState.isLongitudeVisible || infosState.isSpeedVisible)) {
			infoView.setVisibility(View.VISIBLE);
			return true;
		} else {
			infoView.setVisibility(View.GONE);
			return false;
		}
	}

	/**
	 * Set if the informations zone is displayed
	 * 
	 * @param visibility
	 * @return
	 */
	public void setInfoOverlayVisibility(boolean visibility) {
		infosState.isVisible = visibility;
		updateInfOverlayVisibility();
	}

	public boolean isInfoOverlayVisible() {
		return infosState.isVisible;
	}
}
