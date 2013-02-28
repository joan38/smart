package fr.umlv.lastproject.smart.dialog;

import android.app.AlertDialog;
import fr.umlv.lastproject.smart.LayersActivity;
import fr.umlv.lastproject.smart.R;

public class AlertLayerVisibilityDialog extends AlertDialog.Builder {

	public AlertLayerVisibilityDialog(final LayersActivity activity) {
		super(activity);
		setTitle(R.string.warning);
		setMessage(R.string.visibilityLayer);
		setPositiveButton(R.string.ok, null);
	}

}
