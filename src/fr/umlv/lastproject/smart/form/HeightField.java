package fr.umlv.lastproject.smart.form;

/**
 * Field of type Height
 * 
 * @author Maelle Cabot
 * 
 */
public class HeightField extends Field {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;

	/**
	 * 
	 * @param label
	 */
	public HeightField(String label) {
		super("Height", FieldType.HEIGHT);
		this.name = label;
	}

	/**
	 * 
	 * @return the name of field
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

}
