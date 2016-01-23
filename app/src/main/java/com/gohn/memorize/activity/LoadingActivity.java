package com.gohn.memorize.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.DBMgr;

@SuppressLint("NewApi")
public class LoadingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);

		DBMgr.init(this);

		Thread t = new Thread() {
			public void run() {
				try {
					sleep(1500);
					startActivity(new Intent(getApplicationContext(), MainActivity.class));
					finish();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();

	}
}
