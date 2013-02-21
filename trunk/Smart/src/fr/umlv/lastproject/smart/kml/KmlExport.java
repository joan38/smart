package fr.umlv.lastproject.smart.kml;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.util.Log;
import android.widget.Toast;

import fr.umlv.lastproject.smart.database.DbManager;
import fr.umlv.lastproject.smart.database.GeometryRecord;
import fr.umlv.lastproject.smart.database.PointRecord;
import fr.umlv.lastproject.smart.form.Mission;
import fr.umlv.lastproject.smart.utils.SmartException;

/**
 * 
 * 
 * @author joan
 *
 */
public class KmlExport {
	
	public static void exportMission(File kmlFile, Mission mission) throws ParserConfigurationException, TransformerException {
		DbManager dbm = new DbManager();
		try {
			dbm.open(mission.getContext());
		} catch (SmartException e) {
			Toast.makeText(mission.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			Log.e("", e.getMessage());
		}
		
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
		Element coordinatesElement = kml.createElement(Kml.COORDINATESTAG);
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
		Element outerBoundaryIsElement = kml.createElement(Kml.OUTERBOUNDARYTAG);

		// LinearRing element
		Element linearRingElement = kml.createElement(Kml.LINEARRINGTAG);
		outerBoundaryIsElement.appendChild(linearRingElement);

		Element coordinatesElement = prepareCoordinatesElement(kml, geometry);
		linearRingElement.appendChild(coordinatesElement);

		return outerBoundaryIsElement;
	}
}
