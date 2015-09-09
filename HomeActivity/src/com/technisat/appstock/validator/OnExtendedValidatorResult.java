package com.technisat.appstock.validator;

public interface OnExtendedValidatorResult {

	/**
	 * Event called when there is enough data to validate the user input.</br>
	 * result[0] -> validity of username </br>
	 * result[1] -> validity of email </br>
	 * result[2] -> validity of passwords (are they valid and equal) </br>
	 * result[3] -> validity of password (is it valid) </br>
	 * result[4] -> validity of the repeated password (is it valid) </br>
	 * @param results The result array. See description to know what value stands for what validity. May be null.
	 */
	public void onResult(boolean[] results, String errorMsg);
	
}
