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
	private long id;
	private GeometryType type;

	private Geometry g;
	private long idMission;
	private List<PointRecord> points = new ArrayList<PointRecord>();
	private long idFormRecord;

	public GeometryRecord() {
	}

	/**
	 * 
	 * @param g is the geometry associated to the GeometryRecord
	 * @param idMission
	 */
	public GeometryRecord(Geometry g, long idMission, long idFormRecord) {
		this.type = g.getType();
		this.g = g;
		this.idMission = idMission;
		this.idFormRecord = idFormRecord;
		createPoints();
	}

	/**
	 * 
	 * @param type of the geometry
	 * @param idMission
	 */
	public GeometryRecord(GeometryType type, int idMission) {
		this.type = type;
		this.idMission = idMission;
	}

	/**
	 * 
	 * @return the id of the geometryRecord
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * 
	 * @return the id of the formRecord
	 */
	public long getIdFormRecord() {
		return idFormRecord;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(long id) {
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
	public long getIdMission() {
		return idMission;
	}

	/**
	 * 
	 * @param idMission
	 */
	public void setIdMission(long idMission) {
		this.idMission = idMission;
	}

	/**
	 * 
	 * @return the list of PointRecord corresponding to the GeometryRecord
	 */
	public List<PointRecord> getPointsRecord() {
		return points;
	}
	
	public boolean addPoint(PointRecord point) {
		return points.add(point);
	}
	
	public boolean removePoint(PointRecord point) {
		return points.remove(point);
	}

	/**
	 * Create the list of points forming the database
	 */
	public final void createPoints() {
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
