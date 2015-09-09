package com.technisat.appstock.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.technisat.appstock.exception.AppStockContentError;

public class Account implements Parcelable {
	
	/**
	 * Must match the DB RoleId Field for Nutzer
	 */
	public static final int ROLE_NUTZER = 1;
	/**
	 * Must match the DB RoleId Field for Developer
	 */
	public static final int ROLE_DEVELOPER = 2;
	/**
	 * Must match the DB RoleId Field for Kaufmann
	 */
	public static final int ROLE_KAUFMANN = 3;
	/**
	 * Must match the DB RoleId Field for Moderator
	 */
	public static final int ROLE_MODERATOR = 4;
	/**
	 * Must match the DB RoleId Field for Administrator
	 */
	public static final int ROLE_ADMINISTRATOR = 5;
	
	public static final String ACCOUNTID = "accountId";
	public static final String USER = "user";
	public static final String ISACTIVE = "isActive";
	public static final String ISVISABLE = "isVisible";
	public static final String REGISTRATIONDATE = "registrationDate";
	public static final String ROLEID = "roleId";
	public static final String OWNEDCONTENT = "ownedContent";
	public static final String SESSIONKEY = "sessionKey";
	public static final String WISHLIST = "wishlist";
	
	private long mAccountId = -1;
	private User mUser = null;
	private boolean isDeveloper = false;
	private long mRegistrationDate;
	private int mRoleId = -1;
	private List<String> mOwnedContentIdList = new ArrayList<String>();
	private String mSessionKey = null;
	private List<String> mWishlist = new ArrayList<String>();
	
	public Account(JSONObject jsonObj) throws AppStockContentError{
		if(jsonObj != null){
			try {
				mAccountId = jsonObj.getLong(ACCOUNTID);
				JSONObject jsonObjUser = jsonObj.getJSONObject(USER);
				if(jsonObjUser != null){
					mUser = new User(jsonObjUser);
				}

				mRegistrationDate = jsonObj.getLong(REGISTRATIONDATE);
				mRoleId = jsonObj.getInt(ROLEID);
				
				mSessionKey = jsonObj.getString(SESSIONKEY);
				
				if(jsonObj.optJSONObject(OWNEDCONTENT) != null){
					JSONObject ownedContentJsonObj = jsonObj.getJSONObject(OWNEDCONTENT);
					int count = ownedContentJsonObj.getInt("count");
					for(int i = 0; i < count; i++){
						mOwnedContentIdList.add(ownedContentJsonObj.getString("contentId"+i));
					}
				}
				
				if(jsonObj.optJSONObject(WISHLIST) != null){
					JSONObject wishlistJsonObj = jsonObj.getJSONObject(WISHLIST);
					int count = wishlistJsonObj.getInt("count");
					for(int i = 0; i < count; i++){
						mWishlist.add(wishlistJsonObj.getString("contentId"+i));
					}
				}
				
			} catch (JSONException e) {
				throw new AppStockContentError(e.getMessage());
			}
		}else{
			throw new AppStockContentError("JSON must not be null!");
		}
	}
	
	public long getAccountId(){
		return mAccountId;
	}

	
	public User getUser(){
		return mUser;
	}

	public long getRegistrationDate(){
		return mRegistrationDate;
	}

	public int getRoleId(){
		return mRoleId;
	}
	
	public void setRoleId(int id){
		mRoleId = id;
	}
	
	public boolean isDeveloper(){
		return isDeveloper;
	}
	
	public List<String> getOwnedContentIdList(){
		return mOwnedContentIdList;
	}
	
	public void addToOwnedContentList(Content content){
		mOwnedContentIdList.add(content.getContentId()+"");
	}
	
	public String getSessionKey(){
		return mSessionKey;
	}
	
	public List<String> getWishlist(){
		return mWishlist;
	}
	
	public void addToWishlist(Content content){
		mWishlist.add(content.getContentId()+"");
	}
	
	public void removeFromWishlist(Content content){
		mWishlist.remove(content.getContentId()+"");
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeLongArray(new long[] {mAccountId, mRegistrationDate});
		dest.writeParcelable(mUser, 0);
		dest.writeBooleanArray(new boolean[] {isDeveloper});
		dest.writeInt(mRoleId);
		dest.writeList(mOwnedContentIdList);
		dest.writeList(mWishlist);
		dest.writeString(mSessionKey);
	}
	
	public Account(Parcel in){
		long[] longData = new long[2];
		in.readLongArray(longData);
		mAccountId = longData[0];
		mRegistrationDate = longData[1];
		
		this.mUser = in.readParcelable(User.class.getClassLoader());
		
		boolean[] boolData = new boolean[3];
		in.readBooleanArray(boolData);
		isDeveloper = boolData[2];
		
		mRoleId = in.readInt();
		
		in.readList(mOwnedContentIdList, List.class.getClassLoader());
		in.readList(mWishlist, List.class.getClassLoader());
		mSessionKey = in.readString();
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