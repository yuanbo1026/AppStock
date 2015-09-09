package com.technisat.appstock.drawer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.technisat.appstock.R;

public class NavDrawerListAdapter extends BaseAdapter {
     
    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private ArrayList<NavDrawerItem> navDrawerItemsVisible = new ArrayList<NavDrawerItem>();
     
    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
        this.context = context;
        this.navDrawerItems = navDrawerItems;
        
        for(NavDrawerItem item : this.navDrawerItems){
        	if(item.isVisible()){
        		this.navDrawerItemsVisible.add(item);
        	}
        }
    }
 
    @Override
    public int getCount() {
        return navDrawerItemsVisible.size();
    }
 
    @Override
    public Object getItem(int position) { 
        return navDrawerItemsVisible.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    public void setItemVisibility(int id, boolean isVisible){
    	navDrawerItemsVisible.clear();
    	for(NavDrawerItem item : navDrawerItems){
    		if(item.getId() == id){
    			item.setVisiblity(isVisible);    			
    		}
    		
    		if (item.isVisible()){
				navDrawerItemsVisible.add(item);
			}
    	}
    	notifyDataSetChanged();
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
        LayoutInflater mInflater = (LayoutInflater)
        context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.drawer_list_item, null);
	    ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
	    TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
	    
	    
	    imgIcon.setImageResource(navDrawerItemsVisible.get(position).getIcon());
	    txtTitle.setText(navDrawerItemsVisible.get(position).getTitle());
	    if(navDrawerItemsVisible.get(position).getId() > MyDrawer.BUTTON_SPECIAL_DEALS){
	    	convertView.setBackgroundColor(context.getResources().getColor(R.color.AppStockDrawerLightGrey));
	    	txtTitle.setTextColor(context.getResources().getColor(R.color.AppStockDrawerLightBlue));
	    }
        return convertView;
    }
}