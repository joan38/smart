package fr.umlv.lastproject.smart.layers;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.views.MapView;

import fr.umlv.lastproject.smart.layers.PointSymbology.PointSymbologieType;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Region;
import android.graphics.Paint.Style;
import android.graphics.Rect;

/**
 * This class represent the line geometry to draw
 * 
 * @author Fad's
 * 
 */
public class LineGeometry extends Geometry {
	private List<PointGeometry> points;
	private boolean drawPoints = false;

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
    public void draw(MapView map, Canvas c, Boolean bool, Symbology s) {

            Paint paint = new Paint();
            paint.setColor(s.getColor());
            paint.setAlpha(150) ;           
            if (isSelected()) {
                    paint.setStrokeWidth(s.getSize() * 2);
            } else {
                    paint.setStrokeWidth(s.getSize());
            }

            // Retrieving list of points contained


            for (int j = 0; j < getPoints().size() -1 ; j++) {
                    Point a = map.getProjection().toPixels(getPoints().get(j% getPoints().size()).getCoordinates(),null);
                    Point b = map.getProjection().toPixels(getPoints().get(j+1% getPoints().size()).getCoordinates(),null);
                    c.drawLine(a.x, a.y, b.x, b.y, paint) ;
                 //   if(drawPoints ){
                	getPoints().get(j% getPoints().size()).setSelected(false);
                    getPoints().get(j% getPoints().size()).draw(map, c, bool, new PointSymbology(s.getSize() , s.getColor(), PointSymbologieType.CIRCLE ) );

                    	//   }

            }
    }

    @Override
    public boolean isSelected(MapView m, Rect click) {
            for(PointGeometry p : getPoints()){
                    if(p.isSelected(m, click)){
                            return true ;
                    }
            }
            return false;
    }
}

