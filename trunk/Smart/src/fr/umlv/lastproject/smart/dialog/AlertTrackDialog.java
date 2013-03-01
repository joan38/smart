package fr.umlv.lastproject.smart.dialog;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import fr.umlv.lastproject.smart.GPSTrack.TRACK_MODE;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;

/**
 * This dialog is used to set up the track
 * 
 * @author Marc Barat
 * 
 */
public class AlertTrackDialog extends AlertDialog.Builder {

	/**
	 * Constructor
	 * 
	 * @param menu
	 *            the context
	 */
	public AlertTrackDialog(final MenuActivity menu) {
		super(menu);
		setCancelable(false);

		final LayoutInflater factory = LayoutInflater.from(menu);
		final View alertTrackView = factory.inflate(R.layout.alert_track, null);

		setView(alertTrackView);
		setTitle(R.string.track_title);

		final EditText trackName = (EditText) alertTrackView
				.findViewById(R.id.trackname);

		final EditText trackParameter = (EditText) alertTrackView
				.findViewById(R.id.trackparameter);
		final RadioGroup radioGroup = (RadioGroup) alertTrackView
				.findViewById(R.id.trackradiogroup);

		final AlertDialog dialog = setPositiveButton(R.string.create_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						final RadioButton radioButton = (RadioButton) alertTrackView
								.findViewById(radioGroup
										.getCheckedRadioButtonId());
						final TRACK_MODE trackMode = TRACK_MODE
								.valueOf(radioButton.getTag().toString());
						final Object oName = trackName.getText();
						if (oName == null) {
							Toast.makeText(menu, R.string.track_name_missing,
									Toast.LENGTH_LONG).show();
							return;
						}
						final String name = oName.toString();
						final Object oParam = trackParameter.getText();
						if (oParam == null) {
							Toast.makeText(menu, R.string.track_param_missing,
									Toast.LENGTH_LONG).show();
							return;
						}
						try {

							final int param = Integer.parseInt(oParam
									.toString());
							trackMode.setParameter(param);
							menu.createGPSTrack(name, trackMode);

						} catch (NumberFormatException e) {
							Toast.makeText(menu, R.string.track_param_false,
									Toast.LENGTH_LONG).show();
							return;
						}

						Toast.makeText(menu, R.string.track_started,
								Toast.LENGTH_LONG).show();
					}
				}).setNegativeButton(R.string.cancel_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				}).create();

		dialog.show();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

		trackName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				File folder = new File(Environment
						.getExternalStorageDirectory()
						+ "/SMART/tracks/"
						+ trackName.getText().toString() + ".gpx");

				boolean isValidate;

				if (folder.exists()) {
					trackName.setError(menu.getResources().getString(
							R.string.invalid));
					isValidate = false;
				} else {
					if (trackParameter.getText().toString().equals("")
							|| s.toString().equals("")) {
						isValidate = false;
					} else {
						isValidate = true;
					}
				}

				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
						isValidate);
			}
		});

		trackParameter.addTextChangedListener(new TextWatcher() {

			boolean isValidate;

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
				if (s.toString().equals("")) {
					isValidate = false;
				} else {
					if (trackName.getText().toString().equals("")) {
						isValidate = false;
					} else {
						isValidate = true;
					}
				}

				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
						isValidate);
			}
		});

	}
}
