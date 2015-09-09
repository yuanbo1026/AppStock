package com.technisat.appstock.content.contentlist;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.technisat.appstock.R;
import com.technisat.appstock.content.categorylist.ItemView;
import com.technisat.appstock.content.detail.ContentItemView;
import com.technisat.appstock.entity.Content;

public class MyContentExpandableListAdapter extends BaseExpandableListAdapter {
	
    private Context mContext;
    private List<String> mListDataHeader; // header titles
    // child data in format of header title, child
    private HashMap<String, List<?>> mListDataChild;
	private boolean shouldColorCategories;
    
    public MyContentExpandableListAdapter(Context context, List<String> listDataHeader,
            HashMap<String, List<?>> listChildData) {
        this.mContext = context;
        this.mListDataHeader = listDataHeader;
        this.mListDataChild = listChildData;
    }
    
	@Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .get(childPosititon);
    }
	
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    
    @Override
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
    	Content content = null;
    	if(getChild(groupPosition, childPosition) instanceof Content){
    		content = (Content) getChild(groupPosition, childPosition);
    	}
    	
    	ItemView itemView = null;
		
		if(convertView != null){
			itemView = (ItemView) convertView;
		} else {
			itemView = new ItemView(mContext);
		}
		
		if(isLastChild){
			itemView.setPadding(0, 0, 0, 40);
		} else {
			itemView.setPadding(0, 0, 0, 0);
		}
		
		itemView.setContent(content);
		itemView.findViewById(R.id.item_container).setBackgroundColor(mContext.getResources().getColor(R.color.RealWhite));
		return itemView;
    }
    
    @Override
    public int getChildrenCount(int groupPosition) {
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .size();
    }
    
    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }
    
    @Override
    public int getGroupCount() {
        return this.mListDataHeader.size();
    }
    
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.ex_listview_header, null);
        }
 
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setText(headerTitle);
        
    	int color = 0;
    	switch (groupPosition){
    		case 0: 
    			color = mContext.getResources().getColor(R.color.AppStockDrawerLightBlue);
    			break;
    		case 1:
    			color = mContext.getResources().getColor(R.color.AppStockDrawerGreen);
    			break;
    		case 2:
    			color = mContext.getResources().getColor(R.color.AppStockDrawerTrueBlue);
    			break;
    	}
    	if(color != 0){
        	convertView.setBackgroundColor(color);
        	lblListHeader.setTextColor(mContext.getResources().getColor(R.color.RealWhite));
        }
    	
    	if(isExpanded){
    		((ImageView) convertView.findViewById(R.id.group_indicator)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.arrow_white_down));
    	} else {
    		((ImageView) convertView.findViewById(R.id.group_indicator)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.arrow_white_right));
    	}    	
 
        return convertView;
    }
    
    public void setColoredCategories(){
    	shouldColorCategories = true;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

	public void setData(HashMap<String, List<?>> listDataChildWithContent, List<String> listDataHeader) {
		this.mListDataChild = listDataChildWithContent;
		this.mListDataHeader = listDataHeader;
		notifyDataSetChanged();
	}
}