package com.technisat.appstock.content.contentlist;

import com.astuetz.PagerSlidingTabStrip;
import com.technisat.appstock.R;
import com.technisat.appstock.app.BaseDrawerActivity;
import com.technisat.appstock.app.BaseFragment;
import com.technisat.appstock.content.categorylist.MyAppsPagerAdapter;
import com.technisat.appstock.content.categorylist.TabsPagerAdapter;
import com.technisat.appstock.entity.Category;
import com.technisat.appstock.helper.ConnectionDetector;
import com.technisat.appstock.imprint.ImprintFragment;
import com.technisat.appstock.utils.AlertDialogUtil;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MyAppsListFragment extends BaseFragment implements ActionBar.TabListener {

	private String mType;
	private ViewPager viewPager;
	private ActionBar actionBar;
	private MyAppsPagerAdapter mAdapter;
	private String[] mTabs;
	
	public static final String FILTER_ARG = "filter";
	public static final int FILTER_INSTALLED = 1;
	public static final int FILTER_UPDATE = 2;
	public static final int FILTER_INSTALL = 3;
	public static final int FILTER_ALL = 4;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	
		if(getArguments() != null){
			((BaseDrawerActivity) getActivity()).setDrawerIndicatorEnabled(false);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.activity_bycategory, container, false);
	}
	
	@Override
	public String getStackId() {
		return MyAppsListFragment.class.getSimpleName();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		getActivity().getActionBar().setHomeButtonEnabled(true);
		
		ConnectionDetector cd = new ConnectionDetector(getActivity());
		 
		Boolean isInternetPresent = cd.isConnectingToInternet();
		if (isInternetPresent == false){
			AlertDialogUtil.show(getActivity());
		}		

		viewPager = (ViewPager) getView().findViewById(R.id.pager);
		actionBar = getActivity().getActionBar();
		mTabs = getResources().getStringArray(R.array.appstock_categories);
		mAdapter = new MyAppsPagerAdapter(getChildFragmentManager(), getActivity());
		
		viewPager.setAdapter(mAdapter);
		viewPager.setOffscreenPageLimit(4);
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) getView().findViewById(R.id.tabs);
		tabs.setViewPager(viewPager);

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				//actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

}
