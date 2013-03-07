package fr.umlv.lastproject.smart.survey;

import java.util.ArrayList;
import java.util.List;

import fr.umlv.lastproject.smart.SmartMapView;
import fr.umlv.lastproject.smart.form.Mission;
import fr.umlv.lastproject.smart.layers.Geometry;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.layers.GeometryLayerDoubleTapListener;
import fr.umlv.lastproject.smart.layers.GeometryLayerSingleTapListener;
import fr.umlv.lastproject.smart.layers.LineGeometry;
import fr.umlv.lastproject.smart.layers.PointGeometry;
import fr.umlv.lastproject.smart.layers.PolygonGeometry;

/**
 * 
 * This class is use to do a survey on the map The survey could be a point, a
 * line or a polygon
 * 
 * @author thibault B
 * 
 */
public class Survey {

	private final SmartMapView mapView;
	private final List<SurveyStopListener> stopListeners;
	private GeometryLayer geometryLayer;
	private GeometryLayerDoubleTapListener dlistener;
	private GeometryLayerSingleTapListener slistener;
	private Geometry lastGeometry;

	/**
	 * 
	 * @param type
	 *            the type of the geometry which will be surveyed
	 */
	public Survey(final SmartMapView map) {
		if (map == null) {
			throw new IllegalArgumentException();
		}
		this.mapView = map;
		this.stopListeners = new ArrayList<SurveyStopListener>();
		this.lastGeometry = null;

	}

	/**
	 * this method is use to start a survey the type of survey is defined by the
	 * type of the layer
	 * 
	 * @param layer
	 *            the layer wher the survey will be
	 * 
	 */
	public void startSurvey(final GeometryLayer layer) {
		if (layer == null || layer.getType() == null) {
			throw new IllegalArgumentException();
		}
		if (this.geometryLayer != null) {
			stop();
		}

		Mission.getInstance().setSelectable(false);

		geometryLayer = layer;
		geometryLayer.setEditable(true);

		switch (geometryLayer.getType()) {
		case POINT:
			slistener = new GeometryLayerSingleTapListener() {
				@Override
				public void actionPerformed(PointGeometry p) {
					geometryLayer.addGeometry(p);
					lastGeometry = p;
					for (SurveyStopListener listener : stopListeners) {
						listener.actionPerformed(p);
					}
					geometryLayer.setEditable(false);
					mapView.invalidate();
				}
			};
			geometryLayer.addGeometryLayerSingleTapListener(slistener);
			break;
		case LINE:
			final LineGeometry l = new LineGeometry();
			slistener = new GeometryLayerSingleTapListener() {

				@Override
				public void actionPerformed(PointGeometry p) {
					geometryLayer.getGeometries().remove(l);
					l.addPoint(p);
					geometryLayer.addGeometry(l);
					lastGeometry = l;
					mapView.invalidate();
				}
			};
			geometryLayer.addGeometryLayerSingleTapListener(slistener);

			dlistener = new GeometryLayerDoubleTapListener() {

				@Override
				public void actionPerformed(PointGeometry p) {
					geometryLayer.getGeometries().remove(l);
					l.addPoint(p);
					geometryLayer.addGeometry(l);
					lastGeometry = l;
					for (SurveyStopListener listener : stopListeners) {
						listener.actionPerformed(l);
					}
					geometryLayer.setEditable(false);
					mapView.invalidate();

				}
			};
			geometryLayer.addGeometryLayerDoubleTapListener(dlistener);
			break;
		case POLYGON:
			final PolygonGeometry poly = new PolygonGeometry();

			slistener = new GeometryLayerSingleTapListener() {

				@Override
				public void actionPerformed(PointGeometry p) {
					geometryLayer.getGeometries().remove(poly);
					poly.addPoint(p);
					geometryLayer.addGeometry(poly);
					lastGeometry = poly;
					mapView.invalidate();
				}
			};
			geometryLayer.addGeometryLayerSingleTapListener(slistener);

			dlistener = new GeometryLayerDoubleTapListener() {

				@Override
				public void actionPerformed(PointGeometry p) {
					geometryLayer.getGeometries().remove(poly);
					poly.addPoint(p);
					geometryLayer.addGeometry(poly);
					lastGeometry = poly;
					for (SurveyStopListener listener : stopListeners) {
						listener.actionPerformed(poly);
					}
					geometryLayer.setEditable(false);
					mapView.invalidate();
				}
			};
			geometryLayer.addGeometryLayerDoubleTapListener(dlistener);
			break;

		default:
			break;
		}
	}

	/**
	 * 
	 * @param listener
	 *            the listener used when stop
	 */
	public void addStopListeners(SurveyStopListener listener) {
		stopListeners.add(listener);
	}

	/**
	 * 
	 * @param listener
	 *            the listener which will be unused
	 */
	public void removeStopListeners(SurveyStopListener listener) {
		stopListeners.remove(listener);
	}

	/**
	 * the survey is OK
	 */
	public void validateSurvey() {
		lastGeometry = null;
	}

	/**
	 * remove all listeners and clean the survey
	 */
	public void stop() {
		geometryLayer.removeGeometryLayerDoubleTapListener(dlistener);
		geometryLayer.removeGeometryLayerSingleTapListener(slistener);
		geometryLayer.setEditable(false);
		if (lastGeometry != null) {
			geometryLayer.getGeometries().remove(lastGeometry);
		}

		stopListeners.clear();
		lastGeometry = null;
		Mission.getInstance().setSelectable(true);

	}
}
