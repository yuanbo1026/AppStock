package com.technisat.appstock.home;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.technisat.appstock.R;
import com.technisat.appstock.app.ActionBarHelper;
import com.technisat.appstock.app.BaseFragment;
import com.technisat.appstock.content.categorylist.FeaturedItemListAdapter;
import com.technisat.appstock.content.detail.ContentDetailActivity;
import com.technisat.appstock.drawer.MyDrawer;
import com.technisat.appstock.entity.App;
import com.technisat.appstock.entity.Content;
import com.technisat.appstock.entity.Magazine;
import com.technisat.appstock.entity.Video;
import com.technisat.appstock.exception.AppStockContentError;
import com.technisat.appstock.search.NexxooSearch;
import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;
import com.technisat.constants.Nexxoo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class HomeFragment extends BaseFragment implements OnJSONResponse {

	private static final String CONTENT_LIST = "home.fragment.content.list";
	private static final String TAG = HomeFragment.class.getSimpleName();
	private ArrayList<Content> mList;
	private ListView lvApp;
	private FeaturedItemListAdapter adapter;

	public HomeFragment() {
		fragmentId = MyDrawer.BUTTON_HOME;
		mList = new ArrayList<Content>();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (savedInstanceState != null) {
			mList = savedInstanceState.getParcelableArrayList(CONTENT_LIST);
		}
		
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ActionBarHelper.setActionBarTitle(getActivity(), getActivity().getActionBar(), "APPS");
		
		adapter = new FeaturedItemListAdapter(this.getActivity(), 0, mList);
		
		lvApp = (ListView) getView().findViewById(R.id.lv_home_feat_app);
		
		lvApp.setAdapter(adapter);

		lvApp.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent = new Intent(getActivity(),
						ContentDetailActivity.class);
				if (adapter.getItem(position) instanceof App) {
					intent.putExtra(getString(R.string.appstock_extra_content),
							(App) adapter.getItem(position));
				} else if (adapter.getItem(position) instanceof Magazine) {
					intent.putExtra(getString(R.string.appstock_extra_content),
							(Magazine) adapter.getItem(position));
				} else if (adapter.getItem(position) instanceof Video) {
					intent.putExtra(getString(R.string.appstock_extra_content),
							(Video) adapter.getItem(position));
				}
				startActivity(intent);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		NexxooWebservice.getContent(true, NexxooWebservice.CATEGORYFILTER_ALL,
				true, false, this);
		populateViews(getView());
	}

	private void populateViews(View view) {
		if(view != null){
			if (mList.size() > 0) {
				TextView tvEmpty = (TextView) view.findViewById(R.id.tv_home_empty);
				tvEmpty.setVisibility(View.GONE);
			}
			
			if(adapter != null){
				adapter.setData(mList);
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList(CONTENT_LIST, mList);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_home, container, false);
//		if (savedInstanceState != null) {
//			mList = savedInstanceState.getParcelableArrayList(CONTENT_LIST);
//			populateViews(view);
//		}
		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onReceivedJSONResponse(JSONObject json) {
		mList = new ArrayList<Content>();
		try {
			int count = json.getInt("count");

			for (int i = 0; i < count; i++) {
				try {
					JSONObject jsonContentObj = json.getJSONObject("content"
							+ i);

					switch (jsonContentObj.getInt(Content.CONTENTTYPE)) {
					case App.CONTENTTYPE:
						mList.add(new App(jsonContentObj));
						break;
					case Magazine.CONTENTTYPE:
						mList.add(new Magazine(jsonContentObj));
						break;
					case Video.CONTENTTYPE:
						mList.add(new Video(jsonContentObj));
						break;
					}
				} catch (AppStockContentError e) {
					Log.e(Nexxoo.TAG,
							"Fehler bei App " + i + ": " + e.getMessage());
				}
			}
		} catch (JSONException e) {
			Log.d(Nexxoo.TAG, e.getMessage());
		}
		populateViews(getView());
	}

	@Override
	public void onReceivedError(String msg, int code) {
		Log.d(Nexxoo.TAG, msg);
	}

	@Override
	public String getStackId() {
		return HomeFragment.class.getSimpleName();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate(R.menu.main, menu);
	    NexxooSearch.initSearchBar(getActivity(), menu);
	}
}
