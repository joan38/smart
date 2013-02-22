package fr.umlv.lastproject.smart.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.data.DataExport;
import fr.umlv.lastproject.smart.data.KmlExportException;
import fr.umlv.lastproject.smart.database.DbManager;
import fr.umlv.lastproject.smart.database.MissionRecord;
import fr.umlv.lastproject.smart.utils.SmartException;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * This dialog is used to export a mission
 * 
 * @author Maelle Cabot
 *
 */
public class AlertExportDialog extends AlertDialog.Builder {

	/**
	 * Constructor
	 * @param c : the context
	 */
	public AlertExportDialog(final Context c) {
		super(c);
		setCancelable(false);

		final LayoutInflater inflater = LayoutInflater.from(c);
		final View exportMissionDialog = inflater.inflate(
				R.layout.export_mission_dialog, null);

		setView(exportMissionDialog);
		setTitle(R.string.export_mission);

		final RadioGroup formatSelector = (RadioGroup) exportMissionDialog.findViewById(R.id.formatChoice);
		
		final ListView listView = (ListView) exportMissionDialog
				.findViewById(R.id.listViewMission);

		final Map<String, Long> mapMissions = getAllMissions(c);
		List<String> titleMissions = new ArrayList<String>(mapMissions.keySet());

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(c,
				android.R.layout.simple_list_item_1, titleMissions);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				String value = (String) adapter.getItemAtPosition(position);
				long idMission = mapMissions.get(value);
				
				Log.d("RadioButtonIdExport", String.valueOf(formatSelector.getCheckedRadioButtonId()));

				try {
					switch (formatSelector.getCheckedRadioButtonId()) {
					case R.id.CsvExport:
						// Export CSV
						DataExport.exportCsv(Environment
								.getExternalStorageDirectory().getPath() + "/SMART",
								idMission);
						break;

					case R.id.KmlExport:
						// Export KML
						DataExport.exportKml(Environment
								.getExternalStorageDirectory().getPath() + "/SMART",
								idMission, c);
						break;

					default:
						throw new IllegalStateException("Id of the radiobutton unkown");
					}
				} catch (KmlExportException e) {
				} catch (SmartException e) {
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

	private static Map<String, Long> getAllMissions(Context c) {
		DbManager dbm = new DbManager();
		try {
			dbm.open(c);
		} catch (SmartException e) {
			Toast.makeText(c, e.getMessage(), Toast.LENGTH_LONG).show();
			Log.e("", e.getMessage());
		}
		List<MissionRecord> missionRecords = dbm.getAllMissions();
		dbm.close();

		Map<String, Long> mapMissions = new HashMap<String, Long>();
		for (MissionRecord m : missionRecords) {
			mapMissions.put(m.getTitle(), m.getId());
		}
		return mapMissions;
	}

}
