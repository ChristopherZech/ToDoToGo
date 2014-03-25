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
			myList.add(addItem("Entry Item "+i, ""+i+"m", Color.RED));
			//valueList1.add("value 1");
			//valueList2.add(i);
		}
		//ListAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, valueList);
		ListView lv = (ListView)findViewById(R.id.todolist);
		
		SimpleAdapter adapter = new SimpleAdapter(this, myList, 
                R.layout.layout_todo_listentry,
                new String[]{"ToDoName", "distance"}, 
                new int[]{R.id.tododesc, R.id.distance}
            );
		
		lv.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.to_do_list, menu);
		return true;
	}
	
    public HashMap<String, Object> addItem(String value1, String value2, int bgColor){
        HashMap<String, Object> item = new HashMap<String, Object>();
        
        item.put("ToDoName", value1);
        item.put("distance", value2);
        item.put("color", bgColor);
        
        return item;
    }

}
