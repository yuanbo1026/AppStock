package com.technisat.appstock.content.contentlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.RelativeLayout;

import com.technisat.appstock.R;
import com.technisat.appstock.content.detail.ContentDetailActivity;
import com.technisat.appstock.drawer.MyDrawer;
import com.technisat.appstock.entity.Account;
import com.technisat.appstock.entity.App;
import com.technisat.appstock.entity.Content;
import com.technisat.appstock.entity.Magazine;
import com.technisat.appstock.entity.Video;
import com.technisat.appstock.entity.singleton.AccountLoggedIn;
import com.technisat.appstock.exception.AppStockContentError;
import com.technisat.appstock.exception.AppStockSingletonException;
import com.technisat.appstock.helper.StyleMessageDialog;
import com.technisat.appstock.search.NexxooSearch;
import com.technisat.appstock.utils.AlertDialogUtil;
import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;
import com.technisat.constants.FileStorageHelper;
import com.technisat.constants.Nexxoo;

/******************************************************
 * Activity to display An Expandable-list to show * user-OWNED CONTENT /
 * user-WISHLIST / * SPECIAL DEALS / SEARCH RESULTs *
 * 
 * @author b.nolde *
 ******************************************************/
public class ContentListActivity extends FragmentActivity {

	private MyDrawer mMyDrawer;
	private DrawerLayout mDrawerLayout;

	private ExpandableListView expListView;
	private List<String> listDataHeader = new ArrayList<String>();

	private HashMap<String, List<?>> listDataChildWithContent;
	private MyContentExpandableListAdapter listAdapterWithContent;

	private List<App> mAppList;
	private List<Magazine> mMagazineList;
	private List<Video> mVideoList;
	private Account account = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout_container);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mMyDrawer = new MyDrawer(this);
		mDrawerLayout = mMyDrawer.getDrawerLayout();

		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout container = (RelativeLayout) findViewById(R.id.frame_container);
		inflater.inflate(R.layout.activity_my_content, container);

		onNewIntent(getIntent());

		expListView = (ExpandableListView) findViewById(R.id.exlv_content_list);

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

				Intent intent = new Intent(ContentListActivity.this,
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

	@Override
	protected void onNewIntent(Intent intent) {

		setIntent(intent);

		try {
			account = AccountLoggedIn.getInstance();
		} catch (AppStockSingletonException e) {
		}
		/***********************************
		 * Handle owned content Intent *
		 ***********************************/
		if (intent.getAction() != null
				&& intent.getAction().equals(
						getString(R.string.appstock_action_my_content))) {
			getActionBar().setTitle(
					getString(R.string.appstock_content_my_content));

			NexxooWebservice.getOwnedContent(true, account.getAccountId(), 0,
					-1, NexxooWebservice.CONTENTFILTER_ALL,
					account.getSessionKey(), new OnJSONResponse() {
						@Override
						public void onReceivedJSONResponse(JSONObject json) {
							prepareListData(json);

							/********************************************
							 * Check if at least one app in list is * installed
							 * and got an update. * If so display UPDATE ALL
							 * button * and set functionality *
							 ********************************************/
							for (App a : mAppList) {
								if (FileStorageHelper.isInstalled(
										a.getPackageName(),
										ContentListActivity.this)
										&& !FileStorageHelper.isNewestVersion(
												a.getPackageName(),
												a.getVersionCode(),
												ContentListActivity.this)) {
									Button btnUpdateAll = (Button) findViewById(R.id.btn_update_all);
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
													AlertDialogUtil.show(ContentListActivity.this);
													for (App a : mAppList) {
														if (FileStorageHelper.isInstalled(
																a.getPackageName(),
																ContentListActivity.this)
																&& !FileStorageHelper
																		.isNewestVersion(
																				a.getPackageName(),
																				a.getVersionCode(),
																				ContentListActivity.this)) {
															Log.d(Nexxoo.TAG2,
																	a.getName()
																			+ " - will be updated by click on update all apps");
															FileStorageHelper
																	.download(
																			a,
																			ContentListActivity.this,
																			account);
														}
													}
												}
											});
									break;
								}
							}
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
		if (intent.getAction() != null
				&& intent.getAction().equals(
						getString(R.string.appstock_action_wishlist))) {
			getActionBar().setTitle(
					getString(R.string.appstock_content_wishlist));

			NexxooWebservice.getWishlist(true, account.getAccountId(),
					account.getSessionKey(), new OnJSONResponse() {

						@Override
						public void onReceivedJSONResponse(JSONObject json) {
							prepareListData(json);
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
		if (intent.getAction() != null
				&& intent.getAction().equals(
						getString(R.string.appstock_action_special_deal))) {
			getActionBar().setTitle(
					getString(R.string.appstock_content_special_deal));

			NexxooWebservice.getContent(true,
					NexxooWebservice.CATEGORYFILTER_ALL, false, true,
					new OnJSONResponse() {
						@Override
						public void onReceivedJSONResponse(JSONObject json) {
							prepareListData(json);
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
		if (intent.getAction() != null
				&& intent.getAction().equals(Intent.ACTION_SEARCH)) {
			getActionBar()
					.setTitle(getString(R.string.appstock_content_search));
			mDrawerLayout.closeDrawer(Gravity.START);
			String searchQuery = intent.getStringExtra(SearchManager.QUERY);
			NexxooWebservice.searchForContent(true, searchQuery,
					new OnJSONResponse() {
						@Override
						public void onReceivedJSONResponse(JSONObject json) {
							prepareListData(json);
							
						}

						@Override
						public void onReceivedError(String msg, int code) {
							Log.e(Nexxoo.TAG, msg);
						}
					});

		}
	}

	/***********************************
	 * Preparing the list data *
	 ***********************************/
	private void prepareListData(JSONObject json) {
		mAppList = new ArrayList<App>();
		mMagazineList = new ArrayList<Magazine>();
		mVideoList = new ArrayList<Video>();

		try {
			int count = json.getInt("count");
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
						mAppList.add(app);
						break;
					case 2:
						magazine = new Magazine(jsonContentObj);
						mMagazineList.add(magazine);
						break;
					case 3:
						video = new Video(jsonContentObj);
						mVideoList.add(video);
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

		listDataChildWithContent.put(listDataHeader.get(0), mAppList); // Header,
																		// Child
																		// data
		listDataChildWithContent.put(listDataHeader.get(1), mMagazineList);
		listDataChildWithContent.put(listDataHeader.get(2), mVideoList);

		if (mAppList.size() < 1)
			listDataHeader.remove(header[0]);
		if (mMagazineList.size() < 1)
			listDataHeader.remove(header[1]);
		if (mVideoList.size() < 1)
			listDataHeader.remove(header[2]);

		listAdapterWithContent = new MyContentExpandableListAdapter(
				getApplicationContext(), listDataHeader,
				listDataChildWithContent);
		expListView.setAdapter(listAdapterWithContent);

		// expand all groups of expandlistview by default
		for (int i = 0; i < listAdapterWithContent.getGroupCount(); i++)
			expListView.expandGroup(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		NexxooSearch.initSearchBar(this, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (!mDrawerLayout.isDrawerOpen(Gravity.START)) {
				mDrawerLayout.openDrawer(Gravity.START);
			} else {
				mDrawerLayout.closeDrawer(Gravity.START);
			}
			return true;
		default:
			return false;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mMyDrawer.updateView();
		/**
		 * refresh view(fragment), after add or delete item from wish-list
		 * 
		 * @author b.yuan
		 */
		onNewIntent(getIntent());
	}
}