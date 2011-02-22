package com.buzzters.sosync.service;

import java.util.Date;
import java.util.HashSet;

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
import com.buzzters.sosync.dao.GoogleDBAdapter;

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
		while(true){
		super.onStart(intent, startId);
		Context context=this;
		cDBHelper = new CalendarDBAdapter(this);
		cDBHelper.open();
		String[][] values = updateCalendar(context);
		
		for(int i=0;i<values.length;i++){
		    cDBHelper.createRule(values[i][0], values[i][1], values[i][2], values[i][3]);
		}
		
		//cDBHelper.createRule("Party", "20101211t210000", "20101211t220000", "0");
		//cDBHelper.createRule("Studying", "20101211t230000", "20101211t240000", "0");
		//cDBHelper.createRule("Meeting", "20101211t230000", "20101211t240000", "0");
		cDBHelper.close();
		System.out.println("Starting next service");
		Intent contactsettingsIntent = new Intent();
		contactsettingsIntent.putExtra("ringtone_uri", "content://settings/system/ringtone");
		contactsettingsIntent.setAction("com.buzzters.sosync.service.contact_settings");
		currentService.startService(contactsettingsIntent);
		System.out.println("Ending service");
		
		try{
			  //do what you want to do before sleeping
			  Thread.currentThread().sleep(300*1000);//sleep for 1000 ms
			  //do what you want to do after sleeptig
			}
			catch(InterruptedException ie){
			//If this thread was intrrupted by nother thread 
			}
		}
		}
		
	
	public static String[][] updateCalendar(Context context){
				
		ContentResolver contentResolver = context.getContentResolver();
		final Cursor cursor = contentResolver.query(Uri.parse("content://calendar/calendars"), (new String[] {"_id"}), null, null, null);
		HashSet<String> calendarIds = new HashSet<String>();
		
		while(cursor.moveToNext()){
			final String _calId = cursor.getString(0);
			calendarIds.add(_calId);
		}
		
		String[][] values = new String[100][];
		for (String id : calendarIds) {
			Uri.Builder builder = Uri.parse("content://calendar/instance/when").buildUpon();
			//Date date= new Date();
			long now = System.currentTimeMillis();
			ContentUris.appendId(builder, now);
			
			int i=0;
			Cursor eventCursor = contentResolver.query(builder.build(), new String [] {"description", "begin", "end", "allDay"}, "Calendars._id=" + id ,null, null);
			while(eventCursor.moveToNext()){
				/*String des = eventCursor.getString(0);
				String startTime = eventCursor.getString(1);
				String endTime = eventCursor.getString(2);
				Boolean all_Day = !eventCursor.getString(3).equals("0");
				*/
				
					values[i][0] = eventCursor.getString(0).toString();
					values[i][1] = new Date(eventCursor.getLong(1)).toString();
					values[i][2] = new Date(eventCursor.getLong(2)).toString();
					values[i][3] = eventCursor.getString(3).toString();
				
										
			}
			 i++;		
		}
		return values;
	}

	
}
