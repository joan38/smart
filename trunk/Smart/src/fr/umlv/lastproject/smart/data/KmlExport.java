package fr.umlv.lastproject.smart.data;

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

import android.content.Context;
import fr.umlv.lastproject.smart.database.BooleanFieldRecord;
import fr.umlv.lastproject.smart.database.DbManager;
import fr.umlv.lastproject.smart.database.FieldRecord;
import fr.umlv.lastproject.smart.database.FormRecord;
import fr.umlv.lastproject.smart.database.GeometryRecord;
import fr.umlv.lastproject.smart.database.HeightFieldRecord;
import fr.umlv.lastproject.smart.database.ListFieldRecord;
import fr.umlv.lastproject.smart.database.MissionRecord;
import fr.umlv.lastproject.smart.database.NumericFieldRecord;
import fr.umlv.lastproject.smart.database.PictureFieldRecord;
import fr.umlv.lastproject.smart.database.PointRecord;
import fr.umlv.lastproject.smart.database.TextFieldRecord;
import fr.umlv.lastproject.smart.utils.SmartConstants;
import fr.umlv.lastproject.smart.utils.SmartException;

/**
 * Utils for export in Kml
 * 
 * @author Joan Goyeau
 * 
 */
public abstract class KmlExport {

	/**
	 * Export the given mission in a Kml file.
	 * 
	 * @param kmlFile
	 * @param mission
	 * @throws KmlExportException
	 * @throws SmartException 
	 */
	public static void exportMission(String path, long idMission,
			Context context) throws KmlExportException, SmartException {
		DbManager dbm = new DbManager();
		dbm.open(context);
		MissionRecord mission = dbm.getMission(idMission);

		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document kml = docBuilder.newDocument();

			// kml element
			Element kmlElement = kml.createElement(Kml.KMLTAG);
			kml.appendChild(kmlElement);

			// Set attributes to kml element
			Attr xmlns = kml.createAttribute(Kml.KMLNSTAG);
			xmlns.setValue("http://www.opengis.net/kml/2.2");
			kmlElement.setAttributeNode(xmlns);

			Attr xmlnsGx = kml.createAttribute(Kml.KMLNSGXTAG);
			xmlnsGx.setValue("http://www.google.com/kml/ext/2.2");
			kmlElement.setAttributeNode(xmlnsGx);

			Attr xmlnsKml = kml.createAttribute(Kml.XMLNSKMLTAG);
			xmlnsKml.setValue("http://www.opengis.net/kml/2.2");
			kmlElement.setAttributeNode(xmlnsKml);

			Attr xmlnsAtom = kml.createAttribute(Kml.XMLNSATOMTAG);
			xmlnsAtom.setValue("http://www.w3.org/2005/Atom");
			kmlElement.setAttributeNode(xmlnsAtom);

			// Document element
			Element documentElement = kml.createElement(Kml.DOCUMENTTAG);
			kmlElement.appendChild(documentElement);

			// name element
			Element documentNameElement = kml.createElement(Kml.NAMETAG);
			documentNameElement.appendChild(kml.createTextNode(mission.getTitle() + ".kml"));
			documentElement.appendChild(documentNameElement);

			// Folder element
			Element folderElement = kml.createElement(Kml.FOLDERTAG);
			documentElement.appendChild(folderElement);

			// name element
			Element folderNameElement = kml.createElement(Kml.NAMETAG);
			folderNameElement
					.appendChild(kml.createTextNode(mission.getTitle()));
			folderElement.appendChild(folderNameElement);

			

			for (GeometryRecord geometry : dbm.getGeometriesFromMission(mission
					.getId())) {
				// Placemark element
				Element placemarkElement = kml.createElement(Kml.PLACEMARKTAG);
				folderElement.appendChild(placemarkElement);

				// name element
				Element placemarkNameElement = kml.createElement(Kml.NAMETAG);
				placemarkNameElement.appendChild(kml.createTextNode(String
						.valueOf(geometry.getId())));
				placemarkElement.appendChild(placemarkNameElement);

				// description element
				Element descriptionElement = KmlExport
						.pepareDescriptionElement(kml, dbm.getFormRecord(geometry.getIdFormRecord(), mission.getForm().getName()));
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
			StreamResult result = new StreamResult(path + "/" + mission.getTitle() + ".kml");

			transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			throw new KmlExportException("Unable to export the mission", e);
		} catch (TransformerException e) {
			throw new KmlExportException("Unable to export the mission", e);
		}

		dbm.close();
	}

	private static Element pepareGeometryElement(Document kml, GeometryRecord geometry) {
		// Polygon or Point or LineString element
		Element geomertryElement = kml.createElement(geometry.getType()
				.getKmlName());

		switch (geometry.getType()) {
		case POINT:
		case LINE:
			Element coordinatesElement = prepareCoordinatesElement(kml,
					geometry);
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
		Element outerBoundaryIsElement = kml
				.createElement(Kml.OUTERBOUNDARYTAG);

		// LinearRing element
		Element linearRingElement = kml.createElement(Kml.LINEARRINGTAG);
		outerBoundaryIsElement.appendChild(linearRingElement);

		Element coordinatesElement = prepareCoordinatesElement(kml, geometry);
		linearRingElement.appendChild(coordinatesElement);

		return outerBoundaryIsElement;
	}

	private static Element pepareDescriptionElement(Document kml, FormRecord formRecord) {
		Element descriptionElement = kml.createElement(Kml.DESCRIPTIONTAG);
		StringBuilder description = new StringBuilder(formRecord.getName());

		for (FieldRecord field : formRecord.getFields()) {
			description.append(field.getField().getLabel() + ": ");
			switch (field.getField().getType()) {
			case SmartConstants.TEXT_FIELD:
				TextFieldRecord tf = (TextFieldRecord) field;
				description.append(tf.getValue());
				break;

			case SmartConstants.NUMERIC_FIELD:
				NumericFieldRecord nf = (NumericFieldRecord) field;
				description.append(nf.getValue());
				break;

			case SmartConstants.BOOLEAN_FIELD:
				BooleanFieldRecord bf = (BooleanFieldRecord) field;
				description.append(bf.getValue());
				break;

			case SmartConstants.LIST_FIELD:
				ListFieldRecord lf = (ListFieldRecord) field;
				description.append(lf.getValue());
				break;

			case SmartConstants.PICTURE_FIELD:
				PictureFieldRecord pf = (PictureFieldRecord) field;
				description.append(pf.getValue());
				break;

			case SmartConstants.HEIGHT_FIELD:
				HeightFieldRecord hf = (HeightFieldRecord) field;
				description.append(hf.getValue());
				break;

			default:
				throw new IllegalStateException("Unkown field");
			}
			
			description.append("\n");
		}

		descriptionElement.appendChild(kml.createTextNode(description
				.toString()));
		return descriptionElement;
	}
}
