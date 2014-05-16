package uni.ma.todotogo.view;

import java.util.ArrayList;
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
	//HashSet<Integer> locations;

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
                    final int position, long id)
            {
            	final Context context = getApplicationContext();
            	final ToDoEntry toDoBuffer = toDoList.get(position);
            	final String title = toDoBuffer.getName();
            	int success = ToDoEntry.staticDelete(toDoBuffer.getId(), context);
            	if(success>0) updateList();
            	/*AlertDialog.Builder alert = new AlertDialog.Builder(context);
        		alert.setTitle("What to do with task '" + title + "'?");
        		
        		//get Position of marker and pass it to AddActivity
        		alert.setPositiveButton("View on Map", 
        			new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int whichButton) {
        					Intent intent = new Intent(context,MapActivity.class);
        					HashSet<ToDoLocation> positions= toDoBuffer.getLocations();
        					HashSet<Integer> locations = new HashSet<Integer>();
        					for(ToDoLocation location: positions){
        						locations.add(location.getId());
        					}
        					intent.putExtra("Locations", locations);
        					startActivity(intent);
        			//use location for task
        				}
        		});
        		
        		/*
        		//Delete current location
        		alert.setNeutralButton("Delete",
        				new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog, int whichButton) {
        						Log.d("MapView","How Lat looks in marker:"+marker.getPosition().latitude);
        						int success = ToDoLocation.staticDeleteByNameLatLng(marker.getTitle(),marker.getPosition().latitude,marker.getPosition().longitude, getBaseContext());
        						//int success = ToDoLocation.staticDeleteByString(marker.getTitle(), getBaseContext());
        						//pinnedLocations.remove(marker);
        						if (success>0) marker.remove();
        					}
        		});
        		
        		//Do nothing
        		alert.setNegativeButton("Cancel",
        				new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog, int whichButton) {
        						// Canceled.(Do nothing)
        						}
        		});
        		
				
        		alert.show();
        		//return false; //(still show info menu)
            	
            	/*Context context = getApplicationContext();
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
            	}*/
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
		ProximityIntentReceiver.removeAllReceivers(getApplicationContext());
		toDoList.clear();
		// iterate over all ToDoEntry
		HashSet<ToDoEntry> entries = ToDoEntry
				.getAllEntries(getApplicationContext());
		Iterator<ToDoEntry> iterator = entries.iterator();

		while (iterator.hasNext()) {
			ToDoEntry currentEntry = iterator.next();
			currentEntry.connectWithAllLocations(getApplicationContext());
			toDoList.add(currentEntry);
			currentEntry.registerProximityAlerts(getApplicationContext());
		}

		adapter.notifyDataSetChanged();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		new GPSTracker(getApplicationContext()).getLocation();
		
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
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				Intent intent = getIntent();
				// locations = (HashSet<Integer>) intent
				// .getSerializableExtra("locationsAdded");
				
				int entryID = data.getIntExtra("entryID",-1);
				Toast.makeText(this, "" + entryID, Toast.LENGTH_LONG).show();
				Log.d("Intent Info",""+entryID);
				
				ToDoEntry entry = ToDoEntry.getToDoEntryFromDB(entryID, getBaseContext());
				adapter.
			}
		}
	}*/
}