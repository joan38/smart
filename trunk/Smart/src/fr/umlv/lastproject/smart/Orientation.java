package fr.umlv.lastproject.smart;

import android.content.pm.ActivityInfo;

public enum Orientation {
	PORTRAIT(R.id.buttonPortraitOrientation,
			ActivityInfo.SCREEN_ORIENTATION_PORTRAIT), LANDSCAPE(
			R.id.buttonLandscapOrientation,
			ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

	private final int radioButtonId;
	private final int orientationId;

	private Orientation(int radioButtonId, int orientationId) {
		this.radioButtonId = radioButtonId;
		this.orientationId = orientationId;
	}

	public int getRadioButtonId() {
		return radioButtonId;
	}

	public int getOrientationId() {
		return orientationId;
	}

	public static Orientation getByRadioButtonId(int radioButtonId) {
		for (Orientation orientation : values()) {
			if (orientation.radioButtonId == radioButtonId) {
				return orientation;
			}
		}

		return null;
	}

	public static Orientation getByOrientationId(int orientationId) {
		for (Orientation orientation : values()) {
			if (orientation.orientationId == orientationId) {
				return orientation;
			}
		}

		return null;
	}
}
