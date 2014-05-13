package uni.ma.todotogo.model;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class ActivityEntry implements Parcelable {
	
	String name; 
	int date;
	List<Location> locations; 
	
	public ActivityEntry() {
		super();
	}
	
	public ActivityEntry(String name, int date, List<Location> locations) {
		this.name = name; 
		this.date = date;
		this.locations = new ArrayList<Location>();
		for(Location s : locations) this.locations.add(s);
	}

	protected String getName() {
		return name;
	}



	protected void setName(String name) {
		this.name = name;
	}



	protected int getDate() {
		return date;
	}



	protected void setDate(int date) {
		this.date = date;
	}



	protected List<Location> getLocations() {
		return locations;
	}



	protected void setLocations(List<Location> locations) {
		this.locations = locations;
	}
	
	protected void addLocation(Location location){
		checkLocationInitialisation();
		this.locations.add(location);
	}
	
	private void checkLocationInitialisation(){
		if (this.locations.equals(null)) this.locations = new ArrayList<Location>();
	}



	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}

}
