package com.technisat.appstock.content.categorylist;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.technisat.appstock.R;
import com.technisat.appstock.app.ImageLoaderSpinner;
import com.technisat.appstock.entity.App;
import com.technisat.appstock.entity.Content;
import com.technisat.appstock.entity.Magazine;
import com.technisat.appstock.entity.Picture;
import com.technisat.appstock.entity.Video;

public class FeaturedItemView extends RelativeLayout {
	
	private TextView mName, mTeaser, mPrice;
	private ImageView mFeaturedLogo;
	private ProgressBar mProgressBar = null;
	private Context mContext = null;
	private ImageView mIcon;
	private View mDealFlagContainer;
	private TextView mDealPercent;
	private View mDealPrevPriceContainer;
	private TextView mDealPrevPrice;
	private View mDealPriceBackground;
	
	public FeaturedItemView(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	public FeaturedItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	private void init(){
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View root = inflater.inflate(R.layout.content_featured_item, null);
		mName = (TextView) root.findViewById(R.id.tv_feat_name);
		mTeaser = (TextView) root.findViewById(R.id.tv_feat_teaser);
		mPrice = (TextView) root.findViewById(R.id.tv_feat_price);
		mFeaturedLogo = (ImageView) root.findViewById(R.id.i_feat_logo);
		mProgressBar = (ProgressBar) root.findViewById(R.id.pb_feat_spinner);
		mIcon = (ImageView) root.findViewById(R.id.i_feat_icon);
		
		mDealFlagContainer = root.findViewById(R.id.deal_flag_container);
		mDealPercent = (TextView) root.findViewById(R.id.tv_price_cut);
		
		mDealPrevPriceContainer = root.findViewById(R.id.previous_price_container);
		mDealPrevPrice = (TextView) root.findViewById(R.id.prev_price);
		
		mDealPriceBackground = root.findViewById(R.id.tv_feat_price_container);
		
		addView(root);
	}
	
	public void setContent(final Content content){
		ImageLoader imageLoader = ImageLoader.getInstance();
		if(content.getPicturesByType(Picture.PICTURETYPE_FEATURED).size() > 0){
			imageLoader.displayImage(content.getPicturesByType(Picture.PICTURETYPE_FEATURED).get(0).getmUrl(), 
					mFeaturedLogo,
					new ImageLoaderSpinner(mProgressBar));
		}
		
		try {
			ImageLoader mImageLoader = ImageLoader.getInstance();
			switch (content.getContentType()) {
			case App.CONTENTTYPE:
				mImageLoader.displayImage(content.getPicturesByType(Picture.PICTURETYPE_APP_ICON).get(0).getmUrl(), 
						mIcon,
						new ImageLoaderSpinner(mProgressBar));
				break;
			case Magazine.CONTENTTYPE:
				mImageLoader.displayImage(content.getPicturesByType(Picture.PICTURETYPE_MAGAZINE_COVER).get(0).getmUrl(), 
						mIcon,
						new ImageLoaderSpinner(mProgressBar));
				break;
			case Video.CONTENTTYPE:
				mImageLoader.displayImage(content.getPicturesByType(Picture.PICTURETYPE_VIDEO_COVER).get(0).getmUrl(), 
						mIcon,
						new ImageLoaderSpinner(mProgressBar));
				break;
			}
		}catch(IndexOutOfBoundsException ex){
			// don't crash when icon is missing
			ex.printStackTrace();
		}
		
		if(content.isSpecialDeal()){
			mDealFlagContainer.setVisibility(View.VISIBLE);
			mDealPrevPriceContainer.setVisibility(View.VISIBLE);
			
			double ratio = content.getPrice() / content.getStandardPrice();
			int percent = (int) (ratio * 100.) + 1;
			
			mDealPercent.setText(percent + "%");
			mDealPrevPrice.setText(content.getStandardPrice()+" €");
			
			mDealPriceBackground.setBackgroundColor(mContext.getResources().getColor(R.color.AppStockDrawerTrueBlue));
		} else {
			mDealFlagContainer.setVisibility(View.GONE);
			mDealPrevPriceContainer.setVisibility(View.GONE);
			mDealPriceBackground.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.price_gradient_background));
		}
		
		
		mName.setText(content.getName());
		mTeaser.setText(content.getTeaserText());
		if(content.getPrice() > 0)
			mPrice.setText(content.getPrice() + mContext.getString(R.string.appstock_currency));
		else
			mPrice.setText(mContext.getString(R.string.appstock_free));
	}
}