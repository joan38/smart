package fr.umlv.lastproject.smart.kml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import fr.umlv.lastproject.smart.layers.Geometry;
import fr.umlv.lastproject.smart.layers.Geometry.GeometryType;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.layers.LineSymbology;
import fr.umlv.lastproject.smart.layers.PointSymbology;
import fr.umlv.lastproject.smart.layers.PolygonSymbology;
import fr.umlv.lastproject.smart.layers.Symbology;

/**
 * 
 * @author Thibault Douilly
 * 
 * @Description : Import a Kml file
 * 
 */
public class KmlImport {

	/**
	 * Return a list of GeometryLayer with all the geometry type in the kml
	 * 
	 * @param contexte
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */

	public static List<GeometryLayer> getLayersFromKML(String path,
			Context contexte) throws XmlPullParserException, IOException {
		Kml kml = new Kml(new File(path));
		kml.readKml();
		List<GeometryLayer> overlays = new ArrayList<GeometryLayer>();
		for (GeometryType type : GeometryType.values()) {
			List<Geometry> geometries = (ArrayList<Geometry>) kml
					.getGeometries().get(type);
			GeometryLayer overlay = new GeometryLayer(contexte, geometries);

			Symbology symbology = null;
			String name = path.substring(path.lastIndexOf('/') + 1,
					path.lastIndexOf('.'));
			switch (type) {
			case POINT:
				symbology = new PointSymbology();
				name += "_POINT";
				break;

			case LINE:
				symbology = new LineSymbology();
				name += "_LINE";
				break;

			case POLYGON:
				symbology = new PolygonSymbology();
				name += "_POLYGON";
				break;

			default:
				throw new IllegalStateException(
						"The given GeometryType is not supported for the KML export");
			}
			overlay.setSymbology(symbology);
			overlay.setType(type);
			overlay.setName(name);
			overlays.add(overlay);
		}
		return overlays;
	}

	/**
	 * Return a GeometryLayer of the kml with only the geometry type specified
	 * on params
	 * 
	 * @param contexte
	 * @param type
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static GeometryLayer getLayerFromKML(String path, GeometryType type,
			Context contexte) throws XmlPullParserException, IOException {
		Kml kml = new Kml(new File(path));
		kml.readKml();
		ArrayList<Geometry> geometries = (ArrayList<Geometry>) kml
				.getGeometry(type);
		GeometryLayer overlay = new GeometryLayer(contexte, geometries);

		Symbology symbology = null;
		switch (type) {
		case POINT:
			symbology = new PointSymbology();
			break;

		case LINE:
			symbology = new LineSymbology();
			break;

		case POLYGON:
			symbology = new PolygonSymbology();
			break;

		default:
			throw new IllegalStateException(
					"The given GeometryType is not supported for the KML export");
		}
		overlay.setSymbology(symbology);
		overlay.setType(type);
		overlay.setName(path.substring(path.lastIndexOf('/') + 1,
				path.lastIndexOf('.')));

		return overlay;
	}
}