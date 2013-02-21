package fr.umlv.lastproject.smart.layers;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * This class represent the geometry symbology
 * 
 * @author Fad's
 * 
 */
public abstract class Symbology implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int color;

	public Symbology(int color) {
		this.color = color;
	}

	public void setColor(int color) {
		this.color = color;		
		
	}

	public int getColor() {
		
		return this.color;
	}
	
	public abstract Canvas getImage(final Bitmap bitmap);
}
