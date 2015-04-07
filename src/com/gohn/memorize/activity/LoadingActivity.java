package com.gohn.memorize.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.WordsDBMgr;

@SuppressLint("NewApi")
public class LoadingActivity extends BaseActivity {

	public WordsDBMgr dbMgr = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_layout);

		ImageView imgView = (ImageView) findViewById(R.id.loading_image);
		imgView.setScaleType(ScaleType.FIT_XY);
		dbMgr = WordsDBMgr.getInstance(this);

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
