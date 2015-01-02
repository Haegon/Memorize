package com.gohn.memorize.activity;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.WordsDBMgr;
import com.gohn.memorize.model.ExerciseType;
import com.gohn.memorize.model.WordType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TypeSelectActivity extends Activity {

	String groupName = "";
	int exerciseType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.type_select_activity_layout);

		groupName = getIntent().getExtras().getString(WordsDBMgr.GROUP);
		exerciseType = getIntent().getExtras().getInt("ExerciseType");
	}

	public void onClick(View v) {

		Intent intent = new Intent();
		intent.putExtra(WordsDBMgr.GROUP, groupName);

		switch (v.getId()) {
		case R.id.type_select_all_btn:
			intent.putExtra(WordsDBMgr.TYPE, WordType.NONE);
			break;
		case R.id.type_select_noun_btn:
			intent.putExtra(WordsDBMgr.TYPE, WordType.NOUN);
			break;
		case R.id.type_select_verb_btn:
			intent.putExtra(WordsDBMgr.TYPE, WordType.VERB);
			break;
		case R.id.type_select_adjective_btn:
			intent.putExtra(WordsDBMgr.TYPE, WordType.ADJECTIVE);
			break;
		case R.id.type_select_adverb_btn:
			intent.putExtra(WordsDBMgr.TYPE, WordType.ADVERB);
			break;
		case R.id.type_select_etc_btn:
			intent.putExtra(WordsDBMgr.TYPE, WordType.ETC);
			break;
		}

		switch (exerciseType) {
		case ExerciseType.GUESS_MEANING :
			intent.setClass(this, FindMeaningActivity.class);
			break;
		case ExerciseType.GUESS_WORD :
			intent.setClass(this, FindWordActivity.class);
			break;
		case ExerciseType.WRITE_WORD :
			intent.setClass(this, WriteWordActivity.class);
			break;
		}
		
		startActivityForResult(intent, 3);
	}
}
