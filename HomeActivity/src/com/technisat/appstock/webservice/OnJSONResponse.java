package com.technisat.appstock.webservice;

import org.json.JSONObject;

public interface OnJSONResponse {

	/**
	 * Callback event when received an JSON answer from webservice.
	 * @param json Response from webservice as JSON Object.
	 */
	public void onReceivedJSONResponse(final JSONObject json);
	
	/**
	 * Callback event when webservice request failed.
	 * @param msg The description of the error.
	 * @param code The error code.
	 */
	public void onReceivedError(final String msg, final int code);

}
