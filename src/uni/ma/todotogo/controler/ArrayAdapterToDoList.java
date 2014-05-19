package uni.ma.todotogo.controler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import uni.ma.todotogo.model.ToDoEntry;
import uni.ma.todotogo.view.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ArrayAdapterToDoList extends ArrayAdapter<ToDoEntry> {
	  private final Context context;
	  private final ArrayList<ToDoEntry> values;
	  //private final GPSTracker gp; 

	  public ArrayAdapterToDoList(Context context, ArrayList<ToDoEntry> values) {
	   
		super(context, R.layout.todo_list_entry_layout, values);
	    this.context = context;
	    this.values = values;
	    //gp = new GPSTracker(context);
	  }
	  

	  
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.todo_list_entry_layout, parent, false);
	    
	    // get fields
	    TextView textViewDesc = (TextView) rowView.findViewById(R.id.tododesc);
	    TextView textViewDist = (TextView) rowView.findViewById(R.id.distance);
	    View categoryView = (View) rowView.findViewById(R.id.category_colorblock);
	    ToDoEntry buffer = values.get(position);
	    Log.d("ArrayAdapter","Loaded: "+buffer.toString());
	    // set text of description and distance field
	    textViewDesc.setText(buffer.name);
//	    if( buffer.closestDistance ==Float.POSITIVE_INFINITY) textViewDist.setText("no location");
//	    else  textViewDist.setText(((int)buffer.closestDistance)+"m");
	    textViewDist.setText(formatDistance(buffer.closestDistance));
	    categoryView.setBackgroundColor(buffer.category.getColor());

	    // set background color of description and distance field
	    //categoryView.setBackgroundColor((Integer)values.get(position).get("color"));

	    return rowView;
	  }
	  
//	  public String calcDistance(ToDoEntry entry){
//	  float distFloat = entry.getClosestDistanceTo(gp.getLocation(), getContext());
//		String dist;
//		if(distFloat == Float.POSITIVE_INFINITY) {
//			dist = "no loc";
//		} else {
//			dist =  (int)distFloat + "m";
//
//			// if user is within the chosen distance to the location a
//			// notification is issued
//
//			// get distance to notify from preferences
//			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
//			int distToNotify = sharedPref.getInt("pref_distance", 100);
//
//			
//			/*if (distFloat < distToNotify) {
//				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
//						ToDoListActivity.class)
//						.setSmallIcon(R.drawable.ic_launcher)
//						.setContentTitle(entry.getName())
//						.setContentText("You are within " + distFloat + "m!");
//				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//				// int Counter allows you to update the notification later
//				// on (or insures that new notifications are issued
//				mNotificationManager.notify(notificationCounter, mBuilder.build());
//				notificationCounter++;
//			}*/
//		}
//		return dist;
//	  }
//	  
	  
	  public String formatDistance(float distance){
		  if(distance == Float.POSITIVE_INFINITY) return "no location";
		  else if (distance < 1000){
			  return (int)distance+"m";
		  }
		  else return ((float)(Math.round(distance/100)))/10+"km";
	  }


	  
	} 