package com.gohn.memorize.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.WordsDBMgr;
import com.gohn.memorize.model.AnswerItem;
import com.gohn.memorize.model.Exercise;
import com.gohn.memorize.model.WordSet;

public class FindMeaningActivity extends Activity {

	ArrayList<Exercise> exercises = new ArrayList<Exercise>();

	ArrayList<WordSet> wordsSet;
	TextView word;
	RadioGroup radioGroup;
	ArrayList<RadioButton> radioBtns = new ArrayList<RadioButton>();
	Button nextBtn;

	WordsDBMgr dbMgr;

	boolean onNext = false;

	int page = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_meaning_activity_layout);

		dbMgr = WordsDBMgr.getInstance(this);
		Bundle b = getIntent().getExtras();
		wordsSet = dbMgr.getWordsSet(b.getString(WordsDBMgr.GROUP), b.getString(WordsDBMgr.TYPE));

		for (int i = 0; i < wordsSet.size(); i++) {
			Exercise e = new Exercise();
			e.Question = wordsSet.get(i);
			e.AnswerItems = makeAnswerItems(wordsSet.get(i));
			exercises.add(e);
		}

		radioGroup = (RadioGroup) findViewById(R.id.find_meaning_radio_group);

		radioBtns.add((RadioButton) findViewById(R.id.find_meaning_radio1));
		radioBtns.add((RadioButton) findViewById(R.id.find_meaning_radio2));
		radioBtns.add((RadioButton) findViewById(R.id.find_meaning_radio3));
		radioBtns.add((RadioButton) findViewById(R.id.find_meaning_radio4));
		radioBtns.add((RadioButton) findViewById(R.id.find_meaning_radio5));

		word = (TextView) findViewById(R.id.find_meaning_word_text);
		nextBtn = (Button) findViewById(R.id.find_meaning_next_btn);

		nextBtn.setEnabled(false);

		showPage(true);
	}

	public void showPage(boolean checkClear) {

		word.setText(exercises.get(page).Question.Word);

		for (int i = 0; i < 5; i++) {
			radioBtns.get(i).setText(exercises.get(page).AnswerItems.get(i).Answer);
			radioBtns.get(i).setTextColor(exercises.get(page).AnswerItems.get(i).Tint);
		}

		radioGroup.clearCheck();
	}

	public ArrayList<AnswerItem> makeAnswerItems(WordSet wordSet) {
		Cursor c = dbMgr.rawQuery("SELECT " + WordsDBMgr.MEANING + " from " + WordsDBMgr.TABLE_NAME + " where " + WordsDBMgr.TYPE + "=? order by random() limit 5", new String[] { wordSet.Type });

		ArrayList<AnswerItem> answerItems = new ArrayList<AnswerItem>();

		boolean have = false;
		if (c != null) {
			while (c.moveToNext()) {

				if (c.getString(0).equals(wordSet.Meaning)) {
					have = true;
				}
				answerItems.add(new AnswerItem(c.getString(0)));
			}
		}

		if (!have)
			answerItems.get((int) (Math.random() * 5)).Answer = wordSet.Meaning;

		return answerItems;
	}

	public boolean isUserCheck() {
		for (int i = 0; i < 5; i++) {
			if (radioBtns.get(i).isChecked())
				return true;
		}
		return false;
	}

	public void onClick(View v) {

		int tmpPage = page;

		switch (v.getId()) {
		case R.id.find_meaning_prev_btn:
			page--;
			if (page < 0)
				page = 0;
			showPage(true);
			nextBtn.setEnabled(true);
			break;
		case R.id.find_meaning_check_btn:

			if (!isUserCheck())
				return;

			int checkedIndex = -100;

			switch (radioGroup.getCheckedRadioButtonId()) {
			case R.id.find_meaning_radio1:
				checkedIndex = 0;
				break;
			case R.id.find_meaning_radio2:
				checkedIndex = 1;
				break;
			case R.id.find_meaning_radio3:
				checkedIndex = 2;
				break;
			case R.id.find_meaning_radio4:
				checkedIndex = 3;
				break;
			case R.id.find_meaning_radio5:
				checkedIndex = 4;
				break;
			default:
				checkedIndex = -1;
			}

			Log.d("gohn", "@@@@@@@@@@@@ " + checkedIndex);

			if (wordsSet.get(page).Meaning.equals(radioBtns.get(checkedIndex).getText())) {
				exercises.get(page).AnswerItems.get(checkedIndex).Tint = Color.BLUE;
			} else {
				exercises.get(page).AnswerItems.get(checkedIndex).Tint = Color.RED;
			}
			showPage(false);
			onNext = true;
			nextBtn.setEnabled(true);
			break;
		case R.id.find_meaning_next_btn:
			page++;
			if (page >= wordsSet.size())
				page--;
			showPage(true);
			onNext = false;
			nextBtn.setEnabled(false);
			break;
		}
	}
}
