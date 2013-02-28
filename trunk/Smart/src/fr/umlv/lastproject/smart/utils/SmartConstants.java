package fr.umlv.lastproject.smart.utils;

import android.graphics.Color;
import android.os.Environment;
import fr.umlv.lastproject.smart.R;

/**
 * This class is used to define constants values used in many classes
 * 
 * @author Maelle Cabot
 * 
 */
public final class SmartConstants {

	public static final int HOME_VIEW = 1;
	public static final int LAYERS_VIEW = 2;
	public static final int MISSION_BROWSER_ACTIVITY = 3;
	public static final int FORM_BROWSER_ACTIVITY = 4;
	public static final int IMPORT_KML_BROWSER_ACTIVITY = 5;
	public static final int IMPORT_SHP_BROWSER_ACTIVITY = 6;
	public static final int IMPORT_TIFF_BROWSER_ACTIVITY = 7;
	public static final int HEIGHT_ACTIVITY = 8;
	public static final int GPS_ACTIVITY = 9;

	public static final int CREATE_MISSION = 0;
	public static final int CREATE_FORM = 1;
	public static final int POINT_SURVEY = 2;
	public static final int LINE_SURVEY = 3;
	public static final int POLYGON_SURVEY = 4;
	public static final int GPS_TRACK = 5;
	public static final int IMPORT_KML = 6;
	public static final int IMPORT_GEOTIFF = 7;
	public static final int IMPORT_SHAPE = 8;
	public static final int IMPORT_WMS = 9;
	public static final int EXPORT = 10;
	public static final int MEASURE = 11;
	public static final int EXPORT_FORM = 12;
	public static final int DELETE_MISSION = 13;

	public static final int GPS_REFRESH_TIME = 5000;
	public static final int GPS_REFRESH_DISTANCE = 10;

	public static final int DEFAULT_ZOOM = 15;

	public static final int TEXT_FIELD = 0;
	public static final int NUMERIC_FIELD = 1;
	public static final int BOOLEAN_FIELD = 2;
	public static final int LIST_FIELD = 3;
	public static final int PICTURE_FIELD = 4;
	public static final int HEIGHT_FIELD = 5;

	public static final String TIFF_PATH = Environment
			.getExternalStorageDirectory() + "/osmdroid/";
	public static final String TMP_PATH = Environment
			.getExternalStorageDirectory() + "/SMART/.tmp/";

	public static int[] colors = new int[] { Color.rgb(100, 149, 237),
			Color.rgb(0, 0, 128), Color.rgb(238, 203, 173),
			Color.rgb(34, 139, 34), Color.rgb(255, 140, 0),
			Color.rgb(152, 251, 152), Color.rgb(178, 34, 34),
			Color.rgb(255, 215, 0), Color.rgb(49, 79, 79),
			Color.rgb(139, 69, 19) };

	// Icons array for functionalities list
	public static int[] icons = { R.drawable.startmission,
			R.drawable.createform, R.drawable.pointsurvey,
			R.drawable.linesurvey, R.drawable.polygonsurvey,
			R.drawable.startgpstrack, R.drawable.smart, R.drawable.basket,
			R.drawable.smart, R.drawable.basket, R.drawable.exportmission,
			R.drawable.measure, R.drawable.exportform, R.drawable.deletemission };

	private SmartConstants() {
	}
}
