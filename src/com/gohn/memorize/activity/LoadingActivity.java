package com.gohn.memorize.activity;

import java.io.IOException;
import java.util.ArrayList;

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
import com.gohn.memorize.model.WordSet;
import com.gohn.memorize.util.ReadCSV;
import com.gohn.memorize.util.ReadXlsx;

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
					if (!dbMgr.getGroupNames().contains("����")) {
						TextView tv = (TextView) findViewById(R.id.loading_text);
						tv.setVisibility(tv.VISIBLE);

						ArrayList<WordSet> words = new ArrayList<WordSet>();
						words = ReadXlsx.readExcel(getAssets().open("toeic.xlsx"));
						dbMgr.addWordsToDB("����", words);
						finish();
					} else {
						sleep(1500);
					}
					startActivity(new Intent(getApplicationContext(), MainActivity.class));
					finish();
				} catch (InterruptedException e) {
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		t.start();

	}
}
