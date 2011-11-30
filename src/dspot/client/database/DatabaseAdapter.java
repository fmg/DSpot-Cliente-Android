package dspot.client.database;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import dspot.utils.MyLocation;
import dspot.utils.Sport;

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
		initialvalues.put("ischecked", 0);


		return database.insert("sports", null, initialvalues);
	}
	
	
	public ArrayList<MyLocation> getLocations(){
		
		ArrayList<MyLocation> locations = new ArrayList<MyLocation>();
		
		String selectLocations = "Select * from locations Order By name";  
		
	 	Cursor locationsCursor = database.rawQuery(selectLocations, null);
	 	
	 	locationsCursor.moveToFirst();
	 	if(locationsCursor.getCount() == 0){
	 		locationsCursor.close();
	 		return locations;
	 	}
	 	
	 	locationsCursor.moveToFirst();
	 	do {
	 		MyLocation mloc = new MyLocation(locationsCursor.getInt(0), locationsCursor.getString(1));
	 		locations.add(mloc);
	 	}while(locationsCursor.moveToNext());
	 	locationsCursor.close();
	 	
	 	return locations;
	}
	
	
	
	public Cursor getSports(){
		
		//ArrayList<Sport> sports = new ArrayList<Sport>();
		
		String selectSports = "Select * from sports Order By name";  
		
	 	Cursor sportsCursor = database.rawQuery(selectSports, null);
	 	
	 	return sportsCursor;
	 	/*
	 	sportsCursor.moveToFirst();
	 	if(sportsCursor.getCount() == 0){
	 		sportsCursor.close();
	 		return sports;
	 	}
	 	
	 	sportsCursor.moveToFirst();
	 	do {
	 		Sport s = new Sport(sportsCursor.getInt(0),sportsCursor.getString(1));
	 		sports.add(s);
	 	}while(sportsCursor.moveToNext());
	 	sportsCursor.close();
	 	
	 	return sports;
	 	*/
	}
	
	
	public void updateSportState(int id, int check){
		
		 ContentValues args = new ContentValues();
		 args.put("ischecked", check);
		 database.update("sports", args, "_id=" + id, null);
	}

}
