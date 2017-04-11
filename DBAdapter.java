package com.example.mapmanagement;

import android.content.ContentValues;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * Adapter to create a database for the MapManagement app.
 * 
 * It has four columns: one for id, one for the to do item name,
 * one for the latitude, and one for the longitude of the ToDoItem location.
 * 
 * (Taken from Beginning Android 4 Application Development.)
 * @author jesusmolina
 *
 */
public class DBAdapter {
	static final String KEY_ROWID = "_id";
	static final String KEY_NAME = "item";
	static final String KEY_LATITUDE = "latitude";
	static final String KEY_LONGITUDE = "longitude";
	static final String TAG = "DBAdapter";
	
	static final String DATABASE_NAME = "MapDatabase";
	static final String DATABASE_TABLE = "items";
	static final int DATABASE_VERSION = 1;
	static final String DATABASE_CREATE = "create table items (_id integer primary key autoincrement, "
			+ "item text not null, latitude text not null, longitude text not null);";
			
	final Context context;
	
	DatabaseHelper DBHelper;
	SQLiteDatabase db;
		
	public DBAdapter(Context ctx){
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper{
		DatabaseHelper(Context context){
			super(context,DATABASE_NAME,null,DATABASE_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db){
			try{
				db.execSQL(DATABASE_CREATE);
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.w(TAG, "Upgrading database from version" + oldVersion +" to "+newVersion+", which will destroy"
					+ "all old data");
			db.execSQL("DROP TABLE IF EXISTS items");
			onCreate(db);
		}
	}
	
	public DBAdapter open() throws SQLException{
		db = DBHelper.getWritableDatabase();
		return this;
	}
	public void close(){
		DBHelper.close();
	}
	public long insertItem(String name, String latitude, String longitude){
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_LATITUDE, latitude);
		initialValues.put(KEY_LONGITUDE, longitude);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}
	
	public boolean deleteItem(long rowId){
		return db.delete(DATABASE_TABLE, KEY_ROWID+"="+rowId, null) > 0;
	}
	
	public Cursor getAllItems(){
		return db.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_NAME, KEY_LATITUDE, KEY_LONGITUDE},null,null,null,null,null);
	}
	public Cursor getItem(long rowId) throws SQLException{
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,KEY_NAME,KEY_LATITUDE, KEY_LONGITUDE},
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if(mCursor != null){
			mCursor.moveToFirst();
		}
		return mCursor;
	}
}
