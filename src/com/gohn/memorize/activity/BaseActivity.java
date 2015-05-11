package com.gohn.memorize.activity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
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
			BaseActivity.mTypeface = Typeface.createFromAsset(getAssets(), "yoon2.mp3");

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

	public void writeToFile(String path, String data) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(path, Context.MODE_PRIVATE));
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	public String readFromFile(String path) {

		String ret = "";

		try {
			InputStream inputStream = openFileInput(path);

			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ((receiveString = bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}

				inputStream.close();
				ret = stringBuilder.toString();
			}
		} catch (FileNotFoundException e) {
			Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
		}

		return ret;
	}

	public boolean isFileExist(String path) {
		try {
			InputStream inputStream = openFileInput(path);
		} catch (FileNotFoundException e) {
			Log.e("login activity", "File not found: " + e.toString());
			return false;
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
			return false;
		}
		return true;
	}
}
