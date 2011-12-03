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
	
	
	public void resetAll() {
		String resetLocations = "delete from locations";  
		String resetFriends = "delete from friends";  
		String resetFavourites = "delete from favourites";  
		String resetSports = "delete from sports";
		String resetUser = "delete from user";
		String resetVersion = "delete from versions";

		
	 	database.execSQL(resetLocations);
	 	database.execSQL(resetFriends);
	 	database.execSQL(resetFavourites);
	 	database.execSQL(resetSports);
	 	database.execSQL(resetUser);
	 	database.execSQL(resetVersion);
	}
	
	
	public void resetUserInfo(int id) {
		
		String resetFriends = "delete from friends where user_id= " + id;
		String resetFavourites = "delete from favourites where user_id= " + id;
		String resetUser = "delete from user where _id= " + id;

		
	 	database.execSQL(resetFriends);
	 	database.execSQL(resetFavourites);
	 	database.execSQL(resetUser);
	}
	
	
	
	
	
	
/////////////////////////////////////////////////////////////////////

	public long createLocation(int id, String name) {
		
		ContentValues initialvalues = new ContentValues();
		initialvalues.put("_id", id);
		initialvalues.put("name", name);

		return database.insert("locations", null, initialvalues);
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
	
	
/////////////////////////////////////////////////////////////////////
	
	public long createFriend(int id, String friendName, int user_id) {
		
		ContentValues initialvalues = new ContentValues();
		initialvalues.put("_id", id);
		initialvalues.put("name", friendName);
		initialvalues.put("user_id", user_id);
		initialvalues.put("ischecked", 0);

		return database.insert("friends", null, initialvalues);
	}
	
	
	public ArrayList<User> getFriends(int id){
		
		ArrayList<User> friends = new ArrayList<User>();
		
		String selectFriends = "Select * from friends Where user_id= "+ id + " Order By name";  
		
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
	
	public void resetFrienState(int id){
		
		 ContentValues args = new ContentValues();
		 args.put("ischecked", 0);
		 database.update("friends", args, "user_id=?", new String[]{String.valueOf(id)});
	}

	
/////////////////////////////////////////////////////////////////////
	
	public long createSport(int id, String name) {
		
		ContentValues initialvalues = new ContentValues();
		initialvalues.put("_id", id);
		initialvalues.put("name", name);
		initialvalues.put("ischecked", 1);


		return database.insert("sports", null, initialvalues);
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
