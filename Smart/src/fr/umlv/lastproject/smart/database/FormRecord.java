package fr.umlv.lastproject.smart.database;

import java.util.ArrayList;
import java.util.List;

import fr.umlv.lastproject.smart.form.Field;
import fr.umlv.lastproject.smart.form.Form;
import fr.umlv.lastproject.smart.utils.SmartConstants;

/**
 * Class uses to model a record of form's instance
 * 
 * @author Maelle Cabot
 * 
 */
public class FormRecord {

	private String name;
	private ArrayList<FieldRecord> fields;

	/**
	 * 
	 * @param fields is the list of fielRecord in the form
	 * @param name of the formRecord
	 */
	public FormRecord(ArrayList<FieldRecord> fields, String name) {
		super();
		this.fields = fields;
		this.name = name;
	}

	/**
	 * 
	 * @param f is the form associated to the formRecord
	 */
	public FormRecord(Form f) {
		super();
		this.fields = new ArrayList<FieldRecord>();

		ArrayList<Field> fieldslist = f.getFieldsList();
		for (Field fld : fieldslist) {

			switch (fld.getType()) {
			case SmartConstants.TEXT_FIELD:
				TextFieldRecord fr = new TextFieldRecord(fld, null);
				this.fields.add(fr);

				break;
			case SmartConstants.NUMERIC_FIELD:
				NumericFieldRecord nf = new NumericFieldRecord(fld, 0);
				this.fields.add(nf);

				break;
			case SmartConstants.BOOLEAN_FIELD:
				BooleanFieldRecord bf = new BooleanFieldRecord(fld, false);
				this.fields.add(bf);

				break;
			case SmartConstants.LIST_FIELD:
				ListFieldRecord lf = new ListFieldRecord(fld, null);
				this.fields.add(lf);

				break;
			case SmartConstants.PICTURE_FIELD:
				PictureFieldRecord pf = new PictureFieldRecord(fld, null);
				this.fields.add(pf);

				break;
			case SmartConstants.HEIGHT_FIELD:
				HeightFieldRecord hf = new HeightFieldRecord(fld, 0);
				this.fields.add(hf);

				break;
			default:
				break;
			}
		}
		this.name = f.getName();
	}

	/**
	 * 
	 * @return the name of formRecord
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

	/**
	 * 
	 * @return the list of fieldRecord
	 */
	public List<FieldRecord> getFields() {
		return fields;
	}

	/**
	 * 
	 * @param fields
	 */
	public void setFields(ArrayList<FieldRecord> fields) {
		this.fields = fields;
	}

	/**
	 * 
	 * @param f is a fielRecord to add in the formRecord
	 */
	public void addField(FieldRecord f) {
		this.fields.add(f);
	}

}
