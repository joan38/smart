package fr.umlv.lastproject.smart.data;

import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.TilesOverlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.MotionEvent;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.layers.Layer;

public class TMSOverlay extends TilesOverlay implements Layer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((boundingBox == null) ? 0 : boundingBox.hashCode());
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + zoomLevelMax;
		result = prime * result + zoomLevelMin;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TMSOverlay other = (TMSOverlay) obj;
		if (boundingBox == null) {
			if (other.boundingBox != null) {
				return false;
			}
		} else if (!boundingBox.equals(other.boundingBox)) {
			return false;
		}
		if (context == null) {
			if (other.context != null) {
				return false;
			}
		} else if (!context.equals(other.context)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!path.equals(other.path)) {
			return false;
		}
		if (zoomLevelMax != other.zoomLevelMax) {
			return false;
		}
		if (zoomLevelMin != other.zoomLevelMin) {
			return false;
		}
		return true;
	}

	private final int zoomLevelMax, zoomLevelMin;
	private final String name;
	private final Context context;
	private final BoundingBoxE6 boundingBox;
	private final String path;

	public TMSOverlay(final MapTileProviderBase aTileProvider,
			final Context aContext, int minZoomLevel, int maxZoomLevel,
			final String name, final BoundingBoxE6 extent, String path) {
		super(aTileProvider, aContext);
		this.path = path;
		this.context = aContext;

		this.name = name;
		this.boundingBox = extent;

		/*
		 * if (maxZoomLevel < minZoomLevel || maxZoomLevel >
		 * OpenStreetMapTileProviderConstants.MAXIMUM_ZOOMLEVEL || minZoomLevel
		 * < OpenStreetMapTileProviderConstants.MINIMUM_ZOOMLEVEL) {
		 * minZoomLevel = OpenStreetMapTileProviderConstants.MINIMUM_ZOOMLEVEL;
		 * maxZoomLevel = OpenStreetMapTileProviderConstants.MAXIMUM_ZOOMLEVEL;
		 * }
		 */
		zoomLevelMax = maxZoomLevel;
		zoomLevelMin = minZoomLevel;

		setLoadingBackgroundColor(Color.TRANSPARENT);

	}

	public String getPath() {
		return path;
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
	public Extent getExtent() {
		return new Extent(boundingBox, zoomLevelMin);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		return super.onTouchEvent(event, mapView);
	}

}
