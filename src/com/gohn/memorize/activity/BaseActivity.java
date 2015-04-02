package com.gohn.memorize.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gohn.memorize.manager.WordsDBMgr;

public class BaseActivity extends Activity {

	public WordsDBMgr dbMgr = null;
	private static Typeface mTypeface;

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		dbMgr = WordsDBMgr.getInstance(this);
		
		if (BaseActivity.mTypeface == null)
			BaseActivity.mTypeface = Typeface.createFromAsset(getAssets(), "bd_font.mp3");

		ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
		setGlobalFont(root);
	}

	void setGlobalFont(ViewGroup root) {
		for (int i = 0; i < root.getChildCount(); i++) {
			View child = root.getChildAt(i);
			if (child instanceof TextView)
				((TextView) child).setTypeface(mTypeface);
			else if (child instanceof ViewGroup)
				setGlobalFont((ViewGroup) child);
		}
	}
}
