package dspot.client.database;

import java.util.ArrayList;

import dspot.utils.MyLocation;
import dspot.utils.Sport;
import dspot.utils.SpotShortInfo;
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
	
	
	
	public void resetDefinitions(){
		String resetSports = "delete from sports";
		String resetVersion = "delete from versions";
		String resetLocations = "delete from locations"; 
		
		database.execSQL(resetLocations);
	 	database.execSQL(resetSports);
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

	
	
	public long createUser(int id, String username,  String name, String email) {
		
		ContentValues initialvalues = new ContentValues();
		initialvalues.put("_id", id);
		initialvalues.put("name", name);
		initialvalues.put("email", email);
		initialvalues.put("photo", "");
		initialvalues.put("username", username);

		return database.insert("user", null, initialvalues);
	}
	
	
	public User getUserInfo(int user_id){
		
		User u = new User();
		
		String selectUser = "Select * from user Where _id="+ user_id;  
		
	 	Cursor userCursor = database.rawQuery(selectUser, null);
	 	
	 	userCursor.moveToFirst();
	 	if(userCursor.getCount() == 0){
	 		userCursor.close();
	 		return u;
	 	}
	 	
	 	userCursor.moveToFirst();
 		u.setId(userCursor.getInt(0));
 		u.setName(userCursor.getString(1));
 		u.setUsername(userCursor.getString(2));
 		u.setEmail(userCursor.getString(3));
 		u.setPhoto(userCursor.getString(4));
 		
	 	userCursor.close();
	 	
	 	return u;
	}
	
	
	
/////////////////////////////////////////////////////////////////////

	public int getLocationId(String name) {
		
		String selectLocation = "Select _id from locations Where name=\""+ name + "\"";  
		
	 	Cursor locationCursor = database.rawQuery(selectLocation, null);
	 	
	 	locationCursor.moveToFirst();
	 	if(locationCursor.getCount() == 0){
	 		locationCursor.close();
	 		return -1;
	 	}
	 
	 	return locationCursor.getInt(0);
	}
	
	
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
	
	
	
	
	public ArrayList<Integer> getSelectedSports(){
		
		ArrayList<Integer> sports = new ArrayList<Integer>();
		
		String selectSports = "Select _id from sports where ischecked=1";  
		
	 	Cursor sportsCursor = database.rawQuery(selectSports, null);
	 	
	 	
	 	sportsCursor.moveToFirst();
	 	if(sportsCursor.getCount() == 0){
	 		sportsCursor.close();
	 		return sports;
	 	}
	 	
	 	sportsCursor.moveToFirst();
	 	do {
	 		sports.add(sportsCursor.getInt(0));
	 	}while(sportsCursor.moveToNext());
	 	sportsCursor.close();
	 	
	 	return sports;
	 	
	}
	
	
	
	public void updateSportState(int id, int check){
		
		 ContentValues args = new ContentValues();
		 args.put("ischecked", check);
		 database.update("sports", args, "_id=" + id, null);
	}
	
	
/////////////////////////////////////////////////////////////////////

	public long createFavourite(int id, String name, String address, int rating ,int user_id) {
		
		ContentValues initialvalues = new ContentValues();
		initialvalues.put("_id", id);
		initialvalues.put("name", name);
		initialvalues.put("address", address);
		initialvalues.put("user_id", user_id);
		initialvalues.put("rating", rating);

		return database.insert("favourites", null, initialvalues);
	}
	
	
	
	public ArrayList<SpotShortInfo> getFavourites(int user_id){
		
		ArrayList<SpotShortInfo> favourites = new ArrayList<SpotShortInfo>();
		
		String selectFavourites = "Select * from favourites Where user_id="+ user_id +" Order By name";  
		
	 	Cursor favouritesCursor = database.rawQuery(selectFavourites, null);
	 	
	 	favouritesCursor.moveToFirst();
	 	if(favouritesCursor.getCount() == 0){
	 		favouritesCursor.close();
	 		return favourites;
	 	}
	 	
	 	favouritesCursor.moveToFirst();
	 	do {
	 		SpotShortInfo s = new SpotShortInfo(favouritesCursor.getString(1), favouritesCursor.getString(2), favouritesCursor.getInt(0), favouritesCursor.getInt(3));
	 		favourites.add(s);
	 	}while(favouritesCursor.moveToNext());
	 	favouritesCursor.close();
	 	
	 	return favourites;
		
	}

}
