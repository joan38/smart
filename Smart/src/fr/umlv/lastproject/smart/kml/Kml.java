package fr.umlv.lastproject.smart.kml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import fr.umlv.lastproject.smart.database.GeometryRecord;
import fr.umlv.lastproject.smart.dataexport.KmlExport;
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

	public Kml(File file) {
		this.file = file;
		
		for (GeometryType type : GeometryType.values()) {
			this.geometries.put(type, new ArrayList<Geometry>());
		}
	}

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

	public void writeKml(List<GeometryRecord> geometries, String folderName) throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document kml = docBuilder.newDocument();

		// kml element
		Element kmlElement = kml.createElement("kml");
		kml.appendChild(kmlElement);

		// Set attributes to kml element
		Attr xmlns = kml.createAttribute("xmlns");
		xmlns.setValue("http://www.opengis.net/kml/2.2");
		kmlElement.setAttributeNode(xmlns);

		Attr xmlnsGx = kml.createAttribute("xmlns:gx");
		xmlnsGx.setValue("http://www.google.com/kml/ext/2.2");
		kmlElement.setAttributeNode(xmlnsGx);

		Attr xmlnsKml = kml.createAttribute("xmlns:kml");
		xmlnsKml.setValue("http://www.opengis.net/kml/2.2");
		kmlElement.setAttributeNode(xmlnsKml);

		Attr xmlnsAtom = kml.createAttribute("xmlns:atom");
		xmlnsAtom.setValue("http://www.w3.org/2005/Atom");
		kmlElement.setAttributeNode(xmlnsAtom);

		// Document element
		Element documentElement = kml.createElement("Document");
		kmlElement.appendChild(documentElement);

		// name element
		Element documentNameElement = kml.createElement("name");
		documentNameElement.appendChild(kml.createTextNode(file.getName()));
		documentElement.appendChild(documentNameElement);

		// Folder element
		Element folderElement = kml.createElement("Folder");
		documentElement.appendChild(folderElement);

		// name element
		Element folderNameElement = kml.createElement("name");
		folderNameElement.appendChild(kml.createTextNode(folderName));
		folderElement.appendChild(folderNameElement);

		// // open element
		// Element openElement = kml.createElement("open");
		// folderNameElement.appendChild(kml.createTextNode("1"));
		// folderElement.appendChild(openElement);

		for (GeometryRecord geometry : geometries) {
			// Placemark element
			Element placemarkElement = kml.createElement("Placemark");
			folderElement.appendChild(placemarkElement);

			// name element
			Element placemarkNameElement = kml.createElement("name");
			placemarkNameElement.appendChild(kml.createTextNode(String
					.valueOf(geometry.getId())));
			placemarkElement.appendChild(placemarkNameElement);

			// description element
			Element descriptionElement = kml.createElement("description");
			descriptionElement.appendChild(kml.createTextNode(" "));
			placemarkElement.appendChild(descriptionElement);

			// Polygon or Point or LineString element
			Element geometryElement = KmlExport.pepareGeometryElement(kml,
					geometry);
			placemarkElement.appendChild(geometryElement);
		}

		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(kml);
		StreamResult result = new StreamResult(file);

		transformer.transform(source, result);
	}
}
