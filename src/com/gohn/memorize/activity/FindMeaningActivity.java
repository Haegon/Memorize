package com.gohn.memorize.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.WordsDBMgr;
import com.gohn.memorize.model.WordSet;

public class FindMeaningActivity extends Activity {

	ArrayList<WordSet> wordsSet;
	TextView word;
	int page = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_meaning_activity_layout);

		wordsSet = WordsDBMgr.getInstance(this).getWordsSet(getIntent().getExtras().getString(WordsDBMgr.GROUP));

		word = (TextView) findViewById(R.id.find_meaning_word_text);
		word.setText(wordsSet.get(page).Word);
	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.find_meaning_prev_btn:
			page--;
			break;
		case R.id.find_meaning_check_btn:
			break;
		case R.id.find_meaning_next_btn:
			page++;
			break;
		}

		if (page < 0)
			page = 0;
		if (page >= wordsSet.size())
			page--;
		word.setText(wordsSet.get(page).Word);
	}
}
