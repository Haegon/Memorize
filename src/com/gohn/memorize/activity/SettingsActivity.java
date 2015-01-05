package com.gohn.memorize.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.gohn.memorize.R;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.layout.preference_layout);
	}
}