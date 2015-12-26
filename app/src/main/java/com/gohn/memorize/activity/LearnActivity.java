package com.gohn.memorize.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.DBMgr;

import org.json.JSONObject;

public class LearnActivity extends BaseActivity {

	protected DBMgr dbMgr;
	protected JSONObject json;
	protected String fileName;

	protected String groupName;
	protected String wordType;
	protected int exerciseType;
	protected Vibrator vibe;
	protected boolean end = false;

	protected Activity learnActivity;

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		dbMgr = DBMgr.getInstance();

		json = new JSONObject();
		vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		learnActivity = this;

//		AdBuddiz.cacheAds(learnActivity);
//		AdBuddiz.setPublisherKey("2397c3d7-a8b7-4935-9869-794fc416499c");
	}

	protected void goHome() {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
//						AdBuddiz.showAd(learnActivity);
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
