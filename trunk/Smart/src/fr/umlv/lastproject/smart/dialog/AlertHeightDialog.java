package fr.umlv.lastproject.smart.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import fr.umlv.lastproject.smart.HeightActivity;
import fr.umlv.lastproject.smart.R;

public class AlertHeightDialog extends AlertDialog.Builder {

	public AlertHeightDialog(final HeightActivity heightActivity) {
		super(heightActivity);
		setCancelable(false);

		final LayoutInflater factory = LayoutInflater.from(heightActivity);
		final View alertHeightView = factory.inflate(R.layout.alert_height,
				null);

		setView(alertHeightView);
		setTitle(R.string.height_title);

		setIcon(android.R.drawable.ic_dialog_alert);

		final TextView userHeight = (TextView) alertHeightView
				.findViewById(R.id.user_height_value);

		setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				final String height = userHeight.getText().toString();
				try {
					heightActivity.setHeight(Double.parseDouble(height));

				} catch (NumberFormatException e) {
					Toast.makeText(heightActivity, R.string.track_name_missing,
							Toast.LENGTH_LONG).show();
					heightActivity
							.finishWithResult(R.string.track_name_missing);
					return;
				}

			}
		});

	}

}
