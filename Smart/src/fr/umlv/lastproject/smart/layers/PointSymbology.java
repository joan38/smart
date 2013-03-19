package fr.umlv.lastproject.smart.layers;

import fr.umlv.lastproject.smart.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;

/**
 * This class represent the Point symbology
 * 
 * @author Fad's
 * 
 */

public class PointSymbology extends Symbology {

	private static final float RADIUS = 12;
	private static final float STROKEWIDTH = 5;
	public enum PointSymbologieType {
		CIRCLE(0), SQUARE(1);
		private int id;

		private PointSymbologieType(int id) {
			// TODO Auto-generated constructor stub
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public static PointSymbologieType getFromId(int id) {
			for (PointSymbologieType p : values()) {
				if (p.id == id) {
					return p;
				}
			}
			return null;
		}
	}

	private static final long serialVersionUID = 1L;
	private int radius;
	private PointSymbologieType type;

	private static final int VALUE_RADIUS = 8;

	/**
	 * Point default constructor
	 * 
	 * Color : Black / Radius : 5
	 */
	public PointSymbology() {
		this(VALUE_RADIUS, Color.BLACK,150, PointSymbologieType.CIRCLE);
	}

	/**
	 * Point constructor
	 * 
	 * @param radius
	 * @param color
	 */
	public PointSymbology(int radius, int color,int alpha, PointSymbologieType type) {
		super(color, radius,alpha);
		this.radius = radius;
		this.type = type;
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

	public void setType(PointSymbologieType type) {
		this.type = type;
	}

	public PointSymbologieType getType() {
		return type;
	}

	@Override
	public Bitmap getOverview(Context c) {
		final Bitmap bit = BitmapFactory.decodeResource(c.getResources(),
				R.drawable.geometry_blank);

		Bitmap bitmap = bit.copy(Config.ARGB_8888, true);
		bit.recycle();
		int middlex = (bitmap.getHeight() / 2);
		int middley = (bitmap.getWidth() / 2);

		final Canvas canvas = new Canvas(bitmap);

		Paint paint = new Paint();

		paint.setColor(getColor());
			paint.setStrokeWidth(STROKEWIDTH);
			switch (getType()) {
			case CIRCLE:
                canvas.drawCircle(middlex, middley, RADIUS, paint);
				break;
			case SQUARE : 

			default:
				canvas.drawRect(new Rect(middlex - radius, middley - radius, middlex
						+ radius, middley + radius), paint);

				break;
			}
	
			return bitmap;
	}
}
