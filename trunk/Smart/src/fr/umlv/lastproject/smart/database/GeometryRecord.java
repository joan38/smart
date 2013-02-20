package fr.umlv.lastproject.smart.database;

import java.util.ArrayList;
import java.util.List;

import fr.umlv.lastproject.smart.layers.Geometry;
import fr.umlv.lastproject.smart.layers.Geometry.GeometryType;
import fr.umlv.lastproject.smart.layers.LineGeometry;
import fr.umlv.lastproject.smart.layers.PointGeometry;
import fr.umlv.lastproject.smart.layers.PolygonGeometry;

/**
 * Object Geometry which can be stored in table "geometries"
 * 
 * @author Maelle Cabot
 * 
 */
public class GeometryRecord {
	private int id;

	/* O:point 1:ligne 2:polygones */
	private GeometryType type;

	private Geometry g;
	private int idMission;
	private List<PointRecord> points = new ArrayList<PointRecord>();

	public GeometryRecord() {
	}

	/**
	 * 
	 * @param g is the geometry associated to the GeometryRecord
	 * @param idMission
	 */
	public GeometryRecord(Geometry g, int idMission) {
		this.type = g.getType();
		this.g = g;
		this.idMission = idMission;
		createPoints();
	}

	/**
	 * 
	 * @param type of the geometry
	 * @param idMission
	 */
	public GeometryRecord(GeometryType type, int idMission) {
		super();
		this.type = type;
		this.idMission = idMission;

	}

	/**
	 * 
	 * @return the id of the geometryRecord
	 */
	public int getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * 
	 * @return the type of the GeometryRecord
	 */
	public GeometryType getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 */
	public void setType(GeometryType type) {
		this.type = type;
	}

	/**
	 * 
	 * @return the id of the mission associated to the GeometryRecord
	 */
	public int getIdMission() {
		return idMission;
	}

	/**
	 * 
	 * @param idMission
	 */
	public void setIdMission(int idMission) {
		this.idMission = idMission;
	}

	/**
	 * 
	 * @return the list of PointRecord corresponding to the GeometryRecord
	 */
	public List<PointRecord> getPointsRecord() {
		return points;
	}

	/**
	 * Create the list of points forming the database
	 */
	public void createPoints() {

		switch (type) {
		case POINT:
			PointGeometry p = (PointGeometry) g;
			points.add(new PointRecord(p));
			break;

		case LINE:
			LineGeometry l = (LineGeometry) g;
			for (PointGeometry po : l.getPoints()) {
				points.add(new PointRecord(po));
			}
			break;
		case POLYGON:
			PolygonGeometry poly = (PolygonGeometry) g;
			for (PointGeometry po : poly.getPoints()) {
				points.add(new PointRecord(po));
			}
			break;

		default:
			break;
		}
	}

}
