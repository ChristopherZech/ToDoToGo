package uni.ma.todotogo.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import uni.ma.todotogo.model.AvailableColors;
import uni.ma.todotogo.model.ToDoEntry;
import uni.ma.todotogo.model.ToDoEntryLocation;
import uni.ma.todotogo.model.ToDoLocation;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

public class AddActivity extends Activity {

	String name = "";
	// HashSet<Integer> locations = new HashSet<Integer>();
	int locations = 0;
	MultiAutoCompleteTextView myEditText;
	GregorianCalendar myCalendar;
	int entryID; 
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("LOOOOK", " onActivityResult starts");
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == 1) {
			if (data.hasExtra("returnKey1")) {
				Log.d("TestIntent", data.getExtras().getString("returnKey1"));
			} else {
				Log.d("TestIntent", "ITSNOTFUCKINGWORKING!!!!!!!!!!!!!!");
			}
		}
	}

	/*
	 * @Override protected void onActivityResult(int requestCode, int
	 * resultCode, Intent data) { if (requestCode == 1) { if (resultCode ==
	 * RESULT_OK) { Intent intent = getIntent(); // locations =
	 * (HashSet<Integer>) intent // .getSerializableExtra("locationsAdded");
	 * 
	 * locations = data.getIntExtra("locationsAdded", 0); Toast.makeText(this,
	 * "" + locations, Toast.LENGTH_LONG).show();
	 * Log.d("Intent Info",""+locations); } } }
	 */
	@Override
	public void onNewIntent(Intent intent)
	{
		Log.d("LOOOOK", "on New Intent!!	 is getting called");
	  super.onNewIntent(intent);

	  // Why isn't this performed by the framework in the line above?
	  setIntent(intent);
	}
	
	@Override
	protected void onResume() {
		Log.d("LOOOOK", "on Resume!!	 is getting called");
		super.onResume();
		Intent myIntent = getIntent();
		if (myIntent != null) {
			if (myIntent.getExtras() != null) {

				{
					if (myIntent.hasExtra("returnKey1")) {
						Log.d("TestIntent",
								myIntent.getExtras().getString("returnKey1"));
					} else
						Log.d("TestIntent", "key is missing!!!!!!!!!!!!!!");
				}
			} else {
				Log.d("TestIntent", "keys are empty!!!!!!!!!!!!!!");
			}
		} else {
			Log.d("TestIntent", "ITSNOTFUCKINGWORKING!!!!!!!!!!!!!!");
		}
		;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("LOOOOK", "OncreateStarts");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_task_layout);
		locations = 0;
		  // set Action Bar
 		ActionBar actionBar = getActionBar();
 		actionBar.setHomeButtonEnabled(true);
 		actionBar.setDisplayUseLogoEnabled(false);
 		actionBar.setDisplayHomeAsUpEnabled(true);
    
		this.myEditText = (MultiAutoCompleteTextView) findViewById(R.id.add_text_when);

		this.myCalendar = new GregorianCalendar();

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

				String myFormat = "dd/MM/yy"; // In which you need put here
				SimpleDateFormat sdf = new SimpleDateFormat(myFormat,
						Locale.GERMANY);

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

		// fill dropdown menu for colors with content
		Spinner spinnerDropdown = (Spinner) findViewById(R.id.add_spinner_categories);
		ArrayAdapter<CharSequence> spinnerDropdownAdapter = new ArrayAdapter(
				this, android.R.layout.simple_spinner_item,
				AvailableColors.values());
		// Specify the layout to use when the list of choices appears
		spinnerDropdownAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinnerDropdown.setAdapter(spinnerDropdownAdapter);
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
		Context context = getBaseContext();
		int itemID = item.getItemId();
		if (itemID == R.id.action_add_ok) {
			String name = ((MultiAutoCompleteTextView) findViewById(R.id.add_text_what))
					.getEditableText().toString();
			long category = ((Spinner) findViewById(R.id.add_spinner_categories))
					.getSelectedItemId();
			ToDoEntry newEntry = new ToDoEntry(-1, name, (int) category,
					myCalendar);
			entryID = newEntry.writeToDB(getBaseContext());
			Log.d("Add View", entryID + " is the new entry ID");
//			HashSet<ToDoLocation> locationsBuffer = new HashSet<ToDoLocation>();
//			ToDoLocation locationBuffer = new ToDoLocation(-1, null);
//			ToDoEntryLocation mapper = new ToDoEntryLocation(-1);
//			if (locations == 0) {// .isEmpty()){
//				finish();
//				return false;
//			} else {
//				// for(Integer locationID:locations){
//				locationBuffer = ToDoLocation.getToDoLocationFromDB(locations,
//						context);
//				locationsBuffer.add(locationBuffer);
//				mapper = new ToDoEntryLocation(-1, newEntry, locationBuffer);
//				mapper.writeToDB(context);
//				// }
//				newEntry.setLocations(locationsBuffer);
//				int entryID = newEntry.writeToDB(getBaseContext());
//				Intent dataForListView = new Intent();
//				// dataForListView.putExtra("entryID", entryID);
//				setResult(RESULT_OK, dataForListView);
//				finish();
//
//				finish();
//				return true;
//			}
			Intent myIntent = new Intent(this, MapActivity.class);
			 myIntent.putExtra("entryID", entryID);
			startActivity(myIntent);
			finish();
			return true;		
		} else if (itemID == R.id.action_place) {
			Intent myIntent = new Intent(this, MapActivity.class);
			startActivityForResult(myIntent, 1);
			Log.d("LOOOOK", "acitivity is started for result");
			finish();
			return true;
		} else
			return false;
	}
}