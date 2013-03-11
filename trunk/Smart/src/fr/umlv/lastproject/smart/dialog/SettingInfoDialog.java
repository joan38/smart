package fr.umlv.lastproject.smart.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import fr.umlv.lastproject.smart.InfoOverlay;
import fr.umlv.lastproject.smart.R;

/**
 * This dialog allow to editing information zone. You can check or uncheck the
 * informations to display.
 * 
 * If all informations is unchecked, the information zone became invisible.
 * 
 * @author Fad's
 * 
 */
public class SettingInfoDialog extends AlertDialog.Builder {

	public SettingInfoDialog(Context menu, final View view,
			final InfoOverlay infoOverlay) {
		super(menu);

		setCancelable(false);

		LayoutInflater factory = LayoutInflater.from(menu);
		final View alertDialogView = factory.inflate(
				R.layout.informations_settings, null);

		setView(alertDialogView);
		setTitle(R.string.infoSettings);

		setCheckboxStates(alertDialogView, view);

		CheckBox latitude = (CheckBox) alertDialogView
				.findViewById(R.id.latitudeChkbx);
		CheckBox longitude = (CheckBox) alertDialogView
				.findViewById(R.id.longitudeChkbx);
		CheckBox altitude = (CheckBox) alertDialogView
				.findViewById(R.id.altitudeChkbx);
		CheckBox accuracy = (CheckBox) alertDialogView
				.findViewById(R.id.accuracyChkbx);
		CheckBox bearing = (CheckBox) alertDialogView
				.findViewById(R.id.bearingChkbx);
		CheckBox speed = (CheckBox) alertDialogView
				.findViewById(R.id.speedChkbx);

		latitude.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				infoOverlay.setVisibility(view.findViewById(R.id.latitude),
						view.findViewById(R.id.latitudeValue), isChecked);

			}
		});

		longitude.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				infoOverlay.setVisibility(view.findViewById(R.id.longitude),
						view.findViewById(R.id.longitudeValue), isChecked);
			}
		});

		altitude.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				infoOverlay.setVisibility(view.findViewById(R.id.altitude),
						view.findViewById(R.id.altitudeValue), isChecked);

			}
		});

		accuracy.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				infoOverlay.setVisibility(view.findViewById(R.id.precision),
						view.findViewById(R.id.precisionValue), isChecked);

			}
		});

		bearing.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				infoOverlay.setVisibility(view.findViewById(R.id.bearing),
						view.findViewById(R.id.bearingValue), isChecked);

			}
		});

		speed.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				infoOverlay.setVisibility(view.findViewById(R.id.speed),
						view.findViewById(R.id.speedValue), isChecked);

			}
		});

		setPositiveButton(R.string.validate, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		show();

	}

	/**
	 * This function set the state of checkbox based on informations visibility
	 * 
	 * @param checkboxView
	 *            : the view of checkbox
	 * @param infoView
	 *            : the view of informations zone
	 */
	public void setCheckboxStates(View checkboxView, View infoView) {
		((CheckBox) checkboxView.findViewById(R.id.latitudeChkbx))
				.setChecked(infoView.findViewById(R.id.latitude)
						.getVisibility() == View.VISIBLE);

		((CheckBox) checkboxView.findViewById(R.id.longitudeChkbx))
				.setChecked(infoView.findViewById(R.id.longitude)
						.getVisibility() == View.VISIBLE);

		((CheckBox) checkboxView.findViewById(R.id.altitudeChkbx))
				.setChecked(infoView.findViewById(R.id.altitude)
						.getVisibility() == View.VISIBLE);

		((CheckBox) checkboxView.findViewById(R.id.accuracyChkbx))
				.setChecked(infoView.findViewById(R.id.precision)
						.getVisibility() == View.VISIBLE);

		((CheckBox) checkboxView.findViewById(R.id.bearingChkbx))
				.setChecked(infoView.findViewById(R.id.bearing).getVisibility() == View.VISIBLE);

		((CheckBox) checkboxView.findViewById(R.id.speedChkbx))
				.setChecked(infoView.findViewById(R.id.speed).getVisibility() == View.VISIBLE);
	}
}
