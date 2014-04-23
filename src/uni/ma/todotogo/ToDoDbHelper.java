/**
 * for Database
 */
package uni.ma.todotogo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import uni.ma.todotogo.ToDoContract.PlacesEntry;
import uni.ma.todotogo.ToDoContract.ToDoEntry;
import uni.ma.todotogo.ToDoContract.ToDoPlacesEntry;

/**
 * 
 * @author Chris Zech
 * 
 */
public class ToDoDbHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "ToDoToGo.db";
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_TODOS =
		    "CREATE TABLE " + ToDoEntry.TABLE_NAME + " (" +
		    ToDoEntry._ID + " INTEGER PRIMARY KEY," +
		    ToDoEntry.COLUMN_NAME_TODO_ID + " INTEGER " + COMMA_SEP +
		    ToDoEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
		    ToDoEntry.COLUMN_NAME_CATEGORY + " INTEGER " + COMMA_SEP +
		    ToDoEntry.COLUMN_NAME_DATE + " INTEGER " +
		    " )";
	private static final String SQL_CREATE_PLACES =
		    "CREATE TABLE " + PlacesEntry.TABLE_NAME + " (" +
		    PlacesEntry._ID + " INTEGER PRIMARY KEY," +
		    PlacesEntry.COLUMN_NAME_PLACE_ID + " INTEGER " + COMMA_SEP +
		    PlacesEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
		    PlacesEntry.COLUMN_NAME_LATITUDE + " REAL " + COMMA_SEP +
		    PlacesEntry.COLUMN_NAME_LONGDITUDE + " REAL " +
		    " )";
	private static final String SQL_CREATE_TODOPLACES =
		    "CREATE TABLE " + ToDoPlacesEntry.TABLE_NAME + " (" +
    		ToDoPlacesEntry._ID + " INTEGER PRIMARY KEY," +
    		ToDoPlacesEntry.COLUMN_NAME_PLACE_ID + " INTEGER " + COMMA_SEP +
		    ToDoPlacesEntry.COLUMN_NAME_TODO_ID + " INTEGER " +
		    " )";

	private static final String SQL_DELETE_TODOS =
			"DROP TABLE IF EXISTS " + ToDoEntry.TABLE_NAME;
	private static final String SQL_DELETE_PLACES =
		    "DROP TABLE IF EXISTS " + PlacesEntry.TABLE_NAME;
	private static final String SQL_DELETE_TODOPLACES =
		    "DROP TABLE IF EXISTS " + ToDoPlacesEntry.TABLE_NAME;

    public ToDoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TODOS);
        db.execSQL(SQL_CREATE_PLACES);
        db.execSQL(SQL_CREATE_TODOPLACES);
        
        
        // TODO DEBUG DELETE - fill with a dummy entry
        db.execSQL("INSERT INTO " + ToDoEntry.TABLE_NAME + " ("+
				ToDoEntry.COLUMN_NAME_TODO_ID+","+
				ToDoEntry.COLUMN_NAME_NAME+","+
				ToDoEntry.COLUMN_NAME_CATEGORY+","+
				ToDoEntry.COLUMN_NAME_DATE +
        	") VALUES (1, 'Dummy Entry frm ToDoDbHelper.java', 1, 535444)");
    }
    
    public void onDelete(SQLiteDatabase db) {
    	db.execSQL(SQL_DELETE_TODOS);
    	db.execSQL(SQL_DELETE_PLACES);
    	db.execSQL(SQL_DELETE_TODOPLACES);
    }
    
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        onDelete(db);
        onCreate(db);
    }
}
