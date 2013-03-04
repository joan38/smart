package fr.umlv.lastproject.smart.form;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	 * @param label
	 *            of the field
	 * @param type
	 *            of the field
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

	/**
	 * 
	 * @param out
	 *            the object to get
	 * @throws IOException
	 *             if canot read
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(label);
		out.writeObject(type.getId());
		out.close();
	}

	/**
	 * 
	 * @param in
	 *            object to read
	 * @throws IOException
	 *             if object not readable
	 * @throws ClassNotFoundException
	 *             if class does not exist
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		this.label = (String) in.readObject();
		this.type = FieldType.getFromId(in.readInt());
		in.close();
	}

}
