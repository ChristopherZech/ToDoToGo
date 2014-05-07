package uni.ma.todotogo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.android.gms.maps.model.LatLng;

import uni.ma.todotogo.ToDoContract.DBPlacesEntry;
import uni.ma.todotogo.ToDoContract.DBToDoEntry;
import uni.ma.todotogo.ToDoContract.DBToDoPlacesEntry;
import android.os.Bundle;
import android.provider.Settings;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.location.*;

public class ToDoListActivity extends Activity {
	private ArrayList<HashMap<String, Object>> toDoList;
	private ArrayAdapter adapter;
	GPSTracker gps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo_list_layout);
		ActionBar actionBar = getActionBar();
		// actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		// create list which will be filled with data
		toDoList = new ArrayList<HashMap<String, Object>>();

		ListView lv = (ListView) findViewById(R.id.todolist);
		adapter = new ArrayAdapterToDoList(this, toDoList);
		lv.setAdapter(adapter);

		getCurrentPosition();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.to_do_list_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intentSettings = new Intent(ToDoListActivity.this,
					SettingsActivity.class);
			startActivity(intentSettings);

			return true;
		case R.id.action_add:
			Intent intentAdd = new Intent(this, AddActivity.class);
			startActivityForResult(intentAdd, RESULT_OK);
			return true;
		case R.id.action_place:
			Intent intentMapview = new Intent(this, MapView.class);
			startActivity(intentMapview);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Creates a HashMap for configuring list items.
	 * 
	 * @param desc
	 *            Content of the description field.
	 * @param dist
	 *            Content of the distance field.
	 * @param bgColor
	 *            Background color of the fields.
	 * @return Created hash map.
	 */
	public HashMap<String, Object> addItem(String desc, String dist, int bgColor) {
		HashMap<String, Object> item = new HashMap<String, Object>();

		item.put("ToDoName", desc);
		item.put("distance", dist);
		item.put("color", bgColor);

		return item;
	}

	public void updateList() {
		toDoList.clear();
		
		HashMap<Integer, ToDoEntry> entries = ToDoEntry.getAllEntries(getBaseContext());
		Set<Integer> entriesIDs = entries.keySet();
		Iterator<Integer> iterator = entriesIDs.iterator();
		while(iterator.hasNext()) {
			int curID = iterator.next();
			ToDoEntry curEl = entries.get(curID);
			
			// TODO implement distance
			Location loc = new Location("New");
			loc.setLatitude(0);
			loc.setLongitude(0);
			int distance = (int) loc.distanceTo(getCurrentPosition());
			String dist = distance + "m";
			
			toDoList.add(addItem(curEl.getName(), dist, curEl.getCategory().getColor()));
		}

		adapter.notifyDataSetChanged();
	}

	@Override
	public void onResume() {
		super.onResume();
		updateList();
	}

	public Location getCurrentPosition() {
		if(gps == null) {
			gps = new GPSTracker(ToDoListActivity.this);
		}
		if(gps.canGetLocation()) {
			return gps.getLocation();
		} else {
			Log.d("GPS", "cannot get location");
			Location loc = new Location("zero");
			loc.setLatitude(0);
			loc.setLongitude(0);
			return loc;
		}
	}

}
