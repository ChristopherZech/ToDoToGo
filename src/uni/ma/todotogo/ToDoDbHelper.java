/**
e * for Database
 */
package uni.ma.todotogo;



import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import uni.ma.todotogo.ToDoContract.DBPlacesEntry;
import uni.ma.todotogo.ToDoContract.DBToDoEntry;
import uni.ma.todotogo.ToDoContract.DBToDoPlacesEntry;

/**
 * 
 * @author Chris Zech
 * 
 */
public class ToDoDbHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 14;
    public static final String DATABASE_NAME = "ToDoToGo.db";
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_TODOS =
		    "CREATE TABLE " + DBToDoEntry.TABLE_NAME + " (" +
		    DBToDoEntry._ID + " INTEGER PRIMARY KEY," +
		    DBToDoEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
		    DBToDoEntry.COLUMN_NAME_CATEGORY + " INTEGER " + COMMA_SEP +
		    DBToDoEntry.COLUMN_NAME_DATE + " INTEGER " +
		    " )";
	private static final String SQL_CREATE_PLACES =
		    "CREATE TABLE " + DBPlacesEntry.TABLE_NAME + " (" +
		    DBPlacesEntry._ID + " INTEGER PRIMARY KEY," +
		    DBPlacesEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
		    DBPlacesEntry.COLUMN_NAME_MARKER + " REAL " + COMMA_SEP +
		    DBPlacesEntry.COLUMN_NAME_LATITUDE + TEXT_TYPE + COMMA_SEP +
		    DBPlacesEntry.COLUMN_NAME_LONGITUDE + TEXT_TYPE +
		    " )";
	private static final String SQL_CREATE_TODOPLACES =
		    "CREATE TABLE " + DBToDoPlacesEntry.TABLE_NAME + " (" +
    		DBToDoPlacesEntry._ID + " INTEGER PRIMARY KEY," +
    		DBToDoPlacesEntry.COLUMN_NAME_PLACE_ID + " INTEGER " + COMMA_SEP +
		    DBToDoPlacesEntry.COLUMN_NAME_TODO_ID + " INTEGER " +
		    " )";

	private static final String SQL_DELETE_TODOS =
			"DROP TABLE IF EXISTS " + DBToDoEntry.TABLE_NAME;
	private static final String SQL_DELETE_PLACES =
		    "DROP TABLE IF EXISTS " + DBPlacesEntry.TABLE_NAME;
	private static final String SQL_DELETE_TODOPLACES =
		    "DROP TABLE IF EXISTS " + DBToDoPlacesEntry.TABLE_NAME;

    public ToDoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TODOS);
        db.execSQL(SQL_CREATE_PLACES);
        db.execSQL(SQL_CREATE_TODOPLACES);
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
