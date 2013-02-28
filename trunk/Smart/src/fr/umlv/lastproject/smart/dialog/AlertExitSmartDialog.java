package fr.umlv.lastproject.smart.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;

/**
 * This class is used to ask at the user if he really wants to exit the
 * application
 * 
 * @author Maelle Cabot
 * 
 */
public class AlertExitSmartDialog extends AlertDialog.Builder {

	/**
	 * Constructor
	 * 
	 * @param menu
	 */
	public AlertExitSmartDialog(final MenuActivity menu) {
		super(menu);
		setTitle(R.string.exit);
		setMessage(R.string.exitMsg);
		setIcon(android.R.drawable.ic_dialog_alert);
		setNegativeButton(R.string.no, null);
		setPositiveButton(R.string.yes, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				menu.finish();
			}
		});

	}

}
