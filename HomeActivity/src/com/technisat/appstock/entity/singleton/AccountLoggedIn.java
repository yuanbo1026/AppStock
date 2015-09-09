package com.technisat.appstock.entity.singleton;

import com.technisat.appstock.entity.Account;
import com.technisat.appstock.exception.AppStockSingletonException;

public class AccountLoggedIn {

	private static Account mAccount = null;
	
	private AccountLoggedIn(){}
	
	/**
	 * Returns the singleton instance of the logged in account
	 * @return The logged in account object.
	 * @throws AppStockSingletonException If logged in account is not set. Use {@link #initAccountLoggedIn(Account)} for that.
	 */
	public static Account getInstance() throws AppStockSingletonException{
		if (mAccount == null)
			throw new AppStockSingletonException("Logged in account not set!");
		
		return mAccount;
	}
	
	/**
	 * Sets the given account to the logged in account. Method is thread-safe.
	 * @param account The account of the user that logged in.
	 * @throws AppStockSingletonException If the account is already set. It cannot be set more than once. 
	 * Use {@link #clearInstance()} before setting a new account.
	 */	
	public static synchronized void initAccountLoggedIn(Account account) throws AppStockSingletonException{
		if (mAccount != null)
			throw new AppStockSingletonException("Logged in account is already set!");
		
		mAccount = account;
	}
	
	/**
	 * Clears the current logged in user. Use if user logged out.
	 */
	public static void clearInstance(){		
		mAccount = null;
	}
	
	/**
	 * Checks if logged in account is already set.
	 * @return <code>true</code> if there is a logged in account set, <code>false</code> otherwise.
	 */
	public static boolean isSet(){
		return mAccount != null;
	}
	
}
