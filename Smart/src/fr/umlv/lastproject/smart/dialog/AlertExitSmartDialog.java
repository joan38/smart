package fr.umlv.lastproject.smart.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;

public class AlertExitSmartDialog extends AlertDialog.Builder {

	public AlertExitSmartDialog(final MenuActivity menu) {
		super(menu);
		setTitle(R.string.exit);
		setMessage(R.string.exitMsg);
		setNegativeButton(R.string.no, null);
		setPositiveButton(R.string.yes, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				menu.finish();
			}
		});

	}

}
