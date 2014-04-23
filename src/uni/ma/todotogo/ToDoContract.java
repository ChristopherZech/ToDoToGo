/**
 * for Database
 */
package uni.ma.todotogo;
import android.provider.BaseColumns;

/**
 * 
 * @author Chris Zech
 *
 */
public final class ToDoContract {

	public static abstract class ToDoEntry implements BaseColumns {
        public static final String TABLE_NAME = "todos";
        public static final String COLUMN_NAME_TODO_ID = "todo_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_DATE = "date";
    }
	
	public static abstract class PlacesEntry implements BaseColumns {
        public static final String TABLE_NAME = "places";
        public static final String COLUMN_NAME_PLACE_ID = "place_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGDITUDE = "longditude";
    }

	public static abstract class ToDoPlacesEntry implements BaseColumns {
        public static final String TABLE_NAME = "todoplaces";
        public static final String COLUMN_NAME_TODO_ID = ToDoEntry.COLUMN_NAME_TODO_ID;
        public static final String COLUMN_NAME_PLACE_ID = PlacesEntry.COLUMN_NAME_PLACE_ID;
    }
}
