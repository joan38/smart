package fr.umlv.lastproject.smart;

import java.util.Collections;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import fr.umlv.lastproject.smart.dialog.AlertDeleteLayerDialog;
import fr.umlv.lastproject.smart.dialog.AlertHelpDialog;
import fr.umlv.lastproject.smart.drag.DragSortListView;
import fr.umlv.lastproject.smart.drag.DragSortListView.RemoveListener;

/**
 * 
 * 
 * @author Thibault Douilly
 * 
 */
public class LayersActivity extends ListActivity {
	
	private Preferences pref;

	private ListOverlay listOverlay = new ListOverlay();
	SmartItemLayerAdapter adapter;
	private String mission;

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from != to) {
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
			AlertDeleteLayerDialog removeDialog;
			if (mission != null
					&& adapter.getItem(which).getName().contains(mission)) {
				removeDialog = new AlertDeleteLayerDialog(LayersActivity.this,
						which, false);
			} else {
				removeDialog = new AlertDeleteLayerDialog(LayersActivity.this,
						which, true);
			}
			removeDialog.show();
		}
	};

	public void removeLayer(int which, boolean remove) {
		DragSortListView list = getListView();
		LayerItem item = adapter.getItem(which);
		adapter.remove(item);
		if (remove) {
			list.removeCheckState(which);
		} else {
			adapter.insert(item, which);
		}
	}

	@Override
	public DragSortListView getListView() {
		return (DragSortListView) super.getListView();
	}

	// ------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.menuLayersTitle);
		pref = new Preferences(this);
		setTheme(pref.theme);
		setContentView(R.layout.activity_layers);

		listOverlay = (ListOverlay) getIntent().getExtras().get("overlays");
		mission = getIntent().getExtras().getString("mission");

		ListView listView = getListView();
		listView.setTextFilterEnabled(true);

		Collections.reverse(listOverlay.toList());

		adapter = new SmartItemLayerAdapter(this,
				R.layout.listview_layers_items, listOverlay.toList(), this,
				listOverlay, mission);

		listView.setAdapter(adapter);

		DragSortListView list = getListView();
		list.setDropListener(onDrop);
		list.setRemoveListener(onRemove);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_layers, menu);
		menu.add(0, 1, 0, R.string.help);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			final AlertHelpDialog helpDialog = new AlertHelpDialog(this,
					R.string.helpLayer);
			helpDialog.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		Intent intentReturn = new Intent(LayersActivity.this,
				MenuActivity.class);
		intentReturn.putExtra("overlays", listOverlay);
		intentReturn.putExtra("editSymbo", false);
		setResult(RESULT_OK, intentReturn);
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		pref.save();
	}
}
