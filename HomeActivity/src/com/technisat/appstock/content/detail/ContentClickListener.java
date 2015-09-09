package com.technisat.appstock.content.detail;

import java.io.File;
import java.math.BigDecimal;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.technisat.appstock.R;
import com.technisat.appstock.entity.Account;
import com.technisat.appstock.entity.App;
import com.technisat.appstock.entity.Content;
import com.technisat.appstock.entity.singleton.AccountLoggedIn;
import com.technisat.appstock.exception.AppStockSingletonException;
import com.technisat.appstock.helper.StyleMessageDialog;
import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;
import com.technisat.constants.FileStorageHelper;
import com.technisat.constants.Nexxoo;
import com.technisat.constants.NexxooPayment;

public final class ContentClickListener implements OnClickListener {
	public static final int BUY = 1;
	public static final int INSTALL = 2;
	public static final int UPDATE = 3;
	public static final int OPEN = 4;
	public static final int DELETE = 5;

	private Content content;
	private int mActionType = -1;
	private Activity activity;
	private Account mAccount;
	
	private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
	.environment(NexxooPayment.PAYPAL_CONFIG_ENVIRONMENT)
	.clientId(NexxooPayment.PAYPAL_CONFIG_CLIENT_ID)
	// The following are only used in PayPalFuturePaymentActivity.
	.merchantName("AppStock")
	.merchantPrivacyPolicyUri(
			Uri.parse("https://www.example.com/privacy"))
	.merchantUserAgreementUri(
			Uri.parse("https://www.example.com/legal"));

	public ContentClickListener(Activity activity, Content content, int actionType) {
		this.content = content;
		mActionType = actionType;
		this.activity = activity;
		
	}

	@Override
	public void onClick(View v) {
		try {
			mAccount = AccountLoggedIn.getInstance();
		} catch (AppStockSingletonException e) {
			Dialog styleDialog=null;
			if(content.getPrice()==0){
				if(content.getContentType()==App.CONTENTTYPE){
					styleDialog = new StyleMessageDialog(activity,String.format(activity.getString(
							R.string.appstock_text_pleaselogintoinstall), content.getName()));
				}else{
					styleDialog = new StyleMessageDialog(activity,String.format(activity.getString(
							R.string.appstock_text_pleaselogintodownload), content.getName()));
				}
			}else{
				styleDialog = new StyleMessageDialog(activity,String.format(activity.getString(
						R.string.appstock_text_pleaselogintobuy), content.getName()));
			}
			styleDialog.show();
			return;
		}

		switch (mActionType) {

		/********************************************************
		 * Button BUY content was pressed by user *
		 ********************************************************/
		case BUY:
			Intent intent = new Intent(activity, PayPalService.class);
			intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,
					paypalConfig);
			activity.startService(intent);
			if (activity != null) {
				if (content.getPrice() > 0) {

					PayPalPayment thingToBuy = new PayPalPayment(
							new BigDecimal(content.getPrice()),
							NexxooPayment.PAYMENT_CURRENCY,
							content.getName(),
							PayPalPayment.PAYMENT_INTENT_SALE);

					intent = new Intent(activity, PaymentActivity.class);
					intent.putExtra(PaymentActivity.EXTRA_PAYMENT,
							thingToBuy);
					activity.startActivityForResult(intent,
							NexxooPayment.PAYPAL_REQUEST_CODE_PAYMENT);
				} else {
					final ProgressDialog ringProgressDialog = ProgressDialog
							.show(activity,
									activity.getString(R.string.appstock_pleasewait),
									activity.getString(R.string.appstock_text_startingpayment),
									true);
					ringProgressDialog.setCancelable(true);
					/****************************************************
					 * check if user is allowed to buy that content * ( free
					 * is also a 'buy' ) *
					 ****************************************************/
					NexxooWebservice.canBuy(true, content.getContentId(),
							mAccount.getAccountId(),
							mAccount.getSessionKey(), new OnJSONResponse() {

								@Override
								public void onReceivedJSONResponse(
										JSONObject json) {
									/**********************************************
									 * if he is allowed to buy -> buy
									 * content *
									 **********************************************/
									NexxooWebservice.buyContent(true,
											content.getContentId(),
											mAccount.getAccountId(),
											mAccount.getSessionKey(), null,
											new OnJSONResponse() {

												@Override
												public void onReceivedJSONResponse(
														JSONObject json) {
													/**************************************************
													 * restart activity with
													 * updated *
													 * ownedContentList ( to
													 * display right buttons
													 * ) *
													 **************************************************/
													mAccount.addToOwnedContentList(content);
//													activity
//															.reloadActivityWithContent(content); // TODO: callback to refresh activity

													Dialog styleDialog = new StyleMessageDialog(activity,activity.getString(
															R.string.appstock_text_hasbeenbought)
															.replaceAll(
																	Nexxoo.REPLACE_1,
																	content.getName()));
													styleDialog.show();
												}

												@Override
												public void onReceivedError(
														String msg, int code) {
													// buy failed
													ringProgressDialog
															.dismiss();
												}
											});
								}

								@Override
								public void onReceivedError(String msg,
										int code) {
									// check canBuy failed
									ringProgressDialog.dismiss();
								}
							});
				}
			} else {
				Dialog styleDialog=null;
				if(content.getPrice()==0){
					if(content.getContentType()==App.CONTENTTYPE){
						styleDialog = new StyleMessageDialog(activity,String.format(activity.getString(
								R.string.appstock_text_pleaselogintoinstall), content.getName()));
					}else{
						styleDialog = new StyleMessageDialog(activity,String.format(activity.getString(
								R.string.appstock_text_pleaselogintodownload), content.getName()));
					}
				}else{
					styleDialog = new StyleMessageDialog(activity,String.format(activity.getString(
							R.string.appstock_text_pleaselogintobuy), content.getName()));
				}
				styleDialog.show();
			}
			break;

		/********************************************************
		 * Button INSTALL/DOWNLOAD content was pressed by user *
		 ********************************************************/
		case INSTALL:
			FileStorageHelper.download(content, activity, mAccount);
			break;

		/********************************************************
		 * Button UPDATE content was pressed by user *
		 ********************************************************/
		case UPDATE:
			FileStorageHelper.download(content, activity, mAccount);
			break;

		/********************************************************
		 * Button OPEN content was pressed by user *
		 ********************************************************/
		case OPEN:
			// Log.d(Nexxoo.TAG, "contenttype: "+content.getContentType()
			// +" mime: "+ content.getMimeType());
			if (content.getContentType() == App.CONTENTTYPE) {
				App a = (App) content;
				Intent LaunchIntent = activity.getPackageManager()
						.getLaunchIntentForPackage(a.getPackageName());
				activity.startActivity(LaunchIntent);
			} else {
				try {
					File file = new File(
							content.getLocalFileName(activity));
					Intent intent2 = new Intent(Intent.ACTION_VIEW);
					intent2.setDataAndType(Uri.fromFile(file),
							content.getMimeType());
					activity.startActivity(intent2);
				} catch (ActivityNotFoundException e) {
					Log.e(Nexxoo.TAG2,
							"Tried to open activity, but no match was found: "
									+ e.getMessage());
				}
			}
			break;

		/********************************************************
		 * Button DELETE content was pressed by user *
		 ********************************************************/
		case DELETE:
			if (content.getContentType() == App.CONTENTTYPE) {
				App a = (App) content;
				Uri packageUri = Uri.parse("package:" + a.getPackageName());
				Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
						packageUri);
				activity.startActivity(uninstallIntent);
			} else {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						activity);
				alertDialogBuilder.setTitle(activity
						.getString(R.string.appstock_caption_uninstall));
				alertDialogBuilder
						.setMessage(
								activity.getString(
										R.string.appstock_text_alert_uninstall)
										.replaceAll(Nexxoo.REPLACE_1,
												content.getName()))
						.setCancelable(false)
						.setPositiveButton(
								activity.getString(R.string.appstock_yes),
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialog, int id) {

										File file = new File(content
												.getLocalFileName(activity));
										if (file.delete()) {
											Toast.makeText(
													activity,
													activity.getString(
															R.string.appstock_text_uninstall_complete)
															.replaceAll(
																	Nexxoo.REPLACE_1,
																	content.getName()),
													Toast.LENGTH_LONG)
													.show();
											/*Dialog styleDialog = new StyleMessageDialog(activity,
													activity.getString(
															R.string.appstock_text_uninstall_complete)
															.replaceAll(
																	Nexxoo.REPLACE_1,
																	content.getName()));
											styleDialog.show();*/
//											activity										//TODO: callback to refresh activity
//													.reloadActivityWithContent(content);
										} else
											Toast.makeText(
													activity,
													activity.getString(
															R.string.appstock_text_uninstall_failed)
															.replaceAll(
																	Nexxoo.REPLACE_1,
																	content.getName()),
													Toast.LENGTH_SHORT)
													.show();
									}
								})
						.setNegativeButton(
								activity.getString(R.string.appstock_no),
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialog, int id) {
										dialog.cancel();
									}
								});

				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
			break;
		}
	}
}
