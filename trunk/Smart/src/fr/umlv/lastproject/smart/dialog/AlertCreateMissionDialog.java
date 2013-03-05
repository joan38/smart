package fr.umlv.lastproject.smart.dialog;


import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import fr.umlv.lastproject.smart.ListOverlay;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.browser.utils.FileUtils;
import fr.umlv.lastproject.smart.database.DbManager;
import fr.umlv.lastproject.smart.utils.SmartConstants;
import fr.umlv.lastproject.smart.utils.SmartException;
import fr.umlv.lastproject.smart.utils.SmartLogger;

/**
 * This class is used to create a new mission and define its name and the form
 * associated
 * 
 * @author Maelle Cabot
 * 
 */
public class AlertCreateMissionDialog extends AlertDialog.Builder {

	private TextView formPath;
	final Logger logger = SmartLogger.getLocator().getLogger();


	/**
	 * Constructor
	 * 
	 * @param menu
	 */
	public AlertCreateMissionDialog(final MenuActivity menu,
			final ListOverlay overlays) {
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

		final AlertDialog dialog = this
				.setPositiveButton(R.string.validate, new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {			

						menu.startMission(textViewMissionName.getText()
								.toString());
						logger.log(Level.INFO, "Mission "+textViewMissionName.getText()
								.toString()+" started");
						Toast.makeText(menu, R.string.missionStart,
								Toast.LENGTH_SHORT).show();
					}
				}).setNegativeButton(R.string.cancel, new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				}).create();

		dialog.show();
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

		textViewMissionName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {

				if (s.toString().equals("")) {
					dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
							false);
				} else {
					DbManager dbManager = new DbManager();
					try {
						dbManager.open(menu);
					} catch (SmartException e) {
						Toast.makeText(menu, e.getMessage(), Toast.LENGTH_LONG)
								.show();
						Log.e("", e.getMessage());
					}

					if (dbManager.existsMission(textViewMissionName.getText()
							.toString())
							|| ((overlays.search(textViewMissionName.getText()
									.toString() + "_POLYGON") != null)
									&& (overlays.search(textViewMissionName
											.getText().toString() + "_LINE") != null) && (overlays
									.search(textViewMissionName.getText()
											.toString() + "_POINT") != null))) {
						dialog.getButton(AlertDialog.BUTTON_POSITIVE)
								.setEnabled(false);
						textViewMissionName.setError(menu.getResources()
								.getString(R.string.invalid));
					} else {
						dialog.getButton(AlertDialog.BUTTON_POSITIVE)
								.setEnabled(true);
						textViewMissionName.setError(null);
					}

					dbManager.close();

				}

			}
		});

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
						FileUtils.FORM_TYPE, SmartConstants.APP_PATH);
				menu.startActivityForResult(intent,
						SmartConstants.MISSION_BROWSER_ACTIVITY);
			}
		});

	}

	/**
	 * Set the path of form in the textview
	 * 
	 * @param path
	 */
	public void setPathForm(String path) {
		formPath.setText(path);
	}

}
