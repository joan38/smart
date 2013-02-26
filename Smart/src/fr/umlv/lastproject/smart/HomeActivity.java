package fr.umlv.lastproject.smart;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import fr.umlv.lastproject.smart.utils.SmartConstants;

/**
 * This class is the Home Activity, where all SMART functionalities is available
 * 
 * @author Fad's
 * 
 */
public class HomeActivity extends Activity {

	private List<ListViewItem> listItem;
	private String[] items;
	private int[] icons;
	private List<Integer> shortcut = new ArrayList<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.menuFunctionalitiesTitle);
		setContentView(R.layout.activity_home);

		// Retry the mission status
		boolean enabled = getIntent().getExtras().getBoolean("missionCreated");

		// Retry the list of functionalities names
		items = getResources().getStringArray(R.array.items);
		icons = SmartConstants.icons;

		listItem = new ArrayList<ListViewItem>();
		for (int i = 0; i < items.length; i++) {
			ListViewItem item;

			switch (i) {
			case SmartConstants.CREATE_MISSION:
				if (enabled) {
					item = new ListViewItem(icons[i],
							getString(R.string.stopMission), true);
				} else {
					item = new ListViewItem(icons[i], items[i], true);
				}
				break;

			case SmartConstants.POINT_SURVEY:
				item = new ListViewItem(icons[i], items[i], enabled);
				break;

			case SmartConstants.LINE_SURVEY:
				item = new ListViewItem(icons[i], items[i], enabled);
				break;

			case SmartConstants.POLYGON_SURVEY:
				item = new ListViewItem(icons[i], items[i], enabled);
				break;
			default:
				item = new ListViewItem(icons[i], items[i], true);
				break;
			}

			listItem.add(item);
		}

		ListView listView = (ListView) findViewById(R.id.listView);
		SmartItemHomeAdapter adapter = new SmartItemHomeAdapter(this,
				R.layout.listview_home_items, listItem);
		listView.setAdapter(adapter);
		listView.setClickable(false);
		listView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				return false;
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
				intentReturn.putExtra("shortcut", shortcut.toArray());
				setResult(RESULT_OK, intentReturn);
				view.setEnabled(false);
				finish();

			}
		});

		registerForContextMenu(listView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.listView) {
			menu.setHeaderTitle("Option");
			menu.add(0, 1, 0, R.string.addShortcut);
			menu.add(0, 2, 0, R.string.removeShortcut);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		int index = shortcut.indexOf(info.position);
		if (item.getItemId() == 1) {
			if (index == -1) {
				shortcut.add(info.position);
				// Toast.makeText(this,
				// item.getTitle() + " : " + items[info.position],
				// Toast.LENGTH_SHORT).show();
			}
		} else {
			if (index != -1) {
				shortcut.remove(index);
				// Toast.makeText(this,
				// item.getTitle() + " : " + items[info.position],
				// Toast.LENGTH_SHORT).show();
			}
		}
		return super.onContextItemSelected(item);
	}
}