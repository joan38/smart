package fr.umlv.lastproject.smart.kml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import fr.umlv.lastproject.smart.layers.Geometry;
import fr.umlv.lastproject.smart.layers.Geometry.GeometryType;
import fr.umlv.lastproject.smart.layers.LineGeometry;
import fr.umlv.lastproject.smart.layers.PointGeometry;
import fr.umlv.lastproject.smart.layers.PolygonGeometry;

/**
 * 
 * @author Thibault Douilly
 * 
 * @Description : This kml class can parse a .kml file and return all the
 *              geometries
 * 
 */
public class Kml {

	private File file;
	private final Map<GeometryType, List<Geometry>> geometries = new HashMap<GeometryType, List<Geometry>>();

	private static final String GEOMETRIESTAGS = "Point|LineString|Polygon";
	private static final String POINTTAG = "Point";
	private static final String LINETAG = "LineString";
	private static final String POLYGONTAG = "Polygon";
	private static final String COORDINATESTAG = "coordinates";

	public Kml() {
		for (GeometryType type : GeometryType.values()) {
			this.geometries.put(type, new ArrayList<Geometry>());
		}
	}

	/**
	 * Use to read the .kml files and parse the different geometry
	 * 
	 * @param path
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public void readKml(String path) throws XmlPullParserException, IOException {
		if (null != path) {
			this.file = new File(path);
			readKml();
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Use to read the .kml files and parse the different geometry
	 * 
	 * @param file
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public void readKml(File file) throws XmlPullParserException, IOException {
		if (null != file) {
			this.file = file;
			readKml();
		} else {
			throw new IllegalArgumentException();
		}
	}

	private void readKml() throws XmlPullParserException, IOException {

		// initialize the parser
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();
		FileInputStream stream = new FileInputStream(file);
		xpp.setInput(stream, "UTF-8");
		int eventType = xpp.getEventType();

		// create all the objects use in the while loop
		boolean filter = false;
		List<PointGeometry> points = new ArrayList<PointGeometry>();

		// parse the file in a while loop
		while (eventType != XmlPullParser.END_DOCUMENT) {

			// if datas between 2 tags <coordinates> was found
			if (eventType == XmlPullParser.TEXT && filter) {
				String original = xpp.getText().trim();
				original = original.replace('\n', ' ');
				String[] point = original.split(" +");

				// split datas to return one or more points
				for (int i = 0; i < point.length; i++) {

					String[] coordinates = point[i].split(",");

					/*
					 * coordinates[0] = x coordinates[1] = y coordinates[2] = z
					 * (z not use)
					 */
					if (coordinates.length > 2) {
						points.add(new PointGeometry(Double
								.parseDouble(coordinates[1]), Double
								.parseDouble(coordinates[0])));
					}
				}

				// if a <coordinates> tag was found
			} else if (eventType == XmlPullParser.START_TAG
					&& COORDINATESTAG.contains(xpp.getName())) {

				filter = true;

				// if the end of a <coordinates> tag was found
			} else if (eventType == XmlPullParser.END_TAG
					&& GEOMETRIESTAGS.contains(xpp.getName())) {

				filter = false;

				// add the geometry found in the general Map geometries
				if (xpp.getName().equals(POINTTAG)) {
					geometries.get(GeometryType.POINT).add(points.get(0));
				} else if (xpp.getName().equals(LINETAG)) {
					geometries.get(GeometryType.LINE).add(
							new LineGeometry(points));
				} else if (xpp.getName().equals(POLYGONTAG)) {
					geometries.get(GeometryType.POLYGON).add(
							new PolygonGeometry(points));
				}
				points = new ArrayList<PointGeometry>();

			}

			eventType = xpp.next();

		}
	}

	/**
	 * Return all geometries found with the reaKml() method and with the
	 * specified type in params
	 * 
	 * @param type
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public List<Geometry> getGeometry(GeometryType type)
			throws XmlPullParserException, IOException {
		return geometries.get(type);
	}

	/**
	 * Return all geometries found with the readKml() method
	 * 
	 * @param type
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public Map<GeometryType, List<Geometry>> getGeometries()
			throws XmlPullParserException, IOException {
		return geometries;
	}

	public void writeKml(String path) {

	}

	public void writeKml(File file) {

	}

}
