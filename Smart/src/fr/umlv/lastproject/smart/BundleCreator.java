package fr.umlv.lastproject.smart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osmdroid.util.GeoPoint;

import android.location.LocationManager;
import android.os.Bundle;
import fr.umlv.lastproject.smart.GpsTrack.TrackMode;
import fr.umlv.lastproject.smart.data.DataImport;
import fr.umlv.lastproject.smart.data.TMSOverlay;
import fr.umlv.lastproject.smart.form.Form;
import fr.umlv.lastproject.smart.form.Mission;
import fr.umlv.lastproject.smart.layers.Geometry;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.layers.GeometryType;
import fr.umlv.lastproject.smart.layers.Symbology;
import fr.umlv.lastproject.smart.utils.SmartLogger;

/**
 * Static class used to save instance state of MenuActivity
 * 
 * @author thibault brun
 * 
 */

public final class BundleCreator {

	private static final Logger LOGGER = SmartLogger.getLocator().getLogger();

	public static final String MISSION_STARTED = "MissionStarted";
	public static final String MISSION_ID = "MISSIONID";
	public static final String MISSION_NAME = "MISSIONNAME";

	public static final String MISSION_POINT_NAME = "MISSIONPOINTNAME";
	public static final String MISSION_POINT_COUNT = "MISSIONPOINTCOUNT";
	public static final String MISSION_POINT_SYMBO = "MISSIONPOINTSYMBO";

	public static final String MISSION_LINE_NAME = "MISSIONLINENAME";
	public static final String MISSION_LINE_COUNT = "MISSIONLINECOUNT";
	public static final String MISSION_LINE_SYMBO = "MISSIONLINESYMBO";

	public static final String MISSION_POLYGON_NAME = "MISSIONPOLYGONNAME";
	public static final String MISSION_POLYGON_COUNT = "MISSIONPOLYGONCOUNT";
	public static final String MISSION_POLYGON_SYMBO = "MISSIONPOLYGONSYMBO";

	public static final String FORM = "FORM";

	public static final String MAP_LAT = "mapLat";
	public static final String MAP_LON = "mapLon";
	public static final String MAP_ZOOM = "mapZoom";

	/**
	 * Creates the Bundle which saves all data
	 * 
	 * @param mapView
	 * @param gpsTrack
	 * @param polygonTrack
	 * @return
	 */
	public static Bundle createBundle(SmartMapView mapView, GpsTrack gpsTrack,
			GpsTrack polygonTrack) {
		Bundle b = new Bundle();
		BundleCreator.savePosition(b, mapView);
		BundleCreator.saveMission(b);
		BundleCreator.saveGeometryLayers(b, mapView.getGeometryOverlays());
		BundleCreator.saveGeotiffs(b, mapView.getGeoTIFFOverlays());
		BundleCreator.saveTrack(b, gpsTrack);
		BundleCreator.savePolygonTrack(b, polygonTrack);
		return b;
	}

	/**
	 * 
	 * @param outState
	 *            bundle
	 * @param missionCreated
	 *            mission is created ?
	 */
	public static void saveMission(Bundle outState) {
		// Mission started
		outState.putSerializable(MISSION_STARTED, Mission.isCreated());

		if (Mission.isCreated()) {
			// Mission started
			outState.putSerializable(MISSION_STARTED, Mission.getInstance()
					.isStarted());

			outState.putLong(MISSION_ID, Mission.getInstance().getId());

			// Mission form
			outState.putSerializable(FORM, Mission.getInstance().getForm());

			// MISSION NAME
			outState.putString(MISSION_NAME, Mission.getInstance().getTitle());

			// Layer point mission
			outState.putString(MISSION_POINT_NAME, Mission.getInstance()
					.getPointLayer().getName());
			outState.putInt(MISSION_POINT_COUNT, Mission.getInstance()
					.getPointLayer().getGeometries().size());
			for (int i = 0; i < Mission.getInstance().getPointLayer()
					.getGeometries().size(); i++) {
				outState.putSerializable(Mission.getInstance().getPointLayer()
						.getName()
						+ i, Mission.getInstance().getPointLayer()
						.getGeometries().get(i));
			}
			outState.putSerializable(MISSION_POINT_SYMBO, Mission.getInstance()
					.getPointLayer().getSymbology());

			// Layer ligne mission
			outState.putString(MISSION_LINE_NAME, Mission.getInstance()
					.getLineLayer().getName());
			outState.putInt(MISSION_LINE_NAME, Mission.getInstance()
					.getLineLayer().getGeometries().size());
			for (int i = 0; i < Mission.getInstance().getLineLayer()
					.getGeometries().size(); i++) {
				outState.putSerializable(Mission.getInstance().getLineLayer()
						.getName()
						+ i, Mission.getInstance().getLineLayer()
						.getGeometries().get(i));
			}
			outState.putSerializable(MISSION_LINE_SYMBO, Mission.getInstance()
					.getLineLayer().getSymbology());

			// Layer polygon mission
			outState.putString(MISSION_POLYGON_NAME, Mission.getInstance()
					.getPolygonLayer().getName());
			outState.putInt(MISSION_POLYGON_COUNT, Mission.getInstance()
					.getPolygonLayer().getGeometries().size());
			for (int i = 0; i < Mission.getInstance().getPolygonLayer()
					.getGeometries().size(); i++) {
				outState.putSerializable(Mission.getInstance()
						.getPolygonLayer().getName()
						+ i, Mission.getInstance().getPolygonLayer()
						.getGeometries().get(i));
			}
			outState.putSerializable(MISSION_POLYGON_SYMBO, Mission
					.getInstance().getPolygonLayer().getSymbology());
		}
	}

	/**
	 * 
	 * @param outState
	 *            the bundle
	 * @param mapView
	 *            the map
	 */
	public static void savePosition(Bundle outState, SmartMapView mapView) {
		outState.putInt(MAP_LAT, mapView.getBoundingBox().getCenter()
				.getLatitudeE6());
		outState.putInt(MAP_LON, mapView.getBoundingBox().getCenter()
				.getLongitudeE6());
		outState.putInt(MAP_ZOOM, mapView.getZoomLevel());

	}

	/**
	 * 
	 * @param savedInstanceState
	 *            the bundle
	 * @param mapView
	 *            the map
	 */
	public static void loadPosition(Bundle savedInstanceState,
			SmartMapView mapView) {

		mapView.getController().setCenter(
				new GeoPoint(savedInstanceState.getInt(MAP_LAT),
						savedInstanceState.getInt(MAP_LON)));
		mapView.getController().setZoom(savedInstanceState.getInt(MAP_ZOOM));
		mapView.getTileProvider().clearTileCache();

	}

	/**
	 * 
	 * @param savedInstanceState
	 *            the bundle
	 * @param mapView
	 *            the map
	 * @param menu
	 *            the contet
	 * @return if the mission is created
	 */
	public static void loadMission(Bundle savedInstanceState,
			SmartMapView mapView, MenuActivity menu) {

		// Mission created
		boolean missionStarted = savedInstanceState.getBoolean(MISSION_STARTED);

		if (missionStarted) {
			long id = savedInstanceState.getLong(MISSION_ID);

			// Mission name
			String mname = savedInstanceState.getString(MISSION_NAME);

			// Mission Form
			Form f = (Form) savedInstanceState.getSerializable(FORM);

			// Mission point layer
			GeometryLayer missionPoint = new GeometryLayer(menu);
			missionPoint.setType(GeometryType.POINT);
			missionPoint.setSymbology((Symbology) savedInstanceState
					.getSerializable(MISSION_POINT_SYMBO));
			missionPoint.setName(savedInstanceState
					.getString(MISSION_POINT_NAME));
			for (int i = 0; i < savedInstanceState.getInt(MISSION_POINT_COUNT); i++) {
				missionPoint.addGeometry((Geometry) savedInstanceState
						.getSerializable(missionPoint.getName() + i));
			}

			// Mission line layer
			GeometryLayer missionLine = new GeometryLayer(menu);
			missionLine.setType(GeometryType.LINE);
			missionLine.setSymbology((Symbology) savedInstanceState
					.getSerializable(MISSION_LINE_SYMBO));
			missionLine
					.setName(savedInstanceState.getString(MISSION_LINE_NAME));
			for (int i = 0; i < savedInstanceState.getInt(MISSION_LINE_COUNT); i++) {
				missionLine.addGeometry((Geometry) savedInstanceState
						.getSerializable(missionLine.getName() + i));
			}

			// Mission polygon layer
			GeometryLayer missionPolygon = new GeometryLayer(menu);
			missionPolygon.setType(GeometryType.POLYGON);
			missionPolygon.setSymbology((Symbology) savedInstanceState
					.getSerializable(MISSION_POLYGON_SYMBO));
			missionPolygon.setName(savedInstanceState
					.getString(MISSION_POLYGON_NAME));
			for (int i = 0; i < savedInstanceState
					.getInt(MISSION_POLYGON_COUNT); i++) {
				missionPolygon.addGeometry((Geometry) savedInstanceState
						.getSerializable(missionPolygon.getName() + i));
			}

			Mission.create(mname, menu, mapView, f, missionPoint, missionLine,
					missionPolygon);
			mapView.addGeometryLayer(Mission.getInstance().getPointLayer());
			mapView.addGeometryLayer(Mission.getInstance().getLineLayer());
			mapView.addGeometryLayer(Mission.getInstance().getPolygonLayer());
			Mission.getInstance().setId(id);
			Mission.getInstance().startMission();
		}
	}

	/**
	 * 
	 * @param outState
	 *            the bundle
	 * @param geometryOberlays
	 *            the layers geometry
	 */

	public static final String GEOMETRY_LAYER = "GEOMETRYLAYER";
	public static final String TYPE = "TYPE";
	public static final String SYMBO = "SYMBO";
	public static final String GEOMETRY_LAYER_COUNT = "GEOMETRYLAYERCOUNT";

	public static void saveGeometryLayers(Bundle outState,
			List<GeometryLayer> geometryOberlays) {
		int count = 0;
		for (GeometryLayer g : geometryOberlays) {
			if (!isInMission(g)) {
				outState.putString(GEOMETRY_LAYER + count, g.getName());
				outState.putInt(g.getName(), g.getGeometries().size());
				outState.putSerializable(g.getName() + TYPE, g.getType());
				for (int i = 0; i < g.getGeometries().size(); i++) {
					outState.putSerializable(g.getName() + i, g.getGeometries()
							.get(i));
				}
				outState.putSerializable(g.getName() + SYMBO, g.getSymbology());
				count++;
			}
		}
		outState.putInt(GEOMETRY_LAYER_COUNT, count);
	}

	/**
	 * 
	 * @param savedInstanceState
	 *            the bundle
	 * @param menu
	 *            the context
	 * @param map
	 *            the map
	 */
	public static void loadGeometryLayers(Bundle savedInstanceState,
			MenuActivity menu, SmartMapView map) {
		// read the number of layers :)
		int count = savedInstanceState.getInt(GEOMETRY_LAYER_COUNT);
		for (int i = 0; i < count; i++) {
			String name = savedInstanceState.getString(GEOMETRY_LAYER + i);
			int geomCOunt = savedInstanceState.getInt(name);
			GeometryLayer tmp = new GeometryLayer(menu);
			tmp.setType((GeometryType) savedInstanceState.getSerializable(name
					+ TYPE));
			for (int j = 0; j < geomCOunt; j++) {
				tmp.addGeometry((Geometry) savedInstanceState
						.getSerializable(name + j));
			}
			tmp.setName(name);
			tmp.setSymbology((Symbology) savedInstanceState
					.getSerializable(name + SYMBO));
			map.addGeometryLayer(tmp);
		}

	}

	/**
	 * 
	 * @param g
	 *            a geometrylayer
	 * @return true if is in the mission
	 */
	public static boolean isInMission(GeometryLayer g) {

		if (Mission.getInstance() != null) {
			if (g.getName().equals(
					Mission.getInstance().getPointLayer().getName())) {
				return true;
			}
			if (g.getName().equals(
					Mission.getInstance().getPolygonLayer().getName())) {
				return true;
			}
			if (g.getName().equals(
					Mission.getInstance().getLineLayer().getName())) {
				return true;
			}
		}
		return false;
	}

	public static final String GEOTIFF_COUNT = "GEOTIFFCOUNT";
	public static final String GEOTIFF = "GEOTIFF";

	/**
	 * 
	 * @param outState
	 *            the bundle
	 * @param geoTIFFOverlays
	 *            the tiff layers
	 */
	public static void saveGeotiffs(Bundle outState,
			List<TMSOverlay> geoTIFFOverlays) {
		int count = geoTIFFOverlays.size();
		outState.putInt(GEOTIFF_COUNT, count);

		for (int i = 0; i < count; i++) {
			outState.putString(GEOTIFF + i, geoTIFFOverlays.get(i).getPath());
		}
	}

	public static void loadGeotiffs(Bundle savedInstance, SmartMapView map,
			MenuActivity ctx) {
		int count = savedInstance.getInt(GEOTIFF_COUNT);
		for (int i = 0; i < count; i++) {
			String path = savedInstance.getString(GEOTIFF + i);
			try {
				TMSOverlay overlay = DataImport.importGeoTIFFFileFolder(path,
						ctx);
				map.addGeoTIFFOverlay(overlay);
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Load GeoTIFF Error");
			}
		}
	}

	public static final String GPS_TRACK_STARTED = "GPSTRACKSTARTED";
	public static final String GPS_TRACK_NAME = "GPSTRACKNAME";
	public static final String GPS_TRACK_MODE = "GPSTRACKMODE";
	public static final String GPS_TRACK_VALUE = "GPSTRACKVALUE";
	public static final String GPS_TRACK_COUNT = "GPSTRACKCOUNT";
	public static final String GPS_TRACKPOINT = "GPSTRACKPOINT";

	public static void saveTrack(Bundle outState, GpsTrack gpsTrack) {
		boolean started = (gpsTrack == null ? false : gpsTrack.isStarted());
		outState.putBoolean(GPS_TRACK_STARTED, started);
		if (!started) {
			return;
		}
		outState.putString(GPS_TRACK_NAME, gpsTrack.getName());
		outState.putString(GPS_TRACK_MODE, gpsTrack.getTrackMode().name());
		outState.putInt(GPS_TRACK_VALUE, gpsTrack.getTrackMode().getParameter());
		int count = gpsTrack.getTrackPoints().size();
		outState.putInt(GPS_TRACK_COUNT, count);
		for (int i = 0; i < count; i++) {
			outState.putSerializable(GPS_TRACKPOINT + i, gpsTrack
					.getTrackPoints().get(i));
		}
	}

	public static GpsTrack loadTrack(Bundle savedInstanceState,
			SmartMapView mapView, MenuActivity menuActivity,
			LocationManager lm, MenuActivity activity) {
		boolean started = savedInstanceState.getBoolean(GPS_TRACK_STARTED);
		if (!started) {
			return null;
		}
		String name = savedInstanceState.getString(GPS_TRACK_NAME);
		String modeName = savedInstanceState.getString(GPS_TRACK_MODE);
		int modeValue = savedInstanceState.getInt(GPS_TRACK_VALUE);
		GpsTrack.TrackMode mode = TrackMode.valueOf(modeName);
		mode.setParameter(modeValue);
		int count = savedInstanceState.getInt(GPS_TRACK_COUNT);
		List<TrackPoint> points = new ArrayList<TrackPoint>();
		for (int i = 0; i < count; i++) {
			points.add((TrackPoint) savedInstanceState
					.getSerializable(GPS_TRACKPOINT + i));
		}
		GeometryLayer layer = null;
		for (GeometryLayer l : mapView.getGeometryOverlays()) {
			if (l.getName().equals(name)) {
				layer = l;
			}
		}

		GpsTrack track = new GpsTrack(mode, name, lm, GeometryType.LINE,
				points, layer, activity);
		track.startTrack();
		return track;
	}

	public static final String GPS_TRACK_POLYGON_STARTED = "GPSTRACKPOLYGONSTARTED";
	public static final String GPS_TRACK_POLYGON_NAME = "GPSTRACKPOLYGONNAME";
	public static final String GPS_TRACK_POLYGON_MODE = "GPSTRACKPOLYGONMODE";
	public static final String GPS_TRACK_POLYGON_VALUE = "GPSTRACKPOLYGONVALUE";
	public static final String GPS_TRACK_POLYGON_COUNT = "GPSTRACKPOLYGONCOUNT";
	public static final String GPS_TRACK_POLYGON_POINT = "GPSTRACKPOLYGONPOINT";

	/**
	 * Save polygon track data
	 * 
	 * @param outState
	 * @param gpsTrack
	 */
	public static void savePolygonTrack(Bundle outState, GpsTrack gpsTrack) {
		boolean started = (gpsTrack == null ? false : gpsTrack.isStarted());
		outState.putBoolean(GPS_TRACK_POLYGON_STARTED, started);
		if (!started) {
			return;
		}
		outState.putString(GPS_TRACK_POLYGON_NAME, gpsTrack.getName());
		outState.putString(GPS_TRACK_POLYGON_MODE, gpsTrack.getTrackMode()
				.name());
		outState.putInt(GPS_TRACK_POLYGON_VALUE, gpsTrack.getTrackMode()
				.getParameter());
		int count = gpsTrack.getTrackPoints().size();
		outState.putInt(GPS_TRACK_POLYGON_COUNT, count);
		Mission.getInstance().getPolygonLayer()
				.removeGeometry(gpsTrack.getGeometry());
		for (int i = 0; i < count; i++) {
			outState.putSerializable(GPS_TRACK_POLYGON_POINT + i, gpsTrack
					.getTrackPoints().get(i));
		}
	}

	/**
	 * 
	 * Retrieve old polygon track data
	 * 
	 * @param savedInstanceState
	 * @param mapView
	 * @param layer
	 * @param lm
	 * @param activity
	 * @return
	 */
	public static GpsTrack loadPolygonTrack(Bundle savedInstanceState,
			SmartMapView mapView, GeometryLayer layer, LocationManager lm,
			MenuActivity activity) {

		boolean started = savedInstanceState
				.getBoolean(GPS_TRACK_POLYGON_STARTED);
		if (!started) {
			return null;
		}
		String name = savedInstanceState.getString(GPS_TRACK_POLYGON_NAME);
		String modeName = savedInstanceState.getString(GPS_TRACK_POLYGON_MODE);
		int modeValue = savedInstanceState.getInt(GPS_TRACK_POLYGON_VALUE);
		GpsTrack.TrackMode mode = TrackMode.valueOf(modeName);
		mode.setParameter(modeValue);
		int count = savedInstanceState.getInt(GPS_TRACK_POLYGON_COUNT);
		List<TrackPoint> points = new ArrayList<TrackPoint>();
		for (int i = 0; i < count; i++) {
			points.add((TrackPoint) savedInstanceState
					.getSerializable(GPS_TRACK_POLYGON_POINT + i));
		}

		GpsTrack track = new GpsTrack(mode, name, lm, GeometryType.POLYGON,
				points, layer, activity);
		track.startTrack();
		return track;
	}
}
