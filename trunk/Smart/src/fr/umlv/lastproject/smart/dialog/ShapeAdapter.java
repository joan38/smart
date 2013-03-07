package fr.umlv.lastproject.smart.dialog;
import java.util.List;
import java.util.Map;

import fr.umlv.lastproject.smart.R;
import fr.umlv.lastproject.smart.utils.SmartConstants;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;


public class ShapeAdapter extends SimpleAdapter {

	public ShapeAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		view.setBackgroundResource(SmartConstants.shapeSymbology[position]);
		return view;
	}

	@Override
	public View getDropDownView (int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		view.setBackgroundResource(SmartConstants.shapeSymbology[position]);
		return view;
	}


}
