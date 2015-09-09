package com.technisat.appstock.webservice.error;


public class JSONErrorHandler {
	
	public static final int WS_ERROR_UNKNOWN = 0;
	public static final int WS_ERROR_NOERROR = 1;
	public static final int WS_ERROR_AUTHFAILED = 2;
	public static final int WS_ERROR_IOEXCEPTION = 3;
	public static final int WS_ERROR_CLIENTPROTOCOLEXCEPTION = 4;
	
	public static final int WS_ERROR_COULDNOTVERIFYPAYMENT = 101;
	
	public static final int WS_ERROR_NODBCONNECTION = 300;
	
	public static final int WS_ERROR_REGISTER_UNKNOWN = 400;
	public static final int WS_ERROR_REGISTER_NOUSERNAME = 401;
	public static final int WS_ERROR_REGISTER_NOMAIL = 402;
	public static final int WS_ERROR_REGISTER_NOPASSWORD = 403;
	public static final int WS_ERROR_REGISTER_STOREUSERINFORMATION = 404;
	
	public static final int WS_ERROR_UPDACCOUNT_UNKNOWN = 410;
	public static final int WS_ERROR_UPDACCOUNT_NOACCOUNTID = 411;
	public static final int WS_ERROR_UPDACCOUNT_NOSESSIONKEY = 412;
	public static final int WS_ERROR_UPDACCOUNT_STOREUSERINFORMATION = 413;	
	
	public static final int WS_ERROR_LOGIN_UNKNOWN = 420;
	public static final int WS_ERROR_LOGIN_NOUSERNAME = 421;
	public static final int WS_ERROR_LOGIN_NOPASSWORD = 422;
	public static final int WS_ERROR_LOGIN_NOUSERMAILFOUND = 423;
	public static final int WS_ERROR_LOGIN_WRONGPASSWORD = 424;
	public static final int WS_ERROR_LOGIN_NODEVICEID = 425;
	
	public static final int WS_ERROR_VALIDATE_PW_UNKNOWN = 430;
	public static final int WS_ERROR_VALIDATE_PW_NOACCOUNTID = 431;
	public static final int WS_ERROR_VALIDATE_PW_NOPASSWORD = 432;
	public static final int WS_ERROR_VALIDATE_PW_NOSESSIONKEY = 433;
	public static final int WS_ERROR_VALIDATE_PW_NOUSERFOUND = 434;
	public static final int WS_ERROR_VALIDATE_PW_WRONGPASSWORD = 435;
	
	public static final int WS_ERROR_VALIDATE_USERNAME_UNKNOWN = 460;
	public static final int WS_ERROR_VALIDATE_USERNAME_NOUSERNAME = 461;
	public static final int WS_ERROR_VALIDATE_USERNAME_ALREADYEXISTS = 462;
	
	public static final int WS_ERROR_VALIDATE_USERMAIL_UNKNOWN = 470;
	public static final int WS_ERROR_VALIDATE_USERMAIL_NOUSERNAME = 471;
	public static final int WS_ERROR_VALIDATE_USERMAIL_ALREADYEXISTS = 472;
	
	public static final int WS_ERROR_LOGOUT_UNKNOWN = 440;
	public static final int WS_ERROR_LOGOUT_NOACCOUNDID = 441;
	public static final int WS_ERROR_LOGOUT_NOSESSIONKEY = 442;
	
	public static final int WS_ERROR_REFRESHACCOUNT_UNKNOWN = 450;
	public static final int WS_ERROR_REFRESHACCOUNT_NOACCOUNTID = 451;
	public static final int WS_ERROR_REFRESHACCOUNT_NOSESSIONKEY = 452;
	public static final int WS_ERROR_REFRESHACCOUNT_NODEVICEID = 453;
	public static final int WS_ERROR_REFRESHACCOUNT_NOTLOGGEDIN = 454;
	
	public static final int WS_ERROR_UPGRADEACCOUNT_UNKNOWN = 480;
	public static final int WS_ERROR_UPGRADEACCOUNT_NOACCOUNTID = 481;
	public static final int WS_ERROR_UPGRADEACCOUNT_NOSESSIONKEY = 482;
	public static final int WS_ERROR_UPGRADEACCOUNT_NOIBAN = 483;
	public static final int WS_ERROR_UPGRADEACCOUNT_NOBIC = 484;
	public static final int WS_ERROR_UPGRADEACCOUNT_NOOWNERNAME = 485;
	public static final int WS_ERROR_UPGRADEACCOUNT_NOPAYPALRESPONSE = 486;
	public static final int WS_ERROR_UPGRADEACCOUNT_ACCOUNTISNONORMALUSER = 487;
	public static final int WS_ERROR_UPGRADEACCOUNT_ERRORSTORINGBANKACCOUNT = 488;
	public static final int WS_ERROR_UPGRADEACCOUNT_ERRORUPDATINGACCOUNT = 489;
	
	public static final int WS_ERROR_GETUPGRADEPRICE_UNKNOWN = 490;
	public static final int WS_ERROR_GETUPGRADEPRICE_NOSESSIONKEY = 491;
	
	public static final int WS_ERROR_GETCATEGORIES_UNKNOWN = 500;
	public static final int WS_ERROR_GETCATEGORIES_NOQUORUM = 501;
	public static final int WS_ERROR_GETCATEGORIES_NOCONTENTTYPE = 502;
	
	public static final int WS_ERROR_GETCONTENT_UNKNOWN = 510;
	public static final int WS_ERROR_GETCONTENT_NOSTART = 511;
	public static final int WS_ERROR_GETCONTENT_NOEND = 512;
	public static final int WS_ERROR_GETCONTENT_NOCONTENTTYPE = 513;
	public static final int WS_ERROR_GETCONTENT_NOPRICETYPE = 514;
	public static final int WS_ERROR_GETCONTENT_NOSTATUS = 515;
	public static final int WS_ERROR_GETCONTENT_NOCATEGORYTYPE = 516;
	public static final int WS_ERROR_GETCONTENT_NOISFEATURED = 517;
	public static final int WS_ERROR_GETCONTENT_NOISSPECIALDEAL = 518;
	
	public static final int WS_ERROR_GETOWNEDCONTENT_UNKNOWN = 520;
	public static final int WS_ERROR_GETOWNEDCONTENT_NOSTART = 521;
	public static final int WS_ERROR_GETOWNEDCONTENT_NOEND = 522;
	public static final int WS_ERROR_GETOWNEDCONTENT_NOCONTENTTYPE = 523;
	public static final int WS_ERROR_GETOWNEDCONTENT_NOACCOUNTDID = 524;
	public static final int WS_ERROR_GETOWNEDCONTENT_NOSTATUS = 525;
	public static final int WS_ERROR_GETOWNEDCONTENT_NOCATEGORYTYPE = 526;
	
	public static final int WS_ERROR_GETCOMMENTS_UNKNOWN = 530;
	public static final int WS_ERROR_GETCOMMENTS_NOSTART = 531;
	public static final int WS_ERROR_GETCOMMENTS_NOEND = 532;
	public static final int WS_ERROR_GETCOMMENTS_NOCONTENTTYPE = 533;
	public static final int WS_ERROR_GETCOMMENTS_NOACCOUNTDID = 534;
	
	public static final int WS_ERROR_GETWISHLIST_UNKNOWN = 540;	
	public static final int WS_ERROR_GETWISHLIST_NOACCOUNTDID = 541;
	public static final int WS_ERROR_GETWISHLIST_NOSESSIONKEY = 542;
	
	public static final int WS_ERROR_PWFORGOTTEN_UNKNOWN = 550;	
	public static final int WS_ERROR_PWFORGOTTEN_NOUSERMAIL = 551;
	public static final int WS_ERROR_PWFORGOTTEN_NOACCOUNTFOUND = 555;
	
	public static final int WS_ERROR_ADDTOWISHLIST_UNKNOWN = 600;	
	public static final int WS_ERROR_ADDTOWISHLIST_NOCONTENTID = 601;
	public static final int WS_ERROR_ADDTOWISHLIST_NOACCOUNTID = 602;
	public static final int WS_ERROR_ADDTOWISHLIST_NOSESSIONKEY = 603;
	
	public static final int WS_ERROR_REMOVEFROMWISHLIST_UNKNOWN = 610;	
	public static final int WS_ERROR_REMOVEFROMWISHLIST_NOCONTENTID = 611;
	public static final int WS_ERROR_REMOVEFROMWISHLIST_NOACCOUNTID = 612;
	public static final int WS_ERROR_REMOVEFROMWISHLIST_NOSESSIONKEY = 613;
	
	public static final int WS_ERROR_BUY_UNKNOWN = 800;	
	public static final int WS_ERROR_BUY_NOCONTENTID = 801;
	public static final int WS_ERROR_BUY_NOACCOUNTID = 802;
	public static final int WS_ERROR_BUY_NOSESSIONKEY = 803;
	public static final int WS_ERROR_BUY_COULDNOTSTOREDATA = 804;
	
	public static final int WS_ERROR_CANBUY_UNKNOWN = 810;	
	public static final int WS_ERROR_CANBUY_NOCONTENTID = 811;
	public static final int WS_ERROR_CANBUY_NOACCOUNTID = 812;
	public static final int WS_ERROR_CANBUY_NOSESSIONKEY = 813;
	public static final int WS_ERROR_CANBUY_ALREADYOWNSCONTENT = 814;
	
	public static final int WS_ERROR_DOWNLOAD_UNKNOWN = 900;
	public static final int WS_ERROR_DOWNLOAD_IOEXCEPTION = 904;
	public static final int WS_ERROR_DOWNLOAD_DLCANCELED = 905;
	public static final int WS_ERROR_DOWNLOAD_DOWNLOADFORBIDDEN = 906;
	public static final int WS_ERROR_DOWNLOAD_FNF = 907;
	
	public static String getErrorDescriptionByErrorCode(Integer code){
		if(null==code)
			return "There was a problem requesting the information from the webservice. Please try again later.";
			
		switch (code) {
		case WS_ERROR_UNKNOWN:
			return "There was a problem requesting the information from the webservice. Please try again later.";
		case WS_ERROR_NOERROR:
			return "Everything went fine. No need to worry.";
		case WS_ERROR_AUTHFAILED:
			return "Authentification failed. Request was not accepted by the webservice.";
		case WS_ERROR_NODBCONNECTION:
			return "There was a problem connecting to the database. Please try again later.";
		case WS_ERROR_IOEXCEPTION:
			return "Sie haben scheinbar ein Problem mit Ihrer Internetverbindung. Bitte stellen Sie sicher, dass diese auf AN/ON angeschaltet ist und versuchen Sie es erneut.";
		case WS_ERROR_CLIENTPROTOCOLEXCEPTION:
			return "Sie haben scheinbar ein Problem mit Ihrer Internetverbindung. Bitte stellen Sie sicher, dass diese auf AN/ON angeschaltet ist und versuchen Sie es erneut.";
			
		case WS_ERROR_REGISTER_UNKNOWN:
			return "There was a problem creating the account. Please try again later.";
		case WS_ERROR_REGISTER_NOUSERNAME:
			return "There was no user name provided for registering an account.";
		case WS_ERROR_REGISTER_NOMAIL:
			return "There was no mail provided for registering an account.";
		case WS_ERROR_REGISTER_NOPASSWORD:
			return "There was no password provided for registering an account.";
		case WS_ERROR_REGISTER_STOREUSERINFORMATION:
			return "The user name and/or email is already in use. Try to reset your password if you want to login and have forgotten your password.";
			
		case WS_ERROR_UPDACCOUNT_UNKNOWN:
			return "There was a problem updating the account. Please try again later.";
		case WS_ERROR_UPDACCOUNT_NOACCOUNTID:
			return "There was no account id provided for updating an account.";
		case WS_ERROR_UPDACCOUNT_NOSESSIONKEY:
			return "There was no session key provided for updating an account.";
		case WS_ERROR_UPDACCOUNT_STOREUSERINFORMATION:
			return "There was a problem updating your user information in the database. Please try again later.";
			
		case WS_ERROR_LOGIN_UNKNOWN:
			return "There was a problem logging in.";
		case WS_ERROR_LOGIN_NOUSERNAME:
			return "There was no username provided for logging in an account.";
		case WS_ERROR_LOGIN_NOPASSWORD:
			return "There was no password provided for logging in an account.";
		case WS_ERROR_LOGIN_NOUSERMAILFOUND:
			return "There was no user found with that user name or email.";
		case WS_ERROR_LOGIN_WRONGPASSWORD:
			return "The password could not be verified. Please check your entered password and try again.";
		case WS_ERROR_LOGIN_NODEVICEID:
			return "There was no device id provided for logging in an account.";
			
		case WS_ERROR_VALIDATE_PW_UNKNOWN:
			return "The password could not be verified due to an unknown error.";
		case WS_ERROR_VALIDATE_PW_NOACCOUNTID:
			return "No account id was provided for verifing a password.";
		case WS_ERROR_VALIDATE_PW_NOPASSWORD:
			return "No password was provided for verifing it.";
		case WS_ERROR_VALIDATE_PW_NOSESSIONKEY:
			return "No session key was provided for verifing a password.";
		case WS_ERROR_VALIDATE_PW_NOUSERFOUND:
			return "The user account was not found for verifing it's password.";
		case WS_ERROR_VALIDATE_PW_WRONGPASSWORD:
			return "The password you entered is wrong. Please correct the password and try again.";
			
		case WS_ERROR_VALIDATE_USERNAME_UNKNOWN:
			return "Check if user name is available failed due to an unknown error.";
		case WS_ERROR_VALIDATE_USERNAME_NOUSERNAME:
			return "No user name provided for checking if user name is still available.";
		case WS_ERROR_VALIDATE_USERNAME_ALREADYEXISTS:
			return "There is already an account with the given user name.";
			
		case WS_ERROR_VALIDATE_USERMAIL_UNKNOWN:
			return "Check if email address is available failed due to an unknown error.";
		case WS_ERROR_VALIDATE_USERMAIL_NOUSERNAME:
			return "No user name provided for checking if email address is still available.";
		case WS_ERROR_VALIDATE_USERMAIL_ALREADYEXISTS:
			return "There is already an account with the given mail address.";
			
		case WS_ERROR_LOGOUT_UNKNOWN:
			return "You could not be logged out due to an unknown error.";
		case WS_ERROR_LOGOUT_NOACCOUNDID:
			return "No account id was provided for logging out.";
		case WS_ERROR_LOGOUT_NOSESSIONKEY:
			return "No session key was provided for logging out.";
			
		case WS_ERROR_REFRESHACCOUNT_UNKNOWN:
			return "Account could not be refreshed due to an unknown error.";
		case WS_ERROR_REFRESHACCOUNT_NOACCOUNTID:
			return "No account id provided for refreshing account.";
		case WS_ERROR_REFRESHACCOUNT_NOSESSIONKEY:
			return "No session key provided for refreshing account.";
		case WS_ERROR_REFRESHACCOUNT_NODEVICEID:
			return "No device id provided for refreshing account.";
		case WS_ERROR_REFRESHACCOUNT_NOTLOGGEDIN:
			return "Could not refresh account, user is not logged in with given account.";
			
		case WS_ERROR_UPGRADEACCOUNT_UNKNOWN:
			return "Account could not be upgraded due to an unknown error.";
		case WS_ERROR_UPGRADEACCOUNT_NOACCOUNTID:
			return "No account id provided for upgrading account.";
		case WS_ERROR_UPGRADEACCOUNT_NOSESSIONKEY:
			return "No session key provided for upgrading account.";
		case WS_ERROR_UPGRADEACCOUNT_NOIBAN:
			return "No IBAN provided for upgrading account.";
		case WS_ERROR_UPGRADEACCOUNT_NOBIC:
			return "No BIC provided for upgrading account.";
		case WS_ERROR_UPGRADEACCOUNT_NOOWNERNAME:
			return "No owner name for bank account provided for upgrading account.";
		case WS_ERROR_UPGRADEACCOUNT_NOPAYPALRESPONSE:
			return "No paypal response provided for upgrading account. Could not verify paymant.";
		case WS_ERROR_UPGRADEACCOUNT_ACCOUNTISNONORMALUSER:
			return "Could not upgrade account because it is no normal account. Please contact the support team.";
		case WS_ERROR_UPGRADEACCOUNT_ERRORSTORINGBANKACCOUNT:
			return "Could not store bank account in database. Please contact the support team.";
		case WS_ERROR_UPGRADEACCOUNT_ERRORUPDATINGACCOUNT:
			return "Could not update account information in database. Please contact the support team.";
			
		case WS_ERROR_GETUPGRADEPRICE_UNKNOWN:
			return "Upgrade to developer account price could not be received due to an unknown error.";
		case WS_ERROR_GETUPGRADEPRICE_NOSESSIONKEY:
			return "No session key provided for getting upgrade to developer account price.";
			
		case WS_ERROR_GETCATEGORIES_UNKNOWN:
			return "Categories could not be received due to an unknown error.";
		case WS_ERROR_GETCATEGORIES_NOQUORUM:
			return "No quorum was provided for logging out.";
		case WS_ERROR_GETCATEGORIES_NOCONTENTTYPE:
			return "No content type was provided for logging out.";
			
		case WS_ERROR_GETCONTENT_UNKNOWN:
			return "Content could not be received due to an unknown error.";
		case WS_ERROR_GETCONTENT_NOSTART:
			return "No start value was provided for getting content.";
		case WS_ERROR_GETCONTENT_NOEND:
			return "No end value was provided for getting content.";
		case WS_ERROR_GETCONTENT_NOCONTENTTYPE:
			return "No content type value was provided for getting content.";
		case WS_ERROR_GETCONTENT_NOPRICETYPE:
			return "No price type value was provided for getting content.";
		case WS_ERROR_GETCONTENT_NOSTATUS:
			return "No status value was provided for getting content.";
		case WS_ERROR_GETCONTENT_NOCATEGORYTYPE:
			return "No category type value was provided for getting content.";
		case WS_ERROR_GETCONTENT_NOISFEATURED:
			return "No isFeatured value was provided for getting content.";
		case WS_ERROR_GETCONTENT_NOISSPECIALDEAL:
			return "No isSpecialDeal value was provided for getting content.";
			
		case WS_ERROR_GETOWNEDCONTENT_UNKNOWN:
			return "Owned content could not be received due to an unknown error.";
		case WS_ERROR_GETOWNEDCONTENT_NOSTART:
			return "No start value was provided for getting owned content.";
		case WS_ERROR_GETOWNEDCONTENT_NOEND:
			return "No end value was provided for getting owned content.";
		case WS_ERROR_GETOWNEDCONTENT_NOCONTENTTYPE:
			return "No content type value was provided for getting owned content.";
		case WS_ERROR_GETOWNEDCONTENT_NOACCOUNTDID:
			return "No account id value was provided for getting owned content.";
		case WS_ERROR_GETOWNEDCONTENT_NOSTATUS:
			return "No status value was provided for getting owned content.";
		case WS_ERROR_GETOWNEDCONTENT_NOCATEGORYTYPE:
			return "No category type value was provided for getting owned content.";
			
		case WS_ERROR_GETCOMMENTS_UNKNOWN:
			return "Comments could not be received due to an unknown error.";
		case WS_ERROR_GETCOMMENTS_NOSTART:
			return "No start value was provided for getting comments.";
		case WS_ERROR_GETCOMMENTS_NOEND:
			return "No end value was provided for getting comments.";
		case WS_ERROR_GETCOMMENTS_NOCONTENTTYPE:
			return "No content type value was provided for getting comments.";
		case WS_ERROR_GETCOMMENTS_NOACCOUNTDID:
			return "No account id value was provided for getting comments.";
			
		case WS_ERROR_GETWISHLIST_UNKNOWN:
			return "Wishlist could not be received due to an unknown error.";
		case WS_ERROR_GETWISHLIST_NOACCOUNTDID:
			return "No account id was provided for getting wishlist.";
		case WS_ERROR_GETWISHLIST_NOSESSIONKEY:
			return "No session key was provided for getting wishlist.";
			
		case WS_ERROR_PWFORGOTTEN_UNKNOWN:
			return "Password forgotten could not be executed due to an unknown error.";
		case WS_ERROR_PWFORGOTTEN_NOUSERMAIL:
			return "No username or mail was provided for forgotten password.";
		case WS_ERROR_PWFORGOTTEN_NOACCOUNTFOUND:
			return "No account was found for the given data.";
			
		case WS_ERROR_ADDTOWISHLIST_UNKNOWN:
			return "Could not add content to wishlist due to an unknown error.";
		case WS_ERROR_ADDTOWISHLIST_NOCONTENTID:
			return "No content id was provided for adding content to wishlist.";
		case WS_ERROR_ADDTOWISHLIST_NOACCOUNTID:
			return "No account id was provided for adding content to wishlist.";
		case WS_ERROR_ADDTOWISHLIST_NOSESSIONKEY:
			return "No session key was provided for adding content to wishlist.";
			
		case WS_ERROR_REMOVEFROMWISHLIST_UNKNOWN:
			return "Could not remove content from wishlist due to an unknown error.";
		case WS_ERROR_REMOVEFROMWISHLIST_NOCONTENTID:
			return "No content id was provided for removing content from wishlist.";
		case WS_ERROR_REMOVEFROMWISHLIST_NOACCOUNTID:
			return "No account id was provided for removing content from wishlist.";
		case WS_ERROR_REMOVEFROMWISHLIST_NOSESSIONKEY:
			return "No session key was provided for removing content from wishlist.";
			
		case WS_ERROR_BUY_UNKNOWN:
			return "Could not buy content due to an unknown error.";
		case WS_ERROR_BUY_NOCONTENTID:
			return "No content id was provided for buying content.";
		case WS_ERROR_BUY_NOACCOUNTID:
			return "No account id was provided for buying content.";
		case WS_ERROR_BUY_NOSESSIONKEY:
			return "No session key was provided for buying content.";
		case WS_ERROR_BUY_COULDNOTSTOREDATA:
			return "Could not store purchase data. Please try again later.";
			
		case WS_ERROR_CANBUY_UNKNOWN:
			return "Could check if user can buy content due to an unknown error.";
		case WS_ERROR_CANBUY_NOCONTENTID:
			return "No content id was provided for checking if user can buy content.";
		case WS_ERROR_CANBUY_NOACCOUNTID:
			return "No account id was provided for checking if user can buy content.";
		case WS_ERROR_CANBUY_NOSESSIONKEY:
			return "No session key was provided for checking if user can buy content.";
		case WS_ERROR_CANBUY_ALREADYOWNSCONTENT:
			return "User already owns the content.";
			
		case WS_ERROR_DOWNLOAD_UNKNOWN:
			return "Could not download content due to an unknown error.";
		case WS_ERROR_DOWNLOAD_DLCANCELED:
			return "User has canceled the download.";
		case WS_ERROR_DOWNLOAD_DOWNLOADFORBIDDEN:
			return "The currently logged in account is not allowed to download this content.";
		case WS_ERROR_DOWNLOAD_IOEXCEPTION:
			return "Could not download file due to an IOException.";
		case WS_ERROR_DOWNLOAD_FNF:
			return "Could not download the content, webservice did not found the content file. Please contact the support and try again later.";
			
		default:
			return "There was a problem requesting the information from the webservice. Please try again later.";
		}
	}
	
}
