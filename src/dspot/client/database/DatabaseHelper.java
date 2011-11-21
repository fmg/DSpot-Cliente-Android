package dspot.client.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "dspot.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE_SPORTS = " CREATE TABLE sports (id INTEGER PRIMARY KEY,  sport TEXT)";
	private static final String DATABASE_CREATE_LOCATIONS = " CREATE TABLE locations (id INTEGER PRIMARY KEY,  location TEXT)";
	private static final String DATABASE_CREATE_FAVOURITES = " CREATE TABLE favourites (id INTEGER PRIMARY KEY,  name TEXT, address, TEXT, photo TEXT)";


	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_SPORTS);
		database.execSQL(DATABASE_CREATE_LOCATIONS);
		database.execSQL(DATABASE_CREATE_FAVOURITES);
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

		onCreate(database);
	}

}
