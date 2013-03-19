package fr.umlv.lastproject.smart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import fr.umlv.lastproject.smart.dialog.DeleteLayerDialog;
import fr.umlv.lastproject.smart.dialog.HelpDialog;
import fr.umlv.lastproject.smart.drag.DragSortController;
import fr.umlv.lastproject.smart.drag.DragSortListView;
import fr.umlv.lastproject.smart.drag.DragSortListView.RemoveListener;
import fr.umlv.lastproject.smart.layers.BaseMapsAvailable;
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
			DeleteLayerDialog removeDialog;
			if ((mission != null && adapter.getItem(which).getName()
					.contains(mission))
					|| (track != null && adapter.getItem(which).getName()
							.contains(track))) {
				removeDialog = new DeleteLayerDialog(LayersActivity.this,
						which, false);
			} else {
				removeDialog = new DeleteLayerDialog(LayersActivity.this,
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
			pref = Preferences.create(this);
		} catch (PreferencesException e) {
			Toast.makeText(this, getString(R.string.unableLoadPref),
					Toast.LENGTH_LONG).show();
		}
		setTheme(pref.getTheme());
		setRequestedOrientation(pref.getOrientation());
		setContentView(R.layout.activity_layers);

		logger.log(Level.INFO, "Layers menu opened");

		final Spinner baseSpinner = (Spinner) findViewById(R.id.baseMapSpinner) ;
		baseSpinner.setMinimumWidth(200);

		List<String> baseMaps = new ArrayList<String>();
		for(BaseMapsAvailable bma : BaseMapsAvailable.values()){
			baseMaps.add(bma.toString()) ;
		}

		ArrayAdapter<String> baseAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, baseMaps) ;
		baseSpinner.setAdapter(baseAdapter);
		baseSpinner.setSelection(Preferences.getInstance().getBase_map());
		
		baseSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				pref.setBase_map(baseSpinner.getSelectedItemPosition()) ;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		
		
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
			final HelpDialog helpDialog = new HelpDialog(this,
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
		intentReturn.putExtra("baseMap", pref.getBase_map());
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
