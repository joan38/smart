package fr.umlv.lastproject.smart.layers;

import fr.umlv.lastproject.smart.data.Kml;

/**
 * Enum of the geometry types
 * 
 * @author Joan Goyeau
 */
public enum GeometryType {

	POINT(0, Kml.POINTTAG), LINE(1, Kml.LINETAG), POLYGON(2, Kml.POLYGONTAG);

	private final int dbId;
	private final String kmlName;

	private GeometryType(int dbId, String kmlName) {
		this.dbId = dbId;
		this.kmlName = kmlName;
	}

	public int getId() {
		return dbId;
	}

	/**
	 * Gets the KML Tag name
	 * 
	 * @return
	 */
	public String getKmlName() {
		return kmlName;
	}

	/**
	 * Returns the geometry corresponding to the id
	 * 
	 * @param id
	 *            from DataBase
	 * @return
	 */
	public static GeometryType getFromId(int id) {
		for (GeometryType geometryType : values()) {
			if (geometryType.dbId == id) {
				return geometryType;
			}
		}

		return null;
	}
}