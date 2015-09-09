package com.technisat.appstock.update;

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

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import com.technisat.constants.FileStorageHelper;

public class UpdateAsyncTask extends AsyncTask<Void, Integer, Boolean> {

	private String mWebservice;

	private List<NameValuePair> mAdditionalParams;
	private Context mContext;
	private PowerManager.WakeLock mWakeLock;
	private OnUpdateResult mCallback;
	private int contentType = 4;
	private String fileName = "AppStock.apk";

	private int mOldPercentage = -1;

	public UpdateAsyncTask(String webservice, int task,
			List<NameValuePair> additionalParams, Context context,
			OnUpdateResult callback) {
		mWebservice = webservice;
		mCallback = callback;

		mAdditionalParams = additionalParams;

		mContext = context;
		mCallback = callback;
	}

	public void setCallback(OnUpdateResult callback) {
		Log.e("ASYNC", "setting callback : " + callback);
		mCallback = callback;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();

		String filePath = mContext.getExternalFilesDir(null).getAbsolutePath()
				+ File.separator + "downloads" + File.separator + contentType
				+ File.separator + fileName;

		File file = new File(filePath);
		file.delete();
		// }

		Log.e("ASYNC", "Download canceled");

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// take CPU lock to prevent CPU from going off if the user
		// presses the power button during download
		PowerManager pm = (PowerManager) mContext
				.getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass()
				.getName());
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

			OutputStream os = connection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					os, "UTF-8"));
			writer.write(getQuery(mAdditionalParams));
			writer.flush();
			writer.close();
			os.close();

			connection.connect();
			int responseCode = connection.getResponseCode();
			// expect HTTP 200 OK, so we don't mistakenly save error report
			// instead of the file
			if (responseCode != HttpURLConnection.HTTP_OK) {
				switch (connection.getResponseCode()) {
				case HttpURLConnection.HTTP_FORBIDDEN:
					return false;
				case HttpURLConnection.HTTP_NOT_FOUND:
					
					return false;
				default:
					return false;
				}
			}

			// might be -1: server did not report the length
			int fileLength = connection.getContentLength();

			FileStorageHelper helper = new FileStorageHelper(mContext);

			input = connection.getInputStream();

			File folder = new File(helper.getDownloadFolder() + contentType
					+ File.separator);
			if (!folder.exists())
				folder.mkdirs();

			output = new FileOutputStream(helper.getDownloadFolder()
					+ contentType + File.separator + fileName);

			byte data[] = new byte[4096];
			long total = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				// allow canceling with back button
				if (isCancelled()) {
					input.close();
					Log.e("ASYNC", "canceled somehow");
					return false;
				}
				total += count;
				// publishing the progress....
				if (fileLength > 0) // only if total length is known
					publishProgress((int) (total * 100 / fileLength));

				output.write(data, 0, count);
			}

		} catch (IOException e) {
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

	private String getQuery(List<NameValuePair> params)
			throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder();
		boolean first = true;

		for (NameValuePair pair : params) {
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
		if (mCallback != null && progress.length > 0
				&& progress[0] != mOldPercentage) {
			mOldPercentage = progress[0];
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		mWakeLock.release();
		if (result) {
			mCallback.onUpdateSuccess();
		}
	}
	
	
}
