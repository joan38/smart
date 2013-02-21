package fr.umlv.lastproject.smart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import fr.umlv.lastproject.smart.geotiff.TMSOverlay;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.layers.SmartIcon;

/**
 * MapView with geoTIFFOverlays & geometryLayers notion
 * 
 * @author Marc, Thibault Douilly
 * 
 */
public class SmartMapView extends MapView {

	// private static final String WORLD_MAP_FOLDER = "Test";
	// private static final int WORLD_MAP_MIN_ZOOM = 0;
	// private static final int WORLD_MAP_MAX_ZOOM = 4;
	// private static final String WORLD_MAP_EXTENSION = ".png";

	private final Map<LayerState, Overlay> stringToOverlay;

	public SmartMapView(final Context context, final AttributeSet set) {
		super(context, set);
		this.geoTIFFOverlays = new ArrayList<TMSOverlay>();
		// this.geometryLayers = new ArrayList<GeometryLayer>();
		this.listOverlay = new ListOverlay();

		this.stringToOverlay = new HashMap<LayerState, Overlay>();
		// super.getOverlayManager().getTilesOverlay().setEnabled(false);

		// TMSOverlay worldOverlay;
		//
		// worldOverlay = DataImport.importGeoTIFFFileZIP(WORLD_MAP_FOLDER,
		// context, WORLD_MAP_MIN_ZOOM, WORLD_MAP_MAX_ZOOM,
		// WORLD_MAP_EXTENSION);
		//
		// addGeoTIFFOverlay(worldOverlay);

	}

	private final List<TMSOverlay> geoTIFFOverlays;
	// private final List<GeometryLayer> geometryLayers;

	private ListOverlay listOverlay;

	/**
	 * 
	 * @param name
	 * @param overlay
	 */
	public void addOverlay(String name, Overlay overlay) {
		getOverlays().add(overlay);
		stringToOverlay.put(new LayerState(name), overlay);
		listOverlay.add(name);
	}

	/**
	 * 
	 * @param name
	 * @param overlay
	 */
	public void addOverlay(String name, SmartIcon symbologie, Overlay overlay) {
		getOverlays().add(overlay);
		stringToOverlay.put(new LayerState(name), overlay);
		Log.d("debug", " addOverlay " + symbologie.getType());
		listOverlay.add(name, symbologie);
	}

	/**
	 * Adds a {@link TMSOverlay} (Tile Map Service Overlay)
	 * 
	 * @param overlay
	 */
	public void addGeoTIFFOverlay(final TMSOverlay overlay) {
		addOverlay(overlay.getName(), overlay.getOverview(), overlay);
		geoTIFFOverlays.add(overlay);
	}

	/**
	 * Adds a {@link GeometryLayer}
	 * 
	 * @param layer
	 */
	public void addGeometryLayer(final GeometryLayer layer) {
		addOverlay(layer.getName(), layer.getOverview(), layer);
	}

	/**
	 * Adds a some {@link GeometryLayer}
	 * 
	 * @param layer
	 */
	public void addGeometryLayers(final List<GeometryLayer> layers) {
		for (GeometryLayer geom : layers) {
			addOverlay(geom.getName(), geom.getOverview(), geom);
		}
	}

	/**
	 * 
	 * @param overlay
	 */
	private void removeOverlay(String name) {
		getOverlays().remove(stringToOverlay.get(name));
		stringToOverlay.remove(name);
		listOverlay.remove(name);
	}

	/**
	 * Removes a {@link TMSOverlay}
	 * 
	 * @param overlay
	 */
	public void removeGeoTIFFOverlay(final TMSOverlay overlay) {
		removeOverlay(overlay.getName());
		geoTIFFOverlays.remove(overlay);
	}

	/**
	 * Removes a {@link GeometryLayer}
	 * 
	 * @param layer
	 */
	public void removeGeometryLayer(final GeometryLayer layer) {
		removeOverlay(layer.getName());
	}

	/**
	 * 
	 * @param overlays
	 */
	public void setReorderedLayers(final ListOverlay overlays) {
		List<TMSOverlay> newGeotiffoverlays = new ArrayList<TMSOverlay>();

		for (LayerState overlay : this.listOverlay.toList()) {
			Overlay o = this.stringToOverlay.get(overlay);
			getOverlays().remove(o);
		}

		for (int i = 0; i < overlays.size(); i++) {
			Overlay o = this.stringToOverlay.get(overlays.get(i));
			getOverlays().add(i, this.stringToOverlay.get(overlays.get(i)));
			boolean isTMSOverlay = geoTIFFOverlays.remove(o);
			if (isTMSOverlay) {
				newGeotiffoverlays.add((TMSOverlay) o);
			}
		}

		geoTIFFOverlays.clear();
		geoTIFFOverlays.addAll(newGeotiffoverlays);

		this.listOverlay = overlays;
		this.invalidate();

	}

	/**
	 * Clears the map layers
	 */
	public void clear() {
		getOverlays().clear();
		listOverlay.clear();
		geoTIFFOverlays.clear();
	}

	/**
	 * 
	 * @return
	 */
	public ListOverlay getListOverlay() {
		return this.listOverlay;
	}

	/**
	 * 
	 * @return
	 */
	public List<TMSOverlay> getGeoTIFFOverlays() {
		return Collections.unmodifiableList(this.geoTIFFOverlays);
	}

}
