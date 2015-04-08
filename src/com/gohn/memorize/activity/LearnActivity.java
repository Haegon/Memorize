package com.gohn.memorize.activity;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;

import com.gohn.memorize.R;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;

public class LearnActivity extends BaseActivity {

	public JSONObject json;
	public String fileName;

	public String groupName;
	public String wordType;
	public int exerciseType;
	public Vibrator vibe;
	public boolean end = false;

	public Activity learnActivity;

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		json = new JSONObject();
		vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		learnActivity = this;

		AdBuddiz.setPublisherKey("TEST_PUBLISHER_KEY");
		AdBuddiz.cacheAds(learnActivity);
		AdBuddiz.setPublisherKey("2397c3d7-a8b7-4935-9869-794fc416499c");
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
					AdBuddiz.showAd(learnActivity);
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
