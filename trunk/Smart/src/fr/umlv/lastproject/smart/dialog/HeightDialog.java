package fr.umlv.lastproject.smart.dialog;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
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
import fr.umlv.lastproject.smart.utils.SmartException;

/**
 * This class is used to display the form The user can fill the differents
 * fields
 * 
 * @author Maelle Cabot
 * 
 */
public class HeightDialog extends AlertDialog.Builder {

	private Object[] valuesList;
	private TableLayout layoutDynamic;

	private static final int PADDING_LEFT = 20;
	private static final int PADDING_TOP = 10;
	private static final int PADDING_RIGHT = 5;

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param form
	 * @param g
	 *            : the geometry
	 * @param mission
	 */
	public HeightDialog(final MenuActivity context, final Form form,
			final Geometry g, final Mission mission) {
		super(context);
		setCancelable(false);
		final LayoutInflater factory = LayoutInflater.from(context);
		final View alertDialogView = factory.inflate(
				fr.umlv.lastproject.smart.R.layout.activity_formulaire_viewer,
				null);

		layoutDynamic = (TableLayout) alertDialogView
				.findViewById(R.id.layoutDynamicFormulaire);
		layoutDynamic.setVerticalScrollBarEnabled(true);

		valuesList = new Object[form.getFieldsList().size()];

		buildForm(layoutDynamic, context, form.getFieldsList());

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
								text.setValue(((EditText) valuesList[i])
										.getText().toString());
								break;

							case NUMERIC:
								NumericFieldRecord num = (NumericFieldRecord) formRecord
										.getFields().get(i);
								if (!((EditText) valuesList[i]).getText()
										.toString().equals("")) {
									num.setValue(Double
											.parseDouble(((EditText) valuesList[i])
													.getText().toString()));
								} else {
									num.setValue(-1);
								}
								break;

							case BOOLEAN:
								BooleanFieldRecord b = (BooleanFieldRecord) formRecord
										.getFields().get(i);

								Integer idChecked = (Integer) valuesList[i];
								if (idChecked == 0) {
									b.setValue(true);
								} else {
									b.setValue(false);
								}
								break;

							case LIST:
								ListFieldRecord l = (ListFieldRecord) formRecord
										.getFields().get(i);
								Log.d("TEST", "edit "
										+ ((EditText) valuesList[i]).getText());
								l.setValue(((EditText) valuesList[i]).getText()
										.toString());
								break;

							case PICTURE:
								PictureFieldRecord p = (PictureFieldRecord) formRecord
										.getFields().get(i);
								Log.d("TEST", "picture "
										+ ((EditText) valuesList[i]).getText()
												.toString());
								p.setValue(((EditText) valuesList[i]).getText()
										.toString());
								break;

							case HEIGHT:
								HeightFieldRecord h = (HeightFieldRecord) formRecord
										.getFields().get(i);
								h.setValue(Double
										.parseDouble(((EditText) valuesList[i])
												.getText().toString()));
								break;

							default:
								throw new IllegalStateException(
										"Unkown field type");
							}
						}

						try {
							dbManager.open(context);
							long idForm = dbManager
									.insertFormRecord(formRecord);
							long idGeom = dbManager
									.insertGeometry(new GeometryRecord(g,
											Mission.getInstance().getId(),
											idForm));
							g.setId(idGeom);
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
						mission.removeGeometry(g);
					}
				});
	}

	/**
	 * Build the form associated to the mission to display it
	 * 
	 * @param l
	 * @param c
	 * @param fieldsList
	 */
	public final void buildForm(TableLayout l, final MenuActivity c,
			List<Field> fieldsList) {

		for (int i = 0; i < fieldsList.size(); i++) {
			Field field = fieldsList.get(i);
			TextView textView = new TextView(c);
			final EditText editText = new EditText(c);
			switch (field.getType()) {
			case TEXT:
				TextField tf = (TextField) field;
				textView.setTag(tf.getLabel());
				textView.setText(tf.getLabel());
				textView.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, 0);

				l.addView(textView);
				l.addView(editText);
				valuesList[i] = editText;
				break;

			case NUMERIC:
				final NumericField nf = (NumericField) field;
				textView.setText(nf.getLabel());
				textView.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, 0);
				editText.setInputType(InputType.TYPE_CLASS_NUMBER
						| InputType.TYPE_NUMBER_FLAG_DECIMAL);
				editText.addTextChangedListener(new TextWatcher() {

					@Override
					public void afterTextChanged(Editable arg0) {
						// TODO
						// if (editText.getText().toString().equals("")){
						// editText.setError("Invalid");
						//
						// } else if(
						// Double.parseDouble(editText.getText().toString()) >=
						// nf.getMax()
						// || Double.parseDouble(editText.getText()
						// .toString()) <= nf.getMin()) {
						// editText.setError("Invalid");
						// }

					}

					@Override
					public void beforeTextChanged(CharSequence arg0, int arg1,
							int arg2, int arg3) {
					}

					@Override
					public void onTextChanged(CharSequence arg0, int arg1,
							int arg2, int arg3) {

					}
				});

				l.addView(textView);
				l.addView(editText);
				valuesList[i] = editText;
				break;

			case BOOLEAN:
				BooleanField bf = (BooleanField) field;
				textView.setText(bf.getLabel());
				textView.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, 0);

				RadioGroup group = new RadioGroup(c);
				RadioButton buttonYes = new RadioButton(c);
				buttonYes.setId(0);
				buttonYes.setText(R.string.yes);
				buttonYes.setChecked(true);
				RadioButton buttonNo = new RadioButton(c);
				buttonNo.setId(1);
				buttonNo.setText(R.string.no);
				valuesList[i] = 0;

				group.addView(buttonYes);
				group.addView(buttonNo);
				final int j = i;
				group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {

						valuesList[j] = checkedId;
					}
				});

				l.addView(textView);
				l.addView(group);

				break;

			case LIST:
				final ListField lf = (ListField) field;
				textView.setText(lf.getLabel());
				textView.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, 0);

				Spinner spin = new Spinner(c);
				List<String> strings = lf.getValues();
				spin.setAdapter(new ArrayAdapter<String>(c,
						android.R.layout.simple_list_item_1, strings));

				final int h = i;

				spin.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						EditText et = new EditText(c);
						et.setText(lf.getValues().get(position));
						Log.d("TEST", "selected "
								+ lf.getValues().get(position));
						valuesList[h] = et;
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
				PictureField pf = (PictureField) field;
				final EditText et = new EditText(c);

				textView.setText(pf.getLabel());
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
				ll.addView(namePictureView);

				l.addView(ll);

				valuesList[i] = et;
				break;

			case HEIGHT:
				HeightField hf = (HeightField) field;

				textView.setText(hf.getLabel());
				textView.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, 0);
				editText.setInputType(InputType.TYPE_CLASS_NUMBER);

				final ImageView heightPicture = new ImageView(c);
				heightPicture.setClickable(true);
				heightPicture.setImageDrawable(c.getResources().getDrawable(
						R.drawable.toise));
				heightPicture.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						Intent intent = new Intent(c, PictureActivity.class);

						c.startActivityForResult(intent,
								SmartConstants.HEIGHT_ACTIVITY);

					}
				});
				LinearLayout heightLayout = new LinearLayout(c);
				heightLayout.addView(textView);
				heightLayout.addView(heightPicture);
				// zheightLayout.addView(heightPicture);
				// l.addView(editText);
				// valuesList[i]=editText;
				valuesList[i] = 0;
				l.addView(heightLayout);

				break;

			default:
				throw new IllegalStateException("Unkown field type");
			}
		}
	}
}