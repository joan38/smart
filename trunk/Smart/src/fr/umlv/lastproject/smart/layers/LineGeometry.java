package fr.umlv.lastproject.smart.layers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.MapView;

import fr.umlv.lastproject.smart.layers.PointSymbology.PointSymbologieType;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Region;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Parcel;

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
    public void draw(MapView map, Canvas c, Boolean bool, Symbology s) {

            Paint paint = new Paint();
            paint.setColor(s.getColor());
            paint.setAlpha(150) ;           
            if (isSelected()) {
                    paint.setStrokeWidth(s.getSize() * 2);
            } else {
                    paint.setStrokeWidth(s.getSize());
            }



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
    
    
    /**
   	 * 
   	 * @param out
   	 *            the object to get
   	 * @throws IOException
   	 *             if canot read
   	 */
   	private void writeObject(ObjectOutputStream out) throws IOException {
   		out.writeObject(points);
   		out.writeBoolean(isSelected());
   		out.writeLong(getId()) ;
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
   		
   		this.points = (List<PointGeometry>)  in.readObject();
   		this.setSelected(in.readBoolean()) ;
   		this.setId(in.readLong()) ;
   		this.setType(GeometryType.LINE) ;
   		this.setSymbology((Symbology)in.readObject()) ;
   	}

	@Override
	public BoundingBoxE6 getBoundingBox() {

		double north= 90 ;
		double south = -90 ;
		double east = 180;
		double west = -180 ;

		if(points.size() >0){
			north = points.get(0).getBoundingBox().getLatNorthE6() /1E6; 
			south = points.get(0).getBoundingBox().getLatSouthE6()/1E6; 
			east  = points.get(0).getBoundingBox().getLonEastE6()/1E6;
			west  = points.get(0).getBoundingBox().getLonWestE6()/1E6;
		}


		for(PointGeometry g : points){
			BoundingBoxE6 tmp = g.getBoundingBox() ;
			north = (north <  tmp.getLatNorthE6()/1E6 ?  tmp.getLatNorthE6() /1E6: north) ;
			south = (south > tmp.getLatSouthE6()/ 1E6 ? tmp.getLatSouthE6()/1E6 : south) ;
			east = (east < tmp.getLonEastE6()/1E6 ? tmp.getLonEastE6()/1E6 : east) ;
			west = (west < tmp.getLonWestE6() /1E6? tmp.getLonWestE6()/1E6 : west) ;
		}

		return  new BoundingBoxE6(north, east, south, west);	
	}

    
    
}

