package fr.umlv.lastproject.smart.dataimport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import fr.umlv.lastproject.smart.kml.Kml;
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
class KmlImport {

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
		Kml kml = new Kml();
		List<GeometryLayer> overlays = new ArrayList<GeometryLayer>();
		kml.readKml(path);
		for (GeometryType type : GeometryType.values()) {
			List<Geometry> geometries = (ArrayList<Geometry>) kml
					.getGeometries().get(type);
			GeometryLayer overlay = new GeometryLayer(contexte, geometries);

			Symbology symbology = null;
			String name = path.substring(path.lastIndexOf('/') + 1,
					path.lastIndexOf('.'));
			if (type == GeometryType.POINT) {
				symbology = new PointSymbology();
				name += "_POINT";
			} else if (type == GeometryType.LINE) {
				symbology = new LineSymbology();
				name += "_LINE";
			} else if (type == GeometryType.POLYGON) {
				symbology = new PolygonSymbology();
				name += "_POLYGON";
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
		Kml kml = new Kml();
		kml.readKml(path);
		ArrayList<Geometry> geometries = (ArrayList<Geometry>) kml
				.getGeometry(type);
		GeometryLayer overlay = new GeometryLayer(contexte, geometries);

		Symbology symbology = null;
		if (type == GeometryType.POINT) {
			symbology = new PointSymbology();
		} else if (type == GeometryType.LINE) {
			symbology = new LineSymbology();
		} else if (type == GeometryType.POLYGON) {
			symbology = new PolygonSymbology();
		}
		overlay.setSymbology(symbology);
		overlay.setType(type);
		overlay.setName(path.substring(path.lastIndexOf('/'),
				path.lastIndexOf('.')));

		return overlay;
	}
}