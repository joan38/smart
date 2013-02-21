package fr.umlv.lastproject.smart;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * 
 * @author Thibault
 * 
 */
public class LayersActivity extends ListActivity {

	private ListOverlay listOverlay = new ListOverlay();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		listOverlay = (ListOverlay) getIntent().getExtras().get("overlays");

		setListAdapter(new ArrayAdapter<String>(this,
				R.layout.listview_layers_items, listOverlay.toArray()));

		ListView listView = getListView();
		listView.setTextFilterEnabled(true);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, show a toast with the TextView text
				Toast.makeText(getApplicationContext(),
						((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			}
		});

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
