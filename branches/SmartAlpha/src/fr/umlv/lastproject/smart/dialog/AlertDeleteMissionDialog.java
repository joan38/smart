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
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * This class is used to list the mission could be delete
 * 
 * @author Maelle Cabot
 *
 */
public class AlertDeleteMissionDialog extends AlertDialog.Builder {
	
	private List<MissionRecord> missionRecords;

	/**
	 * Constructor 
	 * 
	 * @param c
	 */
	public AlertDeleteMissionDialog(final Context c) {
		super(c);
		setCancelable(false);
		
		final LayoutInflater inflater = LayoutInflater.from(c);
		final View deleteMissionDialog = inflater.inflate(
				R.layout.delete_mission_dialog, null);
		LinearLayout layout = (LinearLayout) deleteMissionDialog.findViewById(R.id.linearDelete);

		setView(deleteMissionDialog);
		setTitle(R.string.delete_mission);


		getAllMissions(c);
		List<String> titleMissions = new ArrayList<String>();
		for(MissionRecord m : missionRecords){
			titleMissions.add(m.getTitle());
		}
		final List<Long> missionsToDelete = new ArrayList<Long>();
		
		for(final String s : titleMissions){
			CheckBox checkBox = new CheckBox(c);
			checkBox.setText(s);
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						long idMission = -1 ;
						for(MissionRecord m : missionRecords){
							if(m.getTitle().equals(s)){
								idMission = m.getId() ;
							}
						}
						missionsToDelete.add(idMission);
					}
					
				}
			});
			layout.addView(checkBox);
		}
		
		
		
		setPositiveButton(R.string.validate, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AlertValidationDialog alertValidationDialog = new AlertValidationDialog(c, missionsToDelete);
				alertValidationDialog.show();
				
			}
		});
		setNegativeButton(R.string.cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		

		
	}


	/**
	 * Create a map of all no active missions with their ids
	 * @param c
	 * @return a map of missions with their id
	 */
	private void  getAllMissions(Context c){
		DbManager dbm = new DbManager() ;
		try {
			dbm.open(c);
		} catch (SmartException e) {
			Toast.makeText(c, e.getMessage(), Toast.LENGTH_LONG).show();
			Log.e("", e.getMessage());
		}
		missionRecords = dbm.getAllMissionsNoActives();
		dbm.close();

	}

}