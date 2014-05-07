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
	HashSet<ToDoLocation> locations;
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

			Cursor cursor = db.query(DBToDoEntry.TABLE_NAME, projection, null, null, null, null, null );
			cursor.moveToFirst();

			while (!cursor.isAfterLast()) {
				// get data
				int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBToDoEntry._ID));
				String name = cursor.getString(cursor
						.getColumnIndexOrThrow(DBToDoEntry.COLUMN_NAME_NAME));
				int category = cursor.getInt(cursor.getColumnIndexOrThrow(DBToDoEntry.COLUMN_NAME_CATEGORY));
				int date = cursor.getInt(cursor.getColumnIndexOrThrow(DBToDoEntry.COLUMN_NAME_DATE));
				// create object
				new ToDoEntry(id, name, category, date); // (automatically gets stored in the HashMap)
				cursor.moveToNext();
			}
		}
		return allEntries;
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
	 * item is created. Update is not implemented yet.
	 */
	public void writeToDB(Context context) {
		ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(DBToDoEntry.COLUMN_NAME_NAME, name);
		values.put(DBToDoEntry.COLUMN_NAME_CATEGORY, category.getColor());
		values.put(DBToDoEntry.COLUMN_NAME_DATE, date.getTimeInMillis());

		if (id == -1) { // a new item is created
			id = (int) db.insert(DBToDoEntry.TABLE_NAME, null, values); // insert to db and get ID


			Log.d("ToDoEntry", "new ID is "+id);
			// update id in allEntries
			allEntries.remove(-1);
			allEntries.put(id, this);
		} else { // item is updated
			db.update(DBToDoEntry.TABLE_NAME, values,
					DBToDoEntry._ID + " = " + id, null);
		}

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
}
