package com.technisat.appstock.validator;

public interface OnValidatorResult {

	/**
	 * Callback for validating single user inputs.
	 * @param isValid <code>true</code> if value was valid, <code>false</code> otherwise
	 */
	public void onResult(boolean isValid);
	
	public void onResult(boolean isValid, String errorMsg);
	
}
