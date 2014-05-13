package uni.ma.todotogo.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import uni.ma.todotogo.controler.ArrayAdapterToDoList;
import uni.ma.todotogo.model.ToDoEntry;
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
	private ArrayAdapter adapter;
	HashSet<ToDoLocation> locations;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View item,
                    int position, long id)
            {
            	Context context = getApplicationContext();
            	ToDoEntry toDoBuffer = toDoList.get(position);
            	int success = toDoBuffer.delete(context);
            	int duration = Toast.LENGTH_SHORT;
            	if(success == 1){
            		adapter.remove(adapter.getItem(position));
                	Toast toast = Toast.makeText(context, "Item "+position+" has been removed", duration);
                	toast.show();
            	}
            	else{
                	Toast toast = Toast.makeText(context, "Item "+position+" could not removed!!", duration);
                	toast.show();
            	}
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
		toDoList.clear();

		// iterate over all ToDoEntry
		HashMap<Integer, ToDoEntry> entries = ToDoEntry
				.getAllEntries(getBaseContext());
		Set<Integer> entriesIDs = entries.keySet();
		Iterator<Integer> iterator = entriesIDs.iterator();

		int notificationCounter = 0; // unique id for notifications

		while (iterator.hasNext()) {
			int curID = iterator.next();
			ToDoEntry curEl = entries.get(curID);
			toDoList.add(curEl);
			
			// TODO Implement proper HashSet for locations which are mapped to a
			// specific task, possibility to display task name
			// iterate over all locations attributable to one specific task
			/*HashSet<ToDoLocation> locations = ToDoLocation.getAllEntries(getBaseContext());
			Iterator<ToDoLocation> iter = locations.iterator();

			int closestDist = 9999999;
			while (iter.hasNext()) {
				ToDoLocation curItem = iter.next();
				// get distance of the specific location to the current position
				double curDist = curItem.distanceTo(getCurrentPosition());
				// if user is within the chosen distance to the location a
				// notification is issued
				if (curDist < 100) {
					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
							ToDoListActivity.this)
							.setSmallIcon(R.drawable.ic_launcher)
							.setContentTitle("Task Name")
							.setContentText(
									"You are within " + (int) curDist + "m of "
											+ curItem.getName() + "!");
					NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					// int Counter allows you to update the notification later
					// on (or insures that new notifications are issued
					mNotificationManager.notify(Counter, mBuilder.build());
					Counter++;
				}
				// to shortest distance to a location of a task is set and
				// displayed
				//
				// notification for all locations in range or only for the
				// closest?
				//
				if (curDist < closestDist) {
					closestDist = (int) curDist;

				}
			}
			String dist = closestDist + "m";
			float distFloat = curEl.getClosestLocationTo(getCurrentPosition());
			String dist;
			if(distFloat == Float.POSITIVE_INFINITY) {
				dist = "no loc";
			} else {
				dist =  (int)distFloat + "m";

				// if user is within the chosen distance to the location a
				// notification is issued

				// get distance to notify from preferences
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
				int distToNotify = sharedPref.getInt("pref_distance", 100);


				if (distFloat < distToNotify) {
					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
							ToDoListActivity.this)
							.setSmallIcon(R.drawable.ic_launcher)
							.setContentTitle(curEl.getName())
							.setContentText("You are within " + distFloat + "m!");
					NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					// int Counter allows you to update the notification later
					// on (or insures that new notifications are issued
					mNotificationManager.notify(notificationCounter, mBuilder.build());
					notificationCounter++;
				}
			}*/


		}

		adapter.notifyDataSetChanged();
	}

	@Override
	public void onResume() {
		super.onResume();
		updateList();
	}
	/*
	public Location getCurrentPosition() {
		if (gps == null) {
			gps = new GPSTracker(ToDoListActivity.this);
		}
		if (gps.canGetLocation()) {
			return gps.getLocation();
		} else {
			Log.d("GPS", "cannot get location");
			Location loc = new Location("zero");
			loc.setLatitude(0);
			loc.setLongitude(0);
			return loc;
		}
	}*/
}