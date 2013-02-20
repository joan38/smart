package fr.umlv.lastproject.smart.database;

import fr.umlv.lastproject.smart.form.Field;

/**
 * Class uses to model a record of field
 * 
 * @author Maelle Cabot
 * 
 */
public class FieldRecord {

	private Field field;

	/**
	 * 
	 * @param field associated to the fieldRecord
	 */
	public FieldRecord(Field field) {
		super();
		this.field = field;
	}

	/**
	 * 
	 * @return the field
	 */
	public Field getField() {
		return field;
	}

	/**
	 * 
	 * @param field
	 */
	public void setField(Field field) {
		this.field = field;
	}


}
