package fr.umlv.lastproject.smart.layers;

import java.io.Serializable;

import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.layers.Geometry.GeometryType;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

public enum SmartIcon implements Serializable {
	RASTER,SYMBOLOGY;
	
	private  Bitmap bitmap;
	private int color;
	private GeometryType type;
	
	
	
	
	
	
	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public GeometryType getType() {
		return type;
	}

	public void setType(GeometryType type) {
		this.type = type;
	}


	
	
	

}
