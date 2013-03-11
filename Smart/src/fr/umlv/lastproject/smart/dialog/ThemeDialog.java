package fr.umlv.lastproject.smart.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import fr.umlv.lastproject.smart.Orientation;
import fr.umlv.lastproject.smart.Preferences;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.Theme;
import fr.umlv.lastproject.smart.utils.SmartParameters;

public class ThemeDialog extends AlertDialog.Builder {

	public ThemeDialog(final Context c) {
		super(c);
		setCancelable(false);
		final Preferences pref = Preferences.getInstance();
		final SmartParameters parameters = SmartParameters.getParameters();

		LayoutInflater factory = LayoutInflater.from(c);
		final View alertThemeView = factory
				.inflate(R.layout.theme_dialog, null);

		setView(alertThemeView);
		setTitle(c.getString(R.string.settings));

		RadioGroup themeRadioGroup = (RadioGroup) alertThemeView
				.findViewById(R.id.groupTheme);
		themeRadioGroup.check(Theme.getByThemeId(pref.getTheme())
				.getRadioButtonId());
		themeRadioGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						parameters.setApplicationTheme(Theme
								.getByRadioButtonId(checkedId).getThemeId());
					}
				});

		RadioGroup orientationRadioGroup = (RadioGroup) alertThemeView
				.findViewById(R.id.groupOrientation);
		orientationRadioGroup.check(Orientation.getByOrientationId(
				pref.getOrientation()).getRadioButtonId());
		orientationRadioGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						parameters.setScreenOrientation(Orientation
								.getByRadioButtonId(checkedId)
								.getOrientationId());
					}
				});

		final AlertDialog dialog = this
				.setPositiveButton(R.string.validate, new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						AlertDialog alertDialog = new AlertDialog.Builder(c)
								.create();
						alertDialog.setMessage(c
								.getString(R.string.shouldRestartApp));
						alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,
								c.getString(R.string.ok),
								(OnClickListener) null);
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
