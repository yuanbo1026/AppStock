package com.technisat.appstock.search;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.SearchView;

import com.technisat.appstock.R;

public class NexxooSearch {

	public static void initSearchBar(Activity activity, Menu menu){
		SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	    searchView.setSearchableInfo( searchManager.getSearchableInfo(activity.getComponentName()));	   
	    
	    int searchIconId = searchView.getContext().getResources().
                getIdentifier("android:id/search_button", null, null);
	    ImageView searchIcon = (ImageView) searchView.findViewById(searchIconId);
	    searchIcon.setImageResource(R.drawable.ic_action_search_nx);
	}
}
