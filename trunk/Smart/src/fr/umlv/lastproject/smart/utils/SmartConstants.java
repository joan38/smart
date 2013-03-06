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
	public static final int IMPORT_KML_SHP_BROWSER_ACTIVITY = 5;
	// public static final int IMPORT_SHP_BROWSER_ACTIVITY = 6;
	public static final int IMPORT_TIFF_BROWSER_ACTIVITY = 6;
	public static final int HEIGHT_ACTIVITY = 7;
	public static final int GPS_ACTIVITY = 8;
	public static final int HEIGHT_MODIFY_ACTIVITY = 9;

	public static final int CREATE_MISSION = 0;
	public static final int CREATE_FORM = 1;
	public static final int POINT_SURVEY = 2;
	public static final int POINT_SURVEY_POSITION = 3;
	public static final int LINE_SURVEY = 4;
	public static final int POLYGON_SURVEY = 5;
	public static final int POLYGON_TRACK = 6;
	public static final int GPS_TRACK = 7;
	public static final int IMPORT_KML_SHP = 8;
	public static final int IMPORT_GEOTIFF = 9;
	public static final int IMPORT_WMS = 10;
	public static final int MEASURE = 11;
	public static final int AREA_MEASURE = 12;
	public static final int EXPORT_MISSION = 13;
	public static final int EXPORT_FORM = 14;
	public static final int DELETE_MISSION = 15;
	public static final int STOP_MISSION = 16;
	public static final int STOP_GPS_TRACK = 17;
	public static final int STOP_POLYGON_TRACK = 18;

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
			.getExternalStorageDirectory().getPath() + "/osmdroid/";
	public static final String APP_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/SMART/";
	public static final String TMP_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/SMART/.tmp/";
	public static final String FORM_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/SMART/Forms/";
	public static final String BDD_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/SMART/.DB/";
	public static final String TRACK_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/SMART/Tracks/";
	public static final String PICTURES_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/SMART/Pictures/";
	public static final String LOG_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/SMART/Log/";

	public static int[] colors = new int[] { Color.BLACK,
			Color.rgb(100, 149, 237), Color.rgb(0, 0, 128),
			Color.rgb(238, 203, 173), Color.rgb(34, 139, 34),
			Color.rgb(255, 140, 0), Color.rgb(152, 251, 152),
			Color.rgb(178, 34, 34), Color.rgb(255, 215, 0),
			Color.rgb(49, 79, 79), Color.rgb(139, 69, 19) };

	// Icons array for functionalities list
	public static int[] icons = { R.drawable.startmission,
			R.drawable.createform, R.drawable.pointsurvey,
			R.drawable.pointsurvey_position, R.drawable.linesurvey,
			R.drawable.polygonsurvey, R.drawable.startpolygontrack,
			R.drawable.startgpstrack, R.drawable.importvector,
			R.drawable.importraster, R.drawable.importwms, R.drawable.measure,
			R.drawable.area_measure, R.drawable.exportmission,
			R.drawable.exportform, R.drawable.deletemission,
			R.drawable.stopmission, R.drawable.stopgpstrack,
			R.drawable.stoppolygontrack };

	private SmartConstants() {
	}

}
