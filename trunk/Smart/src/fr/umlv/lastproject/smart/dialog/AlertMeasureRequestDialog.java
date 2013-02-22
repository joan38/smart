package fr.umlv.lastproject.smart.dialog;

import java.util.concurrent.atomic.AtomicBoolean;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;

/**
 * 
 * This class is used to show an alert dialog to measure a distance 
 * beetween two points ( position and an other point or two others points )
 * 
 * @author thibault brun
 *
 */
public class AlertMeasureRequestDialog extends AlertDialog.Builder {

	/**
	 * Constructor 
	 * 
	 * @param menuActivity
	 */
	public AlertMeasureRequestDialog(final MenuActivity menuActivity) {
		super(menuActivity);
		setTitle(R.string.MeasureTitle) ;
		final AtomicBoolean absolute = new AtomicBoolean(false) ;
		
		final LayoutInflater factory = LayoutInflater.from(menuActivity);
		final View alertDialogView = factory.inflate(
				fr.umlv.lastproject.smart.R.layout.alert_measure_settings,
				null);

		setView(alertDialogView) ;
		
		
		final RadioGroup group= (RadioGroup) alertDialogView.findViewById(R.id.measureChoice) ;
		final RadioButton b1 = (RadioButton) alertDialogView.findViewById(R.id.measureRadioRelative) ;
		
		final TextView tv = (TextView) alertDialogView.findViewById(R.id.measureTextExplain) ;
		
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				if(b1.isChecked()) {
					tv.setText(R.string.MeasureRelativeText) ;
					absolute.set(false) ;
				}else{
					tv.setText(R.string.MeasureAbsoluteText) ;
					absolute.set(true) ;

				}
			}
		}) ;
		
		setPositiveButton(R.string.validate, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				menuActivity.measure(absolute.get());
			}
		});

	}

	
	
}
