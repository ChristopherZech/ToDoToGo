package uni.ma.todotogo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import uni.ma.todotogo.controler.GPSTracker;
import uni.ma.todotogo.view.R;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ProximityIntentReceiver extends BroadcastReceiver {
	private ToDoEntry todo;
	private ToDoLocation loc;
	private static int notificationCounter = 0;
	private PendingIntent proximityIntent;
	private Integer mappingID;

	private static HashMap<Integer, ProximityIntentReceiver> allReceivers = new HashMap<Integer, ProximityIntentReceiver>();

	public ProximityIntentReceiver(ToDoEntryLocation mapping,
			PendingIntent proximityIntent) {
		this.todo = mapping.entry;
		this.loc = mapping.location;
		this.proximityIntent = proximityIntent;
		this.mappingID = mapping.id;
		allReceivers.put(this.mappingID, this);
		Log.d("Proximity", "Mapping put:"+mappingID);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// get distance threshold for notification from preferences
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		int distToNotify = sharedPref.getInt("pref_distance", 100);

		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		if (intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING,
				false)) {
			// entering
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					context)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle(todo.getName())
					.setContentText(
							mappingID + " |You are within " + distToNotify
									+ "m of " + loc.getName() + "!");

			Log.d("Notification", "added notification with id " + mappingID+"| size of mapping: "+ToDoEntryLocation.sizeOfMappings(context));

			// int Counter allows you to update the notification later
			// on (or insures that new notifications are issued
			mNotificationManager.notify(mappingID, mBuilder.build());
		} else {
			// exiting
			mNotificationManager.cancel(mappingID);
		}
	}

	public static void removeReceiverByEntryLocation(ToDoEntryLocation mapping,
			Context context) {
		ProximityIntentReceiver curReceiver = allReceivers.get(mapping.id);
		curReceiver.cancelNotification(context);
		allReceivers.remove(mapping.id);
	}

	public static void removeReceiverByEntryLocationID(Integer mappingID,
			Context context) {
		ProximityIntentReceiver curReceiver = allReceivers.get(mappingID);
		curReceiver.cancelNotification(context);
		allReceivers.remove(mappingID);
	}

	public static void removeAllReceivers(Context context) {

		Iterator<Integer> iter = allReceivers.keySet().iterator();
		while (iter.hasNext()) {
			int value = iter.next();
			ProximityIntentReceiver curReceiver = allReceivers.get(value);
			curReceiver.cancelNotification(context);
		}

		allReceivers.clear();
	}

	public void cancelNotification(Context context) {
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(mappingID);

		LocationManager locationManager = GPSTracker.getLocationManager();

		locationManager.removeProximityAlert(proximityIntent);
		context.unregisterReceiver(this);

		Log.d("Notification", "deleted notification with id " + mappingID);
	}
}
