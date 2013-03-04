package fr.umlv.lastproject.smart.layers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import fr.umlv.lastproject.smart.utils.SmartConstants;
import android.graphics.Canvas;
import android.graphics.Canvas.VertexMode;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.Log;

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
	private double latitude ;
	private double longitude ;

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

	@Override
	public void draw(MapView map, Canvas c, Boolean b, Symbology s) {
		
		Paint paint = new Paint();
		paint.setColor(s.getColor());
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setAlpha(150) ;

		// Retrieving geometry and symbology
		int radius = s.getSize();
		if(isSelected()) radius*=2 ;

		Point point = map.getProjection().toPixels(getCoordinates(), null);
		// Draws the point
		// If point is contained in the screen bounding box
		if (c.getClipBounds().contains(point.x, point.y)) {
			switch(		((PointSymbology)s).getType()){
			case CIRCLE : c.drawCircle(point.x, point.y, radius, paint); break ;
			case SQUARE : c.drawRect(new Rect(point.x - radius, point.y - radius, point.x+radius, point.y + radius), paint);
			}
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
		this.latitude =  in.readDouble() ;
		this.longitude = in.readDouble() ;
	}

}
