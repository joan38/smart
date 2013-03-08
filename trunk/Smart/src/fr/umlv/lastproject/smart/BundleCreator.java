package fr.umlv.lastproject.smart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osmdroid.util.GeoPoint;

import android.location.LocationManager;
import android.os.Bundle;
import fr.umlv.lastproject.smart.GPSTrack.TrackMode;
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
 * 
 * @author thibault brun
 * 
 */

public final class BundleCreator {

	private static final Logger LOGGER = SmartLogger.getLocator().getLogger();

	public static Bundle createBundle(SmartMapView mapView, GPSTrack gpsTrack,
			GPSTrack polygonTrack) {
		Bundle b = new Bundle();
		BundleCreator.savePosition(b, mapView);
		BundleCreator.saveMission(b);
		BundleCreator.saveGeomtryLayers(b, mapView.getGeometryOverlays());
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
		outState.putSerializable("MissionStarted", Mission.isCreated());

		if (Mission.isCreated()) {
			// Mission started
			outState.putSerializable("MissionStarted", Mission.getInstance()
					.isStarted());

			outState.putLong("MISSIONID", Mission.getInstance().getId());

			// Mission form
			outState.putSerializable("FORM", Mission.getInstance().getForm());

			// MISSION NAME
			outState.putString("MISSIONNAME", Mission.getInstance().getTitle());

			// Layer point mission
			outState.putString("MISSIONPOINTNAME", Mission.getInstance()
					.getPointLayer().getName());
			outState.putInt("MISSIONPOINTCOUNT", Mission.getInstance()
					.getPointLayer().getGeometries().size());
			for (int i = 0; i < Mission.getInstance().getPointLayer()
					.getGeometries().size(); i++) {
				outState.putSerializable(Mission.getInstance().getPointLayer()
						.getName()
						+ i, Mission.getInstance().getPointLayer()
						.getGeometries().get(i));
			}
			outState.putSerializable("MISSIONPOINTSYMBO", Mission.getInstance()
					.getPointLayer().getSymbology());

			// Layer ligne mission
			outState.putString("MISSIONLINENAME", Mission.getInstance()
					.getLineLayer().getName());
			outState.putInt("MISSIONLINECOUNT", Mission.getInstance()
					.getLineLayer().getGeometries().size());
			for (int i = 0; i < Mission.getInstance().getLineLayer()
					.getGeometries().size(); i++) {
				outState.putSerializable(Mission.getInstance().getLineLayer()
						.getName()
						+ i, Mission.getInstance().getLineLayer()
						.getGeometries().get(i));
			}
			outState.putSerializable("MISSIONLINESYMBO", Mission.getInstance()
					.getLineLayer().getSymbology());

			// Layer polygon mission
			outState.putString("MISSIONPOLYGONNAME", Mission.getInstance()
					.getPolygonLayer().getName());
			outState.putInt("MISSIONPOLYGONCOUNT", Mission.getInstance()
					.getPolygonLayer().getGeometries().size());
			for (int i = 0; i < Mission.getInstance().getPolygonLayer()
					.getGeometries().size(); i++) {
				outState.putSerializable(Mission.getInstance()
						.getPolygonLayer().getName()
						+ i, Mission.getInstance().getPolygonLayer()
						.getGeometries().get(i));
			}
			outState.putSerializable("MISSIONPOLYGONSYMBO", Mission
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
		outState.putInt("mapLat", mapView.getBoundingBox().getCenter()
				.getLatitudeE6());
		outState.putInt("mapLon", mapView.getBoundingBox().getCenter()
				.getLongitudeE6());
		outState.putInt("mapZoom", mapView.getZoomLevel());

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
				new GeoPoint(savedInstanceState.getInt("mapLat"),
						savedInstanceState.getInt("mapLon")));
		mapView.getController().setZoom(savedInstanceState.getInt("mapZoom"));
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
		boolean missionStarted = savedInstanceState
				.getBoolean("MissionStarted");

		if (missionStarted) {
			long id = savedInstanceState.getLong("MISSIONID");

			// Mission name
			String mname = savedInstanceState.getString("MISSIONNAME");

			// Mission Form
			Form f = (Form) savedInstanceState.getSerializable("FORM");

			// Mission point layer
			GeometryLayer missionPoint = new GeometryLayer(menu);
			missionPoint.setType(GeometryType.POINT);
			missionPoint.setSymbology((Symbology) savedInstanceState
					.getSerializable("MISSIONPOINTSYMBO"));
			missionPoint.setName(savedInstanceState
					.getString("MISSIONPOINTNAME"));
			for (int i = 0; i < savedInstanceState.getInt("MISSIONPOINTCOUNT"); i++) {
				missionPoint.addGeometry((Geometry) savedInstanceState
						.getSerializable(missionPoint.getName() + i));
			}

			// Mission line layer
			GeometryLayer missionLine = new GeometryLayer(menu);
			missionLine.setType(GeometryType.LINE);
			missionLine.setSymbology((Symbology) savedInstanceState
					.getSerializable("MISSIONLINESYMBO"));
			missionLine
					.setName(savedInstanceState.getString("MISSIONLINENAME"));
			for (int i = 0; i < savedInstanceState.getInt("MISSIONLINECOUNT"); i++) {
				missionLine.addGeometry((Geometry) savedInstanceState
						.getSerializable(missionLine.getName() + i));
			}

			// Mission polygon layer
			GeometryLayer missionPolygon = new GeometryLayer(menu);
			missionPolygon.setType(GeometryType.POLYGON);
			missionPolygon.setSymbology((Symbology) savedInstanceState
					.getSerializable("MISSIONPOLYGONSYMBO"));
			missionPolygon.setName(savedInstanceState
					.getString("MISSIONPOLYGONNAME"));
			for (int i = 0; i < savedInstanceState
					.getInt("MISSIONPOLYGONCOUNT"); i++) {
				missionPolygon.addGeometry((Geometry) savedInstanceState
						.getSerializable(missionPolygon.getName() + i));
			}

			Mission.createMission(mname, menu, mapView, f, missionPoint,
					missionLine, missionPolygon);
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
	public static void saveGeomtryLayers(Bundle outState,
			List<GeometryLayer> geometryOberlays) {
		int count = 0;
		for (GeometryLayer g : geometryOberlays) {
			if (!isInMission(g)) {
				outState.putString("GEOMETRYLAYER" + count, g.getName());
				outState.putInt(g.getName(), g.getGeometries().size());
				outState.putSerializable(g.getName() + "TYPE", g.getType());
				for (int i = 0; i < g.getGeometries().size(); i++) {
					outState.putSerializable(g.getName() + i, g.getGeometries()
							.get(i));
				}
				outState.putSerializable(g.getName() + "SYMBO",
						g.getSymbology());
				count++;
			}
		}
		outState.putInt("GEOMETRYLAYERCOUNT", count);
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
		int count = savedInstanceState.getInt("GEOMETRYLAYERCOUNT");
		for (int i = 0; i < count; i++) {
			String name = savedInstanceState.getString("GEOMETRYLAYER" + i);
			int geomCOunt = savedInstanceState.getInt(name);
			GeometryLayer tmp = new GeometryLayer(menu);
			tmp.setType((GeometryType) savedInstanceState.getSerializable(name
					+ "TYPE"));
			for (int j = 0; j < geomCOunt; j++) {
				tmp.addGeometry((Geometry) savedInstanceState
						.getSerializable(name + j));
			}
			tmp.setName(name);
			tmp.setSymbology((Symbology) savedInstanceState
					.getSerializable(name + "SYMBO"));
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
		outState.putInt("GEOTIFFCOUNT", count);

		for (int i = 0; i < count; i++) {
			outState.putString("GEOTIFF" + i, geoTIFFOverlays.get(i).getPath());
		}
	}

	public static void loadGeotiffs(Bundle savedInstance, SmartMapView map,
			MenuActivity ctx) {
		int count = savedInstance.getInt("GEOTIFFCOUNT");
		for (int i = 0; i < count; i++) {
			String path = savedInstance.getString("GEOTIFF" + i);
			try {
				TMSOverlay overlay = DataImport.importGeoTIFFFileFolder(path,
						ctx);
				map.addGeoTIFFOverlay(overlay);
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Load GeoTIFF Error");
			}
		}
	}

	public static void saveTrack(Bundle outState, GPSTrack gpsTrack) {
		boolean started = (gpsTrack == null ? false : gpsTrack.isStarted());
		outState.putBoolean("GPSTRACKSTARTED", started);
		if (!started) {
			return;
		}
		outState.putString("GPSTRACKNAME", gpsTrack.getName());
		outState.putString("GPSTRACKMODE", gpsTrack.getTrackMode().name());
		outState.putInt("GPSTRACKVALUE", gpsTrack.getTrackMode().getParameter());
		int count = gpsTrack.getTrackPoints().size();
		outState.putInt("GPSTRACKCOUNT", count);
		for (int i = 0; i < count; i++) {
			outState.putSerializable("GPSTRACKPOINT" + i, gpsTrack
					.getTrackPoints().get(i));
		}
	}

	public static GPSTrack loadTrack(Bundle savedInstanceState,
			SmartMapView mapView, MenuActivity menuActivity,
			LocationManager lm, MenuActivity activity) {
		boolean started = savedInstanceState.getBoolean("GPSTRACKSTARTED");
		if (!started) {
			return null;
		}
		String name = savedInstanceState.getString("GPSTRACKNAME");
		String modeName = savedInstanceState.getString("GPSTRACKMODE");
		int modeValue = savedInstanceState.getInt("GPSTRACKVALUE");
		GPSTrack.TrackMode mode = TrackMode.valueOf(modeName);
		mode.setParameter(modeValue);
		int count = savedInstanceState.getInt("GPSTRACKCOUNT");
		List<TrackPoint> points = new ArrayList<TrackPoint>();
		for (int i = 0; i < count; i++) {
			points.add((TrackPoint) savedInstanceState
					.getSerializable("GPSTRACKPOINT" + i));
		}
		GeometryLayer layer = null;
		for (GeometryLayer l : mapView.getGeometryOverlays()) {
			if (l.getName().equals(name)) {
				layer = l;
			}
		}
		GPSTrack track = new GPSTrack(mode, name, lm, GeometryType.LINE,
				points, layer, activity);
		track.startTrack();
		return track;
	}

	public static void savePolygonTrack(Bundle outState, GPSTrack gpsTrack) {

		boolean started = (gpsTrack == null ? false : gpsTrack.isStarted());
		outState.putBoolean("GPSTRACKPOLYGONSTARTED", started);
		if (!started) {
			return;
		}
		outState.putString("GPSTRACKPOLYGONNAME", gpsTrack.getName());
		outState.putString("GPSTRACKPOLYGONMODE", gpsTrack.getTrackMode()
				.name());
		outState.putInt("GPSTRACKPOLYGONVALUE", gpsTrack.getTrackMode()
				.getParameter());
		int count = gpsTrack.getTrackPoints().size();
		outState.putInt("GPSTRACKPOLYGONCOUNT", count);
		Mission.getInstance().getPolygonLayer()
				.removeGeometry(gpsTrack.getGeometry());
		for (int i = 0; i < count; i++) {
			outState.putSerializable("GPSTRACKPOLYGONPOINT" + i, gpsTrack
					.getTrackPoints().get(i));
		}
	}

	public static GPSTrack loadPolygonTrack(Bundle savedInstanceState,
			SmartMapView mapView, GeometryLayer layer, LocationManager lm,
			MenuActivity activity) {
		// TODO Auto-generated method stub
		boolean started = savedInstanceState
				.getBoolean("GPSTRACKPOLYGONSTARTED");
		if (!started) {
			return null;
		}
		String name = savedInstanceState.getString("GPSTRACKPOLYGONNAME");
		String modeName = savedInstanceState.getString("GPSTRACKPOLYGONMODE");
		int modeValue = savedInstanceState.getInt("GPSTRACKPOLYGONVALUE");
		GPSTrack.TrackMode mode = TrackMode.valueOf(modeName);
		mode.setParameter(modeValue);
		int count = savedInstanceState.getInt("GPSTRACKPOLYGONCOUNT");
		List<TrackPoint> points = new ArrayList<TrackPoint>();
		for (int i = 0; i < count; i++) {
			points.add((TrackPoint) savedInstanceState
					.getSerializable("GPSTRACKPOLYGONPOINT" + i));
		}
		GPSTrack track = new GPSTrack(mode, name, lm, GeometryType.POLYGON,
				points, layer, activity);

		track.startTrack();
		return track;
	}
}
