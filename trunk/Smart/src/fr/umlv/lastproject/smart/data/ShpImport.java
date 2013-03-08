package fr.umlv.lastproject.smart.data;

import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.Context;
import diewald_shapeFile.files.shp.shapeTypes.ShpPoint;
import diewald_shapeFile.files.shp.shapeTypes.ShpPolyLine;
import diewald_shapeFile.files.shp.shapeTypes.ShpPolygon;
import diewald_shapeFile.files.shp.shapeTypes.ShpShape;
import diewald_shapeFile.shapeFile.ShapeFile;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.layers.GeometryType;
import fr.umlv.lastproject.smart.layers.LineGeometry;
import fr.umlv.lastproject.smart.layers.PointGeometry;
import fr.umlv.lastproject.smart.layers.PolygonGeometry;
import fr.umlv.lastproject.smart.utils.SmartLogger;

public final class ShpImport {

	private static final Logger LOGGER = SmartLogger.getLocator().getLogger();

	private ShpImport() {
	}

	public static GeometryLayer getLayerFromShp(String file, Context context) {
		try {
			String[] split = file.split("/");
			String path = "";
			for (int i = 0; i < split.length - 1; i++) {
				path += split[i] + "/";
			}

			String fn = split[split.length - 1];
			fn = fn.replaceFirst(".shp", "");

			ShapeFile shp = new ShapeFile(path, fn).READ();
			ShpShape.Type type = shp.getSHP_shapeType();
			LOGGER.log(Level.SEVERE, "Import shape file :" + type.toString());
			switch (type) {
			case Point: {
				return getLayerFromPointShp(shp, context, fn);
			}
			case PolyLine: {
				return getLayerFromPolylineShp(shp, context, fn);
			}
			case Polygon: {
				return getLayerFromPolygonShp(shp, context, fn);
			}
			default:
				return null;

			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "unabled to import shape file");
		}

		return null;
	}

	private static GeometryLayer getLayerFromPolygonShp(ShapeFile shp,
			Context context, String fileName) {

		GeometryLayer gl = new GeometryLayer(context);

		gl.setType(GeometryType.POLYGON);
		gl.setName(fileName);

		for (int i = 0; i < shp.getSHP_shapeCount(); i++) {
			PolygonGeometry p = new PolygonGeometry();

			ShpPolygon polygon = shp.getSHP_shape(i);
			double[][] points = polygon.getPoints();

			for (int j = 0; j < polygon.getNumberOfPoints(); j++) {
				p.addPoint(new PointGeometry(points[j][1], points[j][0]));

			}
			gl.addGeometry(p);
		}

		return gl;
	}

	private static GeometryLayer getLayerFromPointShp(ShapeFile shp,
			Context context, String fileName) {

		GeometryLayer gl = new GeometryLayer(context);

		gl.setType(GeometryType.POINT);
		gl.setName(fileName);

		for (int i = 0; i < shp.getSHP_shapeCount(); i++) {
			ShpPoint point = shp.getSHP_shape(i);
			double lat = point.getPoint()[1];
			double lon = point.getPoint()[0];
			gl.addGeometry(new fr.umlv.lastproject.smart.layers.PointGeometry(
					lat, lon));
		}

		return gl;
	}

	private static GeometryLayer getLayerFromPolylineShp(ShapeFile shp,
			Context context, String fileName) {

		GeometryLayer gl = new GeometryLayer(context);
		gl.setType(GeometryType.LINE);
		gl.setName(fileName);

		for (int i = 0; i < shp.getSHP_shapeCount(); i++) {
			LineGeometry line = new LineGeometry();
			ShpPolyLine shpline = shp.getSHP_shape(i);
			double[][] points = shpline.getPoints();

			for (int j = 0; j < shpline.getNumberOfPoints(); j++) {
				line.addPoint(new PointGeometry(points[j][1], points[j][0]));
			}
			gl.addGeometry(line);
		}
		return gl;
	}

}