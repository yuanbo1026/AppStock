package com.technisat.appstock.download;

import com.technisat.appstock.entity.Content;

public interface OnDownloadResult {
	
	/**
	 * Event fired when the actual download starts.
	 * @param content The content that will be downloaded.
	 */
	public void onDownloadStarted(Content content);
	
	/**
	 * Event fired when download was successfully completed.
	 * @param content The content that has been downloaded.
	 */
	public void onDownloadSuccess(Content content);
	
	/**
	 * Event fired when the percentage progress of the download changed.
	 * @param percent Current progress in percent.
	 */
	public void onDownloadProgress(int percent);
	
	/**
	 * Event fired when the download failed.
	 * @param msg The error message.
	 * @param code The error code.
	 */
	public void onDownloadFailed(String msg, int code);
	
}
