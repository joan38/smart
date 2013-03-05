package fr.umlv.lastproject.smart.data;

import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.TilesOverlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.layers.Layer;

public class TMSOverlay extends TilesOverlay implements Layer {

	private final int zoomLevelMax, zoomLevelMin;
	private final String name;
	private final Context context;
	private final BoundingBoxE6 boundingBox;

	public TMSOverlay(final MapTileProviderBase aTileProvider,
			final Context aContext, final int minZoomLevel,
			final int maxZoomLevel, final String name,
			final BoundingBoxE6 extent) {
		super(aTileProvider, aContext);
		this.context = aContext;
		zoomLevelMax = maxZoomLevel;
		zoomLevelMin = minZoomLevel;
		this.name = name;
		this.boundingBox = extent;
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
	public Bitmap getOverview() {

		return BitmapFactory.decodeResource(context.getResources(),
				R.drawable.raster_symbo);

	}

	@Override
	public Overlay getOverlay() {
		return this;
	}

	@Override
	public boolean hasSymbologyEditable() {
		return false;
	}

	@Override
	public BoundingBoxE6 getExtent() {
		return this.boundingBox;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		Log.d("map position", mapView.getBoundingBox().getCenter().getLatitudeE6() / 1E6+ " " +mapView.getBoundingBox().getCenter().getLongitudeE6());
		return super.onTouchEvent(event, mapView);
	}

}
