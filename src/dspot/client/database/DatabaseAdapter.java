package dspot.client.database;

import java.util.Map;
import java.util.TreeMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseAdapter {
	
	private Context context;
	private static SQLiteDatabase database;
	private static DatabaseHelper dbHelper;
	
	
	public DatabaseAdapter(Context context) {
		this.context = context;
	}

	public DatabaseAdapter open() throws SQLException {
		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}
	
	
	public long createLocation(int id, String name) {
		
		ContentValues initialvalues = new ContentValues();
		initialvalues.put("id", id);
		initialvalues.put("location", name);

		return database.insert("locations", null, initialvalues);
	}
	
	
	public long createSport(int id, String name) {
		
		ContentValues initialvalues = new ContentValues();
		initialvalues.put("id", id);
		initialvalues.put("sport", name);

		return database.insert("sports", null, initialvalues);
	}
	
	
	public Map<String, Integer> getLocations(){
		
		Map<String, Integer> locations = new TreeMap<String, Integer>();
		
		String selectLocations = "Select * from locations";  
		
	 	Cursor locationsCursor = database.rawQuery(selectLocations, null);
	 	
	 	locationsCursor.moveToFirst();
	 	if(locationsCursor.getCount() == 0){
	 		locationsCursor.close();
	 		return locations;
	 	}
	 	
	 	locationsCursor.moveToFirst();
	 	do {
	 		locations.put(locationsCursor.getString(1), locationsCursor.getInt(0));
	 	}while(locationsCursor.moveToNext());
	 	locationsCursor.close();
	 	
	 	return locations;
	}
	
	
	
	public Map<String, Integer> getSports(){
		
		Map<String, Integer> sports = new TreeMap<String, Integer>();
		
		String selectVersion = "Select * from sports";  
		
	 	Cursor sportsCursor = database.rawQuery(selectVersion, null);
	 	
	 	sportsCursor.moveToFirst();
	 	if(sportsCursor.getCount() == 0){
	 		sportsCursor.close();
	 		return sports;
	 	}
	 	
	 	sportsCursor.moveToFirst();
	 	do {
	 		sports.put(sportsCursor.getString(1), sportsCursor.getInt(0));
	 	}while(sportsCursor.moveToNext());
	 	sportsCursor.close();
	 	
	 	return sports;
	}

}
