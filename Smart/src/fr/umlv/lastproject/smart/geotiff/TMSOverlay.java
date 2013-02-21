package fr.umlv.lastproject.smart.geotiff;

import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.views.overlay.TilesOverlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.layers.Layer;

public class TMSOverlay extends TilesOverlay implements Layer {

	private final int zoomLevelMax, zoomLevelMin;
	private final String name;
	private final Context context;

	public TMSOverlay(final MapTileProviderBase aTileProvider,
			final Context aContext, final int minZoomLevel,
			final int maxZoomLevel, final String name) {
		super(aTileProvider, aContext);
		this.context=aContext;
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

	@Override
	public Canvas getOverview(Bitmap bitmap) {
		final Canvas canvas = new Canvas();
final Bitmap newBitmap=BitmapFactory.decodeResource(context.getResources(),R.drawable.raster );
		// set drawing colour
		
canvas.setBitmap(Bitmap.createScaledBitmap(newBitmap, bitmap.getWidth(), bitmap.getHeight(), false));
		
		return canvas;
	}
}
