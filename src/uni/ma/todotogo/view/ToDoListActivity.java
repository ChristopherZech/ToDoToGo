package uni.ma.todotogo.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import uni.ma.todotogo.controler.ArrayAdapterToDoList;
import uni.ma.todotogo.controler.GPSTracker;
import uni.ma.todotogo.model.ProximityIntentReceiver;
import uni.ma.todotogo.model.ToDoEntry;
import uni.ma.todotogo.model.ToDoEntryLocation;
import uni.ma.todotogo.model.ToDoLocation;

import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.location.*;

public class ToDoListActivity extends Activity {
	private ArrayList<ToDoEntry> toDoList;
	private ArrayAdapterToDoList adapter;
	private GPSTracker gps;

	// HashSet<Integer> locations;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Context context = getApplicationContext();
		setContentView(R.layout.todo_list_layout);
		ActionBar actionBar = getActionBar();
		// actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		// create list which will be filled with data

		toDoList = new ArrayList<ToDoEntry>();

		ListView lv = (ListView) findViewById(R.id.todolist);

		adapter = new ArrayAdapterToDoList(this, toDoList);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View item,
					final int position, long id) {
				final Context context = getBaseContext();
				final ToDoEntry toDoBuffer = toDoList.get(position);
				final String title = toDoBuffer.getName();
				int success = toDoBuffer.delete(context);
				// int success = ToDoEntry.staticDelete(toDoBuffer.getId(),
				// context);
				if (success > 0)
					updateList();
			}
		});
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
		int itemID = item.getItemId();
		if (itemID == R.id.action_settings) {
			Intent intentSettings = new Intent(ToDoListActivity.this,
					SettingsActivity.class);
			startActivity(intentSettings);

			return true;
		} else if (itemID == R.id.action_add) {

			Intent intentAdd = new Intent(this, AddActivity.class);
			startActivityForResult(intentAdd, RESULT_OK);
			return true;
		} else if (itemID == R.id.action_place) {
			Intent intentMapview = new Intent(this, MapActivity.class);
			startActivity(intentMapview);
			return true;
		} else
			return false;

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
		// ProximityIntentReceiver.removeAllReceivers(getApplicationContext());
		toDoList.clear();
		// iterate over all ToDoEntry
		HashSet<ToDoEntry> entries = ToDoEntry
				.getAllEntries(getApplicationContext());
		// List<ToDoEntry> entryList = new ArrayList<ToDoEntry>(entries);

		Iterator<ToDoEntry> iterator = entries.iterator();
		ProximityIntentReceiver.removeAllReceivers(this);
		ToDoEntryLocation.startAllReceivers(this);
		// for (int i = 0; i<entryList.size();++i){
		while (iterator.hasNext()) {
			ToDoEntry currentEntry = iterator.next();
			// ToDoEntry currentEntry = entryList.get(i);
			currentEntry.setLocationsFromDB(getBaseContext());
			Log.d("ListActivity", "Object to be displayed: "
					+ currentEntry.name);
			// currentEntry.setLocationsFromDB(getBaseContext());
			// currentEntry.connectWithAllLocations(getApplicationContext());
			toDoList.add(currentEntry);
		}
		calcDistance(toDoList);
//		Collections.sort(toDoList, new Comparator<ToDoEntry>() {
//			@Override
//			public int compare(ToDoEntry data1, ToDoEntry data2) {
//				if (data1.closestDistance < data2.closestDistance)
//					return 1;
//				else if (data1.closestDistance == data2.closestDistance)
//					return 0;
//				else
//					return -1;
//			}
//		});
		adapter.notifyDataSetChanged();
		adapter.sort(new Comparator<ToDoEntry>() {
			public int compare(ToDoEntry data1, ToDoEntry data2) {
				Log.d("ToDoListSort", "Compare to:" + data1.closestDistance
						+ "_" + data2.closestDistance);
				if (data1.closestDistance < data2.closestDistance)
					return -1;
				else if (data1.closestDistance == data2.closestDistance)
					return 0;
				else
					return 1;
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();

		gps = new GPSTracker(getApplicationContext());
		gps.getLocation();

		updateList();
	}

	/*
	 * public Location getCurrentPosition() { if (gps == null) { gps = new
	 * GPSTracker(ToDoListActivity.this); } if (gps.canGetLocation()) { return
	 * gps.getLocation(); } else { Log.d("GPS", "cannot get location"); Location
	 * loc = new Location("zero"); loc.setLatitude(0); loc.setLongitude(0);
	 * return loc; } }
	 * 
	 * @Override protected void onActivityResult(int requestCode, int
	 * resultCode, Intent data) { if (requestCode == 1) { if (resultCode ==
	 * RESULT_OK) { Intent intent = getIntent(); // locations =
	 * (HashSet<Integer>) intent // .getSerializableExtra("locationsAdded");
	 * 
	 * int entryID = data.getIntExtra("entryID",-1); Toast.makeText(this, "" +
	 * entryID, Toast.LENGTH_LONG).show(); Log.d("Intent Info",""+entryID);
	 * 
	 * ToDoEntry entry = ToDoEntry.getToDoEntryFromDB(entryID,
	 * getBaseContext()); adapter. } } }
	 */

	public void calcDistance(ArrayList<ToDoEntry> entries) {
		for (ToDoEntry entry : entries) {
			entry.getClosestDistanceTo(gps.getLocation(), this);
		}
	}
}