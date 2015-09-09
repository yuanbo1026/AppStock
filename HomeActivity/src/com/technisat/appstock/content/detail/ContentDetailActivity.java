package com.technisat.appstock.content.detail;

import java.util.Calendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ocpsoft.pretty.time.PrettyTime;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.technisat.appstock.R;
import com.technisat.appstock.app.ActionBarHelper;
import com.technisat.appstock.app.ImageLoaderSpinner;
import com.technisat.appstock.entity.Account;
import com.technisat.appstock.entity.App;
import com.technisat.appstock.entity.Content;
import com.technisat.appstock.entity.Magazine;
import com.technisat.appstock.entity.Picture;
import com.technisat.appstock.entity.Video;
import com.technisat.appstock.entity.singleton.AccountLoggedIn;
import com.technisat.appstock.exception.AppStockSingletonException;
import com.technisat.appstock.helper.StyleMessageDialog;
import com.technisat.appstock.search.NexxooSearch;
import com.technisat.appstock.webservice.NexxooWebservice;
import com.technisat.appstock.webservice.OnJSONResponse;
import com.technisat.constants.Nexxoo;
import com.technisat.constants.NexxooPayment;
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
public class ContentDetailActivity extends Activity {

	boolean mIsOwnedContent = false;
	private Content mContent = null;
	private Account mAccount = null;
	private int mCommentCount = -1;
	private MenuItem mMenuItemWishlist = null;
	private LinearLayout ll = null;
	private Intent output = null;

	ProgressBar mProgressBar = null;
	/*
	 * crated to handle internal reloads
	 * instead of doing full reload (that would result in duplicated reload)
	 */
	public boolean internalReload = false;
	
	private AppUninstallReceiver mReceiver = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content_detail);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		mProgressBar = (ProgressBar) findViewById(R.id.pb_feat_spinner2); 
		
		TypedValue tv = new TypedValue();
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
		    int height = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
			
			ImageView icon = (ImageView) findViewById(android.R.id.home);
			FrameLayout.LayoutParams iconLp = (FrameLayout.LayoutParams) icon.getLayoutParams();
			iconLp.topMargin = iconLp.bottomMargin = (int) (height * 0.08f);
			iconLp.height = LayoutParams.MATCH_PARENT;
			iconLp.width = LayoutParams.MATCH_PARENT;
			icon.setLayoutParams( iconLp );
		}
		
		output = new Intent();

		// Intent intent = getIntent();
		// if (intent != null)
		// onNewIntent(intent);
	}

	@Override
	public void onBackPressed() {
		finishActivity(false);
		
		super.onBackPressed();
	}
	
	private void finishActivity(boolean close) {
		// TODO Auto-generated method stub
		setResult(RESULT_OK, output);
		
		if(close)
			finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		internalReload = false;
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		internalReload = false;
		if (mReceiver != null) {
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mContent != null && mContent instanceof App) {
			mReceiver = new AppUninstallReceiver(this, (App) mContent);
			IntentFilter filters = new IntentFilter();
			filters.addDataScheme("package");
			for (String s : AppUninstallReceiver.INTENT_FILTERS) {
				filters.addAction(s);
			}
			registerReceiver(mReceiver, filters);
		}

		/**
		 * refresh view added by b.yuan
		 */
		Intent intent = getIntent();
		if (intent != null && !internalReload)
			onNewIntent(intent);
		else
			internalReload = false;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		/**
		 * refresh view after installed or deleted App added by b.yuan
		 */
		setIntent(intent);

		mContent = intent
				.getParcelableExtra(getString(R.string.appstock_extra_content));
		

		ActionBarHelper.setActionBarTitle(this, getActionBar(),
				mContent.getName());

		ContentItemView container = (ContentItemView) findViewById(R.id.header_container);
		container.setContent(mContent, true, this);
		
		/**
		 * TODO: as a process removes notification
		 */
		NexxooWebservice.reattachDownloadProgress(this, mContent);

		TextView tvDescription = (TextView) findViewById(R.id.tv_detail_description_text);
		tvDescription.setText(mContent.getDescriptionText());

		/**************************************
		 * set size info text for Content *
		 **************************************/
		TextView tvSize = (TextView) findViewById(R.id.tv_detail_size);
		tvSize.setText(mContent.getSize() + " "
				+ getString(R.string.appstock_text_kb));

		/****************************************************
		 * set pretty time format for last updated *
		 ****************************************************/
		TextView tvLastUpdated = (TextView) findViewById(R.id.tv_detail_lastupdated);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(mContent.getLastUpdated());

		PrettyTime ptime = new PrettyTime();
		tvLastUpdated.setText(getString(R.string.appstock_text_lastupdate)
				+ " " + ptime.format(calendar.getTime()));

		/******************************************
		 * Check if content is Owned by User *
		 ******************************************/
		try {
			mAccount = AccountLoggedIn.getInstance();
			if (mContent != null
					&& mAccount.getOwnedContentIdList().contains(
							mContent.getContentId() + "")) {
				mIsOwnedContent = true;
			}
		} catch (AppStockSingletonException e) {
			Log.e(Nexxoo.TAG, e.getMessage());
		}

		long accountId = -1; // user is not logged in
		if (mAccount != null)
			accountId = mAccount.getAccountId();

		NexxooWebservice.getComments(true, accountId, mContent.getContentId(),
				0, -1, commentResponse);

		/***************************************
		 * Set Screenshots by contentType *
		 ***************************************/
		LinearLayout ll_screenshotList = (LinearLayout) findViewById(R.id.ll_home_magazine_list);
		Resources r = getResources();
		int height = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 355, r.getDisplayMetrics());
		int width = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 200, r.getDisplayMetrics());

		LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(width,
				height); // set
							// size
		ll_screenshotList.removeAllViews();
		para.setMargins(8, 0, 0, 0);
		List<Picture> screenshotList = null;
		ImageLoader mImageLoader = ImageLoader.getInstance();
		switch (mContent.getContentType()) {
		case App.CONTENTTYPE:
			screenshotList = mContent
					.getPicturesByType(Picture.PICTURETYPE_SCREENSHOT_APP);

			for (int i = 0; i < screenshotList.size(); i++) {
				ImageView imageView = new ImageView(getApplicationContext());
				imageView.setLayoutParams(para);
				//mImageLoader.displayImage(screenshotList.get(i).getmUrl(),
				//		imageView);
				mImageLoader.displayImage(screenshotList.get(i).getmUrl(),
						imageView,
						new ImageLoaderSpinner(mProgressBar));
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ContentDetailActivity.this,
								ScreenshotActivity.class);
						intent.putExtra(
								getString(R.string.appstock_extra_content),
								mContent);
						startActivity(intent);
					}
				});
				ll_screenshotList.addView(imageView);
			}
			break;
		case Magazine.CONTENTTYPE:
			screenshotList = mContent
					.getPicturesByType(Picture.PICTURETYPE_SCREENSHOT_MAGAZINE);
			for (int i = 0; i < screenshotList.size(); i++) {
				ImageView imageView = new ImageView(getApplicationContext());
				imageView.setLayoutParams(para);
				//mImageLoader.displayImage(screenshotList.get(i).getmUrl(),
				//		imageView);
				mImageLoader.displayImage(screenshotList.get(i).getmUrl(),
						imageView,
						new ImageLoaderSpinner(mProgressBar));
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ContentDetailActivity.this,
								ScreenshotActivity.class);
						intent.putExtra(
								getString(R.string.appstock_extra_content),
								mContent);
						startActivity(intent);
					}
				});
				ll_screenshotList.addView(imageView);
			}
			break;
		case Video.CONTENTTYPE:
			width = this.getResources().getDisplayMetrics().widthPixels - (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics());;
			para = new LinearLayout.LayoutParams(width,
					width/2);
			screenshotList = mContent
					.getPicturesByType(Picture.PICTURETYPE_SCREENSHOT_VIDEO);
			for (int i = 0; i < screenshotList.size(); i++) {
				ImageView imageView = new ImageView(getApplicationContext());
				imageView.setLayoutParams(para);
				/*mImageLoader.displayImage(screenshotList.get(i).getmUrl(),
						imageView);*/
				mImageLoader.displayImage(screenshotList.get(i).getmUrl(),
						imageView,
						new ImageLoaderSpinner(mProgressBar));
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ContentDetailActivity.this,
								ScreenshotActivity.class);
						intent.putExtra(
								getString(R.string.appstock_extra_content),
								mContent);
						startActivity(intent);
					}
				});
				ll_screenshotList.addView(imageView);
			}
			break;
		}
		if (screenshotList.size() <1){
		com.technisat.appstock.app.CustomTextView customTextView = (com.technisat.appstock.app.CustomTextView) findViewById(R.id.tv_detail_header_screenshot);
		customTextView.setVisibility(View.GONE);
		}
		
		// start paypal session
		if(intent.getExtras().getString(getString(R.string.appstock_extra_paypal)) != null) {
			intent.removeExtra(getString(R.string.appstock_extra_paypal));
			container.buyAction(mContent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.detail_menu, menu);
		// Associate searchable configuration with the SearchView

		NexxooSearch.initSearchBar(this, menu);

		mMenuItemWishlist = menu.findItem(R.id.action_add_to_wishlist);
		if (mAccount != null) {
			if (mAccount.getWishlist().contains(mContent.getContentId() + "")) {
				mMenuItemWishlist.setIcon(R.drawable.ic_cancel);
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
		switch (item.getItemId()) {
		case android.R.id.home:
			finishActivity(true);
			return true;
		case R.id.action_add_to_wishlist:
			if (mAccount != null) {
				if (mAccount.getWishlist().contains(
						mContent.getContentId() + "")) {
					NexxooWebservice.removeFromWishlist(true,
							mAccount.getAccountId(), mContent.getContentId(),
							mAccount.getSessionKey(), new OnJSONResponse() {

								@Override
								public void onReceivedJSONResponse(
										JSONObject json) {
									mAccount.removeFromWishlist(mContent);
									mMenuItemWishlist
											.setIcon(R.drawable.ic_action_wishlist);
									Dialog styleDialog = new StyleMessageDialog(ContentDetailActivity.this,
											getString(R.string.appstock_msg_favremoved));
									styleDialog.show();
								}

								@Override
								public void onReceivedError(String msg, int code) {
								}
							});
				} else {
					NexxooWebservice.addToWishlist(true,
							mAccount.getAccountId(), mContent.getContentId(),
							mAccount.getSessionKey(), new OnJSONResponse() {

								@Override
								public void onReceivedJSONResponse(
										JSONObject json) {
									mAccount.addToWishlist(mContent);
									mMenuItemWishlist
											.setIcon(R.drawable.ic_cancel);
									Dialog styleDialog = new StyleMessageDialog(ContentDetailActivity.this,
											getString(R.string.appstock_msg_favadded));
									styleDialog.show();
								}

								@Override
								public void onReceivedError(String msg, int code) {
								}
							});
				}
			} else {
				Dialog styleDialog = new StyleMessageDialog(this,
						getString(R.string.appstock_msg_pleaselogintowish));
				styleDialog.show();
			}
			return true;
		default:
			return true;
		}
	}

	public void setDownloadProgress(int progress) {
		ContentItemView container = (ContentItemView) findViewById(R.id.header_container);
		container.setProgress(progress);
	}

	public void hideProgress() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.i("REFRESH", "Hide progress and reload");
				ContentItemView container = (ContentItemView) findViewById(R.id.header_container);
				container.hideProgress();
				reloadActivityWithContent(mContent);
			}
		});

	}

	private void setLikeDislikeSection(boolean setVisible) {
		ll = (LinearLayout) findViewById(R.id.ll_detail_like_dislike);
		/**
		 * here set true for test by b.yuan
		 */

		if (setVisible) {
			ll.setVisibility(View.VISIBLE);
			ViewGroup tvLike = (ViewGroup) findViewById(R.id.tv_detail_like);
			tvLike.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					writeCommentAndRate(NexxooWebservice.CONTENT_LIKE_YES);
				}
			});
			ViewGroup tvDislike = (ViewGroup) findViewById(R.id.tv_detail_dislike);
			tvDislike.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					writeCommentAndRate(NexxooWebservice.CONTENT_LIKE_NO);
				}
			});
		} else {
			ll.setVisibility(View.GONE);
		}
	}

	private void writeCommentAndRate(final int liked) {

		CommentDialog dialog = new CommentDialog(this,
				new CommentDialog.CommentActionListener() {
					@Override
					public void onSend(final CommentDialog dialog,
							String comment) {
						NexxooWebservice.reviewContent(true, liked, comment,
								mContent.getContentId(),
								mAccount.getAccountId(),
								mAccount.getSessionKey(), new OnJSONResponse() {

									@Override
									public void onReceivedJSONResponse(
											JSONObject json) {
										Toast.makeText(
												getApplicationContext(),
												getString(R.string.appstock_msg_likedislikesuccessful),
												Toast.LENGTH_SHORT).show();
										dialog.dismiss();

										if (liked == NexxooWebservice.CONTENT_LIKE_YES) {
											mContent.applyLike();
										} else {
											mContent.applyDislike();
										}

										ContentItemView container = (ContentItemView) findViewById(R.id.header_container);
										container.setContent(mContent, true,
												ContentDetailActivity.this);
										
										Log.d("JD", "reloadActivityWithContent start");

										reloadActivityWithContent(mContent);
									}

									@Override
									public void onReceivedError(String msg,
											int code) {
										dialog.dismiss();
									}
								});

					}

					@Override
					public void onSend(final CommentDialog dialog) {
						NexxooWebservice.reviewContent(true, liked, null,
								mContent.getContentId(),
								mAccount.getAccountId(),
								mAccount.getSessionKey(), new OnJSONResponse() {

									@Override
									public void onReceivedJSONResponse(
											JSONObject json) {
										Toast.makeText(
												getApplicationContext(),
												getString(R.string.appstock_msg_likedislikesuccessful),
												Toast.LENGTH_SHORT).show();
										dialog.dismiss();

										if (liked == NexxooWebservice.CONTENT_LIKE_YES) {
											mContent.applyLike();
										} else {
											mContent.applyDislike();
										}

										ContentItemView container = (ContentItemView) findViewById(R.id.header_container);
										container.setContent(mContent, true,
												ContentDetailActivity.this);

										reloadActivityWithContent(mContent);
									}

									@Override
									public void onReceivedError(String msg,
											int code) {
										dialog.dismiss();
									}
								});
					}
				});
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("JD", "paypal finished");
		
		if (requestCode == NexxooPayment.PAYPAL_REQUEST_CODE_PAYMENT) {
			if (resultCode == Activity.RESULT_OK) {
				Log.e("PAYMENT", "RESULT OK");
				PaymentConfirmation confirm = data
						.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
				if (confirm != null) {
					Log.e("PAYMENT", "Confirm != null");
					try {
						Log.i(Nexxoo.PAYPAL, confirm.toJSONObject().toString(4));
						Log.i(Nexxoo.PAYPAL, confirm.getPayment()
								.toJSONObject().toString(4));

						final ProgressDialog ringProgressDialog = ProgressDialog
								.show(ContentDetailActivity.this,
										getString(R.string.appstock_pleasewait),
										getString(R.string.appstock_text_verifyingpayment),
										true);
						ringProgressDialog.setCancelable(true);
						
						Log.e("PAYMENT", "Requesting nexxoo buy intent");
						NexxooWebservice.buyContent(true, mContent
								.getContentId(), mAccount.getAccountId(),
								mAccount.getSessionKey(), confirm
										.toJSONObject().toString(),
								new OnJSONResponse() {

									@Override
									public void onReceivedJSONResponse(
											JSONObject json) {
										Log.e("PAYMENT", "Received nexxoo response");
										/****************************************************
										 * restart activity with updated
										 * ownedContentList * ( to display right
										 * buttons ) *
										 ****************************************************/
										mAccount.addToOwnedContentList(mContent);
										reloadActivityWithContent(mContent);

										Toast.makeText(
												getApplicationContext(),
												getString(
														R.string.appstock_text_hasbeenbought)
														.replaceAll(
																Nexxoo.REPLACE_1,
																mContent.getName()),
												Toast.LENGTH_SHORT).show();
										ringProgressDialog.dismiss();
									}

									@Override
									public void onReceivedError(String msg,
											int code) {
										Log.e("PAYMENT", "Nexxoo buy failed msg:"+msg);
										ringProgressDialog.dismiss();
									}
								});

					} catch (JSONException e) {
						Log.e(Nexxoo.PAYPAL,
								"an extremely unlikely failure occurred: ", e);
					}
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Log.i(Nexxoo.PAYPAL, "The user canceled.");
				/*
				 * intent was created from grid view
				 * close it after callback
				 */
				if(getIntent().getExtras().getString(getString(R.string.appstock_extra_paypal_close)) != null) {
					finishActivity(true);
				}
			} else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
				Log.i(Nexxoo.PAYPAL,
						"An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
			}
		} else if (requestCode == NexxooPayment.PAYPAL_REQUEST_CODE_FUTURE_PAYMENT) {
			if (resultCode == Activity.RESULT_OK) {
				PayPalAuthorization auth = data
						.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
				if (auth != null) {
					try {
						Log.i("FuturePaymentExample", auth.toJSONObject()
								.toString(4));

						String authorization_code = auth.getAuthorizationCode();
						Log.i("FuturePaymentExample", authorization_code);

					} catch (JSONException e) {
						Log.e("FuturePaymentExample",
								"an extremely unlikely failure occurred: ", e);
					}
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Log.i("FuturePaymentExample", "The user canceled.");
			} else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
				Log.i("FuturePaymentExample",
						"Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
			}
		}

	}

	/** handling callback for NexxooWebservice requests **/

	private OnJSONResponse commentResponse = new OnJSONResponse() {

		@Override
		public void onReceivedJSONResponse(final JSONObject json) {
			try {
				/*******************************************************
				 * Checks if the like/dislike party of detail screen * should be
				 * displayed and sets buttons if visible *
				 *******************************************************/
				setLikeDislikeSection((json.has("hasLiked")
						&& !json.getBoolean("hasLiked")) && mIsOwnedContent);// &&

				Log.e("Likes", "hasLiked= " + json.has("hasLiked")
						+ " isOwned= " + mIsOwnedContent);

				Log.e("Comments", json.toString());
				// !json
				// .getBoolean("hasLiked")));
				
				mCommentCount = json.getInt("count");
				
				Log.d("JD", "count= "+mCommentCount);
				
				LinearLayout ll = (LinearLayout) findViewById(R.id.comment_container);
				ll.setOrientation(LinearLayout.VERTICAL);

				Log.d("JD", "elements="+ll.getChildCount());
				
				    	FragmentManager fm = ContentDetailActivity.this.getFragmentManager();
				        ll.removeAllViews();
				        ll.removeAllViewsInLayout();
				        
				        Log.d("JD", "elements="+ll.getChildCount());
				        
				        FragmentTransaction transaction = fm.beginTransaction();
				        
							for (int i = mCommentCount-1; i >= 0; i--) {
								if (json.has("comment" + i)) {
									JSONObject jsonObj = json.getJSONObject("comment" + i);
									CommentFragment comment = new CommentFragment();
									comment.setText(ContentDetailActivity.this, jsonObj.getString("name"),
											jsonObj.getString("comment"),
											jsonObj.getLong("date"));

									transaction.add(ll.getId(), comment);
								}
							}
					
						
						transaction.commit();
				 

			} catch (JSONException e) {
				Log.e(Nexxoo.TAG, "Error loading comments: " + e.getMessage());
			} catch (IllegalStateException e){
				Log.e(Nexxoo.TAG, "Tried to add comment to dead activity.");
			}

			TextView tvComment = (TextView) findViewById(R.id.tv_detail_header_Comments);

			if (mCommentCount < 1) {
				tvComment.setVisibility(View.GONE);
			} else {
				tvComment.setVisibility(View.VISIBLE);
			}

			// mCommentPb.setVisibility(View.INVISIBLE);
		}

		@Override
		public void onReceivedError(String msg, int code) {
			// mCommentPb.setVisibility(View.INVISIBLE);
			Log.e(Nexxoo.TAG2, "ERROR: " + msg + "\n Code: " + code);
		}
	};

	public void reloadActivityWithContent(Content content) {
		output.putExtra(getString(R.string.appstock_extra_reload), true);
		output.putExtra(getString(R.string.appstock_extra_reload_object), mContent);

		Intent intent = new Intent(this, ContentDetailActivity.class);
		intent.putExtra(getString(R.string.appstock_extra_content), content);
		
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		internalReload = true;
		startActivity(intent);
	}
}