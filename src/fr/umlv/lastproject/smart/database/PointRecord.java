package fr.umlv.lastproject.smart.database;

import fr.umlv.lastproject.smart.layers.PointGeometry;

/**
 * Object Point which can be stored in table "points"
 * 
 * @author Maelle Cabot
 * 
 */
public class PointRecord {

	private int id;
	private double x;
	private double y;
	private double z;
	private long idGeometry;
	
	private static final double VALUE_1E6 = 1E6;

	/**
	 * Constructor
	 */
	public PointRecord() {
	}

	/**
	 * Constructor
	 * 
	 * @param p is the PointGeometry to save
	 */
	public PointRecord(PointGeometry p) {

		this.x = p.getLatitude() / VALUE_1E6;
		this.y = p.getLongitude() / VALUE_1E6;
		this.z = -1;
	}

	/**
	 * Constructor
	 * 
	 * @param x is the latitude
	 * @param y is the longitude
	 * @param z is the altitude
	 */
	public PointRecord(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Getter 
	 * @return id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setter
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Getter
	 * @return x
	 */
	public double getX() {
		return x;
	}

	/**
	 * Setter
	 * @param x
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * Getter
	 * @return y
	 */
	public double getY() {
		return y;
	}

	/**
	 * Setter
	 * @param y
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Getter
	 * @return z
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Setter
	 * @param z
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * Getter
	 * @return idGeometry
	 */
	public long getIdGeometry() {
		return idGeometry;
	}

	/**
	 * Setter
	 * @param idGeometry
	 */
	public void setIdGeometry(long idGeometry) {
		this.idGeometry = idGeometry;
	}

}
