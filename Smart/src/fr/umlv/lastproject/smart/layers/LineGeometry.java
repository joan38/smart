package fr.umlv.lastproject.smart.layers;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.views.MapView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Region;
import android.graphics.Paint.Style;
import android.graphics.Path.FillType;
import android.graphics.Rect;
import fr.umlv.lastproject.smart.SmartMapView;
import fr.umlv.lastproject.smart.form.SelectedGeometryListener;

/**
 * This class represent the line geometry to draw
 * 
 * @author Fad's
 * 
 */
public class LineGeometry extends Geometry {
	private List<PointGeometry> points;

	public LineGeometry() {
		this(new ArrayList<PointGeometry>());
	}

	/**
	 * Line constructor
	 * 
	 * @param points
	 *            : list of points to draw
	 */
	public LineGeometry(List<PointGeometry> points) {
		this.points = points;
		setType(GeometryType.LINE);

	}

	/**
	 * Starts a line at the point of coords (latitude,longitude)
	 * 
	 * @param latitude
	 *            : latitude of the point
	 * @param longitude
	 *            : longitude of the point
	 */
	public LineGeometry(double latitude, double longitude) {
		this();
		this.points.add(new PointGeometry(latitude, longitude));

	}

	/**
	 * Function which add point to the list
	 * 
	 * @param point
	 *            : point to add
	 */
	public void addPoint(PointGeometry point) {
		this.points.add(point);
	}

	/**
	 * Function which return all points contained in the geometry
	 * 
	 * @return the list of points
	 */
	public List<PointGeometry> getPoints() {
		return this.points;
	}

	@Override
	public void draw(MapView map, Canvas c, Boolean b, Symbology s) {

		Paint paint = new Paint();
		paint.setColor(s.getColor());
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setAlpha(150) ;

		Path p = new Path() ;
		
		if (isSelected()) {
			paint.setStrokeWidth(s.getSize() * 2);
		} else {
			paint.setStrokeWidth(s.getSize());
		}

		// Retrieving list of points contained


		for (int j = 0; j < getPoints().size() ; j++) {
			// on récupere les 2 points

			// Converting coordinates in pixel
			Point pixelA = map.getProjection().toPixels(getPoints().get(j% getPoints().size()).getCoordinates(),null);
			if(j==0) p.moveTo(pixelA.x, pixelA.y);

			p.lineTo(pixelA.x, pixelA.y);
		}
		p.close();
		c.drawPath(p, paint);
	}

	@Override
	public boolean isSelected(MapView m, Rect click) {

		Path path = new Path() ;
		Region clip = new Region(m.getProjection().toPixels(m.getBoundingBox())) ;
		for(int i =0; i < getPoints().size() ; i++){
			Point ps = m.getProjection().toPixels(getPoints().get(i).getCoordinates(),null);
			if(i==0) path.moveTo(ps.x, ps.y);
			else path.lineTo(ps.x, ps.y);							
		}
		clip.setPath(path, clip);
		if(!clip.quickReject(click)){
			return true ;
		}
		return false;
	}
}
