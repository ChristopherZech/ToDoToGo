package uni.ma.todotogo;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;
import uni.ma.todotogo.ToDoContract.DBPlacesEntry;
import uni.ma.todotogo.ToDoContract.DBToDoEntry;
import uni.ma.todotogo.ToDoContract.DBToDoPlacesEntry;

public class ToDoEntry {
	int id;
	String name;
	AvailableColors category;
	GregorianCalendar date;
	static HashSet<ToDoLocation> locations;
	private static HashMap<Integer,ToDoEntry> allEntries = new HashMap<Integer, ToDoEntry>();

	/**
	 * Returns a list with all entries stored in the db.
	 * 
	 * @return
	 */
	public static HashMap<Integer, ToDoEntry> getAllEntries(Context context) {
		if(allEntries.isEmpty()) {
			// fill list with entries
			ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
			SQLiteDatabase db = mDbHelper.getReadableDatabase();

			// DB entries to get
			String[] projection = { DBToDoEntry._ID,
					DBToDoEntry.COLUMN_NAME_NAME,
					DBToDoEntry.COLUMN_NAME_CATEGORY,
					DBToDoEntry.COLUMN_NAME_DATE };

			Cursor cursorToDoEntry = db.query(DBToDoEntry.TABLE_NAME, projection, null, null, null, null, null );
			cursorToDoEntry.moveToFirst();

			while (!cursorToDoEntry.isAfterLast()) {
				// get data
				int id = cursorToDoEntry.getInt(cursorToDoEntry.getColumnIndexOrThrow(DBToDoEntry._ID));
				String name = cursorToDoEntry.getString(cursorToDoEntry
						.getColumnIndexOrThrow(DBToDoEntry.COLUMN_NAME_NAME));
				int category = cursorToDoEntry.getInt(cursorToDoEntry.getColumnIndexOrThrow(DBToDoEntry.COLUMN_NAME_CATEGORY));
				int date = cursorToDoEntry.getInt(cursorToDoEntry.getColumnIndexOrThrow(DBToDoEntry.COLUMN_NAME_DATE));
				// create object
				ToDoEntry newEntry = new ToDoEntry(id, name, category, date); // (automatically gets stored in the HashMap)
				
				// add locations to this todo
				newEntry.addLocationsFromDB(context);
				
				
				cursorToDoEntry.moveToNext();
			}
		}
		return allEntries;
	}
	
	/**
	 * Queries DB to find all matching locations and adds them to this object.
	 */
	public void addLocationsFromDB(Context context) {
		if(locations.isEmpty()) {
			ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
			SQLiteDatabase db = mDbHelper.getReadableDatabase();

			// DB entries to get
			String[] projection = { DBToDoPlacesEntry._ID,
					DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID,
					DBToDoPlacesEntry.COLUMN_NAME_TODO_ID };

			Cursor cursorToDoLocMappingEntry = db.query(DBToDoPlacesEntry.TABLE_NAME, projection, DBToDoPlacesEntry.COLUMN_NAME_TODO_ID + "='"+id+"'", null, null, null, null );
			cursorToDoLocMappingEntry.moveToFirst();

			while (!cursorToDoLocMappingEntry.isAfterLast()) {
				// get data
				int location_id = cursorToDoLocMappingEntry.getInt(cursorToDoLocMappingEntry.getColumnIndexOrThrow(DBToDoPlacesEntry.COLUMN_NAME_TODO_ID));
				// create object
				ToDoLocation newLoc = ToDoLocation.getToDoLocationFromDB(location_id,context);
				
				// add new location to this object
				this.addLocation(newLoc);
				// add this object to new location
				newLoc.addToDoEntry(this);
				
				cursorToDoLocMappingEntry.moveToNext();
			}
		}
	}
	
	public static int staticDelete(int idToBeDeleted, Context context){ 
		ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
		int success = db.delete(DBToDoEntry.TABLE_NAME, DBToDoEntry._ID + "=" + String.valueOf(idToBeDeleted), null);
		Log.d("ToDoEntry", "ListItem with ID "+idToBeDeleted+" was deleted.");
		allEntries.remove(idToBeDeleted);
		db.close();
		mDbHelper.close();
		return success;	
	}

	/**
	 * 
	 * @param id
	 *            ID in database. <code>-1</code> if does not have ID yet.
	 * @param name
	 * @param category
	 * @param date
	 */
	public ToDoEntry(int id, String name, AvailableColors category,
			GregorianCalendar date) {
		this(id, name, category, date, new HashSet<ToDoLocation>());
	}

	/**
	 * 
	 * @param id
	 *            ID in database. <code>-1</code> if does not have ID yet.
	 * @param name
	 * @param category
	 * @param date
	 */
	public ToDoEntry(int id, String name, int category, GregorianCalendar date) {
		this(id, name, AvailableColors.values()[category], date,
				new HashSet<ToDoLocation>());
	}

	/**
	 * 
	 * @param id
	 *            ID in database. <code>-1</code> if does not have ID yet.
	 * @param name
	 * @param category
	 * @param date Time in milliseconds from the epoch.
	 */
	public ToDoEntry(int id, String name, int category, int date) {
		this.id = id;
		this.name = name;
		this.category = AvailableColors.getColorByInt(category);
		this.locations = new HashSet<ToDoLocation>();
		
		GregorianCalendar date2 = new GregorianCalendar();
		date2.setTimeInMillis(date);
		this.date = date2;
		allEntries.put(id, this);
	}

	/**
	 * 
	 * @param id
	 *            ID in database. <code>-1</code> if does not have ID yet.
	 * @param name
	 * @param category
	 * @param date
	 * @param locations
	 */
	public ToDoEntry(int id, String name, AvailableColors category,
			GregorianCalendar date, HashSet<ToDoLocation> locations) {
		this.id = id;
		this.name = name;
		this.category = category;
		this.date = date;
		this.locations = locations;

		Log.d("ToDoEntry", "added entry with ID "+id);
		allEntries.put(id, this);
	}

	public void addLocation(ToDoLocation newLocation) {
		locations.add(newLocation);
	}

	/**
	 * Calculates the distance from <code>distTo</code> to the closest Location
	 * stored in <code>locations</code>.
	 * 
	 * @param distTo
	 *            Location of which the shortest distance shall be calculated.
	 * @return Shortest distance.
	 */
	public float getClosestLocationTo(Location distTo) {
		Iterator<ToDoLocation> iter = locations.iterator();
		float closestDist = Float.POSITIVE_INFINITY;

		while (iter.hasNext()) {
			ToDoLocation curItem = iter.next();
			float curDist = distTo.distanceTo(curItem);
			if (curDist < closestDist) {
				closestDist = curDist;
			}
		}

		return closestDist;
	}

	/**
	 * Writes content to Database. If <code>id</code> is <code>-1</code> a new
	 * item is created. Update not tested yet. Also adds <code>locations</code>-mappings into DB.
	 */
	public void writeToDB(Context context) {
		ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		ContentValues valuesToDoEntry = new ContentValues();

		valuesToDoEntry.put(DBToDoEntry.COLUMN_NAME_NAME, name);
		valuesToDoEntry.put(DBToDoEntry.COLUMN_NAME_CATEGORY, category.getColor());
		valuesToDoEntry.put(DBToDoEntry.COLUMN_NAME_DATE, date.getTimeInMillis());

		if (id == -1) { // a new item is created
			id = (int) db.insert(DBToDoEntry.TABLE_NAME, null, valuesToDoEntry); // insert to db and get ID


			Log.d("ToDoEntry", "new ID is "+id);
			// update id in allEntries
			allEntries.remove(-1);
			allEntries.put(id, this);
		} else { // item is updated
			db.update(DBToDoEntry.TABLE_NAME, valuesToDoEntry,
					DBToDoEntry._ID + " = " + id, null);
		}
		
		
		// add mappings of todo/places
		Iterator<ToDoLocation> locIter = locations.iterator();
		while(locIter.hasNext()) {
			ToDoLocation curLoc = locIter.next();
			ContentValues valuesEntryLocation = new ContentValues();
	
			valuesEntryLocation.put(DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID, curLoc.getId());
			valuesEntryLocation.put(DBToDoPlacesEntry.COLUMN_NAME_TODO_ID, this.id);
			
			db.insert(DBToDoPlacesEntry.TABLE_NAME, null, valuesEntryLocation);
		}
		
		
		db.close();
	}
	
	public int delete(Context context){
		ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
		int success = db.delete(DBToDoEntry.TABLE_NAME, DBToDoEntry._ID + "=" + String.valueOf(id), null);
		Log.d("ToDoEntry", "ListItem with ID "+this.id+" was deleted.");
		allEntries.remove(id);
		return success;
	}               

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public AvailableColors getCategory() {
		return category;
	}

	public GregorianCalendar getDate() {
		return date;
	}

	public static HashSet<ToDoLocation> getLocations() {
		return locations;
	}
}
