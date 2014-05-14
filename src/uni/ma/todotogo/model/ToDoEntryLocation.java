/**
 * 
 */
package uni.ma.todotogo.model;

import java.util.HashSet;

import uni.ma.todotogo.controler.ToDoDbHelper;
import uni.ma.todotogo.model.ToDoContract.DBPlacesEntry;
import uni.ma.todotogo.model.ToDoContract.DBToDoEntry;
import uni.ma.todotogo.model.ToDoContract.DBToDoPlacesEntry;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author simon.seiter
 * 
 */
public class ToDoEntryLocation {

	Integer id;
	ToDoEntry entry;
	ToDoLocation location;
	
	public ToDoEntryLocation(int id){
		this.id = id;
	}

	/**
	 * 
	 */
	public ToDoEntryLocation(int id, ToDoEntry entry, ToDoLocation location) {
		this.id = id;
		this.entry = entry;
		this.location = location;
	}

	public ToDoEntryLocation(ToDoEntry entry, ToDoLocation location) {
		this.entry = entry;
		this.location = location;
	}
	
	
	
	public static ToDoEntryLocation getToDoEntryLocationFromDB(int id, Context context){
		String[] selectionArgs = {""+id};
		String selection = DBToDoPlacesEntry._ID +" =?";
		Cursor cursor = getMappingCursor(context, selection, selectionArgs);
		if(cursor.getCount()<1){
			return new ToDoEntryLocation(-1);
		}
		return getCurrentObjectFromCursor(cursor, context);
	}
	
	public static ToDoEntryLocation getToDoEntryLocationByEntryLocationFromDB(ToDoEntry entry, ToDoLocation location, Context context){
		String[] selectionArgs = {""+location.id, ""+entry.id};
		String[] selection = {DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID, DBToDoPlacesEntry.COLUMN_NAME_TODO_ID}; 
		Cursor cursor = getMappingCursor(context, createQueryColumns(selection), selectionArgs);
		if(cursor.getCount()<1){
			return new ToDoEntryLocation(-1);
		}
		return getCurrentObjectFromCursor(cursor, context);
	}
	
	
	public static HashSet<ToDoEntry> getConnectedEntries(ToDoLocation location,
			Context context) {
		HashSet<ToDoEntry> connectedEntries = new HashSet<ToDoEntry>();
		String selection = DBToDoPlacesEntry.COLUMN_NAME_TODO_ID + " =?";
		String[] selectionArgs = { String.valueOf(location.id) };
		Cursor cursor = getMappingCursor(context, selection, selectionArgs);
		if(cursor.getCount()<1){
			return new HashSet<ToDoEntry>();
		}
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			connectedEntries
					.add(getCurrentObjectFromCursor(cursor, context).entry);
		}
		return connectedEntries;
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
		if(cursor.getCount()<1){
			return new ToDoEntryLocation(-1);
		}
		int id = cursor.getInt(cursor
				.getColumnIndex(DBToDoPlacesEntry._ID));
		int entryID = Integer.parseInt(cursor.getString(cursor
				.getColumnIndex(DBToDoPlacesEntry.COLUMN_NAME_TODO_ID)));
		int locationID = Integer
				.parseInt(cursor.getString(cursor
						.getColumnIndex(DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID)));
		ToDoEntry entry = ToDoEntry.getToDoEntryFromDB(entryID, context);
		ToDoLocation location = ToDoLocation.getToDoLocationFromDB(locationID,
				context);
		// create object
		return new ToDoEntryLocation(id, entry, location);
	}
	
	public static int updateMapping(int entryID, int locationID, Context context){
		ToDoEntry entry = ToDoEntry.getToDoEntryFromDB(entryID, context);
		ToDoLocation location = ToDoLocation.getToDoLocationFromDB(locationID, context);
		ToDoEntryLocation buffer = getToDoEntryLocationByEntryLocationFromDB(entry, location, context);
		if(buffer != null){
			return buffer.writeToDB(context);
		}
		else{
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
		String selection = DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID + " =?";
		String[] selectionArgs = { String.valueOf(entry.id) };
		Cursor cursor = getMappingCursor(context, selection, selectionArgs);
		if(cursor.getCount()<1){
			return new HashSet<ToDoLocation>();
		}
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			connectedLocations
					.add(getCurrentObjectFromCursor(cursor, context).location);
		}
		return connectedLocations;
	}
	
	public int writeToDB(Context context){		
		ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		String[] projection = { DBToDoPlacesEntry._ID,
				DBToDoPlacesEntry.COLUMN_NAME_TODO_ID,
				DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID };
		String[] selectionArgs = {id+""};
		Cursor cursor = db.query(DBToDoPlacesEntry.TABLE_NAME, projection, DBToDoPlacesEntry._ID+"=?",
				selectionArgs, null, null, null);
		if(cursor.getCount()<1){
			return 0;
		}
		ToDoEntryLocation buffer = getCurrentObjectFromCursor(cursor, context);
		
		ContentValues values = new ContentValues();
		values.put(DBToDoPlacesEntry._ID, id);
		values.put(DBToDoPlacesEntry.COLUMN_NAME_TODO_ID,
				String.valueOf(this.entry.id));
		values.put(DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID,
				String.valueOf(this.location.id));
		
		if (buffer.id<0) { // a new item is created
			id = (int) db.insert(DBPlacesEntry.TABLE_NAME, null, values);
			return id;
		} else { // item is updated
			return db.update(DBPlacesEntry.TABLE_NAME, values, DBPlacesEntry._ID
					+ " = " + id, null);
		}
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
					DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID};
			// Log.d("DB Location", db.query(false,
			// DBPlacesEntry.TABLE_NAME,projection, selection, selectionArgs, null,
			// null, null, null).getCount() + " items found");
			int id = db.delete(DBToDoPlacesEntry.TABLE_NAME, selection, selectionArgs);
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
			return deleteBySelection(context, selection, selectionArgs);
		}
		
		public static int staticDeleteByBothIDs(int entryID, int locationID,
				 Context context) {
			//DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
			//dfs.setDecimalSeparator('.');
			//DecimalFormat numberFormat = new DecimalFormat("#.####", dfs);

			String[] buffer = { DBToDoPlacesEntry.COLUMN_NAME_TODO_ID,
					DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID,};
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
		
		public static int sizeOfMappings(Context context){
			return getMappingCursor(context, null, null).getCount();
		}
		

}
