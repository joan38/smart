package fr.umlv.lastproject.smart.utils;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.util.GeoPoint;

import android.util.Log;
import fr.umlv.lastproject.smart.Polygon2D;
import fr.umlv.lastproject.smart.layers.PointGeometry;
import fr.umlv.lastproject.smart.layers.PolygonGeometry;

public final class PolygonArea {

	private static GeoPoint center = new GeoPoint(0, 0);

	private PolygonArea() {

	}

	// public static float getPolygonArea(PolygonGeometry polygon) {
	//
	// List<PointGeometry> points = polygon.getPoints();
	// int nbPoint = points.size();
	// float a = 0;
	// float[] x = new float[nbPoint];
	// float[] y = new float[nbPoint];
	//
	// // points.get(0).getCoordinates().distanceTo(other)
	//
	// for (int i = 0; i < nbPoint; i++) {
	// x[i] = (float) ((points.get((i + 1) % nbPoint).getLatitude() / 1E6) -
	// (points
	// .get((i + nbPoint - 1) % nbPoint).getLatitude() / 1E6) / 2);
	// y[i] = (float) ((points.get((i + 1) % nbPoint).getLongitude() / 1E6) -
	// (points
	// .get((i + nbPoint - 1) % nbPoint).getLongitude() / 1E6) / 2);
	// a = (float) (a + (points.get(i).getLatitude() / 1E6 * y[i] - points
	// .get(i).getLongitude() / 1E6 * x[i]));
	// }
	//
	// return (a / 2) > 0 ? a / 2 : (-a / 2);
	// }

	public static double getPolygonArea(PolygonGeometry polygon) {

		// List<PointGeometry> points = polygon.getPoints();
		List<PointGeometry> points = new ArrayList<PointGeometry>();

		//
		// points.add(new PointGeometry(48.9037, 2.6519));
		// points.add(new PointGeometry(48.7937, 2.6306));
		// points.add(new PointGeometry(48.8088, 2.472));
		// points.add(new PointGeometry(48.9222, 2.472));
		points.add(new PointGeometry(0.1681, 0.6011));
		points.add(new PointGeometry(0.0544, 0.7605));
		points.add(new PointGeometry(-0.0567, 0.4885));
		points.add(new PointGeometry(0.0643, 0.3624));
		Polygon2D poly = new Polygon2D(points);
		Log.d("TESTX", " Aire poly : " + poly.signedArea());
		int nbPoint = points.size();
		final GeoPoint referentiel = new GeoPoint(0, 0);
		double area = 0;
		float[] x = { 5, 5, 17, 15 };
		float[] y = { 11, 2, 2, 12 };
		Log.d("TESTX", "points size : " + points.size());
		for (int i = 0; i < nbPoint; i++) {

			GeoPoint lat0 = new GeoPoint(points.get(i).getLatitude() / 1E6, 0);
			GeoPoint lat1 = new GeoPoint(points.get((i + 1) % (nbPoint))
					.getLatitude() / 1E6, 0);

			GeoPoint lon0 = new GeoPoint(0, points.get(i).getLongitude() / 1E6);
			GeoPoint lon1 = new GeoPoint(0, points.get((i + 1) % (nbPoint))
					.getLongitude() / 1E6);

			// Log.d("aire2",
			// i + "/" + nbPoint + " __ " + points.get(i).getLatitude()
			// / 1E6 + " / " + points.get(i).getLongitude() / 1E6
			// + " => " + center.distanceTo(initialLatitude)
			// / 1000 + " / "
			// + center.distanceTo(initialLongitude) / 1000);

			// Log.d("aire", "" + i + " / " + (i + 1) % nbPoint);
			//
			double x0 = referentiel.distanceTo(lon0);
			double x1 = referentiel.distanceTo(lon1);

			double y0 = referentiel.distanceTo(lat0);
			double y1 = referentiel.distanceTo(lat1);

			Log.d("TESTX", "x0 : " + x0 + " / y1 : " + y1 + " / y0 : " + y0
					+ " / x1 : " + x1);

			// area += Math.abs((((x1 * y2) - (y1 * x2)) / 2));

			// area += ((x0 * y1) - (y0 * x1));
			area += ((x[i] * y[(i + 1) % points.size()]) - (y[i] * x[(i + 1)
					% points.size()]));
			Log.d("aire2", "" + area);
		}

		return Math.abs(area / 2);
	}
}
