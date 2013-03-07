package fr.umlv.lastproject.smart.data;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import android.content.Context;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.browser.utils.FileUtils;
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
import fr.umlv.lastproject.smart.layers.GeometryType;
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
	public static String exportCsv(String path, long idMission, Context context)
			throws CsvExportException {
		try {
			DbManager dbm = new DbManager();
			dbm.open(context);
			MissionRecord mission = dbm.getMission(idMission);
			Iterator<GeometryRecord> geometryIterator = dbm
					.getGeometriesFromMission(mission.getId()).iterator();

			if (!geometryIterator.hasNext()) {
				throw new CsvExportException(
						context.getString(R.string.noGeometryInMission));
			}

			FileWriter csv = new FileWriter(path + mission.getTitle()
					+ FileUtils.CSV_TYPE[0]);
			csv.write("Geom");

			GeometryRecord geometry = geometryIterator.next();
			FormRecord formRecord = dbm.getFormRecord(
					geometry.getIdFormRecord(), mission.getForm().getTitle());
			for (FieldRecord field : formRecord.getFields()) {
				csv.write(";" + field.getField().getLabel());
			}
			csv.write("\n");

			while (true) {
				StringBuilder line = new StringBuilder(geometry.getType()
						.name() + "(");

				if (geometry.getType() == GeometryType.POLYGON) {
					line.append("(");
				}

				for (PointRecord point : geometry.getPointsRecord()) {
					line.append(point.getY()).append(" ").append(point.getX());
					/*
					 * line.append(" ").append( (point.getZ() == -1.0 ? 0 :
					 * point.getZ()));
					 */
					line.append(",");
				}
				line.deleteCharAt(line.length() - 1);
				if (geometry.getType() == GeometryType.POLYGON) {
					// Add the first point at the end
					PointRecord point = geometry.getPointsRecord().get(0);
					line.append(",").append(point.getY()).append(" ")
							.append(point.getX());
					/*
					 * line.append(" ").append( (point.getZ() == -1.0 ? 0 :
					 * point.getZ()));
					 */
					line.append(")");
				}
				line.append(")");

				for (FieldRecord field : formRecord.getFields()) {
					line.append(";");

					switch (field.getField().getType()) {
					case TEXT:
						TextFieldRecord tf = (TextFieldRecord) field;
						line.append(tf.getValue());
						break;

					case NUMERIC:
						NumericFieldRecord nf = (NumericFieldRecord) field;
						line.append(String.valueOf(nf.getValue()));
						break;

					case BOOLEAN:
						BooleanFieldRecord bf = (BooleanFieldRecord) field;
						line.append(String.valueOf(bf.getValue()));
						break;

					case LIST:
						ListFieldRecord lf = (ListFieldRecord) field;
						line.append(lf.getValue());
						break;

					case PICTURE:
						PictureFieldRecord pf = (PictureFieldRecord) field;
						line.append(pf.getValue());
						break;

					case HEIGHT:
						HeightFieldRecord hf = (HeightFieldRecord) field;
						line.append(String.valueOf(hf.getValue()));
						break;

					default:
						throw new IllegalStateException("Unkown field type");
					}
				}

				line.append("\n");
				csv.write(line.toString());

				if (!geometryIterator.hasNext()) {
					break;
				}
				geometry = geometryIterator.next();
				formRecord = dbm.getFormRecord(geometry.getIdFormRecord(),
						mission.getForm().getTitle());
			}

			csv.close();
			dbm.close();
			return path + mission.getTitle() + FileUtils.CSV_TYPE[0];
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
	 * @return the path to the saved file
	 * @throws KmlExportException
	 * @throws SmartException
	 */
	public static String exportKml(String path, long idMission, Context context)
			throws KmlExportException {
		return KmlExport.exportMission(path, idMission, context);
	}
}
