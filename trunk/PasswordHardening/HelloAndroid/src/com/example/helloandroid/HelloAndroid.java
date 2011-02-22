package com.example.helloandroid;

import com.example.helloandroid.R;
import com.example.helloandroid.service.Start;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.location.LocationManager;
import android.content.Context;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class HelloAndroid extends Activity {
   private static final int REQUEST_CODE = 0;
   final Activity currentActivity = this;
/** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.main);
       final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
       if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
           buildAlertMessageNoGps();
       } 
       
       final Button start = (Button) findViewById(R.id.button1);
       start.setOnClickListener(new View.OnClickListener() {
       		public void onClick(View v) {
       			System.out.println("Starting ");
       			Intent startGPS = new Intent();
       			startGPS.setAction("com.example.helloandroid.service.Start");
           		currentActivity.startService(startGPS);
       		}
       	});
       
       final Button stop = (Button) findViewById(R.id.button2);
       stop.setOnClickListener(new View.OnClickListener() {
       		public void onClick(View v) {
       			System.out.println("Stopping");
       			Intent stopGPS = new Intent();
       			stopGPS.setAction("com.example.helloandroid.service.Start");
           		currentActivity.stopService(stopGPS);
           		
           		int icon = R.drawable.icon;        // icon from resources
           		CharSequence tickerText = "Notification Message to locate GPS";              
           		long when = System.currentTimeMillis();         
           		Context context = getApplicationContext();     
           		CharSequence contentTitle = "My notification";  
           		CharSequence contentText = "GPS Locator";     

           		Intent notificationIntent = new Intent();
           		//PendingIntent contentIntent = PendingIntent.getActivity(this,notificationIntent);
           		Notification notification = new Notification(icon, tickerText, when);
           		//notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
           		String ns = Context.NOTIFICATION_SERVICE;
           		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
           		//mNotificationManager.notify(0, notification);
           		mNotificationManager.cancel(0);
       		}
       	});
   }
   

 private void buildAlertMessageNoGps() {
   final AlertDialog.Builder builder = new AlertDialog.Builder(this);
   builder.setMessage("Yout GPS seems to be disabled, do you want to enable it?")
          .setCancelable(false)
          .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                  launchGPSOptions(); 
              }
          })
          .setNegativeButton("No", new DialogInterface.OnClickListener() {
              public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                   dialog.cancel();
              }
          });
   final AlertDialog alert = builder.create();
   alert.show();
}
 
private void launchGPSOptions(){
	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	startActivityForResult(intent, REQUEST_CODE);
	
}
}