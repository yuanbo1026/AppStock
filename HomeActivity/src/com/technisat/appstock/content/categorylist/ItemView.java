package com.technisat.appstock.content.categorylist;

import java.text.DateFormat;
import java.util.Calendar;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.PopupMenu.OnMenuItemClickListener;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.technisat.appstock.R;
import com.technisat.appstock.content.detail.ContentClickListener;
import com.technisat.appstock.content.detail.ContentDetailActivity;
import com.technisat.appstock.entity.Account;
import com.technisat.appstock.entity.App;
import com.technisat.appstock.entity.Content;
import com.technisat.appstock.entity.Magazine;
import com.technisat.appstock.entity.Picture;
import com.technisat.appstock.entity.Video;
import com.technisat.appstock.entity.singleton.AccountLoggedIn;
import com.technisat.appstock.exception.AppStockSingletonException;
import com.technisat.appstock.helper.StyleMessageDialog;
import com.technisat.appstock.utils.AnimImageViewAware;
import com.technisat.appstock.utils.ProgressBarImageLoadingListener;
import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;
import com.technisat.constants.FileStorageHelper;
import com.technisat.constants.Nexxoo;

public class ItemView extends LinearLayout {

	ImageView itemIcon;
	ProgressBar itemProgressBar;
	TextView itemName; 
	TextView devName;

	TextView likesText;
	ImageView likesIcon;

	TextView itemPrice;

	TextView itemDealPriceText;
	TextView itemDealPriceCut;

	RelativeLayout contextMenuButton;

	Activity mActivity;
	private Content mContent = null;
	private Account mAccount;
	private boolean mIsOwnedContent;
	private boolean mIsInstalled;
	private boolean mIsNewestVersion;

	private ContentClickListener onBuyListener;
	private ContentClickListener onInstallListener;
	private ContentClickListener onUpdateListener;
	private ContentClickListener onDeleteListener;
	private ImageView itemDealFlag;
	private ImageView mInstall;
	
	private int resourceId = R.layout.bkp_item_layout;
	
	public ItemView(Context context, boolean asGrid){
		super(context);
		mActivity = (Activity) context;
		if(asGrid) 
			resourceId = R.layout.item_layout;
		
		init();
	}

	public ItemView(Context context) {
		super(context);
		mActivity = (Activity) context;
		init();
	}

	public ItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mActivity = (Activity) context;
		init();
	}

	public ItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mActivity = (Activity) context;
		init();
	}

	private void init() {	
		this.removeAllViews();
		LayoutInflater inflater = LayoutInflater.from(getContext());	
		View root = inflater.inflate(resourceId, null);
		itemName = (TextView) root.findViewById(R.id.tv_item_name);
		devName = (TextView) root.findViewById(R.id.tv_item_developer_name);
		likesText = (TextView) root.findViewById(R.id.tv_item_like_text);
		itemIcon = (ImageView) root.findViewById(R.id.i_item_icon);
		itemProgressBar = (ProgressBar) root.findViewById(R.id.i_item_progressbar);
		likesIcon = (ImageView) root.findViewById(R.id.i_item_arrow);
		itemPrice = (TextView) root.findViewById(R.id.tv_item_price);
		itemDealFlag = (ImageView) root.findViewById(R.id.iv_special_deal_flag);

		itemDealPriceText = (TextView) root
				.findViewById(R.id.tv_special_deal_string);
		itemDealPriceCut = (TextView) root
				.findViewById(R.id.tv_item_price_cut);
		
		mInstall = (ImageView) root
				.findViewById(R.id.i_item_update_or_install);
		
		contextMenuButton = (RelativeLayout) root.findViewById(R.id.btn_dots_menu);
		addView(root);
	}

	private void setupPopupMenu(final Content content, final boolean freeVersion) {
		onBuyListener = new ContentClickListener(mActivity, content,
				ContentClickListener.BUY);
		onInstallListener = new ContentClickListener(mActivity, content,
				ContentClickListener.INSTALL);
		onUpdateListener = new ContentClickListener(mActivity, content,
				ContentClickListener.UPDATE);
		onDeleteListener = new ContentClickListener(mActivity, content,
				ContentClickListener.DELETE);
		
		setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, ContentDetailActivity.class);
								
				if(content instanceof App){
					Log.d(Nexxoo.TAG, "ich bin eine app");
            		intent.putExtra(mActivity.getString(R.string.appstock_extra_content), (App) content);
            	} else if(content instanceof Magazine){
            		Log.d(Nexxoo.TAG, "ich bin ein magazin");
            		intent.putExtra(mActivity.getString(R.string.appstock_extra_content), (Magazine) content);
            	} else if(content instanceof Video) {
            		Log.d(Nexxoo.TAG, "ich bin ein video");
            		intent.putExtra(mActivity.getString(R.string.appstock_extra_content), (Video) content);
            	} else {
            		Log.d(Nexxoo.TAG, "ich bin ein content :(");
            	}
				mActivity.startActivity(intent);
			}				
		});
		OnClickListener onContextMenuClick=new OnClickListener() {

			@Override
			public void onClick(View v) {
				PopupMenu popup = new PopupMenu(mActivity, v);
				MenuInflater inflater = popup.getMenuInflater();
				inflater.inflate(R.menu.item_context_menu, popup.getMenu());
				
				

				if (mIsOwnedContent) {
					popup.getMenu().removeItem(R.id.buy);
					if (mIsInstalled) {
						popup.getMenu().removeItem(R.id.install);
						if (mIsNewestVersion) {
							popup.getMenu().removeItem(R.id.update);
						}
					} else {
						popup.getMenu().removeItem(R.id.uninstall);
						popup.getMenu().removeItem(R.id.update);
					}

				} else {
					popup.getMenu().removeItem(R.id.install);
					popup.getMenu().removeItem(R.id.uninstall);
					popup.getMenu().removeItem(R.id.update);
					if (freeVersion){
						if (content.getContentType() == App.CONTENTTYPE)
							popup.getMenu().findItem(R.id.buy).setTitle(mActivity
								.getString(R.string.appstock_text_install));
						else if ((content.getContentType() == Magazine.CONTENTTYPE) || (content.getContentType() == Video.CONTENTTYPE))
							popup.getMenu().findItem(R.id.buy).setTitle(mActivity
									.getString(R.string.appstock_text_download));
							
					}
				}
				
				
				try {
					if (mAccount.getWishlist().contains(
							content.getContentId() + "")) {
						popup.getMenu().removeItem(R.id.add_to_wishlist);
					} else {
						popup.getMenu().removeItem(
								R.id.remove_from_wishlist);
					}
				} catch (NullPointerException ex) {
					//popup.getMenu().removeItem(R.id.add_to_wishlist);
					popup.getMenu().removeItem(R.id.remove_from_wishlist);
				}

				popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch (item.getItemId()) {
						case R.id.install:
							onInstallListener.onClick(null);
							break;
						case R.id.buy:
							Log.d("JD", "paypal clicked");
							if (mAccount != null) {
							Intent intent = new Intent(mActivity, ContentDetailActivity.class);
							if(content instanceof App){
												Log.d(Nexxoo.TAG, "ich bin eine app");
							            		intent.putExtra(mActivity.getString(R.string.appstock_extra_content), (App) content);
							            	} else if(content instanceof Magazine){
							            		Log.d(Nexxoo.TAG, "ich bin ein magazin");
							            		intent.putExtra(mActivity.getString(R.string.appstock_extra_content), (Magazine) content);
							            	} else if(content instanceof Video) {
							            		Log.d(Nexxoo.TAG, "ich bin ein video");
							            		intent.putExtra(mActivity.getString(R.string.appstock_extra_content), (Video) content);
							            	} else {
							            		Log.d(Nexxoo.TAG, "ich bin ein content :(");
							            	}
							 intent.putExtra(mActivity.getString(R.string.appstock_extra_paypal), "paypal");
							 intent.putExtra(mActivity.getString(R.string.appstock_extra_paypal_close), "paypal");	
							mActivity.startActivity(intent);
							}
							else
							{
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
							//onBuyListener.onClick(null);
							break;
						case R.id.update:
							onUpdateListener.onClick(null);
							break;
						case R.id.add_to_wishlist:
							if(AccountLoggedIn.isSet()){
								NexxooWebservice.addToWishlist(true,
										mAccount.getAccountId(),
										mContent.getContentId(),
										mAccount.getSessionKey(),
										new OnJSONResponse() {

									@Override
									public void onReceivedJSONResponse(
											JSONObject json) {
										mAccount.addToWishlist(mContent);
										Dialog styleDialog = new StyleMessageDialog(mActivity,mActivity.getString(R.string.appstock_msg_favadded));
										styleDialog.show();
									}

									@Override
									public void onReceivedError(
											String msg, int code) {
									}
								});
							}else{
								Dialog styleDialog=null;
									styleDialog = new StyleMessageDialog(mActivity,String.format(mActivity.getString(
											R.string.appstock_text_pleaselogintoaddtowishlist), content.getName()));
								styleDialog.show();
							}
							break;
						case R.id.remove_from_wishlist:
							NexxooWebservice.removeFromWishlist(true,
									mAccount.getAccountId(),
									mContent.getContentId(),
									mAccount.getSessionKey(),
									new OnJSONResponse() {

										@Override
										public void onReceivedJSONResponse(
												JSONObject json) {
											mAccount.removeFromWishlist(mContent);
											
											Dialog styleDialog = new StyleMessageDialog(mActivity, mActivity.getString(R.string.appstock_msg_favremoved));
											styleDialog.show();
										}

										@Override
										public void onReceivedError(
												String msg, int code) {
										}
									});
							break;
						case R.id.uninstall:
							onDeleteListener.onClick(null);
							break;

						}
						return false;
					}
				});

				popup.show();

			}
		};
		contextMenuButton = (RelativeLayout) findViewById(R.id.btn_dots_menu);
		contextMenuButton.setOnClickListener(onContextMenuClick);
		contextMenuButton.getChildAt(0).setOnClickListener(onContextMenuClick);
	}
	
	public void setContent(final Content content) {
		init();
		mContent = content;

		itemName.setText(content.getName());
		devName.setText(content.getOwnerName());

		/*
		 * Set image for content type
		 */

		ImageLoader mImageLoader = ImageLoader.getInstance();
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.resetViewBeforeLoading()
		.build();
		
		ProgressBarImageLoadingListener pb = new ProgressBarImageLoadingListener(itemProgressBar);
		
		try {
			switch (content.getContentType()) {
			case App.CONTENTTYPE:

				mImageLoader.displayImage(
						content.getPicturesByType(
								Picture.PICTURETYPE_APP_ICON).get(0)
								.getmUrl(), itemIcon, options, pb);
				
				break;
			case Magazine.CONTENTTYPE:
				mImageLoader.displayImage(
						content.getPicturesByType(
								Picture.PICTURETYPE_MAGAZINE_COVER).get(0).getmUrl(),
								itemIcon, options, pb);
				break;
			case Video.CONTENTTYPE:
				mImageLoader.displayImage(
						content.getPicturesByType(
								Picture.PICTURETYPE_VIDEO_COVER).get(0)
								.getmUrl(), itemIcon,options, pb);
				break;
			}
		}
		catch (IndexOutOfBoundsException ex){
			
		}

		/*
		 * Likes string setup
		 */

		String text = mActivity.getString(R.string.appstock_text_xofxlike);
		text = text.replaceAll(Nexxoo.REPLACE_1,
				Integer.toString(content.getLikeCount()));
		text = text.replaceAll(
				Nexxoo.REPLACE_2,
				Integer.toString(content.getDislikeCount()
						+ content.getLikeCount()));
		likesText.setText(text);
		if (content.getLikeCount() > 0 && content.getDislikeCount() > 0) {
			double wert = content.getLikeCount()
					/ content.getDislikeCount();
			if (wert >= 2)
				likesIcon.setImageResource(R.drawable.ic_action_arrow_up); 
			else if (1.1 >= wert && wert < 2)
				likesIcon.setImageResource(R.drawable.ic_action_arrow_side_up);
			else if (0.9 >= wert && wert < 1.1)
				likesIcon.setImageResource(R.drawable.ic_action_arrow_side);
			else if (0.5 >= wert && wert < 0.9)
				likesIcon.setImageResource(R.drawable.ic_action_arrow_side_down);
			else
				likesIcon.setImageResource(R.drawable.ic_action_arrow_down);
		} else if (content.getLikeCount() > 0
				&& content.getDislikeCount() == 0) {
			likesIcon.setImageResource(R.drawable.ic_small_heart); // top rated
																// icon
		} else if (content.getLikeCount() == 0
				&& content.getDislikeCount() > 0) {
			likesIcon.setImageResource(R.drawable.ic_action_arrow_down); // bad rated
																// icon
		} else {
			likesText.setText(mActivity
					.getString(R.string.appstock_text_bethefirst));
			likesIcon.setImageResource(R.drawable.ic_action_arrow_side);
		}

		/*
		 * check if content is owned
		 */

		try {
			contextMenuButton.setVisibility(View.VISIBLE);
			mAccount = AccountLoggedIn.getInstance();
			if (mAccount.getOwnedContentIdList().contains(
					content.getContentId() + "")) {
				mIsOwnedContent = true;

				if (content.getContentType() == App.CONTENTTYPE) {
					/**************************************************************
					 * Check if app is installed and if it is on current
					 * version *
					 **************************************************************/
					App a = (App) content;
					// Log.d(Nexxoo.TAG, "versionCode: "+ a.getVersionCode()
					// +", package: "+ a.getPackageName()
					// +" for content name: "+ a.getName());
					mIsInstalled = FileStorageHelper.isInstalled(
							a.getPackageName(), mActivity);		 			
					if (mIsInstalled) {
						mIsNewestVersion = FileStorageHelper.isNewestVersion(
								a.getPackageName(), a.getVersionCode(),
								mActivity);
						if (mIsNewestVersion) {
							mInstall.setVisibility(View.INVISIBLE);
							contextMenuButton.setVisibility(View.INVISIBLE);
							mInstall.setBackgroundColor(getContext().getResources().getColor(R.color.Transparent));
						} else {
							mInstall.setVisibility(View.VISIBLE);
							contextMenuButton.setVisibility(View.INVISIBLE);
							mInstall.setImageResource(R.drawable.ic_action_update);
						}
					} else {
						mInstall.setVisibility(View.VISIBLE);
						contextMenuButton.setVisibility(View.INVISIBLE);
						mInstall.setImageResource(R.drawable.ic_action_download);
					}
				} else {
					/********************************************************
					 * Check if magazine/video is downloaded *
					 ********************************************************/
					mIsInstalled = FileStorageHelper.isDownloaded(content,
							mActivity);
					if (mIsInstalled) {
						mInstall.setVisibility(View.INVISIBLE);
						contextMenuButton.setVisibility(View.INVISIBLE);
						mInstall.setImageResource(R.drawable.ic_action_update);
						mIsNewestVersion = true;
					} else {
						mInstall.setVisibility(View.VISIBLE);
						contextMenuButton.setVisibility(View.INVISIBLE);
						mInstall.setImageResource(R.drawable.ic_action_download);
					}
				}
			} else {
				mIsOwnedContent = false;
				mInstall.setVisibility(View.INVISIBLE);
			}

		} catch (AppStockSingletonException e) {
		}

		/*
		 * Special deal price layout
		 */
		boolean freeVersion = false;
		if (content.isSpecialDeal() && !mIsOwnedContent) {
			
			itemDealFlag.setVisibility(View.VISIBLE);
			
			// mSpecialPrice.setText(mContext
			// .getString(R.string.appstock_text_onlynow)
			// + " "
			// + content.getPrice()
			// + mContext.getString(R.string.appstock_currency));
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(content.getSpecialDealEndDate());

			DateFormat df = DateFormat.getDateInstance();

			double ratio = content.getPrice() / content.getStandardPrice();
			int percent = (int) (ratio * 100.) + 1;

			String specialDeal = String.format("%d%% Nachlass bis %s",
					percent, df.format(cal.getTime()));
			itemDealPriceText.setText(specialDeal);
			itemDealPriceText.setVisibility(View.VISIBLE);

			itemDealPriceCut.setText(percent + "%");
			itemDealPriceCut.setVisibility(View.VISIBLE);

			itemPrice.setText(content.getPrice()
					+ mActivity.getString(R.string.appstock_currency));
			// mLine.setVisibility(View.VISIBLE);
			itemPrice.setBackgroundColor(mActivity.getResources().getColor(
					R.color.AppStockDrawerTrueBlue));
		} else {
			if (content.getPrice() > 0 && !mIsOwnedContent) {
				itemPrice.setVisibility(View.VISIBLE);
				itemPrice.setText(content.getPrice()
						+ mActivity.getString(R.string.appstock_currency));
			} else if (!mIsOwnedContent) {
				itemPrice.setVisibility(View.VISIBLE);
				itemPrice.setText(mActivity
						.getString(R.string.appstock_free));
				freeVersion = true;
			} else
				itemPrice.setVisibility(View.GONE);
		}

		setupPopupMenu(content,freeVersion);
	}

}
