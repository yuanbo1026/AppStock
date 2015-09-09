package com.technisat.appstock.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.technisat.appstock.R;
import com.technisat.appstock.home.HomeActivity;

/****************************************************
 * Activity that displays ( new ) developers		*
 * how they can publish their content in the		*
 * APP STOCK.										*
 * Activity is called after developer registration	*
 * or from settings if user is developer			*
 ****************************************************/
public class DeveloperInfoActivity extends Activity {
	
	//TODO: remove this function and turn on _onCreate if You want to view register as developer view
	//@Override
	protected void _onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dummy);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
	}
	//TODO: change this function to onCreate if You want to view register as developer view
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_developer_info_new);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        TypedValue tv = new TypedValue();
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
		    int height = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
			
			ImageView icon = (ImageView) findViewById(android.R.id.home);
			FrameLayout.LayoutParams iconLp = (FrameLayout.LayoutParams) icon.getLayoutParams();
			iconLp.topMargin = iconLp.bottomMargin = (int) (height * 0.08f);
			iconLp.height = LayoutParams.MATCH_PARENT;
			iconLp.width = LayoutParams.MATCH_PARENT;
			icon.setLayoutParams( iconLp );
		}
        
        Button btnBack = (Button) findViewById(R.id.btn_devinfo_back);
        btnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
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
}