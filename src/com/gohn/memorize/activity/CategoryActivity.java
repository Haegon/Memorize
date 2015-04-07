package com.gohn.memorize.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.WordsDBMgr;
import com.gohn.memorize.model.ExerciseType;

public class CategoryActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_activity_layout);
	}

	public void onClick(View v) {

		Intent intent = new Intent();
		intent.putExtra(WordsDBMgr.GROUP, getIntent().getExtras().getString(WordsDBMgr.GROUP));
		intent.putExtra(ExerciseType.toStr(), v.getId());
		intent.setClass(this, TypeSelectActivity.class);
		startActivityForResult(intent, 2);
	}
}
