package com.technisat.appstock.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.technisat.appstock.exception.AppStockContentError;

public class User implements Parcelable{
	
	public static final String STREET = "street";
	public static final String USERNAME = "username";
	public static final String FIRSTNAME = "firstName";
	public static final String LASTNAME = "lastName";
	public static final String HOUSENUMBER = "houseNumber";
	public static final String DATEOFBIRTH = "dateOfBirth";
	public static final String EMAIL = "email";
	public static final String STATE = "state";
	public static final String ZIPCODE = "zipCode";
	
	private String mStreet = null;
	private String mUserName = null;
	private String mFirstName = null;
	private String mLastName = null;
	private String mHouseNumber = null;
	private long mDateOfBirth = -1;
	private String mEmail = null;
	private String mState = null;
	private String mZipCode = null;
	
	public User(JSONObject jsonObj) throws AppStockContentError{
		if(jsonObj != null){
			try {
				//must haves				
				mUserName = jsonObj.getString(USERNAME);
				mEmail = jsonObj.getString(EMAIL);
				
				if (jsonObj.has(STREET))
					mStreet = jsonObj.getString(STREET);
				if (jsonObj.has(FIRSTNAME))
					mFirstName = jsonObj.getString(FIRSTNAME);
				if (jsonObj.has(LASTNAME))
					mLastName = jsonObj.getString(LASTNAME);
				if (jsonObj.has(HOUSENUMBER))
					mHouseNumber = jsonObj.getString(HOUSENUMBER);
				if (jsonObj.has(DATEOFBIRTH))
					mDateOfBirth = jsonObj.getLong(DATEOFBIRTH);
				
				if (jsonObj.has(STATE))
					mState = jsonObj.getString(STATE);
				if (jsonObj.has(ZIPCODE))
					mZipCode = jsonObj.getString(ZIPCODE);
			} catch (JSONException e) {
				throw new AppStockContentError(e.getMessage());
			}
		}else{
			throw new AppStockContentError("JSON must not be null!");
		}
	}

	public String getStreet() {
		return mStreet;
	}

	public String getUserName() {
		return mUserName;
	}

	public String getFirstName() {
		return mFirstName;
	}

	public String getLastName() {
		return mLastName;
	}

	public String getHouseNumber() {
		return mHouseNumber;
	}

	public long getDateOfBirth() {
		return mDateOfBirth;
	}

	public String getEmail() {
		return mEmail;
	}

	public String getState() {
		return mState;
	}

	public String getZipCode() {
		return mZipCode;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] {mStreet, mUserName, mFirstName, mLastName, mHouseNumber, mEmail, mState, mZipCode});
		dest.writeLong(mDateOfBirth);
	}
	
	public User(Parcel in){
        String[] data = new String[8];

        in.readStringArray(data);
        this.mStreet = data[0];
        this.mUserName = data[1];
        this.mFirstName = data[2];
        this.mLastName = data[3];
        this.mHouseNumber = data[4];
        this.mEmail = data[5];
        this.mState = data[6];
        this.mZipCode = data[7];
        
        mDateOfBirth = in.readLong();
    }
	
	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in); 
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}