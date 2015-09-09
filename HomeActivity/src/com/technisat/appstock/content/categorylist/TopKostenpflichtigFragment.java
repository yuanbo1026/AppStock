package com.technisat.appstock.content.categorylist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
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
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.technisat.appstock.R;
import com.technisat.appstock.content.detail.ContentDetailActivity;
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
import com.technisat.appstock.utils.SortUtil;
import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;
import com.technisat.constants.Nexxoo;

public class TopKostenpflichtigFragment extends ReloadFragment {

	private List<Content> mList;
	private List<Magazine> mMagazineList;
	private TextView tvEmptyText = null;
	private RecyclerView recyclerView;
	private LinearLayoutManager layoutManager;
	private long categoryFilter = NexxooWebservice.CATEGORYFILTER_ALL;
	private ProgressBar progressSpinner;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_top_kostenpflichtig,
				container, false);

		Bundle bundle = getArguments();
		if (bundle.containsKey(getString(R.string.appstock_extra_category))) {
			Category category = bundle
					.getParcelable(getString(R.string.appstock_extra_category));
			categoryFilter = category.getCategoryId();
		}
		Log.d("JRe", "Create fragment");
		mList = new ArrayList<Content>();
		mMagazineList = new ArrayList<Magazine>();
		recyclerView = (RecyclerView) rootView
				.findViewById(R.id.recycler_in_tab);
		recyclerView.setHasFixedSize(true);

		recyclerView.addItemDecoration(new ItemDecoration() {
			@Override
			public void getItemOffsets(Rect outRect, View view,
					RecyclerView parent, RecyclerView.State state) {
				super.getItemOffsets(outRect, view, parent, state);
				outRect.inset(1, 1);
			}
		});

		String screenType = getString(R.string.screen_type);
		if (screenType.equals("phone")) {
			layoutManager = new LinearLayoutManager(getActivity());
		} else {
			layoutManager = new GridLayoutManager(getActivity(), 3);
		}

		recyclerView.setLayoutManager(layoutManager);

		tvEmptyText = (TextView) rootView.findViewById(R.id.tv_emptyText);
		tvEmptyText.setVisibility(View.GONE);

		progressSpinner = (ProgressBar) rootView
				.findViewById(R.id.progress_spinner);
		ConnectionDetector cd = new ConnectionDetector(getActivity());
		Boolean isInternetPresent = cd.isConnectingToInternet();
		if (isInternetPresent == false) {
			AlertDialogUtil.show(getActivity());
			progressSpinner.setIndeterminate(true);
			progressSpinner.bringToFront();
			progressSpinner.setVisibility(View.VISIBLE);
		}

		final ProgressDialog ringProgressDialog = ProgressDialog.show(
				getActivity(), null, getActivity().getString(R.string.loading),
				true);
		ringProgressDialog.setCancelable(true);

		NexxooWebservice.getContent(true, 0, -1,
				NexxooWebservice.CONTENTFILTER_ALL,
				NexxooWebservice.CONTENTPRICE_BUY, categoryFilter,
				new OnJSONResponse() {
					private ItemListRecycledAdapter adapter;

					@Override
					public void onReceivedJSONResponse(JSONObject json) {

						try {
							int count = json.getInt("count");

							for (int i = 0; i < count; i++) {
								try {
									JSONObject jsonContentObj = json
											.getJSONObject("content" + i);

									switch (jsonContentObj
											.getInt(Content.CONTENTTYPE)) {
									case App.CONTENTTYPE:
										mList.add(new App(jsonContentObj));
										break;
									case 2:
										Magazine magazine = new Magazine(
												jsonContentObj);
										magazine.setmIssue(jsonContentObj
												.getInt("issue"));
										magazine.setmYear(jsonContentObj
												.getInt("year"));
										mMagazineList.add(magazine);
										mList.add(magazine);
										break;
									case 3:
										mList.add(new Video(jsonContentObj));
										break;
									}
								} catch (AppStockContentError e) {
									Log.e(Nexxoo.TAG, "Fehler bei App " + i
											+ ": " + e.getMessage());
								}
							}

							ringProgressDialog.dismiss();

						} catch (JSONException e) {
							Log.e(Nexxoo.TAG, "error: " + e.getMessage());
						}

						Collections.sort(mList, new Comparator<Content>() {
							@Override
							public int compare(Content lhs, Content rhs) {
								if (lhs instanceof Magazine
										&& rhs instanceof Magazine) {
									Magazine m1 = (Magazine) lhs;
									Magazine m2 = (Magazine) rhs;
									if (m1 != null && m2 != null) {
										if (m1.getmIssue() > m2.getmIssue()) {
											return -1;
										} else if (m1.getmIssue() < m2
												.getmIssue()) {
											return 1;
										} else {
											if (m1.getmYear() > m2.getmYear()) {
												return -1;
											} else if (m1.getmYear() < m2
													.getmYear()) {
												return 1;
											} else
												return 0;
										}
									} else {
										return 0;
									}
								} else
									return 0;

							}
						});

						adapter = new ItemListRecycledAdapter(getActivity(),
								mList);
						recyclerView.setAdapter(adapter);

						if (mList.size() < 1)
							tvEmptyText
									.setText(getString(R.string.appstock_toppay_nocontent));
						else
							tvEmptyText.setVisibility(View.GONE);
					}

					@Override
					public void onReceivedError(String msg, int code) {
						ringProgressDialog.dismiss();
						Log.e(Nexxoo.TAG, "error bei getTestApps " + msg);
					}
				});

		return rootView;
	}

	@Override
	public void reload(Content c) {
		// reload adapter
		if (c != null && mList.contains(c)) {
			mList.set(mList.indexOf(c), c);
			recyclerView.setAdapter(new ItemListRecycledAdapter(getActivity(),
					mList));
			recyclerView.refreshDrawableState();
			recyclerView.getAdapter().notifyDataSetChanged();
		}
	}

	public void sort(int[][] ob, final int[] order) {

		Arrays.sort(ob, new Comparator<int[]>() {

			public int compare(int[] o1, int[] o2) {
				if (o1 != null && o2 != null) {
					for (int i = 0; i < 2; i++) {
						if (o1[i] > o2[i]) {
							return -1;
						} else if (o1[i] < o2[i]) {
							return 1;
						} else {
							continue;
						}
					}
					return 0;
				} else {
					return 0;
				}
			}
		});
	}
}