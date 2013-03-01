package fr.umlv.lastproject.smart.layers;

import java.util.ArrayList;
import java.util.List;
import org.osmdroid.views.MapView;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Paint.Style;

/**
 * This class represent the Polygon geometry to draw
 * 
 * @author Fad's
 * 
 */
public class PolygonGeometry extends Geometry {
	private final List<PointGeometry> points;

	public PolygonGeometry() {
		this(new ArrayList<PointGeometry>());
	}

	/**
	 * Polygon constructor
	 * 
	 * @param points
	 *            : list of points to draw
	 */
	public PolygonGeometry(List<PointGeometry> points) {
		this.points = points;
		setType(GeometryType.POLYGON);

	}

	/**
	 * Polygon constructor
	 * 
	 * @param latitude
	 *            : latitude of the point
	 * @param longitude
	 *            : longitude of the point
	 */
	public PolygonGeometry(double latitude, double longitude) {
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
            paint.setStyle(Style.FILL_AND_STROKE);
            paint.setStrokeWidth(s.getSize());
            paint.setColor(s.getColor());


            Path p = new Path() ;
            if (isSelected()) {
                paint.setAlpha(220) ;
            } else {
                paint.setAlpha(150) ;

            }

            for (int j = 0; j < getPoints().size() +1; j++) {
                    // on récupere les 2 points
                    PointGeometry pointA = getPoints().get(j
                                    % getPoints().size());

                    // Converting coordinates in pixel
                    Point pixelA = map.getProjection().toPixels(pointA.getCoordinates(),
                                    null);
                    if(j==0) p.moveTo(pixelA.x, pixelA.y);

                    p.lineTo(pixelA.x, pixelA.y);
            }
            p.close();
            c.drawPath(p, paint);
            
    }

    @Override
    public boolean isSelected(MapView m, Rect click) {
            
            Region clip = new Region(m.getProjection().toPixels(m.getBoundingBox())) ;
            Path p = new Path() ;
            for (int j = 0; j < getPoints().size() +1; j++) {
                    if(getPoints().get(j%getPoints().size()).isSelected(m, click)){
                            return true ;
                    }
                    Point ps = m.getProjection().toPixels(getPoints().get(j%getPoints().size()).getCoordinates(),null);
                    if(j==0) p.moveTo(ps.x, ps.y);
                    p.lineTo(ps.x, ps.y);
            }
            p.close();
            clip.setPath(p, clip);
            if(!clip.quickReject(click)   ){
                    return true ;
            }
            
            
            
            
            return false;
    }
}
