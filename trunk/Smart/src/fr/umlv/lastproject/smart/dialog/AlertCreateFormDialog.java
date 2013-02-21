package fr.umlv.lastproject.smart.dialog;

import java.io.File;

import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.form.CreateFormActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AlertCreateFormDialog extends AlertDialog.Builder{

	public AlertCreateFormDialog(final MenuActivity menu) {
		super(menu);
		setCancelable(false);
		
		LayoutInflater factory = LayoutInflater.from(menu);
		final View alertDialogView = factory.inflate(
				fr.umlv.lastproject.smart.R.layout.name_form, null);
		

		setView(alertDialogView);
		setTitle(alertDialogView.getResources().getString(R.string.CreateForm));
		
		final EditText et = (EditText) alertDialogView
				.findViewById(fr.umlv.lastproject.smart.R.id.nameForm);
		
		et.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				File folder = new File(Environment.getExternalStorageDirectory()
						+ "/SMART/form/"+et.getText().toString()+".xml");
				if (folder.exists()) {
					et.setError(menu.getResources().getString(R.string.invalid));
				}
				
			}
		});
		
		setPositiveButton("Valider",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {

						
						Intent intent = new Intent(
							menu,
								CreateFormActivity.class);
						intent.putExtra("nameForm", et.getText()
								.toString());

						menu.startActivity(intent);

					}
				});

		setNegativeButton("Annuler",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {

					}
				});
	}

}
