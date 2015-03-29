package com.gohn.memorize.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.Global;
import com.gohn.memorize.manager.WordsDBMgr;
import com.gohn.memorize.model.ExerciseType;
import com.gohn.memorize.model.WordSet;

public class StudyActivity extends BaseActivity {

	ArrayList<WordSet> wordsSet;

	TextView word;
	TextView meaning;
	TextView count;

	Button nextBtn;

	WordsDBMgr dbMgr;

	int exerciseType;
	int page = 0;
	boolean isBlind;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_activity_layout);

		dbMgr = WordsDBMgr.getInstance(this);
		Bundle b = getIntent().getExtras();
		exerciseType = b.getInt(ExerciseType.toStr());
		wordsSet = dbMgr.getWordsSet(b.getString(WordsDBMgr.GROUP), b.getString(WordsDBMgr.TYPE));

		isBlind = b.getString("mode").equals("blind") ? true : false;

		Log.d("gohn", "" + isBlind);

		if (Global.getInstance(getApplicationContext()).RandomProblem)
			makeWordRandomly();

		viewInit();
		showPage();
	}

	public void viewInit() {
		if (isBlind) {
			for (int i = 0; i < wordsSet.size(); i++) {
				wordsSet.get(i).IsOpen = false;
			}
		}

		nextBtn = (Button) findViewById(R.id.study_next_btn);
		count = (TextView) findViewById(R.id.study_word_count);
		word = (TextView) findViewById(R.id.study_word_text);
		meaning = (TextView) findViewById(R.id.study_meaning_text);
	}

	public void showPage() {
		if (wordsSet.get(page).IsOpen) {
			meaning.setText(wordsSet.get(page).Meaning);
			nextBtn.setText(R.string.next);
		} else {
			meaning.setText("");
			nextBtn.setText(R.string.check);
		}

		count.setText((page + 1) + " / " + wordsSet.size());
		word.setText(wordsSet.get(page).Word);
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
			// 정답확인을 안한 경우는 확인 버튼역할을 하고 확인한경우는 다음문제
			if (wordsSet.get(page).IsOpen) {
				page++;
				if (page >= wordsSet.size()) {
					page--;
					showResult();
				} else
					showPage();
			} else {
				wordsSet.get(page).IsOpen = true;
				showPage();
			}
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