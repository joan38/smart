package fr.umlv.lastproject.smart.dataexport;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.umlv.lastproject.smart.database.DbManager;
import fr.umlv.lastproject.smart.database.GeometryRecord;
import fr.umlv.lastproject.smart.database.PointRecord;
import fr.umlv.lastproject.smart.form.Mission;
import fr.umlv.lastproject.smart.kml.Kml;

public class KmlExport {
	
	public static void exportMission(File kmlFile, Mission mission) throws ParserConfigurationException, TransformerException {
		DbManager dbm = new DbManager();
		dbm.open(mission.getContext());
		
		Kml kml = new Kml(kmlFile);
		kml.writeKml(dbm.getGeometriesFromMission(mission.getId()), mission.getTitle());
		
		dbm.close();
	}

	public static Element pepareGeometryElement(Document kml,
			GeometryRecord geometry) {
		// Polygon or Point or LineString element
		Element geomertryElement = kml.createElement(geometry.getType()
				.getKmlName());

		switch (geometry.getType()) {
		case POINT:
		case LINE:
			Element coordinatesElement = prepareCoordinatesElement(kml, geometry);
			geomertryElement.appendChild(coordinatesElement);
			break;

		case POLYGON:
			Element outerBoundaryIsElement = prepareOuterBoundaryIsElement(kml,
					geometry);
			geomertryElement.appendChild(outerBoundaryIsElement);
			break;

		default:
			throw new IllegalStateException(
					"The given GeometryType is not supported for the KML export");
		}
		
		return geomertryElement;
	}

	private static Element prepareCoordinatesElement(Document kml,
			GeometryRecord geometry) {
		// coordinates element
		Element coordinatesElement = kml.createElement("coordinates");
		StringBuilder coordinates = new StringBuilder();
		for (PointRecord point : geometry.getPointsRecord()) {
			coordinates.append(point.getY()).append(",");
			coordinates.append(point.getX()).append(",");
			if (point.getZ() == -1.0) {
				coordinates.append(0).append(" ");
			} else {
				coordinates.append(point.getZ()).append(" ");
			}
		}
		coordinatesElement.appendChild(kml.createTextNode(coordinates
				.toString()));

		return coordinatesElement;
	}

	private static Element prepareOuterBoundaryIsElement(Document kml,
			GeometryRecord geometry) {
		// outerBoundaryIs element
		Element outerBoundaryIsElement = kml.createElement("outerBoundaryIs");

		// LinearRing element
		Element linearRingElement = kml.createElement("LinearRing");
		outerBoundaryIsElement.appendChild(linearRingElement);

		Element coordinatesElement = prepareCoordinatesElement(kml, geometry);
		linearRingElement.appendChild(coordinatesElement);

		return outerBoundaryIsElement;
	}
}
