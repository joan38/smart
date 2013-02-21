package fr.umlv.lastproject.smart;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
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
		LayerItem item = getItem(position);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.listview_layers_items, null);
			smartHolder = new SmartHolder();
			smartHolder.txtTitle = (TextView) convertView
					.findViewById(R.id.layer_name);
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

		smartHolder.chkBox.setChecked(item.isVisible());

		return convertView;
	}
}
