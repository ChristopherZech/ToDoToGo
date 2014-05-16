package uni.ma.todotogo.model;

import java.util.ArrayList;
import java.util.Iterator;

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
	private int notificationId;
	private PendingIntent proximityIntent;
	
	private static ArrayList<ProximityIntentReceiver> allReceivers = new ArrayList<ProximityIntentReceiver>();
	
	public ProximityIntentReceiver(ToDoEntry todo, ToDoLocation loc, PendingIntent proximityIntent) {
		this.todo = todo;
		this.loc = loc;
		this.proximityIntent = proximityIntent;
		
		notificationCounter++;
		this.notificationId = Integer.valueOf(notificationCounter);
		
		allReceivers.add(this);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		// get distance threshold for notification from preferences
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		int distToNotify = sharedPref.getInt("pref_distance", 100);
		
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		if(intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false)) {
			// entering
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					context)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle(todo.getName())
					.setContentText(
							notificationId+" |You are within " + distToNotify + "m of "
									+ loc.getName() + "!");
			
			Log.d("Notification", "added notification with id "+ notificationId);
			
			// int Counter allows you to update the notification later
			// on (or insures that new notifications are issued
			mNotificationManager.notify(notificationId, mBuilder.build());
		} else {
			// exiting
			mNotificationManager.cancel(notificationId);
		}
	}
	
	public static void removeAllReceivers(Context context) {
		Iterator<ProximityIntentReceiver> iter = allReceivers.iterator();

		
		while(iter.hasNext()) {
			ProximityIntentReceiver curReceiver = iter.next();
			curReceiver.cancelNotification(context);
		}
		
		allReceivers.clear();
	}
	
	public void cancelNotification(Context context) {
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(notificationId);
		
		LocationManager locationManager = GPSTracker.getLocationManager();
		
		locationManager.removeProximityAlert(proximityIntent);
		context.unregisterReceiver(this);
		
		Log.d("Notification", "deleted notification with id "+notificationId);
	}
}
