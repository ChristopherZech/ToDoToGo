package uni.ma.todotogo.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashSet;

import uni.ma.todotogo.controler.ToDoDbHelper;
import uni.ma.todotogo.model.ToDoContract.DBPlacesEntry;

import com.google.android.gms.maps.model.LatLng;

/**
 * @author simon.seiter
 * @author chris.zech
 * 
 */
public class ToDoLocation extends Location {
	int id;
	private String name;
	private HashSet<ToDoEntry> tasks;
	private String markerID;

	
	
	public ToDoLocation(int id, String name){
		super(name);
		this.id = id;
	}
	
	// private static HashMap<Integer, ToDoLocation> allLocations = new
	// HashMap<Integer, ToDoLocation>();

	
	public String toString() {
		return "#" + id + "; name: " + name + "; Lat: " + this.getLatitude()
				+ "; Long: " + this.getLongitude() + "; #tasks: "
				+ tasks.size();
	}

	/**
	 * Returns a list with all locataions stored in the db.
	 * 
	 * @return
	 */
	public static HashSet<ToDoLocation> getAllEntries(Context context) {
		HashSet<ToDoLocation> locations = new HashSet<ToDoLocation>();
		ToDoLocation buffer;

		Cursor cursor = getCursor(context, null, null);
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			buffer = getCurrentObjectFromCursor(cursor);
			locations.add(buffer);
			cursor.moveToNext();
		}
		return locations;
	}

	/**
	 * @param id
	 * @param name
	 * @param latitude
	 * @param longitude
	 * @param markerID
	 */
	public ToDoLocation(int id, String name, double latitude, double longitude,
			String markerID) {
		this(id, name, latitude, longitude, markerID, new HashSet<ToDoEntry>());
	}

	/**
	 * @param id
	 * @param name
	 * @param latitude
	 * @param longitude
	 * @param markerID
	 * @param tasks
	 */
	public ToDoLocation(int id, String name, double latitude, double longitude,
			String markerID, HashSet<ToDoEntry> tasks) {
		super("none");

		this.id = id;
		this.name = name;
		this.tasks = tasks;
		this.markerID = markerID;
		this.setLatitude(latitude);
		this.setLongitude(longitude);

		// allLocations.put(id, this);
	}

	/**
	 * returns a single location from the DB chosen by ID
	 * 
	 * @param id
	 * @param context
	 * @return
	 */
	public static ToDoLocation getToDoLocationFromDB(int id, Context context) {
		String selection = DBPlacesEntry._ID + " =?";
		String[] selectionArgs = { String.valueOf(id) };
		return getCurrentObjectFromCursor(getCursor(context, selection,
				selectionArgs));
	}

	/**
	 * returns a single location from the DB chosen by name, latitude and
	 * longitude
	 * 
	 * @param name
	 * @param lat
	 * @param lng
	 * @param context
	 * @return
	 */
	public static ToDoLocation getLocationByNameAndLatLng(String name,
			double lat, double lng, Context context) {
		//DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
		//dfs.setDecimalSeparator('.');
		//DecimalFormat numberFormat = new DecimalFormat("#.####", dfs);

		String selection = DBPlacesEntry.COLUMN_NAME_NAME + "= ? AND "
				+ DBPlacesEntry.COLUMN_NAME_LATITUDE + "= ? AND "
				+ DBPlacesEntry.COLUMN_NAME_LONGITUDE + "=?";
		String[] selectionArgs = { name, String.valueOf(lat),
				String.valueOf(lng) };

		Cursor cursor = getCursor(context, selection, selectionArgs);
		cursor.moveToFirst();
		return getCurrentObjectFromCursor(cursor);
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

		String[] projection = { DBPlacesEntry._ID,
				DBPlacesEntry.COLUMN_NAME_NAME,
				DBPlacesEntry.COLUMN_NAME_LATITUDE,
				DBPlacesEntry.COLUMN_NAME_LONGITUDE,
				DBPlacesEntry.COLUMN_NAME_MARKER };
		return db.query(DBPlacesEntry.TABLE_NAME, projection, selection,
				selectionArgs, null, null, null);
	}

	/**
	 * returns a single location from where the cursor points at the DB
	 * 
	 * @param cursor
	 * @return
	 */
	public static ToDoLocation getCurrentObjectFromCursor(Cursor cursor) {
		if(cursor.getCount()<1){
			return new ToDoLocation(-1, "", 0, 0, null);
		}
		int id = cursor.getInt(cursor.getColumnIndex(DBPlacesEntry._ID));
		String name = cursor.getString(cursor
				.getColumnIndex(DBPlacesEntry.COLUMN_NAME_NAME));
		double latitude = Double.parseDouble(cursor.getString(cursor
				.getColumnIndex(DBPlacesEntry.COLUMN_NAME_LATITUDE)));
		double longitude = Double.parseDouble(cursor.getString(cursor
				.getColumnIndex(DBPlacesEntry.COLUMN_NAME_LONGITUDE)));
		String markerId = cursor.getString(cursor
				.getColumnIndex(DBPlacesEntry.COLUMN_NAME_MARKER));

		// create object
		return new ToDoLocation(id, name, latitude, longitude, markerId); // (automatically
		// gets
		// stored
		// in
		// the
		// HashMap)

	}

	/**
	 * returns a single location from the DB by markerID
	 * 
	 * @param markerID
	 * @param context
	 * @return
	 */
	public static ToDoLocation getToDoLocationByMarkerFromDB(String markerID,
			Context context) {
		String selection = DBPlacesEntry.COLUMN_NAME_MARKER + "=?";
		String[] selectionArgs = { markerID };
		Cursor c = getCursor(context, selection, selectionArgs);
		c.moveToFirst();
		return getCurrentObjectFromCursor(c);
	}

	/**
	 * Writes content to Database. If <code>id</code> is <code>-1</code> a new
	 * item is created. Update not tested yet.
	 */
	public void writeToDB(Context context) {
		ToDoLocation proof = getToDoLocationFromDB(this.id, context);

		ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(DBPlacesEntry.COLUMN_NAME_NAME, name);
		values.put(DBPlacesEntry.COLUMN_NAME_LATITUDE,
				String.valueOf(this.getLatitude()));
		values.put(DBPlacesEntry.COLUMN_NAME_LONGITUDE,
				String.valueOf(this.getLongitude()));

		if (proof.id < 0) {
			id = (int) db.insert(DBPlacesEntry.TABLE_NAME, null, values);

		} else {
			db.update(DBPlacesEntry.TABLE_NAME, values, DBPlacesEntry._ID
					+ " = " + id, null);
		}
		db.close();

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

		String[] projection = { DBPlacesEntry._ID,
				DBPlacesEntry.COLUMN_NAME_NAME,
				DBPlacesEntry.COLUMN_NAME_LATITUDE,
				DBPlacesEntry.COLUMN_NAME_LONGITUDE,
				DBPlacesEntry.COLUMN_NAME_MARKER };
		// Log.d("DB Location", db.query(false,
		// DBPlacesEntry.TABLE_NAME,projection, selection, selectionArgs, null,
		// null, null, null).getCount() + " items found");
		int id = db.delete(DBPlacesEntry.TABLE_NAME, selection, selectionArgs);
		// allLocations.remove(id);
		db.close();
		mDbHelper.close();
		Log.d("DB Location", "Delted ID:" + id);
		return id;
	}

	/**
	 * deletes a specific entry with the name strinToBeDeleted
	 * 
	 * @param stringToBeDeleted
	 * @param context
	 * @return
	 */
	public static int staticDeleteByString(String stringToBeDeleted,
			Context context) {
		String[] buffer = { DBPlacesEntry.COLUMN_NAME_NAME };
		String selection = createQueryColumns(buffer);
		String[] selectionArgs = new String[] { stringToBeDeleted };
		return deleteBySelection(context, selection, selectionArgs);
	}

	/**
	 * deletes a specific location from the Db where markerID = ?
	 * 
	 * @param markerID
	 * @param context
	 * @return
	 */
	public static int staticDeleteByMarker(String markerID, Context context) {
		String[] buffer = { DBPlacesEntry.COLUMN_NAME_MARKER };
		String selection = createQueryColumns(buffer);
		String[] selectionArgs = new String[] { markerID };
		return deleteBySelection(context, selection, selectionArgs);
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
		String[] buffer = { DBPlacesEntry._ID };
		String selection = createQueryColumns(buffer);
		String[] selectionArgs = { String.valueOf(idToBeDeleted) };
		return deleteBySelection(context, selection, selectionArgs);
	}

	/**
	 * statically deletes a Location by acombination of name, latitude and
	 * longitude out of the database
	 * 
	 * @param name
	 * @param lat
	 * @param lng
	 * @param context
	 * @return
	 */
	public static int staticDeleteByNameLatLng(String name, double lat,
			double lng, Context context) {
		//DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
		//dfs.setDecimalSeparator('.');
		//DecimalFormat numberFormat = new DecimalFormat("#.####", dfs);

		String[] buffer = { DBPlacesEntry.COLUMN_NAME_NAME,
				DBPlacesEntry.COLUMN_NAME_LATITUDE,
				DBPlacesEntry.COLUMN_NAME_LONGITUDE };
		String[] selectionArgs = { name, String.valueOf(lat),
				String.valueOf(lng) };// numberFormat.format(lat),
										// numberFormat.format(lng)};
		String selection = createQueryColumns(buffer);
		// String selection= DBPlacesEntry.COLUMN_NAME_NAME + " =? AND " +
		// DBPlacesEntry.COLUMN_NAME_LATITUDE + " =? AND " +
		// DBPlacesEntry.COLUMN_NAME_LONGITUDE + " =?";
		Log.d("DB", selectionArgs[0] + selectionArgs[1] + selectionArgs[2]);

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

	/**
	 * creates a full, 'classical' query, e.g. [columx, columy],[blabla,bubu] ->
	 * columx = 'blabla' AND columy = 'bubu'
	 * 
	 * @param columns
	 * @param results
	 * @return
	 */
	public static String createFullQueryColumns(String[] columns,
			String[] results) {
		String result = "";
		for (int i = 0; i < (columns.length - 1); ++i) {
			result += columns[i] + " = '" + results[i] + "' AND ";
		}
		result += columns[columns.length - 1] + " = '"
				+ results[columns.length - 1] + "'";
		return result;
	}

	/**
	 * @param newTask
	 */
	public void addUsedIn(ToDoEntry newTask) {
		tasks.add(newTask);
	}

	/**
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param newToDoEntry
	 */
	public void addToDoEntry(ToDoEntry newToDoEntry) {
		tasks.add(newToDoEntry);
	}

	/**
	 * @return
	 */
	public HashSet<ToDoEntry> getTasks() {
		return tasks;
	}

	/**
	 * @return
	 */
	public LatLng getLatLng() {
		return new LatLng(this.getLatitude(), this.getLongitude());
	}

	public void setEntries(HashSet<ToDoEntry> tasks) {
		this.tasks = tasks;
	}

	public void setEntriesFromDB(Context context) {
		setEntries(ToDoEntryLocation.getConnectedEntries(this, context));
	}

}
