package fr.umlv.lastproject.smart.layers;

import java.io.Serializable;

import android.graphics.Color;

/**
 * This class represents the geometry symbology
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

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(int color) {
		this.color = color;
	}

	public Symbology(int color) {
		this.color = color;
	}

	/**
	 * Gets the color of the symbology associated to a layer
	 * 
	 * @return int see {@link Color}
	 */
	public int getColor() {

		return this.color;
	}

}
