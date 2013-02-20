package fr.umlv.lastproject.smart.database;

import fr.umlv.lastproject.smart.form.Field;

/**
 * Class uses to model a record of height field
 * 
 * @author Maelle Cabot
 * 
 */
public class HeightFieldRecord extends FieldRecord {

	private double value;

	/**
	 * 
	 * @param field
	 * @param value
	 */
	public HeightFieldRecord(Field field, double value) {
		super(field);
		
		this.value = value;
	}

	/**
	 * 
	 * @return the value of height
	 */
	public double getValue() {
		return value;
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue(double value) {
		this.value = value;
	}

}
