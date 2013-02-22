package fr.umlv.lastproject.smart.layers;

import fr.umlv.lastproject.smart.data.Kml;

/**
 * Class that defines the Geometry whith its symbology
 * 
 * @author Fad's
 * 
 */
public abstract class Geometry {

	/**
	 * Enum of the geometry types
	 * 
	 * @author thibault
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

	private GeometryType type;

	private Symbology symbology;

	/**
	 * 
	 * @return the symbology
	 */
	public Symbology getSymbology() {
		return this.symbology;
	}

	/**
	 * 
	 * @param symbology
	 *            : symbology to set
	 */
	public void setSymbology(Symbology symbology) {
		this.symbology = symbology;
	}

	/**
	 * 
	 * @return the type of the geometry
	 */
	public GeometryType getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 *            the type of the geometry
	 */
	public void setType(GeometryType type) {
		this.type = type;
	}

}
