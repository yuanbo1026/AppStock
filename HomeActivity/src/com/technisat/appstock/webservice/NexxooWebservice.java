package com.technisat.appstock.webservice;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.technisat.appstock.download.DownloadAsyncTask;
import com.technisat.appstock.download.OnDownloadResult;
import com.technisat.appstock.entity.Content;
import com.technisat.appstock.hash.NexxooHash;
import com.technisat.appstock.update.OnUpdateResult;
import com.technisat.appstock.update.UpdateAsyncTask;
import com.technisat.appstock.webservice.error.JSONErrorHandler;
import com.technisat.constants.FileStorageHelper;

/**
 * Static class that provides an interface for our webservice to interact with the
 * app stock webservice.
 * @author d.fredrich
 *
 */
public class NexxooWebservice {
	/**
	 * add this to test Git connection
	 */
	private static final String TAG = "NexxooWebservice";
	
//	private static final String URL = "https://www.technisat.com/apps/appstock/webservice.php";
//	private static final String URL = "http://nxodev.de:8010/webservice/webservice_test.php";
	private static final String URL = "https://www.appstock.de/ws/index.php";
//	private static final String BASICAUTH = "webservice-ts:~#Ub@LBq7Z8!_7_q";
	private static final String BASICAUTH = null;
	private static final String TOKEN = "";
	
	/**
	 * Content type for allowing all types in response
	 */
	public static final int CONTENTFILTER_ALL = 0;
	/**
	 * Content type for allowing only apps in response
	 */
	public static final int CONTENTFILTER_APPS = 1;
	/**
	 * Content type for allowing only magazines in response
	 */
	public static final int CONTENTFILTER_MAGAZINES = 2;
	/**
	 * Content type for allowing only videos in response
	 */
	public static final int CONTENTFILTER_VIDEOS = 3;
	
	/**
	 * Constant for requesting all content, regardless of what price
	 */
	public static final int CONTENTPRICE_ALL = 0;
	/**
	 * Constant for only requesting free content
	 */
	public static final int CONTENTPRICE_FREE = 1;
	/**
	 * Constant for only requesting content that needs to be purchased
	 */
	public static final int CONTENTPRICE_BUY = 2;
	
	public static final long CATEGORYFILTER_ALL = 0;
	
	/**
	 * Constant showing that the user made no like\dislike yet.
	 */
	public static final int CONTENT_LIKE_NONE = 0;
	/**
	 * Constant showing user liked the content.
	 */
	public static final int CONTENT_LIKE_YES = 1;
	/**
	 * Constant showing user disliked the content.
	 */
	public static final int CONTENT_LIKE_NO = -1;
	
	/* Constants for webservice tasks */
	
	protected static final int WEBTASK_REGISTERACCOUNT = 5;
	protected static final int WEBTASK_LOGINACCOUNT = 6;
	protected static final int WEBTASK_VALIDATE_ACCOUNT_USERNAME = 7;
	protected static final int WEBTASK_VALIDATE_ACCOUNT_EMAIL = 8;
	protected static final int WEBTASK_VALIDATE_ACCOUNT_ALL = 9;
	protected static final int WEBTASK_UPDATEACCOUNT = 10;
	protected static final int WEBTASK_GETCATEGORIES = 11;
	protected static final int WEBTASK_GETCONTENT = 12;
	protected static final int WEBTASK_GETOWNEDCONTENT = 13;
	protected static final int WEBTASK_GETCOMMENTS = 14;
	protected static final int WEBTASK_GETDOWNLOAD = 15;
	protected static final int WEBTASK_LOGOUTACCOUNT = 16;
	protected static final int WEBTASK_VALIDATE_PASSWORD = 17;
	protected static final int WEBTASK_GETWISHLIST = 18;
	protected static final int WEBTASK_ADDTOWISHLIST = 19;
	protected static final int WEBTASK_REMOVEFROMWISHLIST = 20;
	protected static final int WEBTASK_SEARCH = 21;
	protected static final int WEBTASK_BUY = 22;
	protected static final int WEBTASK_REVIEWCONTENT = 23;
	protected static final int WEBTASK_CANBUY = 24;
	protected static final int WEBTASK_FORGOTPASSWORD = 25;
	protected static final int WEBTASK_REFRESHACCOUNT = 26;
	protected static final int WEBTASK_UPGRADEACCOUNT = 27;
	protected static final int WEBTASK_GETPRICEFORDEVACCOUNT = 28;
	protected static final int WEBTASK_GETLASTESTVERSIONCODE = 30;
	public static final int WEBTASK_GETUPDATE = 31;
	
	/* Constants for parameter identifier */
	private static final String PARA_USERNAME = "username";
	private static final String PARA_PASSWORD = "password";
	private static final String PARA_EMAIL = "email";
	private static final String PARA_ACCOUNTID = "accid";
	private static final String PARA_SESSIONKEY = "sessionkey";
	private static final String PARA_CONTENTID = "contentid";
	private static final String PARA_FIRSTNAME = "firstname";
	private static final String PARA_LASTNAME = "lastname";
	private static final String PARA_STREET = "street";
	private static final String PARA_HOUSENUMBER = "housenumber";
	private static final String PARA_CITY = "city";
	private static final String PARA_ZIP = "zip";
	private static final String PARA_BIRTHDATE = "dateofbirth";
	private static final String PARA_STARTCOUNT = "start";
	private static final String PARA_ENDCOUNT = "end";
	private static final String PARA_CONTENTTYPE = "type";
	private static final String PARA_QUORUM = "quorum";
	private static final String PARA_DEVICEID = "deviceid";
	private static final String PARA_PRICETYPE = "pricetype";
	private static final String PARA_STATUS = "status";
	private static final String PARA_CATEGORYTYPE = "catfilter";
	private static final String PARA_QUERY = "query";
	private static final String PARA_ISFEATURED = "featured";
	private static final String PARA_ISSPECIALDEAL = "sdeal";
	private static final String PARA_LIKEDISLIKE = "likes";
	private static final String PARA_COMMENT = "comment";
	private static final String PARA_MESSAGE = "msg";
	private static final String PARA_IBAN = "iban";
	private static final String PARA_BIC = "bic";
	private static final String PARA_OWNERNAME = "ownername";
	private static final String PARA_PAYPALRESPONSE = "ppjson";
	private static final String PARA_IP = "ip";
	
	/**
	 * add two params for register
	 */
	private static final String PARA_TERMS = "terms";
	private static final String PARA_RECALL = "recall";
	
	private static List<DownloadAsyncTask> activeDownloads;
	
	
	private static void doWebCall(int task, List<NameValuePair> additionalParams, final OnJSONResponse callback, boolean async){
		String basicAuthBase64 = null;
		if (BASICAUTH != null) {
			basicAuthBase64 =  Base64.encodeToString(BASICAUTH.getBytes(), Base64.NO_WRAP);
			Log.e("BASIC", basicAuthBase64);
		}
		
		if (async){
			NexxooHttpJsonRequestAsyncTask asyncTask = 
					new NexxooHttpJsonRequestAsyncTask(URL, basicAuthBase64, task, TOKEN, additionalParams, new IWebServiceResponse() {
						
						@Override
						public void onReceivedResponse(String json) {
							if (json == null){
								callback.onReceivedError(JSONErrorHandler.getErrorDescriptionByErrorCode(JSONErrorHandler.WS_ERROR_UNKNOWN), JSONErrorHandler.WS_ERROR_UNKNOWN);	
								return;
							}
//							Log.d(Nexxoo.TAG, "Response: " + json);
							try {
								JSONObject obj = new JSONObject(json);
//								Log.d(Nexxoo.TAG, "JSON: " + json.toString());
								//check if result == 1
								int resultCode = obj.getInt("result");
								if (resultCode == 1)								
									callback.onReceivedJSONResponse(obj);
								else
									callback.onReceivedError(JSONErrorHandler.getErrorDescriptionByErrorCode(resultCode), resultCode);
							} catch (JSONException e) {
								callback.onReceivedError(JSONErrorHandler.getErrorDescriptionByErrorCode(JSONErrorHandler.WS_ERROR_UNKNOWN), JSONErrorHandler.WS_ERROR_UNKNOWN);
							} catch (NullPointerException e) {
								e.printStackTrace();
								if(callback != null)
									callback.onReceivedError(JSONErrorHandler.getErrorDescriptionByErrorCode(JSONErrorHandler.WS_ERROR_UNKNOWN), JSONErrorHandler.WS_ERROR_UNKNOWN);

							}
						}

						@Override
						public void onReceivedError(int code) {
							callback.onReceivedError(JSONErrorHandler.getErrorDescriptionByErrorCode(code), code);				
						}
					});
			asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
		} else {
			NexxooHttpJsonRequest request = new NexxooHttpJsonRequest();
			String json = request.performJsonRequest(URL, task, TOKEN, additionalParams);
			if (json != null){
				//check if we got an error
				if (json.startsWith("IOException")){
					callback.onReceivedError(JSONErrorHandler.getErrorDescriptionByErrorCode(JSONErrorHandler.WS_ERROR_IOEXCEPTION), 
							JSONErrorHandler.WS_ERROR_IOEXCEPTION);
				} else if (json.startsWith("ClientProtocolException")) {
					callback.onReceivedError(JSONErrorHandler.getErrorDescriptionByErrorCode(JSONErrorHandler.WS_ERROR_CLIENTPROTOCOLEXCEPTION), 
							JSONErrorHandler.WS_ERROR_CLIENTPROTOCOLEXCEPTION);
				} else {			
					//no error, try to parse JSON
					try {
						JSONObject obj = new JSONObject(json);
						//check if result == 1
						int resultCode = obj.getInt("result");
						if (resultCode == 1)								
							callback.onReceivedJSONResponse(obj);
						else
							callback.onReceivedError(JSONErrorHandler.getErrorDescriptionByErrorCode(resultCode), resultCode);
						
					} catch (JSONException e) {
						callback.onReceivedError(JSONErrorHandler.getErrorDescriptionByErrorCode(JSONErrorHandler.WS_ERROR_UNKNOWN), JSONErrorHandler.WS_ERROR_UNKNOWN);
					}
				}
			} else {
				callback.onReceivedError(JSONErrorHandler.getErrorDescriptionByErrorCode(JSONErrorHandler.WS_ERROR_UNKNOWN), JSONErrorHandler.WS_ERROR_UNKNOWN); 
			}			
		}
		
	}
	
	private static void performDownload(Context ctx, Content content, List<NameValuePair> additionalParams, final OnDownloadResult callback){
		String basicAuthBase64 = null;
		if (BASICAUTH != null)
			basicAuthBase64 =  Base64.encodeToString(BASICAUTH.getBytes(), Base64.NO_WRAP);	
		
		DownloadAsyncTask dlTask = new DownloadAsyncTask(URL, 
				basicAuthBase64, 
				WEBTASK_GETDOWNLOAD, 
				TOKEN, 
				additionalParams, 
				ctx, 
				content, 
				callback);			
		dlTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
		
		if(activeDownloads == null){
			activeDownloads = new ArrayList<DownloadAsyncTask>();			
		}
		activeDownloads.add(dlTask);
	}
	
	public static void performUpdate(Context ctx,
			List<NameValuePair> additionalParams, final OnUpdateResult callback) {
		UpdateAsyncTask dlTask = new UpdateAsyncTask(URL, WEBTASK_GETUPDATE,
				additionalParams, ctx, callback);
		dlTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
		/**
		 * whether the update procedure needs to push into the list?
		 */

	}
	
	public static void cancelAllDownlads(){
		for(DownloadAsyncTask task: activeDownloads){
			task.cancel(true);
		}
	}
	
	public static void cancelDownload(Content content){
		if(hasActiveDownloads()){
			for(DownloadAsyncTask task: activeDownloads){
				if(task.getContent().getContentId() == content.getContentId()){
					task.cancel(true);
				}
			}
		}
	}
	
	public static List<DownloadAsyncTask> getActiveDownloads(){
		return activeDownloads;
	}
	
	public static boolean hasActiveDownloads() {
		if(activeDownloads == null) return false;
		return activeDownloads.size() != 0;
	}
	
	public static boolean isDownloading(Activity activity, Content content) {
		if(hasActiveDownloads()) {
			ListIterator<DownloadAsyncTask> li = activeDownloads.listIterator(activeDownloads.size());

			while(li.hasPrevious()) {
				DownloadAsyncTask task = li.previous();
				if(task.getContent().getContentId() == content.getContentId() && !task.isCancelled()){
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean reattachDownloadProgress(Activity activity, Content content){
		/**
		 * iteration direction was reversed
		 * sometimes old (canceled) download task will not be removed
		 * from activeDownloads list. As a result it will be first on the list
		 * this code (due to return true) will only catch first (in this case canceled)
		 * item. Reversing order fixed this issue.
		 */
		if(hasActiveDownloads()) {
			ListIterator<DownloadAsyncTask> li = activeDownloads.listIterator(activeDownloads.size());
			// Iterate in reverse.
			while(li.hasPrevious()) {
				DownloadAsyncTask task = li.previous();
				if(task.getContent().getContentId() == content.getContentId() && !task.isCancelled()){
					Log.e(TAG, "Found active download for "+task.getContent().getName());
					task.setCallback(FileStorageHelper.getCallback(activity, content, true));
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Checks if credentials are valid.
	 * @param executeAsync Execute webcall async.
	 * @param user User name or Email of account to verifiy. Must not be null.
	 * @param pw The password of the account to verifiy. Method will do hashing before submitting it to webservice. Must not be null.
	 * @param deviceId An id unique to the device.
	 * @param msg A user message. May be null.
	 * @param callback Event called on response.
	 */
	public static void loginAccount(boolean executeAsync, 
			String user, String pw, String deviceId, String ip, 
			String msg, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_USERNAME, user));
		String deviceHash = NexxooHash.base64EncodeString( NexxooHash.hashDeviceId(deviceId) ); 
		params.add(new BasicNameValuePair(PARA_DEVICEID, deviceHash));
		String pwHash = NexxooHash.base64EncodeString( NexxooHash.hashPassword(pw) );		
		//params.add(new BasicNameValuePair(PARA_PASSWORD, pwHash));
		params.add(new BasicNameValuePair(PARA_PASSWORD, pw));
		if (ip != null)
			params.add(new BasicNameValuePair(PARA_IP, ip));
		if (msg != null && msg.length() > 0)
			params.add(new BasicNameValuePair(PARA_MESSAGE, msg));		
		doWebCall(WEBTASK_LOGINACCOUNT, params, callback, executeAsync);
	}
	
	/**
	 * Logs out the current logged in user. SessionKey will become invalid after calling.
	 * @param executeAsync Execute webcall async.
	 * @param accountId The account id of the user that wants to log out.
	 * @param sessionKey The current session key the user is logged in with. Must not be null.
	 * @param callback Event called on response.
	 */
	public static void logoutAccount(boolean executeAsync, long accountId, String sessionKey, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_ACCOUNTID, Long.toString(accountId)));
		params.add(new BasicNameValuePair(PARA_SESSIONKEY, sessionKey));
		
		doWebCall(WEBTASK_LOGOUTACCOUNT, params, callback, executeAsync);
	}
	
	/**
	 * Registers a new account using the webservice.
	 * @param executeAsync Execute webcall async.
	 * @param username The user name of the new user. Must not be null.
	 * @param email The email of the new user. Must not be null.
	 * @param password The password (clear) of the new user. Method will do hashing before submitting it to webservice. Must not be null.
	 * @param firstName The first name of the new user. May be null.
	 * @param lastName The last name of the new user. May be null.
	 * @param dateOfBirth The date of birth of the new user. May be -1.
	 * @param street The street of the address of the new user. May be null.
	 * @param houseNumber The house number of the address of the new user. May be null.
	 * @param city The city the new user is living in. May be null.
	 * @param zip The zip code of the new user. May be null.
	 * @param callback Event called on response. 
	 */
	public static void registerAccount(boolean executeAsync, String username, String email, String password,
			String firstName, String lastName, long dateOfBirth, String street, String houseNumber, String city, String zip,
			OnJSONResponse callback){
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_USERNAME, username));
		params.add(new BasicNameValuePair(PARA_EMAIL, email));
		
		/**
		 * if the method is invoked that means user already accept the policy
		 */
		params.add(new BasicNameValuePair(PARA_TERMS, Integer.toString(1)));
		params.add(new BasicNameValuePair(PARA_RECALL, Integer.toString(1)));
		
		params.add(new BasicNameValuePair(PARA_PASSWORD, password));
		if (firstName != null && !firstName.equals(""))
			params.add(new BasicNameValuePair(PARA_FIRSTNAME, firstName));
		if (lastName != null && !lastName.equals(""))
			params.add(new BasicNameValuePair(PARA_LASTNAME, lastName));
		if (dateOfBirth > -1)
			params.add(new BasicNameValuePair(PARA_BIRTHDATE, Long.toString(dateOfBirth)));
		if (street != null && !street.equals(""))
			params.add(new BasicNameValuePair(PARA_STREET, street));
		if (houseNumber != null && !houseNumber.equals(""))
			params.add(new BasicNameValuePair(PARA_HOUSENUMBER, houseNumber));
		if (city != null && !city.equals(""))
			params.add(new BasicNameValuePair(PARA_CITY, city));
		if (zip != null && !zip.equals(""))
			params.add(new BasicNameValuePair(PARA_ZIP, zip));
		
		doWebCall(WEBTASK_REGISTERACCOUNT, params, callback, executeAsync);
		
	}
	
	/**
	 * Updates an account using the webservice.
	 * @param executeAsync Execute webcall async.
	 * @param accId The user account id.
	 * @param sessionKey The current session key.
	 * @param email The email of the new user. Must not be null.
	 * @param password The password (clear) of the new user. Method will do hashing before submitting it to webservice. Must not be null.
	 * @param firstName The first name of the new user. May be null.
	 * @param lastName The last name of the new user. May be null.
	 * @param dateOfBirth The date of birth of the new user. May be -1.
	 * @param street The street of the address of the new user. May be null.
	 * @param houseNumber The house number of the address of the new user. May be null.
	 * @param city The city the new user is living in. May be null.
	 * @param zip The zip code of the new user. May be null.
	 * @param callback Event called on response. 
	 */
	public static void updateAccount(boolean executeAsync, long accId, String sessionKey, String email, String password,
			String firstName, String lastName, long dateOfBirth, String street, String houseNumber, String city, String zip,
			OnJSONResponse callback){
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_ACCOUNTID, Long.toString(accId)));
		params.add(new BasicNameValuePair(PARA_SESSIONKEY, sessionKey));
		
		if (email != null && !email.equals(""))
			params.add(new BasicNameValuePair(PARA_EMAIL, email));
		if (password != null && !password.equals("")){
			//String pwHash = NexxooHash.base64EncodeString( NexxooHash.hashPassword(password) );		
			params.add(new BasicNameValuePair(PARA_PASSWORD, password));
		}
		if (firstName != null && !firstName.equals(""))
			params.add(new BasicNameValuePair(PARA_FIRSTNAME, firstName));
		if (lastName != null && !lastName.equals(""))
			params.add(new BasicNameValuePair(PARA_LASTNAME, lastName));
		if (dateOfBirth > -1)
			params.add(new BasicNameValuePair(PARA_BIRTHDATE, Long.toString(dateOfBirth)));
		if (street != null && !street.equals(""))
			params.add(new BasicNameValuePair(PARA_STREET, street));
		if (houseNumber != null && !houseNumber.equals(""))
			params.add(new BasicNameValuePair(PARA_HOUSENUMBER, houseNumber));
		if (city != null && !city.equals(""))
			params.add(new BasicNameValuePair(PARA_CITY, city));
		if (zip != null && !zip.equals(""))
			params.add(new BasicNameValuePair(PARA_ZIP, zip));
		
		doWebCall(WEBTASK_UPDATEACCOUNT, params, callback, executeAsync);
	}
	
	/**
	 * Verifies a user name using the webservice.
	 * @param executeAsync Execute webcall async.
	 * @param username The user name to verify. Must not be null.
	 * @param callback Event called on response. Must not be null.
	 */
	public static void validateUsername(boolean executeAsync, String username, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_USERNAME, username));
		doWebCall(WEBTASK_VALIDATE_ACCOUNT_USERNAME, params, callback, executeAsync);
	}
	
	/**
	 * Verifies an email address using the webservice.
	 * @param executeAsync Execute webcall async.
	 * @param email The email to verify. Must not be null.
	 * @param callback Event called on response. Must not be null.
	 */
	public static void validateEmail(boolean executeAsync, String email, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_EMAIL, email));
		doWebCall(WEBTASK_VALIDATE_ACCOUNT_EMAIL, params, callback, executeAsync);
	}
	
	/**
	 * Verifies username & email in one request using the webservice.
	 * @param executeAsync Execute webcall async.
	 * @param username The user name to verify. Must not be null.
	 * @param email The email to verify. Must not be null.
	 * @param callback Event called on response. Must not be null.
	 */
	public static void validateUserInput(boolean executeAsync, 
			String username, String email, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_USERNAME, username));
		params.add(new BasicNameValuePair(PARA_EMAIL, email));
		doWebCall(WEBTASK_VALIDATE_ACCOUNT_ALL, params, callback, executeAsync);
	}
	
	/**
	 * Validates the given password for the given account if the given sessionKey is valid for this account at the moment.
	 * @param executeAsync Execute webcall async.
	 * @param password The password to verify.
	 * @param accountId The account id of the user the password belongs to.
	 * @param sessionKey The current session key the user is logged in with. Must not be null.
	 * @param callback Event called on response. Must not be null.
	 */
	public static void validatePassword(boolean executeAsync, String password, long accountId, String sessionKey, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		String pwHash = NexxooHash.base64EncodeString( NexxooHash.hashPassword(password) );
//		Log.d(Nexxoo.TAG, "hash:"+pwHash);
		params.add(new BasicNameValuePair(PARA_PASSWORD, password));
		params.add(new BasicNameValuePair(PARA_SESSIONKEY, sessionKey));
		params.add(new BasicNameValuePair(PARA_ACCOUNTID, Long.toString(accountId)));
		doWebCall(WEBTASK_VALIDATE_PASSWORD, params, callback, executeAsync);
	}

	/**
	 * Returns categories the db knows. Can be filtered for content types.</br>
	 * See </br> {@link #CONTENTFILTER_ALL}, </br> {@link #CONTENTFILTER_APPS}, </br> {@link #CONTENTFILTER_MAGAZINES}, </br> {@link #CONTENTFILTER_VIDEOS}
	 * @param executeAsync Execute webcall async.
	 * @param quorum The number of content the category must have at least. Set to -1 for allowing empty categories in response.
	 * @param contentType Specify for what content Type you want to have categories.
	 * @param callback Event called on response.
	 */
	public static void getCategories(boolean executeAsync, int quorum, int contentType, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_QUORUM, Integer.toString(quorum)));
		params.add(new BasicNameValuePair(PARA_CONTENTTYPE, Integer.toString(contentType)));
		doWebCall(WEBTASK_GETCATEGORIES, params, callback, executeAsync);
	}
	
	/**
	 * Returns content from the db. Can be filtered for content & price types and category. </br>
	 * See for content filters </br> {@link #CONTENTFILTER_ALL}, </br> {@link #CONTENTFILTER_APPS}, </br> {@link #CONTENTFILTER_MAGAZINES}, </br> {@link #CONTENTFILTER_VIDEOS} </br>
	 * See for price filters </br> {@link #CONTENTPRICE_ALL}, </br> {@link #CONTENTPRICE_FREE}, </br> {@link #CONTENTPRICE_BUY} </br>
	 * See for category filters </br> {@link #CATEGORYFILTER_ALL} </br>
	 * @param executeAsync Execute webcall async.
	 * @param startWith Starting index of the result set.
	 * @param endWith Ending index of the result set.
	 * @param contentType Specify for what content Type you want to have content.
	 * @param priceType Specify what kind of content you want get (all, free, purchasable).
	 * @param categoryFilter Specify the category the content has to be in (all or give categoryId).
	 * @param callback Event called on response.
	 */
	public static void getContent(boolean executeAsync, int startWith, int endWith, int contentType, 
			int priceType, long categoryFilter, OnJSONResponse callback){
		getContent(executeAsync,
				startWith,
				endWith,
				contentType,
				priceType,
				categoryFilter,
				false, //we dont care about featured or not
				false, //we dont care about special deal or not
				callback);
	}
	
	/**
	 * Returns content from the db. Can be filtered for category.
	 * @param executeAsync Execute webcall async.
	 * @param categoryFilter Specify the category the content has to be in (all or give categoryId).
	 * @param isFeatured Set to <code>true</code> if you only want featured content. Set to <code>false</code> if you dont care if it is featured or not.
	 * @param isSpecialDeal Set to <code>true</code> if you only want special deals. Set to <code>false</code> if you dont care if it is a special deal or not.
	 * @param callback Event called on response.
	 */
	public static void getContent(boolean executeAsync, long categoryFilter, boolean isFeatured,
			boolean isSpecialDeal, OnJSONResponse callback){
		getContent(executeAsync,
				0, //start with first result item
				-1, //take all we have
				CONTENTFILTER_ALL, //we do not filter for content types
				CONTENTPRICE_ALL, //we do not filter for price types
				categoryFilter,
				isFeatured,
				isSpecialDeal,
				callback);
	}
	
	private static void getContent(boolean executeAsync, int startWith, int endWith, int contentType,
			int priceType, long categoryFilter, boolean isFeatured, boolean isSpecialDeal, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_STARTCOUNT, Integer.toString(startWith)));
		params.add(new BasicNameValuePair(PARA_ENDCOUNT, Integer.toString(endWith)));
		params.add(new BasicNameValuePair(PARA_CONTENTTYPE, Integer.toString(contentType)));
		params.add(new BasicNameValuePair(PARA_PRICETYPE, Integer.toString(priceType)));
		params.add(new BasicNameValuePair(PARA_CATEGORYTYPE, Long.toString(categoryFilter)));
		params.add(new BasicNameValuePair(PARA_STATUS, Integer.toString(2))); //we only need to get published content
		params.add(new BasicNameValuePair(PARA_ISFEATURED, Boolean.toString(isFeatured)));
		params.add(new BasicNameValuePair(PARA_ISSPECIALDEAL, Boolean.toString(isSpecialDeal)));
		doWebCall(WEBTASK_GETCONTENT, params, callback, executeAsync);
	}
	
	/**
	 * Returns content from the db. Can be filtered for content types. </br>
	 * See </br> {@link #CONTENTFILTER_ALL}, </br> {@link #CONTENTFILTER_APPS}, </br> {@link #CONTENTFILTER_MAGAZINES}, </br> {@link #CONTENTFILTER_VIDEOS}
	 * @param executeAsync Execute webcall async.
	 * @param accId The account id of the account you want to have the owned content from.
	 * @param startWith Starting index of the result set.
	 * @param endWith Ending index of the result set.
	 * @param contentType Specify for what content Type you want to have content.
	 * @param callback Event called on response.
	 */
	public static void getOwnedContent(boolean executeAsync, 
			long accId, int startWith, int endWith, int contentType, 
			String sessionKey, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_ACCOUNTID, Long.toString(accId)));
		params.add(new BasicNameValuePair(PARA_STARTCOUNT, Integer.toString(startWith)));
		params.add(new BasicNameValuePair(PARA_ENDCOUNT, Integer.toString(endWith)));
		params.add(new BasicNameValuePair(PARA_CONTENTTYPE, Integer.toString(contentType)));
		params.add(new BasicNameValuePair(PARA_STATUS, Integer.toString(2))); //we only need to get published content
		params.add(new BasicNameValuePair(PARA_CATEGORYTYPE, Long.toString(CATEGORYFILTER_ALL))); //we dont care about categories here
		params.add(new BasicNameValuePair(PARA_SESSIONKEY, sessionKey));
		doWebCall(WEBTASK_GETOWNEDCONTENT, params, callback, executeAsync);
	}
	
	/**
	 * Returns comments to the specified content.
	 * @param executeAsync Execute webcall async.
	 * @param accountId The account id of the logged in account. May be -1.
	 * @param contentId The content id of the product you want to have comments from.
	 * @param startWith Starting index of the result set.
	 * @param endWith Ending index of the result set.
	 * @param callback Event called on response.
	 */
	public static void getComments(boolean executeAsync, long accountId, long contentId, int startWith, int endWith, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_CONTENTID, Long.toString(contentId)));
		params.add(new BasicNameValuePair(PARA_ACCOUNTID, Long.toString(accountId)));
		params.add(new BasicNameValuePair(PARA_STARTCOUNT, Integer.toString(startWith)));
		params.add(new BasicNameValuePair(PARA_ENDCOUNT, Integer.toString(endWith)));
		doWebCall(WEBTASK_GETCOMMENTS, params, callback, executeAsync);
	}
	
	/**
	 * Returns the content that the specified user has on his\her wishlist.
	 * @param executeAsync Execute webcall async.
	 * @param accountId The account id of the logged in account.
	 * @param sessionKey The current session key.
	 * @param callback Event called on response.
	 */
	public static void getWishlist(boolean executeAsync, long accountId, String sessionKey, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_ACCOUNTID, Long.toString(accountId)));
		params.add(new BasicNameValuePair(PARA_SESSIONKEY, sessionKey));
		doWebCall(WEBTASK_GETWISHLIST, params, callback, executeAsync);
	}
	
	/**
	 * Adds the given content to the user's wishlist.
	 * @param executeAsync Execute webcall async.
	 * @param accountId The user's account id you want to add the content to wishlist
	 * @param contentId The content id you want to add to the user's wishlist
	 * @param sessionKey The current session key.
	 * @param callback Event called on response.
	 */
	public static void addToWishlist(boolean executeAsync, long accountId, long contentId, 
			String sessionKey, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_ACCOUNTID, Long.toString(accountId)));
		params.add(new BasicNameValuePair(PARA_CONTENTID, Long.toString(contentId)));
		params.add(new BasicNameValuePair(PARA_SESSIONKEY, sessionKey));
		doWebCall(WEBTASK_ADDTOWISHLIST, params, callback, executeAsync);
	}
	
	/**
	 * Removes the given content from the user's wishlist. If the content is not on the list
	 * webservice will return success.
	 * @param executeAsync Execute webcall async.
	 * @param accountId The user's account id you want to add the content to wishlist
	 * @param contentId The content id you want to add to the user's wishlist
	 * @param sessionKey The current session key.
	 * @param callback Event called on response.
	 */
	public static void removeFromWishlist(boolean executeAsync, long accountId, long contentId,
			String sessionKey, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_ACCOUNTID, Long.toString(accountId)));
		params.add(new BasicNameValuePair(PARA_CONTENTID, Long.toString(contentId)));
		params.add(new BasicNameValuePair(PARA_SESSIONKEY, sessionKey));
		doWebCall(WEBTASK_REMOVEFROMWISHLIST, params, callback, executeAsync);
	}
	
	/**
	 * Get the download link of a content. Webservice does checking if account has the right to download the requested content.
	 * @param executeAsync Execute webcall async.
	 * @param contentId The id of the content you want to download.
	 * @param accId The id of the account you want to download the content for.
	 * @param sessionKey The current session key.
	 * @param callback Event called on response.
	 */
	public static void getDownload(boolean executeAsync, Context context, 
			Content content, long accId, String sessionKey, OnDownloadResult callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_CONTENTID, Long.toString( content.getContentId() )));
		params.add(new BasicNameValuePair(PARA_ACCOUNTID, Long.toString(accId)));
		params.add(new BasicNameValuePair(PARA_SESSIONKEY, sessionKey));
		
		performDownload(context, content, params, callback);
	}
	
	/**
	 * Sends a system mail to the user (if found) so that he\she can reset the password.
	 * @param executeAsync Execute webcall async. 
	 * @param user The user or email of the user. Must not be null.
	 * @param callback Event called on response.
	 */
	public static void getNewPasswordMail(boolean executeAsync, String user, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_USERNAME, user));
		doWebCall(WEBTASK_FORGOTPASSWORD, params, callback, executeAsync);
	}
	
	/**
	 * Buy content via webservice.
	 * @param executeAsync Execute webcall async.
	 * @param contentId The id of the content you want to download.
	 * @param accountId The id of the account you want to download the content for.
	 * @param sessionKey The current session key. Must not be null.
	 * @param paypalJson The paypal response from the app purchase. May be null for free content.
	 * @param callback Event called on response.
	 */
	public static void buyContent(boolean executeAsync, long contentId, long accountId, 
			String sessionKey, String paypalJson, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_CONTENTID, Long.toString(contentId)));
		params.add(new BasicNameValuePair(PARA_ACCOUNTID, Long.toString(accountId)));
		params.add(new BasicNameValuePair(PARA_SESSIONKEY, sessionKey));
		
		if (paypalJson != null && paypalJson.length() > 0)
			params.add(new BasicNameValuePair(PARA_PAYPALRESPONSE, paypalJson));
		
		doWebCall(WEBTASK_BUY, params, callback, executeAsync);
	}
	
	/**
	 * Searches the database for the given query. Search contains fields "name" of a content like %query%.
	 * @param executeAsync Execute webcall async.
	 * @param searchText The query.
	 * @param callback Event called on response.
	 */
	public static void searchForContent(boolean executeAsync, String searchText, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_QUERY, searchText));
		doWebCall(WEBTASK_SEARCH, params, callback, executeAsync);
	}
	
	/**
	 * Sets user review for a content. User can select if he/she likes or dislikes the content and give a comment.
	 * @param executeAsync Execute webcall async.
	 * @param likeDislike Indicator if the user liked or disliked the content. Should use one of </br> {@link #CONTENT_LIKE_NONE}, {@link #CONTENT_LIKE_YES}, {@link #CONTENT_LIKE_NO}  </br>
	 * @param comment The comment the user gave for this content. May be <code>null</code>
	 * @param contentId The id of the content you want to download.
	 * @param accountId The id of the account you want to download the content for.
	 * @param sessionKey The current session key.
	 * @param callback Event called on response.
	 */
	public static void reviewContent(boolean executeAsync, int likeDislike, String comment, 
			long contentId, long accountId, String sessionKey, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_LIKEDISLIKE, Integer.toString(likeDislike)));
		if (comment != null)
			params.add(new BasicNameValuePair(PARA_COMMENT, comment));
		params.add(new BasicNameValuePair(PARA_CONTENTID, Long.toString(contentId)));
		params.add(new BasicNameValuePair(PARA_ACCOUNTID, Long.toString(accountId)));
		params.add(new BasicNameValuePair(PARA_SESSIONKEY, sessionKey));
		doWebCall(WEBTASK_REVIEWCONTENT, params, callback, executeAsync);
	}

	/**
	 * Checks if a user is allowed to buy the given content. Check will fail if the user already owns the content.
	 * @param executeAsync Execute webcall async.
	 * @param contentId The id of the content you want to download.
	 * @param accountId The id of the account you want to download the content for.
	 * @param sessionKey The current session key.
	 * @param callback Event called on response.
	 */
	public static void canBuy(boolean executeAsync, long contentId, long accountId, String sessionKey, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_CONTENTID, Long.toString(contentId)));
		params.add(new BasicNameValuePair(PARA_ACCOUNTID, Long.toString(accountId)));
		params.add(new BasicNameValuePair(PARA_SESSIONKEY, sessionKey));
		doWebCall(WEBTASK_CANBUY, params, callback, executeAsync);
	}
	
	/**
	 * Refreshes an already logged in account. Returns the latest account information like the normal login does.
	 * Also renews the session key, so at least the new key has to be stored when calling this method.
	 * @param executeAsync Execute webcall async.
	 * @param accountId The id of the account you want to refresh.
	 * @param deviceId The id of the device.
	 * @param sessionKey The current session key.
	 * @param callback Event called on response.
	 */
	public static void refreshAccount(boolean executeAsync, long accountId, String deviceId, String sessionKey, OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_ACCOUNTID, Long.toString(accountId)));
		params.add(new BasicNameValuePair(PARA_DEVICEID, deviceId));
		params.add(new BasicNameValuePair(PARA_SESSIONKEY, sessionKey));
		doWebCall(WEBTASK_REFRESHACCOUNT, params, callback, executeAsync);
	}
	
	/**
	 * Upgrades the current user account to an developer account.
	 * @param executeAsync Execute webcall async.
	 * @param accountId The id of the account you want to refresh.
	 * @param sessionKey The current session key. Must not be null.
	 * @param iban The IBAN of the bank account. Must not be null.
	 * @param bic The BIX for the bank account. Must not be null.
	 * @param ownerName The owner of the bank account. Must not be null.
	 * @param paypalJson The Paypal response JSON when performing the payment in app. May be null if upgrade is free.
	 * @param callback Event called on response. Must not be null.
	 */
	public static void upgradeAccount(boolean executeAsync, long accountId, String sessionKey,
			String iban, String bic, String ownerName,
			String paypalJson,
			OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_ACCOUNTID, Long.toString(accountId)));
		params.add(new BasicNameValuePair(PARA_SESSIONKEY, sessionKey));
		
		if (iban != null && iban.length() > 0)
			params.add(new BasicNameValuePair(PARA_IBAN, iban));
		if (bic != null && bic.length() > 0)
			params.add(new BasicNameValuePair(PARA_BIC, bic));
		if (ownerName != null && ownerName.length() > 0)
			params.add(new BasicNameValuePair(PARA_OWNERNAME, ownerName));
		
		if (paypalJson != null && paypalJson.length() > 0)
			params.add(new BasicNameValuePair(PARA_PAYPALRESPONSE, paypalJson));
		
		doWebCall(WEBTASK_UPGRADEACCOUNT, params, callback, executeAsync);
	}
	
	/**
	 * Returns the current price for upgrading a user account to an developer account.
	 * @param executeAsync Execute webcall async.
	 * @param accountId The id of the account that wants to upgrade.
	 * @param sessionKey The current session key. Must not be null.
	 * @param callback Event called on response. Must not be null.
	 */
	public static void getPriceForDevAccountUpgrade(boolean executeAsync, long accountId, String sessionKey,
			OnJSONResponse callback){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(PARA_ACCOUNTID, Long.toString(accountId)));
		params.add(new BasicNameValuePair(PARA_SESSIONKEY, sessionKey));		
		doWebCall(WEBTASK_GETPRICEFORDEVACCOUNT, params, callback, executeAsync);
	}
	
	/**
	 * Returns the current version code from database
	 * 
	 * @param executeAsync
	 *            Execute web-call Async.
	 * @param callback
	 *            Event called on response. Must not be null.
	 * @author b.yuan
	 */
	public static void getLatestVersionCode(boolean executeAsync,
			OnJSONResponse callback) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		doWebCall(WEBTASK_GETLASTESTVERSIONCODE, params, callback, executeAsync);
	}
}
