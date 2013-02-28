package fr.umlv.lastproject.smart.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import fr.umlv.lastproject.smart.R;

/**
 * This dialog is used to display a help menu
 * 
 * @author Maelle Cabot
 *
 */
public class AlertHelpDialog extends AlertDialog.Builder {

	/**
	 * Constructor
	 * @param activity
	 * @param stringText : the message to display
	 */
	public AlertHelpDialog(final Activity activity, final int stringText) {
		super(activity);
		setTitle(R.string.help);
		setMessage(stringText);
		setPositiveButton(R.string.ok, null);

	}

}
