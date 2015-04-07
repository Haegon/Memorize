package com.gohn.memorize.activity;

import java.util.Locale;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.Global;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.layout.preference_layout);

		CheckBoxPreference checkRandom = (CheckBoxPreference) findPreference("randomProblem");
		checkRandom.setOnPreferenceChangeListener(onPreferenceChangeListener);

		Preference pref = (Preference) findPreference("freeAdd");
		
		if (!Locale.KOREA.getCountry().equals(getResources().getConfiguration().locale.getCountry())) {
			PreferenceGroup pg = (PreferenceGroup)findPreference("pref_basic");
			pg.removePreference(pref);
		}
	}

	private void setOnPreferenceChange(Preference mPreference) {
		mPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);
		onPreferenceChangeListener.onPreferenceChange(mPreference, PreferenceManager.getDefaultSharedPreferences(mPreference.getContext()).getString(mPreference.getKey(), ""));
	}

	private Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {

			if (preference instanceof CheckBoxPreference) {
				if (preference.getKey().equals("randomProblem"))
					Global.getInstance(getApplicationContext()).RandomProblem = Boolean.valueOf(newValue.toString());
			} else if (preference instanceof SwitchPreference) {
				if (preference.getKey() == "autoUpdate ") {

				}
			}
			return true;
		}
	};
}