package fr.umlv.lastproject.smart.dialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.database.BooleanFieldRecord;
import fr.umlv.lastproject.smart.database.DbManager;
import fr.umlv.lastproject.smart.database.FieldRecord;
import fr.umlv.lastproject.smart.database.FormRecord;
import fr.umlv.lastproject.smart.database.HeightFieldRecord;
import fr.umlv.lastproject.smart.database.ListFieldRecord;
import fr.umlv.lastproject.smart.database.NumericFieldRecord;
import fr.umlv.lastproject.smart.database.PictureFieldRecord;
import fr.umlv.lastproject.smart.database.TextFieldRecord;
import fr.umlv.lastproject.smart.form.Field;
import fr.umlv.lastproject.smart.form.Form;
import fr.umlv.lastproject.smart.form.ListField;
import fr.umlv.lastproject.smart.form.Mission;
import fr.umlv.lastproject.smart.form.NumericField;
import fr.umlv.lastproject.smart.form.PictureActivity;
import fr.umlv.lastproject.smart.utils.SmartException;

/**
 * This class is used to display the form and change the values of differents fields
 * 
 * @author Maelle Cabot
 *
 */
public class AlertModifFormDialog extends AlertDialog.Builder {

	private List<Object> editTextList;
	private TableLayout layoutDynamic;
	
	private static final int PADDING_LEFT = 20;
	private static final int PADDING_TOP = 10;
	private static final int PADDING_RIGHT = 5;

	/**
	 * Constructor
	 * @param context
	 * @param form
	 * @param g : the geometry
	 * @param mission
	 */
	public AlertModifFormDialog(final MenuActivity context, final Form form,
			final int idGeometry) {
		super(context);
		setCancelable(false);
		final LayoutInflater factory = LayoutInflater.from(context);
		final View alertDialogView = factory.inflate(
				fr.umlv.lastproject.smart.R.layout.activity_formulaire_viewer,
				null);

		layoutDynamic = (TableLayout) alertDialogView
				.findViewById(R.id.layoutDynamicFormulaire);
		layoutDynamic.setVerticalScrollBarEnabled(true);

		try {
			buildForm(layoutDynamic, context, form.getFieldsList(), idGeometry);
		} catch (SmartException e) {
			Toast.makeText(context, e.getMessage(),
					Toast.LENGTH_LONG).show();
			Log.e("", e.getMessage());
		}

		setView(alertDialogView);
		setTitle(R.string.form_title);

		setPositiveButton(R.string.validate,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						DbManager dbManager = new DbManager();
						FormRecord formRecord = new FormRecord(form);

						for (int i = 0; i < formRecord.getFields().size(); i++) {

							switch (formRecord.getFields().get(i).getField()
									.getType()) {
							case TEXT:
								TextFieldRecord text = (TextFieldRecord) formRecord
										.getFields().get(i);
								text.setValue(((EditText) editTextList.get(i))
										.getText().toString());
								break;

							case NUMERIC:
								NumericFieldRecord num = (NumericFieldRecord) formRecord
										.getFields().get(i);
								num.setValue(Double
										.parseDouble(((EditText) editTextList
												.get(i)).getText().toString()));
								break;

							case BOOLEAN:
								BooleanFieldRecord b = (BooleanFieldRecord) formRecord
										.getFields().get(i);
								RadioGroup g = (RadioGroup) editTextList.get(i);
								if (g.getCheckedRadioButtonId() == 0) {
									b.setValue(true);
								} else {
									b.setValue(false);
								}
								break;

							case LIST:
								ListFieldRecord l = (ListFieldRecord) formRecord
										.getFields().get(i);
								Log.d("TEST",
										"edit "
												+ ((EditText) editTextList
														.get(i)).getText());
								l.setValue(((EditText) editTextList.get(i))
										.getText().toString());
								break;

							case PICTURE:
								PictureFieldRecord p = (PictureFieldRecord) formRecord
										.getFields().get(i);
								Log.d("TEST", "picture "
										+ ((EditText) editTextList.get(i))
												.getText().toString());
								p.setValue(((EditText) editTextList.get(i))
										.getText().toString());
								break;

							case HEIGHT:
								HeightFieldRecord h = (HeightFieldRecord) formRecord
										.getFields().get(i);
								h.setValue(Double
										.parseDouble(((EditText) editTextList
												.get(i)).getText().toString()));
								break;

							default:
								throw new IllegalStateException("Unkown field");
							}
						}

						try {
							dbManager.open(context);
//							long idForm = dbManager
//									.insertFormRecord(formRecord);
							
						} catch (SmartException e) {
							Toast.makeText(context, e.getMessage(),
									Toast.LENGTH_LONG).show();
							Log.e("", e.getMessage());
						}
						dbManager.close();
					}
				});

		setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
	}

	/**
	 * Build the form associated to the mission to display it
	 * 
	 * @param l
	 * @param c
	 * @param fieldsList
	 * @throws SmartException 
	 */
	public final void buildForm(TableLayout l, final MenuActivity c,
			List<Field> fieldsList, int idGeometry) throws SmartException {
		editTextList = new LinkedList<Object>();
		
		DbManager dbManager = new DbManager();
		dbManager.open(c);
		int idForm = dbManager.getIdForm(idGeometry);
		FormRecord formRecord = dbManager.getFormRecordTyped(idForm, Mission.getInstance().getForm().getName());
		List<FieldRecord> fieldRecords = formRecord.getFields();
		for (FieldRecord f : fieldRecords){
			Log.d("TEST", f.getField().getLabel());
		}
//		fieldRecords.remove(0);
//		fieldRecords.remove(1);
//		
//		Log.d("TEST", "fieldRecords "+fieldRecords.toString());
//		
//		
//		List<Field> fields = Mission.getInstance().getForm().getFieldsList();
//
//		Log.d("TEST", "fields "+fields.toString());

		
		for(int i=0; i<fieldRecords.size();i++){
	
			TextView textView = new TextView(c);
			final EditText editText = new EditText(c);
			switch (fieldRecords.get(i).getField().getType()) {
			case TEXT:
				TextFieldRecord tf = (TextFieldRecord) fieldRecords.get(i);
				textView.setTag(fieldRecords.get(i).getField().getLabel());
				textView.setText(fieldRecords.get(i).getField().getLabel());
				textView.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, 0);

				l.addView(textView);
				editText.setText(tf.getValue());
				l.addView(editText);
				editTextList.add(editText);
				break;

			case NUMERIC:
				final NumericFieldRecord nf = (NumericFieldRecord) fieldRecords.get(i);
				textView.setText(fieldRecords.get(i).getField().getLabel());
				textView.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, 0);
				editText.setInputType(InputType.TYPE_CLASS_NUMBER);
				editText.setText(String.valueOf(nf.getValue()));
				editText.addTextChangedListener(new TextWatcher() {

					@Override
					public void afterTextChanged(Editable arg0) {
						if (!editText.getText().toString().equals("")) {
							if (Double.parseDouble(editText.getText()
									.toString()) > ((NumericField)nf.getField()).getMax()
									|| Double.parseDouble(editText.getText()
											.toString()) < ((NumericField)nf.getField()).getMin()) {
								editText.setError("Invalid");
							}
						}

					}

					@Override
					public void beforeTextChanged(CharSequence arg0, int arg1,
							int arg2, int arg3) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onTextChanged(CharSequence arg0, int arg1,
							int arg2, int arg3) {

					}
				});

				l.addView(textView);
				l.addView(editText);
				editTextList.add(editText);
				break;

			case BOOLEAN:
				BooleanFieldRecord bf = (BooleanFieldRecord) fieldRecords.get(i);
				textView.setText(fieldRecords.get(i).getField().getLabel());
				textView.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, 0);

				
				RadioGroup group = new RadioGroup(c);
				RadioButton buttonYes = new RadioButton(c);
				buttonYes.setId(0);
				buttonYes.setText(R.string.yes);
				
				RadioButton buttonNo = new RadioButton(c);
				buttonNo.setId(1);
				buttonNo.setText(R.string.no);
				if(bf.getValue()){
					buttonNo.setChecked(true);
				} else {
					buttonYes.setChecked(true);
				}
				group.addView(buttonYes);
				group.addView(buttonNo);

				l.addView(textView);
				l.addView(group);

				editTextList.add(group);
				break;

			case LIST:
				final ListFieldRecord lf = (ListFieldRecord) fieldRecords.get(i);
				textView.setText(fieldRecords.get(i).getField().getLabel());
				textView.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, 0);

				
				Spinner spin = new Spinner(c);
				List<String> strings = ((ListField) fieldRecords.get(i).getField()).getValues();
				spin.setAdapter(new ArrayAdapter<String>(c,
						android.R.layout.simple_list_item_1, strings));
				for(int h=0;h<strings.size();h++){
					if(strings.get(h).equals(lf.getValue())){
						spin.setSelection(h);
					}
				}
				
				spin.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						EditText et = new EditText(c);
						et.setText(((ListField) lf.getField()).getValues().get(position));
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

			case PICTURE:
				PictureFieldRecord pf = (PictureFieldRecord) fieldRecords.get(i);
				final EditText et = new EditText(c);
				textView.setText(fieldRecords.get(i).getField().getLabel());
				textView.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, 0);

				final TextView namePictureView = new TextView(c);

				ImageView takePicture = new ImageView(c);
				takePicture.setClickable(true);
				takePicture.setImageDrawable(c.getResources().getDrawable(
						R.drawable.takepicture));
				takePicture.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"dd/MM/yyyy HH:mm:ss", Locale.FRENCH);
						String date = dateFormat.format(new Date());

						String namePicture = Mission.getInstance().getTitle()
								+ "_" + date;
						namePicture = namePicture.replace(" ", "_");
						namePicture = namePicture.replace(":", "");
						namePicture = namePicture.replace("/", "_");
						Log.d("pictureActivity", "jvai l'appel");
						Intent intent = new Intent(c, PictureActivity.class);
						intent.putExtra("takePicture", true);
						intent.putExtra("namePicture", namePicture);

						c.startActivity(intent);
						Log.d("pictureActivity", "je l'ai appeler !!!!");

						namePictureView.setText(namePicture);

						et.setText(namePicture);
					}
				});

				LinearLayout ll = new LinearLayout(c);
				ll.addView(textView);
				ll.addView(takePicture);
				namePictureView.setText(pf.getValue());

				ll.addView(namePictureView);

				l.addView(ll);
				et.setText(pf.getValue());
				editTextList.add(et);
				break;

			case HEIGHT:
				HeightFieldRecord hf = (HeightFieldRecord) fieldRecords.get(i);
				textView.setText(fieldRecords.get(i).getField().getLabel());
				textView.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, 0);
				editText.setInputType(InputType.TYPE_CLASS_NUMBER);
				editText.setText(String.valueOf(hf.getValue()));
				l.addView(textView);
				l.addView(editText);
				editTextList.add(editText);
				break;

			default:
				throw new IllegalStateException("Unkown field");
			}
		}
	}
}