package com.technisat.appstock.app;

import android.app.Application;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/***************************************************************************************************************************************
 *  **    **        					                        **                 	      ******                                       *
 *  ***   **  ******  **     ** **     **  *******  *******     ***       ******  ****** **    **  ********  *******  *******  **  **  *
 *  ****  **  **       **   **   **   **   *******  *******     ****      **  **  **  ** **        ********  *******  *******  ** **   *
 *  ** ** **  ******    ** **     ** **    **   **  **   **     ** **     **  **  **  ** ********     **     **   **  **       ****    *
 *  **  ****  ******     ***       ***     **   **  **   **     **  ***   ******  ****** ********     **     **   **  **       ***     *
 *  **   ***  **        ** **     ** **    **   **  **   **     ********  **      **           **     **     **   **  **       ****    *
 *  **    **  ******   **   **   **   **   *******  *******     **    **  **      **     **    **     **     *******  *******  ** **   *
 *  **     *  ******  **     ** **     **  *******  *******     **     *  **      **      ******      **     *******  *******  **  **  *
 **************************************************************************************************************************************/

public class AppStart extends Application {

	@Override
	public void onCreate() {
		if (!ImageLoader.getInstance().isInited()){
			DisplayImageOptions options = new DisplayImageOptions.Builder()
	    	.cacheInMemory(true)
	    	.resetViewBeforeLoading(true)
	    	.build();
			ImageLoaderConfiguration imgCfg = new ImageLoaderConfiguration.Builder(getApplicationContext())
			.defaultDisplayImageOptions(options)
			.build();
			
			ImageLoader.getInstance().init(imgCfg);
		}
        
        super.onCreate();
	}
	
	
}