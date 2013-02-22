package fr.umlv.lastproject.smart.form;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.SmartMapView;
import fr.umlv.lastproject.smart.database.DbManager;
import fr.umlv.lastproject.smart.database.MissionRecord;
import fr.umlv.lastproject.smart.layers.Geometry;
import fr.umlv.lastproject.smart.layers.Geometry.GeometryType;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.layers.LineSymbology;
import fr.umlv.lastproject.smart.layers.PointSymbology;
import fr.umlv.lastproject.smart.layers.PolygonSymbology;
import fr.umlv.lastproject.smart.survey.Survey;
import fr.umlv.lastproject.smart.survey.SurveyStopListener;
import fr.umlv.lastproject.smart.utils.SmartException;

/**
 * This class is used to create a mission
 * 
 * @author thibault, maelle cabot
 * 
 */
public final class Mission {

	private static Mission mission = null;
	
	private static final int POINT_RADIUS = 10;
	private static final int LINE_THICKNESS = 10;
	private static final int POLY_THICKNESS = 10;


	/* three type of survey are available : line, polygon, point */
	private GeometryLayer pointLayer;
	private GeometryLayer lineLayer;
	private GeometryLayer polygonLayer;

	private final MenuActivity context;

	/* the name of the mission */
	private long id;
	private final String title;

	/* the status (on / off) */
	private boolean status = false;

	private final SmartMapView mapView;
	private Form form;
	private Survey survey;

	/**
	 * 
	 * @param title
	 *            of the mission
	 * @param context
	 *            the context
	 */
	private Mission(String title, final MenuActivity context,
			final SmartMapView mapview, Form f) {
		this.title = title;
		this.context = context;
		this.mapView = mapview;
		this.form = f;

		pointLayer = new GeometryLayer(context);
		pointLayer.setType(GeometryType.POINT);
		pointLayer.setSymbology(new PointSymbology(POINT_RADIUS, Color.BLACK));

		lineLayer = new GeometryLayer(context);
		lineLayer.setType(GeometryType.LINE);
		lineLayer.setSymbology(new LineSymbology(LINE_THICKNESS, Color.BLACK));

		polygonLayer = new GeometryLayer(context);
		polygonLayer.setType(GeometryType.POLYGON);
		polygonLayer.setSymbology(new PolygonSymbology(POLY_THICKNESS, Color.BLACK));

		survey = new Survey(mapview);

	}

	public static Mission getInstance() {
		return mission;
	}

	/**
	 * 
	 * @param name
	 *            of the mission
	 * @param context
	 *            of the application
	 * @param mapview
	 *            of the mission
	 * @return the new mission
	 */
	public static Mission createMission(String name, MenuActivity context,
			SmartMapView mapview, Form f) {
		mission = new Mission(name, context, mapview, f);
		// ecriture en base
		DbManager dbm = new DbManager();
		try {
			dbm.open(context);
			dbm.insertMission(new MissionRecord());
		} catch (SmartException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
			Log.e("", e.getMessage());
		}
		dbm.close();

		return mission;
	}

	/**
	 * Start the mission.
	 * 
	 * @return status of the mission
	 */
	public boolean startMission() {
		status = true;
		return status;
	}

	/**
	 * Stop the mission.
	 * 
	 * @return status of the mission
	 */
	public boolean stopMission() {

		DbManager dbManager = new DbManager();
		try {
			dbManager.open(context);
		} catch (SmartException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
			Log.e("", e.getMessage());
		}
		dbManager.stopMission(id);
		dbManager.close();
		status = false;
		return status;
	}

	/**
	 * Get the layer which containes the polygons of the mission.
	 * 
	 * @return the layer which containes the polygons of the mission
	 */
	public GeometryLayer getPolygonLayer() {
		return polygonLayer;
	}

	/**
	 * Get the layer which contain the lines.
	 * 
	 * @return the layer which contain the lines
	 */
	public GeometryLayer getLineLayer() {
		return lineLayer;
	}

	/**
	 * Get the which contains the points.
	 * 
	 * @return the which contains the points
	 */
	public GeometryLayer getPointLayer() {
		return pointLayer;
	}

	/**
	 * Get the form of this mission
	 * 
	 * @return the form of this mission
	 */
	public Form getForm() {
		return form;
	}

	/**
	 * Start the survey for a given geometry type.
	 * 
	 * @param type
	 *            of the survey to do
	 */
	public void startSurvey(GeometryType type) {
		if (!status) {
			return;
		}

		switch (type) {
		case LINE:
			Log.d("", "mission survey line");
			survey.startSurvey(lineLayer);
			survey.addStopListeners(new SurveyStopListener() {
				@Override
				public void actionPerformed(Geometry g) {
					form.openForm(context, g, Mission.this);
					survey.validateSurvey();
				}
			});

			break;
		case POINT:
			survey.startSurvey(pointLayer);
			survey.addStopListeners(new SurveyStopListener() {
				@Override
				public void actionPerformed(Geometry g) {
					form.openForm(context, g, Mission.this);
					survey.validateSurvey();

				}
			});

			break;
		case POLYGON:
			survey.startSurvey(polygonLayer);
			survey.addStopListeners(new SurveyStopListener() {
				@Override
				public void actionPerformed(Geometry g) {
					form.openForm(context, g, Mission.this);
					survey.validateSurvey();

				}
			});

			break;
		default:
			break;
		}
	}

	/**
	 * Get the title of the mission.
	 * 
	 * @return the title of the mission
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Get the Android context of the mission.
	 * 
	 * @return the Android context of the mission
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * Set the id of the of the mission.
	 * 
	 * @param id
	 *            the id of the mission
	 */
	public void setId(long id) {
		this.id = id;
		Log.d("", "id mission setid" + id);
	}

	/**
	 * Get the id of the mission.
	 * 
	 * @return the id of the mission
	 */
	public long getId() {
		return id;
	}

	public void removeGeometry(Geometry g) {
		if (g == null) {
			Log.d("TEST2", "Trying to remove a null geometry");
			return;
		}

		switch (g.getType()) {
		case POINT:
			pointLayer.getGeometries().remove(g);
			break;
		case LINE:
			lineLayer.getGeometries().remove(g);
			break;
		case POLYGON:
			polygonLayer.getGeometries().remove(g);
			break;
		default:
			break;
		}

		mapView.invalidate();
	}
}
