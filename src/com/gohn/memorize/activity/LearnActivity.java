package com.gohn.memorize.activity;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;

import com.gohn.memorize.R;

public class LearnActivity extends BaseActivity {
	
	public JSONObject json;
	public String fileName;
	
	public String groupName;
	public String wordType;
	public int exerciseType;
	public Vibrator vibe;
	public boolean end = false;
	
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		
		json = new JSONObject();
		vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	public void goHome() {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
	}
	
	@Override
	public void onBackPressed() {

		if (end)
			return;

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					finish();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(LearnActivity.this);
		builder.setMessage(R.string.stop_study).setPositiveButton(R.string.yes, dialogClickListener).setNegativeButton(R.string.no, dialogClickListener).show();
	}
}
