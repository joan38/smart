package fr.umlv.lastproject.smart.dialog;

import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.database.DbManager;
import fr.umlv.lastproject.smart.form.FormEditedListener;
import fr.umlv.lastproject.smart.form.Mission;
import fr.umlv.lastproject.smart.layers.Geometry;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.utils.SmartException;

/**
 * This class is used to validate the deleting of a survey
 * 
 * @author Maellou
 *
 */
public class ValidationDeleteSurveyDialog extends AlertDialog.Builder {
	/**
	 * Constructor
	 * 
	 * @param c
	 * @param idMission
	 * @param nameMission
	 */

	public ValidationDeleteSurveyDialog(final MenuActivity c,
			final long idGeometry, final int idRowForm, final String title, final GeometryLayer l,
			final Geometry g, final List<FormEditedListener> listeners) {
		// TODO Auto-generated constructor stub
	

		super(c);
		setCancelable(false);

		final LayoutInflater inflater = LayoutInflater.from(c);
		final View exportMissionDialog = inflater.inflate(R.layout.areusure,
				null);

		TextView text = (TextView) exportMissionDialog.findViewById(R.id.areusure);
		text.setText(R.string.sureSurvey);
		setView(exportMissionDialog);
		setTitle(R.string.delete_survey);
		setIcon(android.R.drawable.ic_dialog_alert);

		setPositiveButton(R.string.validate, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				DbManager dbManager = new DbManager();
				try {
					dbManager.open(c);
					dbManager.deleteRecord(idGeometry, idRowForm,
							title);
					l.removeGeometry(g);
					Mission.getInstance().getMapView().invalidate();
				} catch (SmartException e) {
					Toast.makeText(c, e.getMessage(),
							Toast.LENGTH_LONG).show();
					Log.e("", e.getMessage());
				}
				dbManager.close();
				for (FormEditedListener l : listeners) {
					l.actionPerformed(g);
				}

			}
		});

		setNegativeButton(R.string.cancel, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				for (FormEditedListener l : listeners) {
					l.actionPerformed(g);
				}
			}
		});
	}

	
}
