package fr.umlv.lastproject.smart.layers;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.Rect;

/**
 * This class represent the Point geometry to draw
 * 
 * @author Fad's
 * 
 */
public class PointGeometry extends Geometry {
	private GeoPoint coordinates;

	/**
	 * Point constructor
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public PointGeometry(double latitude, double longitude) {
		this.coordinates = new GeoPoint(latitude, longitude);
		setType(GeometryType.POINT);

	}

	/**
	 * Function which return the Point coordinates
	 * 
	 * @return coordinates
	 */
	public GeoPoint getCoordinates() {
		return this.coordinates;

	}

	/**
	 * Function which return the Point latitude
	 * 
	 * @return latitude
	 */
	public float getLatitude() {
		return coordinates.getLatitudeE6();
	}

	/**
	 * Function which return the Point longitude
	 * 
	 * @return longitude
	 */
	public float getLongitude() {
		return coordinates.getLongitudeE6();
	}

	@Override
	public void draw(MapView map, Canvas c, Boolean b, Symbology s) {
		
		Paint paint = new Paint();
		paint.setColor(s.getColor());
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setAlpha(150) ;

		// Retrieving geometry and symbology
		final int radius = s.getSize();

		Point point = map.getProjection().toPixels(getCoordinates(), null);
		// Draws the point
		// If point is contained in the screen bounding box
		if (c.getClipBounds().contains(point.x, point.y)) {
			c.drawCircle(point.x, point.y, radius, paint);
		}		
	}

	@Override
	public boolean isSelected(MapView m, Rect click) {
		Point ps = m.getProjection().toPixels(
				getCoordinates(), null);
		if (click.contains(ps.x, ps.y)) {
			setSelected(true);
			return true ;
		}
		return false;
	}

}
