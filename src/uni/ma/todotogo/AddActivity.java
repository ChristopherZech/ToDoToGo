package uni.ma.todotogo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.MultiAutoCompleteTextView;

public class AddActivity extends Activity {
	
	String name = "";
	Set<Location> locations = new HashSet<Location>();
	MultiAutoCompleteTextView myEditText;
	Calendar myCalendar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_task_layout);
		this.myEditText = (MultiAutoCompleteTextView)findViewById(R.id.add_text_when);
		
		this.myCalendar = Calendar.getInstance();

		final DatePickerDialog.OnDateSetListener datePicked = new DatePickerDialog.OnDateSetListener() {

		    @Override
		    public void onDateSet(DatePicker view, int year, int monthOfYear,
		            int dayOfMonth) {
		        // TODO Auto-generated method stub
		        myCalendar.set(Calendar.YEAR, year);
		        myCalendar.set(Calendar.MONTH, monthOfYear);
		        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		        updateLabel();
		    }

			private void updateLabel() {

			    String myFormat = "MM/dd/yy"; //In which you need put here
			    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

			    myEditText.setText(sdf.format(myCalendar.getTime()));
			    }

		};

		myEditText.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// TODO Auto-generated method stub
		            new DatePickerDialog(AddActivity.this, datePicked, myCalendar
		                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
		                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
				}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_add_forward:
	        	Intent intentSettings = new Intent(AddActivity.this,
	        		      SettingsActivity.class);
	        		      startActivity(intentSettings);
	            return true;
	        case R.id.action_add_backward:
	        	
	            return true;
	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void finish() {
	  // Prepare data intent 
	  Intent data = new Intent();
	  data.putExtra("Name", "Swinging on a star");
	  data.putExtra("Date", "You could be better then you are. ");
	  // Activity finished ok, return the data
	  setResult(RESULT_OK, data);
	  super.finish();
	} 
	
	
	
}

