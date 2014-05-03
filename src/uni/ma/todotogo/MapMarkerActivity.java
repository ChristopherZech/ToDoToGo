package uni.ma.todotogo;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.os.Build;

public class MapMarkerActivity extends Activity implements OnMapClickListener {
	private GoogleMap mapView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_marker);
		
		//set Action Bar
		ActionBar actionBar = getActionBar();
		//actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
			    
			   
//		// check whether map was instantiated
	    if (mapView == null) {
	         mapView = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	         mapView.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	            if (mapView != null) {
	            // The Map is verified. It is now safe to manipulate the map.
	            	GPSTracker gps = new GPSTracker(MapMarkerActivity.this);
		         	   Location currentLocation= gps.getLocation();
		         	  double Lat= currentLocation.getLatitude();
		         	  double Lng= currentLocation.getLongitude();
		        	LatLng position= new LatLng(Lat,Lng);
		        	mapView.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
		        	mapView.setOnMapClickListener(this);
	        	

	            }
			    }
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
			View rootView = inflater.inflate(R.layout.activity_map_marker,
					container, false);
			return rootView;
		}
	}
	public void onMapClick(LatLng point) {
	mapView.addMarker(new MarkerOptions().position(point));
	Toast.makeText(getApplicationContext(), "Top",Toast.LENGTH_LONG).show();
	}

}
