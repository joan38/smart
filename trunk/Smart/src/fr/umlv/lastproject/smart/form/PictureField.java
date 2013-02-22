package fr.umlv.lastproject.smart.form;

import fr.umlv.lastproject.smart.utils.SmartConstants;

/**
 * Field of type Picture
 * 
 * @author Maelle Cabot
 * 
 */
public class PictureField extends Field {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6925473200014882410L;

	/**
	 * Constructor 
	 * 
	 * @param name of the picture field
	 */
	public PictureField(String name) {
		super(name, SmartConstants.PICTURE_FIELD);
	}


}
