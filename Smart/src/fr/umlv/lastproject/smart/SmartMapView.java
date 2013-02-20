package fr.umlv.lastproject.smart;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osmdroid.views.MapView;

import android.content.Context;
import android.util.AttributeSet;
import fr.umlv.lastproject.smart.geotiff.TMSOverlay;

/**
 * MapView with geoTIFFLayers notion
 * @author Marc
 *
 */
public class SmartMapView extends MapView {


	


	
	public SmartMapView(final Context context, final AttributeSet set) {
		super(context, set);
		this.geoTIFFOverlays = new ArrayList<TMSOverlay>();
		//super.getOverlayManager().getTilesOverlay().setEnabled(false);



	}

	private final List<TMSOverlay> geoTIFFOverlays;

	/**
	 * Adds a {@link TMSOverlay} (Tile Map Service Overlay)
	 * @param overlay
	 */
	public void addGeoTIFFOverlay(final TMSOverlay overlay) {
		if(overlay==null){
			throw new IllegalArgumentException();
		}
		geoTIFFOverlays.add(overlay);
		getOverlays().add(0, overlay);

	}

	/**
	 * Removes a {@link TMSOverlay}
	 * @param overlay
	 */
	public void removeGeoTIFFOverlay(final TMSOverlay overlay) {
		if(overlay==null){
			throw new IllegalArgumentException();
		}
		getOverlays().remove(overlay);
		geoTIFFOverlays.remove(overlay);
	}

	/**
	 * Clears the map layers
	 */
	public void clear() {
		getOverlays().clear();
		geoTIFFOverlays.clear();
	}

	/**
	 * Gets an unmodifiable view of all the TMS Overlays 
	 * 
	 * 
	 * @return {@link List}<{@link TMSOverlay}> 
	 */
	public List<TMSOverlay> getGeoTIFFOverlays() {
		return Collections.unmodifiableList(this.geoTIFFOverlays);
	}
	


}
