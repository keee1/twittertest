package com.example.twitdemokt;

import android.content.Context;
import android.content.SharedPreferences;

public class AccountPreference {

	private final static AccountPreference sInstance = new AccountPreference();
	
	private final static String PREFERENCE_FILE_NAME = "AccountPreferences";
	private final static String USER_ID = "UserId";
	private final static String SCREEN_NAME = "ScreenName";
	private final static String ACCESS_TOKEN = "AccessToken";
	private final static String ACCESS_TOKEN_SECRET = "AccessTokenSecret";
	
	private SharedPreferences mPreferences = null;
	private SharedPreferences.Editor mEditor = null;
	
	private AccountPreference() {}
	
	public static synchronized AccountPreference getInstance() {
		return sInstance;
	}
	
	public synchronized void initialize(Context context) {
		mPreferences = context.getSharedPreferences(PREFERENCE_FILE_NAME, 0);
		mEditor = mPreferences.edit();
	}
	
	public synchronized String getUserId() {
		return (mPreferences == null) ? null : mPreferences.getString(USER_ID, null);
	}
	
	public synchronized void setUserId(String id) {
		if(mEditor != null && id != null) {
			mEditor.putString(USER_ID, id);
			mEditor.apply();
		}
	}
	
	public synchronized String getScreenName() {
		return (mPreferences == null) ? null : mPreferences.getString(SCREEN_NAME, null);
	}
	
	public synchronized void setScreenName(String name) {
		if(mEditor != null && name != null) {
			mEditor.putString(SCREEN_NAME, name);
			mEditor.apply();
		}
	}
	
	public synchronized String getAccessToken() {
		return (mPreferences == null) ? null : mPreferences.getString(ACCESS_TOKEN, null);
	}
	
	public synchronized void setAccessToken(String token) {
		if(mEditor != null && token != null) {
			mEditor.putString(ACCESS_TOKEN, token);
			mEditor.apply();
		}
	}
	
	public synchronized String getAccessTokenSecret() {
		return (mPreferences == null) ? null : mPreferences.getString(ACCESS_TOKEN_SECRET, null);
	}
	
	public synchronized void setAccessTokenSecret(String secretToken) {
		if(mEditor != null && secretToken != null) {
			mEditor.putString(ACCESS_TOKEN_SECRET, secretToken);
			mEditor.apply();
		}
	}
}
