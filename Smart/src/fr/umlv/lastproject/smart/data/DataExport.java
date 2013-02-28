package fr.umlv.lastproject.smart.data;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

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
	public static void exportCsv(String path, long idMission, Context context)
			throws CsvExportException {
		try {
			DbManager dbm = new DbManager();
			dbm.open(context);
			MissionRecord mission = dbm.getMission(idMission);
			Iterator<GeometryRecord> geometryIterator = dbm.getGeometriesFromMission(mission.getId()).iterator();

			if (!geometryIterator.hasNext()) {
				throw new CsvExportException("No geometry in the given mission");
			}
			
			FileWriter csv = new FileWriter(path + "/" + mission.getTitle() + ".csv");
			csv.write("Geometries;Points");
			
			GeometryRecord geometry = geometryIterator.next();
			FormRecord formRecord = dbm.getFormRecord(geometry.getIdFormRecord(), mission.getForm().getTitle());
			for (FieldRecord field : formRecord.getFields()) {
				csv.write(";" + field.getField().getLabel());
			}
			csv.write("\n");

			while (true) {
				csv.write(geometry.getType().name() + ";");

				for (PointRecord point : geometry.getPointsRecord()) {
					csv.write("[" + point.getX() + "," + point.getY() + ","
							+ (point.getZ() == -1.0 ? 0 : point.getZ()) + "]");
				}
				
				for (FieldRecord field : formRecord.getFields()) {
					csv.write(";");
					
					switch (field.getField().getType()) {
					case TEXT:
						TextFieldRecord tf = (TextFieldRecord) field;
						csv.write(tf.getValue());
						break;

					case NUMERIC:
						NumericFieldRecord nf = (NumericFieldRecord) field;
						csv.write(String.valueOf(nf.getValue()));
						break;

					case BOOLEAN:
						BooleanFieldRecord bf = (BooleanFieldRecord) field;
						csv.write(String.valueOf(bf.getValue()));
						break;

					case LIST:
						ListFieldRecord lf = (ListFieldRecord) field;
						csv.write(lf.getValue());
						break;

					case PICTURE:
						PictureFieldRecord pf = (PictureFieldRecord) field;
						csv.write(pf.getValue());
						break;

					case HEIGHT:
						HeightFieldRecord hf = (HeightFieldRecord) field;
						csv.write(String.valueOf(hf.getValue()));
						break;

					default:
						throw new IllegalStateException("Unkown field type");
					}
				}

				csv.write("\n");
				
				if (!geometryIterator.hasNext()) {
					break;
				}
				geometry = geometryIterator.next();
				formRecord = dbm.getFormRecord(geometry.getIdFormRecord(), mission.getForm().getTitle());
			}

			csv.close();
			dbm.close();
		} catch (IOException e) {
			throw new CsvExportException("Unable to export the mission", e);
		} catch (SmartException e) {
			throw new CsvExportException("Unable to export the mission", e);
		}
	}

	/**
	 * Export the geometries of the mission in a KML file. The name of the file
	 * is <the name of the mission>.kml
	 * 
	 * @throws KmlExportException
	 * @throws SmartException
	 */
	public static void exportKml(String path, long idMission, Context context)
			throws KmlExportException {
		KmlExport.exportMission(path, idMission, context);
	}
}
