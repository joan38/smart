package fr.umlv.lastproject.smart;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import fr.umlv.lastproject.smart.dialog.HelpDialog;
import fr.umlv.lastproject.smart.form.Mission;
import fr.umlv.lastproject.smart.utils.SmartConstants;
import fr.umlv.lastproject.smart.utils.SmartLogger;

/**
 * This class is the Home Activity, where all SMART functionalities is available
 * 
 * @author Fad's
 * 
 */
public class HomeActivity extends Activity {

	private List<ListViewItem> listItem;
	private String[] items;
	private ArrayList<Integer> shortcut = new ArrayList<Integer>();

	private Preferences pref;
	private final Logger LOGGER = SmartLogger.getLocator().getLogger();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			Preferences.create(this);
		} catch (PreferencesException e) {
			Toast.makeText(this, getString(R.string.unableLoadPref),
					Toast.LENGTH_LONG).show();
		}

		setTheme(pref.getTheme());
		setRequestedOrientation(pref.getOrientation());

		setTitle(R.string.menuFunctionalitiesTitle);
		setContentView(R.layout.activity_home);

		LOGGER.log(Level.INFO, "Functionalities menu opened");

		// Retry the mission status
		boolean trackStarted = getIntent().getExtras().getBoolean(
				"trackStarted");
		boolean polygonTrackStarted = getIntent().getExtras().getBoolean(
				"polygonTrackStarted");

		// Retry the list of functionalities names
		items = getResources().getStringArray(R.array.items);
		int[] icons = SmartConstants.getIcons();

		listItem = new ArrayList<ListViewItem>();
		for (int i = 0; i < items.length; i++) {
			ListViewItem item;

			switch (MenuAction.getFromId(i)) {
			case CREATE_MISSION:
				if (Mission.isCreated() && Mission.getInstance().isStarted()) {
					item = new ListViewItem(R.drawable.stopmission,
							getString(R.string.stopMission));
				} else {
					item = new ListViewItem(icons[i], items[i]);
				}
				break;

			case POLYGON_TRACK:
				if (polygonTrackStarted) {
					item = new ListViewItem(R.drawable.stoppolygontrack,
							getString(R.string.stopPolygonTrack));
				} else {
					item = new ListViewItem(icons[i], items[i]);
				}
				break;

			case GPS_TRACK:
				if (trackStarted) {
					item = new ListViewItem(R.drawable.stopgpstrack,
							getString(R.string.stopGPSTrack));
				} else {
					item = new ListViewItem(icons[i], items[i]);
				}
				break;

			default:
				item = new ListViewItem(icons[i], items[i]);
				break;
			}

			listItem.add(item);
		}

		ListView listView = (ListView) findViewById(R.id.listView);
		SmartItemHomeAdapter adapter = new SmartItemHomeAdapter(this,
				R.layout.listview_home_items, listItem);
		listView.setAdapter(adapter);

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				final int pos = arg2;
				new AlertDialog.Builder(HomeActivity.this)
						.setPositiveButton(R.string.yes, new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								shortcut.add(Integer.valueOf(pos));
							}
						}).setNegativeButton(R.string.cancel, null)
						.setTitle(R.string.addShortcut).show();
				return true;
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				Intent intentReturn = new Intent(HomeActivity.this,
						MenuActivity.class);
				intentReturn.putExtra("function", items[position]);
				intentReturn.putExtra("position", position);
				intentReturn.putIntegerArrayListExtra("shortcut", shortcut);
				setResult(RESULT_OK, intentReturn);
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_home, menu);
		menu.add(0, 0, 0, R.string.help);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			final HelpDialog helpDialog = new HelpDialog(this,
					R.string.helpMenu);
			helpDialog.show();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		Intent intentReturn = new Intent(HomeActivity.this, MenuActivity.class);
		intentReturn.putIntegerArrayListExtra("shortcut", shortcut);
		setResult(RESULT_CANCELED, intentReturn);
		LOGGER.log(Level.INFO, "Back from functionalities menu");
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			Preferences.getInstance().save();
		} catch (PreferencesException e) {
			Toast.makeText(this, getString(R.string.unableLoadPref),
					Toast.LENGTH_LONG).show();
		}
	}
}