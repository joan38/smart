package fr.umlv.lastproject.smart.utils;

import fr.umlv.lastproject.smart.Preferences;

public final class SmartParameters {

	private static SmartParameters parameters = new SmartParameters();
	private int screenOrientation;
	private int applicationTheme;

	final Preferences pref = Preferences.getInstance();

	private SmartParameters() {
		screenOrientation = pref.getOrientation();
		applicationTheme = pref.getTheme();
	}

	/**
	 * @return
	 */
	public static SmartParameters getParameters() {
		return parameters;
	}

	public int getScreenOrientation() {
		return screenOrientation;
	}

	public void setScreenOrientation(int screenOrientation) {
		this.screenOrientation = screenOrientation;
	}

	public int getApplicationTheme() {
		return applicationTheme;
	}

	public void setApplicationTheme(int applicationTheme) {
		this.applicationTheme = applicationTheme;
	}

}
