package com.technisat.appstock.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.technisat.appstock.exception.AppStockContentError;
import com.technisat.constants.FileStorageHelper;

public class Content implements Parcelable {
	
	public static final String CONTENTTYPE = "contentTypeId";
	private static final String CATEGORYID = "categoryId";
//	private static final String DOWNLOADLINK = "downloadLink";
	private static final String PRICE = "price";
	private static final String OWNERID = "ownerId";
	private static final String OWNERNAME = "ownerName";
	private static final String DESCRIPTIONTEXT = "description";
	private static final String TEASERTEXT = "teaserText";
//	private static final String STATUSID = "status";
	private static final String NAME = "name";
	private static final String PACKAGENAME = "packageName";
	private static final String CONTENTID = "contentId";
	private static final String SPECIALDEAL = "specialDeal";
	private static final String INITIALDATE = "initialDate";
	private static final String LASTUPDATED = "lastUpdated";
	private static final String PICTURES = "pictures";
	private static final String COUNT = "count";
	private static final String LIKECOUNT = "likeCount";
	private static final String DISLIKECOUNT = "dislikeCount";
	private static final String SIZE = "size";
	private static final String ISFEATURED ="isFeatured";
	private static final String FILENAME ="filename";
	private static final String MIMETYPE = "mime";
	
	private long mCategoryId = -1;
	private String mDownloadLink = null;
	private double mPrice = -1;
	private long mOwnerId = -1;
	private String mOwnerName = null;
	private String mPackageName = null;
	private String mDescriptionText = null;
	private String mTeaserText = null;
	private int mContentTypeId = -1;
	private long mSpecialDealId = -1;
	private int mStatusId = -1;
	private String mName = null;
	private long mContentId = -1;
	private SpecialDeal sDeal = null;
	private long mInitialDate = -1;
	private long mLasUpdated = -1;
	private int mLikeCount = -1;
	private int mDislikeCount = -1;
	private long mSize = -1;
	private boolean mIsFeatured = false;
	private String mFilename = null;
	private String mMimeType = null;
	
	private List<Picture> mPictureList = new ArrayList<Picture>();
	
//	public Content(Content content){ // not used at this moment
//		mCategoryId = content.getCategoryId();
//		
//		mPrice = content.getStandardPrice();
//		mOwnerId = content.getOwnerId();
//		mOwnerName = content.getOwnerName();
//		mDescriptionText = content.getDescriptionText();
//		mTeaserText = content.getTeaserText();
//		mContentTypeId = content.getContentType();
//		mSpecialDealId = content.getSpecialDealId();
//		mName = content.getName();
//		mContentId = content.getContentId();
//		if(content.getSpecialDealId() != -1)
//			sDeal = new SpecialDeal(content.getSpecialDealStartDate(), content.getSpecialDealEndDate(), content.getPrice(), content.getSpecialDealId());
//		mInitialDate = content.getInitialDate();
//		mLasUpdated = content.getLastUpdated();
//		mLikeCount = content.getLikeCount();
//		mDislikeCount = content.getDislikeCount();
//		mSize = content.getSize();
//		mPictureList = content.getPictureList();
//	}
	
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if(o instanceof Content) {
			Content c = (Content) o;
			return mContentId == c.mContentId;
		}
		return false;
	}
	
	public Content(JSONObject jsonObj) throws AppStockContentError{
		if(jsonObj != null){
			try {
				mContentTypeId = jsonObj.getInt(CONTENTTYPE);
				mCategoryId = jsonObj.getLong(CATEGORYID);
				mPrice = jsonObj.getDouble(PRICE);
				mOwnerId = jsonObj.getLong(OWNERID);
				mOwnerName = jsonObj.getString(OWNERNAME);
				if(jsonObj.has(PACKAGENAME)) {
					mPackageName = jsonObj.getString(PACKAGENAME);
				}
				mDescriptionText = jsonObj.getString(DESCRIPTIONTEXT);
				mTeaserText = jsonObj.getString(TEASERTEXT);
				mMimeType = jsonObj.getString(MIMETYPE);
				
				if(jsonObj.optJSONObject(SPECIALDEAL) != null){
					JSONObject jsonObjSpecialDeal = jsonObj.getJSONObject(SPECIALDEAL);
					sDeal = new SpecialDeal(jsonObjSpecialDeal);
				}
				mName = jsonObj.getString(NAME);
				mContentId = jsonObj.getLong(CONTENTID);
				
				JSONObject jsonObjPics = jsonObj.getJSONObject(PICTURES);
				if(jsonObjPics != null){
					JSONObject jsonObjPic = null;
					int count = jsonObjPics.getInt(COUNT);
					for(int i = 0; i< count; i++){
						if(jsonObjPics.has(("picture"+Integer.toString(i)))){
							jsonObjPic = jsonObjPics.getJSONObject("picture"+Integer.toString(i));
							Picture pic = new Picture(jsonObjPic);
							mPictureList.add(pic);
						}
					}
				}
				mInitialDate = jsonObj.getLong(INITIALDATE)*1000;
				mLasUpdated = jsonObj.getLong(LASTUPDATED)*1000;
				
				mLikeCount = jsonObj.getInt(LIKECOUNT);
				mDislikeCount = jsonObj.getInt(DISLIKECOUNT);
				mSize = jsonObj.getLong(SIZE);
				mIsFeatured = jsonObj.optBoolean(ISFEATURED, false);
				mFilename = jsonObj.getString(FILENAME);
				
			} catch (JSONException e) {
				throw new AppStockContentError(e.getMessage());
			}
		}else{
			throw new AppStockContentError("JSON must not be null!");
		}
	}
	
	public boolean isSpecialDeal(){
		return sDeal != null;
	}
	
	public long getSpecialDealStartDate(){
		if(isSpecialDeal()){
			return sDeal.getStartDate();
		}
		return -1;
	}
	
	public long getSpecialDealEndDate(){
		if(isSpecialDeal()){
			return sDeal.getEndDate();
		}
		return -1;
	}
	
	public int getContentType(){
		return mContentTypeId;
	}
	
	@Deprecated
	public String getDownloadLink() {
		return mDownloadLink;
	}

	public long getCategoryId() {
		return mCategoryId;
	}

	public long getSpecialDealId() {
		return mSpecialDealId;
	}

	public double getPrice() {
		if(isSpecialDeal()){
			return sDeal.getSpecialPrice();
		}
		return mPrice;
	}
	
	public double getStandardPrice(){
		return mPrice;
	}

	public String getDescriptionText() {
		return mDescriptionText;
	}

	public String getTeaserText() {
		return mTeaserText;
	}

	public long getOwnerId() {
		return mOwnerId;
	}
	
	public String getOwnerName(){
		return mOwnerName;
	}
	
	@Deprecated
	public int getStatusId() {
		return mStatusId;
	}

	public String getName() {
		return mName;
	}

	public long getContentId() {
		return mContentId;
	}
	
	public long getInitialDate(){
		return mInitialDate;
	}
	
	public long getLastUpdated(){
		return mLasUpdated;
	}
	
	public int getLikeCount(){
		return mLikeCount;
	}
	
	public int getDislikeCount(){
		return mDislikeCount;
	}
	
	public void applyLike(){
		mLikeCount += 1;
	}
	
	public void applyDislike(){
		mDislikeCount += 1;
	}
	
	public long getSize(){
		return mSize;
	}
	
	public boolean isFeatured(){
		return mIsFeatured;
	}
	
	public String getFilename(){
		return mFilename;
	}
	
	public String getMimeType(){
		return mMimeType;
	}
	
	public void setMimeType(String mime){
		mMimeType = mime;
	}
	
	/**
	 * Returns a List<Picture> with Pictures matching the given typeId
	 * @param type the pictureTypId that the result will filter for
	 * @return Returns a List<Picture> with Pictures matching the typeId
	 */
	public List<Picture> getPicturesByType(int type){
		List<Picture> pictureList = new ArrayList<Picture>();
		for(int i = 0; i < mPictureList.size(); i++){
			if(mPictureList.get(i).getmPictureTypeId() == type)
				pictureList.add( mPictureList.get(i) );
		}
		return pictureList;
	}
	
	public Picture getIcon(){
		for(Picture pic : mPictureList){
			if(pic.getmPictureTypeId() == Picture.PICTURETYPE_APP_ICON||
			   pic.getmPictureTypeId() == Picture.PICTURETYPE_MAGAZINE_COVER||
			   pic.getmPictureTypeId() == Picture.PICTURETYPE_VIDEO_COVER)
				return pic;
		}
		return null;
	}
	
	public String getPackageName() {
		return mPackageName;
	}
	
	public List<Picture> getPictureList(){
		return mPictureList;
	}
	
	public String getLocalFileName(Context ctx){
		return FileStorageHelper.getDownloadFolder(ctx) + getContentType() + File.separator + getFilename();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeLongArray(new long[] {mCategoryId, mOwnerId, mSpecialDealId, mContentId, mInitialDate, mLasUpdated, mSize});
		dest.writeStringArray(new String[] {mDownloadLink, mOwnerName, mDescriptionText, mTeaserText, mName, mFilename, mMimeType});
		dest.writeDouble(mPrice);
		dest.writeIntArray(new int[] {mContentTypeId, mStatusId, mLikeCount, mDislikeCount});
		dest.writeParcelable(sDeal, 0);
		dest.writeList(mPictureList);
	}
	
	public Content(Parcel in){
		
		long[] longData = new long[7];
		String[] stringData = new String[7];
		int[] intData = new int[4];
		
		in.readLongArray(longData);
		mCategoryId = longData[0];
		mOwnerId = longData[1];
		mSpecialDealId = longData[2];
		mContentId = longData[3];
		mInitialDate = longData[4];
		mLasUpdated = longData[5];
		mSize = longData[6];
		
		in.readStringArray(stringData);
		mDownloadLink = stringData[0];
		mOwnerName = stringData[1];
		mDescriptionText = stringData[2];
		mTeaserText = stringData[3];
		mName = stringData[4];
		mFilename = stringData[5];
		mMimeType = stringData[6];
		
		mPrice = in.readDouble();
		
		in.readIntArray(intData);
		mContentTypeId = intData[0];
		mStatusId = intData[1];
		mLikeCount = intData[2];
		mDislikeCount = intData[3];
		
		sDeal = in.readParcelable(SpecialDeal.class.getClassLoader());
		
		in.readList(mPictureList, Picture.class.getClassLoader());
    }
	
	public static final Parcelable.Creator<Content> CREATOR = new Parcelable.Creator<Content>() {
        public Content createFromParcel(Parcel in) {
            return new Content(in); 
        }

        public Content[] newArray(int size) {
            return new Content[size];
        }
    };
}