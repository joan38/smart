package fr.umlv.lastproject.smart.layers;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public interface Layer  {
	
	String getName();
	Canvas getOverview(final Bitmap bitmap);

}
