package fr.umlv.lastproject.smart.dialog;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;

public class AlertMeasureResultDialog extends AlertDialog.Builder {

	public AlertMeasureResultDialog(MenuActivity menuActivity, double result) {
		super(menuActivity);

		final LayoutInflater factory = LayoutInflater.from(menuActivity);
		final View alertDialogView = factory.inflate(
				fr.umlv.lastproject.smart.R.layout.alert_measure_result,
				null);

		setView(alertDialogView);
		setTitle(R.string.result);
		TextView v = (TextView)  alertDialogView.findViewById(R.id.measureTextResult) ;
		v.setText(String.valueOf(result) + " m") ;

		setPositiveButton(R.string.validate, null);
	}

}
