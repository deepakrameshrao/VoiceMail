package com.myvoice;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.provider.ContactsContract.Settings;
import android.util.Log;

public class MyVoiceMail extends PreferenceActivity {

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.pref);

		getPreferenceManager().setSharedPreferencesName("myvoicemail");
	}

	@Override
	protected void onStart() {
		super.onStart();
		getPrefs();
	}

	private void getPrefs() {
		// we need to show the user's existing prefs, this isn't done
		// automatically by the activity
		SharedPreferences myprefs = getSharedPreferences("myvoicemail", 0);
		((CheckBoxPreference) findPreference("voicemail")).setChecked(myprefs
				.getBoolean("voicemail", false));
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}