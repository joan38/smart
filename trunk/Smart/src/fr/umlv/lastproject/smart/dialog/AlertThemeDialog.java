package fr.umlv.lastproject.smart.dialog;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import fr.umlv.lastproject.smart.Preferences;
import fr.umlv.lastproject.smart.R;

public class AlertThemeDialog extends AlertDialog.Builder {

	private int theme = R.style.AppBaseTheme;

	public AlertThemeDialog(final Context c, final Application app) {
		super(c);
		setCancelable(false);
		final Preferences preferences = new Preferences(c);

		LayoutInflater factory = LayoutInflater.from(c);
		final View alertThemeView = factory.inflate(
				fr.umlv.lastproject.smart.R.layout.theme_dialog, null);

		setView(alertThemeView);
		setTitle(alertThemeView.getResources().getString(R.string.theme));

		RadioGroup themeRadioGroup = (RadioGroup) alertThemeView
				.findViewById(R.id.groupTheme);
		
		themeRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.buttonTheme1:
					// Light
					theme = R.style.AppLightTheme;
					break;
					
				case R.id.buttonTheme2:
					// Dark
				default:
					// Dark
					theme = R.style.AppLightTheme;
				}
			}
		});

		final AlertDialog dialog = this
				.setPositiveButton(R.string.validate, new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						preferences.theme = theme;
						AlertDialog alertDialog = new AlertDialog.Builder(c).create();
						//alertDialog.setTitle("");
						alertDialog.setMessage(c.getString(R.string.shouldRestartApp));
						alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, c.getString(R.string.ok), (OnClickListener) null);
						alertDialog.show();
					}
				}).setNegativeButton(R.string.cancel, new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				}).create();

		dialog.show();
	}
}
