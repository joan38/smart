package fr.umlv.lastproject.smart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Toast;
import fr.umlv.lastproject.smart.data.TMSOverlay;
import fr.umlv.lastproject.smart.data.WMSOverlay;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.layers.Layer;

/**
 * MapView with geoTIFFOverlays & geometryLayers notion
 * 
 * @author Marc, Thibault Douilly
 * 
 */
public class SmartMapView extends MapView {

	private final Map<String, Overlay> stringToOverlay;

	/**
	 * 
	 * @param context
	 *            of the application
	 * @param set
	 *            use to use findviewbyid
	 */
	public SmartMapView(final Context context, final AttributeSet set) {
		super(context, set);
		this.geoTIFFOverlays = new ArrayList<TMSOverlay>();
		this.listOverlay = new ListOverlay();

		this.stringToOverlay = new HashMap<String, Overlay>();

	}

	private final List<TMSOverlay> geoTIFFOverlays;

	private ListOverlay listOverlay;

	/**
	 * 
	 * @param layer
	 *            which will be added
	 */
	public void addOverlay(Layer layer) {
		final String name = layer.getName();
		if (listOverlay.search(name) != null) {
			Toast.makeText(getContext(), R.string.layerAlreadyExists,
					Toast.LENGTH_SHORT).show();
			return;
		}
		final Overlay overlay = layer.getOverlay();
		getOverlays().add(overlay);
		stringToOverlay.put(name, overlay);
		listOverlay.add(new LayerItem(name, layer.getOverview(), layer
				.hasSymbologyEditable()));
	}

	/**
	 * Adds a {@link TMSOverlay} (Tile Map Service Overlay)
	 * 
	 * @param overlay
	 *            raster overlay to add
	 */
	public void addGeoTIFFOverlay(final TMSOverlay overlay) {
		addOverlay(overlay);
		geoTIFFOverlays.add(overlay);
	}

	/**
	 * Adds a {@link GeometryLayer}
	 * 
	 * @param layer
	 *            to add to the view
	 */
	public void addGeometryLayer(final GeometryLayer layer) {
		addOverlay(layer);
	}

	/**
	 * Adds a some {@link GeometryLayer}
	 * 
	 * @param layer
	 *            to add to the view
	 */
	public void addGeometryLayers(final List<GeometryLayer> layers) {
		for (GeometryLayer geom : layers) {
			addOverlay(geom);

		}
	}

	/**
	 * 
	 * @param overlay
	 *            to removed
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
	 *            raster to remove
	 */
	public void removeGeoTIFFOverlay(final TMSOverlay overlay) {
		removeOverlay(overlay.getName());
		geoTIFFOverlays.remove(overlay);
	}

	/**
	 * Removes a {@link GeometryLayer}
	 * 
	 * @param layer
	 *            to remove
	 */
	public void removeGeometryLayer(final GeometryLayer layer) {
		removeOverlay(layer.getName());
	}

	/**
	 * 
	 * @param overlays
	 *            list of overlays to reorder
	 */
	public void setReorderedLayers(final ListOverlay overlays) {
		if (this.listOverlay.equals(overlays)) {
			return;
		}
		List<TMSOverlay> newGeotiffoverlays = new ArrayList<TMSOverlay>();

		for (LayerItem overlay : this.listOverlay.toList()) {
			Overlay o = this.stringToOverlay.get(overlay.getName());
			getOverlays().remove(o);
		}

		for (int i = 0; i < overlays.size(); i++) {
			Overlay o = this.stringToOverlay.get(overlays.get(i).getName());
			getOverlays().add(i,
					this.stringToOverlay.get(overlays.get(i).getName()));
			getOverlays().get(i).setEnabled(overlays.get(i).isVisible());
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
	 * @return the list of layers
	 */
	public ListOverlay getListOverlay() {
		return this.listOverlay;
	}

	/**
	 * find the layer
	 * 
	 * @param name
	 * @return
	 */
	public Overlay getOverlay(String name) {
		return this.stringToOverlay.get(name);
	}

	/**
	 * 
	 * @return the list of raster layers
	 */
	public List<TMSOverlay> getGeoTIFFOverlays() {
		return Collections.unmodifiableList(this.geoTIFFOverlays);
	}

	public void addWMSLayer(String wmsUrl, String wmsName) {
		if (wmsUrl == null || wmsName == null) {
			throw new IllegalArgumentException();
		}
		final WMSMapTileProviderBasic tileProvider = new WMSMapTileProviderBasic(
				getContext());
		// "http://sampleserver1.arcgisonline.com/arcgis/services/Specialty/ESRI_StatesCitiesRivers_USA/MapServer/WMSServer"
		// +
		// "?REQUEST=GetMap&SERVICE=WMS&VERSION=1.1.1&LAYERS=0&STYLES=default&FORMAT=image/png&BGCOLOR=0xFFFFFF&TRANSPARENT="
		// + "TRUE&SRS=EPSG:4326&WIDTH=256&HEIGHT=256&QUERY_LAYERS=0&BBOX="
		final ITileSource tileSource = new WMSTileSource(wmsName, null,
				OpenStreetMapTileProviderConstants.MINIMUM_ZOOMLEVEL,
				OpenStreetMapTileProviderConstants.MAXIMUM_ZOOMLEVEL, 256,
				".png", wmsUrl);
		tileProvider.setTileSource(tileSource);
		final WMSOverlay wmsOverlay = new WMSOverlay(tileProvider,
				getContext(), wmsName);

		wmsOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);

		addOverlay(wmsOverlay);

	}

}
