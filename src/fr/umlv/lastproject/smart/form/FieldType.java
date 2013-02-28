package fr.umlv.lastproject.smart.form;


public enum FieldType {

	TEXT(0), NUMERIC(1), BOOLEAN(2), LIST(3), PICTURE(4), HEIGHT(5);

	private final int dbId;

	private FieldType(int dbId) {
		this.dbId = dbId;
	}

	public int getId() {
		return dbId;
	}

	/**
	 * Returns the geometry corresponding to the id
	 * 
	 * @param id
	 *            from DataBase
	 * @return
	 */
	public static FieldType getFromId(int id) {
		for (FieldType fieldType : values()) {
			if (fieldType.dbId == id) {
				return fieldType;
			}
		}

		return null;
	}
}
