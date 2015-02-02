package com.gohn.memorize.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.WordsDBMgr;

@SuppressLint("NewApi")
public class LoadingActivity extends Activity {

	public WordsDBMgr dbMgr = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_layout);

		Thread t = new Thread() {
			public void run() {
				try {
					sleep(1500);
					startActivity(new Intent(getApplicationContext(), MainActivity.class));
					finish();
				} catch (InterruptedException e) {
				}
			}
		};
		t.start();
		dbMgr = WordsDBMgr.getInstance(this);
	}
}
