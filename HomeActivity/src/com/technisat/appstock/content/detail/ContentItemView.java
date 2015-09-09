package com.technisat.appstock.content.detail;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Calendar;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.technisat.appstock.R;
import com.technisat.appstock.entity.Account;
import com.technisat.appstock.entity.App;
import com.technisat.appstock.entity.Content;
import com.technisat.appstock.entity.Magazine;
import com.technisat.appstock.entity.Picture;
import com.technisat.appstock.entity.Video;
import com.technisat.appstock.entity.singleton.AccountLoggedIn;
import com.technisat.appstock.exception.AppStockSingletonException;
import com.technisat.appstock.helper.StyleMessageDialog;
import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;
import com.technisat.constants.FileStorageHelper;
import com.technisat.constants.Nexxoo;
import com.technisat.constants.NexxooPayment;

public class ContentItemView extends RelativeLayout {

	private static final String TAG = "ContentItemView";
	private TextView mName, mDeveloperName, mLikes, mPrice, mSpecialPrice;
	private ImageView mContentIcon, mLikeImage, mInstall;
	private View mLine;
	private boolean mIsOwnedContent = false, mIsInstalled = false,
			mIsNewestVersion = false;
	private ContentDetailActivity mActivity = null;
	private Context mContext = null;
	private Account mAccount = null;
	private ProgressBar mProgressBar = null;
	private Button mCancelDownload;
	private TextView mProgressText;
	private TextView mSpecialDealDate;
	private TextView mSpecialPriceCut;
	private ImageView mSpecialFlag;
	private TextView fileSizeTextView;

	private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
			.environment(NexxooPayment.PAYPAL_CONFIG_ENVIRONMENT)
			.clientId(NexxooPayment.PAYPAL_CONFIG_CLIENT_ID)
			// The following are only used in PayPalFuturePaymentActivity.
			.merchantName("AppStock")
			.merchantPrivacyPolicyUri(
					Uri.parse("https://www.example.com/privacy"))
			.merchantUserAgreementUri(
					Uri.parse("https://www.example.com/legal"));

	public ContentItemView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public ContentItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	public void buyAction(Content content) {
		new ClickListenerTest(content, ClickListenerTest.BUY).buyAction();;
	}

	private void init() {
		if (!isInEditMode()) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			View root = inflater.inflate(R.layout.content_item_new, null);
			mName = (TextView) root.findViewById(R.id.tv_item_name);
			mDeveloperName = (TextView) root
					.findViewById(R.id.tv_item_developer_name);
			mLikes = (TextView) root.findViewById(R.id.tv_item_like_text);
			mContentIcon = (ImageView) root.findViewById(R.id.i_item_icon);
			mLikeImage = (ImageView) root.findViewById(R.id.i_item_arrow);
			mPrice = (TextView) root.findViewById(R.id.tv_item_price);
			mSpecialPrice = (TextView) root
					.findViewById(R.id.tv_item_specialprice);
			mInstall = (ImageView) root
					.findViewById(R.id.i_item_update_or_install);

			mLine = root.findViewById(R.id.line_item_crossout_old_price);
			
			mProgressBar = (ProgressBar) root.findViewById(R.id.item_download_progress);
			mCancelDownload = (Button) root.findViewById(R.id.b_cancel_download);
			mProgressText = (TextView) root.findViewById(R.id.tv_progress_text);
			
			mSpecialDealDate = (TextView) root.findViewById(R.id.tv_special_deal_string);
			mSpecialPriceCut = (TextView) root.findViewById(R.id.tv_item_price_cut);
			mSpecialFlag = (ImageView) root.findViewById(R.id.iv_special_deal_flag);
			fileSizeTextView = (TextView) root.findViewById(R.id.tv_detail_size);
			addView(root);
		}
	}
	
	public void showProgressBar(){
		mProgressBar.setProgress(0);
		mProgressText.setText("0%");
		mProgressBar.setVisibility(View.VISIBLE);		
		mLikes.setVisibility(View.INVISIBLE);
		mLikeImage.setVisibility(View.INVISIBLE);
		mCancelDownload.setVisibility(View.VISIBLE);
		mProgressText.setVisibility(View.VISIBLE);
		fileSizeTextView.setVisibility(View.VISIBLE);
	}
	
	private boolean isProgressBarShown(){
		return mProgressBar.getVisibility() == View.VISIBLE;
	}
	
	public void setProgress(int progress){
		if(progress==100){
			hideProgress();
			return;
		}
		if(!isProgressBarShown()){
			showProgressBar();
		}
		mProgressBar.setProgress(progress);
		mProgressText.setText(progress+"%");
		int width = mProgressBar.getWidth();
		float progresPercentage = ((float)progress)/100f;
		int mProgressTextWidth = mProgressText.getWidth();
		int xMargin = (int)(width*progresPercentage)- mProgressTextWidth/2;
		if(xMargin>0&&xMargin+mProgressTextWidth<width){
			RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams)mProgressText.getLayoutParams();
			relativeParams.setMargins(xMargin, 0, 0, 0);  // left, top, right, bottom
			mProgressText.setLayoutParams(relativeParams);
		}
	}
	
	public void hideProgress(){
		mProgressBar.setVisibility(View.INVISIBLE);
		mProgressBar.setProgress(0);		
		mLikes.setVisibility(View.VISIBLE);
		mLikeImage.setVisibility(View.VISIBLE);
		mCancelDownload.setVisibility(View.INVISIBLE);
		mProgressText.setVisibility(View.INVISIBLE);
		fileSizeTextView.setVisibility(View.INVISIBLE);
	}

	public void setContent(final Content content, boolean isOnDetailPage,
			ContentDetailActivity activity) {
		if (activity != null) {
			mActivity = activity;
			Log.d("JD", "set setContent");
		}
		
		mCancelDownload.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Log.d("JD", "mCancelDownload clicked");
				NexxooWebservice.cancelDownload(content);
			}
		});

		mName.setText(content.getName());
		mDeveloperName.setText(content.getOwnerName());

		/********************************************
		 * Set Content Icon by Type *
		 ********************************************/
		try {
		ImageLoader mImageLoader = ImageLoader.getInstance();
		switch (content.getContentType()) {
		case App.CONTENTTYPE:
			mImageLoader.displayImage(
					content.getPicturesByType(Picture.PICTURETYPE_APP_ICON)
							.get(0).getmUrl(), mContentIcon);
			break;
		case Magazine.CONTENTTYPE:
			mImageLoader.displayImage(
					content.getPicturesByType(
							Picture.PICTURETYPE_MAGAZINE_COVER).get(0)
							.getmUrl(), mContentIcon);
			break;
		case Video.CONTENTTYPE:
			mImageLoader.displayImage(
					content.getPicturesByType(Picture.PICTURETYPE_VIDEO_COVER)
							.get(0).getmUrl(), mContentIcon);
			break;
		}
		}catch(IndexOutOfBoundsException ex){
			
		}

		/********************************************************
		 * This part handles the like/dislike display part *
		 ********************************************************/
		// replace values in text with actual numbers
		String text = mContext.getString(R.string.appstock_text_xofxlike);
		text = text.replaceAll(Nexxoo.REPLACE_1,
				Integer.toString(content.getLikeCount()));
		text = text.replaceAll(
				Nexxoo.REPLACE_2,
				Integer.toString(content.getDislikeCount()
						+ content.getLikeCount()));
		mLikes.setText(text);
		if (content.getLikeCount() > 0 && content.getDislikeCount() > 0) {
			double wert = content.getLikeCount() / content.getDislikeCount();
			if (wert >= 2)
				mLikeImage.setImageResource(R.drawable.ic_action_arrow_up); // TODO:
																		// set
																		// final
																		// icons
			else if (1.1 >= wert && wert < 2)
				mLikeImage.setImageResource(R.drawable.ic_action_arrow_side_up);
			else if (0.9 >= wert && wert < 1.1)
				mLikeImage.setImageResource(R.drawable.ic_action_arrow_side);
			else if (0.5 >= wert && wert < 0.9)
				mLikeImage.setImageResource(R.drawable.ic_action_arrow_side_down);
			else
				mLikeImage.setImageResource(R.drawable.ic_action_arrow_down); 
		} else if (content.getLikeCount() > 0 && content.getDislikeCount() == 0) {
			mLikeImage.setImageResource(R.drawable.ic_small_heart); // top rated
																	// icon
		} else if (content.getLikeCount() == 0 && content.getDislikeCount() > 0) {
			mLikeImage.setImageResource(R.drawable.ic_action_arrow_down); // bad rated
																	// icon
		} else {
			mLikes.setText(mContext
					.getString(R.string.appstock_text_bethefirst));
			mLikeImage.setImageResource(R.drawable.ic_action_arrow_side);
		}

		/********************************************************
		 * Check if content is Owned by User *
		 ********************************************************/
		try {
			mAccount = AccountLoggedIn.getInstance();
			Log.d("JD", "has content="+mAccount.getOwnedContentIdList().contains(
					content.getContentId() + ""));
			if (mAccount.getOwnedContentIdList().contains(
					content.getContentId() + "")) {
				mIsOwnedContent = true;
				
				Log.d("JD", content.getContentType() == App.CONTENTTYPE ? "app" : "other");

				if (content.getContentType() == App.CONTENTTYPE) {
					/**************************************************************
					 * Check if app is installed and if it is on current version
					 * *
					 **************************************************************/
					App a = (App) content;
					// Log.d(Nexxoo.TAG, "versionCode: "+ a.getVersionCode()
					// +", package: "+ a.getPackageName()
					// +" for content name: "+ a.getName());
					mIsInstalled = FileStorageHelper.isInstalled(
							a.getPackageName(), mContext);
					if (mIsInstalled) {
						mIsNewestVersion = FileStorageHelper.isNewestVersion(
								a.getPackageName(), a.getVersionCode(),
								mContext);
						if (mIsNewestVersion) {
							mInstall.setVisibility(View.INVISIBLE);
						} else {
							mInstall.setVisibility(View.VISIBLE);
							mInstall.setImageResource(R.drawable.ic_action_update);
						}
					} else {
						mInstall.setVisibility(View.VISIBLE);
						mInstall.setImageResource(R.drawable.ic_action_download);
					}
				} else {
					Log.e("FILE", "Checking for install");
					/********************************************************
					 * Check if magazine/video is downloaded *
					 ********************************************************/
					mIsInstalled = FileStorageHelper.isDownloaded(content,
							mContext);
					Log.e("FILE", "And it is: "+mIsInstalled);
					if (mIsInstalled) {
						mIsNewestVersion = true;
						mInstall.setVisibility(View.INVISIBLE);
					} else {
						mInstall.setVisibility(View.VISIBLE);
						mInstall.setImageResource(R.drawable.ic_action_download);
					}
					
				}
			} else {
				mIsOwnedContent = false;
				mInstall.setVisibility(View.INVISIBLE);
			}

		} catch (AppStockSingletonException e) {
		}

		/**********************************************************
		 * check if special deal "layout" should be displayed *
		 **********************************************************/
		if (content.isSpecialDeal() && !mIsOwnedContent){// && !isOnDetailPage) {
			mSpecialPrice.setVisibility(View.INVISIBLE);
//			mSpecialPrice.setText(mContext
//					.getString(R.string.appstock_text_onlynow)
//					+ " "
//					+ content.getPrice()
//					+ mContext.getString(R.string.appstock_currency));
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(content.getSpecialDealEndDate());
			
			DateFormat df = DateFormat.getDateInstance();
			
			double ratio = content.getPrice() / content.getStandardPrice();
			int percent = (int) (ratio * 100.) + 1;
			
			String specialDeal = String.format("%d%% Nachlass bis %s", percent, df.format(cal.getTime()));
			mSpecialDealDate.setText(specialDeal);
			mSpecialDealDate.setVisibility(View.VISIBLE);
			
			mSpecialPriceCut.setText(percent+"%");
			mSpecialPriceCut.setVisibility(View.VISIBLE);
			
			mSpecialFlag.setVisibility(View.VISIBLE);
			
			mPrice.setText(content.getPrice()
					+ mContext.getString(R.string.appstock_currency));
			//mLine.setVisibility(View.VISIBLE);
			mPrice.setVisibility(View.VISIBLE);
			mPrice.setBackgroundColor(mContext.getResources().getColor(R.color.AppStockDrawerTrueBlue));
		} else {
			mSpecialPrice.setVisibility(View.GONE);
			mLine.setVisibility(View.GONE);
			if (content.getPrice() > 0 && !mIsOwnedContent) {
				mPrice.setVisibility(View.VISIBLE);
				mPrice.setText(content.getPrice()
						+ mContext.getString(R.string.appstock_currency));
			} else if (!mIsOwnedContent) {
				mPrice.setVisibility(View.VISIBLE);
				mPrice.setText(mContext.getString(R.string.appstock_free));
			} else
				mPrice.setVisibility(View.GONE);
		}

		/********************************************************************
		 * display buttons if view is displayed on detail activity *
		 ********************************************************************/
		if (isOnDetailPage) {
			Button btnBuyUpdateInstallOpen = (Button) findViewById(R.id.btn_item_buy_update_install_open);
			Button btnDeinstall = (Button) findViewById(R.id.btn_item_deinstall);
			btnBuyUpdateInstallOpen.setVisibility(View.VISIBLE);
			mInstall.setVisibility(View.INVISIBLE);
			mSpecialPrice.setVisibility(View.GONE);

			/*****************************************
			 * check if content is owned by user *
			 *****************************************/
			if (mIsOwnedContent) { // is owned content
				mPrice.setVisibility(View.GONE);

				/*************************************************************
				 * check if content is installed. Display or hide * "deinstall"
				 * and check for newestVersion if installed *
				 *************************************************************/
				if (mIsInstalled) {
					btnDeinstall.setOnClickListener(new ClickListenerTest(
							content, ClickListenerTest.DELETE));
					btnDeinstall.setVisibility(View.VISIBLE);
					
					if(content instanceof Magazine) {
						btnDeinstall.setText(mActivity.getString(R.string.appstock_text_uninstall_magazine));
					}else{
						btnDeinstall.setText(mActivity.getString(R.string.appstock_text_uninstall));
					}

					/********************************************************
					 * check if content is newest version and * display "update"
					 * or "open" * magazine and video are always
					 * "newest version" *
					 ********************************************************/
					if (mIsNewestVersion) { // is newest version
						btnBuyUpdateInstallOpen.setText(mContext
								.getString(R.string.appstock_text_open));
						btnBuyUpdateInstallOpen
								.setOnClickListener(new ClickListenerTest(
										content, ClickListenerTest.OPEN));
					} else { // isn't newest version
						btnBuyUpdateInstallOpen.setText(mContext
								.getString(R.string.appstock_text_update));
						btnBuyUpdateInstallOpen
								.setOnClickListener(new ClickListenerTest(
										content, ClickListenerTest.UPDATE));
					}
				} else { // isn't installed
					if (content.getContentType() == App.CONTENTTYPE)
						btnBuyUpdateInstallOpen.setText(mContext
								.getString(R.string.appstock_text_install));
					else
						btnBuyUpdateInstallOpen.setText(mContext
								.getString(R.string.appstock_text_download));
					btnBuyUpdateInstallOpen
							.setOnClickListener(new ClickListenerTest(content,
									ClickListenerTest.INSTALL));
					btnDeinstall.setVisibility(View.INVISIBLE);
				}
			} else { // isn't owned content
				if(content.getPrice() > 0){
					mPrice.setVisibility(View.VISIBLE);
					
					btnBuyUpdateInstallOpen.setText(mContext
							.getString(R.string.appstock_text_buy));
					
					btnBuyUpdateInstallOpen
					.setOnClickListener(new ClickListenerTest(content,
							ClickListenerTest.BUY));
				} else {
					
					if(content.getContentType() == App.CONTENTTYPE){
						btnBuyUpdateInstallOpen.setText(mContext
								.getString(R.string.appstock_text_install));
					} else {
						btnBuyUpdateInstallOpen.setText(mContext
								.getString(R.string.appstock_text_download));
					}
					
					btnBuyUpdateInstallOpen
					.setOnClickListener(new ClickListenerTest(content,
							ClickListenerTest.BUY));
				}
				
				btnBuyUpdateInstallOpen.setVisibility(View.VISIBLE);
			}
		}
	}

	/********************************************************
	 * Nested class to handle all click events *
	 * 
	 * @author b.nolde *
	 ********************************************************/
	private final class ClickListenerTest implements OnClickListener {
		public static final int BUY = 1;
		public static final int INSTALL = 2;
		public static final int UPDATE = 3;
		public static final int OPEN = 4;
		public static final int DELETE = 5;

		private Content content;
		private int mActionType = -1;

		private ClickListenerTest(Content content, int actionType) {
			this.content = content;
			mActionType = actionType;
		}

		@Override
		public void onClick(View v) {

			switch (mActionType) {

			/******************************************
			 * Button BUY content was pressed by user *
			 ******************************************/
			case BUY:
				buyAction();
				break;

			/********************************************************
			 * Button INSTALL/DOWNLOAD content was pressed by user *
			 ********************************************************/
			case INSTALL:
				if (!NexxooWebservice.isDownloading(mActivity, content)) {
					FileStorageHelper.download(content, mActivity, mAccount);
				} else {
					Log.d("JD", "download ignored");
				}
				break;

			/********************************************************
			 * Button UPDATE content was pressed by user *
			 ********************************************************/
			case UPDATE:
				if (!NexxooWebservice.isDownloading(mActivity, content)) {
					FileStorageHelper.download(content, mActivity, mAccount);
				} else {
					Log.d("JD", "download ignored");
				}
				break;

			/********************************************************
			 * Button OPEN content was pressed by user *
			 ********************************************************/
			case OPEN:
				// Log.d(Nexxoo.TAG, "contenttype: "+content.getContentType()
				// +" mime: "+ content.getMimeType());
				if (content.getContentType() == App.CONTENTTYPE) {
					App a = (App) content;
					Intent LaunchIntent = mActivity.getPackageManager()
							.getLaunchIntentForPackage(a.getPackageName());
					mActivity.startActivity(LaunchIntent);
				} else {
					try {
						File file = new File(
								content.getLocalFileName(mActivity));
						Intent intent2 = new Intent(Intent.ACTION_VIEW);
						intent2.setDataAndType(Uri.fromFile(file),
								content.getMimeType());
						mActivity.startActivity(intent2);
					} catch (ActivityNotFoundException e) {
						Log.e(Nexxoo.TAG2,
								"Tried to open activity, but no match was found: "
										+ e.getMessage());
						
						Dialog styleDialog2 = new StyleMessageDialog(getContext(), getContext().getString(R.string.no_application_found));
						styleDialog2.show();
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
					mActivity.startActivity(uninstallIntent);
				} else {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							mContext);
					alertDialogBuilder.setTitle(mContext
							.getString(R.string.appstock_caption_uninstall));
					alertDialogBuilder
							.setMessage(
									mContext.getString(
											R.string.appstock_text_alert_uninstall)
											.replaceAll(Nexxoo.REPLACE_1,
													content.getName()))
							.setCancelable(false)
							.setPositiveButton(
									mContext.getString(R.string.appstock_yes),
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {

											File file = new File(content
													.getLocalFileName(mContext));
											if (file.delete()) {
												Toast.makeText(
														mContext,
														mContext.getString(
																R.string.appstock_text_uninstall_complete)
																.replaceAll(
																		Nexxoo.REPLACE_1,
																		content.getName()),
														Toast.LENGTH_LONG)
														.show();
												mActivity
														.reloadActivityWithContent(content);
											} else {
											final Dialog styleDialog = new StyleMessageDialog(mContext,
													mContext.getString(
															R.string.appstock_text_uninstall_failed)
															.replaceAll(
																	Nexxoo.REPLACE_1,
																	content.getName()));
											styleDialog.show();
											}
										}
									})
							.setNegativeButton(
									mContext.getString(R.string.appstock_no),
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

		private void buyAction() {
			Intent intent = new Intent(mActivity, PayPalService.class);
			intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,
					paypalConfig);
			mActivity.startService(intent);
			if (mAccount != null) {
				if (content.getPrice() > 0) {

					PayPalPayment thingToBuy = new PayPalPayment(
							new BigDecimal(content.getPrice()),
							NexxooPayment.PAYMENT_CURRENCY,
							content.getName(),
							PayPalPayment.PAYMENT_INTENT_SALE);

					intent = new Intent(mActivity, PaymentActivity.class);
					intent.putExtra(PaymentActivity.EXTRA_PAYMENT,
							thingToBuy);
					mActivity.startActivityForResult(intent,
							NexxooPayment.PAYPAL_REQUEST_CODE_PAYMENT);
				} else {
					final ProgressDialog ringProgressDialog = ProgressDialog
							.show(mActivity,
									mContext.getString(R.string.appstock_pleasewait),
									mContext.getString(R.string.appstock_text_startingpayment),
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
													mActivity
															.reloadActivityWithContent(content);
													
													if(content.getPrice() <= 0){
														FileStorageHelper.download(content, mActivity, mAccount);
													}
													else {	
														Toast.makeText(
																getContext(),
																mContext.getString(
																		R.string.appstock_text_hasbeenbought)
																		.replaceAll(
																				Nexxoo.REPLACE_1,
																				content.getName()),
																Toast.LENGTH_SHORT)
																.show();
													}
													ringProgressDialog
															.dismiss();
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
				/*Toast.makeText(
						getContext(),
						mContext.getString(
								R.string.appstock_text_pleaselogintobuy)
								.replaceAll(Nexxoo.REPLACE_1,
										content.getName()),
						Toast.LENGTH_SHORT).show();*/
				Dialog styleDialog=null;
				if(content.getPrice()==0){
					if(content.getContentType()==App.CONTENTTYPE){
						styleDialog = new StyleMessageDialog(mActivity,String.format(mActivity.getString(
								R.string.appstock_text_pleaselogintoinstall), content.getName()));
					}else{
						styleDialog = new StyleMessageDialog(mActivity,String.format(mActivity.getString(
								R.string.appstock_text_pleaselogintodownload), content.getName()));
					}
				}else{
					styleDialog = new StyleMessageDialog(mActivity,String.format(mActivity.getString(
							R.string.appstock_text_pleaselogintobuy), content.getName()));
				}
				styleDialog.show();
			}
		}
	}
}