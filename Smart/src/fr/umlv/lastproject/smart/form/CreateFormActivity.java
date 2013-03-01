package fr.umlv.lastproject.smart.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
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
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.utils.SmartConstants;

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
	Preferences pref;
	private static final int PADDING_LEFT = 40;
	private static final int PADDING_TOP = 30;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pref = Preferences.getInstance(this);
		setTheme(pref.theme);
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

		Button addFieldButton = (Button) findViewById(R.id.buttonAdd);
		addFieldButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LayoutInflater factory = LayoutInflater
						.from(CreateFormActivity.this);
				final View alertDialogView = factory.inflate(
						R.layout.activity_add_field_to_form, null);

				Spinner spin = (Spinner) alertDialogView
						.findViewById(R.id.spinner);
				String[] listStrings = getResources().getStringArray(
						R.array.typeFields);
				spin.setAdapter(new ArrayAdapter<String>(
						CreateFormActivity.this,
						android.R.layout.simple_list_item_1, listStrings));

				spin.setOnItemSelectedListener(new OnItemSelectedListener() {

					private final TableRow rowMax = (TableRow) alertDialogView
							.findViewById(R.id.tableRowMax);
					private final TableRow rowMin = (TableRow) alertDialogView
							.findViewById(R.id.tableRowMin);
					private final TableRow rowList = (TableRow) alertDialogView
							.findViewById(R.id.tableRowList);

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int position, long arg3) {

						allEds.clear();
						fieldType = FieldType.getFromId(position);

						switch (fieldType) {
						case TEXT:
							rowMax.setVisibility(View.GONE);
							rowMin.setVisibility(View.GONE);
							rowList.setVisibility(View.GONE);
							break;

						case NUMERIC:
							// TODO
							// rowMax.setVisibility(View.VISIBLE);
							// rowMin.setVisibility(View.VISIBLE);
							rowList.setVisibility(View.GONE);
							break;

						case BOOLEAN:
							rowMax.setVisibility(View.GONE);
							rowMin.setVisibility(View.GONE);
							rowList.setVisibility(View.GONE);
							break;

						case LIST:
							rowMax.setVisibility(View.GONE);
							rowMin.setVisibility(View.GONE);
							rowList.setVisibility(View.VISIBLE);
							break;

						case PICTURE:
							rowMax.setVisibility(View.GONE);
							rowMin.setVisibility(View.GONE);
							rowList.setVisibility(View.GONE);
							break;

						case HEIGHT:
							rowMax.setVisibility(View.GONE);
							rowMin.setVisibility(View.GONE);
							rowList.setVisibility(View.GONE);
							break;

						default:
							throw new IllegalStateException("Unkown field type");
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// Nothing to do
					}
				});

				final AlertDialog.Builder adb = new AlertDialog.Builder(
						CreateFormActivity.this);

				adb.setView(alertDialogView);
				adb.setTitle(getResources().getString(R.string.AddField));

				adb.setPositiveButton(R.string.validate,
						new DialogInterface.OnClickListener() {

							// Add the new field
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								EditText labelValue = (EditText) alertDialogView
										.findViewById(R.id.valueName);
								String label = labelValue.getText().toString();

								if (form.isLabelExist(label)) {
									Toast.makeText(
											CreateFormActivity.this,
											getString(R.string.field_name_already_used),
											Toast.LENGTH_LONG).show();
									return;
								}

								switch (fieldType) {
								case TEXT:
									TextField tf = new TextField(label);
									form.addField(tf);
									addFieldRow(tf);
									break;

								case NUMERIC:
									// TODO
									// EditText maxValue = (EditText)
									// alertDialogView
									// .findViewById(R.id.valueMax);
									// int max = Integer.parseInt(maxValue
									// .getText().toString());
									//
									// EditText minValue = (EditText)
									// alertDialogView
									// .findViewById(R.id.valueMin);
									// int min = Integer.parseInt(minValue
									// .getText().toString());
									//
									// if (min > max) {
									// Toast.makeText(
									// CreateFormActivity.this,
									// getString(R.string.field_error_min_max),
									// Toast.LENGTH_LONG).show();
									// return;
									// }

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
									EditText listValue = (EditText) alertDialogView
											.findViewById(R.id.valueList);
									String[] tab = listValue.getText()
											.toString().split("/");

									ListField lf = new ListField(label, Arrays
											.asList(tab));
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
									throw new IllegalStateException(
											"Unkown field type");
								}
							}
						});

				adb.setNegativeButton(getString(R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
				adb.show();
			}
		});

		Button validate = (Button) findViewById(R.id.buttonValidate);
		validate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Save the form
				try {
					form.write(SmartConstants.FORM_PATH);
				} catch (FormIOException e) {
					Toast.makeText(getApplicationContext(), e.getMessage(),
							Toast.LENGTH_LONG).show();
				}

				//if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
					// Switch to our application
					//Intent intentMenuActivity = new Intent(
					//		CreateFormActivity.this, MenuActivity.class);
					//intentMenuActivity.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
					// navigateUpTo(intentMenuActivity); // API 16 min...
				//}
				finish();
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
		image.setImageDrawable(getResources().getDrawable(R.drawable.basket));
		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				row.removeAllViews();
				form.deleteField(field.getLabel());
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
			text.setText(getString(R.string.field_list) + field.getLabel());
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

	@Override
	protected void onPause() {
		super.onPause();
		pref.save();
	}
}
