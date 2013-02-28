package fr.umlv.lastproject.smart.database;

import fr.umlv.lastproject.smart.form.Field;

/**
 * Class uses to model a record of boolean field
 * 
 * @author Maelle Cabot
 * 
 */
public class BooleanFieldRecord extends FieldRecord {

	private boolean value;

	/**
	 * 
	 * @param field
	 * @param value
	 */
	public BooleanFieldRecord(Field field, boolean value) {
		
		super(field);
		this.value = value;
	}

	/**
	 * 
	 * @return the value of boolean field
	 */
	public boolean getValue() {
		return value;
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue(boolean value) {
		this.value = value;
	}

}
