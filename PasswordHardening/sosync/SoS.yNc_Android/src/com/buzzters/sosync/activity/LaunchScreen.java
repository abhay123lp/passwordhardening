package com.buzzters.sosync.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.buzzters.sosync.utility.Constants;
import com.buzzters.sosync.utility.fbSynchronizerThread;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

public class LaunchScreen extends Activity {
	final Activity currentActivity = this;
    /** Called when the activity is first created. */
	final Context ctxt = this;
	Facebook fb;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Log.d(Constants.APP_TAG, "started creating activity");        
        
        final Button editSettingsButton = (Button) findViewById(R.id.edit_settings);
        editSettingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               Intent launchEditSettingsIntent = new Intent(ctxt, com.buzzters.sosync.activity.edit_settings.class);               
               startActivity(launchEditSettingsIntent); 
            }
        });
        
        final Button googleCredential = (Button) findViewById(R.id.google_calender);
        googleCredential.setOnClickListener(new View.OnClickListener() {
        		public void onClick(View v) {
        			Intent contactsettingsIntent = new Intent();
        			//contactsettingsIntent.putExtra("ringtone_uri","content://settings/system/ringtone");
                    contactsettingsIntent.setAction("com.buzzters.sosync.service.CalendarUpdate");
            		currentActivity.startService(contactsettingsIntent);
        		}
        	});
        
        final Button fbSyncButton = (Button) findViewById(R.id.fb_sync);
        fbSyncButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				fb = new Facebook(Constants.FACEBOOK_APP_ID);
				fb.authorize((Activity) ctxt, new String[] {"read_friendlists"}, new AuthorizeListener());
			}
		});
    }
    
    class AuthorizeListener implements DialogListener {
    	public void onComplete(Bundle values) {
    		Log.i(Constants.APP_TAG, "Successfully logged into facebook");    		    		
    		new fbSynchronizerThread(fb, ctxt).start();    																				
    		Toast.makeText(ctxt, "Logged into facebook", Toast.LENGTH_LONG);
    	}
		@Override
		public void onCancel() {
			Log.i(Constants.APP_TAG, "User clicked cancel");			
		}
		@Override
		public void onError(DialogError e) {			
			Log.e(Constants.APP_TAG, "Unknown error occured while logging into facebook");
			e.printStackTrace();
		}
		@Override
		public void onFacebookError(FacebookError e) {			
			Log.e(Constants.APP_TAG, "Unknown error occured while logging into facebook");
			e.printStackTrace();
		}
	}        
}
