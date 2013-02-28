package fr.umlv.lastproject.smart.data;

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
import fr.umlv.lastproject.smart.layers.GeometryType;
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

	private final File file;
	private final Map<GeometryType, List<Geometry>> geometries = new HashMap<GeometryType, List<Geometry>>();

	public static final String GEOMETRIESTAGS = "Point|LineString|Polygon";
	public static final String POINTTAG = "Point";
	public static final String LINETAG = "LineString";
	public static final String POLYGONTAG = "Polygon";
	public static final String COORDINATESTAG = "coordinates";
	public static final String KMLTAG = "kml";
	public static final String KMLNSTAG = "xmlns";
	public static final String KMLNSGXTAG = "xmlns:gx";
	public static final String XMLNSKMLTAG = "xmlns:kml";
	public static final String XMLNSATOMTAG = "xmlns:atom";
	public static final String DOCUMENTTAG = "Document";
	public static final String NAMETAG = "name";
	public static final String FOLDERTAG = "Folder";
	public static final String PLACEMARKTAG = "Placemark";
	public static final String DESCRIPTIONTAG = "description";
	public static final String OUTERBOUNDARYTAG = "outerBoundaryIs";
	public static final String LINEARRINGTAG = "LinearRing";

	public Kml(File file) {
		this.file = file;
		//
		// for (GeometryType type : GeometryType.values()) {
		// this.geometries.put(type, new ArrayList<Geometry>());
		// }
	}

	/**
	 * Reader the Kml file given in the constructor.
	 * 
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public void readKml() throws XmlPullParserException, IOException {
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

				// if a geometry tag was found
			} else if (eventType == XmlPullParser.START_TAG
					&& GEOMETRIESTAGS.contains(xpp.getName())) {
				if (xpp.getName().equals(POINTTAG)) {
					this.geometries.put(GeometryType.POINT,
							new ArrayList<Geometry>());
				} else if (xpp.getName().equals(LINETAG)) {
					this.geometries.put(GeometryType.LINE,
							new ArrayList<Geometry>());
				} else if (xpp.getName().equals(POLYGONTAG)) {
					this.geometries.put(GeometryType.POLYGON,
							new ArrayList<Geometry>());
				}

				// if a <coordinates> tag was found
			} else if (eventType == XmlPullParser.START_TAG
					&& COORDINATESTAG.contains(xpp.getName())) {
				if (xpp.getName().equals(POINTTAG)) {
					this.geometries.put(GeometryType.POINT,
							new ArrayList<Geometry>());
				} else if (xpp.getName().equals(LINETAG)) {
					this.geometries.put(GeometryType.LINE,
							new ArrayList<Geometry>());
				} else if (xpp.getName().equals(POLYGONTAG)) {
					this.geometries.put(GeometryType.POLYGON,
							new ArrayList<Geometry>());
				}
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
}
