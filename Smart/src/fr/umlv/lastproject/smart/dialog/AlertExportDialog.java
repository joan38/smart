package fr.umlv.lastproject.smart.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.data.CsvExportException;
import fr.umlv.lastproject.smart.data.DataExport;
import fr.umlv.lastproject.smart.data.KmlExportException;
import fr.umlv.lastproject.smart.database.DbManager;
import fr.umlv.lastproject.smart.database.MissionRecord;
import fr.umlv.lastproject.smart.utils.SmartException;

/**
 * This dialog is used to export a mission
 * 
 * @author Maelle Cabot
 * 
 */
public class AlertExportDialog extends AlertDialog.Builder {

	List<MissionRecord> missionRecords;

	/**
	 * Constructor
	 * 
	 * @param c
	 *            : the context
	 */
	public AlertExportDialog(final Context c) {
		super(c);
		setCancelable(false);

		final LayoutInflater inflater = LayoutInflater.from(c);
		final View exportMissionDialog = inflater.inflate(
				R.layout.export_mission_dialog, null);

		setView(exportMissionDialog);
		setTitle(R.string.export_mission);

		final RadioGroup formatSelector = (RadioGroup) exportMissionDialog
				.findViewById(R.id.formatChoice);

		final ListView listView = (ListView) exportMissionDialog
				.findViewById(R.id.listViewMission);

		getAllMissions(c);
		List<String> titleMissions = new ArrayList<String>();
		for (MissionRecord m : missionRecords) {
			titleMissions.add(m.getTitle());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(c,
				android.R.layout.simple_list_item_1, titleMissions);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				long idMission = -1;
				String value = (String) adapter.getItemAtPosition(position);
				for (MissionRecord m : missionRecords) {
					if (m.getTitle().equals(value))
						idMission = m.getId();
				}

				Log.d("RadioButtonIdExport", String.valueOf(formatSelector
						.getCheckedRadioButtonId()));

				try {
					switch (formatSelector.getCheckedRadioButtonId()) {
					case R.id.CsvExport:
						// Export CSV
						DataExport.exportCsv(Environment
								.getExternalStorageDirectory().getPath()
								+ "/SMART", idMission, c);
						Toast.makeText(c, R.string.csvExport, Toast.LENGTH_LONG)
								.show();
						break;

					case R.id.KmlExport:
						// Export KML
						DataExport.exportKml(Environment
								.getExternalStorageDirectory().getPath()
								+ "/SMART", idMission, c);
						Toast.makeText(c, R.string.kmlExport, Toast.LENGTH_LONG)
								.show();
						break;

					default:
						throw new IllegalStateException(
								"Id of the radiobutton unkown");
					}
				} catch (KmlExportException e) {
					Toast.makeText(c, R.string.kmlExportError,
							Toast.LENGTH_LONG).show();
				} catch (CsvExportException e) {
					Toast.makeText(c, R.string.csvExportError,
							Toast.LENGTH_LONG).show();
				}
			}
		});

		setNegativeButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void getAllMissions(Context c) {
		DbManager dbm = new DbManager();
		try {
			dbm.open(c);
		} catch (SmartException e) {
			Toast.makeText(c, e.getMessage(), Toast.LENGTH_LONG).show();
			Log.e("", e.getMessage());
		}
		missionRecords = dbm.getAllMissionsNoActives();
		dbm.close();

		// Map<String, Long> mapMissions = new HashMap<String, Long>();
		// for (MissionRecord m : missionRecords) {
		// mapMissions.put(m.getTitle(), m.getId());
		// }
	}

}
