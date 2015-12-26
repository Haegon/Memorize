package com.gohn.memorize.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gohn.memorize.R;
import com.gohn.memorize.extention.ColorEx;
import com.gohn.memorize.manager.DBMgr;
import com.gohn.memorize.model.AnswerItem;
import com.gohn.memorize.model.Exercise;
import com.gohn.memorize.model.ExerciseType;
import com.gohn.memorize.model.WordSet;
import com.gohn.memorize.model.WordType;
import com.gohn.memorize.util.GLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ChoiceProblemActivity extends LearnActivity {

//	DBMgr dbMgr;

	ArrayList<Exercise> exercises;
	ArrayList<WordSet> wordsSet = new ArrayList<WordSet>();
	Map<String, ArrayList<WordSet>> wordMap;

	TextView word;
	TextView count;
	Button nextBtn;
	Button checkBtn;
	ArrayList<Button> answerBtns;

	int page = 0;
	int answer = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Bundle b = getIntent().getExtras();
		groupName = b.getString(DBMgr.GROUP);
		exerciseType = b.getInt(ExerciseType.toStr());
		wordType = b.getString(DBMgr.TYPE);
		fileName = groupName + "|" + exerciseType + "|" + wordType;
		vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		wordsSet = dbMgr.getWordsSet(groupName, wordType);

		// TODO 학습 기록이 있는지 캐시에 기록하자 paper 쓰자
//		if (isFileExist(fileName)) {
//			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					switch (which) {
//						case DialogInterface.BUTTON_POSITIVE:
//							readCurrentState();
//							break;
//						case DialogInterface.BUTTON_NEGATIVE:
//							deleteCurrentState();
//							exerciseInit();
//							break;
//					}
//					setContentView(R.layout.content_choice_problem);
//					initView();
//					showPage();
//				}
//			};
//
//			AlertDialog.Builder builder = new AlertDialog.Builder(ChoiceProblemActivity.this);
//			builder.setMessage(R.string.load_save).setPositiveButton(R.string.yes, dialogClickListener).setNegativeButton(R.string.no, dialogClickListener).show();
//		} else {
		exerciseInit();
//			setContentView(R.layout.content_choice_problem);

		View newView = LayoutInflater.from(this).inflate(R.layout.content_choice_problem, null);
		contentView.addView(newView);


		initView();
		showPage();
//		}
	}

	public void exerciseInit() {
		wordMap = new HashMap<String, ArrayList<WordSet>>();
		exercises = new ArrayList<Exercise>();


		for (WordSet wordSet : wordsSet) {
			if (!wordMap.containsKey(wordSet.getType())) {
				wordMap.put(wordSet.getType(), new ArrayList<WordSet>());
			}
			wordMap.get(wordSet.getType()).add(wordSet);
		}

		for (WordSet wordSet : wordsSet) {
			switch (exerciseType) {
				case R.id.btn_category_find_meaning:
					exercises.add(makeGuessMeaningExercise(wordSet));
					break;
				case R.id.btn_category_find_word:
					exercises.add(makeGuessWordExercise(wordSet));
					break;
				default:
					finish();
					break;
			}
		}

		// TODO 문제를 랜덤하게 낼것인지 말것인지 설정값 저장해야함
//		if (Global.getInstance(getApplicationContext()).RandomProblem)
//			makeExercisesRandomly();
	}

	public void initView() {

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
			case R.id.btn_category_find_meaning:
				word.setText(exercises.get(page).getQuestion().getWord());
				break;
			case R.id.btn_category_find_word:
				word.setText(exercises.get(page).getQuestion().getMeaning());
				break;
		}

		for (int i = 0; i < 5; i++) {
			answerBtns.get(i).setText(exercises.get(page).getAnswerItems().get(i).getAnswer());
			answerBtns.get(i).setTextColor(exercises.get(page).getAnswerItems().get(i).getTint());
		}

		if (exercises.get(page).isSolve())
			checkBtn.setText(R.string.next);
		else
			checkBtn.setText(R.string.check);
	}

	public void showResult() {

//		AdBuddiz.showAd(learnActivity);
		deleteCurrentState();

		setContentView(R.layout.result_layout);

		TextView cntTv = (TextView) findViewById(R.id.result_count_text);
		cntTv.setText(exercises.size() + " " + getResources().getString(R.string.result_problems) + " " + correctedCount() + " " + getResources().getString(R.string.result_correct));

		TextView scrTv = (TextView) findViewById(R.id.result_score_text);
		scrTv.setText(correctedCount() * 100 / exercises.size() + " " + getResources().getString(R.string.result_point));

		if (isFinish()) {
			Button againBtn = (Button) findViewById(R.id.result_again_btn);
			againBtn.setVisibility(View.GONE);

			Button saveBtn = (Button) findViewById(R.id.result_save_btn);
			saveBtn.setVisibility(View.GONE);
		}
	}

	public Exercise makeGuessMeaningExercise(WordSet wordSet) {

		Exercise e = new Exercise();
		ArrayList<AnswerItem> answerItems = new ArrayList<AnswerItem>();


		ArrayList<Integer> idxs = new ArrayList<Integer>();
		boolean have = false;
		Random rnd = new Random(System.nanoTime());
		int a = 0;

		ArrayList<WordSet> ws = wordMap.get(wordSet.getType());
		GLog.Debug("@@@@@ 101 ws.size() : " + ws.size());
		for (int i = 0; i < (wordsSet.size() >= 5 ? 5 : wordsSet.size()); i++) {
			GLog.Debug("@@@@@ 102");
			Integer n = rnd.nextInt(ws.size());
			while (idxs.contains(n)) {
				if (wordType.equals(WordType.NONE) && !ws.get(n).getType().equals(wordSet.getType())) {
					continue;
				}
				n = rnd.nextInt(ws.size());
			}
			idxs.add(n);
			if (ws.get(n).getMeaning().equals(wordSet.getMeaning())) {
				have = true;
				e.setAnswerNo(a);
			}
			a++;
			answerItems.add(new AnswerItem(ws.get(n).getMeaning()));
		}

		if (wordsSet.size() < 5) {
			for (int i = 0; i < 5 - wordsSet.size(); i++) {
				answerItems.add(new AnswerItem(""));
			}
		}

		if (!have) {
			int r = (int) (Math.random() * 5);
			answerItems.get(r).setAnswer(wordSet.getMeaning());
			e.setAnswerNo(r);
		}

		e.setQuestion(wordSet);
		e.setAnswerItems(answerItems);

		return e;
	}

	public Exercise makeGuessWordExercise(WordSet wordSet) {

		Exercise e = new Exercise();
		ArrayList<AnswerItem> answerItems = new ArrayList<AnswerItem>();
		ArrayList<Integer> idxs = new ArrayList<Integer>();
		boolean have = false;
		Random rnd = new Random(System.nanoTime());
		int a = 0;

		ArrayList<WordSet> ws = wordMap.get(wordSet.getType());

		for (int i = 0; i < 5; i++) {
			Integer n = rnd.nextInt(ws.size());
			while (idxs.contains(n)) {
				if (wordType.equals(WordType.NONE) && !ws.get(n).getType().equals(wordSet.getType())) {
					continue;
				}
				n = rnd.nextInt(ws.size());
			}
			idxs.add(n);
			if (ws.get(n).getWord().equals(wordSet.getWord())) {
				have = true;
				e.setAnswerNo(a);
			}
			a++;
			answerItems.add(new AnswerItem(ws.get(n).getWord()));
		}

		if (!have) {
			int r = (int) (Math.random() * 5);
			answerItems.get(r).setAnswer(wordSet.getWord());
			e.setAnswerNo(r);
		}

		e.setQuestion(wordSet);
		e.setAnswerItems(answerItems);

		return e;
	}

	public ArrayList<Exercise> assembleWrongExercises() {

//		if (Global.getInstance(getApplicationContext()).RandomProblem)
//			makeExercisesRandomly();

		ArrayList<Exercise> ea = new ArrayList<Exercise>();

		for (int i = 0; i < exercises.size(); i++) {
			if (!exercises.get(i).isCorrect()) {
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
			if (exercises.get(i).isSolve())
				c++;
		}
		return c;
	}

	public int correctedCount() {
		int c = 0;
		for (int i = 0; i < exercises.size(); i++) {
			if (exercises.get(i).isCorrect())
				c++;
		}
		return c;
	}

	public boolean isFinish() {
		for (int i = 0; i < exercises.size(); i++) {
			if (!exercises.get(i).isCorrect())
				return false;
		}
		return true;
	}

	public void onAnswerClick(View v) {

		if (exercises.get(page).isSolve())
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

	private void deleteCurrentState() {
		deleteFile(fileName);
	}

	private void saveCurrentState() {

//		Thread t = new Thread() {
//			public void run() {
//				try {
//					Gson gson = new Gson();
//
//					// ??? ??? ?? ??? ??. ?? ??? ?? ??? ??.
//					json.put("page", page + 1 == exercises.size() ? page : page + 1);
//					json.put("list", gson.toJson(exercises));
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				writeToFile(fileName, json.toString());
//			}
//		};
//		t.start();
	}

	private void readCurrentState() {
//		String jstr = readFromFile(fileName);
//		try {
//			JSONObject j = new JSONObject(jstr);
//
//			page = j.getInt("page");
//
//			String list = j.getString("list");
//			Gson gson = new Gson();
//			exercises = gson.fromJson(list, new TypeToken<ArrayList<Exercise>>() {
//			}.getType());
//
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
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

					exercises.get(page).setSolve(true);

					if (exercises.get(page).getAnswerItems().get(exercises.get(page).getAnswerNo()).getAnswer().equals(answerBtns.get(answer).getText())) {
						exercises.get(page).getAnswerItems().get(exercises.get(page).getAnswerNo()).setTint(ColorEx.VOCA);
						answerBtns.get(answer).setTextColor(exercises.get(page).getAnswerItems().get(exercises.get(page).getAnswerNo()).getTint());
						exercises.get(page).setCorrect(true);
					} else {
						vibe.vibrate(150);
						exercises.get(page).getAnswerItems().get(exercises.get(page).getAnswerNo()).setTint(ColorEx.VOCA);
						answerBtns.get(exercises.get(page).getAnswerNo()).setTextColor(ColorEx.VOCA);
						exercises.get(page).getAnswerItems().get(answer).setTint(Color.BLACK);
						answerBtns.get(answer).setTextColor(exercises.get(page).getAnswerItems().get(answer).getTint());
					}
					checkBtn.setText(R.string.next);
					saveCurrentState();
				} else if (checkBtn.getText().equals(getResources().getString(R.string.next))) {
					page++;
					if (page >= exercises.size()) {
						page--;
						end = true;
						showResult();
					} else {
						showPage();
						answer = -1;
					}
				}
				break;
			case R.id.result_restart_btn:
				page = 0;
				end = false;
				setContentView(R.layout.content_choice_problem);
				exerciseInit();
				initView();
				showPage();
				break;
			case R.id.result_again_btn:
				if (isFinish()) {
					goHome();
					return;
				}
				exercises = assembleWrongExercises();
				page = 0;
				end = false;
				setContentView(R.layout.content_choice_problem);
				initView();
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
								View promptsView = li.inflate(R.layout.dialog_input_group_name, null);
								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ChoiceProblemActivity.this);

								alertDialogBuilder.setView(promptsView);

								final EditText userInput = (EditText) promptsView.findViewById(R.id.et_group_name);

									alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {

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
											words.add(exercises.get(i).getQuestion());
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
									intent.putExtra(exercises.get(i).getQuestion().getWord(), exercises.get(i).getQuestion().getMeaning());
								}

								// TODO 틀린문제 따로 저장할 수 있게 단어 목록 로드
//								dbMgr.setWorngExercises(exercises);
//								intent.setClass(getApplicationContext(), SelectGroupActivity.class);
//								startActivity(intent);
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