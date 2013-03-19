package fr.umlv.lastproject.smart.layers;

import fr.umlv.lastproject.smart.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;

/**
 * This class represent the symbology of the polygon
 * 
 * 
 * @author Fad's
 * 
 */
public class PolygonSymbology extends Symbology {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int thickness;
	
     private static final float RECTSIZE = 12;
     private static final float STROKEWIDTH = 5;

	private static final int DEFAULT_THICKNESS = 5;

	/**
	 * Polygon default constructor
	 * 
	 * Color : Black / Thickness : 5
	 */
	public PolygonSymbology() {
		this(DEFAULT_THICKNESS, Color.BLACK,150);
	}

	/**
	 * Polygon constructor
	 * 
	 * @param thickness
	 * @param color
	 */
	public PolygonSymbology(int thickness, int color,int alpha) {
		super(color, thickness,alpha);
		this.thickness = thickness;
	}

	/**
	 * 
	 * @return the line thickness
	 */
	public int getThickness() {
		return this.thickness;
	}

	/**
	 * Function which set the Polygon thickness
	 * 
	 * @param thickness
	 */
	public void setThickness(int thickness) {
		this.thickness = thickness;
	}

	@Override
	public Bitmap getOverview(Context c) {
		final Bitmap bit = BitmapFactory.decodeResource(c.getResources(),
				R.drawable.geometry_blank);

		Bitmap bitmap = bit.copy(Config.ARGB_8888, true);
		bit.recycle();
		int height = (bitmap.getHeight());
		int width = (bitmap.getWidth());

		final Canvas canvas = new Canvas(bitmap);

		Paint paint = new Paint();

		paint.setColor(getColor());
			paint.setStrokeWidth(STROKEWIDTH);
			 canvas.drawRect(RECTSIZE, RECTSIZE, height - RECTSIZE, width
                     - RECTSIZE, paint);	
			return bitmap;
	}

}
