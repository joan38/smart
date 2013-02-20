package fr.umlv.lastproject.smart.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.browser.utils.FileUtils;
import fr.umlv.lastproject.smart.utils.SmartConstants;

public class AlertCreateMissionDialog extends AlertDialog.Builder {

	private TextView formPath;

	public AlertCreateMissionDialog(final MenuActivity menu) {
		super(menu);
		setCancelable(false);

		final LayoutInflater inflater = LayoutInflater.from(menu);
		final View createMissionDialog = inflater.inflate(
				R.layout.create_mission_dialog, null);

		setView(createMissionDialog);
		setTitle(R.string.mission);

		final Button openBrowser = (Button) createMissionDialog
				.findViewById(R.id.selectFormButton);
		formPath = (TextView) createMissionDialog.findViewById(R.id.formPath);

		final TextView textViewMissionName = ((TextView) createMissionDialog
				.findViewById(R.id.missionNameValue));

		RadioGroup radioForm = (RadioGroup) createMissionDialog
				.findViewById(R.id.radioForm);

		radioForm.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (openBrowser.getVisibility() == View.GONE) {
					openBrowser.setVisibility(View.VISIBLE);
					formPath.setVisibility(View.VISIBLE);
				} else {
					openBrowser.setVisibility(View.GONE);
					formPath.setVisibility(View.GONE);
				}

			}
		});

		openBrowser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = FileUtils.createGetContentIntent(
						FileUtils.XML_TYPE,
						Environment.getExternalStorageDirectory() + "");
				menu.startActivityForResult(intent,
						SmartConstants.BROWSER_ACTIVITY);
			}
		});

		setPositiveButton(R.string.validate, new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				menu.startMission(textViewMissionName.getText().toString());
			}
		});

		setNegativeButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

			}
		});

	}

	public void setPathForm(String path) {
		formPath.setText(path);
	}

}
