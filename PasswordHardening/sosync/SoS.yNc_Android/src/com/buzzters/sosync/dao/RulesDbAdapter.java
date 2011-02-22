package com.buzzters.sosync.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RulesDbAdapter {

    public static final String KEY_GROUP = "groups";
    public static final String KEY_EVENT = "event";
    public static final String KEY_ACTION = "action";
    public static final String KEY_PRIORITY = "priority";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "RulesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table rules (_id integer primary key autoincrement,groups text not null, event text not null, action text not null, priority text not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "rules";
    private static final int DATABASE_VERSION = 3;

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS rules");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public RulesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public RulesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long createRule(String groups, String event, String action, String priority) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_GROUP, groups);
        initialValues.put(KEY_EVENT, event);
        initialValues.put(KEY_ACTION, action);
        initialValues.put(KEY_PRIORITY, priority);
        
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteRule() {

        return mDb.delete(DATABASE_TABLE,null ,null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllRules() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_GROUP,
                KEY_EVENT, KEY_ACTION, KEY_PRIORITY}, null, null, null, null, null);
    }
    
    
    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchRule(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                        KEY_GROUP, KEY_EVENT, KEY_ACTION, KEY_PRIORITY}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
  /*Query to fetch rule given a particular event*/
    public Cursor fetchRule_event(String cal_event) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                        KEY_GROUP, KEY_EVENT, KEY_ACTION, KEY_PRIORITY}, KEY_EVENT + "= '" + cal_event + "'", null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateRule(long rowId, String groups, String event, String action, String priority) {
        ContentValues args = new ContentValues();
        args.put(KEY_GROUP, groups);
        args.put(KEY_EVENT, event);
        args.put(KEY_ACTION, action);
        args.put(KEY_PRIORITY, priority);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
