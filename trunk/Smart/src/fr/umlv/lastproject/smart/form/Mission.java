package fr.umlv.lastproject.smart.form;

import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.SmartMapView;
import fr.umlv.lastproject.smart.database.DbManager;
import fr.umlv.lastproject.smart.database.MissionRecord;
import fr.umlv.lastproject.smart.layers.Geometry;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.layers.GeometryType;
import fr.umlv.lastproject.smart.layers.LineSymbology;
import fr.umlv.lastproject.smart.layers.PointGeometry;
import fr.umlv.lastproject.smart.layers.PointSymbology;
import fr.umlv.lastproject.smart.layers.PolygonSymbology;
import fr.umlv.lastproject.smart.survey.Survey;
import fr.umlv.lastproject.smart.survey.SurveyStopListener;
import fr.umlv.lastproject.smart.utils.SmartException;
import fr.umlv.lastproject.smart.utils.SmartLogger;

/**
 * This class is used to create a mission
 * 
 * @author thibault, maelle cabot
 * 
 */
public final class Mission {

	private static Mission mission = null;
	final static Logger logger = SmartLogger.getLocator().getLogger();

	private static final int POINT_RADIUS = 10;
	private static final int LINE_THICKNESS = 10;
	private static final int POLY_THICKNESS = 10;

	/* three type of survey are available : line, polygon, point */
	private GeometryLayer pointLayer;
	private GeometryLayer lineLayer;
	private GeometryLayer polygonLayer;

	private boolean trackinprogress = false;

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

		SelectedGeometryListener list = new SelectedGeometryListener() {

			@Override
			public void actionPerformed(Geometry g, GeometryLayer l) {
				setSelectable(false);
				mapview.invalidate();
				context.createModifFormDialog(getForm(), g, l, Mission.this);
				g.setSelected(false);
			}
		};

		pointLayer = new GeometryLayer(context);
		pointLayer.setType(GeometryType.POINT);
		pointLayer.setName(title + "_POINT");
		pointLayer.setSymbology(new PointSymbology());
		pointLayer.setSelectable(true);
		pointLayer.addSelectedGeometryListener(list);

		lineLayer = new GeometryLayer(context);
		lineLayer.setType(GeometryType.LINE);
		lineLayer.setName(title + "_LINE");
		lineLayer.setSymbology(new LineSymbology(LINE_THICKNESS, Color.BLACK));
		lineLayer.setSelectable(true);
		lineLayer.addSelectedGeometryListener(list);

		polygonLayer = new GeometryLayer(context);
		polygonLayer.setType(GeometryType.POLYGON);
		polygonLayer.setName(title + "_POLYGON");
		polygonLayer.setSymbology(new PolygonSymbology(POLY_THICKNESS,
				Color.BLACK));
		polygonLayer.setSelectable(true);
		polygonLayer.addSelectedGeometryListener(list);

		survey = new Survey(mapview);

	}

	/**
	 * 
	 * @param title
	 * @param menuActivity
	 * @param mapView2
	 * @param f
	 * @param missionPoint
	 * @param missionLine
	 * @param missionPolygon
	 */
	public Mission(String title, MenuActivity menuActivity,
			SmartMapView mapView2, Form f, GeometryLayer missionPoint,
			GeometryLayer missionLine, GeometryLayer missionPolygon) {

		this.title = title;
		this.context = menuActivity;
		this.mapView = mapView2;
		this.form = f;

		SelectedGeometryListener list = new SelectedGeometryListener() {

			@Override
			public void actionPerformed(Geometry g, GeometryLayer l) {
				setSelectable(false);
				mapView.invalidate();
				context.createModifFormDialog(getForm(), g, l, Mission.this);
				g.setSelected(false);
			}
		};

		pointLayer = missionPoint;
		pointLayer.setSelectable(true);
		pointLayer.addSelectedGeometryListener(list);
		removeGeometryNotSave(pointLayer);

		lineLayer = missionLine;
		lineLayer.setSelectable(true);
		lineLayer.addSelectedGeometryListener(list);
		removeGeometryNotSave(lineLayer);

		polygonLayer = missionPolygon;
		polygonLayer.setSelectable(true);
		polygonLayer.addSelectedGeometryListener(list);
		removeGeometryNotSave(polygonLayer);

		survey = new Survey(mapView);

	}

	/**
	 * 
	 * @param status
	 */
	public void trackInProgress(boolean status) {
		this.trackinprogress = status;
	}

	/**
	 * 
	 * @return
	 */
	public SmartMapView getMapView() {
		return mapView;
	}

	/**
	 * 
	 * @return mission instantiated
	 */
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
			logger.log(Level.INFO, "Mission saved in database");
		} catch (SmartException e) {
			logger.log(Level.SEVERE,
					"Mission unsaved in database " + e.getMessage());
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		dbm.close();

		return mission;
	}

	public static Mission createMission(String mname,
			MenuActivity menuActivity, SmartMapView mapView2, Form f,
			GeometryLayer missionPoint, GeometryLayer missionLine,
			GeometryLayer missionPolygon) {

		mission = new Mission(mname, menuActivity, mapView2, f, missionPoint,
				missionLine, missionPolygon);
		// ecriture en base

		return mission;

	}

	/**
	 * Start the mission.
	 * 
	 * @return status of the mission
	 */
	public boolean startMission() {
		status = true;
		logger.log(Level.INFO, "Mission " + title + " started");
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
		logger.log(Level.INFO, "Mission " + title + " stopped");
		dbManager.close();
		status = false;
		setSelectable(false);
		mission = null;
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

	public void startSurvey(PointGeometry p) {
		logger.log(Level.INFO, "Position point survey in progress");
		pointLayer.addGeometry(p);
		form.openForm(context, p, Mission.this);
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
		setSelectable(false);

		logger.log(Level.INFO, "Survey in progress");
		switch (type) {
		case LINE:
			survey.startSurvey(lineLayer);
			survey.addStopListeners(new SurveyStopListener() {
				@Override
				public void actionPerformed(Geometry g) {
					form.openForm(context, g, Mission.this);
					survey.validateSurvey();
					setSelectable(true);

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
					setSelectable(true);

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
					setSelectable(true);

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

	/**
	 * Get the status (on/off) of the mission.
	 * 
	 * @return
	 */
	public boolean isStatus() {
		return status;
	}

	/**
	 * Remove a geometry of the mission
	 * 
	 * @param g
	 *            is the geometry to remove
	 * 
	 */
	public void removeGeometry(Geometry g) {
		if (g == null) {
			logger.log(Level.WARNING, "Try to remove null geometry");
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
		logger.log(Level.INFO, "Geometry " + g.getId() + " removed");
		mapView.invalidate();
	}

	public void setSelectable(boolean b) {
		pointLayer.setSelectable(b);
		lineLayer.setSelectable(b);
		if (!trackinprogress) {
			polygonLayer.setSelectable(b);
		}
	}

	private void removeGeometryNotSave(GeometryLayer layer) {
		for (Geometry g : layer.getGeometries()) {
			if (g.getId() == -1) {
				Toast.makeText(context, R.string.survayError, Toast.LENGTH_LONG)
						.show();
				layer.removeGeometry(g);
			}
		}
	}

}
