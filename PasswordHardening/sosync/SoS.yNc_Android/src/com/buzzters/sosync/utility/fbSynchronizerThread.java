package com.buzzters.sosync.utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.buzzters.sosync.model.ContactInfo;
import com.facebook.android.Facebook;

public class fbSynchronizerThread extends Thread{
	Facebook fb;
	Context ctxt;
	public fbSynchronizerThread(Facebook fb, Context ctxt){
		this.fb = fb;
		this.ctxt = ctxt;
	}
	@Override
	public void run(){
		try
		{
			Log.i(Constants.APP_TAG, "Beginning fbSync thread");
			String jsonFriendList = fb.request("me/friendlists");
			Map<String, String> groupIdNameMap = getFriendLists(jsonFriendList);
			Map<String, List<ContactInfo>> listMembersMap = new HashMap<String, List<ContactInfo>>();
			List <FBContactsFetcherThread> threadList = new ArrayList<FBContactsFetcherThread>(); 
			for(String listId : groupIdNameMap.keySet())
			{
				FBContactsFetcherThread fbContactsFetcherThread = new FBContactsFetcherThread(fb, ctxt, listId, groupIdNameMap.get(listId), listMembersMap);
				threadList.add(fbContactsFetcherThread);
				fbContactsFetcherThread.start();
			}
			for(FBContactsFetcherThread fbContactsFetcherThread : threadList)
			{
				fbContactsFetcherThread.join();
			}
			// serializeAsXml(listMembersMap);		-- Test method to dump output as XML
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		catch(InterruptedException ie)
		{
			ie.printStackTrace();
		}
	}
	
	private Map<String, String> getFriendLists(String jsonFriendList) throws JSONException
	{
		Map<String, String> groupIdNameMap = new HashMap<String, String>();
		JSONObject friendList = new JSONObject(jsonFriendList);
		JSONArray friendListArray = friendList.getJSONArray("data");
		for(int index = 0; index < friendListArray.length(); index++)
		{
			groupIdNameMap.put(friendListArray.getJSONObject(index).getString("id"),
								friendListArray.getJSONObject(index).getString("name"));				
		}
		return groupIdNameMap;
	}
	
	
	
	/*		-- Test method to dump output as XML
	private void serializeAsXml(Map<String, Map<String, String>> listMembersMap)
	{
		StringBuilder friendListXMLBuilder = new StringBuilder("<?xml version=\"1.0\"?>");
		friendListXMLBuilder.append("<FriendList>\n");
		for(String listId : listMembersMap.keySet())
		{
			friendListXMLBuilder.append("\t").append("<List id=\"").append(listId).append("\">\n");
			for(String friendId : listMembersMap.get(listId).keySet())
			{
				friendListXMLBuilder.append("\t\t<Name id=\"").append(friendId).append("\" name=\"").
							append(listMembersMap.get(listId).get(friendId)).append("\"></Name>\n");
			}
			friendListXMLBuilder.append("\t</List>\n");
		}
		friendListXMLBuilder.append("</FriendList>\n");
		Log.d(Constants.APP_TAG, friendListXMLBuilder.toString());
	}
	*/
}
