package fr.umlv.lastproject.smart.dialog;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.database.DbManager;
import fr.umlv.lastproject.smart.utils.SmartException;

/**
 * This class is used to validate the deleting of a mission
 * 
 * @author Maelle Cabot
 * 
 */
public class AlertValidationDialog extends AlertDialog.Builder {

	/**
	 * Constructor
	 * 
	 * @param c
	 * @param idMission
	 * @param nameMission
	 */

	public AlertValidationDialog(final Context c,
			final List<Long> missionsToDelete) {

		super(c);
		setCancelable(false);

		final LayoutInflater inflater = LayoutInflater.from(c);
		final View exportMissionDialog = inflater.inflate(R.layout.areusure,
				null);

		setView(exportMissionDialog);
		setTitle(R.string.delete_mission);

		setPositiveButton(R.string.validate, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				DbManager dbm = new DbManager();
				try {
					dbm.open(c);
				} catch (SmartException e) {
					Toast.makeText(c, e.getMessage(), Toast.LENGTH_LONG).show();
					Log.e("", e.getMessage());
				}
				for (Long l : missionsToDelete) {
					dbm.deleteMission(l);
				}
				dbm.close();
				Toast.makeText(c, R.string.missionDelete, Toast.LENGTH_SHORT)
						.show();
			}
		});

		setNegativeButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
	}

}
