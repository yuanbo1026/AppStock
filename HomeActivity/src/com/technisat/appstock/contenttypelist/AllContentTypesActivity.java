package com.technisat.appstock.contenttypelist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.RelativeLayout;

import com.technisat.appstock.R;

import com.technisat.appstock.drawer.MyDrawer;
import com.technisat.appstock.entity.Category;
import com.technisat.appstock.exception.AppStockContentError;
import com.technisat.appstock.search.NexxooSearch;
import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;

/************************************************
 * Activity to display An Expandable-list 		*
 * @author b.nolde								*
 ************************************************/
public class AllContentTypesActivity extends FragmentActivity {
	
	private MyDrawer mMyDrawer;
	private DrawerLayout mDrawerLayout;
	
	private ExpandableListAdapter listAdapter;
	private ExpandableListView expListView;
	private List<String> listDataHeader;
	private HashMap<String, List<Category>> listDataChild;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout_container);
		
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
		
		mMyDrawer = new MyDrawer(this);
        mDrawerLayout = mMyDrawer.getDrawerLayout();
        
        LayoutInflater inflater = getLayoutInflater();
        RelativeLayout container = (RelativeLayout) findViewById(R.id.frame_container);
    	inflater.inflate(R.layout.activity_my_content, container);
    	
    	expListView = (ExpandableListView) findViewById(R.id.exlv_content_list);
        // preparing list data
        prepareListData();
        
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {}
        });
 
        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {}
        });
        
     // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
//                Intent intent = new Intent(getApplicationContext(), CategoryListActivity.class);
//                intent.putExtra(getString(R.string.appstock_extra_content_type), listDataHeader.get(groupPosition));
//                intent.putExtra(getString(R.string.appstock_extra_category), listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition));
//                startActivity(intent);
                return false;
            }
        });
	}
	
	/****************************
     * Preparing the list data	*
     ****************************/
    private void prepareListData() {
    	listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Category>>();
        
        // Adding child data
        // Adding child data
        String[] header = getResources().getStringArray(R.array.appstock_contenttypes);
        for (String s : header) {
			listDataHeader.add(s);
		}
        
        NexxooWebservice.getCategories(true, 1, NexxooWebservice.CONTENTFILTER_ALL, new OnJSONResponse() {
			
			@Override
			public void onReceivedJSONResponse(JSONObject json) {
				List<Category> apps = new ArrayList<Category>();
				List<Category> magazines = new ArrayList<Category>();
				List<Category> videos = new ArrayList<Category>();
				try {
					int count = json.getInt("count");
					for(int i=0; i<count;i++){
						JSONObject categortyJsonObject = json.getJSONObject("category" + i);
						switch (categortyJsonObject.getInt("contentTypeId")) {
						case 1:
							apps.add(new Category(categortyJsonObject));
							break;
						case 2:
							magazines.add(new Category(categortyJsonObject));
							break;
						case 3:
							videos.add(new Category(categortyJsonObject));
							break;
						default:
							break;
						}
					}
					listDataChild.put(listDataHeader.get(0), apps); // Header, Child data
			        listDataChild.put(listDataHeader.get(1), magazines);
			        listDataChild.put(listDataHeader.get(2), videos);
			        
			        listAdapter = new ExpandableListAdapter(getApplicationContext(), listDataHeader, listDataChild);
			        expListView.setAdapter(listAdapter);
			        
			        for(int i=0; i < listAdapter.getGroupCount(); i++)
			        	expListView.expandGroup(i);
				} catch (JSONException e) {
					e.printStackTrace();
				}catch (AppStockContentError e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onReceivedError(String msg, int code) { }
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
		// Handle action bar actions click
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