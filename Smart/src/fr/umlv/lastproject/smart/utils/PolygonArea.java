package fr.umlv.lastproject.smart.utils;

import java.util.List;

import org.osmdroid.util.GeoPoint;

import android.util.Log;
import fr.umlv.lastproject.smart.layers.PointGeometry;
import fr.umlv.lastproject.smart.layers.PolygonGeometry;

public final class PolygonArea {

	// public static final double DEG2RAD = (Math.PI / 180.0);

	private PolygonArea() {
	}

	public static double getPolygonArea(PolygonGeometry polygon) {

		List<PointGeometry> points = polygon.getPoints();
		points.remove(points.size() - 1);
		// List<PointGeometry> points = new ArrayList<PointGeometry>();
		// 295
		// points.add(new PointGeometry(48.914, 2.689));
		// points.add(new PointGeometry(48.758, 2.710));
		// points.add(new PointGeometry(48.765, 2.468));
		// points.add(new PointGeometry(48.924, 2.47));
		// 17892
		// points.add(new PointGeometry(49.227, 1.384));
		// points.add(new PointGeometry(49.202, 3.4114));
		// points.add(new PointGeometry(48.135, 3.495));
		// points.add(new PointGeometry(48.165, 1.411));

		double area = 0;

		// double[] x = { 5, 5, 17, 15 };
		// double[] y = { 11, 2, 2, 12 };
		final double[] x = new double[points.size()];
		final double[] y = new double[points.size()];
		for (int j = 0; j < points.size(); j++) {
			// final double lat = DEG2RAD * points.get(j).getLatitude() / 1E6;
			// final double lon = DEG2RAD * points.get(j).getLongitude() / 1E6;
			final double lat = points.get(j).getLatitude() / 1E6;
			final double lon = points.get(j).getLongitude() / 1E6;
			Log.d("AIRE", "LAT : " + points.get(j).getLatitude() / 1E6);
			Log.d("AIRE", "LON : " + points.get(j).getLongitude() / 1E6);
			// Log.d("aire2", "" + area);
			// x[j] = R * Math.cos(lat) * Math.cos(lon);
			// y[j] = R * Math.cos(lat) * Math.sin(lon);
			GeoPoint lat0 = new GeoPoint(0, lon);
			GeoPoint current = new GeoPoint(lat, lon);
			y[j] = lat0.distanceTo(current);
			GeoPoint lon0 = new GeoPoint(lat, 0);
			x[j] = lon0.distanceTo(current);
			Log.d("AIRE", "X : " + x[j] + " / Y : " + y[j]);
		}
		for (int i = 0; i < points.size(); i++) {

			area += ((x[i] * y[(i + 1) % points.size()]) - (y[i] * x[(i + 1)
					% points.size()]));
			Log.d("aire2", "" + area);
		}
		Log.d("AIRE", "AIRE : " + Math.abs(area / 2));
		return Math.abs(area / 2);
	}
}