package com.technisat.appstock.content.contentlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.technisat.appstock.R;
import com.technisat.appstock.app.ActionBarHelper;
import com.technisat.appstock.app.BaseDrawerActivity;
import com.technisat.appstock.app.BaseFragment;
import com.technisat.appstock.content.categorylist.MyAppsPagerAdapter;
import com.technisat.appstock.content.detail.ContentDetailActivity;
import com.technisat.appstock.contenttypelist.ExpandableListAdapter;
import com.technisat.appstock.drawer.MyDrawer;
import com.technisat.appstock.entity.Account;
import com.technisat.appstock.entity.App;
import com.technisat.appstock.entity.Content;
import com.technisat.appstock.entity.Magazine;
import com.technisat.appstock.entity.Video;
import com.technisat.appstock.entity.singleton.AccountLoggedIn;
import com.technisat.appstock.exception.AppStockContentError;
import com.technisat.appstock.exception.AppStockSingletonException;
import com.technisat.appstock.helper.ConnectionDetector;
import com.technisat.appstock.helper.StyleMessageDialog;
import com.technisat.appstock.search.NexxooSearch;
import com.technisat.appstock.utils.AlertDialogUtil;
import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;
import com.technisat.constants.FileStorageHelper;
import com.technisat.constants.Nexxoo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

public class ContentListFragment extends BaseFragment {
	private static final String FRAGMENT_ACTION = "appstock.contentlist.fragment.action";

	private ExpandableListView expListView;
	private List<String> listDataHeader = new ArrayList<String>();

	private HashMap<String, List<?>> listDataChildWithContent;
	private MyContentExpandableListAdapter listAdapterWithContent;

	private List<App> mAppList;
	private List<Magazine> mMagazineList;
	private List<Video> mVideoList;
	private Account account = null;
	private String mAction;

	private ProgressBar mProgressSpinner;

	private View btnUpdateAll;

	public ContentListFragment() {
		fragmentId = MyDrawer.BUTTON_MY_CONTENT;
	}

	public ContentListFragment(Intent intent) {
		super();
		setAction(intent.getAction());
		setArguments(intent.getExtras());
	}

	@Override
	public int getFragmentId() {
		return super.getFragmentId();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listDataChildWithContent = new HashMap<String, List<?>>();
		listAdapterWithContent = new MyContentExpandableListAdapter(
				getActivity(), listDataHeader, listDataChildWithContent);
		
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_my_content, container,
				false);
		view.setBackgroundColor(getResources().getColor(R.color.AppStockDrawerLightGrey));
		mProgressSpinner = (ProgressBar) view
				.findViewById(R.id.progress_spinner);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.e("BACK", mAction);
		onNewIntent(mAction);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(FRAGMENT_ACTION, mAction);
	}

	public void setAction(String action) {
		mAction = action;
	}
	
	public void createActivity(){
		
	}
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("JD", "content list fragment");
		
		ConnectionDetector cd = new ConnectionDetector(getActivity());
		Boolean isInternetPresent = cd.isConnectingToInternet();
		if ( isInternetPresent == false ){
			AlertDialogUtil.show(getActivity());
			mProgressSpinner.setIndeterminate(true);
			mProgressSpinner.bringToFront();
			mProgressSpinner.setVisibility(View.VISIBLE);
		} else {
			getActivity().getActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_STANDARD);
	
			if (savedInstanceState != null
					&& savedInstanceState.containsKey(FRAGMENT_ACTION)) {
				mAction = savedInstanceState.getString(FRAGMENT_ACTION);
			}
	
			if (mAction != null) {
				onNewIntent(mAction);
				getActivity().invalidateOptionsMenu();
				// menu.findItem(R.id.action_search)
				
				mProgressSpinner.setIndeterminate(true);
				mProgressSpinner.bringToFront();
				mProgressSpinner.setVisibility(View.VISIBLE);
			}
	
			expListView = (ExpandableListView) getView().findViewById(
					R.id.exlv_content_list);
			
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View view = inflater.inflate(R.layout.update_apps_header, null);
			btnUpdateAll = view.findViewById(R.id.btn_update_all);
			expListView.addHeaderView(view);
			btnUpdateAll.setVisibility(View.GONE);
			
			expListView.setAdapter(listAdapterWithContent);
	
			/************************************
			 * Listview GROUP click listener *
			 ************************************/
			expListView.setOnGroupClickListener(new OnGroupClickListener() {
				@Override
				public boolean onGroupClick(ExpandableListView arg0, View arg1,
						int arg2, long arg3) {
					return false;
				}
			});
	
			/************************************
			 * Listview on CHILD click listener *
			 ************************************/
			expListView.setOnChildClickListener(new OnChildClickListener() {
				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupPosition, int childPosition, long id) {
	
					Intent intent = new Intent(getActivity(),
							ContentDetailActivity.class);
					if (listAdapterWithContent.getChild(groupPosition,
							childPosition) instanceof App) {
						intent.putExtra(getString(R.string.appstock_extra_content),
								(App) listAdapterWithContent.getChild(
										groupPosition, childPosition));
					} else if (listAdapterWithContent.getChild(groupPosition,
							childPosition) instanceof Magazine) {
						intent.putExtra(getString(R.string.appstock_extra_content),
								(Magazine) listAdapterWithContent.getChild(
										groupPosition, childPosition));
					} else if (listAdapterWithContent.getChild(groupPosition,
							childPosition) instanceof Video) {
						intent.putExtra(getString(R.string.appstock_extra_content),
								(Video) listAdapterWithContent.getChild(
										groupPosition, childPosition));
					}
					startActivity(intent);
					return false;
				}
			});
		}
        if(getActivity() instanceof BaseDrawerActivity 
        		&& getArguments() != null
        		&& getArguments().containsKey(SearchManager.QUERY)) {
        	BaseDrawerActivity act = (BaseDrawerActivity)getActivity();
        	act.setDrawerIndicatorEnabled(false);
        }
	}

	protected void onNewIntent(String action) {

		try {
			account = AccountLoggedIn.getInstance();
		} catch (AppStockSingletonException e) {
		}
		/***********************************
		 * Handle owned content Intent *
		 ***********************************/
		if (action.equals(getString(R.string.appstock_action_my_content))) {
			fragmentId = R.string.appstock_action_my_content;
			
			final Integer searchQuery = this.getArguments().getInt(MyAppsListFragment.FILTER_ARG);

			ActionBarHelper.setActionBarTitle(
					getActivity(),
					getActivity().getActionBar(),
					getActivity().getString(
							R.string.appstock_content_my_content));

			listAdapterWithContent.setColoredCategories();

			NexxooWebservice.getOwnedContent(true, account.getAccountId(), 0,
					-1, NexxooWebservice.CONTENTFILTER_ALL,
					account.getSessionKey(), new OnJSONResponse() {
						@Override
						public void onReceivedJSONResponse(JSONObject json) {
							prepareListData(json, searchQuery);

							/***************************************************
							 * Check if at least one app in list is * installed*
							 * and got an update. * If so display UPDATE ALL   *
							 * button * and set functionality 				   *
							 ***************************************************/
							for (App a : mAppList) {
								if (FileStorageHelper.isInstalled(
										a.getPackageName(), getActivity())
										&& !FileStorageHelper.isNewestVersion(
												a.getPackageName(),
												a.getVersionCode(),
												getActivity())) {
									
									
									btnUpdateAll.setVisibility(View.VISIBLE);
									btnUpdateAll
											.setOnClickListener(new OnClickListener() {

												/********************************************
												 * Check if app in list is
												 * installed got * an update. If
												 * so start update * (multiple
												 * updates will be queued in *
												 * asynctasks - said Daniel /
												 * untestet) *
												 ********************************************/
												@Override
												public void onClick(View v) {
													ConnectionDetector cd = new ConnectionDetector(getActivity());
													Boolean isInternetPresent = cd.isConnectingToInternet();
													if (isInternetPresent == false){
														AlertDialogUtil.show(getActivity());
													}else{
														for (App a : mAppList) {
															if (FileStorageHelper.isInstalled(
																	a.getPackageName(),
																	getActivity())
																	&& !FileStorageHelper
																			.isNewestVersion(
																					a.getPackageName(),
																					a.getVersionCode(),
																					getActivity())) {
																Log.d(Nexxoo.TAG2,
																		a.getName()
																				+ " - will be updated by click on update all apps");
																FileStorageHelper
																		.download(
																				a,
																				getActivity(),
																				account);
															}
														}
														expListView.invalidate();
													}
												}
											});
									break;
								}
							}
							mProgressSpinner.setIndeterminate(false);
							mProgressSpinner.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onReceivedError(String msg, int code) {
							Log.e(Nexxoo.TAG, msg);
						}
					});
		}
		/***********************************
		 * Handle wishlist Intent *
		 ***********************************/
		if (action.equals(getString(R.string.appstock_action_wishlist))) {
			fragmentId = R.string.appstock_action_wishlist;

			ActionBarHelper.setActionBarTitle(
					getActivity(),
					getActivity().getActionBar(),
					getActivity().getString(
							R.string.appstock_content_wishlist));			

			NexxooWebservice.getWishlist(true, account.getAccountId(),
					account.getSessionKey(), new OnJSONResponse() {

						@Override
						public void onReceivedJSONResponse(JSONObject json) {
							prepareListData(json, 0);
							mProgressSpinner.setIndeterminate(false);
							mProgressSpinner.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onReceivedError(String msg, int code) {
							Log.d(Nexxoo.TAG, msg); 
						}
					});
		}
		/***********************************
		 * Handle special Deal Intent *
		 ***********************************/
		if (action.equals(getString(R.string.appstock_action_special_deal))) {
			fragmentId = R.string.appstock_action_special_deal;

			ActionBarHelper.setActionBarTitle(
					getActivity(),
					getActivity().getActionBar(),
					getActivity().getString(
							R.string.appstock_content_special_deal));

			NexxooWebservice.getContent(true,
					NexxooWebservice.CATEGORYFILTER_ALL, false, true,
					new OnJSONResponse() {
						@Override
						public void onReceivedJSONResponse(JSONObject json) {
							prepareListData(json, 0);
							mProgressSpinner.setIndeterminate(false);
							mProgressSpinner.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onReceivedError(String msg, int code) {
							Log.d(Nexxoo.TAG, msg);
						}
					});
		}
		/***********************************
		 * Handle Search Action Intent *
		 ***********************************/
		if (action.equals(Intent.ACTION_SEARCH)) {
			ActionBarHelper.setActionBarTitle(
					getActivity(),
					getActivity().getActionBar(),
					getActivity().getString(
							R.string.appstock_content_search));
			
			// mDrawerLayout.closeDrawer(Gravity.START);
			String searchQuery = this.getArguments().getString(SearchManager.QUERY);
			Log.e("SEARCH", searchQuery);
			

			NexxooWebservice.searchForContent(true, searchQuery,
					new OnJSONResponse() {
						@Override
						public void onReceivedJSONResponse(JSONObject json) {
							Log.i("SEARCH RESULTS", json.toString());
							if(isAdded()) {
								Log.d("JD", "is added");
							}else{
								Log.d("JD", "not added");
							}
							prepareListData(json, 0);
							mProgressSpinner.setIndeterminate(false);
							mProgressSpinner.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onReceivedError(String msg, int code) {
							Log.e(Nexxoo.TAG, msg);
						}
					});
		}
	}
	
	
	
	private void prepareListData(JSONObject json, int searchQuery) {
		mAppList = new ArrayList<App>();
		mMagazineList = new ArrayList<Magazine>();
		mVideoList = new ArrayList<Video>();
		
		

		try {
			int count = json.getInt("count");
			
			// this request is created twice
			// skip when view is not created
			if(getView() == null) {
				return;
			}
			
			if(TextUtils.equals(mAction, Intent.ACTION_SEARCH) && count == 0){
				getView().findViewById(R.id.empty_text).setVisibility(View.VISIBLE);
				return;
			}
			
			App app = null;
			Magazine magazine = null;
			Video video = null;

			for (int i = 0; i < count; i++) {
				try {
					JSONObject jsonContentObj = json.getJSONObject("content"
							+ i);

					switch (jsonContentObj.getInt(Content.CONTENTTYPE)) {
					case App.CONTENTTYPE:
						app = new App(jsonContentObj);
						switch(searchQuery) {
							case MyAppsListFragment.FILTER_INSTALL:
								if (!FileStorageHelper.isInstalled(
										app.getPackageName(), getActivity())
										) 
									mAppList.add(app);
							break;
							case MyAppsListFragment.FILTER_INSTALLED:
								if (FileStorageHelper.isInstalled(
										app.getPackageName(), getActivity())
										&& FileStorageHelper.isNewestVersion(
												app.getPackageName(),
												app.getVersionCode(),
												getActivity())) 
									mAppList.add(app);
							break;
							case MyAppsListFragment.FILTER_UPDATE:
								if (FileStorageHelper.isInstalled(
										app.getPackageName(), getActivity())
										&& !FileStorageHelper.isNewestVersion(
												app.getPackageName(),
												app.getVersionCode(),
												getActivity())) 
									mAppList.add(app);
							break;
							default:
								mAppList.add(app);
									
						}
						break;
					case 2:
						magazine = new Magazine(jsonContentObj);
						switch(searchQuery) {
						case MyAppsListFragment.FILTER_INSTALL:
							if (!FileStorageHelper.isDownloaded(magazine, getActivity())) 
								mMagazineList.add(magazine);
						break;
						case MyAppsListFragment.FILTER_INSTALLED:
							if (FileStorageHelper.isDownloaded(magazine, getActivity())) 
								mMagazineList.add(magazine);
						case MyAppsListFragment.FILTER_UPDATE:
						break;
						default:
							mMagazineList.add(magazine);
						}
						break;
					case 3:
						video = new Video(jsonContentObj);
						switch(searchQuery) {
						case MyAppsListFragment.FILTER_INSTALL:
							if (!FileStorageHelper.isDownloaded(video, getActivity())) 
								mVideoList.add(video);
						break;	
						case MyAppsListFragment.FILTER_INSTALLED:
							if (FileStorageHelper.isDownloaded(video, getActivity())) 
								mVideoList.add(video);
						break;	
						case MyAppsListFragment.FILTER_UPDATE:
						break;
						default:
							mVideoList.add(video);
						}
						break;
					}
				} catch (AppStockContentError e) {
					Log.d(Nexxoo.TAG, e.getMessage());
				}
			}

		} catch (JSONException e) {
			Log.d(Nexxoo.TAG, e.getMessage());
		}
		listDataHeader = new ArrayList<String>();
		listDataChildWithContent = new HashMap<String, List<?>>();

		// Adding child data
		String[] header = getResources().getStringArray(
				R.array.appstock_contenttypes);
		for (String s : header) {
			listDataHeader.add(s);
		}

		listDataChildWithContent.put(listDataHeader.get(0), mAppList);
		listDataChildWithContent.put(listDataHeader.get(1), mMagazineList);
		listDataChildWithContent.put(listDataHeader.get(2), mVideoList);

		if (mAppList.size() < 1)
			listDataHeader.remove(header[0]);
		if (mMagazineList.size() < 1)
			listDataHeader.remove(header[1]);
		if (mVideoList.size() < 1)
			listDataHeader.remove(header[2]);
		
		listAdapterWithContent.setData(listDataChildWithContent, listDataHeader);
		
		for(int i=0;i<listDataHeader.size();i++){
			expListView.expandGroup(i);
		}
	}

	@Override
	public void onDrawerClosed() {
		listAdapterWithContent
				.setData(listDataChildWithContent, listDataHeader);
		listAdapterWithContent.notifyDataSetChanged();

		for (int i = 0; i < listAdapterWithContent.getGroupCount(); i++)
			expListView.expandGroup(i);
	}

	@Override
	public String getStackId() {
		return ContentListFragment.class.getSimpleName();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.main, menu);
		NexxooSearch.initSearchBar(getActivity(), menu);
	}

}
