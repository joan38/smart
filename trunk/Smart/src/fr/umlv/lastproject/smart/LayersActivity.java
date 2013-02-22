package fr.umlv.lastproject.smart;

import android.animation.AnimatorSet;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import fr.umlv.lastproject.smart.drag.DragSortListView;
import fr.umlv.lastproject.smart.drag.DragSortListView.RemoveListener;

/**
 * 
 * 
 * @author Thibault Douilly
 * 
 */
public class LayersActivity extends ListActivity {

	private ListOverlay listOverlay = new ListOverlay();
	SmartItemLayerAdapter adapter;

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from != to) {
				Log.d("TEST2","test pd encule");
				DragSortListView list = getListView();
				LayerItem item = adapter.getItem(from);
				adapter.remove(item);
				adapter.insert(item, to);
				list.moveCheckState(from, to);
			}
		}
	};

	private RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			DragSortListView list = getListView();
			LayerItem item = adapter.getItem(which);
			adapter.remove(item);
			list.removeCheckState(which);
		}
	};

	@Override
	public DragSortListView getListView() {
		return (DragSortListView) super.getListView();
	}

	// ------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.menuLayersTitle);
		
		setContentView(R.layout.activity_layers);

		listOverlay = (ListOverlay) getIntent().getExtras().get("overlays");

		ListView listView = getListView();
		listView.setTextFilterEnabled(true);

		adapter = new SmartItemLayerAdapter(this,
				R.layout.listview_layers_items, listOverlay.toList());
		listView.setAdapter(adapter);

		DragSortListView list = getListView();
		list.setDropListener(onDrop);
		list.setRemoveListener(onRemove);
		

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
