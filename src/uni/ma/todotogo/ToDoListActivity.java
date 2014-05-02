package uni.ma.todotogo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uni.ma.todotogo.ToDoContract.DBToDoEntry;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.location.*;


public class ToDoListActivity extends Activity {
	private ArrayList<HashMap<String, Object>> toDoList;
	private ArrayAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo_list_layout);
		ActionBar actionBar = getActionBar();
	    //actionBar.setHomeButtonEnabled(true);
	    actionBar.setDisplayUseLogoEnabled(false);
	    actionBar.setDisplayHomeAsUpEnabled(true);
		// create list which will be filled with data
		toDoList = new ArrayList<HashMap<String,Object>>();
		
		ListView lv = (ListView)findViewById(R.id.todolist);
		adapter = new ArrayAdapterToDoList(this, toDoList);
		lv.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.to_do_list_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	        	Intent intentSettings = new Intent(ToDoListActivity.this,
	        			SettingsActivity.class);
	        			startActivity(intentSettings);
	        			
	            return true;
	        case R.id.action_add:
	        	Intent intentAdd = new Intent(this,
	        		    AddActivity.class);
	        			startActivityForResult(intentAdd, RESULT_OK);
	            return true;
	        case R.id.action_place:	   
	        	Intent intentMapview = new Intent(this,MapView.class);
	        	startActivity(intentMapview);
	            return true;    
	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * Creates a HashMap for configuring list items.
	 * @param desc Content of the description field.
	 * @param dist Content of the distance field.
	 * @param bgColor Background color of the fields.
	 * @return Created hash map.
	 */
    public HashMap<String, Object> addItem(String desc, String dist, int bgColor){
        HashMap<String, Object> item = new HashMap<String, Object>();
        
        item.put("ToDoName", desc);
        item.put("distance", dist);
        item.put("color", bgColor);
        
        return item;
    }
    
    public void updateList() {
    	toDoList.clear();
    	// initialize DB
		ToDoDbHelper mDbHelper = new ToDoDbHelper(getBaseContext());
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		
		System.out.println();
		
		// fill list with content
		String[] projection = {DBToDoEntry._ID,
				DBToDoEntry.COLUMN_NAME_TODO_ID,
				DBToDoEntry.COLUMN_NAME_NAME,
				DBToDoEntry.COLUMN_NAME_CATEGORY,
				DBToDoEntry.COLUMN_NAME_DATE
		};
		Cursor cursor = db.query(
			    DBToDoEntry.TABLE_NAME,  // The table to query
			    projection,                               // The columns to return
			    null,                                // The columns for the WHERE clause
			    null,                            // The values for the WHERE clause
			    null,                                     // don't group the rows
			    null,                                     // don't filter by row groups
			    null                                // The sort order
			    );
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			String name = cursor.getString(cursor.getColumnIndexOrThrow(DBToDoEntry.COLUMN_NAME_NAME));
			int color = cursor.getInt(cursor.getColumnIndexOrThrow(DBToDoEntry.COLUMN_NAME_CATEGORY));
			// TODO implement distance
			toDoList.add(addItem(name, "111m", color));
			cursor.moveToNext();
			
		}
		
		adapter.notifyDataSetChanged();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	updateList();
    }

}
