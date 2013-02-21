package fr.umlv.lastproject.smart.dataexport;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import fr.umlv.lastproject.smart.database.DbManager;
import fr.umlv.lastproject.smart.database.GeometryRecord;
import fr.umlv.lastproject.smart.database.PointRecord;
import fr.umlv.lastproject.smart.form.Mission;

public class DataExport {
	/**
	 * Export the geometries of the mission in a CSV file. The name of the file
	 * is <the name of the mission>.csv
	 * 
	 * @throws IOException
	 */
	public static void exportCsv(File file, Mission mission) throws IOException {
		FileWriter csv = new FileWriter(file);
		DbManager dbm = new DbManager();
		dbm.open(mission.getContext());
		csv.write("Geometries,Points\n");

		for (GeometryRecord geometry : dbm.getGeometriesFromMission(mission.getId())) {
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
	 */
	public static void exportKml(String path, Mission mission) throws KmlExportException {
		try {
			KmlExport.exportMission(new File(path), mission);
		} catch (ParserConfigurationException e) {
			throw new KmlExportException("Unable to export the mission " + mission.getTitle(), e);
		} catch (TransformerException e) {
			throw new KmlExportException("Unable to export the mission " + mission.getTitle(), e);
		}
	}
}
