package fr.umlv.lastproject.smart;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.os.Environment;

/**
 * Writes a GPX file.
 * 
 * @author Marc Barat
 * 
 */
public final class GPXWriter {

	/**
	 * XML header.
	 */
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";

	/**
	 * GPX opening tag
	 */
	private static final String TAG_GPX = "<gpx"
			+ " xmlns=\"http://www.topografix.com/GPX/1/1\""
			+ " version=\"1.1\""
			+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
			+ " xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd \">";
	
	
	private static final String TRACKS_FOLDER=Environment.getExternalStorageDirectory()+ "/SMART/"+"tracks/";

	private GPXWriter() {

	}

	/**
	 * Writes the GPX file
	 * 
	 * @param trackName
	 *            Name of the GPX track (metadata)
	 * @param cTrackPoints
	 *            Cursor to track points.
	 * @param cWayPoints
	 *            Cursor to way points.
	 * @param target
	 *            Target GPX file
	 * @throws IOException
	 */
	public static void writeGpxFile(String trackName,
			List<TrackPoint> trackPoints) throws IOException {
		if (trackName == null || trackPoints==null){
			throw new IllegalArgumentException();
		}
		if(trackPoints.isEmpty()){
			return;
		}
		final File appFolder = new File(TRACKS_FOLDER);
		appFolder.mkdirs();
		
		
		final File trackFile = new File(TRACKS_FOLDER+trackName+".gpx");
		
		final FileWriter fileWriter = new FileWriter(trackFile);

		fileWriter.write(XML_HEADER + "\n");
		fileWriter.write(TAG_GPX + "\n");
		
		writeTrackPoints(trackName, fileWriter, trackPoints);
		fileWriter.write("</gpx>");

		fileWriter.close();
	}

	/**
	 * Iterates on track points and write them.
	 * 
	 * @param trackName
	 *            Name of the track (metadata).
	 * @param fileWriter
	 *            Writer to the target file.
	 * @param trackPoints
	 *            {@link List}<{@link TrackPoint}>
	 * @throws IOException
	 */
	public static void writeTrackPoints(final String trackName,
			final FileWriter fileWriter, final List<TrackPoint> trackPoints)
			throws IOException {
		

		final StringBuilder builder = new StringBuilder();
		builder.append("\t").append("<trk>");
		builder.append("\t\t").append("<name>").append(trackName).append("</name>").append("\n");
		builder.append("\t\t").append("<trkseg>").append("\n");
		for (TrackPoint trackPoint : trackPoints) {
			builder.append("\t\t\t").append("<trkpt lat=\"").append(trackPoint.getLatitude()).append("\" ").append("lon=\"").append(trackPoint.getLongitude()).append("\">");
			builder.append("<ele>").append(trackPoint.getElevation()).append("</ele>");
			builder.append("<time>").append(trackPoint.getTime()).append("</time>");
			builder.append("</trkpt>").append("\n");
		}

		
		builder.append("\t\t").append("</trkseg>").append("\n");
		builder.append("\t").append("</trk>").append("\n");
		
		fileWriter.write(builder.toString());
	}

}