package fr.umlv.lastproject.smart.dialog;

import java.io.File;

import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.form.CreateFormActivity;
import fr.umlv.lastproject.smart.form.Form;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * This class is used to define the name of form to create
 * 
 * @author Maelle Cabot
 * 
 */
public class AlertCreateFormDialog extends AlertDialog.Builder {

	/**
	 * Constructor
	 * 
	 * @param menu
	 */
	public AlertCreateFormDialog(final MenuActivity menu) {
		super(menu);
		setCancelable(false);

		LayoutInflater factory = LayoutInflater.from(menu);
		final View alertDialogView = factory.inflate(
				fr.umlv.lastproject.smart.R.layout.name_form, null);

		setView(alertDialogView);
		setTitle(alertDialogView.getResources().getString(R.string.CreateForm));

		final EditText et = (EditText) alertDialogView
				.findViewById(R.id.nameForm);

		final AlertDialog dialog = setPositiveButton(R.string.validate, new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent intent = new Intent(menu,
								CreateFormActivity.class);
						intent.putExtra("form", new Form(et.getText().toString()));

						menu.startActivity(intent);
					}
				}).setNegativeButton(R.string.cancel, new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				}).create();

		dialog.show();

		et.addTextChangedListener(new TextWatcher() {

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
				File folder = new File(Environment
						.getExternalStorageDirectory()
						+ "/SMART/form/"
						+ et.getText().toString() + ".form");
				if (folder.exists()) {
					et.setError(menu.getResources().getString(R.string.invalid));
					dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
							false);
				} else {
					dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
							true);
				}
			}
		});
	}
}
