package fr.umlv.lastproject.smart.data;

import org.osmdroid.ResourceProxy.string;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;

import android.util.Log;

/**
 * This class will allow you to overlay tiles from a WMS server. Your WMS server
 * needs to support . An example of how your base url should look:
 * 
 * https://xxx.xxx.xx.xx/geoserver/gwc/service/wms?LAYERS=base_map&FORMAT=image/
 * jpeg
 * &SERVICE=WMS&VERSION=1.1.1REQUEST=GetMap&STYLES=&SRS=EPSG:900913&WIDTH=256
 * &HEIGHT=256&BBOX=
 * 
 * Notice three things: 1. I am pulling jpeg instead of png files. For some
 * reason our server makes much smaller jpg files and this gives us a faster
 * load time on mobile networks. 2. The bounding box is at the end of the base
 * url. This is because the getTileURLString method adds the bounding box values
 * onto the end of the base url. 3. We are pulling the SRS=EPSG:900913 and not
 * the SRS=EPSG:4326. This all has to do drawing rounded maps onto flat
 * displays.
 * 
 * @author Steve Potell -- spotell@t-sciences.com
 * 
 */
public class WMSTileSource extends OnlineTileSourceBase {

	public WMSTileSource(final String aName, final string aResourceId,
			final int aZoomMinLevel, final int aZoomMaxLevel,
			final int aTileSizePixels, final String aImageFilenameEnding,
			final String... aBaseUrl) {
		super(aName, aResourceId, aZoomMinLevel, aZoomMaxLevel,
				aTileSizePixels, aImageFilenameEnding, aBaseUrl);
	}

	@Override
	public String getTileURLString(MapTile aTile) {

		StringBuffer tileURLString = new StringBuffer();
		tileURLString.append(getBaseUrl());
		// tileURLString.append("-125.192865,11.2289864971264,-66.105824,62.5056715028736");
		tileURLString.append(wmsTileCoordinates(aTile));
		Log.d("TESTX", tileURLString.toString());

		return tileURLString.toString();
	}

	private final static double ORIGIN_SHIFT = Math.PI * 6378137;

	/**
	 * WMS requires the bounding box to be defined as the point (west, south) to
	 * the point (east, north).
	 * 
	 * @return The WMS string defining the bounding box values.
	 */
	public String wmsTileCoordinates(MapTile value) {

		BoundingBox newTile = tile2boundingBox(value.getX(), value.getY(),
				value.getZoomLevel());

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(newTile.west);
		stringBuffer.append(",");
		stringBuffer.append(newTile.south);
		stringBuffer.append(",");
		stringBuffer.append(newTile.east);
		stringBuffer.append(",");
		stringBuffer.append(newTile.north);

		return stringBuffer.toString();

	}

	/**
	 * A simple class for holding the NSEW lat and lon values.
	 */
	class BoundingBox {
		double north;
		double south;
		double east;
		double west;
	}

	/**
	 * This method converts tile xyz values to a WMS bounding box.
	 * 
	 * @param x
	 *            The x tile coordinate.
	 * @param y
	 *            The y tile coordinate.
	 * @param zoom
	 *            The zoom level.
	 * 
	 * @return The completed bounding box.
	 */
	BoundingBox tile2boundingBox(final int x, final int y, final int zoom) {
		BoundingBox bb = new BoundingBox();
		bb.north = tile2lat(y, zoom);
		bb.south = tile2lat(y + 1, zoom);
		bb.west = tile2lon(x, zoom);
		bb.east = tile2lon(x + 1, zoom);
		return bb;
	}

	static double tile2lon(int x, int z) {
		return x / Math.pow(2.0, z) * 360.0 - 180;
	}

	static double tile2lat(int y, int z) {
		double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
		return Math.toDegrees(Math.atan(Math.sinh(Math.abs(n))));
	}

}
