package fr.umlv.lastproject.smart.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;

public class AlertGPSSettingDialog extends AlertDialog.Builder {

	public AlertGPSSettingDialog(final MenuActivity menu) {
		super(menu);
		setTitle(R.string.gpsSettings);
		setMessage(R.string.gpsMessage);

		setNegativeButton(R.string.cancel, null);

		setPositiveButton(R.string.validate, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				menu.startActivity(intent);

			}
		});
	}

}
