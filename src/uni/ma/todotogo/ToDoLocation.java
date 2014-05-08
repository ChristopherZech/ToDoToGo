package uni.ma.todotogo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;

import com.google.android.gms.maps.model.LatLng;

import uni.ma.todotogo.ToDoContract.DBPlacesEntry;
import uni.ma.todotogo.ToDoContract.DBToDoEntry;

public class ToDoLocation extends Location {
	int id;
	private String name;
	private HashSet<ToDoEntry> tasks;
	
	private static HashMap<Integer,ToDoLocation> allLocations = new HashMap<Integer, ToDoLocation>();

	/**
	 * Returns a list with all locataions stored in the db.
	 * 
	 * @return
	 */
	public static HashMap<Integer, ToDoLocation> getAllEntries(Context context) {
		if(allLocations.isEmpty()) {
			// fill list with entries
			ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
			SQLiteDatabase db = mDbHelper.getReadableDatabase();

			// DB entries to get
			String[] projection = { DBPlacesEntry._ID,
					DBPlacesEntry.COLUMN_NAME_NAME,
					DBPlacesEntry.COLUMN_NAME_LATITUDE,
					DBPlacesEntry.COLUMN_NAME_LONGITUDE};

			Cursor cursor = db.query(DBPlacesEntry.TABLE_NAME, projection, null, null, null, null, null );
			cursor.moveToFirst();

			while (!cursor.isAfterLast()) {
				// get data
				int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBPlacesEntry._ID));
				String name = cursor.getString(cursor
						.getColumnIndexOrThrow(DBPlacesEntry.COLUMN_NAME_NAME));
				double latitude = cursor.getFloat(cursor.getColumnIndexOrThrow(DBPlacesEntry.COLUMN_NAME_LATITUDE));
				double longitude = cursor.getFloat(cursor.getColumnIndexOrThrow(DBPlacesEntry.COLUMN_NAME_LONGITUDE));
				
				// create object
				new ToDoLocation(id, name, latitude, longitude); // (automatically gets stored in the HashMap)
				cursor.moveToNext();
				
			}
		}
		return allLocations;
	}
	
	public ToDoLocation(int id, String name, double latitude, double longitude) {
		this(id, name, latitude, longitude, new HashSet<ToDoEntry>());
	}
	
	public ToDoLocation(int id, String name, double latitude, double longitude, HashSet<ToDoEntry> tasks) {
		super("none");

		this.id = id;
		this.name = name;
		this.tasks = tasks;
		this.setLatitude(latitude);
		this.setLongitude(longitude);
		
		allLocations.put(id, this);
	}
	

	/**
	 * Writes content to Database. If <code>id</code> is <code>-1</code> a new
	 * item is created. Update not tested yet.
	 */
	public void writeToDB(Context context) {
		ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(DBPlacesEntry.COLUMN_NAME_NAME, name);
		values.put(DBPlacesEntry.COLUMN_NAME_LATITUDE, this.getLatitude());
		values.put(DBPlacesEntry.COLUMN_NAME_LONGITUDE, this.getLongitude());

		if (id == -1) { // a new item is created
			id = (int) db.insert(DBPlacesEntry.TABLE_NAME, null, values); // insert to db and get ID

			// update id in allEntries
			allLocations.remove(-1);
			allLocations.put(id, this);
		} else { // item is updated
			db.update(DBPlacesEntry.TABLE_NAME, values,
					DBPlacesEntry._ID + " = " + id, null);
		}
		db.close();

	}
	
	public void addUsedIn(ToDoEntry newTask) {
		tasks.add(newTask);
	}

	public String getName() {
		return name;
	}

	public HashSet<ToDoEntry> getTasks() {
		return tasks;
	}
	
	public LatLng getLatLng(){
		return new LatLng(this.getLatitude(),this.getLongitude());
	}

}
