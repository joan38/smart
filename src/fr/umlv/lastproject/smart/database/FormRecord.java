package fr.umlv.lastproject.smart.database;

import java.util.ArrayList;
import java.util.List;

import fr.umlv.lastproject.smart.form.Field;
import fr.umlv.lastproject.smart.form.Form;

/**
 * Class uses to model a record of form's instance
 * 
 * @author Maelle Cabot
 * 
 */
public class FormRecord {

	private final String name;
	private final List<FieldRecord> fields;

	public FormRecord(String name) {
		this.fields = new ArrayList<FieldRecord>();
		this.name = name;
	}
	
	/**
	 * 
	 * @param fields is the list of fielRecord in the form
	 * @param name of the formRecord
	 */
	public FormRecord(List<FieldRecord> fields, String name) {
		this.fields = fields;
		this.name = name;
	}

	/**
	 * 
	 * @param f is the form associated to the formRecord
	 */
	public FormRecord(Form f) {
		this.fields = new ArrayList<FieldRecord>();

		ArrayList<Field> fieldslist = (ArrayList<Field>) f.getFieldsList();
		for (Field fld : fieldslist) {
			switch (fld.getType()) {
			case TEXT:
				TextFieldRecord fr = new TextFieldRecord(fld, null);
				this.fields.add(fr);
				break;
				
			case NUMERIC:
				NumericFieldRecord nf = new NumericFieldRecord(fld, 0);
				this.fields.add(nf);
				break;
				
			case BOOLEAN:
				BooleanFieldRecord bf = new BooleanFieldRecord(fld, false);
				this.fields.add(bf);
				break;
				
			case LIST:
				ListFieldRecord lf = new ListFieldRecord(fld, null);
				this.fields.add(lf);
				break;
				
			case PICTURE:
				PictureFieldRecord pf = new PictureFieldRecord(fld, null);
				this.fields.add(pf);
				break;
				
			case HEIGHT:
				HeightFieldRecord hf = new HeightFieldRecord(fld, 0);
				this.fields.add(hf);
				break;

			default:
				throw new IllegalStateException("Unkown field type");
			}
		}
		
		this.name = f.getTitle();
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
	 * @return the list of fieldRecord
	 */
	public List<FieldRecord> getFields() {
		return fields;
	}

	/**
	 * 
	 * @param f is a fielRecord to add in the formRecord
	 */
	public void addField(FieldRecord f) {
		this.fields.add(f);
	}
}
