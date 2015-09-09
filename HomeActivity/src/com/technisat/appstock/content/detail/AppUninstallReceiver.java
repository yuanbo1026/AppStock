package com.technisat.appstock.content.detail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.technisat.appstock.entity.App;
import com.technisat.constants.Nexxoo;

public class AppUninstallReceiver extends BroadcastReceiver {
	
	private ContentDetailActivity mActivity;
	private App mApp;
	
	public static String[] INTENT_FILTERS = {
		Intent.ACTION_PACKAGE_REMOVED};
	
	public AppUninstallReceiver(ContentDetailActivity activity, App app){
		mActivity = activity;
		mApp = app;
	}

	@Override
	public void onReceive(Context context, Intent intent) {	
		if (intent != null && intent.getAction() != null && mActivity != null && mApp != null){

			if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)){
				//check if our content has been removed
				String s = "package:" + mApp.getPackageName();
				if (s.equals(intent.getDataString()))
					mActivity.reloadActivityWithContent(mApp);
				else
					Log.d(Nexxoo.TAG, "Data: " + intent.getDataString());
			}
			
		}
	}

}
