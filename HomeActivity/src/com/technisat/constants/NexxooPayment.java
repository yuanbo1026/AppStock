package com.technisat.constants;

import com.paypal.android.sdk.payments.PayPalConfiguration;

public class NexxooPayment {

	/**
	 * - Set to PaymentActivity.ENVIRONMENT_PRODUCTION to move real money. - Set
	 * to PaymentActivity.ENVIRONMENT_SANDBOX to use your test credentials from
	 * https://developer.paypal.com - Set to
	 * PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires without
	 * communicating to PayPal's servers.
	 */
	/**
	 * to set the Paypal environment from Sandbox to production
	 */
	
	public static final String PAYPAL_CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
	// nexxoo_test_sandbox
	// public static final String PAYPAL_CONFIG_CLIENT_ID = "AS9DoxCbPZSlXty_0PJFdY-Bio0aezbHpnvgriQrc-z8JnY8ak0Rx7ddWFuC";
	
	// nexxoo_live
	// public static final String PAYPAL_CONFIG_CLIENT_ID ="AQmtNBDDU8lTQ87aGnMtHhejnNE4jWqr9C8FOEs1zx1-pKqVCz5ZSas5nGSb";
	
	// technisat_sandbox
	public static final String PAYPAL_CONFIG_CLIENT_ID = "AfO69SRJrfrX908hto5_CunyvZ1Jr-PL6nSgoyXjid3I9bT_pLdvYDJvf_t4MPgKSQfri6UPCoJW-EKj";
	
	// techisat_live
	// public static final String PAYPAL_CONFIG_CLIENT_ID ="AchkDSIprpQygcedDa8HBwVNP5yTmKs4_DuKJ2wkOdh0LSTPkJp5NQtU2x9OY5G8j_ly1q-LqI52oQyL";
		

	public static final int PAYPAL_REQUEST_CODE_PAYMENT = 1;

	public static final int PAYPAL_REQUEST_CODE_FUTURE_PAYMENT = 2;

	public static final String PAYMENT_CURRENCY = "EUR";
}