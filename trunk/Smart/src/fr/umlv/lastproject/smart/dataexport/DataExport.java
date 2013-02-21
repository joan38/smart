package fr.umlv.lastproject.smart.dataexport;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import fr.umlv.lastproject.smart.database.DbManager;
import fr.umlv.lastproject.smart.database.GeometryRecord;
import fr.umlv.lastproject.smart.database.PointRecord;
import fr.umlv.lastproject.smart.form.Mission;
import fr.umlv.lastproject.smart.kml.KmlExport;
import fr.umlv.lastproject.smart.kml.KmlExportException;
import fr.umlv.lastproject.smart.utils.SmartException;

public final class DataExport {

	private DataExport() {
	}

	/**
	 * Export the geometries of the mission in a CSV file. The name of the file
	 * is <the name of the mission>.csv
	 * 
	 * @throws IOException
	 */
	public static void exportCsv(File file, Mission mission) throws IOException {
		FileWriter csv = new FileWriter(file);
		DbManager dbm = new DbManager();
		try {
			dbm.open(mission.getContext());
		} catch (SmartException e) {
			Toast.makeText(mission.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
			Log.e("", e.getMessage());
		}
		csv.write("Geometries,Points\n");

		for (GeometryRecord geometry : dbm.getGeometriesFromMission(mission
				.getId())) {
			csv.write(geometry.getType().name() + ",");

			for (PointRecord point : geometry.getPointsRecord()) {
				csv.write("[" + point.getX() + ";" + point.getY() + ";"
						+ point.getZ() + "]");
			}

			csv.write("\n");
		}

		csv.close();
		dbm.close();
	}

	/**
	 * Export the geometries of the mission in a KML file. The name of the file
	 * is <the name of the mission>.kml
	 * 
	 * @throws KmlExportException
	 * @throws SmartException 
	 */
	public static void exportKml(String path, int idMission, Context context)
			throws KmlExportException, SmartException {
			KmlExport.exportMission(new File(path), idMission, context);
	}
}
