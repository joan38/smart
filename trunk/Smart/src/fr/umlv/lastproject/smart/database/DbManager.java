package fr.umlv.lastproject.smart.database;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import fr.umlv.lastproject.smart.form.BooleanField;
import fr.umlv.lastproject.smart.form.Field;
import fr.umlv.lastproject.smart.form.Form;
import fr.umlv.lastproject.smart.form.HeightField;
import fr.umlv.lastproject.smart.form.ListField;
import fr.umlv.lastproject.smart.form.Mission;
import fr.umlv.lastproject.smart.form.NumericField;
import fr.umlv.lastproject.smart.form.PictureField;
import fr.umlv.lastproject.smart.form.TextField;
import fr.umlv.lastproject.smart.layers.Geometry.GeometryType;
import fr.umlv.lastproject.smart.utils.SmartConstants;
import fr.umlv.lastproject.smart.utils.SmartException;

/**
 * Class to manage the database and the static tables (missions, geometries,
 * points)
 * 
 * @author Maelle Cabot
 * 
 */

public class DbManager {

	public static final String DB_NAME = "smart.db";
	public static final String DB_PATH = Environment
			.getExternalStorageDirectory() + "/SMART/DB/";

	public static final String TABLE_MISSIONS = "missions";
	private static final String MISSIONS_COL_ID = "id";
	private static final int MISSIONS_NUM_COL_ID = 0;
	private static final String MISSIONS_COL_TITLE = "title";
	private static final int MISSIONS_NUM_COL_TITLE = 1;
	private static final String MISSIONS_COL_STATUS = "status";
	private static final int MISSIONS_NUM_COL_STATUS = 2;
	private static final String MISSIONS_COL_DATE = "date";
	private static final int MISSIONS_NUM_COL_DATE = 3;
	private static final String MISSIONS_COL_FORM = "form";
	private static final int MISSIONS_NUM_COL_FORM = 4;

	public static final String TABLE_GEOMETRIES = "geometries";
	private static final String GEOMETRIES_COL_ID = "id";
	private static final int GEOMETRIES_NUM_COL_ID = 0;
	private static final String GEOMETRIES_COL_TYPE = "type";
	private static final int GEOMETRIES_NUM_COL_TYPE = 1;
	private static final String GEOMETRIES_COL_ID_MISSION = "idMission";
	private static final String GEOMETRIES_COL_ID_FORM_RECORD = "idFormRecord";
	private static final int GEOMETRIES_NUM_COL_ID_MISSION = 2;

	public static final String TABLE_POINTS = "points";
	private static final String POINTS_COL_ID = "id";
	private static final int POINTS_NUM_COL_ID = 0;
	private static final String POINTS_COL_X = "x";
	private static final int POINTS_NUM_COL_X = 1;
	private static final String POINTS_COL_Y = "y";
	private static final int POINTS_NUM_COL_Y = 2;
	private static final String POINTS_COL_Z = "z";
	private static final int POINTS_NUM_COL_Z = 3;
	private static final String POINTS_COL_ID_GEOMETRY = "idGeometry";
	private static final int POINTS_NUM_COL_ID_GEOMETRY = 4;

	private DbHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static final String CREATE_TABLE_MISSIONS = "CREATE TABLE IF NOT EXISTS missions ( "
			+ "id INTEGER PRIMARY KEY,"
			+ "title TEXT UNIQUE,"
			+ "status INTEGER NOT NULL,"
			+ "date TEXT NOT NULL, "
			+ "form TEXT NOT NULL);";

	private static final String CREATE_TABLE_GEOMETRIES = "CREATE TABLE IF NOT EXISTS geometries ("
			+ "id INTEGER PRIMARY KEY, "
			+ "type INTEGER NOT NULL, "
			+ GEOMETRIES_COL_ID_FORM_RECORD
			+ " INTEGER NOT NULL, "
			+ GEOMETRIES_COL_ID_MISSION
			+ " INTEGER NOT NULL, "
			+ "FOREIGN KEY (" + GEOMETRIES_COL_ID_MISSION + ") REFERENCES "
			+ TABLE_MISSIONS
			+ "("
			+ MISSIONS_COL_ID
			+ "));";
	
	private static final String CREATE_TABLE_POINTS = "CREATE TABLE IF NOT EXISTS points ( "
			+ "id INTEGER PRIMARY KEY,"
			+ "x REAL NOT NULL,"
			+ "y REAL NOT NULL,"
			+ "z REAL,"
			+ "idGeometry INTEGER NOT NULL,"
			+ "FOREIGN KEY (idGeometry) REFERENCES "
			+ TABLE_GEOMETRIES
			+ "("
			+ GEOMETRIES_COL_ID + "));";

	/**
	 * Create the database and the statics tables
	 * 
	 * @author Maelle Cabot
	 * 
	 */
	private final class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DB_NAME, null, 1);
			File folder = new File(Environment.getExternalStorageDirectory()
					+ "/SMART/");
			if (!folder.exists()) {
				folder.mkdir();

			}
			File ssfolder = new File(Environment.getExternalStorageDirectory()
					+ "/SMART/DB/");
			if (!ssfolder.exists()) {
				ssfolder.mkdir();
			}
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}

		/**
		 * Open the database and create it if it's not exist
		 * 
		 * @return an access to database (read and write mode)
		 * @throws SmartException
		 */
		public SQLiteDatabase openDataBase() throws SmartException {
			SQLiteDatabase dbRetour = null;

			try {
				dbRetour = SQLiteDatabase.openOrCreateDatabase(DB_PATH
						+ DB_NAME, null);
				dbRetour.execSQL(CREATE_TABLE_MISSIONS);
				dbRetour.execSQL(CREATE_TABLE_GEOMETRIES);
				dbRetour.execSQL(CREATE_TABLE_POINTS);
			} catch (SQLiteException e) {
				throw new SmartException("Open database error");
			}

			return dbRetour;
		}
	}

	/**
	 * Open the database and instanciate the point of entry
	 * 
	 * @param context
	 *            of application
	 * @throws SmartException
	 */
	public void open(Context context) throws SmartException {
		mDbHelper = new DbHelper(context);
		mDb = mDbHelper.openDataBase();

	}

	/**
	 * Close the database
	 */
	public void close() {
		mDb.close();
	}

	/**
	 * Create a table for the form if it's not exists
	 * 
	 * @param form
	 *            to create
	 * @return 0 if the creation ok, -1 if it's not
	 * @throws SmartException
	 */
	public int createTableForm(Form f) throws SmartException {
		SQLiteDatabase db = null;
		StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
		sql.append(f.getName());
		sql.append("( id INTEGER PRIMARY KEY, date TEXT NOT NULL, ");

		List<Field> listFields = f.getFieldsList();
		for (Field field : listFields) {
			int typeField = field.getType();

			switch (typeField) {
			case SmartConstants.TEXT_FIELD:
				TextField tf = (TextField) field;
				sql.append(tf.getLabel()).append(" TEXT, ");
				break;
			case SmartConstants.NUMERIC_FIELD:
				NumericField nf = (NumericField) field;
				sql.append(nf.getLabel()).append(" REAL CHECK (")
						.append(nf.getLabel()).append(" > ")
						.append(nf.getMin()).append(" AND ")
						.append(nf.getLabel()).append(" < ")
						.append(nf.getMax()).append(" ), ");
				break;
			case SmartConstants.BOOLEAN_FIELD:
				BooleanField bf = (BooleanField) field;
				sql.append(bf.getLabel()).append(" INTEGER, ");
				break;
			case SmartConstants.LIST_FIELD:
				ListField lf = (ListField) field;
				sql.append(lf.getLabel()).append(" TEXT, ");
				break;
			case SmartConstants.PICTURE_FIELD:
				PictureField pf = (PictureField) field;
				sql.append(pf.getLabel()).append(" TEXT, ");
				break;
			case SmartConstants.HEIGHT_FIELD:
				HeightField hf = (HeightField) field;
				sql.append(hf.getLabel()).append(" TEXT, ");
				break;
			default:
				break;
			}
		}

		sql.delete(sql.length() - 3, sql.length() - 1);
		sql.append(");");

		Log.d("Cmd SQL", sql.toString());

		try {
			db = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null,
					SQLiteDatabase.OPEN_READWRITE);
			db.execSQL(sql.toString());
			db.close();
		} catch (SQLException e) {
			throw new SmartException("Database Error");
		}

		return 0;
	}

	/**
	 * Insert a new row in the table form corresponding
	 * 
	 * @param formRecord
	 *            to insert
	 * @return the id of the new insertion if the insertion ok or -1 if it's not
	 * @throws SmartException
	 */
	public long insertFormRecord(FormRecord formRecord) throws SmartException {
		ContentValues values = new ContentValues();

		List<FieldRecord> fields = new ArrayList<FieldRecord>();

		fields = formRecord.getFields();
		Log.d("TEST", "f " + fields.toString() + " " + formRecord.getName());
		for (FieldRecord field : fields) {
			int typeField = field.getField().getType();

			switch (typeField) {
			case SmartConstants.TEXT_FIELD:
				TextFieldRecord tf = (TextFieldRecord) field;
				values.put(tf.getField().getLabel(), tf.getValue());
				break;
			case SmartConstants.NUMERIC_FIELD:
				NumericFieldRecord nf = (NumericFieldRecord) field;
				values.put(nf.getField().getLabel(), nf.getValue());
				break;
			case SmartConstants.BOOLEAN_FIELD:
				BooleanFieldRecord bf = (BooleanFieldRecord) field;
				values.put(bf.getField().getLabel(), bf.getValue());
				break;
			case SmartConstants.LIST_FIELD:
				ListFieldRecord lf = (ListFieldRecord) field;
				values.put(lf.getField().getLabel(), lf.getValue());
				break;
			case SmartConstants.PICTURE_FIELD:
				PictureFieldRecord pf = (PictureFieldRecord) field;
				values.put(pf.getField().getLabel(), pf.getValue());
				break;
			case SmartConstants.HEIGHT_FIELD:
				HeightFieldRecord hf = (HeightFieldRecord) field;
				values.put(hf.getField().getLabel(), hf.getValue());
				break;
			default:
				throw new IllegalStateException("Unkown field");
			}
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss", Locale.FRENCH);
		values.put("date", dateFormat.format(new Date()));

		try {
			return mDb.insertOrThrow(formRecord.getName(), null, values);
		} catch (SQLException e) {
			throw new SmartException("Insert database error");
		}
	}

	/**
	 * Insert a new mission in the table "missions"
	 * 
	 * @param mission
	 *            to insert
	 * @return 0 if the insertion ok, -1 if it's not
	 * @throws SmartException
	 */
	public long insertMission(MissionRecord mission) throws SmartException {
		ContentValues values = new ContentValues();

		createTableForm(mission.getForm());

		values.put(MISSIONS_COL_TITLE, mission.getTitle());
		if (mission.isStatus()) {
			values.put(MISSIONS_COL_STATUS, 1);
		} else {
			values.put(MISSIONS_COL_STATUS, 0);
		}
		values.put(MISSIONS_COL_DATE, mission.getDate());
		values.put(MISSIONS_COL_FORM, mission.getForm().getName());
		try {
			long id = mDb.insertOrThrow(TABLE_MISSIONS, null, values);
			mission.setId(id);
			return id;
		} catch (SQLException e) {
			int id = getMissionId(Mission.getInstance().getTitle());
			mission.setId(id);
			throw new SmartException("Insert database error");
		}
	}

	/**
	 * Request the table "missions" with a criterion of name
	 * 
	 * @param name
	 * @return the id of the mission
	 */
	public int getMissionId(String name) {
		Cursor c = mDb.rawQuery("SELECT " + MISSIONS_COL_ID + " FROM "
				+ TABLE_MISSIONS + " WHERE " + MISSIONS_COL_TITLE + " = '"
				+ name + "'", null);

		c.moveToNext();
		int id = c.getInt(MISSIONS_NUM_COL_ID);

		c.close();
		return id;
	}

	/**
	 * Get the mission from the given id.
	 * 
	 * @param idMission
	 *            the id of the mission
	 * @return the MissionRecord corresponding to the given id
	 */
	public MissionRecord getMission(long idMission) {
		Cursor c = mDb.rawQuery("SELECT * FROM " + TABLE_MISSIONS + " WHERE "
				+ MISSIONS_COL_ID + " = " + idMission, null);

		c.moveToNext();
		MissionRecord mission = cursorToMission(c);

		c.close();
		return mission;
	}

	/**
	 * Stop the mission
	 * 
	 * @param idMission
	 */
	public void stopMission(long idMission) {
		ContentValues args = new ContentValues();
		args.put(MISSIONS_COL_STATUS, 0);
		mDb.update(TABLE_MISSIONS, args, "id=" + idMission, null);
	}

	/**
	 * Search if a mission is activated
	 * 
	 * @return id of the activated mission, -1 if no one is activated
	 */
	public long existsActivatedMission() {
		Cursor c = mDb
				.rawQuery("SELECT " + MISSIONS_COL_ID + " FROM "
						+ TABLE_MISSIONS + " WHERE " + MISSIONS_COL_STATUS
						+ "=1", null);

		if (c.getCount() == 0) {
			return -1;
		} else {
			c.moveToNext();
			MissionRecord r = cursorToMission(c);
			return r.getId();
		}
	}

	/**
	 * Search if a mission is activated
	 * 
	 * @return id of the activated mission, -1 if no one is activated
	 */
	public boolean existsMission(String name) {
		Cursor c = null;
		c = mDb.rawQuery("SELECT " + MISSIONS_COL_ID + " FROM "
				+ TABLE_MISSIONS + " WHERE " + MISSIONS_COL_TITLE + "='" + name
				+ "'", null);

		if (c.getCount() == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Request the table "missions" to get all rows
	 * 
	 * @return a list of Mission
	 */
	public List<MissionRecord> getAllMissions() {
		Cursor c = mDb.query(TABLE_MISSIONS, new String[] { MISSIONS_COL_ID,
				MISSIONS_COL_TITLE, MISSIONS_COL_STATUS, MISSIONS_COL_DATE,
				MISSIONS_COL_FORM }, null, null, null, null, null);

		LinkedList<MissionRecord> missions = new LinkedList<MissionRecord>();
		while (c.moveToNext()) {
			missions.add(cursorToMission(c));
		}

		c.close();
		return missions;
	}

	/**
	 * Convert a cursor to a Mission object
	 * 
	 * @param c
	 *            the cursor
	 * @return a Mission object
	 */
	private MissionRecord cursorToMission(Cursor c) {
		if (c.getCount() == 0) {
			return null;
		}

		MissionRecord mission = new MissionRecord();
		mission.setId(c.getInt(MISSIONS_NUM_COL_ID));
		mission.setTitle(c.getString(MISSIONS_NUM_COL_TITLE));

		if (c.getInt(MISSIONS_NUM_COL_STATUS) == 0) {
			mission.setStatus(false);
		} else {
			mission.setStatus(true);
		}

		mission.setDate(c.getString(MISSIONS_NUM_COL_DATE));

		// Retrieve all FormRecord and his subfieds
		mission.setForm(new Form(c.getString(MISSIONS_NUM_COL_FORM)));

		return mission;
	}

	/**
	 * Insert a new geometry in the table "geometries"
	 * 
	 * @param geometry
	 *            to insert
	 * @return 0 if the insertion ok, -1 if it's not
	 * @throws SmartException
	 */
	public long insertGeometry(GeometryRecord geometry) throws SmartException {
		ContentValues values = new ContentValues();

		values.put(GEOMETRIES_COL_TYPE, geometry.getType().getId());
		values.put(GEOMETRIES_COL_ID_MISSION, geometry.getIdMission());
		values.put(GEOMETRIES_COL_ID_FORM_RECORD, geometry.getIdFormRecord());

		try {
			long id = mDb.insertOrThrow(TABLE_GEOMETRIES, null, values);
			for (PointRecord pr : geometry.getPointsRecord()) {
				pr.setIdGeometry(id);
				insertPoint(pr);
			}
			return id;
		} catch (SQLException e) {
			throw new SmartException("Insert database error");
		}
	}

	/**
	 * Request the table "geometries" to get all rows
	 * 
	 * @return a list of Geometry
	 */
	public List<GeometryRecord> getAllGeometries() {
		Cursor c = mDb.query(TABLE_GEOMETRIES, new String[] {
				GEOMETRIES_COL_ID, GEOMETRIES_COL_TYPE,
				GEOMETRIES_COL_ID_MISSION, GEOMETRIES_COL_ID_FORM_RECORD },
				null, null, null, null, null);

		LinkedList<GeometryRecord> geometries = new LinkedList<GeometryRecord>();
		while (c.moveToNext()) {
			geometries.add(cursorToGeometry(c));
		}

		c.close();
		return geometries;
	}

	public List<GeometryRecord> getGeometriesFromMission(long idMission) {
		ArrayList<GeometryRecord> geometries = new ArrayList<GeometryRecord>();
		Cursor c = mDb.rawQuery("SELECT * FROM geometries WHERE "
				+ GEOMETRIES_COL_ID_MISSION + "=" + idMission, null);

		while (c.moveToNext()) {
			geometries.add(cursorToGeometry(c));
		}

		return geometries;
	}

	/**
	 * Convert a cursor to a Geometry object
	 * 
	 * @param c
	 *            the cursor
	 * @return a Geometry object
	 */
	private GeometryRecord cursorToGeometry(Cursor c) {
		if (c.getCount() == 0) {
			return null;
		}

		GeometryRecord geometry = new GeometryRecord();
		geometry.setId(c.getInt(GEOMETRIES_NUM_COL_ID));
		geometry.setIdMission(c.getInt(GEOMETRIES_NUM_COL_ID_MISSION));
		geometry.setType(GeometryType.getFromId(c
				.getInt(GEOMETRIES_NUM_COL_TYPE)));

		for (PointRecord point : getPointsFromGeometry(geometry.getId())) {
			geometry.addPoint(point);
		}

		return geometry;
	}

	/**
	 * Get all points of a Geometry.
	 * 
	 * @param idGeometry
	 *            the id of the geometry
	 * @return
	 */
	public List<PointRecord> getPointsFromGeometry(long idGeometry) {
		ArrayList<PointRecord> points = new ArrayList<PointRecord>();

		Cursor c = mDb.rawQuery("SELECT * FROM points WHERE "
				+ POINTS_COL_ID_GEOMETRY + "=" + idGeometry, null);

		while (c.moveToNext()) {
			points.add(cursorToPoint(c));
		}

		return points;
	}

	/**
	 * Insert a new point in the table "points"
	 * 
	 * @param point
	 *            to insert
	 * @return the id of the inserted point if the insertion ok, -1 if it's not
	 * @throws SmartException
	 */
	public long insertPoint(PointRecord point) throws SmartException {
		ContentValues values = new ContentValues();

		values.put(POINTS_COL_X, point.getX());
		values.put(POINTS_COL_Y, point.getY());
		values.put(POINTS_COL_Z, point.getZ());
		values.put(POINTS_COL_ID_GEOMETRY, point.getIdGeometry());

		try {
			return mDb.insertOrThrow(TABLE_POINTS, null, values);
		} catch (SQLException e) {
			throw new SmartException("Insert database error");
		}
	}

	/**
	 * Request the table "points" to get all rows
	 * 
	 * @return a list of Geometry
	 */
	public List<PointRecord> getAllPoints() {
		Cursor c = mDb.query(TABLE_POINTS, new String[] { POINTS_COL_ID,
				POINTS_COL_X, POINTS_COL_Y, POINTS_COL_Z,
				POINTS_COL_ID_GEOMETRY }, null, null, null, null, null);

		LinkedList<PointRecord> points = new LinkedList<PointRecord>();
		while (c.moveToNext()) {
			points.add(cursorToPoint(c));
		}

		c.close();
		return points;
	}

	/**
	 * Request the table "points" in terms of the mission id
	 * 
	 * @param idMission
	 * @return a list of PointRecord
	 */
	public List<PointRecord> getGeometriesPointsOfMission(int idMission) {
		LinkedList<PointRecord> points = new LinkedList<PointRecord>();

		Cursor c = mDb
				.rawQuery(
						"SELECT * FROM points JOIN geometries ON geometries.id=points.idGeometry WHERE geometries.type=0 and geometries.idMission = "
								+ idMission, null);
		while (c.moveToNext()) {
			points.add(cursorToPoint(c));
		}

		c.close();
		return points;
	}

	/**
	 * Convert a cursor to a Point object
	 * 
	 * @param c
	 *            the cursor
	 * @return a Point object
	 */
	private PointRecord cursorToPoint(Cursor c) {
		if (c.getCount() == 0) {
			return null;
		}

		PointRecord point = new PointRecord();
		point.setId(c.getInt(POINTS_NUM_COL_ID));
		point.setX(c.getDouble(POINTS_NUM_COL_X));
		point.setY(c.getDouble(POINTS_NUM_COL_Y));
		point.setZ(c.getDouble(POINTS_NUM_COL_Z));
		point.setIdGeometry(c.getInt(POINTS_NUM_COL_ID_GEOMETRY));

		return point;
	}

	public FormRecord getFormRecord(long idFormRecord, String formName) {
		Cursor c = mDb.rawQuery("SELECT * FROM " + formName + " WHERE id" + "="
				+ idFormRecord, null);

		c.moveToNext();
		return cursorToFormRecord(c);
	}

	private FormRecord cursorToFormRecord(Cursor c) {
		if (c.getCount() == 0) {
			return null;
		}

		FormRecord form = new FormRecord();
		for (int i = 0; c.moveToNext(); i++) {
			TextField field = new TextField(c.getColumnName(i));
			form.addField(new TextFieldRecord(field, c.getString(i)));
		}

		return form;
	}
}
