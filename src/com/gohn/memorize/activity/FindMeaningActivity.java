package com.gohn.memorize.activity;

import java.util.ArrayList;

import android.app.Activity;
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
import com.gohn.memorize.manager.WordsDBMgr;
import com.gohn.memorize.model.AnswerItem;
import com.gohn.memorize.model.Exercise;
import com.gohn.memorize.model.WordSet;

public class FindMeaningActivity extends Activity {

	ArrayList<Exercise> exercises = new ArrayList<Exercise>();

	ArrayList<WordSet> wordsSet;
	TextView word;
	TextView count;
	RadioGroup radioGroup;
	ArrayList<RadioButton> radioBtns = new ArrayList<RadioButton>();
	Button nextBtn;
	Button checkBtn;

	WordsDBMgr dbMgr;

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
			exercises.add(makeExercise(wordsSet.get(i)));
		}

		radioGroup = (RadioGroup) findViewById(R.id.find_meaning_radio_group);

		radioBtns.add((RadioButton) findViewById(R.id.find_meaning_radio1));
		radioBtns.add((RadioButton) findViewById(R.id.find_meaning_radio2));
		radioBtns.add((RadioButton) findViewById(R.id.find_meaning_radio3));
		radioBtns.add((RadioButton) findViewById(R.id.find_meaning_radio4));
		radioBtns.add((RadioButton) findViewById(R.id.find_meaning_radio5));

		count = (TextView) findViewById(R.id.find_meaning_word_count);
		word = (TextView) findViewById(R.id.find_meaning_word_text);
		nextBtn = (Button) findViewById(R.id.find_meaning_next_btn);
		checkBtn = (Button) findViewById(R.id.find_meaning_check_btn);
		nextBtn.setEnabled(false);

		showPage();
	}

	public void showPage() {

		count.setText(correctedCount() + " / " + solvedCount() + " / " + exercises.size());
		word.setText(exercises.get(page).Question.Word);

		for (int i = 0; i < 5; i++) {
			radioBtns.get(i).setText(exercises.get(page).AnswerItems.get(i).Answer);
			radioBtns.get(i).setTextColor(exercises.get(page).AnswerItems.get(i).Tint);
		}

		radioGroup.clearCheck();

		if (exercises.get(page).Solve) {
			nextBtn.setEnabled(true);
			checkBtn.setEnabled(false);
		} else {
			nextBtn.setEnabled(false);
			checkBtn.setEnabled(true);
		}
	}

	public void showResult() {

		setContentView(R.layout.result_layout);

		TextView cntTv = (TextView) findViewById(R.id.result_count_text);
		cntTv.setText(exercises.size() + " 문제 중 " + correctedCount() + " 개 맞았습니다.");

		TextView scrTv = (TextView) findViewById(R.id.result_score_text);
		scrTv.setText(correctedCount() * 100 / exercises.size() + " 점");
	}

	public Exercise makeExercise(WordSet wordSet) {

		Exercise e = new Exercise();

		Cursor c = dbMgr.rawQuery("SELECT " + WordsDBMgr.MEANING + " from " + WordsDBMgr.TABLE_NAME + " where " + WordsDBMgr.TYPE + "=? order by random() limit 5", new String[] { wordSet.Type });

		ArrayList<AnswerItem> answerItems = new ArrayList<AnswerItem>();

		boolean have = false;
		if (c != null) {
			int a = 0;
			while (c.moveToNext()) {
				if (c.getString(0).equals(wordSet.Meaning)) {
					have = true;
					e.AnswerNo = a;
				}
				answerItems.add(new AnswerItem(c.getString(0)));
				a++;
			}
		}

		if (!have) {
			int r = (int) (Math.random() * 5);
			answerItems.get(r).Answer = wordSet.Meaning;
			e.AnswerNo = r;
		}

		e.Question = wordSet;
		e.AnswerItems = answerItems;

		return e;
	}

	public boolean isUserCheck() {
		for (int i = 0; i < 5; i++) {
			if (radioBtns.get(i).isChecked())
				return true;
		}
		return false;
	}

	public int solvedCount() {
		int c = 0;
		for (int i = 0; i < exercises.size(); i++) {
			if (exercises.get(i).Solve)
				c++;
		}
		return c;
	}

	public int correctedCount() {
		int c = 0;
		for (int i = 0; i < exercises.size(); i++) {
			if (exercises.get(i).Correct)
				c++;
		}
		return c;
	}

	public void onClick(View v) {

		int tmpPage = page;

		switch (v.getId()) {
		case R.id.find_meaning_prev_btn:
			page--;
			if (page < 0)
				page = 0;
			showPage();
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

			exercises.get(page).Solve = true;

			if (wordsSet.get(page).Meaning.equals(radioBtns.get(checkedIndex).getText())) {
				exercises.get(page).AnswerItems.get(checkedIndex).Tint = Color.BLUE;
				exercises.get(page).Correct = true;
			} else {
				exercises.get(page).AnswerItems.get(checkedIndex).Tint = Color.RED;
				exercises.get(page).AnswerItems.get(exercises.get(page).AnswerNo).Tint = Color.BLUE;
			}
			showPage();
			break;
		case R.id.find_meaning_next_btn:
			page++;
			if (page >= wordsSet.size()) {
				page--;
				showResult();
			} else
				showPage();
			break;
		case R.id.result_home_btn:
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setClass(this, MainActivity.class);
			startActivity(intent);
			break;

		}
	}
}
