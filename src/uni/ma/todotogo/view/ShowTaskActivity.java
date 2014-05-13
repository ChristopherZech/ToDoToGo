package uni.ma.todotogo.view;

import android.app.Activity;
import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;
 
import java.util.ArrayList;

import uni.ma.todotogo.model.Parent;

public class ShowTaskActivity extends Activity {
	
	private ExpandableListView mExpandableList;
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_show_task);
 
 
        mExpandableList = (ExpandableListView)findViewById(R.id.expandableListView1);
 
        ArrayList arrayParents = new ArrayList();
        ArrayList arrayChildren = new ArrayList();
 
        //here we set the parents and the children
        for (int i = 0; i < 10; i++){
            //for each "i" create a new Parent object to set the title and the children
            Parent parent = new Parent();
            parent.setTitle("Parent " + i);
 
            arrayChildren = new ArrayList();
            for (int j = 0; j < 10; j++) {
                arrayChildren.add("Child " + j);
            }
            parent.setArrayChildren(arrayChildren);
 
            //in this array we add the Parent object. We will use the arrayParents at the setAdapter
            arrayParents.add(parent);
        }
 
        //sets the adapter that provides data to the list.
        //mExpandableList.setAdapter(new ExpandableListAdapter(ShowTask.this,arrayParents));
 
    }
}