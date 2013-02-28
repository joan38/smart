package fr.umlv.lastproject.smart;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import fr.umlv.lastproject.smart.dialog.AlertLayerVisibilityDialog;

public class SmartItemLayerAdapter extends ArrayAdapter<LayerItem> {

	private Context context;
	private Activity activity;
	private ListOverlay listOverlay;
	private String mission;

	/**
	 * 
	 * @param context
	 *            of the application
	 * @param resourceId
	 *            xmlconfig
	 * @param items
	 *            to add
	 */
	public SmartItemLayerAdapter(Context context, int resourceId,
			List<LayerItem> items, Activity activity, ListOverlay listOverlay,
			final String mission) {
		super(context, resourceId, items);
		this.context = context;
		this.activity = activity;
		this.listOverlay = listOverlay;
		this.mission = mission;
	}

	private class SmartHolder {
		private CheckBox chkBox;
		private ImageView imageView;
		private TextView txtTitle;
	}

	/**
	 * return the view
	 */
	public View getView(final int position, View convertView, ViewGroup parent) {

		SmartHolder smartHolder = new SmartHolder();
		final LayerItem item = getItem(position);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.listview_layers_items, null);
			smartHolder.txtTitle = (TextView) convertView
					.findViewById(R.id.drag_handle);
			smartHolder.imageView = (ImageView) convertView
					.findViewById(R.id.layer_symbo);
			convertView.setTag(smartHolder);
			smartHolder.chkBox = (CheckBox) convertView
					.findViewById(R.id.layer_check);
			convertView.setTag(smartHolder);
		} else {
			smartHolder = (SmartHolder) convertView.getTag();
		}

		smartHolder.txtTitle.setText(item.getName());
		if (item.getOverview() != null) {
			smartHolder.imageView.setImageBitmap(item.getOverview());

		}

		smartHolder.chkBox.setChecked(item.isVisible());
		if (mission != null && item.getName().contains(mission)) {
			smartHolder.chkBox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					AlertLayerVisibilityDialog dialog = new AlertLayerVisibilityDialog(
							(LayersActivity) context);
					dialog.show();
					((CheckBox) arg0).setChecked(true);

				}
			});
		} else {
			smartHolder.chkBox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					boolean checked = ((CheckBox) arg0).isChecked();
					((CheckBox) arg0).setChecked(checked);
					item.setVisible(checked);
				}

			});

		}

		// if the layer symbology is editable, we add listener on the ImageView
		if (listOverlay.get(position).isEditable()) {
			Log.d("symbo", listOverlay.get(position).isEditable() + "");
			smartHolder.imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intentReturn = new Intent(activity,
							MenuActivity.class);
					intentReturn.putExtra("overlays", listOverlay);
					intentReturn.putExtra("editSymbo", true);
					intentReturn.putExtra("symboToEdit", position);
					activity.setResult(activity.RESULT_OK, intentReturn);
					activity.finish();

				}
			});
		}
		return convertView;
	}
}
