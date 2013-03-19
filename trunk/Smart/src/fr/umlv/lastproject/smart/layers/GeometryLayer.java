package fr.umlv.lastproject.smart.layers;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import fr.umlv.lastproject.smart.form.SelectedGeometryListener;

/**
 * This class represent the geometry layer and draw it if it is contained on the
 * screen
 * 
 * @author Fad's, thibault brun
 * 
 */
public class GeometryLayer extends Overlay implements Layer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime
				* result
				+ ((doubleTapListeners == null) ? 0 : doubleTapListeners
						.hashCode());
		result = prime * result + (editable ? 1231 : 1237);
		result = prime * result
				+ ((geometries == null) ? 0 : geometries.hashCode());
		result = prime * result + (isSelectable ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((paint == null) ? 0 : paint.hashCode());
		result = prime
				* result
				+ ((selectedListener == null) ? 0 : selectedListener.hashCode());
		result = prime
				* result
				+ ((singleTapListeners == null) ? 0 : singleTapListeners
						.hashCode());
		result = prime * result
				+ ((symbology == null) ? 0 : symbology.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GeometryLayer other = (GeometryLayer) obj;
		if (context == null) {
			if (other.context != null) {
				return false;
			}
		} else if (!context.equals(other.context)) {
			return false;
		}
		if (doubleTapListeners == null) {
			if (other.doubleTapListeners != null) {
				return false;
			}
		} else if (!doubleTapListeners.equals(other.doubleTapListeners)) {
			return false;
		}
		if (editable != other.editable) {
			return false;
		}
		if (geometries == null) {
			if (other.geometries != null) {
				return false;
			}
		} else if (!geometries.equals(other.geometries)) {
			return false;
		}
		if (isSelectable != other.isSelectable) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (paint == null) {
			if (other.paint != null) {
				return false;
			}
		} else if (!paint.equals(other.paint)) {
			return false;
		}
		if (selectedListener == null) {
			if (other.selectedListener != null) {
				return false;
			}
		} else if (!selectedListener.equals(other.selectedListener)) {
			return false;
		}
		if (singleTapListeners == null) {
			if (other.singleTapListeners != null) {
				return false;
			}
		} else if (!singleTapListeners.equals(other.singleTapListeners)) {
			return false;
		}
		if (symbology == null) {
			if (other.symbology != null) {
				return false;
			}
		} else if (!symbology.equals(other.symbology)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

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
	private static final int BUFFER = 100;
	private static final String DEFAULT_NAME = "default";


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
		for (Geometry geometry : geometries) {
			geometry.draw(mapView, canvas, b, getSymbology());
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

			final IGeoPoint point = mapView.getProjection().fromPixels(x, y);
			float latitude = (float) (point.getLatitudeE6() / VALUE_1E6);
			float longitude = (float) (point.getLongitudeE6() / VALUE_1E6);

			for (int i = 0; i < singleTapListeners.size(); i++) {
				doubleTapListeners.get(i).actionPerformed(
						new PointGeometry(latitude, longitude));
			}
			return true;
		}

		return super.onDoubleTap(e, mapView);
	}

	/**
	 * Adds a single point
	 */
	@Override
	public boolean onSingleTapUp(MotionEvent e, MapView m) {

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
				Point ref = m.getProjection().toPixels(point, null);
				Rect r = new Rect(ref.x - BUFFER / 2, ref.y - BUFFER / 2, ref.x
						+ BUFFER / 2, ref.y + BUFFER / 2);
				for (Geometry g : geometries) {
					if (g.isSelected(m, r)) {
						for (SelectedGeometryListener lis : selectedListener) {
							lis.actionPerformed(g, this);
							g.setSelected(true);

							return super.onSingleTapUp(e, m);
						}
					}
				}
			}
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
		return symbology.getOverview(context);

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

	/**
	 * 
	 * @param g
	 *            the geometry to remove
	 */
	public void removeGeometry(Geometry g) {
		geometries.remove(g);

	}

	private static final double NORTH = 90;
	private static final double SOUTH = -90;
	private static final double EAST = 180;
	private static final double WEST = -180;

	@Override
	public Extent getExtent() {
		double north = NORTH;
		double south = SOUTH;
		double east = EAST;
		double west = WEST;

		if (geometries.size() > 0) {
			north = geometries.get(0).getBoundingBox().getLatNorthE6()
					/ VALUE_1E6;
			south = geometries.get(0).getBoundingBox().getLatSouthE6()
					/ VALUE_1E6;
			east = geometries.get(0).getBoundingBox().getLonEastE6()
					/ VALUE_1E6;
			west = geometries.get(0).getBoundingBox().getLonWestE6()
					/ VALUE_1E6;
		}

		for (Geometry g : geometries) {
			BoundingBoxE6 tmp = g.getBoundingBox();
			north = (north < tmp.getLatNorthE6() / VALUE_1E6 ? tmp
					.getLatNorthE6() / VALUE_1E6 : north);
			south = (south > tmp.getLatSouthE6() / VALUE_1E6 ? tmp
					.getLatSouthE6() / VALUE_1E6 : south);
			east = (east < tmp.getLonEastE6() / VALUE_1E6 ? tmp.getLonEastE6()
					/ VALUE_1E6 : east);
			west = (west < tmp.getLonWestE6() / VALUE_1E6 ? tmp.getLonWestE6()
					/ VALUE_1E6 : west);
		}

		return new Extent(new BoundingBoxE6(north, east, south, west));
	}

}
