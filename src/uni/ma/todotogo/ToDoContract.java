/**
 * Contract for Database
 * @author Chris
 */
package uni.ma.todotogo;
import android.provider.BaseColumns;

/**
 * 
 * @author Chris Zech
 *
 */
public final class ToDoContract {

	/**
	 * Stores the Entrys of the ToDo list
	 * @author Chris
	 *
	 */
	public static abstract class DBToDoEntry implements BaseColumns {
        public static final String TABLE_NAME = "todos";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_MARKER = "marker";
    }
	
	/**
	 * Stores the available places.
	 * @author Chris
	 *
	 */
	public static abstract class DBPlacesEntry implements BaseColumns {
        public static final String TABLE_NAME = "places";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
		public static final String COLUMN_NAME_MARKER = "marker";
    }

	/**
	 * Maps ToDos and places m:n.
	 * @author Chris
	 *
	 */
	public static abstract class DBToDoPlacesEntry implements BaseColumns {
        public static final String TABLE_NAME = "todoplaces";
        public static final String COLUMN_NAME_TODO_ID = "todo_id";
        public static final String COLUMN_NAME_PLACE_ID = "place_id";
    }
}
