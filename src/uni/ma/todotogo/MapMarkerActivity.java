package uni.ma.todotogo;

import java.util.HashSet;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;

public class MapMarkerActivity extends Activity implements OnMapClickListener {
	private GoogleMap mapView;
	private HashSet<ToDoLocation> pinnedLocations = new HashSet<ToDoLocation>();
	private String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_marker);

		// set Action Bar
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// check whether map was instantiated
		if (mapView == null) {
			mapView = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			mapView.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			if (mapView != null) {
				// The Map is verified. It is now safe to manipulate the map.
				// get current Position from GPSTracker and set
				// OnMapClickListner
				GPSTracker gps = new GPSTracker(MapMarkerActivity.this);
				Location currentLocation = gps.getLocation();
				double Lat = currentLocation.getLatitude();
				double Lng = currentLocation.getLongitude();
				LatLng position = new LatLng(Lat, Lng);
				mapView.animateCamera(CameraUpdateFactory.newLatLngZoom(
						position, 13));
				Toast.makeText(getApplicationContext(), "Lat:" + Lat + "Lng:" + Lng,
						Toast.LENGTH_SHORT).show();
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
		if(id == R.id.add_activity_map_menu_action_add_ok) {
			
			finish();
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

		// Prompt location name input
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Location Name");
		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);
		final double Lat= point.latitude;
		final double Lng= point.longitude;
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  
			name = input.getText().toString();
			Toast.makeText(getApplicationContext(), name+ "Lat:" + Lat + "Lng:" + Lng ,Toast.LENGTH_LONG).show();


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
		// add marker at clicked position
		mapView.addMarker(new MarkerOptions().position(point).title(name));
		
		// create new ToDoLocation object and add it to pinnedLocations
		ToDoLocation newEntry = new ToDoLocation(-1, name, point.latitude, point.longitude);
		newEntry.writeToDB(getBaseContext());
		pinnedLocations.add(newEntry);
	}

}