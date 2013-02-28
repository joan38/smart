package fr.umlv.lastproject.smart.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import fr.umlv.lastproject.smart.LayersActivity;
import fr.umlv.lastproject.smart.R;

public class AlertDeleteLayerDialog extends AlertDialog.Builder {

	public AlertDeleteLayerDialog(final LayersActivity activity, final int id,
			final boolean isRemovable) {
		super(activity);
		setTitle(R.string.delete);
		setIcon(android.R.drawable.ic_dialog_alert);

		if (isRemovable) {
			setMessage(R.string.deleteLayer);
			setNegativeButton(R.string.no,
					this.createListener(activity, Boolean.valueOf(false), id));
			setPositiveButton(R.string.yes,
					this.createListener(activity, Boolean.valueOf(true), id));
		} else {
			setMessage(R.string.deleteLayerFalse);
			setPositiveButton(R.string.ok,
					this.createListener(activity, Boolean.valueOf(false), id));
		}
	}

	private OnClickListener createListener(final LayersActivity activity,
			final boolean returnValue, final int id) {
		return new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				activity.removeLayer(id, returnValue);
			}
		};
	}
}
