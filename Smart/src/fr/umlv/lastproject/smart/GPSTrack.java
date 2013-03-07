package fr.umlv.lastproject.smart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.graphics.Color;
import android.location.LocationManager;
import fr.umlv.lastproject.smart.form.Form;
import fr.umlv.lastproject.smart.form.Mission;
import fr.umlv.lastproject.smart.layers.Geometry;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.layers.GeometryType;
import fr.umlv.lastproject.smart.layers.LineGeometry;
import fr.umlv.lastproject.smart.layers.LineSymbology;
import fr.umlv.lastproject.smart.layers.PointGeometry;
import fr.umlv.lastproject.smart.layers.PolygonGeometry;
import fr.umlv.lastproject.smart.layers.PolygonSymbology;
import fr.umlv.lastproject.smart.utils.SmartLogger;

/**
 * Main class for tracking Starts and stops a track
 * 
 * @author Marc
 * 
 */
public class GPSTrack {

	private static final int LINE_THICKNESS = 5;
	private static final int MULT = 1000;
	private final TrackMode trackMode;

	private final GPS gps;
	private final GeometryType type;
	private final List<TrackPoint> trackPoints;
	private final IGPSListener gpsListener;
	private final GeometryLayer geometryLayer;
	private boolean isFinished, isStarted;
	private final Geometry geometry;
	private final String trackName;
	private Form form = null;
	private Activity activity = null;

	private final Logger logger = SmartLogger.getLocator().getLogger();

	/**
	 * To know if we track by distance (meters) or time (seconds)
	 * 
	 * @author Marc
	 * 
	 */
	public enum TrackMode {
		TIME(1), DISTANCE(10);

		private int parameter;

		TrackMode(final int param) {
			this.parameter = param;
		}

		public int getParameter() {
			return this.parameter;
		}

		public void setParameter(int parameter) {
			this.parameter = parameter;
		}
	}

	/**
	 * 
	 * @param mode
	 * @param trackName
	 * @param lm
	 * @param mapView
	 * @param type
	 * @param layer
	 * @param form
	 * @param activity
	 * @param mission
	 */
	public GPSTrack(final TrackMode mode, final String trackName,
			final LocationManager lm, final SmartMapView mapView,
			final GeometryType type, final GeometryLayer layer,
			final Form form, final Activity activity) {

		this.geometryLayer = layer;
		this.type = type;
		this.form = form;
		this.activity = activity;
		switch (type) {
		case LINE:
			this.geometry = new LineGeometry();
			break;
		case POLYGON:
			this.geometry = new PolygonGeometry();
			break;
		default:
			geometry = null;
			break;
		}
		this.geometryLayer.addGeometry(geometry);
		isFinished = false;
		isStarted = false;
		this.trackName = trackName;
		this.trackMode = mode;
		this.gps = new GPS(lm);
		this.trackPoints = new ArrayList<TrackPoint>();
		this.gpsListener = new IGPSListener() {

			@Override
			public void actionPerformed(GPSEvent event) {
				final double latitude = event.getLatitude();
				final double longitude = event.getLongitude();
				final TrackPoint trackPoint = new TrackPoint(longitude,
						latitude, event.getAltitude(), event.getTime());
				trackPoints.add(trackPoint);
				switch (type) {
				case LINE:
					((LineGeometry) geometry).addPoint(new PointGeometry(
							latitude, longitude));
					break;
				case POLYGON:
					((PolygonGeometry) geometry).addPoint(new PointGeometry(
							latitude, longitude));
					break;
				default:
					break;
				}
			}
		};
		gps.addGPSListener(gpsListener);

	}

	/**
	 * 
	 * @param mode
	 *            of the track
	 * @param trackName
	 *            name of the track
	 * @param lm
	 *            the locationManager
	 * @param mapView
	 *            the map where will be the track
	 * @param type
	 *            the type of the geometry
	 */
	public GPSTrack(final TrackMode mode, final String trackName,
			final LocationManager lm, final SmartMapView mapView,
			final GeometryType type) {

		this.geometryLayer = new GeometryLayer(mapView.getContext());
		this.geometryLayer.setType(type);
		this.geometryLayer.setName(trackName);
		this.type = type;
		switch (type) {
		case LINE:
			this.geometry = new LineGeometry();
			this.geometryLayer.setSymbology(new LineSymbology(LINE_THICKNESS,
					Color.RED));
			break;
		case POLYGON:
			this.geometry = new PolygonGeometry();
			this.geometryLayer.setSymbology(new PolygonSymbology(
					LINE_THICKNESS, Color.RED));
			break;
		default:
			geometry = null;
			break;
		}
		this.geometryLayer.addGeometry(geometry);
		isFinished = false;
		isStarted = false;
		this.trackName = trackName;
		this.trackMode = mode;
		this.gps = new GPS(lm);
		this.trackPoints = new ArrayList<TrackPoint>();
		this.gpsListener = new IGPSListener() {

			@Override
			public void actionPerformed(GPSEvent event) {
				final double latitude = event.getLatitude();
				final double longitude = event.getLongitude();
				final TrackPoint trackPoint = new TrackPoint(longitude,
						latitude, event.getAltitude(), event.getTime());
				trackPoints.add(trackPoint);
				switch (type) {
				case LINE:
					((LineGeometry) geometry).addPoint(new PointGeometry(
							latitude, longitude));
					break;
				case POLYGON:
					((PolygonGeometry) geometry).addPoint(new PointGeometry(
							latitude, longitude));
					break;
				default:
					break;
				}
			}
		};
		gps.addGPSListener(gpsListener);

		mapView.addGeometryLayer(geometryLayer);
	}

	/**
	 * 
	 * @param mode
	 *            of the track
	 * @param trackName
	 *            name of the track
	 * @param lm
	 *            the locationManager
	 * @param mapView
	 *            the map where will be the track
	 * @param type
	 *            the type of the geometry
	 */
	public GPSTrack(final TrackMode mode, final String trackName,
			final LocationManager lm, final SmartMapView mapView,
			final GeometryType type, List<TrackPoint> points, GeometryLayer l,
			MenuActivity activity) {

		this.geometryLayer = l;
		this.type = type;
		this.activity = activity;

		switch (type) {
		case LINE:
			this.geometry = new LineGeometry();
			break;
		case POLYGON:
			this.geometry = new PolygonGeometry();
			break;
		default:
			geometry = null;
			break;
		}
		this.geometryLayer.addGeometry(geometry);
		isFinished = false;
		isStarted = false;
		this.trackName = trackName;
		this.trackMode = mode;
		this.gps = new GPS(lm);
		this.trackPoints = points;

		for (TrackPoint p : points) {
			double lat = p.getLatitude();
			double lon = p.getLongitude();

			switch (type) {
			case LINE:
				((LineGeometry) geometry).addPoint(new PointGeometry(lat, lon));
				break;
			case POLYGON:
				((PolygonGeometry) geometry).addPoint(new PointGeometry(lat,
						lon));
				break;
			default:
				break;
			}
		}

		this.gpsListener = new IGPSListener() {

			@Override
			public void actionPerformed(GPSEvent event) {
				final double latitude = event.getLatitude();
				final double longitude = event.getLongitude();
				final TrackPoint trackPoint = new TrackPoint(longitude,
						latitude, event.getAltitude(), event.getTime());
				trackPoints.add(trackPoint);
				switch (type) {
				case LINE:
					((LineGeometry) geometry).addPoint(new PointGeometry(
							latitude, longitude));
					break;
				case POLYGON:
					((PolygonGeometry) geometry).addPoint(new PointGeometry(
							latitude, longitude));
					break;
				default:
					break;
				}
			}
		};
		gps.addGPSListener(gpsListener);

	}

	/**
	 * Gets the graphics layer
	 * 
	 * @return the layer to add to the map view
	 */
	public GeometryLayer getGeometryLayer() {
		return geometryLayer;
	}

	/**
	 * List of track points that will be written in gpx file
	 * 
	 * @return List of track points
	 */
	public List<TrackPoint> getTrackPoints() {
		return trackPoints;
	}

	/**
	 * Starts track if not already started
	 */
	public void startTrack() {
		if (!isStarted && !isFinished) {
			isStarted = true;
			geometryLayer.setSelectable(false);
			switch (this.trackMode) {
			case DISTANCE:
				logger.log(Level.INFO, "Track started by meter distance");
				gps.start(0, trackMode.getParameter());
				break;

			default:
				gps.start(trackMode.getParameter() * MULT, 0);
				logger.log(Level.INFO, "Track started by time distance");
				break;
			}
		}
	}

	/**
	 * Stops track and writes gpx files
	 * 
	 * @throws IOException
	 */
	public void stopTrack() throws IOException {
		if (isStarted && !isFinished) {
			logger.log(Level.INFO, "Track stopped");
			gps.removeGPSListener(gpsListener);
			isFinished = true;
			isStarted = false;
			/** Writing of gpx file */
			switch (type) {
			case LINE:
				GPXWriter.writeGpxFile(trackName, trackPoints);
				break;

			case POLYGON:
				if (((PolygonGeometry) geometry).getPoints().size() < 1) {
					this.geometryLayer.removeGeometry(geometry);
				} else {
					Mission.getInstance()
							.getForm()
							.openForm((MenuActivity) activity, geometry,
									Mission.getInstance());
				}
				geometryLayer.setSelectable(true);
				break;

			default:
				break;
			}
		}

	}

	public boolean isFinished() {
		return isFinished;
	}

	public boolean isStarted() {
		return isStarted;
	}

	public String getName() {
		return trackName;
	}

	public TrackMode getTrackMode() {
		return trackMode;
	}

	public Geometry getGeometry() {
		return geometry;
	}

}
