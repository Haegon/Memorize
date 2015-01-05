package com.gohn.memorize.manager;

import android.content.Context;

public class Global {

	public boolean RandomProblem = false;

	Context mContext = null;
	private static Global mGlobal = null;

	public static Global getInstance(Context context) {
		if (mGlobal == null) {
			mGlobal = new Global(context);
		}
		return mGlobal;
	}

	private Global(Context context) {
		mContext = context;
	}

}
