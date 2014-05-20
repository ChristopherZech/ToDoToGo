package uni.ma.todotogo.controler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;

import uni.ma.todotogo.model.ToDoEntryLocation;
import uni.ma.todotogo.view.R;
import uni.ma.todotogo.view.R.drawable;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.Notification.InboxStyle;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

// http://www.androidhive.info/2012/07/android-gps-location-manager-tutorial/
public class GPSTracker extends Service implements LocationListener {

	private final Context mContext;

	private HashSet<Integer> notifiedIDs;
	private HashSet<Integer> handledIDs;
	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1; // 1 minute

	// Declaring a Location Manager
	protected static LocationManager locationManager = null;

	public GPSTracker(Context context) {
		this.mContext = context;
		// getLocation();
		notifiedIDs = new HashSet<Integer>();
		handledIDs = new HashSet<Integer>();
		ToDoEntryLocation.setAllEntries(context);
		// Log.d("GPSTRACKER","initiated!");
	}

	public Location getLocation() {
		try {
			if (locationManager == null) {
				locationManager = (LocationManager) mContext
						.getSystemService(LOCATION_SERVICE);
			}

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			// prompt user to switch on GPS
			if (!isGPSEnabled && !isNetworkEnabled) {
				showSettingsAlert();
			}
			// no network provider is enabled
			else {
				this.canGetLocation = true;
//				 First get location from Network Provider
//				if (isNetworkEnabled) {
//					locationManager.requestLocationUpdates(
//							LocationManager.NETWORK_PROVIDER,
//							MIN_TIME_BW_UPDATES,
//							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//					// Log.d("GPSTRACKER", "Network");
//					if (locationManager != null) {
//						location = locationManager
//								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//						if (location != null) {
//							latitude = location.getLatitude();
//							longitude = location.getLongitude();
//						}
//					}
//					Log.d("Network", "loc");
//				}
				// if GPS Enabled get lat/long using GPS Services
			if (isGPSEnabled) {
				
				Log.d("GPS", "a");
			
					if (location == null) {
						Log.d("GPS", "b");
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						// Log.d("GPSTRACKER", "GPS Enabled");
						
							if (locationManager != null) {
							Log.d("GPS", "c");
							Log.d("GPS", "new pos set");
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);}//}
						if (location != null) {
								Log.d("GPS", "d");
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
			}
					
				
		

		} catch (Exception e) {
			e.printStackTrace();
			Log.d("GPS", "Error!");
			Toast.makeText(getApplicationContext(),
					"Position not found. Please check settings!",
					Toast.LENGTH_LONG).show();
			showSettingsAlert();
		}

		return location;
	}

	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 * */
	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(GPSTracker.this);
		}
	}

	/**
	 * Function to get latitude
	 * */
	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}

		// return latitude
		return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}

		// return longitude
		return longitude;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	/**
	 * Function to show settings alert dialog On pressing Settings button will
	 * lauch Settings Options
	 * */
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog
				.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						mContext.startActivity(intent);
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

	@SuppressLint("NewApi")
	@Override
	public void onLocationChanged(Location currentLocation) {
		Log.d("GPSTRACKER", "onLocationChanged");
		Context context = mContext;
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		int distToNotify = sharedPref.getInt("pref_distance", 100);

		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		HashSet<ToDoEntryLocation> allEntries = ToDoEntryLocation.allEntries;

		for (ToDoEntryLocation entryLocation : allEntries) {
			Log.d("GPSTRACKER",
					"is "
							+ entryLocation.id
							+ " close?"
							+ (entryLocation.location
									.distanceTo(currentLocation) < distToNotify)
							+ "| Is it already contained?"
							+ entryLocation.notified);

			if (entryLocation.location.distanceTo(currentLocation) < distToNotify) {
				if (!entryLocation.getNotified() && ToDoEntryLocation.allEntries.contains(entryLocation)) {
					long[] pattern = { 500, 500, 500, 500, 500, 500, 500, 500,
							500 };
					Uri alarmSound = RingtoneManager
							.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
					if (alarmSound == null) {
						alarmSound = RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
						if (alarmSound == null) {
							alarmSound = RingtoneManager
									.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
						}
					}

					Notification notification = new Notification.Builder(context).setSmallIcon(R.drawable.ic_launcher)
					.setContentText(("You are within "
							+ distToNotify + "m of "
							+ entryLocation.location.getName()
							+ "!"))
					.setTicker(entryLocation.id + " |You are within "
							+ distToNotify + "m of "
							+ entryLocation.location.getName()
							+ "!")
							.setSmallIcon(R.drawable.ic_launcher)
							.setContentTitle(entryLocation.entry.getName())
					.build();

					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
							context)
							.setStyle(new NotificationCompat.InboxStyle())
							.setSmallIcon(R.drawable.ic_launcher)
							.setContentTitle(entryLocation.entry.getName())
							.setContentText("You are within "
											+ distToNotify + "m of "
											+ entryLocation.location.getName()
											+ "!")
							.setLights(Color.BLUE, 500, 500)

							.setSound(alarmSound);
					mBuilder.build();
	

					Log.d("GPSTRACKER",
							"added notification id: "
									+ entryLocation.id
									+ "| size of mapping: "
									+ ToDoEntryLocation.sizeOfMappings(context)
									+ " | locName:"
									+ entryLocation.location.getName()
									+ " | dist: "
									+ entryLocation.location
											.distanceTo(getLocation()));

					entryLocation.setNotified(true);

					Log.d("GPSTRACKER", "Was the notification "
							+ entryLocation.id + " added?"
							+ entryLocation.notified);
					mNotificationManager.notify(entryLocation.id,
							notification);
				}
			} else {
				if (entryLocation.getNotified()) {
					Log.d("GPSTRACKER", "Notification " + entryLocation.id
							+ " will be deleted");
					entryLocation.setNotified(false);
					mNotificationManager.cancel(entryLocation.id);
				}
			}
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		showSettingsAlert();
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public static LocationManager getLocationManager() {
		return locationManager;
	}

}