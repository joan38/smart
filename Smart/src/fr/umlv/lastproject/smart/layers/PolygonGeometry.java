package fr.umlv.lastproject.smart.layers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.MapView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;

/**
 * This class represent the Polygon geometry to draw
 * 
 * @author Fad's
 * 
 */
public class PolygonGeometry extends Geometry {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<PointGeometry> points;

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

	private static final int ALPHA_SELECTED = 220;
	private static final int ALPHA_UNSELECTED = 150;

	@Override
	public void draw(MapView map, Canvas c, Boolean b, Symbology s) {
		if (points.size() < 2) {
			return;
		}
		Paint paint = new Paint();
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setStrokeWidth(s.getSize());
		paint.setColor(s.getColor());

		Path p = new Path();
		if (isSelected()) {
			paint.setAlpha(ALPHA_SELECTED);
		} else {
			paint.setAlpha(ALPHA_UNSELECTED);
		}
		for (int j = 0; j < getPoints().size() + 1; j++) {
			// on récupere les 2 points
			PointGeometry pointA = getPoints().get(j % getPoints().size());

			// Converting coordinates in pixel
			Point pixelA = map.getProjection().toPixels(
					pointA.getCoordinates(), null);
			if (j == 0) {
				p.moveTo(pixelA.x, pixelA.y);
			}

			p.lineTo(pixelA.x, pixelA.y);

		}
		p.close();
		c.drawPath(p, paint);

	}

	@Override
	public boolean isSelected(MapView m, Rect click) {
		if (points.size() < 2) {
			return false;
		}
		Region clip = new Region(m.getProjection().toPixels(m.getBoundingBox()));
		Path p = new Path();
		for (int j = 0; j < getPoints().size() + 1; j++) {
			if (getPoints().get(j % getPoints().size()).isSelected(m, click)) {
				return true;
			}
			Point ps = m.getProjection().toPixels(
					getPoints().get(j % getPoints().size()).getCoordinates(),
					null);
			if (j == 0) {
				p.moveTo(ps.x, ps.y);
			}
			p.lineTo(ps.x, ps.y);
		}
		p.close();
		clip.setPath(p, clip);
		if (!clip.quickReject(click)) {
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
		out.writeObject(points);
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

		this.points = (List<PointGeometry>) in.readObject();
		this.setSelected(in.readBoolean());
		this.setId(in.readLong());
		this.setType(GeometryType.POLYGON);
		this.setSymbology((Symbology) in.readObject());
	}

	private static final double NORTH = 90;
	private static final double SOUTH = -90;
	private static final double EAST = 180;
	private static final double WEST = -180;

	private static final double VALUE_1E6 = 1E6;

	@Override
	public BoundingBoxE6 getBoundingBox() {
		double north = NORTH;
		double south = SOUTH;
		double east = EAST;
		double west = WEST;

		if (points.size() > 0) {
			north = points.get(0).getBoundingBox().getLatNorthE6() / VALUE_1E6;
			south = points.get(0).getBoundingBox().getLatSouthE6() / VALUE_1E6;
			east = points.get(0).getBoundingBox().getLonEastE6() / VALUE_1E6;
			west = points.get(0).getBoundingBox().getLonWestE6() / VALUE_1E6;
		}

		for (PointGeometry g : points) {
			BoundingBoxE6 tmp = g.getBoundingBox();
			north = (north < tmp.getLatNorthE6() / VALUE_1E6 ? tmp
					.getLatNorthE6() / VALUE_1E6 : north);
			south = (south > tmp.getLatSouthE6() / VALUE_1E6 ? tmp
					.getLatSouthE6() / VALUE_1E6 : south);
			east = (east < tmp.getLonEastE6() / VALUE_1E6 ? tmp.getLonEastE6()
					/ VALUE_1E6 : east);
			west = (west > tmp.getLonWestE6() / VALUE_1E6 ? tmp.getLonWestE6()
					/ VALUE_1E6 : west);
		}

		return new BoundingBoxE6(north, east, south, west);
	}

}
