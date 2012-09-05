package com.example.passrepo.store;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesCredentialStore implements CredentialStore {

	private static final String ACCESS_TOKEN = "access_token";
	private static final String REFRESH_TOKEN = "refresh_token";

	private SharedPreferences prefs;
	
	public SharedPreferencesCredentialStore(SharedPreferences prefs) {
		this.prefs = prefs;
	}
	
	@Override
	public String[] read() {
		String[] tokens = new String[2];
		tokens[0]=prefs.getString(ACCESS_TOKEN, null);
		tokens[1]=prefs.getString(REFRESH_TOKEN, null);
		return tokens;
	}

	@Override
	public void write(String[] tokens) {
		Editor editor = prefs.edit();
		editor.putString(ACCESS_TOKEN,tokens[0]);
		editor.putString(REFRESH_TOKEN,tokens[1]);
		editor.commit();
	}
	
	@Override
	public void clearCredentials() {
		Editor editor = prefs.edit();
		editor.remove(ACCESS_TOKEN);
		editor.remove(REFRESH_TOKEN);
		editor.commit();
	}
}
