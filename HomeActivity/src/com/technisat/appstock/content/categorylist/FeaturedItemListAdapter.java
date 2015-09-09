package com.technisat.appstock.content.categorylist;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.technisat.appstock.entity.Content;

/**
 * Handles the list layout part of that activity
 */
public class FeaturedItemListAdapter extends ArrayAdapter<Content>{
	
	private Context mContext;
	private List<Content> mList;
	private Content mContent = null;
	
	public FeaturedItemListAdapter(Context context, int textViewResourceId, List<Content> objects) {
		super(context, textViewResourceId, objects);
		
		mContext = context;
		mList = objects;
	}
	
	public void setData(List<Content> data){
		mList = data;
	}
	
	

	@Override
	public Content getItem(int position) {		
		return mList.get(position);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
		
    	if(mList.get(position) instanceof Content){
    		mContent = (Content) mList.get(position);
    	}
    	
    	FeaturedItemView itemView = null;
		
		if(convertView != null){
			itemView = (FeaturedItemView) convertView;
		} else {
			itemView = new FeaturedItemView(mContext);
		}
		itemView.setContent(mContent);
		
		return itemView;
	  }
}