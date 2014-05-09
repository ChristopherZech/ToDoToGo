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
	private String markerID;

	//private static HashMap<Integer, ToDoLocation> allLocations = new HashMap<Integer, ToDoLocation>();

	public String toString() {
		return "Location name: " + name + "; Lat: " + this.getLatitude()
				+ "; Long: " + this.getLongitude() + ";#connected tasks: "
				+ tasks.size();
	}


	/**
	 * Returns a list with all locataions stored in the db.
	 * 
	 * @return
	 */
	public static HashSet<ToDoLocation> getAllEntries(Context context) {
		//if (allLocations.isEmpty()) {
			// fill list with entries
			HashSet<ToDoLocation> locations = new HashSet<ToDoLocation>();
			ToDoLocation buffer;
			Cursor cursor = getCursor(context, null, null);
			cursor.moveToFirst();

			while (!cursor.isAfterLast()) {
				buffer = getCurrentObjectFromCursor(cursor);
				Log.d("DB", buffer.toString());
				locations.add(buffer);
				cursor.moveToNext();
			}
			return locations;
		//}
		//return allLocations;
	}

	public ToDoLocation(int id, String name, double latitude, double longitude,
			String markerID) {
		this(id, name, latitude, longitude, markerID, new HashSet<ToDoEntry>());
	}

	public ToDoLocation(int id, String name, double latitude, double longitude,
			String markerID, HashSet<ToDoEntry> tasks) {
		super("none");

		this.id = id;
		this.name = name;
		this.tasks = tasks;
		this.markerID = markerID;
		this.setLatitude(latitude);
		this.setLongitude(longitude);

		//allLocations.put(id, this);
	}

	public static ToDoLocation getToDoLocationFromDB(int id, Context context) {
		String selection = DBPlacesEntry._ID + " =?";
		String[] selectionArgs = {String.valueOf(id)};
		return getCurrentObjectFromCursor(getCursor(context, selection, selectionArgs));
	}

	public static ToDoLocation getLocationByNameAndLatLng(String name,
			double lat, double lng, Context context) {
		String selection = DBPlacesEntry.COLUMN_NAME_NAME + "= ? AND"
				+ DBPlacesEntry.COLUMN_NAME_LATITUDE + "= ? AND"
				+ DBPlacesEntry.COLUMN_NAME_LONGITUDE + "=?";
		String[] selectionArgs = {name, String.valueOf(lat), String.valueOf(lng)};

		Cursor cursor = getCursor(context, selection, selectionArgs);
		cursor.moveToFirst();
		return getCurrentObjectFromCursor(cursor);
	}

	public static Cursor getCursor(Context context, String selection,
			String[] selectionArgs) {
		ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		String[] projection = { DBPlacesEntry._ID,
				DBPlacesEntry.COLUMN_NAME_NAME,
				DBPlacesEntry.COLUMN_NAME_LATITUDE,
				DBPlacesEntry.COLUMN_NAME_LONGITUDE,
				DBPlacesEntry.COLUMN_NAME_MARKER };
		return db.query(DBPlacesEntry.TABLE_NAME, projection, selection,
				selectionArgs, null, null, null);
	}
	
	
	
	public static ToDoLocation getCurrentObjectFromCursor(Cursor cursor){
		int id = cursor.getInt(cursor
				.getColumnIndexOrThrow(DBPlacesEntry._ID));
		String name = cursor.getString(cursor
				.getColumnIndexOrThrow(DBPlacesEntry.COLUMN_NAME_NAME));
		double latitude = cursor
				.getFloat(cursor
						.getColumnIndexOrThrow(DBPlacesEntry.COLUMN_NAME_LATITUDE));
		double longitude = cursor
				.getFloat(cursor
						.getColumnIndexOrThrow(DBPlacesEntry.COLUMN_NAME_LONGITUDE));
		String markerId = cursor
				.getString(cursor
						.getColumnIndexOrThrow(DBPlacesEntry.COLUMN_NAME_MARKER));

		// create object
		return new ToDoLocation(id, name, latitude, longitude, markerId); // (automatically
																	// gets
																	// stored
																	// in
																	// the
																	// HashMap)
		
	}

	public static ToDoLocation getToDoLocationByMarkerFromDB(String markerID,
			Context context) {
		String selection = DBPlacesEntry.COLUMN_NAME_MARKER + "=?";
		String[] selectionArgs = {markerID};
		Cursor c = getCursor(context, selection, selectionArgs);
		c.moveToFirst();
		return getCurrentObjectFromCursor(c);
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
			
			// insert
			// to
			// db
			// and
			// get
			// ID
			id = (int) db.insert(DBPlacesEntry.TABLE_NAME, null, values); 


			// update id in allEntries
			//allLocations.remove(-1);
			//allLocations.put(id, this);
		} else { // item is updated
			db.update(DBPlacesEntry.TABLE_NAME, values, DBPlacesEntry._ID
					+ " = " + id, null);
		}
		db.close();

	}
	
	
	//DELETERS
		public static int deleteBySelection(Context context, String selection, String[] selectionArgs){
			ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
			SQLiteDatabase db = mDbHelper.getReadableDatabase();
			
			String[] projection = { DBPlacesEntry._ID,
					DBPlacesEntry.COLUMN_NAME_NAME,
					DBPlacesEntry.COLUMN_NAME_LATITUDE,
					DBPlacesEntry.COLUMN_NAME_LONGITUDE,
					DBPlacesEntry.COLUMN_NAME_MARKER };
			
			//Log.d("DB Location", db.query(false, DBPlacesEntry.TABLE_NAME,projection, selection, selectionArgs, null, null, null, null).getCount() + " items found");
			int id =  db.delete(DBPlacesEntry.TABLE_NAME, selection, selectionArgs);
			//allLocations.remove(id);
			db.close();
			mDbHelper.close();
			Log.d("DB Location", "Delted ID:"+id);
			return id;
		}
		
		public static int staticDeleteByString (String stringToBeDeleted,
				Context context) {
			String[] buffer = {DBPlacesEntry.COLUMN_NAME_NAME};
			String selection = createQueryColumns(buffer);
			String[] selectionArgs =	new String[] { stringToBeDeleted };
			return deleteBySelection(context, selection, selectionArgs);
		}
		
		

		public static int staticDeleteByMarker(String markerID, Context context) {
			String[] buffer = {DBPlacesEntry.COLUMN_NAME_MARKER};
			String selection = createQueryColumns(buffer);
			String[] selectionArgs = new String[] { markerID };
			return deleteBySelection(context, selection, selectionArgs);
		}

		public static int staticDeleteByID(int idToBeDeleted, Context context) {
			String[] buffer = {DBPlacesEntry._ID};
			String selection = createQueryColumns(buffer);
			String[] selectionArgs = {String.valueOf(idToBeDeleted)};
			return deleteBySelection(context, selection, selectionArgs);
		}
		
		public static int staticDeleteByNameLatLng(String name, double lat, double lng, Context context) {
			String[] buffer = {DBPlacesEntry.COLUMN_NAME_NAME,
					DBPlacesEntry.COLUMN_NAME_LATITUDE,
					DBPlacesEntry.COLUMN_NAME_LONGITUDE};
			// = createQueryColumns(buffer);
				String selection= DBPlacesEntry.COLUMN_NAME_NAME + " =? AND " + 
					DBPlacesEntry.COLUMN_NAME_LATITUDE + " =? AND " + 
					DBPlacesEntry.COLUMN_NAME_LONGITUDE + " =?";
			String[] selectionArgs = {name, String.valueOf(lat), String.valueOf(lng)};
			return deleteBySelection(context, selection, selectionArgs);
		}
		
	public static String createQueryColumns(String[] columns){
		String result = "";
		for (int i =  0;  i < (columns.length-1); ++i){
			result += columns[i]+" =? AND";
		}
		result += columns[columns.length-1] + " =?";
		return result;
	}

	public void addUsedIn(ToDoEntry newTask) {
		tasks.add(newTask);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void addToDoEntry(ToDoEntry newToDoEntry) {
		tasks.add(newToDoEntry);
	}

	public HashSet<ToDoEntry> getTasks() {
		return tasks;
	}

	public LatLng getLatLng() {
		return new LatLng(this.getLatitude(), this.getLongitude());
	}

}
