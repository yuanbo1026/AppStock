package com.technisat.appstock.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.technisat.appstock.exception.AppStockContentError;

public class Category implements Parcelable {
	
	public static final String CATEGORYID = "id";
	public static final String CONTENTTYPEID = "contentTypeId";
	public static final String CATEGORYNAME = "name";
	public static final String DESCRIPTION = "description";
	
	private long mCategoryId = -1;
	private long mContentTypeId = -1;
	private String mCategoryName = null;
	private String mDescription = null;
	
	public Category(JSONObject jsonObj) throws AppStockContentError{
		if(jsonObj != null){
			try {
				mCategoryId = jsonObj.getLong(CATEGORYID);
				mContentTypeId = jsonObj.getLong(CONTENTTYPEID);
				mCategoryName = jsonObj.getString(CATEGORYNAME);
				mDescription = jsonObj.getString(DESCRIPTION);
			} catch (JSONException e) {
				throw new AppStockContentError(e.getMessage());
			}
		}else{
			throw new AppStockContentError("JSON must not be null!");
		}
	}
	
	public long getCategoryId(){
		return mCategoryId;
	}
	
	public long getContentTypeId(){
		return mContentTypeId;
	}
	
	public String getCategoryName(){
		return mCategoryName;
	}
	
	public String getDescription(){
		return mDescription;
	}
	
	
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLongArray(new long[] {mContentTypeId, mCategoryId});
		dest.writeStringArray(new String[] {mCategoryName, mDescription});
	}
	
	public Category(Parcel in){
		
		long[] longData = new long[2];
		String[] stringData = new String[2];
		
		in.readLongArray(longData);
		mContentTypeId = longData[0];
		mCategoryId = longData[1];
		
		in.readStringArray(stringData);
		mCategoryName = stringData[0];
		mDescription = stringData[1];
    }
	
	public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in); 
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}