package org.fosslab.android.osm;

import org.osmdroid.ResourceProxy.string;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;

public class GoogleMapsPhysicalTileSource extends OnlineTileSourceBase {

	public GoogleMapsPhysicalTileSource(final String aName, final string aResourceId,
			final int aZoomMinLevel, final int aZoomMaxLevel, final int aTileSizePixels,
			final String aImageFilenameEnding, final String... aBaseUrl) {
		super(aName, aResourceId, aZoomMinLevel, aZoomMaxLevel, aTileSizePixels,
				aImageFilenameEnding, aBaseUrl);
	}

	@Override
	public String getTileURLString(MapTile aTile) {
		// http://mt1.google.com/vt/lyrs=t@127,r@156&x=275&y=169&z=9
		return getBaseUrl() + "&x=" + aTile.getX() + "&y=" + aTile.getY() + "&z=" + aTile.getZoomLevel();
	}
}