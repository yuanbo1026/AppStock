package com.technisat.appstock.validator;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;
import com.technisat.appstock.webservice.error.JSONErrorHandler;
import com.technisat.constants.Nexxoo;

public class AccountRegisterValidator {
	
	/**
	 * Checks all user input if it is valid.
	 * @param username The username to check.
	 * @param email The email to check.
	 * @param password The password to check.
	 * @param password2 The repeated password to check.
	 * @param callback Results of the validating. See {@link OnExtendedValidatorResult#onResult(boolean[])} for more information.
	 */
	public static void checkAll(final String username, final String email, final String password,
			final String password2,
			final OnExtendedValidatorResult callback){
		
		if (isUsernameWorthChecking(username) && isEmailWorthChecking(email)){
			NexxooWebservice.validateUserInput(true, username, email, new OnJSONResponse() {
				
				private boolean[] results = new boolean[5];
				
				@Override
				public void onReceivedJSONResponse(JSONObject json) {
					if (json != null){
						try {
							Log.d("JRe", json.toString());
							int nameCode = json.has("username_ok") ? json.getInt("username_ok") : -1;
							int emailCode = json.has("email_ok") ? json.getInt("email_ok") : -1;
							
							results[0] = nameCode == 1;
							results[1] = emailCode == 1;
							results[2] = checkPassword(password, password2);
							results[3] = checkPassword(password);
							results[4] = checkPassword(password2);
							String err = null;
							err = JSONErrorHandler.getErrorDescriptionByErrorCode(emailCode > 1 ? emailCode : null);
							err = JSONErrorHandler.getErrorDescriptionByErrorCode(nameCode > 1 ? nameCode : null);
								
							callback.onResult(results, err);
						} catch (JSONException e) {
							callback.onResult(null, null);
						}
					} else {
						callback.onResult(null, null);
					}					
				}

				@Override
				public void onReceivedError(String msg, int code) {
					// TODO give user information
					Log.e(Nexxoo.TAG, "Error " + code + " requesting information from webservice in checkUsername: " + msg);
					callback.onResult(null, msg);
				}
			});
		} else {
			boolean[] results = new boolean[5];
			results[0] = isUsernameWorthChecking(username);
			results[1] = isEmailWorthChecking(email);
			results[2] = checkPassword(password, password2);
			results[3] = checkPassword(password);
			results[4] = checkPassword(password2);
			callback.onResult(results, null);
		}
	}
	
	private static boolean isUsernameWorthChecking(String username){
		return username != null && username.length() > 4;
	}
	
	private static boolean isEmailWorthChecking(String email){
		return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}
	
	/**
	 * Calls the webservice to verifiy that the given user name is still available.
	 * @param username The user name to verify.
	 * @param callback Event called on response. 
	 */
	public static void checkUsername(String username, final OnValidatorResult callback){
		if (isUsernameWorthChecking(username)){
			NexxooWebservice.validateUsername(true, username, new OnJSONResponse() {
				
				@Override
				public void onReceivedJSONResponse(JSONObject json) {
					if (json != null){
						try {
							boolean isOk = json.getInt("result") == 1;
							callback.onResult(isOk, null);
						} catch (JSONException e) {
							callback.onResult(false, "Server error");
						}
					} else {
						callback.onResult(false, "Server error");
					}					
				}

				@Override
				public void onReceivedError(String msg, int code) {
					// TODO give user information
					Log.e(Nexxoo.TAG, "Error " + code + " requesting information from webservice in checkUsername: " + msg);
					callback.onResult(false, msg);
				}
			});
		} else {
			callback.onResult(false, null);
		}
	}
	
	/**
	 * Calls the webservice to verifiy that the given Email address is still available.
	 * @param email The Email to verify.
	 * @param callback Event called on response. 
	 */
	public static void checkEmail(String email, final OnValidatorResult callback){
		if (isEmailWorthChecking(email)){
			NexxooWebservice.validateEmail(true, email, new OnJSONResponse() {
				
				@Override
				public void onReceivedJSONResponse(JSONObject json) {
					if (json != null){
						try {
							boolean isOk = json.getInt("result") == 1;
							callback.onResult(isOk);
						} catch (JSONException e) {
							callback.onResult(false);
						}
					} else {
						callback.onResult(false);
					}					
				}

				@Override
				public void onReceivedError(String msg, int code) {
					// TODO give user information
					Log.e(Nexxoo.TAG, "Error " + code + " requesting information from webservice in checkEmail: " + msg);
					callback.onResult(false);
				}
			});
		} else {
			callback.onResult(false);
		}
	}
	
	public static void checkPassword(String password, final OnValidatorResult callback){
		if (checkPassword(password)){
			callback.onResult(true);
		} else {
			callback.onResult(false);
		}
	}

	/**
	 * Checks the user password.
	 * @param password The password to verify
	 * @return <code>true</code> if the password is valid, <code>false</code> otherwise.
	 */
	public static boolean checkPassword(String password){
		return password != null && password.length() > 5;
	}
	
	/**
	 * Checks the user passwords if they are valid and equal.
	 * @param password The password
	 * @param password2 The repeated password.
	 * @return <code>true</code> if passwords are equal and valid, <code>false</code> otherwise
	 */
	public static boolean checkPassword(String password, String password2){
		return password != null && password.equals(password2);
	}
	
	// http://de.wikipedia.org/wiki/IBAN
	// 2-stelliger Ländercode gemäß ISO 3166-1 (bestehend aus Buchstaben)
	// 2-stellige Prüfsumme mit Prüfziffern gemäß ISO 7064 (bestehend aus Ziffern)
	// Max. 30-stellige Kontoidentifikation (bestehend aus Buchstaben und/oder Ziffern)
	// Deutsche IBAN (22 Stellen)
	public static boolean checkIBAN(String iban){
		if(iban.length() < 8)
			return false;
		if(iban.substring(0, 2).matches("-?\\d+(\\.\\d+)?")) // if char at 0 and 1 are numeric return false
			return false;
		if(iban.substring(2, 4).matches("[a-zA-Z]+")) // if char at 2 and 3 are letters return false
			return false;
		return true;
	}
	
	// http://de.wikipedia.org/wiki/ISO_9362
	// Der BIC hat eine Länge von 8 oder 11
	public static boolean checkBIC(String bic){
		if(bic.length() < 8) // if length is smaller than than 8 return false
			return false;
		return bic.substring(0, 6).matches("[a-zA-Z]+"); // if the first 6 chars are not letters only return false else true
	}
}