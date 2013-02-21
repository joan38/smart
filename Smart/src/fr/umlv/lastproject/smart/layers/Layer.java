package fr.umlv.lastproject.smart.layers;


import org.osmdroid.views.overlay.Overlay;



import android.graphics.Bitmap;


public interface Layer  {
	

	String getName();
	Bitmap getOverview();
	Overlay getOverlay();



}
