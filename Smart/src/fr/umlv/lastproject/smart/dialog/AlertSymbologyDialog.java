package fr.umlv.lastproject.smart.dialog;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import fr.umlv.lastproject.smart.LayerItem;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.utils.SmartConstants;

public class AlertSymbologyDialog extends AlertDialog.Builder {

	public AlertSymbologyDialog(final MenuActivity menu,
			final GeometryLayer layer, final LayerItem layerItem) {
		super(menu);
		setCancelable(false);

		final LayoutInflater inflater = LayoutInflater.from(menu);
		final View symbologyDialog = inflater.inflate(R.layout.alert_symbology,
				null);

		setView(symbologyDialog);
		setTitle(R.string.symbo);

		final Spinner spinner = (Spinner) symbologyDialog
				.findViewById(R.id.colorSpinner);
		spinner.setMinimumWidth(200);
		List<? extends Map<String, ?>> colors = new LinkedList<Map<String, ?>>();
		for (int i = 0; i < SmartConstants.colors.length; i++) {
			colors.add(null);
		}

		ColorAdapter adapter = new ColorAdapter(menu, colors,
				android.R.layout.simple_list_item_1, null, null);
		spinner.setAdapter(adapter);

		final Spinner tailleSpinner = (Spinner) symbologyDialog
				.findViewById(R.id.tailleSpinner);
		final List<Integer> tailles = new LinkedList<Integer>();
		for (int i = 1; i <= 20; i++) {
			tailles.add(i);
		}
		ArrayAdapter<Integer> tailleAdapter = new ArrayAdapter<Integer>(menu,
				android.R.layout.simple_list_item_1, tailles);
		tailleSpinner.setAdapter(tailleAdapter);
		
		tailleSpinner.setSelection(layer.getSymbology().getSize()-1) ;
		for(int i=0 ; i < SmartConstants.colors.length ; i++){
			int a = SmartConstants.colors[i] ;
				if(a == layer.getSymbology().getColor()){
				spinner.setSelection(i) ;
				break ;
			}
		}


		
		
		final AlertDialog dialog = this
				.setPositiveButton(R.string.validate, new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						int positionColor = spinner.getSelectedItemPosition();
						Integer taille = tailles.get(tailleSpinner
								.getSelectedItemPosition());

						layer.editSymbology(taille,
								SmartConstants.colors[positionColor]);
						layerItem.setOverview(layer.getOverview());
						menu.getMapView().invalidate();

					}
				}).setNegativeButton(R.string.cancel, new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

					}
				}).create();

		dialog.show();

	}
}
