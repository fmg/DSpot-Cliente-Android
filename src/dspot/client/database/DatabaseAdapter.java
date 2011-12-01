package dspot.client.database;

import java.util.ArrayList;

import dspot.utils.MyLocation;
import dspot.utils.Sport;
import dspot.utils.User;

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
	
	
	public ArrayList<User> getFriends(){
		
		ArrayList<User> friends = new ArrayList<User>();
		
		String selectFriends = "Select * from friends Order By name";  
		
	 	Cursor friendsCursor = database.rawQuery(selectFriends, null);
	 	
	 	friendsCursor.moveToFirst();
	 	if(friendsCursor.getCount() == 0){
	 		friendsCursor.close();
	 		return friends;
	 	}
	 	
	 	friendsCursor.moveToFirst();
	 	do {
	 		User u = new User(friendsCursor.getInt(0), friendsCursor.getString(1), friendsCursor.getInt(2));
	 		friends.add(u);
	 	}while(friendsCursor.moveToNext());
	 	friendsCursor.close();
	 	
	 	return friends;
	}
	
	
	
	public void updateFrienState(int id, int check){
		
		 ContentValues args = new ContentValues();
		 args.put("ischecked", check);
		 database.update("friends", args, "_id=" + id, null);
	}
	
	public void resetFrienState(){
		
		 ContentValues args = new ContentValues();
		 args.put("ischecked", 0);
		 database.update("friends", args, null, null);
	}

	
	
	
	public ArrayList<Sport> getSports(){
		
		ArrayList<Sport> sports = new ArrayList<Sport>();
		
		String selectSports = "Select * from sports Order By name";  
		
	 	Cursor sportsCursor = database.rawQuery(selectSports, null);
	 	
	 	
	 	sportsCursor.moveToFirst();
	 	if(sportsCursor.getCount() == 0){
	 		sportsCursor.close();
	 		return sports;
	 	}
	 	
	 	sportsCursor.moveToFirst();
	 	do {
	 		Sport s = new Sport(sportsCursor.getInt(0),sportsCursor.getString(1), sportsCursor.getInt(2));
	 		sports.add(s);
	 	}while(sportsCursor.moveToNext());
	 	sportsCursor.close();
	 	
	 	return sports;
	 	
	}
	
	
	public void updateSportState(int id, int check){
		
		 ContentValues args = new ContentValues();
		 args.put("ischecked", check);
		 database.update("sports", args, "_id=" + id, null);
	}

}
