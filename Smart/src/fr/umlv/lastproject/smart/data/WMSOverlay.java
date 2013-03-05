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

	public WMSOverlay(MapTileProviderBase aTileProvider, Context aContext,
			String name) {
		super(aTileProvider, aContext);
		this.name = name;
		this.context = aContext;

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
		return new Extent(new BoundingBoxE6(90, 180, -90, 180));
	}

}
