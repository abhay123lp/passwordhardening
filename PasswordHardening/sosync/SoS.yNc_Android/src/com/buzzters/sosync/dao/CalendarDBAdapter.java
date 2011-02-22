package com.buzzters.sosync.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.lang.Object;
import android.text.format.Time;

public class CalendarDBAdapter {

	public static final String KEY_EVENT = "event";
	public static final String KEY_STARTTIME = "starttime";
	public static final String KEY_ENDTIME = "endtime";
	public static final String KEY_ALLDAY = "allday";
	public static final String ROW_ID = "_cid";
	private static final String TAG = "CalendarDBAdapter";
	private DatabaseHelper cDbHelper;
	private SQLiteDatabase cDb;
	private static final String DATABASE_CREATE = "create table eventdatatable(_cid integer primary key autoincrement,event text not null,starttime text not null,endtime text not null,allday text not null);";

	private static final String DATABASE_NAME = "calendar";
	private static final String DATABASE_TABLE = "eventdatatable";
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

	public CalendarDBAdapter(Context ctx){
		this.ctx = ctx;
	}
	public CalendarDBAdapter open() throws SQLException{
		cDbHelper = new DatabaseHelper(ctx);
		cDb = cDbHelper.getWritableDatabase();
		return this;
	}
	public void close(){
		cDbHelper.close();
	}
	public long createRule(String eventname,String starttime, String endtime, String allday){
		System.out.println("Inserting data");
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_EVENT, eventname);
		initialValues.put(KEY_STARTTIME, starttime);
		initialValues.put(KEY_ENDTIME, endtime);
		initialValues.put(KEY_ALLDAY, allday);
		return cDb.insert(DATABASE_TABLE, null, initialValues);
		
	}
	public boolean deleteRule (long rowId){
		return cDb.delete(DATABASE_NAME, ROW_ID + "=" + rowId, null) > 0;
	}
	public Cursor fetchAllRules(){
		return cDb.query(DATABASE_TABLE, new String [] {ROW_ID,KEY_EVENT,KEY_STARTTIME,KEY_ENDTIME,KEY_ALLDAY}, null, null, null, null, null);
	}
	public Cursor fetchRule(long rowID) throws SQLException{
		Cursor cCursor = cDb.query(DATABASE_TABLE, new String[] {ROW_ID,KEY_EVENT,KEY_STARTTIME,KEY_ENDTIME,KEY_ALLDAY}, ROW_ID + "=" + rowID, null, null, null, null);
		if(cCursor!=null){
			cCursor.moveToFirst();
		}
		return cCursor;
	}
	public boolean updateRule(long rowID, String eventname, String starttime, String endtime, String allday){
		ContentValues args = new ContentValues();
		args.put(KEY_EVENT, eventname);
		args.put(KEY_STARTTIME, starttime);
		args.put(KEY_ENDTIME, endtime);
		args.put(KEY_ALLDAY, allday);
		return cDb.update(DATABASE_TABLE, args, ROW_ID + "=" + rowID, null) > 0;
	}
}