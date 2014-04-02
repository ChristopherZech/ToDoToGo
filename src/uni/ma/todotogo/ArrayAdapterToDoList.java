package uni.ma.todotogo;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ArrayAdapterToDoList extends ArrayAdapter<HashMap<String, Object>> {
	  private final Context context;
	  private final ArrayList<HashMap<String, Object>> values;

	  public ArrayAdapterToDoList(Context context, ArrayList<HashMap<String, Object>> values) {
	    super(context, R.layout.todo_list_entry_layout, values);
	    this.context = context;
	    this.values = values;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.todo_list_entry_layout, parent, false);
	    
	    // get fields
	    TextView textViewDesc = (TextView) rowView.findViewById(R.id.tododesc);
	    TextView textViewDist = (TextView) rowView.findViewById(R.id.distance);
	    View categoryView = (View) rowView.findViewById(R.id.category_colorblock);
	    
	    // set text of description and distance field
	    textViewDesc.setText((String)values.get(position).get("ToDoName"));
	    textViewDist.setText((String)values.get(position).get("distance"));

	    // set background color of description and distance field
	    categoryView.setBackgroundColor((Integer)values.get(position).get("color"));

	    return rowView;
	  }
	} 