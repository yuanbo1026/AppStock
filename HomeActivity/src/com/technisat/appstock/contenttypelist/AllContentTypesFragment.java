package com.technisat.appstock.contenttypelist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.technisat.appstock.R;
import com.technisat.appstock.app.ActionBarHelper;
import com.technisat.appstock.app.BaseDrawerActivity;
import com.technisat.appstock.app.BaseFragment;
import com.technisat.appstock.content.categorylist.CategoryListFragment;
import com.technisat.appstock.drawer.MyDrawer;
import com.technisat.appstock.entity.Category;
import com.technisat.appstock.exception.AppStockContentError;
import com.technisat.appstock.helper.ConnectionDetector;
import com.technisat.appstock.helper.StyleMessageDialog;
import com.technisat.appstock.search.NexxooSearch;
import com.technisat.appstock.utils.AlertDialogUtil;
import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;

public class AllContentTypesFragment extends BaseFragment {
	private static final String MAGAZINES_STATE = "all.content.types.magazines.state";
	private static final String VIDEOS_STATE = "all.content.types.videos.state";
	private static final String APPS_STATE = "all.content.types.apps.state";

	private ExpandableListView expListView;
	private ExpandableListAdapter listAdapter;
	private List<String> listDataHeader;
	private HashMap<String, List<Category>> listDataChild;

	private ArrayList<Category> magazines;
	private ArrayList<Category> videos;
	private ArrayList<Category> apps;

	public AllContentTypesFragment() {
		fragmentId = MyDrawer.BUTTON_ALL_CONTENT_TYPES;

		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<Category>>();

		apps = new ArrayList<Category>();
		magazines = new ArrayList<Category>();
		videos = new ArrayList<Category>();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		String[] header = getResources().getStringArray(
				R.array.appstock_contenttypes);
		for (String s : header) {
			listDataHeader.add(s);
		}

		listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader,
				listDataChild);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList(MAGAZINES_STATE, magazines);
		outState.putParcelableArrayList(VIDEOS_STATE, videos);
		outState.putParcelableArrayList(APPS_STATE, apps);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			magazines = savedInstanceState
					.getParcelableArrayList(MAGAZINES_STATE);
			apps = savedInstanceState.getParcelableArrayList(APPS_STATE);
			videos = savedInstanceState.getParcelableArrayList(VIDEOS_STATE);
		}
		setHasOptionsMenu(true);
	}

	public void handleData(JSONObject json) {
		apps = new ArrayList<Category>();
		magazines = new ArrayList<Category>();
		videos = new ArrayList<Category>();

		try {
			int count = json.getInt("count");
			for (int i = 0; i < count; i++) {
				JSONObject categortyJsonObject = json.getJSONObject("category"
						+ i);
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
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (AppStockContentError e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ConnectionDetector cd = new ConnectionDetector(getActivity());
		Boolean isInternetPresent = cd.isConnectingToInternet();
		if (isInternetPresent == false){
			AlertDialogUtil.show(getActivity());
			// no connection - render empty view
			
		}
		else
		{		
			// preparing list data
			prepareListData();
			populateViews();
	
			// Listview on child click listener
			expListView.setOnChildClickListener(new OnChildClickListener() {
				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupPosition, int childPosition, long id) {
	
					Bundle args = new Bundle();
					args.putString(
							getActivity().getString(
									R.string.appstock_extra_content_type),
							listDataHeader.get(groupPosition));
					args.putString(CategoryListFragment.TITLE_ARG, listDataChild.get(listDataHeader.get(groupPosition))
							.get(childPosition).getCategoryName());
					args.putParcelable(
							getActivity().getString(
									R.string.appstock_extra_category),
							listDataChild.get(listDataHeader.get(groupPosition))
									.get(childPosition));
	
					((BaseDrawerActivity) getActivity()).getNavigator()
							.showCategoryList(args);
	
					
					
					return false;
				}
			});
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		ActionBarHelper.setActionBarTitle(getActivity(), getActivity()
				.getActionBar(),
				getActivity()
						.getString(R.string.appstock_allcontenttypes_title));
		
		//((BaseDrawerActivity) getActivity()).setDrawerIndicatorEnabled(false);
		
	}

	private void populateViews() {
		listDataChild.put(listDataHeader.get(0), apps);
		listDataChild.put(listDataHeader.get(1), magazines);
		listDataChild.put(listDataHeader.get(2), videos);

		for (int i = 0; i < listAdapter.getGroupCount(); i++) {
			expListView.expandGroup(i);
		}

		listAdapter.setData(listDataChild);
		listAdapter.notifyDataSetChanged();
	}

	private void prepareListData() {
		NexxooWebservice.getCategories(true, 1,
				NexxooWebservice.CONTENTFILTER_ALL, new OnJSONResponse() {

					@Override
					public void onReceivedJSONResponse(JSONObject json) {
						handleData(json);
						populateViews();
					}

					@Override
					public void onReceivedError(String msg, int code) {
					}
				});
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_my_content, container,
				false);
		view.setBackgroundColor(getResources().getColor(R.color.AppStockDrawerLightGrey));
		expListView = (ExpandableListView) view
				.findViewById(R.id.exlv_content_list);
		expListView.setVisibility(View.VISIBLE);

		
//		int width = getResources().getDisplayMetrics().widthPixels;
//	    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
//	        expListView.setIndicatorBounds(width - getPixelValue(40), width - getPixelValue(10));
//	    } else {
//	        expListView.setIndicatorBoundsRelative(width - getPixelValue(40), width - getPixelValue(10));
//	    }
		
		view.findViewById(R.id.promotion_banner).setVisibility(View.VISIBLE);
		view.findViewById(R.id.promotion_banner).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((BaseDrawerActivity) getActivity()).getNavigator().showSpecialDeals();
			}
		});
//		View footer = inflater.inflate(R.layout.promo_banner_footer, null);
//		expListView.addFooterView(footer);	
		expListView.setAdapter(listAdapter);
		
		ConnectionDetector cd = new ConnectionDetector(getActivity());
		Boolean isInternetPresent = cd.isConnectingToInternet();
		if (isInternetPresent == false) {
			view.findViewById(R.id.exlv_content_list).setVisibility(View.INVISIBLE);
			view.findViewById(R.id.promotion_banner).setVisibility(View.INVISIBLE);
			view.findViewById(R.id.progress_spinner).setVisibility(View.VISIBLE);
		}

		populateViews();
		return view;
	}
	
	public int getPixelValue(int dp) {
	    final float scale = getActivity().getResources().getDisplayMetrics().density;
	    return (int) (dp * scale + 0.5f);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			getActivity().onBackPressed();
			return true;
		}
		return true;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.main, menu);
		NexxooSearch.initSearchBar(getActivity(), menu);
	}

	@Override
	public String getStackId() {
		return AllContentTypesFragment.class.getSimpleName();
	}
}
