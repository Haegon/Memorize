package com.gohn.memorize.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Global {

	public boolean RandomProblem = false;

	Context mContext = null;
	private static Global mGlobal = null;

	public static Global getInstance(Context context) {
		if (mGlobal == null) {
			mGlobal = new Global(context);
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			// then you use
			mGlobal.RandomProblem = prefs.getBoolean("pref_random", true);
		}
		return mGlobal;
	}

	private Global(Context context) {
		mContext = context;
	}

}
