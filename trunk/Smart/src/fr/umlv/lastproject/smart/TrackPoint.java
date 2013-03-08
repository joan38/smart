package fr.umlv.lastproject.smart;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Defines a track point 1)Longitude 2)Latitude 3)Elevation 4)Time
 * 
 * @author Marc
 * 
 */
public class TrackPoint implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double longitude;
	private double latitude;
	private double elevation;
	private String time;
	/**
	 * Date format for a point timestamp.
	 */
	private static final SimpleDateFormat POINT_DATE_FORMATTER = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

	/**
	 * 
	 * @param longitude
	 *            og the point tracked
	 * @param latitude
	 *            of the point tracked
	 * @param elevation
	 *            of the point tracked
	 * @param time
	 *            when the point has been tracked
	 */
	public TrackPoint(final double longitude, final double latitude,
			final double elevation, final Date time) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.elevation = elevation;
		this.time = POINT_DATE_FORMATTER.format(time);
	}

	/**
	 * 
	 * @return the longitude of the point
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * 
	 * @return the latitude of the point
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * 
	 * @return the evelation of the point
	 */
	public double getElevation() {
		return elevation;
	}

	/**
	 * 
	 * @return the date when the point has been tracked
	 */
	public String getTime() {
		return time;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeDouble(latitude);
		out.writeDouble(longitude);
		out.writeDouble(elevation);
		out.writeObject(time);
	}

	/**
	 * 
	 * @param in
	 *            object to read
	 * @throws IOException
	 *             if object not readable
	 * @throws ClassNotFoundException
	 *             if class does not exist
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		this.latitude = in.readDouble();
		this.longitude = in.readDouble();
		this.elevation = in.readDouble();
		this.time = (String) in.readObject();
	}

}
