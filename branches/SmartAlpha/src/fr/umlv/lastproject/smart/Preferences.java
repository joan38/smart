package fr.umlv.lastproject.smart;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

	private Context context;
	private static Preferences pref;
	
	// Values by default
	public int theme = R.style.AppBaseTheme;
	// Add values to persist here with his default value

	private Preferences(Context context) {
		this.context = context;
		load();
	}

	/**
	 * Return the instance of Preferences. If there is no instance an exception
	 * is thown.
	 * 
	 * @return the instance of Preferences
	 * @throws IllegalStateException
	 *             when the preferences are not instanciated.
	 *             Use getInstance(Context context) to instanciate it before.
	 */
	public static Preferences getInstance() {
		if (pref == null) {
			throw new IllegalStateException(
					"The preferences are not instanciated. Use getInstance(Context context) to instanciate it before.");
		}

		return pref;
	}
	
	/**
	 * Return the instance of Preferences.
	 * 
	 * @return the instance of Preferences
	 */
	public static Preferences getInstance(Context context) {
		if (pref == null) {
			pref = new Preferences(context);
		}

		return pref;
	}

	/**
	 * Load the preferences.
	 */
	public void load() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);

		this.theme = pref.getInt("theme", theme);
	}

	/**
	 * Save the preferences. 
	 */
	public void save() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = pref.edit();

		editor.putInt("theme", theme);

		editor.commit();
	}
}