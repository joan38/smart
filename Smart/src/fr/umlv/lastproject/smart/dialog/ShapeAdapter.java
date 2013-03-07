package fr.umlv.lastproject.smart.dialog;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import fr.umlv.lastproject.smart.utils.SmartConstants;

public class ShapeAdapter extends SimpleAdapter {

	public ShapeAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		view.setBackgroundResource(SmartConstants.getShapeSymbology()[position]);
		return view;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		view.setBackgroundResource(SmartConstants.getShapeSymbology()[position]);
		return view;
	}

}
