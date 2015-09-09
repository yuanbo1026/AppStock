package com.technisat.appstock.webservice;

public interface IWebServiceResponse {
	
	/**
	 * Event called when webservice responded on request with valid JSON.
	 * @param json The JSON response as String.
	 */
	public void onReceivedResponse(final String json);
	
	/**
	 * Callback event when webservice request failed.
	 * @param code The error code.
	 */
	public void onReceivedError(final int code);
}
