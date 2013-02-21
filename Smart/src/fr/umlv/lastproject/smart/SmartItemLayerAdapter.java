package fr.umlv.lastproject.smart;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import fr.umlv.lastproject.smart.layers.SmartIcon;

public class SmartItemLayerAdapter extends ArrayAdapter<LayerState> {

	private Context context;

	public SmartItemLayerAdapter(Context context, int resourceId,
			List<LayerState> items) {
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
		LayerState item = getItem(position);

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

		Bitmap bitmap = null;

		if (item.getSymbologie() == SmartIcon.RASTER) {
			bitmap = item.getSymbologie().getBitmap();
		} else if (item.getSymbologie() == SmartIcon.SYMBOLOGY) {
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.raster);
			bitmap = Bitmap.createScaledBitmap(bitmap, 32, 32, true);
			Canvas c = new Canvas(bitmap);
			Paint p = new Paint();
			p.setColor(item.getSymbologie().getColor());
			Log.d("debug", "" + item.getSymbologie().getType());
			switch (item.getSymbologie().getType()) {
			case POINT:
				c.drawCircle(8, 8, 4, p);
				break;
			case LINE:
				c.drawLine(0, 0, 16, 16, p);
				break;
			case POLYGON:
				c.drawRect(new Rect(0, 0, 16, 16), p);
				break;
			default:
				break;
			}
		}

		smartHolder.imageView.setImageBitmap(bitmap);

		smartHolder.chkBox.setChecked(item.isVisible());

		return convertView;
	}
}
