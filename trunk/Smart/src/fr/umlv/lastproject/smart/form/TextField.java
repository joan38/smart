package fr.umlv.lastproject.smart.form;

import fr.umlv.lastproject.smart.utils.SmartConstants;

/**
 * Field of type Text
 * 
 * @author Maelle
 * 
 */
public class TextField extends Field {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5771639179711379906L;

	/**
	 * Constructor
	 * 
	 * @param label of field
	 */
	public TextField(String label) {
		super(label, SmartConstants.TEXT_FIELD);
	}

	
}
