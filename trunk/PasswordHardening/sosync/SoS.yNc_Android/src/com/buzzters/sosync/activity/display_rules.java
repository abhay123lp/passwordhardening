package com.buzzters.sosync.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.buzzters.sosync.dao.RulesDbAdapter;
import com.buzzters.sosync.utility.Constants;

public class display_rules extends Activity {

	private RulesDbAdapter mDbHelper;
	private ListView list_view;
	//private LinearLayout linear_view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_rule);
        list_view = (ListView) findViewById(R.id.rulelist);
        
      //  linear_view = (LinearLayout) findViewById(R.id.rel_layout);
        mDbHelper = new RulesDbAdapter(this);
        mDbHelper.open();
                
        Intent i = this.getIntent(); 
        String group= i.getStringExtra(Constants.SELECTED_GROUP);
        String event= i.getStringExtra(Constants.SELECTED_EVENT);
        String action= i.getStringExtra(Constants.SELECTED_ACTION);
        String priority= i.getStringExtra(Constants.SELECTED_PRIORITY);
        
        System.out.println("group ->"+group+" event ->"+event+" action ->"+action+" priority ->"+priority);
        
      //  list_view=new ListView(this);
        
        Cursor c = mDbHelper.fetchAllRules();
        int cnt_rows=c.getCount();
        c.moveToFirst();
        int cnt=0;
        String[] rule_created=new String[cnt_rows];
        while(!c.isAfterLast())
        {
        	//System.out.println("group selected ->"+c.getString(c.getColumnIndex(mDbHelper.KEY_GROUP))+c.getString(c.getColumnIndex(mDbHelper.KEY_EVENT))+c.getString(c.getColumnIndex(mDbHelper.KEY_ACTION)));
        	String groups=new String();
        	String events=new String();
        	String actions=new String();
        	String priorities=new String();
        	String rule=new String();
        	
        	
        	groups=c.getString(c.getColumnIndex(mDbHelper.KEY_GROUP));
        	events=c.getString(c.getColumnIndex(mDbHelper.KEY_EVENT));
        	actions=c.getString(c.getColumnIndex(mDbHelper.KEY_ACTION));
        	priorities=c.getString(c.getColumnIndex(mDbHelper.KEY_PRIORITY));
        	rule="Group ->"+groups;
        	rule=rule+"\n"+"Event ->"+events;
        	rule=rule+"\n"+"Action ->"+actions;
        	rule=rule+"\n"+"Priority ->"+priorities;
        	System.out.println("rule string ->"+rule);
        	rule_created[cnt]=rule;
        	
        	c.moveToNext();
        	cnt++;
        }
                
        mDbHelper.close();
                
        Button back=new Button(this);
        back.setId(1000);
        back.setText("BACK");
        back.setWidth(70);
        list_view.addFooterView(back);
        
        TextView txtHead=new TextView(this);
        txtHead.setText("SoS.ync");
        txtHead.setTextSize(50);
        list_view.addHeaderView(txtHead);
        
                
        list_view.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rule_created));

        final Activity currentActivity = this;
        //linear_view.addView(back);
        //setContentView(linear_view);
        
        /*Intent contactsettingsIntent = new Intent();
        contactsettingsIntent.setAction("com.buzzters.sosync.service.contact_settings");
		currentActivity.startService(contactsettingsIntent);*/
        
        final Context ctxt = this;
        
        final Button backButton = (Button) findViewById(1000);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {               
               Intent launchSettingsMenuIntent = new Intent(ctxt, com.buzzters.sosync.activity.LaunchScreen.class);                            
               startActivity(launchSettingsMenuIntent); 
            }
        });
        
    }
}