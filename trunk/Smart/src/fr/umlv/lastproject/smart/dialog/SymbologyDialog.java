package fr.umlv.lastproject.smart.dialog;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import fr.umlv.lastproject.smart.LayerItem;
import fr.umlv.lastproject.smart.MenuActivity;
import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.layers.GeometryLayer;
import fr.umlv.lastproject.smart.layers.GeometryType;
import fr.umlv.lastproject.smart.layers.LineSymbology;
import fr.umlv.lastproject.smart.layers.PointSymbology;
import fr.umlv.lastproject.smart.layers.PointSymbology.PointSymbologieType;
import fr.umlv.lastproject.smart.layers.PolygonSymbology;
import fr.umlv.lastproject.smart.utils.SmartConstants;

public class SymbologyDialog extends AlertDialog.Builder {

	private static final int SPINNER_WIDTH = 200;
	private static final int SIZE = 20;
	

	public SymbologyDialog(final MenuActivity menu, final GeometryLayer layer,
			final LayerItem layerItem) {
		super(menu);
		setCancelable(false);

		final LayoutInflater inflater = LayoutInflater.from(menu);
		
		
		
		final View symbologyDialog = inflater.inflate(R.layout.alert_symbology,
				null);
		TableRow rowShape = (TableRow) symbologyDialog
				.findViewById(R.id.symbologyRow3);

		rowShape.setVisibility(View.GONE);
		setView(symbologyDialog);
		setTitle(R.string.symbo);

		
		final TextView alphaText = (TextView) symbologyDialog.findViewById(R.id.alphaLabel);
		final SeekBar alphaSeek = (SeekBar) symbologyDialog.findViewById(R.id.seekAlpha) ;
		alphaSeek.setMax(100);
		alphaSeek.setProgress((int)(layer.getSymbology().getAlpha()/2.56));
		alphaText.setText(new String((int)(layer.getSymbology().getAlpha()/2.56)+" %")) ;
		alphaSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				alphaText.setText(new String(progress+" %"));
				
			}
		});

		
		
		final Spinner spinner = (Spinner) symbologyDialog
				.findViewById(R.id.colorSpinner);
		spinner.setMinimumWidth(SPINNER_WIDTH);
		List<? extends Map<String, ?>> colors = new LinkedList<Map<String, ?>>();
		for (int i = 0; i < SmartConstants.getColors().length; i++) {
			colors.add(null);
		}

		ColorAdapter adapter = new ColorAdapter(menu, colors,
				android.R.layout.simple_list_item_1, null, null);
		spinner.setAdapter(adapter);

		final Spinner tailleSpinner = (Spinner) symbologyDialog
				.findViewById(R.id.tailleSpinner);
		final List<Integer> tailles = new LinkedList<Integer>();
		for (int i = 1; i <= SIZE; i++) {
			tailles.add(i);
		}
		ArrayAdapter<Integer> tailleAdapter = new ArrayAdapter<Integer>(menu,
				android.R.layout.simple_list_item_1, tailles);
		tailleSpinner.setAdapter(tailleAdapter);

		tailleSpinner.setSelection(layer.getSymbology().getSize() - 1);
		for (int i = 0; i < SmartConstants.getColors().length; i++) {
			int a = SmartConstants.getColors()[i];
			if (a == layer.getSymbology().getColor()) {
				spinner.setSelection(i);
				break;
			}
		}

		final Spinner shapeSpinner = (Spinner) symbologyDialog
				.findViewById(R.id.shapeSpinner);

		if (layer.getType() == GeometryType.POINT) {
			Log.d("TEST", "points");
			rowShape.setVisibility(View.VISIBLE);

			List<? extends Map<String, ?>> shape = new LinkedList<Map<String, ?>>();
			for (int i = 0; i < 2; i++) {
				shape.add(null);
			}

			ShapeAdapter shapeAdapter = new ShapeAdapter(menu, shape,
					android.R.layout.simple_list_item_1, null, null);
			shapeSpinner.setAdapter(shapeAdapter);
			for (int i = 0; i < SmartConstants.getShapeSymbology().length; i++) {
				if (i == ((PointSymbology) layer.getSymbology()).getType()
						.getId()) {
					shapeSpinner.setSelection(i);
					break;
				}
			}
		}

		final AlertDialog dialog = this
				.setPositiveButton(R.string.validate, new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						
						int positionColor = spinner.getSelectedItemPosition();
						Integer taille = tailles.get(tailleSpinner
								.getSelectedItemPosition());
						int positionShape = shapeSpinner
								.getSelectedItemPosition();
						int alpha = (int)(alphaSeek.getProgress() * 2.55 );

						switch (layer.getType()) {
						case POINT :
							layer.setSymbology(new PointSymbology(taille,
									SmartConstants.getColors()[positionColor],alpha,
									PointSymbologieType.getFromId(positionShape)));
							break ;
						case LINE:
							layer.setSymbology(new LineSymbology(taille, SmartConstants.getColors()[positionColor], alpha));
							break;
							
						case POLYGON : 
							
							layer.setSymbology(new PolygonSymbology(taille, SmartConstants.getColors()[positionColor],alpha));
							
						default:
							break;
						}
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
