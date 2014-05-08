package uni.ma.todotogo;

import java.util.HashMap;
import java.util.Set;

import com.google.android.gms.maps.GoogleMap;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

//Author:Timo

public class MapView extends Activity {
	// Create Google Map
	private GoogleMap mapView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
	            GPSTracker gps = new GPSTracker(MapView.this);
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
	        	}
	           
	            
	            HashMap<Integer, ToDoLocation> locationList = ToDoLocation.getAllEntries(getBaseContext());
	        	Set<Integer> keys = locationList.keySet();
	        	ToDoLocation buffer;
	            for(Integer i : keys){
	            	buffer = locationList.get(i);
	            	Log.d("MapView", buffer.toString()+"loaded");
	        		mapView.addMarker(new MarkerOptions().position(buffer.getLatLng()).title(buffer.getName()));
	        	}
	            
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.to_do_list_menu, menu);
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
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.activity_map_view,
					container, false);
			return rootView;
		}
	}

	
}
