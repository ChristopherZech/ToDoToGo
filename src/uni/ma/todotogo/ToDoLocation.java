package uni.ma.todotogo;

import android.location.Location;
import java.util.HashSet;;

public class ToDoLocation extends Location {
	int id;
	private String name;
	private HashSet<ToDoEntry> tasks;
	
	public ToDoLocation(String provider, String name) {
		this(provider, name, new HashSet<ToDoEntry>());
	}
	
	public ToDoLocation(String provider, String name, HashSet<ToDoEntry> tasks) {
		super(provider);

		this.name = name;
		this.tasks = tasks;
	}
	
	public void addUsedIn(ToDoEntry newTask) {
		tasks.add(newTask);
	}

	public String getName() {
		return name;
	}

	public HashSet<ToDoEntry> getTasks() {
		return tasks;
	}

}
