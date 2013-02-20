package fr.umlv.lastproject.smart.form;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.util.LinkedList;
import java.util.List;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.dialog.FormDialog;
import fr.umlv.lastproject.smart.layers.Geometry;
import fr.umlv.lastproject.smart.utils.SmartConstants;

/**
 * Object form associated at a mission
 * 
 * @author Maelle Cabot
 * 
 */
public class Form implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -30424477427478579L;
	private String name;
	private List<Field> fieldsList;
	private List<Object> editTextList;


	private final String FORM ="form" ;
	private final String FIELD="field" ;
	private final String TYPE="type" ;
	private final String TITLE="title"; 
	private final String VALUES="values" ;
	private final String MIN="min" ;
	private final String MAX="max" ;


/**
 * 
 * @param name of the form
 */
	public Form(String name) {
		this.name = name;
		this.fieldsList = new ArrayList<Field>();
		this.fieldsList.add(new TextField("commentaires"));
	}

	/**
	 * Form by default
	 */
	public Form() {
		this.name = "FormDefault";
		this.fieldsList = new ArrayList<Field>();
		this.fieldsList.add(new TextField("commentaires"));
	}

	/**
	 * 
	 * @return the name of form
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
	 * @return the fields list of the form
	 */
	public List<Field> getFieldsList() {
		return fieldsList;
	}

	/**
	 * 
	 * @param fieldsList
	 */
	public void setFieldsList(List<Field> fieldsList) {
		this.fieldsList = fieldsList;
	}

	/**
	 * 
	 * @param f is the field to add at the form
	 */
	public void addField(Field f) {
		this.fieldsList.add(f);
	}

	/**
	 * 
	 * @param label of the field to delete
	 */
	public void deleteField(String label){
		for(int i=0;i<fieldsList.size();i++){
			if(fieldsList.get(i).getLabel().equals(label)){
				fieldsList.remove(i);
			}
		}
	}

	/**
	 * 
	 * @param label of the field to search
	 * @return true if the field exists
	 */
	public boolean searchLabel(String label){
		for(int i=0;i<fieldsList.size();i++){
			if(fieldsList.get(i).getLabel().equals(label)){
				return false;
			}
		}
		return true;
	}

	/**
	 * Open a form after a survey and save the informations
	 * 
	 * @param context
	 *            of application
	 * @param g
	 *            geometry to insert
	 */
	public void openForm(final Context context, final Geometry g, final Mission mission) {
		final FormDialog dialog=new FormDialog(context, this, g, mission);
		dialog.show();
	}
	/**
	 * 
	 * @param s the file to load
	 * @throws XmlPullParserException if the xml is malformatted
	 * @throws FileNotFoundException if the file does not exist
	 */
	public void read(String s) throws XmlPullParserException, FileNotFoundException{

		XmlPullParserFactory xml = XmlPullParserFactory.newInstance() ;
		xml.setNamespaceAware(true);
		XmlPullParser xpp = xml.newPullParser();
		FileInputStream fis = new FileInputStream(new File(s));
		xpp.setInput(fis,"UTF-8");
		int eventype = xpp.getEventType() ;

		while(eventype != XmlPullParser.END_DOCUMENT){

			if(eventype == XmlPullParser.START_TAG){
				String tag = xpp.getName();
				if(tag == FORM){
					for(int i =0 ; i< xpp.getAttributeCount() ; i++){
						if(xpp.getAttributeName(i) == TITLE ){
							setName(xpp.getAttributeValue(i)) ;
						}
					}
				}else if(tag == FIELD){


					String type = null ;
					String title = null ; ;
					int max = 0 ;
					int min = 0 ;
					String values = null ;

					for(int i = 0 ; i < xpp.getAttributeCount() ; i++){
						if(xpp.getAttributeType(i) == TYPE){
							type = xpp.getAttributeValue(i);
						}else if(xpp.getAttributeType(i) == TITLE){
							title = xpp.getAttributeValue(i) ;
						}else if(xpp.getAttributeType(i)== MAX){
							max = Integer.valueOf(xpp.getAttributeValue(i));
						}else if(xpp.getAttributeType(i)== MIN){
							min = Integer.getInteger(xpp.getAttributeValue(i)) ;
						}else if(xpp.getAttributeType(i)==VALUES){
							values= xpp.getAttributeValue(i) ;
						}
					}
					/*if(type == null) 

					if( type == "photo" ){
						addField(new HeightField("")) ;
					}else if(type == "height"){
						addField(new HeightFieldRecord(field, value))
					}else if(type == "")*/

				}

			}

		}
	}

	public void buildForm(TableLayout l, final Context c){

		editTextList = new LinkedList<Object>();

		for(Field field : fieldsList){
			int typeField = field.getType();
			TextView textView = new TextView(c);
			final EditText editText = new EditText(c);


			switch (typeField) {
			case SmartConstants.TEXT_FIELD:
				TextField tf = (TextField) field;
				textView.setTag(tf.getLabel());
				textView.setText(tf.getLabel());
				textView.setPadding(20, 10, 5, 0);

				l.addView(textView);
				l.addView(editText);
				editTextList.add(editText);

				break;
			case SmartConstants.NUMERIC_FIELD:
				final NumericField nf = (NumericField) field;
				textView.setText(nf.getLabel());
				textView.setPadding(20, 10, 5, 0);
				editText.setInputType(InputType.TYPE_CLASS_NUMBER);
				editText.addTextChangedListener(new TextWatcher(){

					@Override
					public void afterTextChanged(Editable arg0) {
						if(!editText.getText().toString().equals("")){
							if(Double.parseDouble(editText.getText().toString()) > nf.getMax() 
									|| Double.parseDouble(editText.getText().toString()) < nf.getMin()){
								editText.setError("Invalid");
							}
						}

					}

					@Override
					public void beforeTextChanged(
							CharSequence arg0, int arg1, int arg2,
							int arg3) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onTextChanged(CharSequence arg0,
							int arg1, int arg2, int arg3) {

					}

				});

				l.addView(textView);
				l.addView(editText);
				editTextList.add(editText);

				break;
			case SmartConstants.BOOLEAN_FIELD:
				BooleanField bf = (BooleanField) field;
				textView.setText(bf.getLabel());
				textView.setPadding(20, 10, 5, 0);

				RadioGroup group = new RadioGroup(c);
				RadioButton buttonYes = new RadioButton(c);
				buttonYes.setId(0);
				buttonYes.setText(R.string.yes);
				buttonYes.setChecked(true);
				RadioButton buttonNo = new RadioButton(c);
				buttonNo.setId(1);
				buttonNo.setText(R.string.no);

				group.addView(buttonYes);
				group.addView(buttonNo);

				l.addView(textView);
				l.addView(group);

				editTextList.add(group);

				break;
			case SmartConstants.LIST_FIELD:
				final ListField lf = (ListField) field;
				textView.setText(lf.getLabel());
				textView.setPadding(20, 10, 5, 0);

				Spinner spin = new Spinner(c);
				List<String> strings = lf.getValues();
				spin.setAdapter(new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1,strings));

				spin.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						EditText et  = new EditText(c);
						et.setText(lf.getValues().get(position));
						editTextList.add(et);

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});

				l.addView(textView);
				l.addView(spin);

				break;
			case SmartConstants.PICTURE_FIELD:
				PictureField pf = (PictureField) field;

				textView.setText(pf.getLabel());
				textView.setPadding(20, 10, 5, 0);

				l.addView(textView);
				editTextList.add(editText);

				break;
			case SmartConstants.HEIGHT_FIELD:
				HeightField hf = (HeightField) field;
				textView.setText(hf.getLabel());
				textView.setPadding(20, 10, 5, 0);
				editText.setInputType(InputType.TYPE_CLASS_NUMBER);

				l.addView(textView);
				l.addView(editText);
				editTextList.add(editText);

				break;
			default:
				break;
			}
		}

	}

}
