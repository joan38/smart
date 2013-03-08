package fr.umlv.lastproject.smart.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import fr.umlv.lastproject.smart.GPSTrack.TrackMode;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;

/**
 * This dialog is used to set up the track
 * 
 * @author Marc Barat
 * 
 */
public class AlertPolygonTrackDialog extends AlertDialog.Builder {

	/**
	 * Constructor
	 * 
	 * @param menu
	 *            the context
	 */
	public AlertPolygonTrackDialog(final MenuActivity menu) {
		super(menu);
		setCancelable(false);

		final LayoutInflater factory = LayoutInflater.from(menu);
		final View alertTrackView = factory.inflate(
				R.layout.alert_polygon_track, null);

		setView(alertTrackView);
		setTitle(R.string.polygon_track_title);

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
						final TrackMode trackMode = TrackMode
								.valueOf(radioButton.getTag().toString());
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
							menu.createPolygonTrack(trackMode);
						} catch (NumberFormatException e) {
							Toast.makeText(menu, R.string.track_param_false,
									Toast.LENGTH_LONG).show();
							return;
						}
					}
				}).setNegativeButton(R.string.cancel_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();

		dialog.show();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

		trackParameter.addTextChangedListener(new TextWatcher() {
			private boolean isValidate;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().equals("")) {
					isValidate = false;
				} else {
					isValidate = true;
				}
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
						isValidate);
			}
		});

	}
}
