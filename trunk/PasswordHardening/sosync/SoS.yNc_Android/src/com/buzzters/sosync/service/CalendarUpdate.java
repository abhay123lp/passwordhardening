package com.buzzters.sosync.service;

import java.util.Date;
import java.util.HashSet;

import android.app.Activity;
import android.app.Service;
import android.os.Binder;
import android.os.IBinder;
import android.content.Intent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;
import com.buzzters.sosync.dao.CalendarDBAdapter;

//import com.buzzters.sosync.dao.GoogleDBAdapter;

public class CalendarUpdate extends Service{
	final Service currentService = this;
	private CalendarDBAdapter cDBHelper;
	@Override
	public IBinder onBind(Intent intent){
		return null;
	}
	@Override
	public void onCreate(){
		super.onCreate();
		Toast.makeText(this, "Calendar Updating", Toast.LENGTH_LONG).show();
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		Toast.makeText(this, "Calendar Update", Toast.LENGTH_LONG).show();
	}
	@Override
	public void onStart(Intent intent, int startId){
		super.onStart(intent, startId);
		
		Context context=this;
		cDBHelper = new CalendarDBAdapter(this);
		cDBHelper.open();
	//	String[][] values = updateCalendar(context);
		//for(int i=0;i<100;i++){
			cDBHelper.createRule("Meeting", "20101223T103000", "20101223T123000","0");
			cDBHelper.createRule("Studying", "20101223T130000", "20101223T160000","0");
			cDBHelper.createRule("Hangout", "20101223T173000", "20101223T183000","0");
			cDBHelper.createRule("Party", "20101223T203000", "20101223T230000","0");
			
			System.out.println("Done adding data to eventdata");
	//	}

		Intent contactsettingsIntent = new Intent();
		contactsettingsIntent.putExtra("ringtone_uri","content://settings/system/ringtone");
        contactsettingsIntent.setAction("com.buzzters.sosync.service.contact_settings");
		currentService.startService(contactsettingsIntent);
	/*
	}
	/*public static String[][] updateCalendar(Context context){
		ContentResolver contentResolver = context.getContentResolver();
		final Cursor cursor = contentResolver.query(Uri.parse("content://com.android.calendar/calendars"), (new String[] {"url"}), null, null, null);
		HashSet<String> calendarNames = new HashSet<String>();
		while(cursor.moveToNext()){
			final String names = cursor.getString(0);
			/*final String beginTime = cursor.getString(0);
			final String endTime = cursor.getString(1);*/
		/*	calendarNames.add(names);
		}
		int i=0;
		String[][] values = new String[100][];
		for (String id : calendarNames) {
			Uri.Builder builder = Uri.parse("content://com.android.calendar/instance/when").buildUpon();
			long now = System.currentTimeMillis();
			ContentUris.appendId(builder, now + DateUtils.DAY_IN_MILLIS);
			Cursor eventCursor = contentResolver.query(builder.build(), new String [] {"description", "begin", "end"}, "Calendars.name=" + id ,null, null);
			while(eventCursor.moveToNext()){
				/*String des = eventCursor.getString(0);
String startTime = eventCursor.getString(1);
String endTime = eventCursor.getString(2);
Boolean all_Day = !eventCursor.getString(3).equals("0");
				 */
			/*	for(int j=0;j<3;j++){
					values[i][j] = eventCursor.getString(j).toString();
				}
				i++;
			}

		}
		return values;*/
	}

}

