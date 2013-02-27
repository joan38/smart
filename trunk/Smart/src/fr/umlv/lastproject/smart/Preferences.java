package fr.umlv.lastproject.smart;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

	private Context context;

	// Values by default
	public int theme = R.style.AppBaseTheme;
	public int nbInfoVisible = 4;

	// Add values to persist here with his default value

	public Preferences(Context context) {
		this.context = context;
		load();
	}

	public void load() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);

		this.theme = pref.getInt("theme", theme);
		this.nbInfoVisible = pref.getInt("nbInfoVisible", nbInfoVisible);
	}

	public void save() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = pref.edit();

		editor.putInt("theme", theme);
		editor.putInt("nbInfoVisible", nbInfoVisible);

		editor.commit();
	}
}