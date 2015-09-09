package com.technisat.appstock.content.categorylist;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.technisat.appstock.R;
import com.technisat.appstock.entity.Category;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	private Context mContext = null;
	private Category mCategory;
	private String[] titles;
	FeaturedFragment ff;
	TopKostenlosFragment tklf;
	TopKostenpflichtigFragment tkpf;

	public TabsPagerAdapter(FragmentManager fm, Context context, Category cat,
			String[] tabTitles) {
		super(fm);
		mContext = context;
		mCategory = cat;
		titles = tabTitles;
	}

	@Override
	public Fragment getItem(int index) {
		Bundle bundle = new Bundle();
		if (mCategory != null) {
			bundle.putParcelable(
					mContext.getString(R.string.appstock_extra_category),
					mCategory);
		}
		Log.d("JRe","FRAGMENT CHANGE!!!!!!");
		switch (index) {
		case 0:
			if (ff == null) {
				ff = new FeaturedFragment();
				ff.setArguments(bundle);
			}
			return ff;
		case 1:
			if (tklf == null) {
				tklf = new TopKostenlosFragment();
				tklf.setArguments(bundle);
			}
			return tklf;
		case 2:
			if (tkpf == null) {
				tkpf = new TopKostenpflichtigFragment();
				tkpf.setArguments(bundle);
			}
			return tkpf;
		}
		return null;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}

	@Override
	public int getCount() {
		return 3;
	}
}