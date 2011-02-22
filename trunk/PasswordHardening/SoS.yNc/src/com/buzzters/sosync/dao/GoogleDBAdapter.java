package com.buzzters.sosync.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GoogleDBAdapter {

	public static final String KEY_NAME = "name";
	public static final String KEY_PASSWORD = "password";
	public static final String ROW_ID = "_gid";
	
	private static final String TAG = "GoogleDBAdapter";
	private DatabaseHelper gDbHelper;
	private SQLiteDatabase gDb;
	
	private static final String DATABASE_CREATE = "create table rules(_gid integer primary key autoincrement,name text not null,password text not null);";

	private static final String DATABASE_NAME = "credentials";
	private static final String DATABASE_TABLE = "rules";
	private static final int DATABASE_VERSION = 2;
	
	private final Context ctx;
	
	private static class DatabaseHelper extends SQLiteOpenHelper{
	
		DatabaseHelper(Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		public void onCreate(SQLiteDatabase db){
			db.execSQL(DATABASE_CREATE);
		}
		
		public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion){
			Log.v(TAG, "Version changed from"+ oldVersion +"to"+newVersion +"which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS rules");
			onCreate(db);
		}
	}

	public GoogleDBAdapter(Context ctx){
		this.ctx = ctx;
	}
	
	public GoogleDBAdapter open() throws SQLException{
		gDbHelper = new DatabaseHelper(ctx);
		gDb = gDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		gDbHelper.close();
	}
	
	public long createRule(String name,String password){
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_PASSWORD, password);
		
		return gDb.insert(DATABASE_TABLE, null, initialValues);
	}
	
	public boolean deleteRule (long rowId){
		return gDb.delete(DATABASE_NAME, ROW_ID + "=" + rowId, null) > 0;
	}
	
	public Cursor fetchAllRules(){
		
		return gDb.query(DATABASE_TABLE, new String [] {ROW_ID,KEY_NAME,KEY_PASSWORD}, null, null, null, null, null);
		
	}
	
	public Cursor fetchRule(long rowID) throws SQLException{
		
		Cursor gCursor = gDb.query(DATABASE_TABLE, new String[] {ROW_ID,KEY_NAME,KEY_PASSWORD}, ROW_ID + "=" + rowID, null, null, null, null);
	    if(gCursor!=null){
	    	gCursor.moveToFirst();
	    }
	    return gCursor;
		
	}
	
	public boolean updateRule(long rowID, String name, String password){
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);
		args.put(KEY_PASSWORD, password);
		
		return gDb.update(DATABASE_TABLE, args, ROW_ID + "=" + rowID, null) > 0;
	}
	
	
	
}
