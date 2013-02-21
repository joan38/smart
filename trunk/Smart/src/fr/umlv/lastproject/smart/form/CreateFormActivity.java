package fr.umlv.lastproject.smart.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.utils.SmartConstants;

/**
 * Creation's activity to make a new form
 * 
 * @author Maelle Cabot
 * 
 */
public class CreateFormActivity extends Activity {

	private final Form form = new Form();
	private TableLayout layoutDynamic;
	private Spinner spin;
	private final List<EditText> allEds = new ArrayList<EditText>();
	private int type;
	
	private static final String MIN = " Min : ";
	private static final String MAX = " Max : ";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_form);

		layoutDynamic = (TableLayout) findViewById(R.id.layoutDynamicCreateFormulaire);
		layoutDynamic.removeAllViewsInLayout();

		String name = (String) getIntent().getSerializableExtra("nameForm");

		form.setName(name);

		final Context c = this;

		Button addFieldButton = (Button) findViewById(R.id.buttonAdd);
		addFieldButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				LayoutInflater factory = LayoutInflater.from(c);
				final View alertDialogView = factory
						.inflate(
								fr.umlv.lastproject.smart.R.layout.activity_add_field_to_form,
								null);

				spin = (Spinner) alertDialogView.findViewById(R.id.spinner);
				String[] listeStrings = getResources().getStringArray(
						R.array.typeFields);
				spin.setAdapter(new ArrayAdapter<String>(c,
						android.R.layout.simple_list_item_1, listeStrings));

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

						switch (position) {
						case SmartConstants.TEXT_FIELD:

							rowMax.setVisibility(View.GONE);
							rowMin.setVisibility(View.GONE);
							rowList.setVisibility(View.GONE);
							type = SmartConstants.TEXT_FIELD;
							break;
						case SmartConstants.NUMERIC_FIELD:
							rowMax.setVisibility(View.VISIBLE);
							rowMin.setVisibility(View.VISIBLE);
							rowList.setVisibility(View.GONE);

							type = SmartConstants.NUMERIC_FIELD;
							break;
						case SmartConstants.BOOLEAN_FIELD:
							rowMax.setVisibility(View.GONE);
							rowMin.setVisibility(View.GONE);
							rowList.setVisibility(View.GONE);
							type = SmartConstants.BOOLEAN_FIELD;
							break;
						case SmartConstants.LIST_FIELD:
							rowMax.setVisibility(View.GONE);
							rowMin.setVisibility(View.GONE);
							rowList.setVisibility(View.VISIBLE);
							type = SmartConstants.LIST_FIELD;
							break;
						case SmartConstants.PICTURE_FIELD:
							rowMax.setVisibility(View.GONE);
							rowMin.setVisibility(View.GONE);
							rowList.setVisibility(View.GONE);

							type = SmartConstants.PICTURE_FIELD;
							break;
						case SmartConstants.HEIGHT_FIELD:
							rowMax.setVisibility(View.GONE);
							rowMin.setVisibility(View.GONE);
							rowList.setVisibility(View.GONE);

							type = SmartConstants.HEIGHT_FIELD;
							break;
						default:
						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						//

					}

				});
				final AlertDialog.Builder adb = new AlertDialog.Builder(c);

				adb.setView(alertDialogView);
				adb.setTitle(getResources().getString(R.string.AddField));

				adb.setPositiveButton(R.string.validate,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								final TableRow row = new TableRow(c);
								final TextView view = new TextView(c);
								String label = null;
								boolean erreur = false;

								EditText labelValue = null;
								EditText maxValue = null;
								EditText minValue = null;
								EditText listValue = null;

								int max = -1;
								int min = -1;

								switch (type) {
								case SmartConstants.TEXT_FIELD:
									labelValue = (EditText) alertDialogView
											.findViewById(R.id.valueName);
									label = labelValue.getText().toString();

									if (!form.searchLabel(label)) {
										erreur = true;
										Toast.makeText(
												c,
												getString(R.string.field_name_already_used),
												Toast.LENGTH_LONG).show();
									} else {

										TextField t = new TextField(label);
										view.setText(getString(R.string.field_text)
												+ t.getLabel());

										form.addField(t);
									}
									break;
								case SmartConstants.NUMERIC_FIELD:

									labelValue = (EditText) alertDialogView
											.findViewById(R.id.valueName);
									label = labelValue.getText().toString();
									maxValue = (EditText) alertDialogView
											.findViewById(R.id.valueMax);
									max = Integer.parseInt(maxValue.getText()
											.toString());
									minValue = (EditText) alertDialogView
											.findViewById(R.id.valueMin);
									min = Integer.parseInt(minValue.getText()
											.toString());

									if (!form.searchLabel(label)) {
										erreur = true;
										Toast.makeText(
												c,
												getString(R.string.field_name_already_used),
												Toast.LENGTH_LONG).show();
									} else if (min > max) {
										erreur = true;
										Toast.makeText(
												c,
												getString(R.string.field_error_min_max),
												Toast.LENGTH_LONG).show();
									} else {

										NumericField num = new NumericField(
												label, min, max);
										view.setText(getString(R.string.field_numeric)
												+ num.getLabel()
												+ MAX 
												+ num.getMax()
												+ MIN
												+ num.getMin());

										form.addField(num);
									}

									break;
								case SmartConstants.BOOLEAN_FIELD:
									labelValue = (EditText) alertDialogView
											.findViewById(R.id.valueName);
									label = labelValue.getText().toString();

									if (!form.searchLabel(label)) {
										erreur = true;

										Toast.makeText(
												c,
												getString(R.string.field_name_already_used),
												Toast.LENGTH_LONG).show();
									} else {
										BooleanField b = new BooleanField(label);
										view.setText(getString(R.string.field_boolean)
												+ b.getLabel());

										form.addField(b);
									}
									break;
								case SmartConstants.LIST_FIELD:
									labelValue = (EditText) alertDialogView
											.findViewById(R.id.valueName);
									label = labelValue.getText().toString();

									listValue = (EditText) alertDialogView
											.findViewById(R.id.valueList);
									String[] tab = listValue.getText()
											.toString().split("/");
									if (!form.searchLabel(label)) {
										erreur = true;

										Toast.makeText(
												c,
												getString(R.string.field_name_already_used),
												Toast.LENGTH_LONG).show();
									} else {
										List<String> list = Arrays.asList(tab);

										ListField listField = new ListField(
												label, list);
										view.setText(getString(R.string.field_list)
												+ listField.getLabel());
										form.addField(listField);

									}
									break;
								case SmartConstants.PICTURE_FIELD:
									labelValue = (EditText) alertDialogView
											.findViewById(R.id.valueName);
									label = labelValue.getText().toString();
									if (!form.searchLabel(label)) {
										erreur = true;
										Toast.makeText(
												c,
												getString(R.string.field_name_already_used),
												Toast.LENGTH_LONG).show();
									} else {
										PictureField p = new PictureField(label);
										view.setText(getString(R.string.field_picture)
												+ p.getLabel());

										form.addField(p);
									}
									break;
								case SmartConstants.HEIGHT_FIELD:
									labelValue = (EditText) alertDialogView
											.findViewById(R.id.valueName);
									label = labelValue.getText().toString();
									if (!form.searchLabel(label)) {
										erreur = true;
										Toast.makeText(
												c,
												getString(R.string.field_name_already_used),
												Toast.LENGTH_LONG).show();

									} else {
										HeightField h = new HeightField(label);
										view.setText(getString(R.string.field_height)
												+ h.getLabel());

										form.addField(h);
									}
									break;
								}
								if (erreur) {
									erreur = false;
								} else {
									final String name = label;
									ImageView image = new ImageView(c);
									image.setClickable(true);
									image.setImageDrawable(getResources()
											.getDrawable(R.drawable.basket));
									image.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											row.removeAllViews();
											form.deleteField(name);
										}
									});
									row.addView(image);
									view.setPadding(40, 30, 0, 0);
									row.addView(view);
									layoutDynamic.addView(row);
								}
							}
						});

				adb.setNegativeButton(getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
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
				Toast.makeText(getApplicationContext(),
						form.getName() + " " + form.getFieldsList().size(),
						Toast.LENGTH_LONG).show();
				for (Field f : form.getFieldsList()) {
					Log.d("TEST", f.getLabel());
				}
				finish();

			}
		});

	}

	// public int dipToPixel(int dip){
	// return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
	// getResources().getDisplayMetrics());
	// }
	//
	// public void refresh(Context c){
	// TextView v = new TextView(c);
	// v.setText("Nom");
	// EditText t = new EditText(c);
	// layoutDynamicAddField.addView(v);
	// layoutDynamicAddField.addView(t);
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_create_form, menu);
		return true;
	}

}
