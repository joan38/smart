package fr.umlv.lastproject.smart.layers;


/**
 * Class that defines the Geometry whith its symbology
 * 
 * @author Fad's
 */
public abstract class Geometry {

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
