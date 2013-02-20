package fr.umlv.lastproject.smart.form;

import fr.umlv.lastproject.smart.utils.SmartConstants;

/**
 * Field of type Numeric
 * 
 * @author Maelle Cabot
 *
 */
public class NumericField extends Field {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5108601667354465487L;
	private int min;
	private int max;

	/**
	 * 
	 * @param label of the field
	 * @param min of the value
	 * @param max of the value
	 */
	public NumericField(String label, int min, int max) {
		super(label, SmartConstants.NUMERIC_FIELD);
		this.min = min;
		this.max = max;
	}

	/**
	 * 
	 * @return min
	 */
	public int getMin() {
		return min;
	}

	/**
	 * 
	 * @param min
	 */
	public void setMin(int min) {
		this.min = min;
	}

	/**
	 * 
	 * @return max
	 */
	public int getMax() {
		return max;
	}

	/**
	 * 
	 * @param max
	 */
	public void setMax(int max) {
		this.max = max;
	}

}
