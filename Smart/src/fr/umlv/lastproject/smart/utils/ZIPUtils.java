package fr.umlv.lastproject.smart.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.util.BoundingBoxE6;

import android.R.string;
import android.util.Log;
import android.util.Pair;
import fr.umlv.lastproject.smart.browser.utils.FileUtils;

/**
 * Utility Class to compress a folder of tiles into a zip Its also permits to
 * retrieve tiles metadata
 * 
 * @author Marc
 * 
 */
public final class ZIPUtils {

	private static final int BUFFER = 2048;

	private static final int METADATA_SIZE = 5;

	private static final String ZIP_FOLDER = "/mnt/sdcard/osmdroid/";

	private ZIPUtils() {
	}

	/**
	 * 
	 * @param directory
	 *            complete pathname to the tiles directory
	 * @return tiles metadata [0]->Root folder in zip archive / [1]->extension
	 *         of tiles / [2]-> minimum level of zoom / [3]-> maximum level of
	 *         zoom
	 * @throws IOException
	 */
	public static Object[] compress(final String directory) throws IOException {

		if (directory == null) {
			throw new IllegalArgumentException();
		}

		/** We retrieve tiles metadata */
		final Object[] metaData = getTilesMetaData(directory);

		BufferedInputStream origin = null;
		final FileOutputStream dest = new FileOutputStream(ZIP_FOLDER
				+ directory.substring(directory.lastIndexOf('/')) + ".zip");

		final ZipOutputStream out = new ZipOutputStream(
				new BufferedOutputStream(dest));
		final byte data[] = new byte[BUFFER];

		final File dir = new File(directory);
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("can't compress a file");
		}
		ZipEntry entry = null;
		final String rootFolder = metaData[0].toString() + "/";
		out.putNextEntry(new ZipEntry(rootFolder));
		final List<String> filenames = listFiles(directory, true);
		for (int i = 0; i < filenames.size(); i++) {
			String file = filenames.get(i);
			final File lFile = new File(file);

			FileInputStream inputStream = null;

			if (!lFile.isDirectory()) {

				inputStream = new FileInputStream(file);
				origin = new BufferedInputStream(inputStream, BUFFER);
				Log.d("TEST2", "file zip : " + file.toString());
				entry = new ZipEntry(rootFolder + file.split(rootFolder)[1]);

				out.putNextEntry(entry);
				Log.v("TEST2", "Adding: " + entry);

				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}

		}

		out.close();
		return metaData;

	}

	/**
	 * 
	 * @param directory
	 *            the directory to iterate
	 * @param if the file is a directory (first time it's called must be true)
	 * @return
	 */
	private static List<String> listFiles(final String directory,
			final boolean root) {
		final File file = new File(directory);

		final List<String> files = new ArrayList<String>();
		if (root) {
			files.add(directory);
		}

		final File[] tabFiles = file.listFiles();
		for (File f : tabFiles) {
			files.add(f.toString());
			if (f.isDirectory()) {
				files.addAll(listFiles(f.toString(), false));
			}

		}
		return files;

	}

	/**
	 * 0 folderName 1 extension 2 min tile zoom level 3 max tile zoom level
	 * 
	 * @return
	 */
	private static Object[] getTilesMetaData(final String directory) {
		if (directory == null) {
			throw new IllegalArgumentException();
		}
		final Object[] metaData = new Object[METADATA_SIZE];

		final File file = new File(directory);

		metaData[0] = directory.substring(directory.lastIndexOf('/') + 1);
		final File[] tileDirectories = file.listFiles();

		 
		String extension = getExtension(file);
		

		 String lastZoomTileDirectory=null;		
		for(File f : tileDirectories){
			if(f.isDirectory()){
				lastZoomTileDirectory = f.toString();
			}
		}
		int i = 0;
		int y = -1;
		int x = -1;
		for (i = 0; i < tileDirectories.length; i++) {
			if (tileDirectories[i].isDirectory()) {
				try {
					Pair<Integer, Integer> pair = searchBoundingBox(
							tileDirectories[i], extension);
					x = pair.first;
					y = pair.second;
				} catch (Exception e) {
					Log.d("TESTX", "EXCEPTION METADATA");
					y = -1;
					break;
				}
				if (y >= 0) {
					break;
				}
			}
		}
		BoundingBoxE6 boundingBox = new BoundingBoxE6(90, 180, -90, -180);

		String firstZoomTileDirectory ;
		if(i >= tileDirectories.length){
			firstZoomTileDirectory = tileDirectories[0].toString();
		}else{
			firstZoomTileDirectory = tileDirectories[i].toString();

		}

		int minZoom, maxZoom;
		try {
			minZoom = Integer.parseInt(firstZoomTileDirectory
					.substring(firstZoomTileDirectory.lastIndexOf('/') + 1));
			maxZoom = Integer.parseInt(lastZoomTileDirectory
					.substring(lastZoomTileDirectory.lastIndexOf('/') + 1));
		} catch (Exception e) {
			minZoom = OpenStreetMapTileProviderConstants.MINIMUM_ZOOMLEVEL;
			maxZoom = OpenStreetMapTileProviderConstants.MAXIMUM_ZOOMLEVEL;
		}
		if (y >= 0) {

			BoundingBox box = tile2boundingBox(x, y, minZoom);
			boundingBox = new BoundingBoxE6(box.north, box.east, box.south,
					box.west);
			Log.d("TESTX", "X : " + x + " / Y : " + y + " / zoom : " + minZoom);
			Log.d("TESTX", "North : " + box.north + " / EAST : " + box.east
					+ " / SOUTH : " + box.south + " / WEST : " + box.west);
		}

		metaData[1] = extension;
		metaData[2] = minZoom;
		metaData[3] = maxZoom;
		metaData[4] = boundingBox;

		return metaData;

	}

	private static Pair<Integer, Integer> searchBoundingBox(
			File tileDirectories, final String extension) throws Exception {
		Log.d("TESTX", "SEARCH BOUNDING BOX");
		File[] dirs = tileDirectories.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory()) {
					return true;
				}
				return false;
			}
		});

		for (File dir : dirs) {
			Log.d("TESTX", dir.getPath());
			if (dir.isDirectory()) {
				File[] tiles = dir.listFiles(new FileFilter() {

					@Override
					public boolean accept(File pathname) {
						return FileUtils.getExtension(pathname.getPath())
								.equals(extension);

					}
				});
				if (tiles.length > 0) {

					String tileY = tiles[0].getPath();
					String tileNameWithExtensionY = tileY.substring(tileY
							.lastIndexOf('/') + 1);
					String tileNameWithoutExtensionY = tileNameWithExtensionY
							.split(extension)[0];
					Log.d("TESTX", "plop : " + tileNameWithExtensionY);
					Log.d("TESTX", "plop : " + tileNameWithoutExtensionY);
					Log.d("TESTX", "plop : " + extension);
					String tileX = dir.getPath();
					String tileNameWithoutExtensionX = tileX.substring(tileX
							.lastIndexOf("/") + 1);
					Log.d("TESTXB",
							"X : "
									+ Integer
									.parseInt(tileNameWithoutExtensionX)
									+ " / Y : "
									+ Integer
									.parseInt(tileNameWithoutExtensionY));

					return new Pair<Integer, Integer>(
							Integer.parseInt(tileNameWithoutExtensionX),
							Integer.parseInt(tileNameWithoutExtensionY));
				}
			}
		}
		return new Pair<Integer, Integer>(-1, -1);
	}

	/**
	 * Get the extension of a file (e.g ".png" )
	 * 
	 * @param file
	 * @return
	 */
	private static String getExtension(final File file) {

		if (file == null) {
			throw new IllegalArgumentException();
		}

		if(file.isDirectory()){
			for(File f : file.listFiles()){
				String ext = getExtension(f);
				if(ext != null) {
					return ext ;
				}
			}
		}
		String ext =FileUtils.getExtension(file.toString()) ;
		if(ext != null && (
				ext.equals(".png") || ext.equals(".PNG") || ext.equals(".jpg") || ext.equals(".JPG") || ext.equals(".JPEG"))){
			return ext ;
		}
		return null ;
	}

	private static class BoundingBox {
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
	private static BoundingBox tile2boundingBox(final int x, final int y,
			final int zoom) {
		BoundingBox bb = new BoundingBox();
		bb.north = tile2lat(y, zoom);
		bb.south = tile2lat(y + 1, zoom);
		bb.west = tile2lon(x, zoom);
		bb.east = tile2lon(x + 1, zoom);
		getTileNumber(bb.north, bb.west, zoom); 
		return bb;
	}

	private static double tile2lon(int x, int z) {
		return x / Math.pow(2.0, z) * 360.0 - 180;
	}

	private static double tile2lat(int y, int z) {
		double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
		/* WARNING MATH.ABS IS VERY NOT SURE SEE OSM FORMULA OPENSOURCE HIPPY :) */
		return Math.toDegrees(Math.atan(Math.sinh(Math.abs(n))));
	}
	
	 public static void getTileNumber(final double lat, final double lon, final int zoom) {
		   int xtile = (int)Math.floor( (lon + 180) / 360 * (1<<zoom) ) ;
		   int ytile = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<zoom) ) ;
		    Log.d("","" + zoom + "/" + xtile + "/" + ytile);
	 }


}
