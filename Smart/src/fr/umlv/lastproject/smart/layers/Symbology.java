package fr.umlv.lastproject.smart.layers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.content.Context;
import android.graphics.Bitmap;
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
	private int size;
	private int alpha = 150 ;

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(int color) {
		this.color = color;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	public Symbology(int color, int size, int alpha) {
		this.color = color;
		this.size = size;
		this.alpha = alpha;
	}

	/**
	 * Gets the color of the symbology associated to a layer
	 * 
	 * @return int see {@link Color}
	 */
	public int getColor() {

		return this.color;
	}

	/**
	 * 
	 * @return the symbology size
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * 
	 * @param out
	 *            the object to get
	 * @throws IOException
	 *             if canot read
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(color);
		out.writeInt(size);
		out.close();
	}

	/**
	 * 
	 * @param in
	 *            object to read
	 * @throws IOException
	 *             if object not readable
	 * @throws ClassNotFoundException
	 *             if class does not exist
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		this.color = in.readInt();
		this.size = in.readInt();
		in.close();
	}
	
	public abstract Bitmap getOverview(Context c);

}
