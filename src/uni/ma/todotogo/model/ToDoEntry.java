package uni.ma.todotogo.model;

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
import uni.ma.todotogo.controler.ToDoDbHelper;
import uni.ma.todotogo.model.ToDoContract.DBPlacesEntry;
import uni.ma.todotogo.model.ToDoContract.DBToDoEntry;
import uni.ma.todotogo.model.ToDoContract.DBToDoPlacesEntry;

public class ToDoEntry {
	int id;
	public String name;
	public AvailableColors category;
	GregorianCalendar date;
	static HashSet<ToDoLocation> locations;

	/**
	 * Returns a list with all entries stored in the db.
	 * 
	 * @return
	 */
	public static HashSet<ToDoEntry> getAllEntries(Context context) {

		HashSet<ToDoEntry> allEntries = new HashSet<ToDoEntry>();
		ToDoEntry buffer;

		Cursor cursorToDoEntry = getCursor(context, null, null);
		cursorToDoEntry.moveToFirst();

		while (!cursorToDoEntry.isAfterLast()) {
			buffer = getCurrentObjectFromCursor(cursorToDoEntry);
			buffer.setLocationsFromDB(context);
			allEntries.add(buffer);
			cursorToDoEntry.moveToNext();
		}
		cursorToDoEntry.close();
		return allEntries;
	}
	
	public ToDoEntry(int id){
		this.id = id; 
	}

	public static int staticDelete(int idToBeDeleted, Context context) {
		ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		int success = db.delete(DBToDoEntry.TABLE_NAME, DBToDoEntry._ID + "="
				+ String.valueOf(idToBeDeleted), null);
		Log.d("ToDoEntry", "ListItem with ID " + idToBeDeleted
				+ " was deleted.");
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
	 * @param date
	 *            Time in milliseconds from the epoch.
	 */
	public ToDoEntry(int id, String name, String category, String date) {
		this.id = id;
		this.name = name;
		this.category = AvailableColors.getColorByInt(Integer.parseInt(category));
		this.locations = new HashSet<ToDoLocation>();

		GregorianCalendar date2 = new GregorianCalendar();
		date2.setTimeInMillis(Long.parseLong(date));
		this.date = date2;
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
	}


	/**
	 * Calculates the distance from <code>distTo</code> to the closest Location
	 * stored in <code>locations</code>.
	 * 
	 * @param distTo
	 *            Location of which the shortest distance shall be calculated.
	 * @return Shortest distance.
	 */
	public ToDoLocation getClosestLocationTo(Location distTo) {
		Iterator<ToDoLocation> iter = locations.iterator();
		float closestDist = Float.POSITIVE_INFINITY;
		ToDoLocation result = null;
		
		while (iter.hasNext()) {
			ToDoLocation curItem = iter.next();
			float curDist = distTo.distanceTo(curItem);
			if (curDist < closestDist) {
				closestDist = curDist;
				result = curItem;
			}
		}

		return result;
	}
	
	/**
	 * Calculates the distance from <code>distTo</code> to the closest Location
	 * stored in <code>locations</code>.
	 * 
	 * @param distTo
	 *            Location of which the shortest distance shall be calculated.
	 * @return Shortest distance.
	 */
	public float getClosestDistanceTo(Location distTo) {
		Log.d("Entry", "ConnectedLocations: "+locations.size());
		Iterator<ToDoLocation> iter = locations.iterator();
		float closestDist = Float.POSITIVE_INFINITY;
		while (iter.hasNext()) {
			ToDoLocation curItem = iter.next();
			float curDist = distTo.distanceTo(curItem);
			if (curDist < closestDist) {
				closestDist = curDist;
			}
		}
		Log.d("Entry", "Closest distance to "+this.name+" is "+closestDist);
		return closestDist;
	}

	
	/**
	 * Writes content to Database. If <code>id</code> is <code>-1</code> a new
	 * item is created. Update not tested yet. Also adds <code>locations</code>
	 * -mappings into DB.
	 */
	public int writeToDB(Context context) {
		ToDoEntry proof = getToDoEntryFromDB(this.id, context);
		
		ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		ContentValues valuesToDoEntry = new ContentValues();

		valuesToDoEntry.put(DBToDoEntry.COLUMN_NAME_NAME, name);
		valuesToDoEntry.put(DBToDoEntry.COLUMN_NAME_CATEGORY,
				category.getColor());
		valuesToDoEntry.put(DBToDoEntry.COLUMN_NAME_DATE,
				date.getTimeInMillis());

		if (proof.id<0) { // a new item is created
			id = (int) db.insert(DBToDoEntry.TABLE_NAME, null, valuesToDoEntry); // insert
																					// to
																					// db
																					// and
																					// get
																					// ID

			Log.d("ToDoEntry", "new ID is " + id);
			return id;
			// update id in allEntries
		} else { // item is updated
			id = db.update(DBToDoEntry.TABLE_NAME, valuesToDoEntry, DBToDoEntry._ID
					+ " = " + id, null);
			return id;
		}

		/*
		// add mappings of todo/places
		Iterator<ToDoLocation> locIter = locations.iterator();
		while (locIter.hasNext()) {
			ToDoLocation curLoc = locIter.next();
			ContentValues valuesEntryLocation = new ContentValues();

			valuesEntryLocation.put(DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID,
					curLoc.getId());
			valuesEntryLocation.put(DBToDoPlacesEntry.COLUMN_NAME_TODO_ID,
					this.id);

			db.insert(DBToDoPlacesEntry.TABLE_NAME, null, valuesEntryLocation);
		}*/

		
	}

	/**
	 * returns a single entry from the DB chosen by ID
	 * 
	 * @param id
	 * @param context
	 * @return
	 */
	public static ToDoEntry getToDoEntryFromDB(int entryID, Context context) {
		String selection = DBToDoEntry._ID + " =?";
		String[] selectionArgs = { String.valueOf(entryID) };
		return getCurrentObjectFromCursor(getCursor(context, selection,
				selectionArgs));
	}
	
	/**
	 * returns a single location from where the cursor points at the DB
	 * 
	 * @param cursor
	 * @return
	 */
	public static ToDoEntry getCurrentObjectFromCursor(Cursor cursor) {
		if(cursor.getCount()<1){
			return new ToDoEntry(-1);
		}
		
		int id = cursor.getInt(cursor.getColumnIndex(DBToDoEntry._ID));
		String name = cursor.getString(cursor
				.getColumnIndex(DBToDoEntry.COLUMN_NAME_NAME));
		String category = cursor.getString(cursor
				.getColumnIndex(DBToDoEntry.COLUMN_NAME_CATEGORY));
		String date = cursor.getString(cursor
				.getColumnIndex(DBToDoEntry.COLUMN_NAME_DATE));
		return new ToDoEntry(id, name, category, date);
	}

	/**
	 * returns a cursor for a specific query with where=selection and
	 * whereargs=selectionargs
	 * 
	 * @param context
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public static Cursor getCursor(Context context, String selection,
			String[] selectionArgs) {
		ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		String[] projection = { DBToDoEntry._ID, DBToDoEntry.COLUMN_NAME_NAME,
				DBToDoEntry.COLUMN_NAME_CATEGORY, DBToDoEntry.COLUMN_NAME_DATE };
		return db.query(DBToDoEntry.TABLE_NAME, projection, selection,
				selectionArgs, null, null, null);
	}

	public int delete(Context context) {
		ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		int success = db.delete(DBToDoEntry.TABLE_NAME, DBToDoEntry._ID + "="
				+ String.valueOf(id), null);
		Log.d("ToDoEntry", "ListItem with ID " + this.id + " was deleted.");
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

	public HashSet<ToDoLocation> getLocations() {
		return locations;
	}
	
	public void setLocations(HashSet<ToDoLocation> locations){
		this.locations = locations;
	}
	
	public void setLocationsFromDB(Context context){
		setLocations(ToDoEntryLocation.getConnectedLocations(this, context));
	}
	
	public void connectWithAllLocations(Context context){
		HashSet<ToDoLocation> allLocations = ToDoLocation.getAllEntries(context);
		for (ToDoLocation test:allLocations){
			locations.add(test);
			ToDoEntryLocation buffer = new ToDoEntryLocation(this, test);
			buffer.writeToDB(context);
		}
	}

}
