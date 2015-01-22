package com.gohn.memorize.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.Global;
import com.gohn.memorize.manager.WordsDBMgr;
import com.gohn.memorize.model.AnswerItem;
import com.gohn.memorize.model.ExerciseType;
import com.gohn.memorize.model.ExerciseWrite;
import com.gohn.memorize.model.WordSet;

public class WriteProblemActivity extends Activity {

	ArrayList<ExerciseWrite> exercises;
	ArrayList<WordSet> wordsSet;

	TextView word;
	TextView count;
	Button nextBtn;
	Button checkBtn;
	EditText editText;
	TextView rightAnswer;

	WordsDBMgr dbMgr;

	int exerciseType;
	int page = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.write_problem_activity_layout);

		dbMgr = WordsDBMgr.getInstance(this);
		Bundle b = getIntent().getExtras();
		exerciseType = b.getInt(ExerciseType.toStr());
		wordsSet = dbMgr.getWordsSet(b.getString(WordsDBMgr.GROUP), b.getString(WordsDBMgr.TYPE));

		exerciseInit();
		viewInit();
		showPage();
	}

	public void exerciseInit() {
		exercises = new ArrayList<ExerciseWrite>();

		for (int i = 0; i < wordsSet.size(); i++) {
			exercises.add(makeWriteWordExercise(wordsSet.get(i)));
		}

		if (Global.getInstance(getApplicationContext()).RandomProblem)
			makeExercisesRandomly();
	}

	public void viewInit() {

		count = (TextView) findViewById(R.id.write_problem_word_count);
		word = (TextView) findViewById(R.id.write_problem_word_text);
		checkBtn = (Button) findViewById(R.id.write_problem_check_btn);
		editText = (EditText) findViewById(R.id.write_problem_word_edit);
		rightAnswer = (TextView) findViewById(R.id.write_right_answer_word_text);

		editText.setTextColor(Color.BLACK);
		editText.setText("");
		rightAnswer.setText("");
	}

	public void showPage() {

		count.setText(page + " / " + exercises.size());
		word.setText(exercises.get(page).Question.Meaning);
		if (exercises.get(page).Solve) {
			checkBtn.setText("다음 문제");
		} else {
			checkBtn.setText("정답 확인");
		}

		editText.setText(exercises.get(page).AnswerItems.Answer);
		editText.setTextColor(exercises.get(page).AnswerItems.Tint);
		rightAnswer.setText(exercises.get(page).AnswerItems.RightAnswer);
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

	public ExerciseWrite makeWriteWordExercise(WordSet wordSet) {

		ExerciseWrite e = new ExerciseWrite();
		e.Question = wordSet;
		return e;
	}

	public ArrayList<ExerciseWrite> assembleWrongExercises() {

		if (Global.getInstance(getApplicationContext()).RandomProblem)
			makeExercisesRandomly();

		ArrayList<ExerciseWrite> ea = new ArrayList<ExerciseWrite>();

		for (int i = 0; i < exercises.size(); i++) {
			if (!exercises.get(i).Correct) {
				ExerciseWrite e = exercises.get(i);
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

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.write_problem_prev_btn:
			page--;
			if (page < 0)
				page = 0;
			showPage();
			break;
		case R.id.write_problem_check_btn:
			if (editText.getText().toString().isEmpty())
				return;

			if (checkBtn.getText().equals("정답 확인")) {
				exercises.get(page).Solve = true;

				exercises.get(page).AnswerItems.Answer = editText.getText().toString();
				exercises.get(page).AnswerItems.RightAnswer = exercises.get(page).Question.Word;

				if (exercises.get(page).Question.Word.toLowerCase().equals(editText.getText().toString().toLowerCase())) {
					exercises.get(page).AnswerItems.Tint = Color.BLUE;
					exercises.get(page).Correct = true;
				} else {
					exercises.get(page).AnswerItems.Tint = Color.RED;
				}
				showPage();
			} else if (checkBtn.getText().equals("다음 문제")) {
				page++;
				if (page >= exercises.size()) {
					page--;
					showResult();
				} else {
					editText.setTextColor(Color.BLACK);
					editText.setText("");
					rightAnswer.setText("");
					showPage();
				}
			}
			break;
		case R.id.result_restart_btn:
			page = 0;
			setContentView(R.layout.write_problem_activity_layout);
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
			setContentView(R.layout.write_problem_activity_layout);
			viewInit();
			showPage();
			break;
		case R.id.result_home_btn:
			goHome();
			break;
		}
	}
}
