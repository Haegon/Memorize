package com.gohn.memorize.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.Global;
import com.gohn.memorize.manager.WordsDBMgr;
import com.gohn.memorize.model.AnswerItem;
import com.gohn.memorize.model.Exercise;
import com.gohn.memorize.model.ExerciseType;
import com.gohn.memorize.model.WordSet;

public class StudyActivity extends Activity {

	ArrayList<WordSet> wordsSet;
	
	TextView word;
	TextView meaning;
	TextView count;

	WordsDBMgr dbMgr;

	int exerciseType;
	int page = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_activity_layout);

		dbMgr = WordsDBMgr.getInstance(this);
		Bundle b = getIntent().getExtras();
		exerciseType = b.getInt(ExerciseType.toStr());
		wordsSet = dbMgr.getWordsSet(b.getString(WordsDBMgr.GROUP), b.getString(WordsDBMgr.TYPE));
		
		if (Global.getInstance(getApplicationContext()).RandomProblem)
			makeWordRandomly();
		
		viewInit();
		showPage();
	}

	public void viewInit() {
		count = (TextView) findViewById(R.id.study_word_count);
		word = (TextView) findViewById(R.id.study_word_text);
		meaning = (TextView) findViewById(R.id.study_meaning_text);
	}

	public void showPage() {
		count.setText(page + " / " + wordsSet.size());
		word.setText(wordsSet.get(page).Word);
		meaning.setText(wordsSet.get(page).Meaning);
	}

	public void showResult() {
		setContentView(R.layout.study_finish_layout);
	}

	public void makeWordRandomly() {
		long seed = System.nanoTime();
		Collections.shuffle(wordsSet, new Random(seed));
	}

	public boolean isFinish() {
		return page == wordsSet.size();
	}

	public void goHome() {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.study_prev_btn:
			page--;
			if (page < 0)
				page = 0;
			showPage();
			break;
		case R.id.study_next_btn:
			page++;
			if (page >= wordsSet.size()) {
				page--;
				showResult();
			} else
				showPage();
			break;
		case R.id.study_restart_btn:
			page = 0;
			setContentView(R.layout.study_activity_layout);
			viewInit();
			showPage();
			break;
		case R.id.study_home_btn:
			goHome();
			break;
		}
	}
}