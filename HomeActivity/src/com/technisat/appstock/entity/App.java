package com.technisat.appstock.entity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.technisat.appstock.exception.AppStockContentError;

public class App extends Content {
	
	/**
	 * Must match the DB ContentTypeId for App
	 */
	public static final int CONTENTTYPE = 1;
	public static final String VERSIONCODE = "versionCode";
	public static final String VERSION = "version";
	public static final String PACKAGE = "packageName";
	
	private int mVersionCode = -1;
	private String mVersion = null;
	private String mPackage = null;
	
//	public App(App app){ // not used at this moment
//		super(app);
//		mVersionCode = app.getVersionCode();
//		mVersion = app.getVersion();
//		mPackage = app.getPackageName();
//	}
	
	public App(JSONObject jsonObj) throws AppStockContentError{
		super(jsonObj);
		try {
			if (getContentType() != CONTENTTYPE){
				throw new AppStockContentError("Wrong content type");
			}
			mVersion = jsonObj.getString(VERSION);
			mVersionCode = jsonObj.getInt(VERSIONCODE);
			mPackage = jsonObj.getString(PACKAGE);
		} catch (JSONException e) {
			throw new AppStockContentError(e.getMessage());
		}
	}
	
	public int getVersionCode() {
		return mVersionCode;
	}

	public String getVersion() {
		return mVersion;
	}
	
	public String getPackageName() {
		return mPackage;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		super.writeToParcel(dest, flags);
		dest.writeStringArray(new String[] {mVersion, mPackage});
		dest.writeInt(mVersionCode);
	}
	
	public App(Parcel in){
		super(in);
		
		String[] stringData = new String[2];
		
		in.readStringArray(stringData);
		mVersion = stringData[0];
		mPackage = stringData[1];
		
		mVersionCode = in.readInt();
    }
	
	public static final Parcelable.Creator<App> CREATOR = new Parcelable.Creator<App>() {
        public App createFromParcel(Parcel in) {
            return new App(in); 
        }

        public App[] newArray(int size) {
            return new App[size];
        }
    };
}