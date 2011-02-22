package com.buzzters.sosync.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class MyOnItemSelectedListener implements OnItemSelectedListener {
	@Override
    public void onItemSelected(AdapterView<?> parent,
        View view, int pos, long id) {
		//String ans;
		Toast.makeText(parent.getContext(), "The selected value is " +
          parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
        //ans=parent.getItemAtPosition(pos).toString();
        //return ans;
    }
	@Override
    public void onNothingSelected(AdapterView parent) {
      // Do nothing.
    }
}