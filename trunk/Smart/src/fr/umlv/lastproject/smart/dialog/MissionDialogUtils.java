package fr.umlv.lastproject.smart.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import fr.umlv.lastproject.smart.ListOverlay;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.browser.utils.FileUtils;
import fr.umlv.lastproject.smart.data.CsvExportException;
import fr.umlv.lastproject.smart.data.DataExport;
import fr.umlv.lastproject.smart.data.KmlExportException;
import fr.umlv.lastproject.smart.database.DbManager;
import fr.umlv.lastproject.smart.database.MissionRecord;
import fr.umlv.lastproject.smart.layers.GeometryType;
import fr.umlv.lastproject.smart.utils.SmartConstants;
import fr.umlv.lastproject.smart.utils.SmartException;

/**
 * This dialog is used to export a mission
 * 
 * @author Maelle Cabot
 * 
 */
public final class MissionDialogUtils {

	private MissionDialogUtils() {
	}

	public static AlertDialog showCreateDialog(final MenuActivity activity,
			final ListOverlay overlays) {
		AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(activity);
		dialogbuilder.setCancelable(false);

		final LayoutInflater inflater = LayoutInflater.from(activity);
		final View createMissionDialog = inflater.inflate(
				R.layout.create_mission_dialog, null);

		dialogbuilder.setView(createMissionDialog);
		dialogbuilder.setTitle(R.string.mission);

		final Button openBrowser = (Button) createMissionDialog
				.findViewById(R.id.selectFormButton);
		final TextView textViewMissionName = ((TextView) createMissionDialog
				.findViewById(R.id.missionNameValue));

		final AlertDialog dialog = dialogbuilder
				.setPositiveButton(R.string.validate, new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						activity.startMission(textViewMissionName.getText()
								.toString());

						Toast.makeText(activity, R.string.missionStart,
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
			public void afterTextChanged(Editable s) {
				validMissionName(s, dialog, activity, overlays,
						textViewMissionName);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
		});

		RadioGroup radioForm = (RadioGroup) createMissionDialog
				.findViewById(R.id.radioForm);

		radioForm.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (openBrowser.getVisibility() == View.GONE) {
					openBrowser.setVisibility(View.VISIBLE);
				} else {
					openBrowser.setVisibility(View.GONE);
				}
			}
		});

		openBrowser.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = FileUtils.createGetContentIntent(
						FileUtils.FORM_TYPE, SmartConstants.FORM_PATH);
				activity.startActivityForResult(intent,
						SmartConstants.MISSION_BROWSER_ACTIVITY);
			}
		});

		return dialog;
	}

	private static void validMissionName(Editable s, AlertDialog dialog,
			MenuActivity activity, ListOverlay overlays,
			TextView textViewMissionName) {
		if (s.toString().equals("")) {
			dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
		} else {
			if (s.toString().equals(
					activity.getResources().getString(R.string.cheatcode1))) {
				final AboutDialog about = new AboutDialog(activity);
				about.show();
			}
			DbManager dbManager = new DbManager();
			try {
				dbManager.open(activity);
			} catch (SmartException e) {
				Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG)
						.show();
				Log.e("", e.getMessage());
			}

			if (dbManager.existsMission(textViewMissionName.getText()
					.toString())
					|| ((overlays.search(textViewMissionName.getText()
							.toString() + "_POLYGON") != null)
							&& (overlays.search(textViewMissionName.getText()
									.toString() + "_LINE") != null) && (overlays
							.search(textViewMissionName.getText().toString()
									+ "_POINT") != null))) {
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
				textViewMissionName.setError(activity.getResources().getString(
						R.string.invalid));
			} else {
				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
				textViewMissionName.setError(null);
			}

			dbManager.close();
		}
	}

	/**
	 * Build and show the alert export dialog.
	 * 
	 * @param context
	 * @throws SmartException
	 */
	public static void showExportDialog(final Context context)
			throws SmartException {
		AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(context);
		dialogbuilder.setCancelable(false);
		dialogbuilder.setTitle(R.string.export_mission);

		final LayoutInflater inflater = LayoutInflater.from(context);
		final View exportMissionDialog = inflater.inflate(
				R.layout.export_mission_dialog, null);

		dialogbuilder.setView(exportMissionDialog);

		final RadioGroup formatSelector = (RadioGroup) exportMissionDialog
				.findViewById(R.id.formatChoice);
		final CheckBox checkBoxEmail = (CheckBox) exportMissionDialog
				.findViewById(R.id.emailExportCheckBox);
		LinearLayout checkBoxGroup = (LinearLayout) exportMissionDialog
				.findViewById(R.id.linearMissionExport);
		final List<Long> missionsToExport = new ArrayList<Long>();

		dialogbuilder.setPositiveButton(R.string.validate,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						exportMission(missionsToExport, context,
								formatSelector, checkBoxEmail);
					}
				});

		dialogbuilder.setNegativeButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		final AlertDialog alertDialog = dialogbuilder.create();
		alertDialog.show();
		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

		List<MissionRecord> missionRecords = getAllMissions(context);

		if (missionRecords.size() == 0) {
			TextView noMissionText = new TextView(context);
			noMissionText.setText(R.string.noMissionAvailable);
			checkBoxGroup.addView(noMissionText);
		}

		for (MissionRecord m : missionRecords) {
			CheckBox checkBox = new CheckBox(context);
			checkBox.setText(m.getTitle());
			checkBox.setHint(String.valueOf(m.getId()));
			checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton checkBox,
						boolean isChecked) {
					if (isChecked) {
						missionsToExport.add(Long.valueOf(String
								.valueOf(checkBox.getHint())));
						if (missionsToExport.size() > 0) {
							alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
									.setEnabled(true);
						}
					} else {
						missionsToExport.remove(Long.valueOf(String
								.valueOf(checkBox.getHint())));
						if (missionsToExport.size() == 0) {
							alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
									.setEnabled(false);
						}
					}
				}
			});

			checkBoxGroup.addView(checkBox);
		}
	}

	private static void exportMission(List<Long> missionsToExport,
			Context context, RadioGroup formatSelector, CheckBox checkBoxEmail) {
		if (missionsToExport.size() == 0) {
			Toast.makeText(context,
					context.getString(R.string.pleaseSelectMission),
					Toast.LENGTH_LONG).show();
			return;
		}

		ArrayList<Uri> files = new ArrayList<Uri>();
		for (Long idMission : missionsToExport) {
			try {
				switch (formatSelector.getCheckedRadioButtonId()) {
				case R.id.csvExport:
					// Export CSV

					// files.add(Uri.fromFile(new File()));
					String path = DataExport.exportCsv(SmartConstants.APP_PATH,
							idMission, context);
					for (GeometryType t : GeometryType.values()) {
						files.add(Uri.fromFile(new File(path + t.toString()
								+ FileUtils.CSV_TYPE[0])));
					}
					break;

				case R.id.kmlExport:
					// Export KML
					files.add(Uri.fromFile(new File(DataExport.exportKml(
							SmartConstants.APP_PATH, idMission, context))));
					break;

				default:
					throw new IllegalStateException(
							"Id of the radiobutton unkown");
				}
			} catch (KmlExportException e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
						.show();
				return;
			} catch (CsvExportException e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG)
						.show();
				return;
			}
		}

		Toast.makeText(context, context.getString(R.string.missionExported)+" : "+SmartConstants.APP_PATH, Toast.LENGTH_LONG)
				.show();

		if (checkBoxEmail.isChecked()) {
			Intent intent = FileUtils.createEmailIntent(files);
			context.startActivity(intent);
		}
	}

	public static void showDeleteDialog(final Context context)
			throws SmartException {
		AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(context);
		dialogbuilder.setCancelable(false);
		dialogbuilder.setTitle(R.string.delete_mission);

		final LayoutInflater inflater = LayoutInflater.from(context);
		final View deleteMissionDialog = inflater.inflate(
				R.layout.delete_mission_dialog, null);

		dialogbuilder.setView(deleteMissionDialog);

		LinearLayout checkBoxGroup = (LinearLayout) deleteMissionDialog
				.findViewById(R.id.linearMissionDelete);
		final List<Long> missionsToDelete = new ArrayList<Long>();

		dialogbuilder.setPositiveButton(R.string.validate,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ValidationDeleteMissionDialog alertValidationDialog = new ValidationDeleteMissionDialog(
								context, missionsToDelete);
						alertValidationDialog.show();
					}
				});

		dialogbuilder.setNegativeButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		final AlertDialog alertDialog = dialogbuilder.create();
		alertDialog.show();
		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

		List<MissionRecord> missionRecords = getAllMissions(context);

		if (missionRecords.size() == 0) {
			TextView noMissionText = new TextView(context);
			noMissionText.setText(R.string.noMissionAvailable);
			checkBoxGroup.addView(noMissionText);
		}

		for (MissionRecord m : missionRecords) {
			CheckBox checkBox = new CheckBox(context);
			checkBox.setText(m.getTitle());
			checkBox.setHint(String.valueOf(m.getId()));
			checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton checkBox,
						boolean isChecked) {
					if (isChecked) {
						missionsToDelete.add(Long.valueOf(String
								.valueOf(checkBox.getHint())));
						if (missionsToDelete.size() > 0) {
							alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
									.setEnabled(true);
						}
					} else {
						missionsToDelete.remove(Long.valueOf(String
								.valueOf(checkBox.getHint())));
						if (missionsToDelete.size() == 0) {
							alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
									.setEnabled(false);
						}
					}
				}
			});
			checkBoxGroup.addView(checkBox);
		}
	}

	private static List<MissionRecord> getAllMissions(Context c)
			throws SmartException {
		DbManager dbm = new DbManager();
		dbm.open(c);

		try {
			return dbm.getAllMissionsNoActives();
		} finally {
			dbm.close();
		}
	}
}
