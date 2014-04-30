package uni.ma.todotogo;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
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
	
	/**
	 * 
	 * @param id ID in database. <code>-1</code> if does not have ID yet. 
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
	 * @param id ID in database. <code>-1</code> if does not have ID yet. 
	 * @param name
	 * @param category
	 * @param date
	 */
	public ToDoEntry(int id, String name, int category,	GregorianCalendar date) {
		this(id, name, AvailableColors.values()[category], date, new HashSet<ToDoLocation>());
	}

	/**
	 * 
	 * @param id ID in database. <code>-1</code> if does not have ID yet. 
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
	
	public void addLocation(ToDoLocation newLocation) {
		locations.add(newLocation);
	}
	
	/**
	 * Calculates the distance from <code>distTo</code> to the closest Location stored in <code>locations</code>.  
	 * @param distTo Location of which the shortest distance shall be calculated.
	 * @return Shortest distance.
	 */
	public float getClosestLocationTo(Location distTo) {
		Iterator<ToDoLocation> iter = locations.iterator();
		float closestDist = Float.POSITIVE_INFINITY;
		
		while(iter.hasNext()) {
			ToDoLocation curItem = iter.next();
			float curDist = distTo.distanceTo(curItem);
			if(curDist < closestDist) {
				closestDist = curDist;
			}
		}
		
		return closestDist;
	}
	
	/**
	 * Writes content to Database. If <code>id</code> is <code>-1</code> a new item is created. Update is not implemented yet.
	 */
	public void writeToDB(Context context) {
		ToDoDbHelper mDbHelper = new ToDoDbHelper(context);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
	
		values.put(DBToDoEntry.COLUMN_NAME_NAME, name);
		values.put(DBToDoEntry.COLUMN_NAME_CATEGORY, category.getColor());
		values.put(DBToDoEntry.COLUMN_NAME_DATE, date.getTimeInMillis());
		
		if(id == -1) { // a new item is created
			values.put(DBToDoEntry.COLUMN_NAME_TODO_ID, id);
			id = (int)db.insert(DBToDoEntry.TABLE_NAME, null, values);
			System.out.println("DB eintrag mit ID: " + id);
			
		} else {
			// item is updated
			// TODO UPDATE NOT IMPLEMENTED YET!
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
