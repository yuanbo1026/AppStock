package com.technisat.appstock.entity;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.technisat.appstock.exception.AppStockContentError;

public class Video extends Content{
	
	/**
	 * Must match the DB ContentTypeId for Video
	 */
	public static final int CONTENTTYPE = 3;
	
	public Video(JSONObject jsonObj) throws AppStockContentError {
		super(jsonObj);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
	}
	
	public Video(Parcel in){
		super(in);
    }
	
	public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        public Video createFromParcel(Parcel in) {
            return new Video(in); 
        }

        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}