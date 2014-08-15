package fr.umlv.lastproject.smart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.util.constants.MapViewConstants;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera.OnZoomChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;
import fr.umlv.lastproject.smart.data.TMSOverlay;
import fr.umlv.lastproject.smart.data.WMSMapTileProviderBasic;
import fr.umlv.lastproject.smart.data.WMSOverlay;
import fr.umlv.lastproject.smart.data.WMSTileSource;
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
	private final List<TMSOverlay> geoTIFFOverlays;
	private final List<GeometryLayer> geometryOverlays;
	private final List<WMSOverlay> wmsOverlays;
	private final Set<Layer> layers;
	public static final int MAXIMUM_ZOOMLEVEL = 28;

	private ListOverlay listOverlay;

	/**
	 * 
	 * @param context
	 *            of the application
	 * @param set
	 *            use to use findviewbyid
	 */
	public SmartMapView(final Context context, final AttributeSet set) {
		super(context, set);
		this.layers = new HashSet<Layer>();
		this.geoTIFFOverlays = new ArrayList<TMSOverlay>();
		this.listOverlay = new ListOverlay();
		this.geometryOverlays = new ArrayList<GeometryLayer>();
		this.wmsOverlays = new ArrayList<WMSOverlay>();
		this.stringToOverlay = new HashMap<String, Overlay>();
	}
	
	

	public List<GeometryLayer> getGeometryOverlays() {
		return geometryOverlays;
	}

	/**
	 * 
	 * @param layer
	 *            which will be added
	 * 
	 * @return true if layer has been added
	 */
	public boolean addOverlay(Layer layer) {
		final String name = layer.getName();
		if (listOverlay.search(name) != null) {
			Toast.makeText(getContext(), R.string.layerAlreadyExists,
					Toast.LENGTH_SHORT).show();
			return false;
		}
		this.layers.add(layer);
		final Overlay overlay = layer.getOverlay();
		getOverlays().add(overlay);
		stringToOverlay.put(name, overlay);
		listOverlay.add(new LayerItem(name, layer.getOverview(), layer
				.hasSymbologyEditable()));
		return true;
	}

	/**
	 * Adds a {@link TMSOverlay} (Tile Map Service Overlay)
	 * 
	 * @param overlay
	 *            raster overlay to add
	 */
	public void addGeoTIFFOverlay(final TMSOverlay overlay) {
		if (overlay == null) {
			Log.e("TESTX", "ERROR TMS OVERLAY");
			return;
		}
		if (!addOverlay(overlay)) {
			return;
		}
		geoTIFFOverlays.add(overlay);
	}

	/**
	 * Adds a {@link GeometryLayer}
	 * 
	 * @param layer
	 *            to add to the view
	 */
	public void addGeometryLayer(final GeometryLayer layer) {

		if (!addOverlay(layer)) {
			return;
		}
		this.geometryOverlays.add(layer);
	}

	/**
	 * Adds a some {@link GeometryLayer}
	 * 
	 * @param layer
	 *            to add to the view
	 */
	public void addGeometryLayers(final List<GeometryLayer> layers) {
		for (GeometryLayer geom : layers) {
			addGeometryLayer(geom);

		}
	}

	/**
	 * Adds a {@link WMSOverlay}
	 * 
	 * @param layer
	 *            to add to the view
	 */
	public void addWMSLayer(final WMSOverlay layer) {

		if (!addOverlay(layer)) {
			return;
		}
		this.wmsOverlays.add(layer);

	}

	public Layer getLayer(LayerItem item) {
		final Iterator<Layer> it = layers.iterator();
		Layer tmpLayer = null;
		while (it.hasNext()) {
			Layer temp = it.next();
			if (temp.getName().equals(item.getName())) {
				tmpLayer = temp;
			}
		}
		return tmpLayer;
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
		final Iterator<Layer> it = layers.iterator();
		Layer tmpLayer = null;
		while (it.hasNext()) {
			Layer temp = it.next();
			if (temp.getName().equals(name)) {
				tmpLayer = temp;
			}
		}
		if (tmpLayer != null) {
			layers.remove(tmpLayer);
		}
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
		this.geometryOverlays.remove(layer);
		removeOverlay(layer.getName());
	}

	/**
	 * Removes a {@link WMSOverlay}
	 * 
	 * @param layer
	 *            to remove
	 */
	public void removeWMSLayer(final WMSOverlay layer) {
		this.wmsOverlays.remove(layer);
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
		List<GeometryLayer> newGeometryLayers = new ArrayList<GeometryLayer>();
		List<WMSOverlay> newWMSLayers = new ArrayList<WMSOverlay>();

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
			boolean isGeometryOverlay = geometryOverlays.remove(o);
			wmsOverlays.remove(o);
			if (isTMSOverlay) {
				newGeotiffoverlays.add((TMSOverlay) o);
			} else if (isGeometryOverlay) {
				newGeometryLayers.add((GeometryLayer) o);
			} else {
				newWMSLayers.add((WMSOverlay) o);
			}
		}

		geoTIFFOverlays.clear();
		geoTIFFOverlays.addAll(newGeotiffoverlays);

		geometryOverlays.clear();
		geometryOverlays.addAll(newGeometryLayers);

		wmsOverlays.clear();
		wmsOverlays.addAll(newWMSLayers);

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
		geometryOverlays.clear();
		wmsOverlays.clear();
		layers.clear();
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
