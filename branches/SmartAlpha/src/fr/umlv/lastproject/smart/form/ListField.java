package fr.umlv.lastproject.smart.form;

import java.util.ArrayList;
import java.util.List;

/**
 * Field of type List
 * 
 * @author Maelle Cabot
 * 
 */
public class ListField extends Field {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1340773531544440170L;
	private List<String> values;

	/**
	 * 
	 * @param label
	 * @param list
	 */
	public ListField(String label, List<String> list) {
		super(label, FieldType.LIST);
		values = new ArrayList<String>();
		values.addAll(list);
	}

	/**
	 * 
	 * @return the values of the list
	 */
	public List<String> getValues() {
		return values;
	}

	/**
	 * 
	 * @param values
	 */
	public void setValues(List<String> values) {
		this.values = values;
	}

}
