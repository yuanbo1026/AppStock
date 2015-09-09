package com.technisat.appstock.content.categorylist;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

import com.technisat.appstock.R;
import com.technisat.appstock.content.detail.ReloadFragment;
import com.technisat.appstock.content.detail.ReloadInterface;
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

@SuppressWarnings("rawtypes")
public class TopKostenlosFragment extends ReloadFragment {
	
	private List<Content> mList;
	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;
	private TextView tvEmptyText = null;
	private long categoryFilter = NexxooWebservice.CATEGORYFILTER_ALL;
	private ProgressBar progressSpinner;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	 
        View rootView = inflater.inflate(R.layout.fragment_top_kostenpflichtig, container, false);
        
        Bundle bundle = getArguments();
        if(bundle.containsKey(getString(R.string.appstock_extra_category))){
        	Category category = bundle.getParcelable(getString(R.string.appstock_extra_category));
        	categoryFilter = category.getCategoryId();
        }
        mList = new ArrayList<Content>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_in_tab);
        recyclerView.setHasFixedSize(true);
        
        recyclerView.addItemDecoration(new ItemDecoration(){
        	@Override
        	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        		super.getItemOffsets(outRect, view, parent, state);
        		outRect.inset(1, 1);
        	}
        });
        
        String screenType = getString(R.string.screen_type);
        if(screenType.equals("phone")){
        	layoutManager = new LinearLayoutManager(getActivity());
        } else {
        	layoutManager = new GridLayoutManager(getActivity(), 3);
        }
        
		recyclerView.setLayoutManager(layoutManager);
        
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
		
        final ProgressDialog ringProgressDialog = ProgressDialog
				.show(getActivity(),null,
						getActivity().getString(R.string.loading),
						true);
		ringProgressDialog.setCancelable(true);
        
        NexxooWebservice.getContent(true, 0, -1, NexxooWebservice.CONTENTFILTER_ALL, NexxooWebservice.CONTENTPRICE_FREE, categoryFilter, new OnJSONResponse() {
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
							Log.e(Nexxoo.TAG, "Fehler bei App "+i+": "+e.getMessage());
						}
					}
					ringProgressDialog.dismiss();
			        
				} catch (JSONException e) {
					Log.e(Nexxoo.TAG, "error: " + e.getMessage());
				}
				
				/**
				 * sort content list when category is magazine
				 */
				
				adapter = new ItemListRecycledAdapter(getActivity(), mList);
				recyclerView.setAdapter(adapter);			
		        
		        if(mList.size() < 1)
		        	tvEmptyText.setText(getString(R.string.appstock_topfree_nocontent));
		        else
		        	tvEmptyText.setVisibility(View.GONE);
			}
			@Override
			public void onReceivedError(String msg, int code) {
				ringProgressDialog.dismiss();
				Log.e(Nexxoo.TAG, "error bei getTestApps "+ msg);
			}
		});
        
        return rootView;
    }

	@Override
	public void reload(Content c) {
		//reload adapter
		if(c != null && mList.contains(c)){
			mList.set(mList.indexOf(c), c);
			recyclerView.setAdapter(new ItemListRecycledAdapter(getActivity(), mList));
			recyclerView.refreshDrawableState();
			recyclerView.getAdapter().notifyDataSetChanged();
		}
	}
}