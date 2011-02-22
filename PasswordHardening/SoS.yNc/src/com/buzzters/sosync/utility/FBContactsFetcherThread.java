package com.buzzters.sosync.utility;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;

import com.buzzters.sosync.model.ContactInfo;
import com.facebook.android.Facebook;

public class FBContactsFetcherThread extends Thread
{
	Facebook fb;
	String listId;
	String listName;
	Map<String, List<ContactInfo>> listMembersMap;
	Context ctxt;
	
	private static final Bundle bundleParams = new Bundle();
	private static final String NOTES = "Contact group imported from Facebook";
	
	static
	{
		bundleParams.putString("fields", "id,name,picture");
	}
	
	public FBContactsFetcherThread(Facebook fb, Context ctxt, String listId, String listName, Map<String, List<ContactInfo>> listMembersMap)
	{
		this.fb = fb;
		this.listMembersMap = listMembersMap;
		this.listId = listId;
		this.listName = listName;
		this.ctxt = ctxt;
	}
	
	@Override
	public void run()
	{
		try 
		{
			String listMembersAsJsonString = fb.request(listId +"/members");
			listMembersMap.put(listId, getListMembers(listMembersAsJsonString));
			ContentValues cValues = new ContentValues();
			cValues.put(Groups.TITLE, listName);
			cValues.put(Groups.NOTES, NOTES);			
			Uri dataUri = ctxt.getContentResolver().insert(ContactsContract.Groups.CONTENT_URI, cValues);
			long groupId = ContentUris.parseId(dataUri);
			ArrayList<ContentProviderOperation> op_list = null;
			for(ContactInfo cInfo : listMembersMap.get(listId)){	

				/*ContentValues values = new ContentValues();
				 values.put(RawContacts.ACCOUNT_TYPE, null);
				 values.put(RawContacts.ACCOUNT_NAME, null);
				 Uri rawContactUri = ctxt.getContentResolver().insert(RawContacts.CONTENT_URI, values);
				 long rawContactId = ContentUris.parseId(rawContactUri);
				 values.clear();
				 values.put(Data.RAW_CONTACT_ID, rawContactId);
				 values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
				 values.put(StructuredName.DISPLAY_NAME, cInfo.getContactName());
				 ctxt.getContentResolver().insert(Data.CONTENT_URI, values);*/
				op_list = new ArrayList<ContentProviderOperation>();
		         op_list.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
		             .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
		             .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
		             .withValue(RawContacts.AGGREGATION_MODE, RawContacts.AGGREGATION_MODE_DISABLED)

		             .build());

		      // first and last names
		           op_list.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
		       .withValueBackReference(Data.RAW_CONTACT_ID, 0)
		             .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
		             .withValue(StructuredName.DISPLAY_NAME, cInfo.getContactName())		             
		             .build());
		           op_list.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
		    		       .withValueBackReference(Data.RAW_CONTACT_ID, 0)
		    		             .withValue(Data.MIMETYPE, GroupMembership.CONTENT_ITEM_TYPE)
		    		             .withValue(GroupMembership.GROUP_ROW_ID, groupId)		             
		    		             .build());
				 /*values.clear();
				 values.put(Data.RAW_CONTACT_ID, rawContactId);
				 values.put(Groups._ID, listName);
				 ctxt.getContentResolver().insert(Data.CONTENT_URI, values);*/
				/*ops.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
						.withValue(Data.RAW_CONTACT_ID, 0)
						.withValue(Data.DISPLAY_NAME, cInfo.getContactName())
						//.withValue(Groups._ID, groupId)
						.build());*/
			}
		      
			       ContentProviderResult[] results = ctxt.getContentResolver().applyBatch(ContactsContract.AUTHORITY, op_list);			  
			//ctxt.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		 /*catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	private List<ContactInfo> getListMembers(String jsonFriendList) throws JSONException, MalformedURLException, IOException
	{
		List<ContactInfo> contactsList = new ArrayList<ContactInfo>();
		JSONObject friendList = new JSONObject(jsonFriendList);
		JSONArray friendListArray = friendList.getJSONArray("data");
		for(int index = 0; index < friendListArray.length(); index++)
		{
			ContactInfo cInfo = new ContactInfo();
			cInfo.setListId(listId);
			String friendId = friendListArray.getJSONObject(index).getString("id"); 
			cInfo.setFbId(friendId);
			String friendDetailsJSON = fb.request(friendId, bundleParams);
			cInfo.setContactName(friendListArray.getJSONObject(index).getString("name"));
			cInfo.setPictureUrl(new JSONObject(friendDetailsJSON).getString("picture"));
			contactsList.add(cInfo);
		}
		return contactsList;
	}
	
	
}
