package com.technisat.appstock.entity;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.technisat.appstock.exception.AppStockContentError;

public class Magazine extends Content{
	
	/**
	 * Must match the DB ContentTypeId for Magazine
	 */
	public static final int CONTENTTYPE = 2;
	private int mIssue;
	private int mYear;
	
	public int getmIssue() {
		return mIssue;
	}

	public void setmIssue(int mIssue) {
		this.mIssue = mIssue;
	}

	public int getmYear() {
		return mYear;
	}

	public void setmYear(int mYear) {
		this.mYear = mYear;
	}

	public Magazine(JSONObject jsonObj) throws AppStockContentError {
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
	
	public Magazine(Parcel in){
		super(in);
    }
	
	public static final Parcelable.Creator<Magazine> CREATOR = new Parcelable.Creator<Magazine>() {
        public Magazine createFromParcel(Parcel in) {
            return new Magazine(in); 
        }

        public Magazine[] newArray(int size) {
            return new Magazine[size];
        }
    };
}