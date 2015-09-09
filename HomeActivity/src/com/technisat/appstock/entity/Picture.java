package com.technisat.appstock.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.technisat.appstock.exception.AppStockContentError;

public class Picture implements Parcelable {
	
	public static final int PICTURETYPE_APP_ICON = 1;
	public static final int PICTURETYPE_MAGAZINE_COVER = 2;
	public static final int PICTURETYPE_VIDEO_COVER = 3;
	public static final int PICTURETYPE_SCREENSHOT_APP = 4;
	public static final int PICTURETYPE_SCREENSHOT_MAGAZINE = 5;
	public static final int PICTURETYPE_SCREENSHOT_VIDEO = 6;
	public static final int PICTURETYPE_FEATURED = 7;
	
	public static final String PICTURETYPEID = "pictureTypeId";
//	public static final String PICTUREID = "pictureId"; // ist erst mal raus
	public static final String URL = "url";
	
	private int mPictureTypeId = -1;
//	private long mPictureId = -1;
	private String mUrl = null;
	
	public Picture(JSONObject jsonObj) throws AppStockContentError {
		if(jsonObj != null){
			try {
				mPictureTypeId = jsonObj.getInt(PICTURETYPEID);
//				mPictureId = jsonObj.getLong(PICTUREID); // ist erst mal raus
				mUrl = jsonObj.getString(URL);
//				Log.d(Nexxoo.TAG, "url: "+ mUrl);
			} catch (JSONException e) {
				throw new AppStockContentError(e.getMessage());
			}
		}else{
			throw new AppStockContentError("JSON must not be null!");
		}
	}

	public int getmPictureTypeId() {
		return mPictureTypeId;
	}

	public String getmUrl() {
		return mUrl;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeString(mUrl);
		dest.writeInt(mPictureTypeId);
	}
	
	public Picture(Parcel in){
		
		mUrl = in.readString();
		mPictureTypeId = in.readInt();
    }
	
	public static final Parcelable.Creator<Picture> CREATOR = new Parcelable.Creator<Picture>() {
        public Picture createFromParcel(Parcel in) {
            return new Picture(in); 
        }

        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };
}