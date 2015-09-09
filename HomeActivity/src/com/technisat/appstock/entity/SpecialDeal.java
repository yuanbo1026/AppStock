package com.technisat.appstock.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.technisat.appstock.exception.AppStockContentError;

public class SpecialDeal implements Parcelable {
	
	private static final String STARTDATE = "startDate";
	private static final String ENDDATE = "endDate";
	private static final String SPECIALPRICE = "specialPrice";
	private static final String SPECIALDEALID = "specialDealId";
	
	private long mStartDate = -1;
	private long mEndDate = -1;
	private double mSpecialPrice = -1;
	private long mSpecialDealId = -1;
	
//	public SpecialDeal( long startDate, long endDate, double specialPrice, long specialDealId){ // not used at this moment
//		mStartDate = endDate;
//		mEndDate = endDate;
//		mSpecialPrice = specialPrice;
//		mSpecialDealId = specialDealId;
//	}
	
	public SpecialDeal(JSONObject jsonObj) throws AppStockContentError{
		try {
			mStartDate = jsonObj.getLong(STARTDATE)*1000;
			mEndDate = jsonObj.getLong(ENDDATE)*1000;
			mSpecialPrice = jsonObj.getDouble(SPECIALPRICE);
			mSpecialDealId = jsonObj.getLong(SPECIALDEALID);
		} catch (JSONException e) {
			throw new AppStockContentError(e.getMessage());
		}
	}

	public long getStartDate() {
		return mStartDate;
	}


	public long getEndDate() {
		return mEndDate;
	}


	public double getSpecialPrice() {
		return mSpecialPrice;
	}


	public long getSpecialDealId() {
		return mSpecialDealId;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeLongArray(new long[] {mStartDate, mEndDate, mSpecialDealId});
		dest.writeDouble(mSpecialPrice);
	}
	
	public SpecialDeal(Parcel in){
		
		long[] longData = new long[3];
		
		in.readLongArray(longData);
		mStartDate = longData[0];
		mEndDate = longData[1];
		mSpecialDealId = longData[2];
		
		mSpecialPrice = in.readDouble();
    }
	
	public static final Parcelable.Creator<SpecialDeal> CREATOR = new Parcelable.Creator<SpecialDeal>() {
        public SpecialDeal createFromParcel(Parcel in) {
            return new SpecialDeal(in); 
        }

        public SpecialDeal[] newArray(int size) {
            return new SpecialDeal[size];
        }
    };
}