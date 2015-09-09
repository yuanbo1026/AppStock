package com.technisat.appstock.content.detail;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.technisat.appstock.R;
import com.technisat.appstock.app.ImageLoaderSpinner;
import com.technisat.appstock.entity.App;
import com.technisat.appstock.entity.Content;
import com.technisat.appstock.entity.Magazine;
import com.technisat.appstock.entity.Picture;
import com.technisat.appstock.entity.Video;

/******************************************************************
 * Activity to display screenshots in full-screen with			  *
 * slide functionality. The user gets there if he clicks on a	  *
 *  screenshot (small view) in content detail view				  *
 ******************************************************************/
public class ScreenshotActivity extends Activity {
	
	private Content mContent = null;
	ProgressBar mProgressBar = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screenshot);
		
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        Intent intent = getIntent();
        mContent = intent.getParcelableExtra(getString(R.string.appstock_extra_content));
        getActionBar().setTitle(mContent.getName() + " Screenshots");
        
        LinearLayout ll_screenshotList = (LinearLayout) findViewById(R.id.ll_screenshot_list);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_feat_spinner2); 
        
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(width, LayoutParams.MATCH_PARENT); // TODO set size right
	    para.setMargins(8, 0, 0, 0);
        List<Picture> screenshotList = null;
        ImageLoader mImageLoader = ImageLoader.getInstance();
        /********************************************************
         * get content type to display the right screenshots	*
         ********************************************************/
        switch (mContent.getContentType()) {
		case App.CONTENTTYPE:
			screenshotList = mContent.getPicturesByType(Picture.PICTURETYPE_SCREENSHOT_APP);
			for(int i = 0; i < screenshotList.size(); i++){
				ImageView imageView = new ImageView(getApplicationContext());
				imageView.setLayoutParams(para);
				imageView.setScaleType(ScaleType.FIT_CENTER);
				mImageLoader.displayImage(screenshotList.get(i).getmUrl(), imageView,
						new ImageLoaderSpinner(mProgressBar));
				ll_screenshotList.addView(imageView);
			}
			break;
		case Magazine.CONTENTTYPE:
			screenshotList = mContent.getPicturesByType(Picture.PICTURETYPE_SCREENSHOT_MAGAZINE);
			for(int i = 0; i < screenshotList.size(); i++){
				ImageView imageView = new ImageView(getApplicationContext());
				imageView.setLayoutParams(para);
				imageView.setScaleType(ScaleType.FIT_CENTER);
				mImageLoader.displayImage(screenshotList.get(i).getmUrl(), imageView,
						new ImageLoaderSpinner(mProgressBar));
				ll_screenshotList.addView(imageView);
			}
			break;
		case Video.CONTENTTYPE:
			screenshotList = mContent.getPicturesByType(Picture.PICTURETYPE_SCREENSHOT_VIDEO);
			for(int i = 0; i < screenshotList.size(); i++){
				ImageView imageView = new ImageView(getApplicationContext());
				imageView.setLayoutParams(para);
				imageView.setScaleType(ScaleType.FIT_CENTER);
				mImageLoader.displayImage(screenshotList.get(i).getmUrl(), imageView,
						new ImageLoaderSpinner(mProgressBar));
				ll_screenshotList.addView(imageView);
			}
			break;
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
	        finish();
	        return true;
        default:
        	return true;
        }
	}
}