package fr.umlv.lastproject.smart.form;

import java.io.Serializable;

/**
 * Field used for forms management
 * 
 * @author Maelle Cabot
 * 
 */
public abstract class Field implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1486832698681988189L;
	private String label;
	private int type;

	/**
	 * 
	 * @param label of the field
	 * @param type of the field
	 */
	public Field(String label, int type) {
		this.label = label;
		this.type = type;
	}

	/**
	 * 
	 * @return the type of field
	 */
	public int getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * 
	 * @return the label of field
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * 
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

}
