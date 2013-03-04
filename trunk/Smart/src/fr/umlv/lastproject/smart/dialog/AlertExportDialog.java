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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.browser.utils.FileUtils;
import fr.umlv.lastproject.smart.data.CsvExportException;
import fr.umlv.lastproject.smart.data.DataExport;
import fr.umlv.lastproject.smart.data.KmlExportException;
import fr.umlv.lastproject.smart.database.DbManager;
import fr.umlv.lastproject.smart.database.MissionRecord;
import fr.umlv.lastproject.smart.utils.SmartConstants;
import fr.umlv.lastproject.smart.utils.SmartException;

/**
 * This dialog is used to export a mission
 * 
 * @author Maelle Cabot
 * 
 */
public final class AlertExportDialog {

	private AlertExportDialog() {
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
						if (missionsToExport.size() == 0) {
							Toast.makeText(
									context,
									context.getString(R.string.pleaseSelectMission),
									Toast.LENGTH_LONG).show();
							return;
						}

						ArrayList<Uri> files = new ArrayList<Uri>();
						for (Long idMission : missionsToExport) {
							try {
								switch (formatSelector
										.getCheckedRadioButtonId()) {
								case R.id.csvExport:
									// Export CSV
									files.add(Uri.fromFile(new File(DataExport
											.exportCsv(SmartConstants.APP_PATH,
													idMission, context))));
									break;

								case R.id.kmlExport:
									// Export KML
									files.add(Uri.fromFile(new File(DataExport
											.exportKml(SmartConstants.APP_PATH,
													idMission, context))));
									break;

								default:
									throw new IllegalStateException(
											"Id of the radiobutton unkown");
								}
							} catch (KmlExportException e) {
								Toast.makeText(context, e.getMessage(),
										Toast.LENGTH_LONG).show();
								return;
							} catch (CsvExportException e) {
								Toast.makeText(context, e.getMessage(),
										Toast.LENGTH_LONG).show();
								return;
							}
						}

						Toast.makeText(context, R.string.missionExported,
								Toast.LENGTH_LONG).show();

						if (checkBoxEmail.isChecked()) {
							Intent intent = FileUtils.createEmailIntent(files);
							context.startActivity(intent);
						}
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
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

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
						AlertValidationDialog alertValidationDialog = new AlertValidationDialog(
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
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

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
