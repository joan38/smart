package fr.umlv.lastproject.smart.form;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.Preferences;
import fr.umlv.lastproject.smart.PreferencesException;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.utils.SmartConstants;
import fr.umlv.lastproject.smart.utils.SmartLogger;

/**
 * Creation's activity to make a new form
 * 
 * @author Maelle Cabot
 * 
 */
public class CreateFormActivity extends Activity {

	private Form form;
	private TableLayout tableLayout;
	private final List<EditText> allEds = new ArrayList<EditText>();
	private FieldType fieldType;
	private List<EditText> listFieldValues = new ArrayList<EditText>();
	private List<TableRow> rowDynamic = new ArrayList<TableRow>();
	private Preferences pref;
	private static final int PADDING_LEFT = 40;
	private static final int PADDING_TOP = 30;
	private Button validate;
	private final Logger logger = SmartLogger.getLocator().getLogger();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			pref = Preferences.getInstance(this);
		} catch (PreferencesException e) {
			Toast.makeText(this, getString(R.string.unableLoadPref),
					Toast.LENGTH_LONG).show();
		}
		setTheme(pref.getTheme());
		setRequestedOrientation(pref.getOrientation());
		setContentView(R.layout.activity_create_form);
		setTitle(getString(R.string.title_activity_create_form));

		if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
			try {
				form = Form.read(getIntent().getData().getPath());
			} catch (FormIOException e) {
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
				finish();
				return;
			}
		} else {
			form = (Form) getIntent().getSerializableExtra("form");
		}

		tableLayout = (TableLayout) findViewById(R.id.layoutDynamicCreateFormulaire);
		tableLayout.removeAllViewsInLayout();

		for (Field field : form.getFieldsList()) {
			addFieldRow(field);
		}

		validate = (Button) findViewById(R.id.buttonValidate);

		Button addFieldButton = (Button) findViewById(R.id.buttonAdd);
		addFieldButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				addFieldDialog();
			}
		});

		validate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Save the form
				try {
					File f = new File(SmartConstants.FORM_PATH);
					if (!f.exists()) {
						f.mkdir();
					}
					form.write(SmartConstants.FORM_PATH);
					logger.log(Level.INFO, "Form " + form.getTitle() + " saved");
				} catch (FormIOException e) {
					logger.log(Level.SEVERE, "Form unsaved : " + e.getMessage());
					Toast.makeText(getApplicationContext(), e.getMessage(),
							Toast.LENGTH_LONG).show();
				}

				// Switch to our application
				Intent intentMenuActivity = new Intent(CreateFormActivity.this,
						MenuActivity.class);
				intentMenuActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intentMenuActivity);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_create_form, menu);
		return true;
	}

	private void addFieldRow(final Field field) {
		final TableRow row = new TableRow(CreateFormActivity.this);

		ImageView image = new ImageView(CreateFormActivity.this);
		image.setClickable(true);
		image.setImageDrawable(getResources().getDrawable(R.drawable.delete));
		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				row.removeAllViews();
				Log.d("TEST", " name field " + field.getLabel());
				form.deleteField(field.getLabel());
				if (form.getFieldsList().isEmpty()) {
					validate.setEnabled(false);
				} else {
					validate.setEnabled(true);
				}
			}
		});
		row.addView(image);

		TextView text = new TextView(CreateFormActivity.this);
		switch (field.getType()) {
		case BOOLEAN:
			text.setText(getString(R.string.field_boolean) + field.getLabel());
			break;

		case HEIGHT:
			text.setText(getString(R.string.field_height) + field.getLabel());
			break;

		case LIST:
			ListField l = (ListField) field;
			StringBuilder values = new StringBuilder();
			for (String s : l.getValues()) {
				values.append(s).append(", ");
			}
			values.replace(values.length() - 2, values.length(), "");
			text.setText(getString(R.string.field_list) + field.getLabel()
					+ " " + values);
			break;

		case NUMERIC:
			text.setText(getString(R.string.field_numeric) + field.getLabel());
			break;

		case PICTURE:
			text.setText(getString(R.string.field_picture) + field.getLabel());
			break;

		case TEXT:
			text.setText(getString(R.string.field_text) + field.getLabel());
			break;

		default:
			throw new IllegalStateException("Unkown field type");
		}
		text.setPadding(PADDING_LEFT, PADDING_TOP, 0, 0);
		row.addView(text);

		tableLayout.addView(row);
	}

	private void addFieldDialog() {
		LayoutInflater factory = LayoutInflater.from(CreateFormActivity.this);
		final View alertDialogView = factory.inflate(
				R.layout.activity_add_field_to_form, null);
		final TableLayout tableLayoutAddField = (TableLayout) alertDialogView
				.findViewById(R.id.layoutDynamicAddField);
		final AlertDialog.Builder adb = new AlertDialog.Builder(
				CreateFormActivity.this);

		adb.setView(alertDialogView);
		adb.setTitle(getResources().getString(R.string.AddField));

		final EditText labelValue = (EditText) alertDialogView
				.findViewById(R.id.valueName);

		adb.setPositiveButton(R.string.validate,
				new DialogInterface.OnClickListener() {

					// Add the new field
					@Override
					public void onClick(DialogInterface dialog, int which) {
						validNewField(labelValue);
					}
				});

		adb.setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});

		final AlertDialog alert = adb.create();
		alert.show();
		alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

		Spinner spin = (Spinner) alertDialogView.findViewById(R.id.spinner);
		String[] listStrings = getResources()
				.getStringArray(R.array.typeFields);
		spin.setAdapter(new ArrayAdapter<String>(CreateFormActivity.this,
				android.R.layout.simple_list_item_1, listStrings));

		spin.setOnItemSelectedListener(new OnItemSelectedListener() {

			private final TableRow rowList = (TableRow) alertDialogView
					.findViewById(R.id.tableRowList);

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				spinnerSelection(position, rowList, tableLayoutAddField,
						labelValue, alert, alertDialogView);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// Nothing to do
			}
		});

	}

	private void spinnerSelection(int position, TableRow rowList,
			final TableLayout tableLayoutAddField, final EditText labelValue,
			final AlertDialog alert, View alertDialogView) {
		allEds.clear();
		fieldType = FieldType.getFromId(position);

		for (TableRow r : rowDynamic) {
			tableLayoutAddField.removeView(r);
		}

		labelValue.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().equals("")) {
					alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
							false);
				} else if (form.isLabelExist(labelValue.getText().toString())) {
					labelValue.setError(getResources().getString(
							R.string.field_name_already_used));
					alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
							false);
				} else if (fieldType == FieldType.LIST
						&& listFieldValues.isEmpty()) {
					alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
							false);
				} else {
					alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
							true);
				}
			}
		});

		switch (fieldType) {
		case TEXT:
			rowList.setVisibility(View.GONE);
			break;

		case NUMERIC:
			rowList.setVisibility(View.GONE);
			break;

		case BOOLEAN:
			rowList.setVisibility(View.GONE);
			break;

		case LIST:
			rowList.setVisibility(View.VISIBLE);
			ImageView imageAdd = (ImageView) alertDialogView
					.findViewById(R.id.plusImage);
			imageAdd.setImageResource(R.drawable.add);
			imageAdd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
							false);
					final TableRow row = new TableRow(CreateFormActivity.this);
					rowDynamic.add(row);
					final AutoCompleteTextView listValues = new AutoCompleteTextView(
							CreateFormActivity.this);
					listValues.addTextChangedListener(new TextWatcher() {

						@Override
						public void onTextChanged(CharSequence s, int start,
								int before, int count) {
							// TODO Auto-generated method stub

						}

						@Override
						public void beforeTextChanged(CharSequence s,
								int start, int count, int after) {
							// TODO Auto-generated method stub

						}

						@Override
						public void afterTextChanged(Editable s) {
							if (listFieldValues.isEmpty()) {
								alert.getButton(AlertDialog.BUTTON_POSITIVE)
										.setEnabled(false);
							} else if (labelValue.getText().toString()
									.equals("")) {
								alert.getButton(AlertDialog.BUTTON_POSITIVE)
										.setEnabled(false);
							} else {
								alert.getButton(AlertDialog.BUTTON_POSITIVE)
										.setEnabled(true);
							}
						}
					});
					final ImageView imageDelete = new ImageView(
							CreateFormActivity.this);
					imageDelete.setImageResource(R.drawable.delete);
					imageDelete.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							listFieldValues.remove(listValues);
							if (listFieldValues.isEmpty()) {
								alert.getButton(AlertDialog.BUTTON_POSITIVE)
										.setEnabled(false);
							} else if (labelValue.getText().toString()
									.equals("")) {
								alert.getButton(AlertDialog.BUTTON_POSITIVE)
										.setEnabled(false);
							} else {
								alert.getButton(AlertDialog.BUTTON_POSITIVE)
										.setEnabled(true);
							}
							row.removeView(imageDelete);
							row.removeView(listValues);
						}
					});

					listFieldValues.add(listValues);
					listValues.requestFocus();
					row.addView(imageDelete);
					row.addView(listValues);
					tableLayoutAddField.addView(row);
				}
			});
			break;

		case PICTURE:
			rowList.setVisibility(View.GONE);
			break;

		case HEIGHT:
			rowList.setVisibility(View.GONE);
			break;

		default:
			throw new IllegalStateException("Unkown field type");
		}
	}

	private void validNewField(EditText labelValue) {
		String label = labelValue.getText().toString();
		switch (fieldType) {
		case TEXT:
			TextField tf = new TextField(label);
			form.addField(tf);
			addFieldRow(tf);
			break;

		case NUMERIC:
			NumericField nf = new NumericField(label);
			form.addField(nf);
			addFieldRow(nf);
			break;

		case BOOLEAN:
			BooleanField bf = new BooleanField(label);
			form.addField(bf);
			addFieldRow(bf);
			break;

		case LIST:
			ArrayList<String> list = new ArrayList<String>();
			for (EditText e : listFieldValues) {
				list.add(e.getText().toString());
			}
			listFieldValues.clear();
			ListField lf = new ListField(label, list);
			form.addField(lf);
			addFieldRow(lf);
			break;

		case PICTURE:
			PictureField pf = new PictureField(label);
			form.addField(pf);
			addFieldRow(pf);
			break;

		case HEIGHT:
			HeightField hf = new HeightField(label);
			form.addField(hf);
			addFieldRow(hf);
			break;

		default:
			throw new IllegalStateException("Unkown field type");
		}
		if (form.getFieldsList().isEmpty()) {
			validate.setEnabled(false);
		} else {
			validate.setEnabled(true);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			pref.save();
		} catch (PreferencesException e) {
			Toast.makeText(this, getString(R.string.unableLoadPref),
					Toast.LENGTH_LONG).show();
		}
	}
}
