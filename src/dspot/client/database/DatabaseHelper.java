package dspot.client.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "dspot.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE_SPORTS = " CREATE TABLE sports (_id INTEGER PRIMARY KEY,  name TEXT, ischecked INT)";
	private static final String DATABASE_CREATE_LOCATIONS = " CREATE TABLE locations (_id INTEGER PRIMARY KEY,  name TEXT)";
	private static final String DATABASE_CREATE_FAVOURITES = " CREATE TABLE favourites (_id INTEGER PRIMARY KEY, name TEXT, address, TEXT, photo TEXT, user_id INT)";
	private static final String DATABASE_CREATE_FRIENDS = "CREATE TABLE friends (_id INTEGER PRIMARY KEY, name TEXT, ischecked INT, user_id INT)";
	private static final String DATABASE_CREATE_USER = "CREATE TABLE user (_id INTEGER PRIMARY KEY, username TEXT, name TEXT, email TEXT, photo BLOB)";
	private static final String DATABASE_CREATE_VERSIONS = "CREATE TABLE versions (location_version INT, sport_version INT)";


	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_SPORTS);
		database.execSQL(DATABASE_CREATE_LOCATIONS);
		database.execSQL(DATABASE_CREATE_FAVOURITES);
		database.execSQL(DATABASE_CREATE_FRIENDS);
		database.execSQL(DATABASE_CREATE_USER);
		database.execSQL(DATABASE_CREATE_VERSIONS);
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(DatabaseHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		
		database.execSQL(" DROP TABLE IF EXISTS locations");
		database.execSQL(" DROP TABLE IF EXISTS sports");
		database.execSQL(" DROP TABLE IF EXISTS favourites");
		database.execSQL(" DROP TABLE IF EXISTS friends");
		database.execSQL(" DROP TABLE IF EXISTS user");
		database.execSQL(" DROP TABLE IF EXISTS versions");
		
		onCreate(database);
	}

}
