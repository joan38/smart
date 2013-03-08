package fr.umlv.lastproject.smart.data;

import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.TilesOverlay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.layers.Layer;

public class WMSOverlay extends TilesOverlay implements Layer {

	private final String name;
	private final Context context;
	private static final int NORTHE6 = 90;
	private static final int EASTE6 = 180;
	private static final int SOUTHE6 = -90;
	private static final int WESTE6 = -180;

	public WMSOverlay(MapTileProviderBase aTileProvider, Context aContext,
			String name) {
		super(aTileProvider, aContext);
		this.name = name;
		this.context = aContext;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		WMSOverlay other = (WMSOverlay) obj;
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
		return true;
	}

	@Override
	public String getName() {
		return name;

	}

	@Override
	public Bitmap getOverview() {
		return BitmapFactory.decodeResource(context.getResources(),

		R.drawable.globe);

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

		return new Extent(new BoundingBoxE6(NORTHE6, EASTE6, SOUTHE6, WESTE6));
	}

}
