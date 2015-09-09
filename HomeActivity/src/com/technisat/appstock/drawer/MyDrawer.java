package com.technisat.appstock.drawer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.technisat.appstock.R;
import com.technisat.appstock.app.BaseFragment;
import com.technisat.appstock.content.categorylist.CategoryListFragment;
import com.technisat.appstock.content.contentlist.ContentListFragment;
import com.technisat.appstock.content.contentlist.MyAppsListFragment;
import com.technisat.appstock.contenttypelist.AllContentTypesFragment;
import com.technisat.appstock.entity.Account;
import com.technisat.appstock.entity.singleton.AccountLoggedIn;
import com.technisat.appstock.exception.AppStockSingletonException;
import com.technisat.appstock.home.HomeFragment;
import com.technisat.appstock.imprint.ImprintFragment;
import com.technisat.appstock.login.LoginActivity;
import com.technisat.appstock.settings.SettingsFragment;

/****************************************************************
 * Class to display menu slider and handle menu item clicks *
 ****************************************************************/
@SuppressLint("InflateParams")
public class MyDrawer {

	public static final int BUTTON_ACCOUNT = 0;
	public static final int BUTTON_HOME = 1;
	public static final int BUTTON_ALL_CONTENT_TYPES = 2;
	public static final int BUTTON_MY_CONTENT = 3;
	public static final int BUTTON_WISHLIST = 4;
	public static final int BUTTON_SPECIAL_DEALS = 5;
	public static final int BUTTON_SETTINGS = 6;
	public static final int BUTTON_PAYMENT = 7;
	public static final int BUTTON_IMPRINT = 8;

	private static final int FRAGMENT_CONTAINER_ID = R.id.frame_container;
	private static final String FRAGMENT_BUNDLE_KEY = "fragment.cache";
	protected static final String TAG = MyDrawer.class.getSimpleName();

	private Map<Integer, Bundle> mFragmentCache;

	private String[] mNavMenuTitles;
	private TypedArray mNavMenuIcons;
	private FragmentActivity mActivity;
	private ListView mDrawerList;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private View mHeader;
	private TextView mHeaderAccountName, mHeaderEdit;
	private ArrayList<NavDrawerItem> mNavDrawerItems;
	private NavDrawerListAdapter mAapter;
	private BaseFragment currentFragment;

	@SuppressLint("UseSparseArrays")
	public MyDrawer(FragmentActivity activity) {
		mActivity = activity;
		mFragmentCache = new HashMap<Integer, Bundle>();
		init();
	}

	public void setcurrentFragment(BaseFragment fragment) {
		currentFragment = fragment;
	}

	public ListView getDrawerList() {
		return mDrawerList;
	}

	public BaseFragment getCurrentDrawer() {
		return currentFragment;
	}

	public DrawerLayout getDrawerLayout() {
		return mDrawerLayout;
	}

	public ActionBarDrawerToggle getDrawerToggle() {
		return mDrawerToggle;
	}

	public void setDrawerIndicatorEnabled(boolean flag) {
		mDrawerToggle.setDrawerIndicatorEnabled(flag);
	}

	public void init() {
		mNavMenuTitles = mActivity.getResources().getStringArray(
				R.array.nav_drawer_items);
		mNavMenuIcons = mActivity.getResources().obtainTypedArray(
				R.array.nav_drawer_icons);

		boolean isLoggedIn = false;
		if (AccountLoggedIn.isSet()) {
			isLoggedIn = true;
		}
		/****************************************
		 * setting all menu items * ( names and icons in strings.xml ) *
		 ****************************************/
		mNavDrawerItems = new ArrayList<NavDrawerItem>();
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[0], mNavMenuIcons
				.getResourceId(0, -1), BUTTON_HOME, true));
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[1], mNavMenuIcons
				.getResourceId(1, -1), BUTTON_ALL_CONTENT_TYPES, true));
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[2], mNavMenuIcons
				.getResourceId(2, -1), BUTTON_MY_CONTENT, isLoggedIn));
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[3], mNavMenuIcons
				.getResourceId(3, -1), BUTTON_WISHLIST, isLoggedIn));
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[4], mNavMenuIcons
				.getResourceId(4, -1), BUTTON_SPECIAL_DEALS, true));
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[5], mNavMenuIcons
				.getResourceId(5, -1), BUTTON_SETTINGS, isLoggedIn));
		// mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[6],
		// mNavMenuIcons.getResourceId(6, -1), BUTTON_PAYMENT, true));
		mNavDrawerItems.add(new NavDrawerItem(mNavMenuTitles[6], mNavMenuIcons
				.getResourceId(6, -1), BUTTON_IMPRINT, true));
		mNavMenuIcons.recycle();

		mHeader = mActivity.getLayoutInflater().inflate(R.layout.drawer_header,
				null);
		mHeaderAccountName = (TextView) mHeader
				.findViewById(R.id.txt_account_name);

		mHeaderEdit = (TextView) mHeader.findViewById(R.id.txt_account_edit);
		updateHeader();

		mDrawerList = (ListView) mActivity.findViewById(R.id.list_slidermenu);
		mDrawerList.addHeaderView(mHeader);
		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		mAapter = new NavDrawerListAdapter(mActivity.getApplicationContext(),
				mNavDrawerItems);
		mDrawerList.setAdapter(mAapter);

		mDrawerLayout = (DrawerLayout) mActivity
				.findViewById(R.id.drawer_layout);

		mDrawerToggle = new ActionBarDrawerToggle(mActivity, mDrawerLayout,
				R.drawable.drawer_up_button, R.string.app_name,
				R.string.app_name) {

			@Override
			public boolean onOptionsItemSelected(MenuItem item) {
				if (item != null && item.getItemId() == android.R.id.home) {
					if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
						mDrawerLayout.closeDrawer(Gravity.START);
					} else {
						mDrawerLayout.openDrawer(Gravity.START);
					}
				}
				return false;
			}

			public void onDrawerClosed(View view) {
				currentFragment.onDrawerClosed();
			}

			public void onDrawerOpened(View drawerView) {
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState();
	}

	/************************************
	 * Slide menu item click listener *
	 ************************************/
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (position == 0) {
				if (!AccountLoggedIn.isSet()) {
					if (!(mActivity instanceof LoginActivity)) {
						Intent intent = new Intent(mActivity,
								LoginActivity.class);
						mActivity.startActivity(intent);
					}
					mDrawerLayout.closeDrawer(Gravity.START);
				} else {
					// show account site
					displayView(BUTTON_SETTINGS);
				}
			} else {
				NavDrawerItem item = (NavDrawerItem) mAapter
						.getItem(position - 1);
				if (item != null) {
					if (currentFragment != null
							&& currentFragment.getFragmentId() == item.getId()) {
						mDrawerLayout.closeDrawer(Gravity.START);
					} else {
						displayView(item.getId());
					}
				} else {
					mDrawerLayout.closeDrawer(Gravity.START);
				}
			}
		}
	}

	public void showCategoryList(Bundle args) {
		BaseFragment fragment = new CategoryListFragment();
		fragment.setArguments(args);
		showFragment(fragment, true);
	}

	public void showSearchResults(Intent intent) {
		BaseFragment fragment = new ContentListFragment(intent);
		showFragment(fragment, true);
	}

	public void showFragment(BaseFragment fragment, boolean addToBackStack) {
		FragmentManager fm = mActivity.getSupportFragmentManager();
		
		if(fragment.getStackId().equals(currentFragment.getStackId())) {
			Log.d("JD", "ignore " + fragment.getStackId());
			
			fm.popBackStack(fragment.getStackId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);

				fm.beginTransaction()
				.setCustomAnimations(android.R.anim.fade_in,
						android.R.anim.fade_out)
				.replace(R.id.frame_container, fragment)
				.addToBackStack(fragment.getStackId()).commit();
		}else{
			fm.beginTransaction()
				.setCustomAnimations(android.R.anim.fade_in,
						android.R.anim.fade_out)
				.replace(R.id.frame_container, fragment)
				.addToBackStack(fragment.getStackId()).commit();
		}
		currentFragment = fragment;
	}


	public void saveFragmentBundle(BaseFragment fragment) {
		FragmentManager fm = mActivity.getSupportFragmentManager();
		Bundle bundle = new Bundle();
		fm.putFragment(bundle, FRAGMENT_BUNDLE_KEY, fragment);
		mFragmentCache.put(fragment.getFragmentId(), bundle);
		mDrawerLayout.closeDrawer(Gravity.START);
	}

	private BaseFragment getFragmentForId(int id) {
		switch (id) {
		case BUTTON_HOME:
			return new CategoryListFragment();
		case BUTTON_ALL_CONTENT_TYPES:
			return new AllContentTypesFragment();
		case BUTTON_SETTINGS:
			return new SettingsFragment();
		case BUTTON_IMPRINT:
			return new ImprintFragment();
		case BUTTON_MY_CONTENT:
			return new MyAppsListFragment();
		case BUTTON_WISHLIST:
		case BUTTON_SPECIAL_DEALS:
			return new ContentListFragment();
		default:
			return null;
		}
	}
	
	public void showSpecialDeals(){
		displayView(BUTTON_SPECIAL_DEALS);
	}

	public void displayView(int id) {
		FragmentManager fm = mActivity.getSupportFragmentManager();
		BaseFragment newFragment = null;

//		try {
//			newFragment = (BaseFragment) fm.getFragment(mFragmentCache.get(id),
//					FRAGMENT_BUNDLE_KEY);
//		} catch (Exception ex) {
//			ex.printStackTrace();
		
		/**
		 * magic fix
		 * prevent home back loop (twice).
		 * if current fragment is home and user will press home
		 * in menu - just ignore and close drawer.
		 */
		if (id == BUTTON_HOME && currentFragment instanceof CategoryListFragment
				&& currentFragment.getArguments() == null) {
			mDrawerLayout.closeDrawer(Gravity.START);
			return;
		}
		
		newFragment = getFragmentForId(id);
		

		if (newFragment instanceof ContentListFragment) {
			String action = null;
			switch (id) {
			case BUTTON_MY_CONTENT:
				action = mActivity
						.getString(R.string.appstock_action_my_content);
				break;
			case BUTTON_WISHLIST:
				action = mActivity.getString(R.string.appstock_action_wishlist);
				break;
			case BUTTON_SPECIAL_DEALS:
				action = mActivity
						.getString(R.string.appstock_action_special_deal);
				break;
			}
			((ContentListFragment) newFragment).setAction(action);
		}

//		currentFragment = newFragment;
//
//		fm.beginTransaction()
//				.setCustomAnimations(android.R.anim.fade_in,
//						android.R.anim.fade_out)
//				.replace(FRAGMENT_CONTAINER_ID, newFragment)
//				.addToBackStack(newFragment.getStackId()).commit();
		showFragment(newFragment, true);

	}

	private void updateHeader() {
		if (AccountLoggedIn.isSet()) {
			// logged in
			try {
				Account acc = AccountLoggedIn.getInstance();
				mHeaderAccountName.setText(acc.getUser().getUserName());
				mHeaderEdit.setText(mActivity
						.getString(R.string.appstock_menu_loggedinas));
				mHeader.findViewById(R.id.avatar_image).setVisibility(
						View.VISIBLE);

			} catch (AppStockSingletonException e) {
				// ignore that
			}

		} else {
			// not logged in
			mHeaderEdit.setText(mActivity
					.getString(R.string.appstock_menu_clickhereand));
			mHeaderAccountName.setText(mActivity
					.getString(R.string.appstock_menu_loginnow));
			mHeader.findViewById(R.id.avatar_image).setVisibility(View.GONE);
		}
	}

	public void updateView() {
		updateHeader();
		boolean isLoggedIn = false;
		if (AccountLoggedIn.isSet())
			isLoggedIn = true;
		mAapter.setItemVisibility(BUTTON_SETTINGS, isLoggedIn);
		mAapter.setItemVisibility(BUTTON_WISHLIST, isLoggedIn);
		mAapter.setItemVisibility(BUTTON_MY_CONTENT, isLoggedIn);
	}

	public boolean onBackPressed() {
		final FragmentManager fm = mActivity.getSupportFragmentManager();
		if (fm.getBackStackEntryCount() > 0) {
			if (currentFragment instanceof AllContentTypesFragment
					|| (currentFragment instanceof CategoryListFragment && currentFragment.getArguments() != null)) {
				fm.addOnBackStackChangedListener(new OnBackStackChangedListener() {
					@Override
					public void onBackStackChanged() {
						currentFragment = (BaseFragment) fm
								.findFragmentById(FRAGMENT_CONTAINER_ID);
						fm.removeOnBackStackChangedListener(this);
					}
				});
				fm.popBackStack();
				return true;
			}
		}
		return false;
	}
}