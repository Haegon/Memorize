package com.gohn.memorize.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gohn.memorize.R;
import com.gohn.memorize.extention.ColorEx;
import com.gohn.memorize.manager.Global;
import com.gohn.memorize.manager.WordsDBMgr;
import com.gohn.memorize.model.AnswerItem;
import com.gohn.memorize.model.Exercise;
import com.gohn.memorize.model.ExerciseType;
import com.gohn.memorize.model.WordSet;
import com.gohn.memorize.model.WordType;

public class ChoiceProblemActivity extends BaseActivity {

	ArrayList<Exercise> exercises;
	ArrayList<WordSet> wordsSet;
	Map<String, ArrayList<WordSet>> wordMap;

	TextView word;
	TextView count;
	Button nextBtn;
	Button checkBtn;
	ArrayList<Button> answerBtns;

	String groupName;
	String wordType;

	WordsDBMgr dbMgr;

	int exerciseType;
	int page = 0;
	int answer = -1;

	Vibrator vibe;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choice_problem_activity_layout);

		dbMgr = WordsDBMgr.getInstance(this);
		Bundle b = getIntent().getExtras();
		groupName = b.getString(WordsDBMgr.GROUP);
		exerciseType = b.getInt(ExerciseType.toStr());
		wordType = b.getString(WordsDBMgr.TYPE);
		wordsSet = dbMgr.getWordsSet(groupName, wordType);

		exerciseInit();
		viewInit();
		showPage();

		vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	}

	public void exerciseInit() {
		wordMap = new HashMap<String, ArrayList<WordSet>>();

		for (int i = 0; i < wordsSet.size(); i++) {
			if (!wordMap.containsKey(wordsSet.get(i).Type)) {
				wordMap.put(wordsSet.get(i).Type, new ArrayList<WordSet>());
			}
			wordMap.get(wordsSet.get(i).Type).add(wordsSet.get(i));
		}

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
			answerBtns.get(i).setTextColor(exercises.get(page).AnswerItems.get(i).Tint);
		}

		if (exercises.get(page).Solve)
			checkBtn.setText(R.string.next);
		else
			checkBtn.setText(R.string.check);
	}

	public void showResult() {

		setContentView(R.layout.result_layout);

		TextView cntTv = (TextView) findViewById(R.id.result_count_text);
		cntTv.setText(exercises.size() + " " + getResources().getString(R.string.result_problems) + " " + correctedCount() + " " + getResources().getString(R.string.result_correct));

		TextView scrTv = (TextView) findViewById(R.id.result_score_text);
		scrTv.setText(correctedCount() * 100 / exercises.size() + " " + getResources().getString(R.string.result_point));

		if (isFinish()) {
			Button againBtn = (Button) findViewById(R.id.result_again_btn);
			againBtn.setVisibility(View.GONE);
		}
	}

	// 문제를 넣으면 보기와 정답유무를 알고있는 클래스를 리턴해주는 함수.
	public Exercise makeGuessMeaningExercise(WordSet wordSet) {
		Log.d("gohn", "@@@@@@@@@@@@@@@@@@@@@@@@@");
		// 리턴한 문제 클래스 생성.
		Exercise e = new Exercise();
		// 보기는 5개의 리스트로 관리된다.
		ArrayList<AnswerItem> answerItems = new ArrayList<AnswerItem>();

		// 전체 보기 리스트에서 가져올 보기의 인덱스 리스트
		// 5개의 보기만 넣을것임.
		ArrayList<Integer> idxs = new ArrayList<Integer>();
		boolean have = false;
		Random rnd = new Random(System.nanoTime());
		int a = 0;

		// 보기에 넣을 단어 리스트를 만들어줄 리스트를 생성한다.
		ArrayList<WordSet> ws = wordMap.get(wordSet.Type);

		for (int i = 0; i < (wordsSet.size() >= 5 ? 5 : wordsSet.size()); i++) {
			// 랜덤한 숫자를 가져온다.
			Integer n = rnd.nextInt(ws.size());
			// 인덱스가 겹치지 않도록 랜덤하게 가져온다.
			while (idxs.contains(n)) {
				if (wordType.equals(WordType.NONE) && !ws.get(n).Type.equals(wordSet.Type)) {
					continue;
				}
				n = rnd.nextInt(ws.size());
			}
			idxs.add(n);
			// 인덱스에 해당하는 단어가 문제의 정답과 일치하면
			// 정답이 보기 중에 있다고 하고 정답을 적어 놓는다.
			if (ws.get(n).Meaning.equals(wordSet.Meaning)) {
				have = true;
				e.AnswerNo = a;
			}
			a++;
			// 문제 리스트에 추가 한다.
			answerItems.add(new AnswerItem(ws.get(n).Meaning));
		}

		if (wordsSet.size() < 5) {
			for (int i = 0; i < 5- wordsSet.size() ; i++) {
				answerItems.add(new AnswerItem(""));
			}
		}

		// 5개의 보기에 정답이 없는경우 5개중 하나를 택해서 정답을 꽂아준다.
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
		ArrayList<AnswerItem> answerItems = new ArrayList<AnswerItem>();
		ArrayList<Integer> idxs = new ArrayList<Integer>();
		boolean have = false;
		Random rnd = new Random(System.nanoTime());
		int a = 0;

		// 보기에 넣을 단어 리스트를 만들어줄 리스트를 생성한다.
		ArrayList<WordSet> ws = wordMap.get(wordSet.Type);

		for (int i = 0; i < 5; i++) {
			Integer n = rnd.nextInt(ws.size());
			while (idxs.contains(n)) {
				if (wordType.equals(WordType.NONE) && !ws.get(n).Type.equals(wordSet.Type)) {
					continue;
				}
				n = rnd.nextInt(ws.size());
			}
			idxs.add(n);
			if (ws.get(n).Word.equals(wordSet.Word)) {
				have = true;
				e.AnswerNo = a;
			}
			a++;
			answerItems.add(new AnswerItem(ws.get(n).Word));
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

	public void onAnswerClick(View v) {

		if (exercises.get(page).Solve)
			return;

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

			if (checkBtn.getText().equals(getResources().getString(R.string.check))) {
				if (answer < 0)
					return;

				exercises.get(page).Solve = true;

				if (exercises.get(page).AnswerItems.get(exercises.get(page).AnswerNo).Answer.equals(answerBtns.get(answer).getText())) {
					exercises.get(page).AnswerItems.get(exercises.get(page).AnswerNo).Tint = ColorEx.VOCA;
					answerBtns.get(answer).setTextColor(exercises.get(page).AnswerItems.get(exercises.get(page).AnswerNo).Tint);
					exercises.get(page).Correct = true;
				} else {
					vibe.vibrate(150);
					exercises.get(page).AnswerItems.get(exercises.get(page).AnswerNo).Tint = ColorEx.VOCA;
					answerBtns.get(exercises.get(page).AnswerNo).setTextColor(ColorEx.VOCA);
					exercises.get(page).AnswerItems.get(answer).Tint = Color.BLACK;
					answerBtns.get(answer).setTextColor(exercises.get(page).AnswerItems.get(answer).Tint);
				}
				checkBtn.setText(R.string.next);
			} else if (checkBtn.getText().equals(getResources().getString(R.string.next))) {
				page++;
				if (page >= exercises.size()) {
					page--;
					showResult();
				} else {
					showPage();
					answer = -1;
				}
			}
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
		case R.id.result_save_btn:
			exercises = assembleWrongExercises();
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:

						LayoutInflater li = LayoutInflater.from(ChoiceProblemActivity.this);
						View promptsView = li.inflate(R.layout.activity_group_edittext, null);
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChoiceProblemActivity.this);
						// set prompts.xml to alertdialog builder
						alertDialogBuilder.setView(promptsView);

						final EditText userInput = (EditText) promptsView.findViewById(R.id.set_group_edittext);

						// set dialog message
						alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								
								// 단어장 이름이 비어있을때
								if (userInput.getText().toString().equals("")) {
									AlertDialog.Builder builder = new AlertDialog.Builder(ChoiceProblemActivity.this);
									builder.setMessage(R.string.no_name).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
											// do things
										}
									});
									AlertDialog alert = builder.create();
									alert.show();
									return;
								}
								
								// 동일한 단어장 이름이 있을때
								if (dbMgr.getGroupNames().contains(userInput.getText().toString())) {
									AlertDialog.Builder builder = new AlertDialog.Builder(ChoiceProblemActivity.this);
									builder.setMessage("Vocabulary name is duplicated").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int id) {
											// do things
										}
									});
									AlertDialog alert = builder.create();
									alert.show();
									return;
								}
								
								final ArrayList<WordSet> words = new ArrayList<WordSet>();
								for (int i = 0; i < exercises.size(); i++) {
									words.add(exercises.get(i).Question);
								}
								dbMgr.addWordsToDB(userInput.getText().toString(), words);
								goHome();
							}
						}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

						// create alert dialog
						AlertDialog alertDialog = alertDialogBuilder.create();

						// show it
						alertDialog.show();
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						// No button clicked
						Intent intent = new Intent();
						for (int i = 0; i < exercises.size(); i++) {
							intent.putExtra(exercises.get(i).Question.Word, exercises.get(i).Question.Meaning);
						}
						dbMgr.worngExercises = exercises;
						intent.setClass(getApplicationContext(), SelectGroupActivity.class);
						startActivity(intent);
						break;
					}
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(ChoiceProblemActivity.this);
			builder.setMessage(R.string.result_save_mention).setPositiveButton(R.string.result_save_new, dialogClickListener).setNegativeButton(R.string.result_save_old, dialogClickListener).show();

			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		exercises = null;
		wordsSet = null;
		word = null;
		count = null;
		nextBtn = null;
		checkBtn = null;
		answerBtns = null;
		dbMgr = null;
	}
}