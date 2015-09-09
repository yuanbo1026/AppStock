package com.technisat.appstock.content.categorylist;

import com.technisat.appstock.R;
import com.technisat.appstock.content.contentlist.ContentListFragment;
import com.technisat.appstock.content.contentlist.MyAppsListFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyAppsPagerAdapter extends FragmentPagerAdapter {

	private Context c;
	
	private final String[] title = {"Installiert", "Installieren", "Update", "Alle"};

	
	public MyAppsPagerAdapter(FragmentManager fragmentManager, Context c) {
        super(fragmentManager);
		this.c = c;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return 4;
    }
    
	@Override
	public CharSequence getPageTitle(int position) {
		return title[position];
	}

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
    	ContentListFragment f = new ContentListFragment();
    	Bundle b = new Bundle();
        switch (position) {
        case 0:
        	f.setAction(c.getString(R.string.appstock_action_my_content));
        	b.putInt(MyAppsListFragment.FILTER_ARG, MyAppsListFragment.FILTER_INSTALL);
        	f.setArguments(b);
            return f;
        case 1:
        	f.setAction(c.getString(R.string.appstock_action_my_content));
        	b.putInt(MyAppsListFragment.FILTER_ARG, MyAppsListFragment.FILTER_INSTALLED);
        	f.setArguments(b);
            return f;
        case 2:
        	f.setAction(c.getString(R.string.appstock_action_my_content));
        	b.putInt(MyAppsListFragment.FILTER_ARG, MyAppsListFragment.FILTER_UPDATE);
        	f.setArguments(b);
            return f;
        default:
        	f.setAction(c.getString(R.string.appstock_action_my_content));
        	b.putInt(MyAppsListFragment.FILTER_ARG, MyAppsListFragment.FILTER_ALL);
        	f.setArguments(b);
            return f;
        }
    }

}
