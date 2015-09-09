package com.technisat.appstock.content.categorylist;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.technisat.appstock.R;
import com.technisat.appstock.app.ActionBarHelper;
import com.technisat.appstock.app.BaseDrawerActivity;
import com.technisat.appstock.app.BaseFragment;
import com.technisat.appstock.app.TypefaceSpan;
import com.technisat.appstock.entity.Category;
import com.technisat.appstock.helper.ConnectionDetector;
import com.technisat.appstock.helper.StyleMessageDialog;
import com.technisat.appstock.search.NexxooSearch;
import com.technisat.appstock.utils.AlertDialogUtil;

public class CategoryListFragment extends BaseFragment implements
		ActionBar.TabListener {
	
	public static final String TITLE_ARG = "title";

	private String mType;
	private ViewPager viewPager;
	private ActionBar actionBar;
	private TabsPagerAdapter mAdapter;
	private String[] mTabs;
	public CategoryListFragment() {
		fragmentId = 38;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view =inflater.inflate(R.layout.activity_bycategory, container, false);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	
		if(getArguments() != null){
			((BaseDrawerActivity) getActivity()).setDrawerIndicatorEnabled(false);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	public void reload() {
		viewPager.refreshDrawableState();
		mAdapter.notifyDataSetChanged();
		viewPager.invalidate();
	}
	
	public Fragment getCurrentTabFragment() {
		return mAdapter.getItem(viewPager.getCurrentItem());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Bundle args = getArguments();
		Category category = null;
		if(args != null){
			category = args
					.getParcelable(getString(R.string.appstock_extra_category));
			mType = args.getString(getString(R.string.appstock_extra_content_type));
			String title = mType;// + " - " + category.getCategoryName();
		}
		
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		getActivity().getActionBar().setHomeButtonEnabled(true);
		
//		ActionBarHelper.setActionBarTitle(getActivity(), getActivity().getActionBar(), "APPS");
		
		// set application title (if arg is present)
		if(getArguments() != null) {
			// replace app title with "title" arg
			String appTilte = getArguments().getString(TITLE_ARG, getString(R.string.default_title));
			getActivity().getActionBar().setTitle(appTilte.toUpperCase());
		}else{
			getActivity().getActionBar().setTitle(getString(R.string.default_title));
		}
		ConnectionDetector cd = new ConnectionDetector(getActivity());
		 
		Boolean isInternetPresent = cd.isConnectingToInternet();
		if (isInternetPresent == false){
			AlertDialogUtil.show(getActivity());
		}		

		viewPager = (ViewPager) getView().findViewById(R.id.pager);
		actionBar = getActivity().getActionBar();
		mTabs = getResources().getStringArray(R.array.appstock_categories);
		mAdapter = new TabsPagerAdapter(getChildFragmentManager(),
				getActivity(), category, mTabs);
		
		viewPager.setAdapter(mAdapter);
		viewPager.setOffscreenPageLimit(3);
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.main, menu);
		NexxooSearch.initSearchBar(getActivity(), menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			getActivity().onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public String getStackId() {
		return CategoryListFragment.class.getSimpleName();
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
}