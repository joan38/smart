package fr.umlv.lastproject.smart.layers;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.Projection;
import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.form.SelectedGeometryListener;

/**
 * This class represent the geometry layer and draw it if it is contained on the
 * screen
 * 
 * @author Fad's, thibault brun
 * 
 */
public class GeometryLayer extends Overlay implements Layer {

	/**
	 * To work with OSMDROID
	 */
	private static final double VALUE_1E6 = 1E6;
	private GeometryType type;
	private final List<Geometry> geometries;
	private Paint paint;
	private Symbology symbology;
	private boolean editable = false;
	private String name;
	private final List<GeometryLayerSingleTapListener> singleTapListeners;
	private final List<GeometryLayerDoubleTapListener> doubleTapListeners;
	private final List<SelectedGeometryListener> selectedListener;
	private final Context context;
	private boolean isSelectable = false;

	private static final String DEFAULT_NAME = "default";

	private static final float radius = 12;
	private static final float lineSize = 12;
	private static final float rectSize = 12;
	private static final float strokeWidth = 5;

	/**
	 * 
	 * @param ctx
	 *            : context for the GeometryLayer
	 * 
	 */
	public GeometryLayer(Context ctx) {
		this(ctx, new ArrayList<Geometry>());

	}

	public GeometryLayer(final Context ctx, List<Geometry> geometries) {
		this(ctx, geometries, GeometryType.POLYGON, new PolygonSymbology(),
				DEFAULT_NAME);

	}

	public GeometryLayer(final Context ctx, List<Geometry> geometries,
			GeometryType type, Symbology symbologie, String name) {
		super(ctx);
		this.context = ctx;
		this.geometries = geometries;
		this.type = type;
		this.symbology = symbologie;
		this.name = name;
		this.doubleTapListeners = new ArrayList<GeometryLayerDoubleTapListener>();
		this.singleTapListeners = new ArrayList<GeometryLayerSingleTapListener>();
		this.selectedListener = new ArrayList<SelectedGeometryListener>();
	}

	/**
	 * Gets all the geometries contained in this layer
	 * 
	 * @return {@link List}<{@link Geometry}> contained
	 */
	public List<Geometry> getGeometries() {
		return geometries;
	}

	/**
	 * Edits symbology
	 */
	public void editSymbology(int size, int color) {

		symbology.setColor(color);
		symbology.setSize(size);

	}

	/**
	 * Adds geometry to the geometry list
	 * 
	 * @param geometry
	 */
	public void addGeometry(Geometry geometry) {
		this.geometries.add(geometry);
	}

	/**
	 * Function which add all geometries to the geometries list
	 * 
	 * @param geometry
	 */
	public void addGeometries(List<Geometry> geometries) {
		this.geometries.addAll(geometries);
	}

	/**
	 * Function which set a type to the geometry
	 * 
	 * @param type
	 */
	public void setType(GeometryType type) {
		this.type = type;
	}

	/**
	 * Gets the {@link GeometryType}
	 * 
	 * @return the type
	 */
	public GeometryType getType() {
		return type;
	}

	/**
	 * Gives a name to the layer
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the name of the layer
	 * 
	 * @return {@link String}
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Function which set a symbology to the geometry
	 * 
	 * @param symbology
	 */
	public void setSymbology(Symbology symbology) {
		this.symbology = symbology;
	}

	/**
	 * Function which return the symbology for the geometry
	 * 
	 * @return the symbology of geometry
	 */
	public Symbology getSymbology() {
		return this.symbology;
	}

	/**
	 * Function which test if the geometry is contained in the boundingBox
	 * 
	 * @param clipBound
	 *            : boundingBox of the screen
	 * @param geometryBoundingBox
	 *            : boundingBox of the geometry
	 * @return true the geometry is contened in the screen else false
	 */
	public boolean isInBoundingBox(Rect clipBound, Rect geometryBoundingBox) {
		return (clipBound.contains(geometryBoundingBox) || geometryBoundingBox
				.contains(clipBound));
	}

	/**
	 * Function which draw the geometry if it is contained in the boundingBox
	 */
	@Override
	protected void draw(Canvas canvas, MapView mapView, boolean b) {
		Projection projection = mapView.getProjection();
		paint = new Paint();
		paint.setColor(getSymbology().getColor());

		for (Geometry geometry : geometries) {

			switch (type) {
			case POINT:
				// Retrieving geometry and symbology
				final PointGeometry pointGeometry = (PointGeometry) geometry;
				// final PointSymbology pointSymbology = (PointSymbology)
				// symbology;
				final int radius = symbology.getSize();

				// final PointSymbology pointSymbology = (PointSymbology)
				// symbology;
				// int radius = pointSymbology.getRadius();
				// if(geometry.isSelected()) radius *=2;
				// If point is contained in the screen bounding box
				// Transform coordinates (lat/long) in pixels

				Point point = projection.toPixels(
						pointGeometry.getCoordinates(), null);
				// Draws the point
				// If point is contained in the screen bounding box
				if (canvas.getClipBounds().contains(point.x, point.y)) {
					canvas.drawCircle(point.x, point.y, radius, paint);

				}
				break;

			case LINE:
				// Retrieving geometry and symbology
				final LineGeometry lineGeometry = (LineGeometry) geometry;
				// final LineSymbology lineSymbology = (LineSymbology)
				// symbology;
				// paint.setStrokeWidth(symbology.getSize());

				if (geometry.isSelected()) {
					paint.setStrokeWidth(symbology.getSize() * 2);
				} else {
					paint.setStrokeWidth(symbology.getSize());
				}
				// Retrieving list of points contained
				final List<PointGeometry> linePoints = lineGeometry.getPoints();

				for (int j = 0; j < linePoints.size() - 1; j++) {

					PointGeometry pointA = linePoints.get(j);
					PointGeometry pointB = linePoints.get(j + 1);

					Point pointT = projection.toPixels(pointA.getCoordinates(),
							null);
					// Projection des coordonn�es en pixel

					Point pixelA = projection.toPixels(pointA.getCoordinates(),
							null);
					Point pixelB = projection.toPixels(pointB.getCoordinates(),
							null);

					Point pointD = projection.toPixels(pointB.getCoordinates(),
							null);
					// Dessine la geometrie si elle est contenue dans la
					// boundingBox
					if (isInBoundingBox(
							canvas.getClipBounds(),
							new Rect(Math.max(pixelA.x, pixelB.x), Math.max(
									pixelA.y, pixelB.y), Math.min(pixelA.x,

							pixelB.x), Math.min(pixelA.y, pixelB.y)))) {

						canvas.drawLine(pointT.x, pointT.y, pointD.x, pointD.y,
								paint);

						canvas.drawLine(pixelA.x, pixelA.y, pixelB.x, pixelB.y,
								paint);
					}
				}

				break;

			case POLYGON:

				// Retrieving geometry and symbology
				PolygonGeometry polygonGeometry = (PolygonGeometry) geometry;

				// PolygonSymbology polygonSymbology = (PolygonSymbology)
				// symbology;

				// PolygonSymbology polygonSymbology = (PolygonSymbology)
				// symbology;
				if (geometry.isSelected()) {
					paint.setStrokeWidth(symbology.getSize() * 2);
				} else {
					paint.setStrokeWidth(symbology.getSize());
				}

				// Retrieving list of points contained
				List<PointGeometry> polygonPoints = polygonGeometry.getPoints();

				for (int j = 0; j < polygonPoints.size(); j++) {

					PointGeometry pointA = polygonPoints.get(j
							% polygonPoints.size());
					PointGeometry pointB = polygonPoints.get((j + 1)
							% polygonPoints.size());

					// Converting coordinates in pixel
					Point pixelA = projection.toPixels(pointA.getCoordinates(),
							null);
					Point pixelB = projection.toPixels(pointB.getCoordinates(),
							null);

					// Draws the geometry if it is contained in the bounding box
					if (isInBoundingBox(
							canvas.getClipBounds(),
							new Rect(Math.max(pixelA.x, pixelB.x), Math.max(
									pixelA.y, pixelB.y), Math.min(pixelA.x,
									pixelB.x), Math.min(pixelA.y, pixelB.y)))) {

						canvas.drawLine(pixelA.x, pixelA.y, pixelB.x, pixelB.y,
								paint);

					}
				}
				break;

			default:
				break;
			}
		}
	}

	/**
	 * Gets the {@link GeometryType} of the layer
	 */
	@Override
	public String toString() {
		return type.toString();
	}

	/**
	 * Indicates if we want the layer to be editable or not
	 * 
	 * @param editable
	 * 
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * Usually used to stop an edition
	 */
	@Override
	public boolean onDoubleTap(MotionEvent e, MapView mapView) {

		if (editable) {
			float x = e.getX();

			float y = e.getY();
			if (e.getPointerCount() > 1) {
				float x0 = e.getX(0);
				float x1 = e.getX(1);
				float y0 = e.getY(0);
				float y1 = e.getY(1);
				// If we tapped really quickly on the map but not at the same
				// coordinate, then we want to add two points
				if (Math.abs(x1 - x0) < 5 && Math.abs(y1 - y0) < 5) {

					final IGeoPoint firstPoint = mapView.getProjection()
							.fromPixels(x0, y0);
					final float firstLatitude = (float) (firstPoint
							.getLatitudeE6() / VALUE_1E6);
					final float firstLongitude = (float) (firstPoint
							.getLongitudeE6() / VALUE_1E6);

					for (int i = 0; i < singleTapListeners.size(); i++) {
						singleTapListeners.get(i)
								.actionPerformed(
										new PointGeometry(firstLatitude,
												firstLongitude));
					}
					final IGeoPoint secondPoint = mapView.getProjection()
							.fromPixels(x1, y1);
					final float secondLatitude = (float) (secondPoint
							.getLatitudeE6() / VALUE_1E6);
					final float secondLongitude = (float) (secondPoint
							.getLongitudeE6() / VALUE_1E6);

					for (int i = 0; i < singleTapListeners.size(); i++) {
						singleTapListeners.get(i).actionPerformed(
								new PointGeometry(secondLatitude,
										secondLongitude));
					}
					return super.onDoubleTap(e, mapView);
				}

			}

			final IGeoPoint point = mapView.getProjection().fromPixels(x, y);
			float latitude = (float) (point.getLatitudeE6() / VALUE_1E6);
			float longitude = (float) (point.getLongitudeE6() / VALUE_1E6);

			for (int i = 0; i < singleTapListeners.size(); i++) {
				doubleTapListeners.get(i).actionPerformed(
						new PointGeometry(latitude, longitude));
			}
		}

		return super.onDoubleTap(e, mapView);
	}

	/**
	 * Adds a single point
	 */
	@Override
	public boolean onSingleTapUp(MotionEvent e, MapView m) {

		// mapView.invalidate();

		final float x = e.getX();
		final float y = e.getY();
		final IGeoPoint point = m.getProjection().fromPixels(x, y);
		float latitude = (float) (point.getLatitudeE6() / VALUE_1E6);
		float longitude = (float) (point.getLongitudeE6() / VALUE_1E6);

		if (editable) {
			for (int i = 0; i < singleTapListeners.size(); i++) {
				singleTapListeners.get(i).actionPerformed(
						new PointGeometry(latitude, longitude));
			}
		} else {
			if (isSelectable) {

				// m.getController().zoomToSpan(new )
				Point ref = m.getProjection().toPixels(point, null);
				Rect r = new Rect(ref.x - 50, ref.y - 50, ref.x + 50,
						ref.y + 50);
				Log.d("", "Selected " + r.toString());

				for (Geometry g : geometries) {
					switch (type) {
					case LINE: {
						LineGeometry l = (LineGeometry) g;
						for (PointGeometry p : l.getPoints()) {
							Point ps = m.getProjection().toPixels(
									p.getCoordinates(), null);

							if (r.contains(ps.x, ps.y)) {
								Log.d("", "Selected" + g.getId());
								g.setSelected(true);
								for (SelectedGeometryListener lis : selectedListener) {
									lis.actionPerformed(g,this);
								}
								return super.onSingleTapUp(e, m);
							}
						}
						break;
					}
					case POINT: {
						PointGeometry p = (PointGeometry) g;
						Point ps = m.getProjection().toPixels(
								p.getCoordinates(), null);

						Log.d("", "Select bb " + ps.toString());

						if (r.contains(ps.x, ps.y)) {
							Log.d("", "Selected" + g.getId());
							p.setSelected(true);
							for (SelectedGeometryListener lis : selectedListener) {
								lis.actionPerformed(g,this);
							}

							return super.onSingleTapUp(e, m);
						}
						break;
					}
					case POLYGON: {
						PolygonGeometry l = (PolygonGeometry) g;
						for (PointGeometry p : l.getPoints()) {
							Point ps = m.getProjection().toPixels(
									p.getCoordinates(), null);

							if (r.contains(ps.x, ps.y)) {
								Log.d("", "Selected" + g.getId());
								g.setSelected(true);
								for (SelectedGeometryListener lis : selectedListener) {
									lis.actionPerformed(g,this);
								}
								return super.onSingleTapUp(e, m);
							}
						}
						break;
					}
					}
				}
				m.invalidate();
			}

			// geometries
		}
		return super.onSingleTapUp(e, m);
	}

	/**
	 * Add a listener for adding points
	 * 
	 * @param listener
	 *            the listener which will listen
	 */
	public void addGeometryLayerSingleTapListener(
			GeometryLayerSingleTapListener listener) {
		singleTapListeners.add(listener);
	}

	/**
	 * 
	 * Removes a listener if exists or does nothing if not
	 * 
	 * @param listener
	 *            the listener wich will listen
	 */
	public void removeGeometryLayerSingleTapListener(
			GeometryLayerSingleTapListener listener) {
		singleTapListeners.remove(listener);
	}

	/**
	 * 
	 * Adds a double tap listener
	 * 
	 * @param listener
	 *            the listener wich will listen
	 */
	public void addGeometryLayerDoubleTapListener(
			GeometryLayerDoubleTapListener listener) {
		doubleTapListeners.add(listener);
	}

	/**
	 * Removes a listener if exists or does nothing if not
	 * 
	 * @param listener
	 *            the listener which will listen
	 */
	public void removeGeometryLayerDoubleTapListener(
			GeometryLayerDoubleTapListener listener) {
		doubleTapListeners.remove(listener);
	}

	/**
	 * Generates an overview with the symbology parameter
	 */
	@Override
	public Bitmap getOverview() {

		final Bitmap bit = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.geometry_blank);

		Bitmap bitmap = bit.copy(Config.ARGB_8888, true);

		int height = (bitmap.getHeight());
		int width = (bitmap.getWidth());
		int middlex = (bitmap.getHeight() / 2);
		int middley = (bitmap.getWidth() / 2);

		final Canvas canvas = new Canvas(bitmap);

		final Paint paint = new Paint();

		paint.setColor(symbology.getColor());
		switch (type) {
		case POINT:
			canvas.drawCircle(middlex, middley, radius, paint);
			break;
		case LINE:
			paint.setStrokeWidth(strokeWidth);
			canvas.drawLine(lineSize, lineSize, height - lineSize, width
					- lineSize, paint);
			break;
		case POLYGON:
			canvas.drawRect(rectSize, rectSize, height - rectSize, width
					- rectSize, paint);
			break;
		default:
			break;
		}
		return bitmap;

	}

	/**
	 * Cast it as an overlay
	 */
	@Override
	public Overlay getOverlay() {
		return this;
	}

	@Override
	public boolean hasSymbologyEditable() {
		return true;
	}

	/**
	 * 
	 * @param b
	 *            is selectable or not
	 */
	public void setSelectable(boolean b) {
		isSelectable = b;
	}

	/**
	 * 
	 * @return true if the layer is selectable or false
	 */
	public boolean isSelectable() {
		return isSelectable;
	}

	/**
	 * 
	 * @param l
	 *            the listener to add
	 */
	public void addSelectedGeometryListener(SelectedGeometryListener l) {
		selectedListener.add(l);
	}

	/**
	 * 
	 * @param l
	 *            the listener to remove
	 */
	public void removeGeometryListener(SelectedGeometryListener l) {
		selectedListener.remove(l);
	}
	
	public void removeGeometry(Geometry g){
		geometries.remove(g);
		
	}

}
