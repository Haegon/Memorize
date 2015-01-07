package com.gohn.memorize.activity;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.Global;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.layout.preference_layout);

		CheckBoxPreference checkRandom = (CheckBoxPreference) findPreference("randomProblem");
		checkRandom.setOnPreferenceChangeListener(onPreferenceChangeListener);
	}

	private void setOnPreferenceChange(Preference mPreference) {
		mPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);

		onPreferenceChangeListener.onPreferenceChange(mPreference, PreferenceManager.getDefaultSharedPreferences(mPreference.getContext()).getString(mPreference.getKey(), ""));
	}

	private Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {

			if (preference instanceof CheckBoxPreference) {

				if (preference.getKey().equals("randomProblem")) {
					Global.getInstance(getApplicationContext()).RandomProblem = Boolean.valueOf(newValue.toString());
				} else if (preference.getKey().equals("useUpdateNotify")) {

				}
			} else if (preference instanceof SwitchPreference) {

				if (preference.getKey() == "autoUpdate ") {

				}
			}

			return true;
		}

	};

}