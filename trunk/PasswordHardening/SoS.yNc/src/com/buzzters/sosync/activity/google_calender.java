package com.buzzters.sosync.activity;

import com.buzzters.sosync.activity.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.buzzters.sosync.dao.GoogleDBAdapter;
import android.database.Cursor;


public class google_calender extends Activity {
	
	private GoogleDBAdapter gDBHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.google_cred);
		gDBHelper = new GoogleDBAdapter(this);
		gDBHelper.open();
		final Context ctxt = this;
		EditText username= new EditText(this);
		EditText passwd= new EditText(this);
		username=(EditText)findViewById(R.id.google_username);
		passwd=(EditText)findViewById(R.id.google_password);
		final String uname = username.getText().toString();
		final String pwd = passwd.getText().toString();
		
		final Button enterCredentials = (Button) findViewById(R.id.create_cred);
		enterCredentials.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
						
		Cursor gCursor = gDBHelper.fetchAllRules();
		gCursor.moveToFirst();
		int flag = 0;
		while(gCursor.moveToNext()){
			if(uname == gCursor.getString(gCursor.getColumnIndex(gDBHelper.KEY_NAME))){
			flag = 1;
			}
			}
		if(flag == 0){
			gDBHelper.createRule(uname, pwd);	
		}
		
		
	}
		
	});
	}

}
