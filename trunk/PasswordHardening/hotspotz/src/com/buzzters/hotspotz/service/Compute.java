package com.buzzters.hotspotz.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeMap;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;


public class Compute extends Service{

	
	private static final int START_STICKY = 0;
	private NotificationManager mnm;
	
	public class LocalBinder extends Binder{
		Compute getService(){
			return Compute.this;
			
		}
	}
	
	public void onCreate(){
		mnm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		//showNotification();
	}
	
	
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mnm.cancel(0);

        // Tell the user we stopped.
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
    }
	
	
     @Override
     public IBinder onBind(Intent intent){
    	 return mBinder;
     }
     
     private final IBinder mBinder = new LocalBinder();

     
     
    	 
     
     public List <GeoPoint> computePlace(List <GeoPoint> friendLocations,List <GeoPoint> placeLocations){
    	 int l=1;
    	 int i=friendLocations.size();
    	 int k=placeLocations.size();
    	 float aggregate[]=new float[k];
    	 float stddev[]=new float[k];
    	 float mean[]=new float[k];
    	 //float sum[]=new float[i];
    	 float distance[]=new float[i] ; 
    	 ArrayList <GeoPoint> gp = new ArrayList<GeoPoint>();
    	 TreeMap<Float, GeoPoint> tm = new TreeMap<Float, GeoPoint>();
    	 ListIterator <GeoPoint> itr1 = placeLocations.listIterator();
    	 while(itr1.hasNext()){
    		 int j=1;
    		 float sum=0;
    		 GeoPoint location1 = itr1.next();
    		 ListIterator <GeoPoint> itr2 = friendLocations.listIterator();
    		 while(itr2.hasNext()){
    			 GeoPoint location2 = itr2.next();
    			 Location.distanceBetween((double)location1.getLatitudeE6(),(double)location1.getLongitudeE6(),(double)location2.getLatitudeE6(),(double)location2.getLongitudeE6(),distance);
    		     sum=sum + distance[j];
    		     ++j;
    		 }
    		 aggregate[l]=sum[j];
    		 mean[l]=aggregate[l]/i;
    		 stddev[l]=standardDeviation(distance,mean[l],i);
    		 tm.put(stddev[l], location1);
    		 ++l;
    	 }
    	 Arrays.sort(stddev);
    	 gp.add(tm.get(stddev[1]));
    	 gp.add(tm.get(stddev[2]));
    	 gp.add(tm.get(stddev[3]));
    	 return gp; 
    	 
      }
     
     public float standardDeviation(float[] distance,float mean,int i){
    	 float sum=0;
    	 for(int j=1;j<=i;j++){
    		 sum=sum+(distance[j]-mean)*(distance[j]-mean);
    	 }
    	 float result=(float)Math.sqrt(sum);
    	 return result;
    	 
     }
     
       
}