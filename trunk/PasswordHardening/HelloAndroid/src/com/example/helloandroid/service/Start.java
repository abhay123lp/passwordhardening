package com.example.helloandroid.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.example.helloandroid.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

public class Start extends Service {

	private static final String TAG = null;
	private static final int HELLO_ID = 0;
	final Service currentService = this;
	
	@Override
	public IBinder onBind(Intent intent){
		return null;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		Toast.makeText(this, "Creating", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		Toast.makeText(this, "Stopping", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onStart(Intent intent, int startId){
	super.onStart(intent, startId);
	int icon = R.drawable.icon;       
	CharSequence tickerText = "Notification Message to locate GPS";              
	long when = System.currentTimeMillis();        
	Context context = getApplicationContext();      
	CharSequence contentTitle = "My notification";  
	CharSequence contentText = "GPS Locator";      

	Intent notificationIntent = new Intent(this, Start.class);
	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

	// the next two lines initialize the Notification, using the configurations above
	Notification notification = new Notification(icon, tickerText, when);
	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
	String ns = Context.NOTIFICATION_SERVICE;
	NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
	mNotificationManager.notify(HELLO_ID, notification);
	
	Toast.makeText(this, "Starting Service", Toast.LENGTH_LONG).show();
	LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
	//Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	myLocationListener locationListener= new myLocationListener();
	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
	/*double longitude = location.getLongitude();
	double latitude = location.getLatitude();
	System.out.println(longitude);
	System.out.println(latitude);
    */
	
}


public class myLocationListener implements LocationListener{

  

	public void onLocationChanged(Location argLocation) {
            if(argLocation != null){
               String latitude = String.valueOf(argLocation.getLatitude());
               String longitude = String.valueOf(argLocation.getLongitude());
               System.out.println("latitude=" + latitude);
               System.out.println("longitude=" + longitude);
               try {
            	    File root = Environment.getExternalStorageDirectory();
            	    if (root.canWrite()){
            	        File gpxfile = new File(root, "gpxfile.gpx");
            	        FileWriter gpxwriter = new FileWriter(gpxfile);
            	        BufferedWriter out = new BufferedWriter(gpxwriter);
            	        out.write(latitude);
            	        
            	        out.write(longitude);
            	        out.close();
            	        System.out.println("Written");
            	        FileReader gpxreader = new FileReader(gpxfile);
            	        BufferedReader in = new BufferedReader(gpxreader);
            	        String text;
            	        while((text=in.readLine())!=null){
            	        	System.out.println(text); 	          	        	
            	        }
            	    }
            	} catch (IOException e) {
            	    //Log.e(TAG, "Could not write file " + e.getMessage());
            		System.out.println("Could not write file " + e.getMessage());
            	}
            }
            
    }
    public void onProviderDisabled(String provider) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle
arg2) {
    }

};
}