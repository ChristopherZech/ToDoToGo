package uni.ma.todotogo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ToDoList extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_to_do_list);
		
		// fill the test list with content
		ArrayList<HashMap<String, Object>> myList = new ArrayList<HashMap<String,Object>>();
		for (int i = 0; i < 20; i++)
		{
			// todo ADD CONTENT HERE!
			myList.add(addItem("Entry Item "+i, ""+i+"m", (i%2 == 0? Color.RED : Color.BLUE)));
		}
		//ListAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, valueList);
		ListView lv = (ListView)findViewById(R.id.todolist);
		
		ArrayAdapter adapter = new ArrayAdapterToDoList(this, myList);
		
		lv.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.to_do_list, menu);
		return true;
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

}
