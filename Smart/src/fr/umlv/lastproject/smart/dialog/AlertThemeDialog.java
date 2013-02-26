package fr.umlv.lastproject.smart.dialog;

import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class AlertThemeDialog extends AlertDialog.Builder {
	
	private String name;

	public AlertThemeDialog(final MenuActivity c, final Application app) {
		super(c);
		
		setCancelable(false);

		LayoutInflater factory = LayoutInflater.from(c);
		final View alertThemeView = factory.inflate(
				fr.umlv.lastproject.smart.R.layout.theme_dialog, null);

		setView(alertThemeView);
		setTitle(alertThemeView.getResources().getString(R.string.theme));

		RadioGroup group = (RadioGroup) alertThemeView.findViewById(R.id.groupTheme);
		
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
					case R.id.buttonTheme1:
						name="dark";
						break;
					case R.id.buttonTheme2:
						name="light";
						break;
				}
				

			}
		});
		
		
		final AlertDialog dialog = this
				.setPositiveButton(R.string.validate, new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent i = c.getPackageManager()
					             .getLaunchIntentForPackage( c.getPackageName() );
						i.putExtra("theme", name);
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						c.startActivity(i);
					}
				}).setNegativeButton(R.string.cancel, new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				}).create();

		dialog.show();
	}

}
