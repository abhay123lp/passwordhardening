package com.buzzters.hotspotz.service;

import java.util.*;


import com.google.android.maps.*;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocationUpdaterService extends Service{

	private static final String TAG = "hotspotz";
	private static ArrayList<GeoPoint> fL = new ArrayList<GeoPoint>();
	private static ArrayList<GeoPoint> pL = new ArrayList<GeoPoint>();
	private static GeoPoint fgp1=new GeoPoint(10,12);
	private static GeoPoint fgp2=new GeoPoint(12,14);
	private static GeoPoint fgp3=new GeoPoint(15,18);
	
	
	private static GeoPoint pgp1=new GeoPoint(11,9);
	private static GeoPoint pgp2=new GeoPoint(9,7);
	private static GeoPoint pgp3=new GeoPoint(1,3);
	private static GeoPoint pgp4=new GeoPoint(1,7);
	
	static
	{
		fL.add(fgp1);
		fL.add(fgp2);
		fL.add(fgp3);
		pL.add(pgp1);
		pL.add(pgp2);
		pL.add(pgp3);
		pL.add(pgp4);
		
	}
	
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// Its an internal service that should ideally not allow a Bind from external applications.		
		return null;
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		Toast.makeText(this, "HotspotZ boot service created", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Toast.makeText(this, "HotspotZ boot service destroyed", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onStart(Intent intent, int startId) 
	{
		super.onStart(intent, startId);
		computePlace(fL,pL);
		Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
	}
	
	public List <GeoPoint> computePlace(List <GeoPoint> friendLocations,List <GeoPoint> placeLocations){
   	 int l=1;
   	 Log.i(TAG, "Called compute method");
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
   		 aggregate[l]=sum;
   		 mean[l]=aggregate[l]/i;
   		 stddev[l]=standardDeviation(distance,mean[l],i);
   		 tm.put(stddev[l], location1);
   		 ++l;
   	 }
   	 Arrays.sort(stddev);
   	 gp.add(tm.get(stddev[0]));
   	 gp.add(tm.get(stddev[1]));
   	 gp.add(tm.get(stddev[2]));
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