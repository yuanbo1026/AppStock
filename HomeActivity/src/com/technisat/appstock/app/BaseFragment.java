package com.technisat.appstock.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;


public abstract class BaseFragment extends Fragment {
	
	protected int fragmentId = 0;
	
	@Override
	public void onResume() {
		BaseDrawerActivity act = (BaseDrawerActivity) getActivity();
		act.saveFragmentBundle(this);
		super.onResume();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		((BaseDrawerActivity) getActivity()).setDrawerIndicatorEnabled(true); 
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	public int getFragmentId(){
		return fragmentId;
	}
	
	abstract public String getStackId();
	
	public void onDrawerClosed(){}
	
	public void handleIntent(Intent intent) {}

}
