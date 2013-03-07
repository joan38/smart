package fr.umlv.lastproject.smart.layers;

import android.graphics.Color;

/**
 * This class represent the Point symbology
 * 
 * @author Fad's
 * 
 */


public class PointSymbology extends Symbology {
	
	public enum PointSymbologieType{
		CIRCLE(0), SQUARE(1) ;
		private int id ;
		
		private PointSymbologieType(int id) {
			// TODO Auto-generated constructor stub
			this.id = id ;
		}
		
		public int getId(){
			return id ;
		}
		public static PointSymbologieType getFromId(int id){
			for(PointSymbologieType p : values()){
				if(p.id == id){
					return p;
				}
			}
			return null;
		}
	}


	private static final long serialVersionUID = 1L;
	private int radius;
	private PointSymbologieType type ;

	private static final int VALUE_RADIUS = 5;

	/**
	 * Point default constructor
	 * 
	 * Color : Black / Radius : 5
	 */
	public PointSymbology() {
		this(VALUE_RADIUS, Color.BLACK, PointSymbologieType.SQUARE);
	}

	/**
	 * Point constructor
	 * 
	 * @param radius
	 * @param color
	 */
	public PointSymbology(int radius, int color, PointSymbologieType type ) {
		super(color, radius);
		this.radius = radius;
		this.type = type ;
	}

	/**
	 * Function which return the Point radius
	 * 
	 * @return radius
	 */
	public int getRadius() {
		return this.radius;
	}

	/**
	 * @param radius
	 *            the radius to set
	 */
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	public void setType(PointSymbologieType type){
		this.type = type ;
	}

	public PointSymbologieType getType(){
		return type ;
	}
}
