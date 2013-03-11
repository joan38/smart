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
import fr.umlv.lastproject.smart.InfosOverlay;
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

	public SettingInfoDialog(Context menu, final InfosOverlay infoOverlay) {
		super(menu);
		setCancelable(false);

		LayoutInflater factory = LayoutInflater.from(menu);
		final View alertDialogView = factory.inflate(
				R.layout.informations_settings, null);

		setView(alertDialogView);
		setTitle(R.string.infoSettings);

		setCheckboxStates(alertDialogView, infoOverlay);

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
				infoOverlay.setLatitudeVisibility(isChecked);
			}
		});

		longitude.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				infoOverlay.setLongitudeVisibility(isChecked);
			}
		});

		altitude.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				infoOverlay.setAltitudeVisibility(isChecked);
			}
		});

		accuracy.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				infoOverlay.setAccuracyVisibility(isChecked);
			}
		});

		bearing.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				infoOverlay.setBearingVisibility(isChecked);
			}
		});

		speed.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				infoOverlay.setSpeedVisibility(isChecked);
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
	private void setCheckboxStates(View checkboxView, InfosOverlay infoOverlay) {
		((CheckBox) checkboxView.findViewById(R.id.latitudeChkbx))
				.setChecked(infoOverlay.isLatitudeVisible());

		((CheckBox) checkboxView.findViewById(R.id.longitudeChkbx))
				.setChecked(infoOverlay.isLongitudeVisible());

		((CheckBox) checkboxView.findViewById(R.id.altitudeChkbx))
				.setChecked(infoOverlay.isAltitudeVisible());

		((CheckBox) checkboxView.findViewById(R.id.accuracyChkbx))
				.setChecked(infoOverlay.isAccuracyVisible());

		((CheckBox) checkboxView.findViewById(R.id.bearingChkbx))
				.setChecked(infoOverlay.isBearingVisible());

		((CheckBox) checkboxView.findViewById(R.id.speedChkbx))
				.setChecked(infoOverlay.isSpeedVisible());
	}
}
