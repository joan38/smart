package fr.umlv.lastproject.smart.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.utils.SmartConstants;

/**
 * This dialog is used to prevent the user that the GPS has to be activated
 * 
 * @author Maelle Cabot
 * 
 */
public class AlertGPSTrackDialog extends AlertDialog.Builder {

	/**
	 * Constructor
	 * 
	 * @param menu
	 */
	public AlertGPSTrackDialog(final MenuActivity menu) {
		super(menu);
		setTitle(R.string.gpsCompulsory);
		setMessage(R.string.gpsTrackMessage);

		setNegativeButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		setPositiveButton(R.string.validate, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				dialog.dismiss();
				menu.startActivityForResult(intent, SmartConstants.GPS_ACTIVITY);

			}
		});
	}

}
