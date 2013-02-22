package fr.umlv.lastproject.smart.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import fr.umlv.lastproject.smart.R;

public class AlertHelpDialog extends AlertDialog.Builder {

	public AlertHelpDialog(final Activity activity, final int stringText) {
		super(activity);
		setTitle(R.string.help);
		setMessage(stringText);
		setPositiveButton(R.string.ok, null);

	}

}
