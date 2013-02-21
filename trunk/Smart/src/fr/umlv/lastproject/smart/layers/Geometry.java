package fr.umlv.lastproject.smart.layers;

/**
 * Class which define the Geometry whith their symbology
 * 
 * @author Fad's
 * 
 */
public abstract class Geometry {
	
	private static final String POINT_STRING="Point";
	private static final String LINE_STRING="LineString";
	private static final String POLYGON_STRING="Polygon";

	/**
	 * Enum of the geometry types
	 * 
	 * @author thibault
	 */
	public enum GeometryType {
		POINT(0, POINT_STRING),
		LINE(1, LINE_STRING),
		POLYGON(2, POLYGON_STRING);

		private final int dbId;
		private final String kmlName;

		private GeometryType(int dbId, String kmlName) {
			this.dbId = dbId;
			this.kmlName = kmlName;
		}

		public int getId() {
			return dbId;
		}

		public String getKmlName() {
			return kmlName;
		}

		public static GeometryType getFromId(int id) {
			for (GeometryType geometryType : values()) {
				if (geometryType.dbId == id) {
					return geometryType;
				}
			}

			return null;
		}
	}
	
	private GeometryType type ;

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
	public GeometryType getType(){
		return type;
	}
	
	/**
	 * 
	 * @param type the type of the geometry
	 */
	public void setType(GeometryType type){
		this.type = type ;
	}

}
