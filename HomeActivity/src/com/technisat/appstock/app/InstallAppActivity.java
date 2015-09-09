package com.technisat.appstock.app;

import java.io.File;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.technisat.appstock.R;
import com.technisat.appstock.content.detail.ContentDetailActivity;
import com.technisat.appstock.entity.App;
import com.technisat.appstock.entity.Content;
import com.technisat.appstock.entity.Picture;
import com.technisat.constants.Nexxoo;
import com.technisat.constants.NexxooPayment;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

public class InstallAppActivity extends Activity {
	
	private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed ;
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		final Content c = (Content) getIntent()
				.getParcelableExtra(getString(R.string.appstock_extra_content));
		
		Log.d("JD", "package installed="+appInstalledOrNot(c.getPackageName()));
		
		if(c.getPackageName() != null && !appInstalledOrNot(c.getPackageName())) {
				
			Intent intent = new Intent(this, ContentDetailActivity.class);
			intent.putExtra(this.getString(R.string.appstock_extra_content), c);
			PendingIntent pIntent = PendingIntent.getActivity(this, (int) c.getContentId(),
					intent, PendingIntent.FLAG_CANCEL_CURRENT);
			
			final NotificationManager mNotifyManager = (NotificationManager) this
					.getSystemService(Context.NOTIFICATION_SERVICE);
			final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					this);
			
			
			File file = new File(c
					.getLocalFileName(this));
			Intent intentForPrepare = null;
			
			try {
				if(c instanceof App){
					intentForPrepare = new Intent(
							Intent.ACTION_VIEW);
					intentForPrepare.setDataAndType(
							Uri.fromFile(file),
							c.getMimeType());
					pIntent = PendingIntent
							.getActivity(
									this,
									NexxooPayment.PAYPAL_REQUEST_CODE_PAYMENT,
									intentForPrepare,
									PendingIntent.FLAG_UPDATE_CURRENT);
	
					this.startActivity(intentForPrepare);
					
					mBuilder.setContentIntent(pIntent).setOngoing(
							false);
				}
	
				if (c.getContentType() == App.CONTENTTYPE) {
					mBuilder.setContentText(this
							.getString(R.string.appstock_download_done_start));
				}
				mBuilder.setOngoing(
						false);
				ImageLoader.getInstance().loadImage(c.getPicturesByType(
						Picture.PICTURETYPE_APP_ICON).get(0)
						.getmUrl(), new SimpleImageLoadingListener() 
				{
				    @Override
				    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) 
				    {
						mBuilder.setLargeIcon(loadedImage);
						mNotifyManager.notify(
								(int) c.getContentId(),
								mBuilder.build());
				    }
				});
	
			} catch (ActivityNotFoundException e) {
				Log.d(Nexxoo.TAG2,
						"Activity for pending intent not found: "
								+ e.getMessage());
			}
		}
		
		finish();
	}
	
}
