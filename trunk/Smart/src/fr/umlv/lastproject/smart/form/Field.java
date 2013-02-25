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
	private FieldType type;

	/**
	 * 
	 * @param label of the field
	 * @param type of the field
	 */
	public Field(String label, FieldType type) {
		this.label = label;
		this.type = type;
	}

	/**
	 * 
	 * @return the type of field
	 */
	public FieldType getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 */
	public void setType(FieldType type) {
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
