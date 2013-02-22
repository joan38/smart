package fr.umlv.lastproject.smart.layers;

import java.util.ArrayList;
import java.util.List;

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

}
