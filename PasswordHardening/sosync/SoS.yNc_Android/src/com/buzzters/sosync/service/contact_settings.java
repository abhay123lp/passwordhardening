package com.buzzters.sosync.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Service;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.Contacts.People;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.widget.ListView;

import com.buzzters.sosync.dao.CalendarDBAdapter;
import com.buzzters.sosync.dao.RulesDbAdapter;
import com.buzzters.sosync.utility.Constants;


public class contact_settings extends Service 
{

	private CalendarDBAdapter mDbHelper;
	private RulesDbAdapter rulesmDbHelper;
	private ListView list_view;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onStart(Intent intent, int starId)
	{
		while(true)
		{
			mDbHelper = new CalendarDBAdapter(this);
			mDbHelper.open();

			rulesmDbHelper = new RulesDbAdapter(this);
			rulesmDbHelper.open();

			String ringtone=intent.getStringExtra("ringtone_uri");

			Cursor c = mDbHelper.fetchAllRules();
			int cnt_rows=c.getCount();
			c.moveToFirst();
			while(!c.isAfterLast())
			{
				String calendar_event=new String();
				String start_time=new String();
				String end_time=new String();
				calendar_event=c.getString(c.getColumnIndex(mDbHelper.KEY_EVENT));
				start_time=c.getString(c.getColumnIndex(mDbHelper.KEY_STARTTIME)).replace("T", "");
				end_time=c.getString(c.getColumnIndex(mDbHelper.KEY_ENDTIME)).replace("T", "");
				Log.i(Constants.APP_TAG, "Start time : " + start_time);
				Log.i(Constants.APP_TAG, "End time : " + end_time);

				SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
				try {
					Date d1 = (Date)sdf.parse(start_time);
					Date d2=(Date)sdf.parse(end_time);
					Date sys=new Date();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				Cursor rule=rulesmDbHelper.fetchRule_event(calendar_event);
				rule.moveToFirst();
				String rule_action=new String();
				rule_action=rule.getString(rule.getColumnIndex(rulesmDbHelper.KEY_ACTION));

				if(rule_action.equals("Ring as Silent"))
				{
					switch_silentmode();
					System.out.println("Switched to silent mode");
				}

				if(rule_action.equals("Send to Voicemail"))
				{
					ContentResolver cr = getContentResolver();
					Cursor cur = cr.query(People.CONTENT_URI, null, null, null, null);
					if (cur.getCount() > 0) {
						while (cur.moveToNext()) {
							//	cnt++;
							String name = cur.getString(cur.getColumnIndex(People.NAME));
							System.out.println("contact name ->"+name);

							ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
							ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
									.withSelection(Data.DISPLAY_NAME + "=?", new String[]{name})
									.withValue(RawContacts.SEND_TO_VOICEMAIL, 1)
									.build());
							System.out.println("Voice mail !!");
							try {
								getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
								//System.out.println("DONE !!");
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (OperationApplicationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				if(rule_action.equals("Assign Ringtone"))
				{
					ContentResolver cr = getContentResolver();
					Cursor cur = cr.query(People.CONTENT_URI, null, null, null, null);
					if (cur.getCount() > 0) {
						while (cur.moveToNext()) {
							//	cnt++;
							String name = cur.getString(cur.getColumnIndex(People.NAME));
							System.out.println("contact name ->"+name);

							ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
							ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
									.withSelection(Data.DISPLAY_NAME + "=?", new String[]{name})
									.withValue(RawContacts.CUSTOM_RINGTONE, ringtone)
									.build());
							System.out.println("Ringtone !!");
							try {
								getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
								//System.out.println("DONE !!");
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (OperationApplicationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				c.moveToNext();
			}

			/*		Uri uri = intent.getParcelableExtra("ringtone_uri");
		//System.out.println("ringtone uri is ->"+uri.toString());
		String ringtone=uri.toString();*/

			System.out.println("DONE !!!!");
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

	public void switch_silentmode()
	{
		AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
	}

}