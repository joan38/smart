package fr.umlv.lastproject.smart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.GeoPoint;

import android.os.Bundle;
import fr.umlv.lastproject.smart.data.DataImport;
import fr.umlv.lastproject.smart.data.TMSOverlay;
import fr.umlv.lastproject.smart.form.Form;
import fr.umlv.lastproject.smart.form.Mission;
import fr.umlv.lastproject.smart.layers.Geometry;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.layers.GeometryType;
import fr.umlv.lastproject.smart.layers.Symbology;

/**
 * 
 * @author thibault brun
 * 
 */

public class BundleCreator {

	public static Bundle createBundle(SmartMapView mapView,
			boolean missionCreated) {
		Bundle b = new Bundle();
		BundleCreator.savePosition(b, mapView);
		BundleCreator.saveMission(b, missionCreated);
		BundleCreator.saveGeomtryLayers(b, mapView.getGeometryOverlays());
		BundleCreator.saveGeotiffs(b, mapView.getGeoTIFFOverlays());
		return b;
	}

	/**
	 * 
	 * @param outState
	 *            bundle
	 * @param missionCreated
	 *            mission is created ?
	 */
	public static void saveMission(Bundle outState, boolean missionCreated) {
		// Mission started
		outState.putSerializable("MissionCreated", missionCreated);

		if (Mission.getInstance() != null) {
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
	public static boolean loadMission(Bundle savedInstanceState,
			SmartMapView mapView, MenuActivity menu) {

		// Mission created
		boolean missionCreated = savedInstanceState
				.getBoolean("MissionCreated");

		if (missionCreated) {

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
			Mission.getInstance().startMission();
		}

		return missionCreated;

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
		List<GeometryLayer> list = new ArrayList<GeometryLayer>();

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
					Mission.getInstance().getPointLayer().getName()))
				return true;
			if (g.getName().equals(
					Mission.getInstance().getPolygonLayer().getName()))
				return true;
			if (g.getName().equals(
					Mission.getInstance().getLineLayer().getName()))
				return true;
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
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}

	}

	public static void saveTrack(Bundle outState, boolean trackStarted,
			GPSTrack gpsTrack) {

	}

	public static void loadTrack(Bundle savedInstanceState,
			SmartMapView mapView, MenuActivity menuActivity) {
		// TODO Auto-generated method stub

	}

}
