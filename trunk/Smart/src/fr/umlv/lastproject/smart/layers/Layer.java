package fr.umlv.lastproject.smart.layers;

import org.osmdroid.views.overlay.Overlay;

import android.graphics.Bitmap;
import fr.umlv.lastproject.smart.data.TMSOverlay;

/**
 * Main interface for all kind of layers : {@link GeometryLayer} ,
 * {@link TMSOverlay}
 * 
 * @author Marc
 * 
 */
public interface Layer {

	/**
	 * 
	 * @return name of the layer
	 */
	String getName();

	/**
	 * If layer instanceof TMSOverlay : generic image
	 * 
	 * If layer instanceof GeometryLayer : dynamic image generated with
	 * symbology
	 * 
	 * @return the bitmap associated to the layer
	 * 
	 * 
	 */
	Bitmap getOverview();

	/**
	 * Casts the layer in overlay
	 * 
	 * @return overlay wrapped in layer
	 */
	Overlay getOverlay();

}
