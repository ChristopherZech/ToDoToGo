/**
 * 
 */
package uni.ma.todotogo.model;

import java.util.HashSet;
import java.util.Iterator;

import uni.ma.todotogo.controler.ToDoDbHelper;
import uni.ma.todotogo.model.ToDoContract.DBPlacesEntry;
import uni.ma.todotogo.model.ToDoContract.DBToDoEntry;
import uni.ma.todotogo.model.ToDoContract.DBToDoPlacesEntry;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author simon.seiter
 * 
 */
public class ToDoEntryLocation {

	public Integer id;
	public ToDoEntry entry;
	public ToDoLocation location;
	public static HashSet<ToDoEntryLocation> allEntries;
	public boolean notified;

	public ToDoEntryLocation(int id) {
		this.id = id;
		notified = false;
	}
	
	public void setNotified(boolean value){
		this.notified = value;
	}

	/**
	 * 
	 */
	public ToDoEntryLocation(int id, ToDoEntry entry, ToDoLocation location) {
		this.id = id;
		this.entry = entry;
		this.location = location;
		this.notified = false;

	}

	public ToDoEntryLocation(ToDoEntry entry, ToDoLocation location) {
		this.entry = entry;
		this.location = location;
		this.id = -1;
		this.notified = false;
	}

	public static ToDoEntryLocation getToDoEntryLocationFromDB(int id,
			Context context) {
		String[] selectionArgs = { "" + id };
		String selection = DBToDoPlacesEntry._ID + " =?";
		Cursor cursor = getMappingCursor(context, selection, selectionArgs);
		if (cursor.getCount() < 1) {
			return new ToDoEntryLocation(-1);
		}
		cursor.moveToFirst();
		return getCurrentObjectFromCursor(cursor, context);
	}

	public static ToDoEntryLocation getToDoEntryLocationByEntryLocationFromDB(
			ToDoEntry entry, ToDoLocation location, Context context) {
		String[] selectionArgs = { "" + location.id, "" + entry.id };
		String[] selection = { DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID,
				DBToDoPlacesEntry.COLUMN_NAME_TODO_ID };
		Cursor cursor = getMappingCursor(context,
				createQueryColumns(selection), selectionArgs);
		if (cursor.getCount() < 1) {
			return new ToDoEntryLocation(-1);
		}
		cursor.moveToFirst();
		return getCurrentObjectFromCursor(cursor, context);
	}

	public static HashSet<ToDoEntry> getConnectedEntries(ToDoLocation location,
			Context context) {
		HashSet<ToDoEntry> connectedEntries = new HashSet<ToDoEntry>();
		String selection = DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID + " =?";
		String[] selectionArgs = { String.valueOf(location.id) };
		Cursor cursor = getMappingCursor(context, selection, selectionArgs);
		if (cursor.getCount() < 1) {
			return new HashSet<ToDoEntry>();
		}
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			connectedEntries
					.add(getCurrentObjectFromCursor(cursor, context).entry);
			cursor.moveToNext();
		}
		return connectedEntries;
	}
	
	public int delete(Context context) {
		ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		int success = db.delete(DBToDoPlacesEntry.TABLE_NAME, DBToDoPlacesEntry._ID + "="
				+ String.valueOf(id), null);
		//ProximityIntentReceiver.removeReceiverByEntryLocation(this, context);
		Log.d("ToDoEntryLocation", "Mapping with ID " + this.id + " was deleted.");
		return success;
	}
	

	/**
	 * returns a cursor for a query over the location-entry mapping with
	 * where=selection and whereargs=selectionargs
	 * 
	 * @param context
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public static Cursor getMappingCursor(Context context, String selection,
			String[] selectionArgs) {
		ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		String[] projection = { DBToDoPlacesEntry._ID,
				DBToDoPlacesEntry.COLUMN_NAME_TODO_ID,
				DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID };
		return db.query(DBToDoPlacesEntry.TABLE_NAME, projection, selection,
				selectionArgs, null, null, null);
	}

	/**
	 * returns a single location from where the cursor points at the DB
	 * 
	 * @param cursor
	 * @return
	 */
	public static ToDoEntryLocation getCurrentObjectFromCursor(Cursor cursor,
			Context context) {
		if (cursor.getCount() < 1) {
			return new ToDoEntryLocation(-1);
		}
		int id = cursor.getInt(cursor.getColumnIndex(DBToDoPlacesEntry._ID));
		int entryID = Integer.parseInt(cursor.getString(cursor
				.getColumnIndex(DBToDoPlacesEntry.COLUMN_NAME_TODO_ID)));
		int locationID = Integer.parseInt(cursor.getString(cursor
				.getColumnIndex(DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID)));
		ToDoEntry entry = ToDoEntry.getToDoEntryFromDB(entryID, context);
		ToDoLocation location = ToDoLocation.getToDoLocationFromDB(locationID,
				context);
		// create object
		return new ToDoEntryLocation(id, entry, location);
	}

	public static int updateMapping(int entryID, int locationID, Context context) {
		ToDoEntry entry = ToDoEntry.getToDoEntryFromDB(entryID, context);
		ToDoLocation location = ToDoLocation.getToDoLocationFromDB(locationID,
				context);
		ToDoEntryLocation buffer = getToDoEntryLocationByEntryLocationFromDB(
				entry, location, context);
		if (buffer != null) {
			return buffer.writeToDB(context);
		} else {
			return (Integer) null;
		}
	}

	/**
	 * @param entry2
	 * @param context
	 * @return
	 */
	public static HashSet<ToDoLocation> getConnectedLocations(ToDoEntry entry,
			Context context) {
		HashSet<ToDoLocation> connectedLocations = new HashSet<ToDoLocation>();
		String selection = DBToDoPlacesEntry.COLUMN_NAME_TODO_ID + " =?";
		String[] selectionArgs = { String.valueOf(entry.id) };
		Cursor cursor = getMappingCursor(context, selection, selectionArgs);
		ToDoEntryLocation buffer;
		if (cursor.getCount() < 1) {
			return new HashSet<ToDoLocation>();
		}
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			buffer = getCurrentObjectFromCursor(cursor, context);
			Log.d("ToDoEntryLocation",
					"Buffered a local mapping" + buffer.location.toString());
			connectedLocations.add(buffer.location);
			cursor.moveToNext();
		}
		return connectedLocations;
	}

	public int writeToDB(Context context) {
		// ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
		// SQLiteDatabase db = mDbHelper.getWritableDatabase();
		// String[] projection = { DBToDoPlacesEntry._ID,
		// DBToDoPlacesEntry.COLUMN_NAME_TODO_ID,
		// DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID };
		// String[] selectionArgs = {id+""};
		// Cursor cursor = db.query(DBToDoPlacesEntry.TABLE_NAME, projection,
		// DBToDoPlacesEntry._ID+"=?",
		// selectionArgs, null, null, null);
		// if(cursor.getCount()<1){
		// return 0;
		// }
		// ToDoEntryLocation buffer = getCurrentObjectFromCursor(cursor,
		// context);
		//
		// ContentValues values = new ContentValues();
		// values.put(DBToDoPlacesEntry._ID, id);
		// values.put(DBToDoPlacesEntry.COLUMN_NAME_TODO_ID,
		// String.valueOf(this.entry.id));
		// values.put(DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID,
		// String.valueOf(this.location.id));
		//
		// if (buffer.id<0) { // a new item is created
		// id = (int) db.insert(DBPlacesEntry.TABLE_NAME, null, values);
		// return id;
		// } else { // item is updated
		// return db.update(DBPlacesEntry.TABLE_NAME, values, DBPlacesEntry._ID
		// + " = " + id, null);
		// }

		ToDoEntryLocation proof = getToDoEntryLocationFromDB(this.id, context);
		int result = 0;
		ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(DBToDoPlacesEntry.COLUMN_NAME_TODO_ID,
				String.valueOf(this.entry.id));
		values.put(DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID,
				String.valueOf(this.location.id));
		Log.d("ToDoEntryLocation", "Values have been put: " + values.toString());

		if (proof.id < 0) {
			result = (int) db
					.insert(DBToDoPlacesEntry.TABLE_NAME, null, values);
			id = result;
		} else {
			result = db.update(DBToDoPlacesEntry.TABLE_NAME, values,
					DBPlacesEntry._ID + " = " + id, null);
		}
		db.close();
		return result;

		// this.entry.setLocations(getConnectedLocations(this.entry, context));
		// this.location.setEntries(getConnectedEntries(this.location,context));

	}

	// DELETERS
	/**
	 * deletes a specific entry that is selected by selection columsn and
	 * selectionArgs arguments
	 * 
	 * @param context
	 * @param selection
	 * @param selectionArgs
	 * @return
	 */
	public static int deleteBySelection(Context context, String selection,
			String[] selectionArgs) {
		ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		String[] projection = { DBToDoPlacesEntry._ID,
				DBToDoPlacesEntry.COLUMN_NAME_TODO_ID,
				DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID };
		// Log.d("DB Location", db.query(false,
		// DBPlacesEntry.TABLE_NAME,projection, selection, selectionArgs, null,
		// null, null, null).getCount() + " items found");
		int id = db.delete(DBToDoPlacesEntry.TABLE_NAME, selection,
				selectionArgs);
		// allLocations.remove(id);
		db.close();
		mDbHelper.close();
		Log.d("DB Location", "Delted ID:" + id);
		return id;
	}

	/**
	 * deletes a location from the DB with specific ID
	 * 
	 * statically deletes a location ouf of the DB with a
	 * 
	 * @param idToBeDeleted
	 * @param context
	 * @return
	 */
	public static int staticDeleteByID(int idToBeDeleted, Context context) {
		String[] buffer = { DBToDoPlacesEntry._ID };
		String selection = createQueryColumns(buffer);
		String[] selectionArgs = { String.valueOf(idToBeDeleted) };
		//ProximityIntentReceiver.removeReceiverByEntryLocationID(idToBeDeleted, context);
		return deleteBySelection(context, selection, selectionArgs);
	}

	public static int staticDeleteByBothIDs(int entryID, int locationID,
			Context context) {
		// DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
		// dfs.setDecimalSeparator('.');
		// DecimalFormat numberFormat = new DecimalFormat("#.####", dfs);

		String[] buffer = { DBToDoPlacesEntry.COLUMN_NAME_TODO_ID,
				DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID, };
		String[] selectionArgs = { String.valueOf(entryID),
				String.valueOf(locationID) };// numberFormat.format(lat),
		// numberFormat.format(lng)};
		String selection = createQueryColumns(buffer);
		// String selection= DBPlacesEntry.COLUMN_NAME_NAME + " =? AND " +
		// DBPlacesEntry.COLUMN_NAME_LATITUDE + " =? AND " +
		// DBPlacesEntry.COLUMN_NAME_LONGITUDE + " =?";
		Log.d("DB", selectionArgs[0] + selectionArgs[1]);
		
		return deleteBySelection(context, selection, selectionArgs);
	}

	public static int staticDeleteByEntryID(int entryID, Context context) {
		String[] buffer = { DBToDoPlacesEntry.COLUMN_NAME_TODO_ID };
		String[] selectionArgs = { String.valueOf(entryID) };// numberFormat.format(lat),
		// numberFormat.format(lng)};
		String selection = createQueryColumns(buffer);
		return deleteBySelection(context, selection, selectionArgs);
	}

	public static int staticDeleteByLocationID(int locationID, Context context) {
		String[] buffer = { DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID };
		String[] selectionArgs = { String.valueOf(locationID) };// numberFormat.format(lat),
		// numberFormat.format(lng)};
		String selection = createQueryColumns(buffer);
		return deleteBySelection(context, selection, selectionArgs);
	}

	/**
	 * Creates a query string used for db.query from columns string[] where
	 * arguments are left as ?
	 * 
	 * @param columns
	 * @return
	 */
	public static String createQueryColumns(String[] columns) {
		String result = "";
		for (int i = 0; i < (columns.length - 1); ++i) {
			result += columns[i] + " =? AND ";
		}
		result += columns[columns.length - 1] + " =?";
		return result;
	}

	public static int sizeOfMappings(Context context) {
		return getMappingCursor(context, null, null).getCount();
	}

//	/**
//	 * Registers proximity alerts to all mapped locations.
//	 */
//	public void registerProximityAlert(Context context) {
//		GPSTracker gps = new GPSTracker(context);
//		gps.getLocation();
//		LocationManager locationManager = GPSTracker.getLocationManager();
//
//		// get distance threshold for notification from preferences
//		SharedPreferences sharedPref = PreferenceManager
//				.getDefaultSharedPreferences(context);
//		int distToNotify = sharedPref.getInt("pref_distance", 100);
//
//		Intent intent = new Intent("uni.ma.todotogo.model.ProximityAlert");
//		PendingIntent proximityIntent = PendingIntent.getBroadcast(context, 0,
//				intent, 0);
//		locationManager.addProximityAlert(location.getLatitude(), // the latitude
//																// of the
//																// central point
//																// of the alert
//																// region
//				location.getLongitude(), // the longitude of the central point of
//										// the alert region
//				distToNotify, // the radius of the central point of the alert
//								// region, in meters
//				-1, // time for this proximity alert, in milliseconds, or -1 to
//					// indicate no expiration
//				proximityIntent // will be used to generate an Intent to fire
//								// when entry to or exit from the alert region
//								// is detected
//				);
//		//IntentFilter filter = new IntentFilter("uni.ma.todotogo.model.ProximityAlert"); 
//		//context.registerReceiver(new ProximityIntentReceiver(this, proximityIntent), filter);
//		Log.d("ToDoEntryLocation", "registered ProximityIntentReceiver for todo: "+entry.getName()+" | loc: "+location.getName());
//	}
	
	public static void setAllEntries(Context context){
		Log.d("ToDoEntryLocation","setAllEntries");
		if(!(allEntries == null))allEntries.clear();
		allEntries = getAllEntries(context);
	}
	
	/**
	 * Returns a list with all entries stored in the db.
	 * 
	 * @return
	 */
	public static HashSet<ToDoEntryLocation> getAllEntries(Context context) {

		HashSet<ToDoEntryLocation> allEntries = new HashSet<ToDoEntryLocation>();
		ToDoEntryLocation entryBuffer;

		Cursor cursorToDoEntry = getMappingCursor(context, null, null);
		cursorToDoEntry.moveToFirst();

		while (!cursorToDoEntry.isAfterLast()) {
			entryBuffer = getCurrentObjectFromCursor(cursorToDoEntry, context);
			//entryBuffer.setLocationsFromDB(context);
			Log.d("ToDoEntryLocation", "Entry in all entries:"+ entryBuffer.id+" - entry: "+entryBuffer.entry.name+" - location: "+entryBuffer.location.getName());
			allEntries.add(entryBuffer);
			cursorToDoEntry.moveToNext();
		}
		cursorToDoEntry.close();
		return allEntries;
	}
	
//	public static void startAllReceivers(Context context){
//		HashSet<ToDoEntryLocation> allEntries = getAllEntries(context);
//		for(ToDoEntryLocation entry: allEntries){
//			entry.registerProximityAlert(context);
//		}
//	}
	
}
