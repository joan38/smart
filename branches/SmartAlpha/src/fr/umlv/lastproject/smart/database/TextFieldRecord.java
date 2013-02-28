package fr.umlv.lastproject.smart.database;

import fr.umlv.lastproject.smart.form.Field;

/**
 * Class uses to model a record of text field
 * 
 * @author Maelle Cabot
 * 
 */
public class TextFieldRecord extends FieldRecord {

	private String value;

	/**
	 * 
	 * @param field
	 * @param value
	 */
	public TextFieldRecord(Field field, String value) {
		super(field);
		this.value = value;
	}

	/**
	 * 
	 * @return the value of the field
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
