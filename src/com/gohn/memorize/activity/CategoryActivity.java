package com.gohn.memorize.activity;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.WordsDBMgr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CategoryActivity extends Activity {

	String groupName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_layout);

		groupName = getIntent().getExtras().getString(WordsDBMgr.GROUP);
	}

	public void onClick(View v) {

		Intent intent = new Intent();
		intent.putExtra(WordsDBMgr.GROUP, groupName);
		
		switch (v.getId()) {
		case R.id.category_find_meaning_btn:
			intent.setClass(this, FindMeaningActivity.class);
			startActivityForResult(intent, 2);
			break;
		}
	}
}
