package fr.umlv.lastproject.smart.database;

import fr.umlv.lastproject.smart.form.Field;

/**
 * Class uses to model a record of list field
 * 
 * @author Maelle Cabot
 * 
 */
public class ListFieldRecord extends FieldRecord {

	private String value;

	/**
	 * 
	 * @param field
	 * @param value
	 */
	public ListFieldRecord(Field field, String value) {
		super(field);
		this.value = value;
	}

	/**
	 * 
	 * @return the value of list
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
