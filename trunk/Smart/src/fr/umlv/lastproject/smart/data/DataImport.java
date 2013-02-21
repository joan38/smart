package fr.umlv.lastproject.smart.data;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import fr.umlv.lastproject.smart.layers.Geometry.GeometryType;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.utils.ZIPUtils;

public final class DataImport {

	/**
	 * Utility class/ can't instantiate
	 */
	private DataImport() {

	}

	private static final int TILE_SIZE = 256;

	/**
	 * Import Tiles giving a path containing the tiles
	 * 
	 * @param pathName
	 * @param context
	 * @return
	 * @throws IOException
	 */
	public static TMSOverlay importGeoTIFFFileFolder(final String pathName,
			final Context context, final String name) throws IOException {

		if (pathName == null || context == null) {
			throw new IllegalArgumentException();
		}

		final File f = new File(pathName);

		if (!f.exists()) {
			throw new IllegalArgumentException("pathName doesn't exist");

		}

		final Object[] metadata = ZIPUtils.compress(pathName);
		final String folderName = (String) metadata[0];
		final String tileExtension = (String) metadata[1];
		final int minZoom = Integer.parseInt(metadata[2].toString());
		final int maxZoom = Integer.parseInt(metadata[3].toString());
		return importGeoTIFFFileZIP(folderName, context, minZoom, maxZoom,
				tileExtension, name);

	}

	/**
	 * Import tiles when a zip file already exists
	 * 
	 * @param folderName
	 * @param context
	 * @param tileNameExtension
	 * @return
	 */
	public static TMSOverlay importGeoTIFFFileZIP(final String folderName,
			final Context context, final int minZoom, final int maxZoom,
			final String tileExtension, final String name) {
		if (folderName == null
				|| minZoom < OpenStreetMapTileProviderConstants.MINIMUM_ZOOMLEVEL
				|| maxZoom > OpenStreetMapTileProviderConstants.MAXIMUM_ZOOMLEVEL
				|| tileExtension == null) {
			throw new IllegalArgumentException();
		}

		final MapTileProviderBasic mProvider = new MapTileProviderBasic(context);
		final TMSTileSourceBase tmsSource = new TMSTileSourceBase(folderName,
				null, minZoom, maxZoom, TILE_SIZE, tileExtension);

		mProvider.setTileSource(tmsSource);
		mProvider.setUseDataConnection(false);

		final TMSOverlay mTilesOverlay = new TMSOverlay(mProvider, context,
				minZoom, maxZoom, name);

		return mTilesOverlay;
	}

	/**
	 * 
	 * @param context
	 * @param filename
	 * @return
	 */
	public static GeometryLayer importShapeFile(Context context, String filename) {
		if (context == null || filename == null) {
			throw new IllegalArgumentException();
		}
		return ShpImport.getLayerFromShp(filename, context);
	}

	/**
	 * 
	 * 
	 * @param context
	 * @param filename
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static List<GeometryLayer> importKml(Context context, String filename)
			throws XmlPullParserException, IOException {
		return KmlImport.getLayersFromKML(filename, context);
	}

	/**
	 * 
	 * 
	 * @param context
	 * @param filename
	 * @param type
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static GeometryLayer importKml(Context context, String filename,
			GeometryType type) throws XmlPullParserException, IOException {
		return KmlImport.getLayerFromKML(filename, type, context);
	}

}
