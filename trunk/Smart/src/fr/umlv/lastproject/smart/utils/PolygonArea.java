package fr.umlv.lastproject.smart.utils;

import java.util.List;

import org.osmdroid.util.GeoPoint;

import fr.umlv.lastproject.smart.layers.PointGeometry;
import fr.umlv.lastproject.smart.layers.PolygonGeometry;

public final class PolygonArea {

	private final static double VALUE_1E6 = 1E6;

	private PolygonArea() {
	}

	public static double getPolygonArea(PolygonGeometry polygon) {

		List<PointGeometry> points = polygon.getPoints();
		points.remove(points.size() - 1);

		double area = 0;

		final double[] x = new double[points.size()];
		final double[] y = new double[points.size()];
		for (int j = 0; j < points.size(); j++) {

			final double lat = points.get(j).getLatitude() / VALUE_1E6;
			final double lon = points.get(j).getLongitude() / VALUE_1E6;

			GeoPoint lat0 = new GeoPoint(0, lon);
			GeoPoint current = new GeoPoint(lat, lon);
			y[j] = lat0.distanceTo(current);
			GeoPoint lon0 = new GeoPoint(lat, 0);
			x[j] = lon0.distanceTo(current);
		}

		for (int i = 0; i < points.size(); i++) {

			area += ((x[i] * y[(i + 1) % points.size()]) - (y[i] * x[(i + 1)
					% points.size()]));
		}
		return Math.abs(area / 2);
	}
}