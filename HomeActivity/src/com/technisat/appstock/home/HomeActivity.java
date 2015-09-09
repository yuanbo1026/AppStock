package com.technisat.appstock.home;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.technisat.appstock.R;
import com.technisat.appstock.content.categorylist.FeaturedItemListAdapter;
import com.technisat.appstock.content.detail.ContentDetailActivity;
import com.technisat.appstock.drawer.MyDrawer;
import com.technisat.appstock.entity.App;
import com.technisat.appstock.entity.Content;
import com.technisat.appstock.entity.Magazine;
import com.technisat.appstock.entity.Video;
import com.technisat.appstock.exception.AppStockContentError;
import com.technisat.appstock.search.NexxooSearch;
import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;
import com.technisat.constants.Nexxoo;

/************************************************
 * Activity to display start screen.			*
 * Is displays a list of all featured content	*
 * items from the database						*
 * ( set to fixed number if db gets to big )	*
 ************************************************/
public class HomeActivity extends FragmentActivity {
	
	private MyDrawer mMyDrawer;
	private DrawerLayout mDrawerLayout;
	
	private ListView lvApp = null;
	private List<Content> mList = null;
	private FeaturedItemListAdapter adapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout_container);
		
        getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("");
        
		mMyDrawer = new MyDrawer(this);
        mDrawerLayout = mMyDrawer.getDrawerLayout();
        LayoutInflater inflater = getLayoutInflater();
        RelativeLayout container = (RelativeLayout) findViewById(R.id.frame_container);
    	inflater.inflate(R.layout.activity_home, container);
    	
    	mList = new ArrayList<Content>();
    	lvApp = (ListView) findViewById(R.id.lv_home_feat_app);
    	NexxooWebservice.getContent(true,
    			NexxooWebservice.CATEGORYFILTER_ALL,
    			true,
    			false,
    			new OnJSONResponse() {
    		
			@Override
			public void onReceivedJSONResponse(JSONObject json) {
				
				try {
					int count = json.getInt("count");
					
					for(int i=0;i<count;i++){
						try {
							JSONObject jsonContentObj = json.getJSONObject("content"+i);
							
							switch (jsonContentObj.getInt(Content.CONTENTTYPE)) {
							case App.CONTENTTYPE:
								mList.add(new App(jsonContentObj));
								break;
							case Magazine.CONTENTTYPE:
								mList.add(new Magazine(jsonContentObj));
								break;
							case Video.CONTENTTYPE:
								mList.add(new Video(jsonContentObj));
								break;
							}
						} catch (AppStockContentError e) {
							Log.e(Nexxoo.TAG, "Fehler bei App " + i + ": " + e.getMessage());
						}
					}
			        
				} catch (JSONException e) {
					Log.d(Nexxoo.TAG, e.getMessage());
				}
				if(mList.size() > 0){
					TextView tvEmpty = (TextView) findViewById(R.id.tv_home_empty);
					tvEmpty.setVisibility(View.GONE);
				}
				
				adapter = new FeaturedItemListAdapter(HomeActivity.this, 0, mList);
				lvApp.setAdapter(adapter);
		        
				lvApp.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						Intent intent = new Intent(HomeActivity.this, ContentDetailActivity.class);
						
						if(lvApp.getItemAtPosition(arg2) instanceof App){
		            		intent.putExtra(getString(R.string.appstock_extra_content), (App) lvApp.getItemAtPosition(arg2));
		            	} else if(lvApp.getItemAtPosition(arg2) instanceof Magazine){
		            		intent.putExtra(getString(R.string.appstock_extra_content), (Magazine) lvApp.getItemAtPosition(arg2));
		            	} else if(lvApp.getItemAtPosition(arg2) instanceof Video) {
		            		intent.putExtra(getString(R.string.appstock_extra_content), (Video) lvApp.getItemAtPosition(arg2));
		            	}
						startActivity(intent);
					}
				});
			}
			
			@Override
			public void onReceivedError(String msg, int code) {
				Log.d(Nexxoo.TAG, msg);
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		
	    NexxooSearch.initSearchBar(this, menu);
		return true;
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        	if (!mDrawerLayout.isDrawerOpen(Gravity.START)) {
        		mDrawerLayout.openDrawer(Gravity.START);
        	}else{
        		mDrawerLayout.closeDrawer(Gravity.START);
        	}
            return true;
        default:
        	return true;
        }
	}
	
	@Override
	public void onResume(){
		super.onResume();
		mMyDrawer.updateView();
	}
}