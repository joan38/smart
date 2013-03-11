package fr.umlv.lastproject.smart.dialog;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
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
import fr.umlv.lastproject.smart.database.FieldRecord;
import fr.umlv.lastproject.smart.database.FormRecord;
import fr.umlv.lastproject.smart.database.HeightFieldRecord;
import fr.umlv.lastproject.smart.database.ListFieldRecord;
import fr.umlv.lastproject.smart.database.NumericFieldRecord;
import fr.umlv.lastproject.smart.database.PictureFieldRecord;
import fr.umlv.lastproject.smart.database.TextFieldRecord;
import fr.umlv.lastproject.smart.form.Field;
import fr.umlv.lastproject.smart.form.Form;
import fr.umlv.lastproject.smart.form.FormEditedListener;
import fr.umlv.lastproject.smart.form.ListField;
import fr.umlv.lastproject.smart.form.Mission;
import fr.umlv.lastproject.smart.form.PictureActivity;
import fr.umlv.lastproject.smart.layers.Geometry;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.utils.SmartException;
import fr.umlv.lastproject.smart.utils.SmartLogger;

/**
 * This class is used to display the form and change the values of differents
 * fields
 * 
 * @author Maelle Cabot
 * 
 */
public class ModifFormDialog extends AlertDialog.Builder {

	private Object[] valuesList;
	private TableLayout layoutDynamic;

	private static final int PADDING_LEFT = 20;
	private static final int PADDING_TOP = 10;
	private static final int PADDING_RIGHT = 5;

	private static final String UNKNOW_FIELD = "Unkown field";

	private static final DecimalFormat ACCURACY_FORMAT = new DecimalFormat(
			"#####0.00");
	private final Form form;
	private final Geometry geom;
	private final GeometryLayer layer;
	private final Logger logger = SmartLogger.getLocator().getLogger();
	private List<FormEditedListener> listeners = new ArrayList<FormEditedListener>();

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param form
	 * @param l
	 * @param g
	 *            : the geometry
	 * @param mission
	 */
	public ModifFormDialog(final MenuActivity context, final Form form,
			final Geometry g, final GeometryLayer l, final Object[] values) {
		super(context);
		this.form = form;
		this.geom = g;
		this.layer = l;

		final long idGeometry = g.getId();
		setCancelable(false);
		final LayoutInflater factory = LayoutInflater.from(context);
		final View alertDialogView = factory.inflate(
				fr.umlv.lastproject.smart.R.layout.activity_formulaire_viewer,
				null);

		layoutDynamic = (TableLayout) alertDialogView
				.findViewById(R.id.layoutDynamicFormulaire);
		layoutDynamic.setVerticalScrollBarEnabled(true);
		valuesList = new Object[form.getFieldsList().size()];

		int idForm = 0;
		try {
			idForm = buildForm(layoutDynamic, context, form.getFieldsList(),
					idGeometry, values);

		} catch (SmartException e) {
			logger.log(Level.SEVERE, "No geometry in database");
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
			Log.e("", e.getMessage());

		}

		setView(alertDialogView);
		setTitle(R.string.form_title);
		final int idRowForm = idForm;
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
								l.setValue(((EditText) valuesList[i]).getText()
										.toString());
								break;

							case PICTURE:
								PictureFieldRecord p = (PictureFieldRecord) formRecord
										.getFields().get(i);
								p.setValue(((EditText) valuesList[i]).getText()
										.toString());
								break;

							case HEIGHT:
								HeightFieldRecord h = (HeightFieldRecord) formRecord
										.getFields().get(i);
								h.setValue(Double.parseDouble(valuesList[i]
										.toString()));
								break;

							default:
								throw new IllegalStateException(UNKNOW_FIELD);
							}
						}

						try {
							dbManager.open(context);
							dbManager.updateFormRecord(formRecord, idRowForm);

						} catch (SmartException e) {
							Toast.makeText(context, e.getMessage(),
									Toast.LENGTH_LONG).show();
							Log.e("", e.getMessage());
						}
						dbManager.close();
						for (FormEditedListener l : listeners) {
							l.actionPerformed(g);
						}
					}
				});

		setNeutralButton(R.string.delete,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						ValidationDeleteSurveyDialog dialogDelete = new ValidationDeleteSurveyDialog(
								context, idGeometry, idRowForm,
								form.getTitle(), l, g, listeners);
						dialogDelete.show();
					}
				});

		setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						for (FormEditedListener l : listeners) {
							l.actionPerformed(g);
						}

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
	public final int buildForm(TableLayout l, final MenuActivity c,
			List<Field> fieldsList, long idGeometry, Object[] values)
			throws SmartException {

		boolean isAlreadyFilled = false;
		if (values != null) {
			isAlreadyFilled = true;
		} else {
			isAlreadyFilled = false;
		}

		DbManager dbManager = new DbManager();
		dbManager.open(c);
		int idForm = dbManager.getIdForm(idGeometry);
		FormRecord formRecord = dbManager.getFormRecordTyped(idForm, Mission
				.getInstance().getForm().getTitle());
		List<FieldRecord> fieldRecords = formRecord.getFields();

		for (int i = 0; i < fieldRecords.size(); i++) {

			TextView textView = new TextView(c);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
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
				valuesList[i] = editText;
				break;

			case NUMERIC:
				final NumericFieldRecord nf = (NumericFieldRecord) fieldRecords
						.get(i);
				textView.setText(fieldRecords.get(i).getField().getLabel());
				textView.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, 0);
				editText.setInputType(InputType.TYPE_CLASS_NUMBER
						| InputType.TYPE_NUMBER_FLAG_DECIMAL);
				editText.setText(String.valueOf(nf.getValue()));
				editText.addTextChangedListener(new TextWatcher() {

					@Override
					public void afterTextChanged(Editable arg0) {

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
				BooleanFieldRecord bf = (BooleanFieldRecord) fieldRecords
						.get(i);
				textView.setText(fieldRecords.get(i).getField().getLabel());
				textView.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, 0);

				RadioGroup group = new RadioGroup(c);
				RadioButton buttonYes = new RadioButton(c);
				buttonYes.setId(0);
				buttonYes.setText(R.string.yes);

				RadioButton buttonNo = new RadioButton(c);
				buttonNo.setId(1);
				buttonNo.setText(R.string.no);
				if (bf.getValue()) {
					buttonYes.setChecked(true);
					valuesList[i] = 0;
				} else {
					buttonNo.setChecked(true);
					valuesList[i] = 1;
				}
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
				final ListFieldRecord lf = (ListFieldRecord) fieldRecords
						.get(i);
				textView.setText(fieldRecords.get(i).getField().getLabel());
				textView.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, 0);

				Spinner spin = new Spinner(c);
				List<String> strings = ((ListField) fieldRecords.get(i)
						.getField()).getValues();
				spin.setAdapter(new ArrayAdapter<String>(c,
						android.R.layout.simple_list_item_1, strings));
				for (int h = 0; h < strings.size(); h++) {
					if (strings.get(h).equals(lf.getValue())) {
						spin.setSelection(h);
					}
				}
				final int g = i;

				spin.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						EditText et = new EditText(c);
						et.setText(((ListField) lf.getField()).getValues().get(
								position));
						valuesList[g] = et;
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
				PictureFieldRecord pf = (PictureFieldRecord) fieldRecords
						.get(i);
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
						Intent intent = new Intent(c, PictureActivity.class);
						intent.putExtra("takePicture", true);
						intent.putExtra("namePicture", namePicture);

						c.startActivity(intent);

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
				valuesList[i] = et;
				break;

			case HEIGHT:
				HeightFieldRecord hf = (HeightFieldRecord) fieldRecords.get(i);

				final int heightIndex = i;
				textView.setText(hf.getField().getLabel());
				textView.setPadding(PADDING_LEFT, PADDING_TOP, PADDING_RIGHT, 0);
				editText.setInputType(InputType.TYPE_CLASS_NUMBER);
				final TextView heightName = new TextView(c);
				double t;
				if (isAlreadyFilled) {
					t = Double.parseDouble(String.valueOf(values[i]));
				} else {
					t = Double.parseDouble(String.valueOf(hf.getValue()));
				}
				heightName.setText(ACCURACY_FORMAT.format(t) + " m");

				final ImageView heightPicture = new ImageView(c);
				heightPicture.setClickable(true);
				heightPicture.setImageDrawable(c.getResources().getDrawable(
						R.drawable.toisemeasure));
				heightPicture.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						c.startModifHeightActivityForResult(layer, form, geom,
								valuesList, heightIndex);

					}
				});
				LinearLayout heightLayout = new LinearLayout(c);
				heightLayout.addView(textView);
				heightLayout.addView(heightPicture);
				heightLayout.addView(heightName);
				layoutDynamic.addView(heightLayout);
				if (isAlreadyFilled) {
					valuesList[i] = values[i];
				} else {
					valuesList[i] = hf.getValue();
				}
				break;

			default:
				throw new IllegalStateException(UNKNOW_FIELD);
			}
		}
		return idForm;
	}

	/**
	 * 
	 * @param fel
	 *            add the listener
	 */
	public void addFormEditedListener(FormEditedListener fel) {
		listeners.add(fel);
	}

	/**
	 * 
	 * @param fel
	 *            the listener to remove
	 */
	public void removeFormEditedListener(FormEditedListener fel) {
		listeners.remove(fel);
	}

}