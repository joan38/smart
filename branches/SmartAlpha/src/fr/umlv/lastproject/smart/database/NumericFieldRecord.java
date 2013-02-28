package fr.umlv.lastproject.smart.database;

import fr.umlv.lastproject.smart.form.Field;

/**
 * Class uses to model a record of numeric field
 * 
 * @author Maelle Cabot
 * 
 */
public class NumericFieldRecord extends FieldRecord {

	private double value;

	/**
	 * 
	 * @param field
	 * @param value
	 */
	public NumericFieldRecord(Field field, double value) {
		super(field);
		this.value = value;
	}

	/**
	 * 
	 * @return value of Numeric field
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
