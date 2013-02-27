package fr.umlv.lastproject.smart.layers;

import org.osmdroid.views.MapView;

import android.graphics.Canvas;
import android.graphics.Rect;
import fr.umlv.lastproject.smart.SmartMapView;


/**
 * Class that defines the Geometry whith its symbology
 * 
 * @author Fad's
 */
public abstract class Geometry {
	
	
	private long id ;

	private GeometryType type;
	private Symbology symbology;
	
	private boolean isSelected = false ;

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
	
	/**
	 * 
	 * @param b if the geometry is selected
	 */
	public void setSelected(boolean b){
		isSelected = b ;
	}
	
	/**
	 * @return true is the geometry is selected
	 */
	public boolean isSelected(){
		return isSelected;
	}

	/**
	 * 
	 * @return the id of the geometry
	 */
	public long getId() {
		return id;
	}

	/**
	 * 
	 * @param id of the geometry
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * 
	 * @param map the map 
	 * @param c the canvas
	 * @param b ?
	 */
	public abstract void draw(MapView map, Canvas c, Boolean b , Symbology s);	
	
	public abstract boolean isSelected(MapView map, Rect click) ;
	
	

}
