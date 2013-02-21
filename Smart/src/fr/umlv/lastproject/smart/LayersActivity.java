package fr.umlv.lastproject.smart;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

/**
 * 
 * 
 * @author Thibault Douilly
 * 
 */
public class LayersActivity extends ListActivity {

	private ListOverlay listOverlay = new ListOverlay();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.menuLayersTitle);

		listOverlay = (ListOverlay) getIntent().getExtras().get("overlays");

		

		
				ListView listView = getListView();
		listView.setTextFilterEnabled(true);
		
		

		SmartItemLayerAdapter adapter = new SmartItemLayerAdapter(this,
				R.layout.listview_layers_items, listOverlay.toList());
		listView.setAdapter(adapter);

		/*
		 * Log.d("debug", listOverlay.toString()); // (0)test_POINT (1)test_LINE
		 * (2)test_POLYGON (3)poly (4)geo1 (5)geo2
		 * 
		 * listOverlay.reorganize(3, 0); Log.d("debug", listOverlay.toString());
		 * // 3 0 1 2 4 5
		 * 
		 * listOverlay.remove(2); Log.d("debug", listOverlay.toString()); // 3 0
		 * 1 2 4 5
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_layers, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		Intent intentReturn = new Intent(LayersActivity.this,
				MenuActivity.class);
		intentReturn.putExtra("layers", listOverlay);
		setResult(RESULT_OK, intentReturn);
		finish();
	}

}
