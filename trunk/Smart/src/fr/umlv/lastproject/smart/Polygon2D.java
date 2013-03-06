package fr.umlv.lastproject.smart;

import java.util.LinkedList;
import java.util.List;

import android.util.Log;
import fr.umlv.lastproject.smart.layers.PointGeometry;

/**
 * Copyright 2008 - 2011
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loonframework
 * @author chenpeng
 * @email��ceponline@yahoo.com.cn
 * @version 0.1
 */
public class Polygon2D {

	private static final int a = 6378137;// m
	private static final double e2 = 0.00669437999014;
	private static final double DEG_TO_RAD = Math.PI / 180.0;

	public static class Point2D {

		public double x;

		public double y;

		public Point2D(double x, double y) {

			this.x = x;
			this.y = y;
		}

	}

	private static double norm(double latitude) {

		double x1 = Math.pow(Math.sin(latitude), 2);
		double x2 = Math.sqrt(1 - e2 * x1);
		return a / x2;
	}

	public static Point2D[] geographicToProjected(List<PointGeometry> geometries) {
		Point2D[] points = new Point2D[geometries.size()];
		for (int i = 0; i < geometries.size(); i++) {
			PointGeometry geometry = geometries.get(i);
			double latitude = geometry.getLatitude() / 1E6 * DEG_TO_RAD;
			double longitude = geometry.getLongitude() / 1E6 * DEG_TO_RAD;
			double x = norm(latitude) * Math.cos(longitude)
					* Math.cos(latitude);
			double y = norm(latitude) * Math.sin(longitude)
					* Math.cos(latitude);
			// double x = Math.cos(longitude) * Math.cos(latitude);
			// double y = Math.sin(longitude) * Math.cos(latitude);
			Point2D point = new Point2D(x, y);
			points[i] = point;
		}
		return points;
	}

	private LinkedList<Point2D> points = new LinkedList<Point2D>();

	public Polygon2D(List<PointGeometry> geometries) {

		Point2D[] pts = geographicToProjected(geometries);

		for (int i = 0; i < pts.length; i++) {
			this.points.add(pts[i]);
		}

	}

	public double signedArea() {
		double sum = 0.0;
		for (int i = 0; i < points.size(); i++) {

			Point2D p1 = points.get(i);
			Log.d("TESTX", "x : " + p1.x + " / y : " + p1.y);
			Point2D p2 = points.get((i + 1) % points.size());
			sum += ((p1.x * p2.y) - (p2.x * p1.y));
		}
		return Math.abs(0.5 * sum);
	}
}