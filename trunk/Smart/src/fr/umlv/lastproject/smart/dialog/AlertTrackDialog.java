package fr.umlv.lastproject.smart.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

	private boolean trackStarted = false;

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

		setPositiveButton(R.string.create_button,
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
							trackStarted = false;
							return;
						}
						final String name = oName.toString();
						final Object oParam = trackParameter.getText();
						if (oParam == null) {
							Toast.makeText(menu, R.string.track_param_missing,
									Toast.LENGTH_LONG).show();
							trackStarted = false;
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
							trackStarted = false;
							return;
						}

						trackStarted = true;
						Toast.makeText(menu, R.string.track_started,
								Toast.LENGTH_LONG).show();
					}
				});

		setNegativeButton(R.string.cancel_button,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				});
	}

	public boolean isTrackStarted() {
		return this.trackStarted;
	}

}
