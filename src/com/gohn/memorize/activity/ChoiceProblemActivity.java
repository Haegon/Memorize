package com.gohn.memorize.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.Global;
import com.gohn.memorize.manager.WordsDBMgr;
import com.gohn.memorize.model.AnswerItem;
import com.gohn.memorize.model.Exercise;
import com.gohn.memorize.model.ExerciseType;
import com.gohn.memorize.model.WordSet;

public class ChoiceProblemActivity extends BaseActivity {

	ArrayList<Exercise> exercises;

	ArrayList<WordSet> wordsSet;

	TextView word;
	TextView count;
	Button nextBtn;
	Button checkBtn;
	ArrayList<Button> answerBtns;
	ImageView countDown;
	ArrayList<Integer> countdownImage = new ArrayList<Integer>();;

	WordsDBMgr dbMgr;
	Handler handler = new Handler();

	int exerciseType;
	int page = 0;
	int answer = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choice_problem_activity_layout);

		dbMgr = WordsDBMgr.getInstance(this);
		Bundle b = getIntent().getExtras();
		exerciseType = b.getInt(ExerciseType.toStr());
		wordsSet = dbMgr.getWordsSet(b.getString(WordsDBMgr.GROUP), b.getString(WordsDBMgr.TYPE));

		exerciseInit();
		viewInit();
		showPage();
	}

	public void exerciseInit() {
		exercises = new ArrayList<Exercise>();

		for (int i = 0; i < wordsSet.size(); i++) {
			switch (exerciseType) {
			case R.id.category_find_meaning_btn:
				exercises.add(makeGuessMeaningExercise(wordsSet.get(i)));
				break;
			case R.id.category_find_word_btn:
				exercises.add(makeGuessWordExercise(wordsSet.get(i)));
				break;
			default:
				finish();
				break;
			}
		}

		if (Global.getInstance(getApplicationContext()).RandomProblem)
			makeExercisesRandomly();
	}

	public void viewInit() {

		answerBtns = new ArrayList<Button>();
		answerBtns.add((Button) findViewById(R.id.choice_problem_a1_btn));
		answerBtns.add((Button) findViewById(R.id.choice_problem_a2_btn));
		answerBtns.add((Button) findViewById(R.id.choice_problem_a3_btn));
		answerBtns.add((Button) findViewById(R.id.choice_problem_a4_btn));
		answerBtns.add((Button) findViewById(R.id.choice_problem_a5_btn));

		count = (TextView) findViewById(R.id.choice_problem_word_count);
		word = (TextView) findViewById(R.id.choice_problem_word_text);
		checkBtn = (Button) findViewById(R.id.choice_problem_check_btn);

		countDown = (ImageView) findViewById(R.id.choice_problem_count_down);

		countdownImage.add(R.raw.n10);
		countdownImage.add(R.raw.n09);
		countdownImage.add(R.raw.n08);
		countdownImage.add(R.raw.n07);
		countdownImage.add(R.raw.n06);
		countdownImage.add(R.raw.n05);
		countdownImage.add(R.raw.n04);
		countdownImage.add(R.raw.n03);
		countdownImage.add(R.raw.n02);
		countdownImage.add(R.raw.n01);
		countdownImage.add(R.raw.n00);
	}

	public void showPage() {

		count.setText((page + 1) + " / " + exercises.size());
		switch (exerciseType) {
		case R.id.category_find_meaning_btn:
			word.setText(exercises.get(page).Question.Word);
			break;
		case R.id.category_find_word_btn:
			word.setText(exercises.get(page).Question.Meaning);
			break;
		}

		for (int i = 0; i < 5; i++) {
			answerBtns.get(i).setText(exercises.get(page).AnswerItems.get(i).Answer);
			answerBtns.get(i).setTextColor(Color.GRAY);
		}

		Thread t = new Thread("Count Down Thread") {
			int idx = 0;

			public void run() {
				for (int i = 0; i < 10; i++) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							countDown.setImageResource(countdownImage.get(idx));

						}
					});
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					idx++;
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						countDown.setImageResource(countdownImage.get(idx));
						checkAnswer();
					}
				});

			}
		};
		t.start();
	}

	public void showResult() {

		setContentView(R.layout.result_layout);

		TextView cntTv = (TextView) findViewById(R.id.result_count_text);
		cntTv.setText(exercises.size() + " 문제 중 " + correctedCount() + " 개 맞았습니다.");

		TextView scrTv = (TextView) findViewById(R.id.result_score_text);
		scrTv.setText(correctedCount() * 100 / exercises.size() + " 점");

		if (isFinish()) {
			Button againBtn = (Button) findViewById(R.id.result_again_btn);
			againBtn.setVisibility(View.GONE);
		}
	}

	public Exercise makeGuessMeaningExercise(WordSet wordSet) {

		Exercise e = new Exercise();

		Cursor c = dbMgr.rawQuery("SELECT " + WordsDBMgr.MEANING + " from " + WordsDBMgr.TABLE_NAME + " where " + WordsDBMgr.TYPE + "=? group by " + WordsDBMgr.MEANING + " order by random() limit 5",
				new String[] { wordSet.Type });

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

	public Exercise makeGuessWordExercise(WordSet wordSet) {

		Exercise e = new Exercise();

		Cursor c = dbMgr.rawQuery("SELECT " + WordsDBMgr.WORD + " from " + WordsDBMgr.TABLE_NAME + " where " + WordsDBMgr.TYPE + "=? group by " + WordsDBMgr.WORD + " order by random() limit 5",
				new String[] { wordSet.Type });

		ArrayList<AnswerItem> answerItems = new ArrayList<AnswerItem>();

		boolean have = false;
		if (c != null) {
			int a = 0;
			while (c.moveToNext()) {
				if (c.getString(0).equals(wordSet.Word)) {
					have = true;
					e.AnswerNo = a;
				}
				answerItems.add(new AnswerItem(c.getString(0)));
				a++;
			}
		}

		if (!have) {
			int r = (int) (Math.random() * 5);
			answerItems.get(r).Answer = wordSet.Word;
			e.AnswerNo = r;
		}

		e.Question = wordSet;
		e.AnswerItems = answerItems;

		return e;
	}

	public ArrayList<Exercise> assembleWrongExercises() {

		if (Global.getInstance(getApplicationContext()).RandomProblem)
			makeExercisesRandomly();

		ArrayList<Exercise> ea = new ArrayList<Exercise>();

		for (int i = 0; i < exercises.size(); i++) {
			if (!exercises.get(i).Correct) {
				Exercise e = exercises.get(i);
				e.Clear();
				ea.add(e);
			}
		}
		return ea;
	}

	public void makeExercisesRandomly() {
		long seed = System.nanoTime();
		Collections.shuffle(exercises, new Random(seed));
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

	public boolean isFinish() {
		for (int i = 0; i < exercises.size(); i++) {
			if (!exercises.get(i).Correct)
				return false;
		}
		return true;
	}

	public void goHome() {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
	}

	public void checkAnswer() {

		if (checkBtn.getText().equals("정답 확인")) {
			if (answer < 0)
				return;

			exercises.get(page).Solve = true;

			if (exercises.get(page).AnswerItems.get(exercises.get(page).AnswerNo).Answer.equals(answerBtns.get(answer).getText())) {
				answerBtns.get(answer).setTextColor(0xFF02b58b);
				exercises.get(page).Correct = true;
			} else {
				answerBtns.get(exercises.get(page).AnswerNo).setTextColor(0xFF02b58b);
			}
			checkBtn.setText("다음 문제");
		} else if (checkBtn.getText().equals("다음 문제")) {
			page++;
			if (page >= exercises.size()) {
				page--;
				showResult();
			} else {
				showPage();
				checkBtn.setText("정답 확인");
				answer = -1;
			}
		}
	}

	public void onAnswerClick(View v) {

		for (int i = 0; i < 5; i++) {
			if (answerBtns.get(i).getId() == v.getId()) {
				answerBtns.get(i).setTextColor(Color.BLACK);
			} else {
				answerBtns.get(i).setTextColor(Color.GRAY);
			}
		}

		switch (v.getId()) {
		case R.id.choice_problem_a1_btn:
			answer = 0;
			break;
		case R.id.choice_problem_a2_btn:
			answer = 1;
			break;
		case R.id.choice_problem_a3_btn:
			answer = 2;
			break;
		case R.id.choice_problem_a4_btn:
			answer = 3;
			break;
		case R.id.choice_problem_a5_btn:
			answer = 4;
			break;
		}
	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.choice_problem_prev_btn:
			page--;
			if (page < 0)
				page = 0;
			showPage();
			break;
		case R.id.choice_problem_check_btn:
			checkAnswer();
			break;
		case R.id.result_restart_btn:
			page = 0;
			setContentView(R.layout.choice_problem_activity_layout);
			exerciseInit();
			viewInit();
			showPage();
			break;
		case R.id.result_again_btn:
			if (isFinish()) {
				goHome();
				return;
			}
			exercises = assembleWrongExercises();
			page = 0;
			setContentView(R.layout.choice_problem_activity_layout);
			viewInit();
			showPage();
			break;
		case R.id.result_home_btn:
			goHome();
			break;
		}
	}
}