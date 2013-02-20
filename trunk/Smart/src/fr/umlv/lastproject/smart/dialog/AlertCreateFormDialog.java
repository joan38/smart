package fr.umlv.lastproject.smart.dialog;

import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.form.CreateFormActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

		setPositiveButton("Valider",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {

						EditText et = (EditText) alertDialogView
								.findViewById(fr.umlv.lastproject.smart.R.id.nameForm);

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
