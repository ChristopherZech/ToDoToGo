package uni.ma.todotogo.view;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import uni.ma.todotogo.controler.GPSTracker;
import uni.ma.todotogo.model.ToDoContract.DBPlacesEntry;
import uni.ma.todotogo.model.ToDoLocation;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

//Author:Timo

public class MapActivity extends Activity implements OnMarkerClickListener, OnMapClickListener {
	// Create Google Map
	private GoogleMap mapView;
	//private HashMap<Marker, ToDoLocation> pinnedLocations = new HashMap<Marker, ToDoLocation>();
	private String name;
	private int locationsAdded;//private HashSet<Integer> locationsAdded;
	private LocationManager locationManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//locationsAdded = new HashSet<Integer>();
		setContentView(R.layout.activity_map_view);
		//set Action Bar
		ActionBar actionBar = getActionBar();
	    actionBar.setHomeButtonEnabled(true);
	    actionBar.setDisplayUseLogoEnabled(false);
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    
	   
	    // check whether map was instantiated
	    if (mapView == null) {
	         mapView = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	         mapView.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	            if (mapView != null) {
	            // The Map is verified. It is now safe to manipulate the map.
	            
	            	// Instantiated GPSTracker object to get current Position
	            /*GPSTracker gps = new GPSTracker(MapActivity.this);
	            Location currentLocation= gps.getLocation();
	            double Lat= currentLocation.getLatitude();
	         	double Lng= currentLocation.getLongitude();
	        	LatLng position= new LatLng(Lat,Lng);
	        	//set Focus of Map current
	        	mapView.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
	        	//change color of marker
	        	BitmapDescriptor bitmapDescriptor = 
	        			BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
	        	// add marker to current Position
	        	mapView.addMarker(new MarkerOptions().position(position).icon(bitmapDescriptor)
	        			.title("Current Location"));
	        	//Display currentposition as toast
	        	Toast.makeText(getApplicationContext(),	"Lat:" + Lat + "Lng:" + Lng, Toast.LENGTH_SHORT).show();*/
	            mapView.setMyLocationEnabled(true);
	            GPSTracker gps = new GPSTracker(MapActivity.this);
	            Location currentLocation= gps.getLocation();
	            double Lat= currentLocation.getLatitude();
	         	double Lng= currentLocation.getLongitude();
	        	LatLng position= new LatLng(Lat,Lng);
	            mapView.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
				mapView.setOnMapClickListener(this);
	        	mapView.setOnMarkerClickListener(this);
	        	}
	           
	            
	            HashSet<ToDoLocation> locationList = ToDoLocation.getAllEntries(getBaseContext());
	            //HashMap<Integer, ToDoLocation> locationList = ToDoLocation.getAllEntries(getBaseContext());
	        	//Set<Integer> keys = locationList.keySet();
	            for(ToDoLocation i : locationList){
	            	Log.d("MapView", i.toString()+"loaded");
	            	
	            	//change color of marker
		        	BitmapDescriptor bitmapDescriptor = 
		        			BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
	        		mapView.addMarker(new MarkerOptions().position(i.getLatLng()).icon(bitmapDescriptor).title(i.getName()));
	        	}
	           /* mapView.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
	            	public void onInfoWindowClick(Marker marker){
	            		
	            		AlertDialog.Builder alert = new AlertDialog.Builder(this);
	            		final String name = marker.getTitle();
	            		alert.setTitle("What to do with location " + name + "?");
	            		
	            		//get Position of marker and pass it to AddActivity
	            		alert.setPositiveButton("Use for new task", 
	            			new DialogInterface.OnClickListener() {
	            				public void onClick(DialogInterface dialog, int whichButton) {
	            					Intent intent = new Intent(MapActivity.this,AddActivity.class);
	            					LatLng position=marker.getPosition();
	            					intent.putExtra("LatLng", position);
	            					startActivity(intent);
	            			//use location for task
	            				}
	            		});
	            		
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
	            		
	            	}
	            });
	            /*
	            //get Database to display Locations
	        	ToDoDbHelper mDbHelper = new ToDoDbHelper(getBaseContext());
	    		SQLiteDatabase db = mDbHelper.getReadableDatabase();
	    			if(db!=null){
	    				Cursor mCursor = db.query("places", new String[]{"name","latitude","longitude"} ,
	    						null, null, null, null,null);
	    				mCursor.moveToFirst();
	    				String name;
	    				double lat;
	    				double lng;
	        			//iterate through database to add marker for each location
	    				while(!mCursor.isAfterLast()){
	    					name = mCursor.getString(0);
	    					lat = mCursor.getDouble(1);
	    					lng = mCursor.getDouble(2);
	    					LatLng markerPosition = new LatLng(lat,lng);
	        				mapView.addMarker(new MarkerOptions().position(markerPosition).title(name));
	        				mCursor.moveToNext();
	    				}
	    			}
	           */
	    }
   }
	
	public boolean onMarkerClick(final Marker marker) {
		//ToDoDbHelper mDbHelper = new ToDoDbHelper(getBaseContext());
		//SQLiteDatabase db = mDbHelper.getReadableDatabase();
		//Log.d("Test", db.query(DBPlacesEntry.TABLE_NAME, new String[]{DBPlacesEntry.COLUMN_NAME_NAME}, DBPlacesEntry.COLUMN_NAME_NAME+" =? AND " +DBPlacesEntry.COLUMN_NAME_LATITUDE+" =? AND " +DBPlacesEntry.COLUMN_NAME_LONGITUDE+" =?", new String[]{"test","49.5919","11.0086"}, null, null, null).getString(1) );
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		final String name = marker.getTitle();
		alert.setTitle("What to do with location " + name + "?");
		final Context context = getBaseContext();
		//get Position of marker and pass it to AddActivity
		/*
		alert.setPositiveButton("Use for new task", 
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Intent intent = new Intent(MapActivity.this,AddActivity.class);
					LatLng position=marker.getPosition();
					locationsAdded.add(ToDoLocation.getLocationByNameAndLatLng(marker.getTitle(), position.latitude, position.longitude, context));
					intent.putExtra("locationsAdded", locationsAdded);
					startActivity(intent);
			//use location for task
				}
		});*/
		
		//Delete current location
		alert.setNeutralButton("Use for current task",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						ToDoLocation buffer = ToDoLocation.getLocationByNameAndLatLng(marker.getTitle(), marker.getPosition().latitude, marker.getPosition().longitude, context);
						String name = "no name";
						Toast.makeText(getApplicationContext(), buffer.getName(),
								Toast.LENGTH_LONG).show();
						int success = buffer.getId();
						locationsAdded = buffer.getId();//locationsAdded.add(buffer.getId());
						Log.d("MapView","New locationsAdded"+locationsAdded);
						//int success = ToDoLocation.staticDeleteByString(marker.getTitle(), getBaseContext());
						//pinnedLocations.remove(marker);
					}
		});
		
		//Do nothing
		alert.setNegativeButton("Delete",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Log.d("MapView","How Lat looks in marker:"+marker.getPosition().latitude);
						int success = ToDoLocation.staticDeleteByNameLatLng(marker.getTitle(),marker.getPosition().latitude,marker.getPosition().longitude, context);
						//int success = ToDoLocation.staticDeleteByString(marker.getTitle(), getBaseContext());
						//pinnedLocations.remove(marker);
						if (success>0) marker.remove();
						}
		});
		

		alert.show();
		return false; //(still show info menu)
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_marker, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == R.id.add_activity_map_menu_action_add_ok) {
			Intent data = new Intent();
			data.putExtra("locationsAdded", locationsAdded);
			setResult(RESULT_OK, data);
			finish();
		}
		return super.onOptionsItemSelected(item);
	}


	public void onMapClick(LatLng point) {
		//System.out.println("Number of locations pinned"+ pinnedLocations.size());
		System.out.println("Clicked on location " + point.latitude
				+ point.longitude);
		// Prompt location name input
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Location Name");
		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);
		final double Lat = point.latitude;
		final double Lng = point.longitude;
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				name = input.getText().toString();
				System.out.println("Name for location  " + name);
				Toast.makeText(getApplicationContext(),
						name + "Lat:" + Lat + "Lng:" + Lng, Toast.LENGTH_LONG)
						.show();
				// add marker at clicked position
				Marker marker = mapView.addMarker(new MarkerOptions().position(new LatLng(Lat,Lng)).title(name));

				// create new ToDoLocation object and add it to pinnedLocations
				ToDoLocation newEntry = new ToDoLocation(-1, name, Lat,
						Lng, marker.getId());	
				newEntry.writeToDB(getBaseContext());
				//pinnedLocations.put(marker, newEntry);
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
						String name = "no name";
						Toast.makeText(getApplicationContext(), name,
								Toast.LENGTH_LONG).show();
					}
				});

		alert.show();
	}
}