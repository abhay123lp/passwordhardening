package com.buzzters.sosync.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class edit_settings extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_startpage);
        
        final Context ctxt = this;
        
        final Button createRuleButton = (Button) findViewById(R.id.create_rule);
        createRuleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {         	
               Intent createRuleIntent = new Intent(ctxt, com.buzzters.sosync.activity.edit_settingsmenu.class);              
               startActivity(createRuleIntent); 
            }
        });
        
    }
}