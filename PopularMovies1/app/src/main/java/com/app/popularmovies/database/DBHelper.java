package com.app.popularmovies.database;

import android.database.sqlite.SQLiteDatabase;

public abstract class DBHelper {
	/**
	 * Abstract method to get an instance of DB
	 * @return An instance of Sqlite DB
	 * @author ravind maurya
	 */
	public abstract SQLiteDatabase openDB();
}
