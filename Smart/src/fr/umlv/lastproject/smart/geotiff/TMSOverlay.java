package fr.umlv.lastproject.smart.geotiff;

import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.views.overlay.TilesOverlay;

import android.content.Context;
import android.graphics.Color;
import fr.umlv.lastproject.smart.layers.Layer;

public class TMSOverlay extends TilesOverlay implements Layer {

	private final int zoomLevelMax, zoomLevelMin;
	private final String name;

	public TMSOverlay(final MapTileProviderBase aTileProvider,
			final Context aContext, final int minZoomLevel,
			final int maxZoomLevel, final String name) {
		super(aTileProvider, aContext);
		zoomLevelMax = maxZoomLevel;
		zoomLevelMin = minZoomLevel;
		this.name = name;
		if (zoomLevelMax < zoomLevelMin
				|| zoomLevelMax > OpenStreetMapTileProviderConstants.MAXIMUM_ZOOMLEVEL
				|| zoomLevelMin < OpenStreetMapTileProviderConstants.MINIMUM_ZOOMLEVEL)
			throw new IllegalArgumentException();
		setLoadingBackgroundColor(Color.TRANSPARENT);

	}

	public int getZoomLevelMax() {
		return zoomLevelMax;
	}

	public int getZoomLevelMin() {
		return zoomLevelMin;
	}

	public String getName() {
		return name;
	}
}
