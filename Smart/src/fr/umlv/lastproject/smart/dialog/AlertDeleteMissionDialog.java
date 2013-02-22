package fr.umlv.lastproject.smart.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.database.DbManager;
import fr.umlv.lastproject.smart.database.MissionRecord;
import fr.umlv.lastproject.smart.utils.SmartException;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AlertDeleteMissionDialog extends AlertDialog {

	public AlertDeleteMissionDialog(final Context c) {
		super(c);
		setCancelable(false);

		final LayoutInflater inflater = LayoutInflater.from(c);
		final View exportMissionDialog = inflater.inflate(
				R.layout.delete_mission_dialog, null);

		setView(exportMissionDialog);
		setTitle(R.string.delete_mission);

		final ListView listView = (ListView) exportMissionDialog.findViewById(R.id.listViewMission);

		final Map<String, Long> mapMissions = getAllMissions(c);
		List<String> titleMissions = new ArrayList<String>(mapMissions.keySet());

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(c,
				android.R.layout.simple_list_item_1,titleMissions);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {

				// Connect to export CSV
				// Id mission = position + 1
				String value = (String) adapter.getItemAtPosition(position);
				long idMission = mapMissions.get(value);

				Log.d("TEST", "id de la mission "+value+" "+idMission);

				dismiss();

				AlertValidationDialog alertValidationDialog = new AlertValidationDialog(c, idMission, value);
				alertValidationDialog.show();


			}
		});

		TextView cancel = (TextView) exportMissionDialog.findViewById(R.id.cancelDeleteMission);
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();				
			}
		});

	}



	private Map<String, Long> getAllMissions(Context c){
		DbManager dbm = new DbManager() ;
		try {
			dbm.open(c);
		} catch (SmartException e) {
			Toast.makeText(c, e.getMessage(), Toast.LENGTH_LONG).show();
			Log.e("", e.getMessage());
		}
		List<MissionRecord> missionRecords = dbm.getAllMissionsNoActives();
		dbm.close();

		Map<String, Long> mapMissions = new HashMap<String, Long>();
		for(MissionRecord m : missionRecords){
			mapMissions.put(m.getTitle(), m.getId());
		}
		return mapMissions;
	}

}