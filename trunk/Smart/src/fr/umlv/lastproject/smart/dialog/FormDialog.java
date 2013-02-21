package fr.umlv.lastproject.smart.dialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.database.BooleanFieldRecord;
import fr.umlv.lastproject.smart.database.DbManager;
import fr.umlv.lastproject.smart.database.FormRecord;
import fr.umlv.lastproject.smart.database.GeometryRecord;
import fr.umlv.lastproject.smart.database.HeightFieldRecord;
import fr.umlv.lastproject.smart.database.ListFieldRecord;
import fr.umlv.lastproject.smart.database.NumericFieldRecord;
import fr.umlv.lastproject.smart.database.PictureFieldRecord;
import fr.umlv.lastproject.smart.database.TextFieldRecord;
import fr.umlv.lastproject.smart.form.BooleanField;
import fr.umlv.lastproject.smart.form.Field;
import fr.umlv.lastproject.smart.form.Form;
import fr.umlv.lastproject.smart.form.HeightField;
import fr.umlv.lastproject.smart.form.ListField;
import fr.umlv.lastproject.smart.form.Mission;
import fr.umlv.lastproject.smart.form.NumericField;
import fr.umlv.lastproject.smart.form.PictureActivity;
import fr.umlv.lastproject.smart.form.PictureField;
import fr.umlv.lastproject.smart.form.TextField;
import fr.umlv.lastproject.smart.layers.Geometry;
import fr.umlv.lastproject.smart.utils.SmartConstants;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class FormDialog extends AlertDialog.Builder{
	
	private List<Object> editTextList;
	private TableLayout layoutDynamic;
	private TextView namePicture;

	
	public FormDialog(final MenuActivity context, final Form form, final Geometry g, final Mission mission){
		super(context);
		final LayoutInflater factory = LayoutInflater.from(context);
		final View alertDialogView = factory.inflate(
				fr.umlv.lastproject.smart.R.layout.activity_formulaire_viewer,
				null);
		
		layoutDynamic = ( TableLayout ) alertDialogView.findViewById(R.id.layoutDynamicFormulaire);
		layoutDynamic.setVerticalScrollBarEnabled(true);

		buildForm(layoutDynamic, context, form.getFieldsList());

		setView(alertDialogView);
		setTitle(R.string.form_title);

		setPositiveButton(R.string.validate, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				DbManager dbManager = new DbManager();
				dbManager.open(context);
				int idGeometry = dbManager.insertGeometry(new GeometryRecord(g,
						Mission.getInstance().getId()));
				FormRecord formRecord = new FormRecord(form);

				for(int i=0; i<formRecord.getFields().size();i++){
					int type = formRecord.getFields().get(i).getField().getType();


					switch(type){
					case SmartConstants.TEXT_FIELD:
						TextFieldRecord text = (TextFieldRecord) formRecord.getFields().get(i);
						text.setValue(((EditText) editTextList.get(i)).getText().toString());

						break;
					case SmartConstants.NUMERIC_FIELD:
						NumericFieldRecord num = (NumericFieldRecord) formRecord.getFields().get(i);
						num.setValue(Double.parseDouble(((EditText) editTextList.get(i)).getText().toString()));
						break;
					case  SmartConstants.BOOLEAN_FIELD:
						BooleanFieldRecord b = (BooleanFieldRecord) formRecord.getFields().get(i);
						RadioGroup g = (RadioGroup) editTextList.get(i);
						if(g.getCheckedRadioButtonId() == 0){
							b.setValue(true);
						} else {
							b.setValue(false);
						}
						break;
					case  SmartConstants.LIST_FIELD:
						ListFieldRecord l = (ListFieldRecord) formRecord.getFields().get(i);
						l.setValue(((EditText) editTextList.get(i)).getText().toString());
						break;
					case  SmartConstants.PICTURE_FIELD:
						PictureFieldRecord p = (PictureFieldRecord) formRecord.getFields().get(i);
						p.setValue(((EditText) editTextList.get(i)).getText().toString());
						break;
					case  SmartConstants.HEIGHT_FIELD:
						HeightFieldRecord h = (HeightFieldRecord) formRecord.getFields().get(i);
						h.setValue(Double.parseDouble(((EditText) editTextList.get(i)).getText().toString()));

						break;
					default:
					}
				}

				dbManager.insertFormRecord(formRecord, idGeometry);
				dbManager.close();
				//mission.addGeometry(g);
			}
		});

		setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
					mission.removeGeometry(g);
			}
		});
	}
	

	public final void buildForm(TableLayout l, final MenuActivity c, List<Field> fieldsList){


		editTextList = new LinkedList<Object>();

		for(Field field : fieldsList){
			int typeField = field.getType();
			TextView textView = new TextView(c);
			final EditText editText = new EditText(c);


			switch (typeField) {
			case  SmartConstants.TEXT_FIELD:
				TextField tf = (TextField) field;
				textView.setTag(tf.getLabel());
				textView.setText(tf.getLabel());
				textView.setPadding(20, 10, 5, 0);

				l.addView(textView);
				l.addView(editText);
				editTextList.add(editText);

				break;
			case  SmartConstants.NUMERIC_FIELD:
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
			case  SmartConstants.BOOLEAN_FIELD:
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
			case  SmartConstants.LIST_FIELD:
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
			case  SmartConstants.PICTURE_FIELD:
				PictureField pf = (PictureField) field;

				textView.setText(pf.getLabel());
				textView.setPadding(20, 10, 5, 0);
			
				namePicture = new TextView(c);
				
				ImageView takePicture = new ImageView(c);
				takePicture.setClickable(true);
				takePicture.setImageDrawable(c.getResources().getDrawable(R.drawable.takepicture));
				takePicture.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"dd/MM/yyyy HH:mm:ss", Locale.FRENCH);
						String date = dateFormat.format(new Date());
						String namePicture = Mission.getInstance().getTitle()+"_"+date;
						Intent intent = new Intent(c,
								PictureActivity.class);
						intent.putExtra("namePicture", namePicture);
						c.startActivityForResult(intent, 10);
						
					}
				});
				
				LinearLayout ll = new LinearLayout(c);
				ll.addView(textView);
				ll.addView(takePicture);
				ll.addView(namePicture);

				l.addView(ll);
				editTextList.add(editText);

				break;
			case  SmartConstants.HEIGHT_FIELD:
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
	
	public void setNamePicture(String name){
		this.namePicture.setText(name);
	}

}