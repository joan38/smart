/* 
 * Copyright (C) 2007-2008 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.umlv.lastproject.smart.browser.utils;

import java.io.File;
import java.io.FileFilter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Video;
import android.util.Log;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.browser.FileChooserActivity;

/**
 * @version 2009-07-03
 * 
 * @author Peli, marc barat
 * 
 */
public final class FileUtils {
	/** TAG for log messages. */
	private static final String TAG = "FileUtils";

	// Set to true to enable logging
	private static final boolean DEBUG = false;

	public static final String MIME_TYPE_AUDIO = "audio/*";
	public static final String MIME_TYPE_TEXT = "text/*";
	public static final String MIME_TYPE_IMAGE = "image/*";
	public static final String MIME_TYPE_VIDEO = "video/*";
	public static final String MIME_TYPE_APP = "application/*";

	private static FileFilter fileFilter;

	public static final String[] SHP_TYPE = { ".shp" };
	public static final String[] KML_TYPE = { ".kml" };
	public static final String[] CSV_TYPE = { ".csv" };
	public static final String[] TIF_TYPE = { ".tif", ".tiff" };
	/* We do not want any file displayed */
	public static final String[] DIRECTORY_TYPE = { "?" };
	public static final String[] FORM_TYPE = { ".form" };

	private FileUtils() {

	}

	/**
	 * Gets the extension of a file name, like ".png" or ".jpg".
	 * 
	 * @param uri
	 * @return Extension including the dot("."); "" if there is no extension;
	 *         null if uri was null.
	 */
	public static String getExtension(String uri) {
		if (uri == null) {
			return null;
		}

		int dot = uri.lastIndexOf('.');
		if (dot >= 0) {
			return uri.substring(dot);
		} else {
			// No extension.
			return "";
		}
	}

	/**
	 * Returns true if uri is a media uri.
	 * 
	 * @param uri
	 * @return
	 */
	public static boolean isMediaUri(Uri uri) {
		String uriString = uri.toString();
		return (uriString.startsWith(Audio.Media.INTERNAL_CONTENT_URI
				.toString())
				|| uriString.startsWith(Audio.Media.EXTERNAL_CONTENT_URI
						.toString())
				|| uriString.startsWith(Video.Media.INTERNAL_CONTENT_URI
						.toString()) || uriString
					.startsWith(Video.Media.EXTERNAL_CONTENT_URI.toString()));

	}

	/**
	 * Convert File into Uri.
	 * 
	 * @param file
	 * @return uri
	 */
	public static Uri getUri(File file) {
		if (file != null) {
			return Uri.fromFile(file);
		}
		return null;
	}

	/**
	 * Convert Uri into File.
	 * 
	 * @param uri
	 * @return file
	 */
	public static File getFile(Uri uri) {
		if (uri != null) {
			String filepath = uri.getPath();
			if (filepath != null) {
				return new File(filepath);
			}
		}
		return null;
	}

	/**
	 * Returns the path only (without file name).
	 * 
	 * @param file
	 * @return
	 */
	public static File getPathWithoutFilename(File file) {
		if (file != null) {
			if (file.isDirectory()) {
				// no file to be split off. Return everything
				return file;
			} else {
				String filename = file.getName();
				String filepath = file.getAbsolutePath();

				// Construct path without file name.
				String pathwithoutname = filepath.substring(0,
						filepath.length() - filename.length());
				if (pathwithoutname.endsWith("/")) {
					pathwithoutname = pathwithoutname.substring(0,
							pathwithoutname.length() - 1);
				}
				return new File(pathwithoutname);
			}
		}
		return null;
	}

	/**
	 * Constructs a file from a path and file name.
	 * 
	 * @param curdir
	 * @param file
	 * @return
	 */
	public static File getFile(String curdir, String file) {
		String separator = "/";
		if (curdir.endsWith("/")) {
			separator = "";
		}

		return new File(curdir + separator + file);
	}

	public static File getFile(File curdir, String file) {
		return getFile(curdir.getAbsolutePath(), file);
	}

	/**
	 * Get a file path from a Uri.
	 * 
	 * @param context
	 * @param uri
	 * @return
	 * @throws URISyntaxException
	 * 
	 * @author paulburke
	 */
	public static String getPath(Context context, Uri uri)
			throws URISyntaxException {

		if (DEBUG) {
			Log.d(TAG + " File -",
					"Authority: " + uri.getAuthority() + ", Fragment: "
							+ uri.getFragment() + ", Port: " + uri.getPort()
							+ ", Query: " + uri.getQuery() + ", Scheme: "
							+ uri.getScheme() + ", Host: " + uri.getHost()
							+ ", Segments: " + uri.getPathSegments().toString());
		}

		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = context.getContentResolver().query(uri, projection,
						null, null, null);
				int columnIndex = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(columnIndex);
				}
			} catch (Exception e) {
				// Nothing to do
			}
		}

		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * Load MIME types from XML
	 * 
	 * @param context
	 * @return
	 */
	private static MimeTypes getMimeTypes(Context context) {
		MimeTypes mimeTypes = null;
		final MimeTypeParser mtp = new MimeTypeParser();
		final XmlResourceParser in = context.getResources().getXml(
				R.xml.mimetypes);

		try {
			mimeTypes = mtp.fromXmlResource(in);
		} catch (Exception e) {
			if (DEBUG) {
				Log.e(TAG, "getMimeTypes", e);
			}

		}
		return mimeTypes;
	}

	/**
	 * Get the file MIME type
	 * 
	 * @param context
	 * @param file
	 * @return
	 */
	public static String getMimeType(Context context, File file) {
		String mimeType = null;
		final MimeTypes mimeTypes = getMimeTypes(context);
		if (file != null) {
			mimeType = mimeTypes.getMimeType(file.getName());
		}

		return mimeType;
	}

	private static final String HIDDEN_PREFIX = ".";

	/**
	 * File and folder comparator.
	 * 
	 * @author paulburke
	 */
	private static Comparator<File> mComparator = new Comparator<File>() {
		public int compare(File f1, File f2) {
			// Sort alphabetically by lower case, which is much cleaner
			return f1.getName().toLowerCase()
					.compareTo(f2.getName().toLowerCase());
		}
	};

	/**
	 * File (not directories) filter.
	 * 
	 * @author marc barat
	 */
	public static void setFileFilter(final String[] filter) {
		fileFilter = new FileFilter() {

			@Override
			public boolean accept(File file) {
				final String fileName = file.getName();
				// Return files only (not directories) and skip hidden files
				if (file.isFile()) {
					for (String filt : filter) {
						if (!fileName.endsWith(filt)) {
							return false;
						}
					}
					return true;

				}
				return false;
			}
		};
	}

	/**
	 * Folder (directories) filter.
	 * 
	 * @author paulburke
	 */
	private static FileFilter mDirFilter = new FileFilter() {
		public boolean accept(File file) {
			final String fileName = file.getName();
			// Return directories only and skip hidden directories
			return file.isDirectory() && !fileName.startsWith(HIDDEN_PREFIX);
		}
	};

	/**
	 * Get a list of Files in the give path
	 * 
	 * @param path
	 * @return Collection of files in give directory
	 * 
	 * @author paulburke
	 */
	public static List<File> getFileList(String path) {
		ArrayList<File> list = new ArrayList<File>();

		// Current directory File instance
		final File pathDir = new File(path);

		// List file in this directory with the directory filter
		final File[] dirs = pathDir.listFiles(mDirFilter);
		if (dirs != null) {
			// Sort the folders alphabetically
			Arrays.sort(dirs, mComparator);
			// Add each folder to the File list for the list adapter
			for (File dir : dirs) {
				list.add(dir);
			}

		}

		// List file in this directory with the file filter
		final File[] files = pathDir.listFiles(fileFilter);
		if (files != null) {
			// Sort the files alphabetically
			Arrays.sort(files, mComparator);
			// Add each file to the File list for the list adapter
			for (File file : files) {
				list.add(file);
			}

		}

		return list;
	}

	/**
	 * Get the Intent for selecting content to be used in an Intent Chooser.
	 * 
	 * @param filters
	 *            either shp, kml or tiff using constants defined in
	 *            FileUtils.java
	 * @return The intent for opening a file with Intent.createChooser()
	 * 
	 * @author paulburke, marc barat
	 */
	public static Intent createGetContentIntent(String[] filters, String path) {
		// Implicitly allow the user to select a particular kind of data
		final Intent intent = new Intent("fr.umlv.lastproject.smart.browser");
		setFileFilter(filters);
		// The MIME data type filter
		intent.setType("*/*");
		// Only return URIs that can be opened with ContentResolver
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.putExtra(FileChooserActivity.PATH, path);
		return intent;
	}

	public static Intent createEmailIntent(ArrayList<Uri> files) {
		Intent emailIntent = new Intent(
				android.content.Intent.ACTION_SEND_MULTIPLE);
		emailIntent.setType("plain/text");
		emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);

		return emailIntent;
	}
}