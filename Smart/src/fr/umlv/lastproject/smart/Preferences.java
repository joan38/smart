package fr.umlv.lastproject.smart;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class Preferences {

	private SharedPreferences sharedPref;
	private SharedPreferences.Editor sharedPrefEditor;
	private static Preferences preferences;

	// Values by default
	private int theme = R.style.AppBaseTheme;
	private ArrayList<Integer> shortcuts = new ArrayList<Integer>();

	private static final String UNABLE_TO_SAVE = "Unable to save the object in preferences";

	// Add values to persist here with his default value

	/**
	 * Load the preferences.
	 * 
	 * @throws PreferencesException
	 */
	@SuppressWarnings("unchecked")
	public void load() throws PreferencesException {
		theme = getInt("theme", theme);
		shortcuts = (ArrayList<Integer>) getObject("shortcuts", shortcuts);
	}

	/**
	 * Save the preferences.
	 * 
	 * @throws PreferencesException
	 */
	public void save() throws PreferencesException {
		putInt("theme", theme);
		putObject("shortcuts", shortcuts);

		sharedPrefEditor.commit();
	}

	private Preferences(Context context) throws PreferencesException {
		sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPrefEditor = sharedPref.edit();
		load();
	}

	/**
	 * Return the instance of Preferences. If there is no instance an exception
	 * is thown.
	 * 
	 * @return the instance of Preferences
	 * @throws IllegalStateException
	 *             when the preferences are not instanciated. Use
	 *             getInstance(Context context) to instanciate it before.
	 */
	public static Preferences getInstance() {
		if (preferences == null) {
			throw new IllegalStateException(
					"The preferences are not instanciated. Use getInstance(Context context) to instanciate it before.");
		}

		return preferences;
	}

	/**
	 * Return the instance of Preferences.
	 * 
	 * @return the instance of Preferences
	 * @throws PreferencesException
	 */
	public static Preferences getInstance(Context context)
			throws PreferencesException {
		if (preferences == null) {
			preferences = new Preferences(context);
		}

		return preferences;
	}

	private void putObject(String key, Serializable object)
			throws PreferencesException {
		try {
			sharedPrefEditor.putString(key, objectToString(object));
		} catch (IOException e) {
			throw new PreferencesException(UNABLE_TO_SAVE, e);
		}
	}

	private void putInt(String key, int value) {
		sharedPrefEditor.putInt(key, value);
	}

	private void putFloat(String key, float value) {
		sharedPrefEditor.putFloat(key, value);
	}

	private void putBoolean(String key, boolean value) {
		sharedPrefEditor.putBoolean(key, value);
	}

	private void putLong(String key, long value) {
		sharedPrefEditor.putLong(key, value);
	}

	private void putString(String key, String value) {
		sharedPrefEditor.putString(key, value);
	}

	private Object getObject(String key, Serializable defaultObject)
			throws PreferencesException {
		try {
			return stringToObject(sharedPref.getString(key,
					objectToString(defaultObject)));
		} catch (IOException e) {
			throw new PreferencesException(UNABLE_TO_SAVE, e);
		} catch (ClassNotFoundException e) {
			throw new PreferencesException(UNABLE_TO_SAVE, e);
		}
	}

	private int getInt(String key, int defaultInt) {
		return sharedPref.getInt(key, defaultInt);
	}

	private float getFloat(String key, float defaultFloat) {
		return sharedPref.getFloat(key, defaultFloat);
	}

	private boolean getBoolean(String key, boolean defaultBoolean) {
		return sharedPref.getBoolean(key, defaultBoolean);
	}

	private long getLong(String key, long defaultLong) {
		return sharedPref.getLong(key, defaultLong);
	}

	private String getString(String key, String defaultString) {
		return sharedPref.getString(key, defaultString);
	}

	private static String objectToString(Serializable object)
			throws IOException {
		ObjectOutputStream oos = null;

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(out);
			oos.writeObject(object);
			return bytesToString(out.toByteArray());
		} finally {
			oos.close();
		}
	}

	private static Object stringToObject(String string)
			throws StreamCorruptedException, IOException,
			ClassNotFoundException {
		ObjectInputStream ois = null;

		try {
			ByteArrayInputStream in = new ByteArrayInputStream(
					stringToBytes(string));
			ois = new ObjectInputStream(in);

			return ois.readObject();
		} finally {
			ois.close();
		}
	}

	private static String bytesToString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append((char) b);
		}

		return sb.toString();
	}

	private static byte[] stringToBytes(String string) {
		byte[] bytes = new byte[string.length()];
		for (int i = 0; i < string.length(); i++) {
			bytes[i] = (byte) string.charAt(i);
		}

		return bytes;
	}

	public int getTheme() {
		return theme;
	}

	public void setTheme(int theme) {
		this.theme = theme;
	}

	public ArrayList<Integer> getShortcuts() {
		return shortcuts;
	}

	public void setShortcuts(ArrayList<Integer> shortcuts) {
		this.shortcuts = shortcuts;
	}
}