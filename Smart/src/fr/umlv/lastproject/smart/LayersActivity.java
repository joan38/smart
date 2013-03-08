package fr.umlv.lastproject.smart;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;
import fr.umlv.lastproject.smart.dialog.AlertDeleteLayerDialog;
import fr.umlv.lastproject.smart.dialog.AlertHelpDialog;
import fr.umlv.lastproject.smart.drag.DragSortController;
import fr.umlv.lastproject.smart.drag.DragSortListView;
import fr.umlv.lastproject.smart.drag.DragSortListView.RemoveListener;
import fr.umlv.lastproject.smart.utils.SmartLogger;

/**
 * 
 * 
 * @author Thibault Douilly
 * 
 */
public class LayersActivity extends ListActivity {

	private Preferences pref;

	private ListOverlay listOverlay = new ListOverlay();
	private SmartItemLayerAdapter adapter;
	private String mission;
	private String track;
	private int itemPosition;

	private final Logger logger = SmartLogger.getLocator().getLogger();

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
			if ((mission != null && adapter.getItem(which).getName()
					.contains(mission))
					|| (track != null && adapter.getItem(which).getName()
							.contains(track))) {
				removeDialog = new AlertDeleteLayerDialog(LayersActivity.this,
						which, false);
			} else {
				removeDialog = new AlertDeleteLayerDialog(LayersActivity.this,
						which, true);
			}
			removeDialog.show();
		}
	};

	private DragSortListView mDslv;
	private DragSortController mController;

	private boolean dragEnabled = true;

	public DragSortController buildController(DragSortListView dslv) {
		return new MyDSController(dslv);
	}

	private class MyDSController extends DragSortController {

		public MyDSController(DragSortListView dslv) {
			super(dslv, 0, 1, 1);
			setDragHandleId(R.id.drag_handle);
			mDslv = dslv;

			mDslv.setDropListener(onDrop);
			mDslv.setRemoveListener(onRemove);

			mDslv.setDragEnabled(dragEnabled);
			setDragInitMode(ON_DRAG);
			setRemoveEnabled(true);
		}

		@Override
		public void onLongPress(MotionEvent e) {
			super.onLongPress(e);
			if (LayersActivity.this.mDslv.getAdapter().isEmpty()) {
				return;
			}

			int touchPos = itemPosition;
			if (touchPos < 0) {
				return;
			}
			final Intent intentReturn = new Intent();
			intentReturn.putExtra("overlays", listOverlay);
			intentReturn.putExtra("editSymbo", false);
			intentReturn.putExtra("zoomTo", touchPos);
			setResult(RESULT_OK, intentReturn);
			finish();
		}

		@Override
		public void onDestroyFloatView(View floatView) {
			// do nothing; block super from crashing
		}

		@Override
		public int startDragPosition(MotionEvent ev) {

			int res = super.dragHandleHitPosition(ev);
			if (adapter.isEmpty()) {
				return DragSortController.MISS;
			}
			itemPosition = res;
			CheckBox check = (CheckBox) findViewById(R.id.layer_check);
			ImageView image = (ImageView) findViewById(R.id.layer_symbo);

			double badWidth = check.getWidth() + image.getWidth();

			if ((int) ev.getX() > badWidth && res >= 0) {
				Log.d("DRAG", "DRAG DOIT SE FAIRE : " + res);
				return res;
			} else {
				return DragSortController.MISS;
			}
		}
	}

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

		try {
			pref = Preferences.getInstance(this);
		} catch (PreferencesException e) {
			Toast.makeText(this, getString(R.string.unableLoadPref),
					Toast.LENGTH_LONG).show();
		}
		setTheme(pref.getTheme());
		setContentView(R.layout.activity_layers);

		logger.log(Level.INFO, "Layers menu opened");

		listOverlay = (ListOverlay) getIntent().getExtras().get("overlays");
		mission = getIntent().getExtras().getString("mission");
		track = getIntent().getExtras().getString("track");

		Collections.reverse(listOverlay.toList());

		adapter = new SmartItemLayerAdapter(this,
				R.layout.listview_layers_items, listOverlay.toList(), this,
				listOverlay, mission);

		mDslv = getListView();
		mDslv.setAdapter(adapter);
		mDslv.setTextFilterEnabled(true);

		mController = buildController(mDslv);

		mDslv.setOnTouchListener(mController);

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
		logger.log(Level.INFO, "Back from layers menu");
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			pref.save();
		} catch (PreferencesException e) {
			Toast.makeText(this, getString(R.string.unableLoadPref),
					Toast.LENGTH_LONG).show();
		}
	}
}
