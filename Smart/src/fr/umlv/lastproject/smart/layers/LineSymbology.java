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
 * This class represent the symbology of the Line
 * 
 * @author Fad's
 * 
 */
public class LineSymbology extends Symbology {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private static final float LINESIZE = 12;
	
	private static final float STROKEWIDTH = 5;



	/**
	 * Line default constructor
	 * 
	 * Color : Black / Thickness : 5
	 */
	public LineSymbology() {
		this(Color.BLACK, 5,150);
	}

	/**
	 * Line constructor
	 * 
	 * @param thickness
	 * @param color
	 */
	public LineSymbology(int thickness, int color, int alpha) {
		super(color, thickness,alpha);
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
			canvas.drawLine(LINESIZE, LINESIZE, height - LINESIZE, width
					- LINESIZE, paint);
	
			return bitmap;
	}
	


}
