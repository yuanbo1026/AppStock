package com.technisat.appstock.content.categorylist;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.technisat.appstock.R;
import com.technisat.appstock.content.detail.ContentDetailActivity;
import com.technisat.appstock.entity.App;
import com.technisat.appstock.entity.Category;
import com.technisat.appstock.entity.Content;
import com.technisat.appstock.entity.Magazine;
import com.technisat.appstock.entity.Video;
import com.technisat.appstock.exception.AppStockContentError;
import com.technisat.appstock.helper.ConnectionDetector;
import com.technisat.appstock.utils.AlertDialogUtil;
import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;
import com.technisat.constants.Nexxoo;

public class FeaturedFragment extends Fragment {
	
	private List<Content> mList;
	private ListView lv = null;
	private TextView tvEmptyText = null;
	private long categoryFilter = NexxooWebservice.CATEGORYFILTER_ALL;
	private ProgressBar progressSpinner;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        View rootView = inflater.inflate(R.layout.fragment_featured, container, false);
        
        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey(getString(R.string.appstock_extra_category))){
	        Category category = bundle.getParcelable(getString(R.string.appstock_extra_category));
	        categoryFilter = category.getCategoryId();
	    }
        mList = new ArrayList<Content>();
        
        lv = (ListView) rootView.findViewById(R.id.lv_in_tab);
        tvEmptyText = (TextView) rootView.findViewById(R.id.tv_emptyText);

		progressSpinner = (ProgressBar) rootView.findViewById(R.id.progress_spinner);
		ConnectionDetector cd = new ConnectionDetector(getActivity());
		Boolean isInternetPresent = cd.isConnectingToInternet();
		if (isInternetPresent == false){
			AlertDialogUtil.show(getActivity());
			progressSpinner.setIndeterminate(true);
			progressSpinner.bringToFront();
			progressSpinner.setVisibility(View.VISIBLE);
		}		
		
        NexxooWebservice.getContent(true, categoryFilter,true, false, new OnJSONResponse() {
			@Override
			public void onReceivedJSONResponse(JSONObject json) {
				
				try {
					int count = json.getInt("count");
					
					for(int i=0;i<count;i++){
						try {
							JSONObject jsonContentObj = json.getJSONObject("content" + i);
							
							switch (jsonContentObj.getInt(Content.CONTENTTYPE)) {
							case App.CONTENTTYPE:
								mList.add(new App(jsonContentObj));
								break;
							case 2:
								mList.add(new Magazine(jsonContentObj));
								break;
							case 3:
								mList.add(new Video(jsonContentObj));
								break;
							}
						} catch (AppStockContentError e) {
							Log.d(Nexxoo.TAG, "Fehler bei App " + i + ": " + e.getMessage());
						}
					}
			        
				} catch (JSONException e) {
					Log.d(Nexxoo.TAG, "error!"+ e.getMessage());
				}
				
				/**
				 * sort content list when category is magazine
				 */
				
				FeaturedItemListAdapter adapter = new FeaturedItemListAdapter(getActivity(), 0, mList);
		        lv.setAdapter(adapter);
		        
		        lv.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						Intent intent = new Intent(getActivity(), ContentDetailActivity.class);
						
						
						if(lv.getItemAtPosition(arg2) instanceof App){
//							Log.d(Nexxoo.TAG, "ich bin eine app");
		            		intent.putExtra(getString(R.string.appstock_extra_content), (App) lv.getItemAtPosition(arg2));
		            	} else if(lv.getItemAtPosition(arg2) instanceof Magazine){
//		            		Log.d(Nexxoo.TAG, "ich bin ein magazin");
		            		intent.putExtra(getString(R.string.appstock_extra_content), (Magazine) lv.getItemAtPosition(arg2));
		            	} else if(lv.getItemAtPosition(arg2) instanceof Video) {
//		            		Log.d(Nexxoo.TAG, "ich bin ein video");
		            		intent.putExtra(getString(R.string.appstock_extra_content), (Video) lv.getItemAtPosition(arg2));
		            	} else {
//		            		Log.d(Nexxoo.TAG, "ich bin ein content :(");
		            	}
						getActivity().startActivity(intent);
					}
				});
		        if(mList.size() < 1)
		        	tvEmptyText.setText(getString(R.string.appstock_featured_nocontent));
		        else
		        	tvEmptyText.setVisibility(View.GONE);
			}
			
			@Override
			public void onReceivedError(String msg, int code) {
				Log.e(Nexxoo.TAG, "error bei getTestApps "+ msg);
			}
		});
        
        return rootView;
    }
}