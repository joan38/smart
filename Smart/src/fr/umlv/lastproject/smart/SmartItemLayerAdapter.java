package fr.umlv.lastproject.smart;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class SmartItemLayerAdapter extends ArrayAdapter<LayerItem> {

	private Context context;

	public SmartItemLayerAdapter(Context context, int resourceId,
			List<LayerItem> items) {
		super(context, resourceId, items);
		this.context = context;
	}

	private class SmartHolder {
		private CheckBox chkBox;
		private ImageView imageView;
		private TextView txtTitle;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		SmartHolder smartHolder = null;
		final LayerItem item = getItem(position);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.listview_layers_items, null);
			smartHolder = new SmartHolder();
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

		smartHolder.imageView.setImageBitmap(item.getOverview());
		// smartHolder.imageView.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		//
		// }
		// });

		smartHolder.chkBox.setChecked(item.isVisible());
		smartHolder.chkBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				boolean checked = ((CheckBox) arg0).isChecked();
				((CheckBox) arg0).setChecked(!checked);
				item.setVisible(!checked);

			}
		});

		return convertView;
	}
}
