package fr.umlv.lastproject.smart.form;


/**
 * Field of type boolean
 * 
 * @author Maelle Cabot
 * 
 */
public class BooleanField extends Field {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2750533583120339444L;

	/**
	 * Constructor
	 * 
	 * @param label of the field
	 */
	public BooleanField(String label) {
		super(label, FieldType.BOOLEAN);
	}

}
