package fr.umlv.lastproject.smart.form;

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


	/**
	 * 
	 * @param label of the field
	 * @param min of the value
	 * @param max of the value
	 */
	public NumericField(String label) {
		super(label, FieldType.NUMERIC);
	}


}
