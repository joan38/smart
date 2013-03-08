package fr.umlv.lastproject.smart.layers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * This class represent the Point geometry to draw
 * 
 * @author Fad's
 * 
 */
public class PointGeometry extends Geometry {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7731488440345191629L;
	private double latitude;
	private double longitude;

	/**
	 * Point constructor
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public PointGeometry(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		setType(GeometryType.POINT);

	}

	/**
	 * Function which return the Point coordinates
	 * 
	 * @return coordinates
	 */
	public GeoPoint getCoordinates() {
		return new GeoPoint(latitude, longitude);
	}

	/**
	 * Function which return the Point latitude
	 * 
	 * @return latitude
	 */
	public float getLatitude() {
		GeoPoint a = new GeoPoint(latitude, longitude);
		return a.getLatitudeE6();
	}

	/**
	 * Function which return the Point longitude
	 * 
	 * @return longitude
	 */
	public float getLongitude() {
		GeoPoint a = new GeoPoint(latitude, longitude);
		return a.getLongitudeE6();
	}

	private static final int ALPHA = 150;

	@Override
	public void draw(MapView map, Canvas c, Boolean b, Symbology s) {

		Paint paint = new Paint();
		paint.setColor(s.getColor());
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setAlpha(ALPHA);

		// Retrieving geometry and symbology
		int radius = s.getSize();
		if (isSelected()) {
			radius *= 2;
		}
		Point point = map.getProjection().toPixels(getCoordinates(), null);
		// Draws the point
		// If point is contained in the screen bounding box
		if (c.getClipBounds().contains(point.x, point.y)) {
			switch (((PointSymbology) s).getType()) {
			case CIRCLE:
				c.drawCircle(point.x, point.y, radius, paint);
				break;
			case SQUARE:
				c.drawRect(new Rect(point.x - radius, point.y - radius, point.x
						+ radius, point.y + radius), paint);
			}
		}
	}

	@Override
	public boolean isSelected(MapView m, Rect click) {
		Point ps = m.getProjection().toPixels(getCoordinates(), null);
		if (click.contains(ps.x, ps.y)) {
			setSelected(true);
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param out
	 *            the object to get
	 * @throws IOException
	 *             if canot read
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeDouble(latitude);
		out.writeDouble(longitude);
		out.writeBoolean(isSelected());
		out.writeLong(getId());
		out.writeObject(getSymbology());
	}

	/**
	 * 
	 * @param in
	 *            object to read
	 * @throws IOException
	 *             if object not readable
	 * @throws ClassNotFoundException
	 *             if class does not exist
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		this.latitude = in.readDouble();
		this.longitude = in.readDouble();
		this.setSelected(in.readBoolean());
		this.setId(in.readLong());
		this.setType(GeometryType.POINT);
		this.setSymbology((Symbology) in.readObject());
	}

	@Override
	public BoundingBoxE6 getBoundingBox() {
		return new BoundingBoxE6(latitude, longitude, latitude, longitude);
	}

}
