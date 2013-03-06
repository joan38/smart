package fr.umlv.lastproject.smart.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import fr.umlv.lastproject.smart.Preferences;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.Theme;

public class AlertThemeDialog extends AlertDialog.Builder {

	public AlertThemeDialog(final Context c) {
		super(c);
		setCancelable(false);
		final Preferences pref = Preferences.getInstance();

		LayoutInflater factory = LayoutInflater.from(c);
		final View alertThemeView = factory
				.inflate(R.layout.theme_dialog, null);

		setView(alertThemeView);
		setTitle(c.getString(R.string.theme));

		RadioGroup themeRadioGroup = (RadioGroup) alertThemeView
				.findViewById(R.id.groupTheme);
		themeRadioGroup
				.check(Theme.getByThemeId(pref.theme).getRadioButtonId());
		themeRadioGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						pref.theme = Theme.getByRadioButtonId(checkedId)
								.getThemeId();
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
