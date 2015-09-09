package com.technisat.appstock.download;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Base64;
import android.util.Log;

import com.technisat.appstock.entity.Content;
import com.technisat.appstock.webservice.error.JSONErrorHandler;
import com.technisat.constants.FileStorageHelper;

public class DownloadAsyncTask extends AsyncTask<Void, Integer, Boolean>{

	private String mWebservice;
	private String mBasicAuth;
	
	private int mTask;
	private String mToken;
	private List<NameValuePair> mAdditionalParams;
	private Context mContext;
    private PowerManager.WakeLock mWakeLock;
    private OnDownloadResult mCallback;
    private Content mContent;
    
    private int mOldPercentage = -1;

    public DownloadAsyncTask(String webservice, String basicAuth, 
			int task, String token, List<NameValuePair> additionalParams,
			Context context, Content content, OnDownloadResult callback) {
    	mWebservice = webservice;
		mCallback = callback;
		mBasicAuth = basicAuth;
		
		mTask = task;
		mToken = token;
		mAdditionalParams = additionalParams;
    	
    	mContext = context;
        mContent = content;
        mCallback = callback;
    }
    
    public Content getContent(){
    	return mContent;
    }
    
    public void setCallback(OnDownloadResult callback){
    	Log.e("ASYNC", "setting callback : "+callback);
    	mCallback = callback;
    	if(!isCancelled())
    		mCallback.onDownloadProgress(mOldPercentage > 0 ? mOldPercentage : 0);
    }
    
    @Override
	protected void onCancelled() {
		super.onCancelled();
		
		if(FileStorageHelper.isDownloaded(mContent, mContext)){
			File file = new File(mContent.getLocalFileName(mContext));
			file.delete();
		}
		
		Log.e("ASYNC", "Download canceled");
		
		mCallback.onDownloadFailed("Canceled", JSONErrorHandler.WS_ERROR_UNKNOWN);
	}
    
    

	@Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user 
        // presses the power button during download
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
             getClass().getName());
        mWakeLock.acquire();
    }

	@Override
	protected Boolean doInBackground(Void... params) {
		InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;        
        
        try {
            URL url = new URL(mWebservice);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            
            if (mBasicAuth != null)
            	connection.addRequestProperty("Authorization", "Basic " +
                    Base64.encode(mBasicAuth.getBytes(), Base64.NO_WRAP));
            
            mAdditionalParams.add(new BasicNameValuePair("token", mToken));
            mAdditionalParams.add(new BasicNameValuePair("requesttask", Integer.toString(mTask)));
                        
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(mAdditionalParams));
            writer.flush();
            writer.close();
            os.close();
            
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {            	
            	switch (connection.getResponseCode()) {
				case HttpURLConnection.HTTP_FORBIDDEN:
					mCallback.onDownloadFailed(JSONErrorHandler.getErrorDescriptionByErrorCode(JSONErrorHandler.WS_ERROR_DOWNLOAD_DOWNLOADFORBIDDEN), JSONErrorHandler.WS_ERROR_DOWNLOAD_DOWNLOADFORBIDDEN);
					return false;
				case HttpURLConnection.HTTP_NOT_FOUND:
					mCallback.onDownloadFailed(JSONErrorHandler.getErrorDescriptionByErrorCode(JSONErrorHandler.WS_ERROR_DOWNLOAD_FNF), JSONErrorHandler.WS_ERROR_DOWNLOAD_FNF);
					return false;
				default:
					mCallback.onDownloadFailed(JSONErrorHandler.getErrorDescriptionByErrorCode(JSONErrorHandler.WS_ERROR_UNKNOWN), JSONErrorHandler.WS_ERROR_UNKNOWN);
	                return false;
				}
            }
            
            //download is actually starting
            mCallback.onDownloadStarted(mContent);
            
            //get content type
            mContent.setMimeType(connection.getContentType());            

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            FileStorageHelper helper = new FileStorageHelper(mContext);
            
            input = connection.getInputStream();
            
            File folder = new File(helper.getDownloadFolder() + mContent.getContentType() + File.separator);	                
    		if (!folder.exists())
    		    folder.mkdirs();
            
            output = new FileOutputStream( helper.getDownloadFolder() + mContent.getContentType() + File.separator + mContent.getFilename() );
            
            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    Log.e("ASYNC", "canceled somehow");
                    //mCallback.onDownloadFailed(JSONErrorHandler.getErrorDescriptionByErrorCode(JSONErrorHandler.WS_ERROR_DOWNLOAD_DLCANCELED), JSONErrorHandler.WS_ERROR_DOWNLOAD_DLCANCELED);
                    return false;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                
                output.write(data, 0, count);
//                Log.d(Nexxoo.TAG, "Filedownload: Wrote " + count + "bytes");	
            }

        } catch (IOException e) {
        	mCallback.onDownloadFailed(e.getMessage(), JSONErrorHandler.WS_ERROR_DOWNLOAD_IOEXCEPTION);
			return false;
		} finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        
		return true;
	}
	
	private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
	{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;

	    for (NameValuePair pair : params)
	    {
	        if (first)
	            first = false;
	        else
	            result.append("&");

	        result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
	    }

	    return result.toString();
	}
	
	@Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        if (mCallback != null && progress.length > 0 && progress[0] != mOldPercentage){
        	mOldPercentage = progress[0];
        	mCallback.onDownloadProgress(mOldPercentage);
        }        	
    }
	
	@Override
    protected void onPostExecute(Boolean result) {
        mWakeLock.release();
        if (result){
        	mCallback.onDownloadSuccess(mContent);
        }
        try {
        	this.cancel(true);
        } catch (Exception e) {
        	// just in case, was commented before
        	e.printStackTrace();
        }
    }
}
