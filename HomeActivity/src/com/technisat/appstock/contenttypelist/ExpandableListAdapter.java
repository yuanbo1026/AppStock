package com.technisat.appstock.contenttypelist;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.technisat.appstock.R;
import com.technisat.appstock.entity.Category;
 
public class ExpandableListAdapter extends BaseExpandableListAdapter {
 
    private Context mContext;
    private List<String> mListDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Category>> mListDataChild;
    private HashMap<Long, Integer> mCategoryIcons;
 
    public ExpandableListAdapter(Context context, List<String> listDataHeader,
            HashMap<String, List<Category>> listChildData) {
        this.mContext = context;
        this.mListDataHeader = listDataHeader;
        this.mListDataChild = listChildData;        
        
        mCategoryIcons = new HashMap<Long, Integer>();
        mCategoryIcons.put(1L, R.drawable.ic_cat_sport);
        mCategoryIcons.put(2L, R.drawable.ic_cat_gehirnjogging);
        mCategoryIcons.put(6L, R.drawable.ic_cat_finances);
        mCategoryIcons.put(5L, R.drawable.ic_cat_music);
    }
    
    public void setData(HashMap<String, List<Category>> data){
    	mListDataChild = data;
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
    	
        final Category childCategory = (Category) getChild(groupPosition, childPosition);
 
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.ex_listview_child, null);
        }
 
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        
        ImageView icon = (ImageView) convertView.findViewById(R.id.iv_child_item_icon);        
        if(mCategoryIcons.containsKey(childCategory.getCategoryId())){
        	icon.setImageDrawable(mContext.getResources().getDrawable(mCategoryIcons.get(childCategory.getCategoryId())));
        } else {
        	icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_cat_wein));
        }
        
        if(isLastChild && getGroupCount() != groupPosition + 1){
        	convertView.findViewById(R.id.v_list_divider).setVisibility(View.INVISIBLE);
        } else {
        	convertView.findViewById(R.id.v_list_divider).setVisibility(View.VISIBLE);
        }       
 
        txtListChild.setText(childCategory.getCategoryName());
        return convertView;
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
        lblListHeader.setText(headerTitle.toUpperCase());
       
        if(isExpanded){
    		((ImageView) convertView.findViewById(R.id.group_indicator)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.arrow_grey_down));
    	} else {
    		((ImageView) convertView.findViewById(R.id.group_indicator)).setImageDrawable(mContext.getResources().getDrawable(R.drawable.arrow_grey_right));
    	}
 
        return convertView;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}