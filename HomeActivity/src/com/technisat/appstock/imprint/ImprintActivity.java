package com.technisat.appstock.imprint;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.technisat.appstock.R;

/************************************
 * Activity to display the imprint	*
 ************************************/
public class ImprintActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout_container);
		
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        LayoutInflater inflater = getLayoutInflater();
        RelativeLayout container = (RelativeLayout) findViewById(R.id.frame_container);
    	inflater.inflate(R.layout.activity_imprint, container);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case android.R.id.home:
	        finish();
	        return true;
        default:
        	return true;
        }
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
}