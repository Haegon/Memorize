package com.gohn.memorize.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.gohn.memorize.model.ExerciseType;
import com.gohn.memorize.model.ExerciseWrite;
import com.gohn.memorize.model.WordSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;

public class WriteProblemActivity extends LearnActivity {

	WordsDBMgr dbMgr;

	ArrayList<ExerciseWrite> exercises;
	ArrayList<WordSet> wordsSet;

	TextView word;
	TextView count;
	Button nextBtn;
	Button checkBtn;
	EditText editText;
	TextView rightAnswer;

	int page = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		dbMgr = WordsDBMgr.getInstance(this);
		Bundle b = getIntent().getExtras();
		groupName = b.getString(WordsDBMgr.GROUP);
		exerciseType = b.getInt(ExerciseType.toStr());
		wordType = b.getString(WordsDBMgr.TYPE);
		fileName = groupName + "|" + exerciseType + "|" + wordType;
		wordsSet = dbMgr.getWordsSet(groupName, wordType);

		if (isFileExist(fileName)) {
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						readCurrentState();
						break;
					case DialogInterface.BUTTON_NEGATIVE:
						deleteCurrentState();
						exerciseInit();
						break;
					}
					setContentView(R.layout.write_problem_activity_layout);
					viewInit();
					showPage();
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(WriteProblemActivity.this);
			builder.setMessage(R.string.load_save).setPositiveButton(R.string.yes, dialogClickListener).setNegativeButton(R.string.no, dialogClickListener).show();
		} else {
			setContentView(R.layout.write_problem_activity_layout);
			exerciseInit();
			viewInit();
			showPage();
		}
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

		editText.setTextColor(Color.GRAY);
		editText.setText("");
		rightAnswer.setText("");
	}

	public void showPage() {

		count.setText((page + 1) + " / " + exercises.size());
		word.setText(exercises.get(page).Question.Meaning);

		editText.setText(exercises.get(page).AnswerItem.Answer);
		if (exercises.get(page).Solve) {
			checkBtn.setText(R.string.next);
		} else {
			checkBtn.setText(R.string.check);
		}

		// editText.setText(exercises.get(page).AnswerItems.Answer);
		editText.setTextColor(exercises.get(page).AnswerItem.Tint);

		if (!exercises.get(page).Correct) {
			rightAnswer.setText(exercises.get(page).AnswerItem.RightAnswer);
			rightAnswer.setTextColor(ColorEx.VOCA);
		}
	}

	public void showResult() {
		AdBuddiz.showAd(learnActivity);

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

			if (checkBtn.getText().equals(getResources().getString(R.string.check))) {
				exercises.get(page).Solve = true;

				exercises.get(page).AnswerItem.Answer = editText.getText().toString();
				exercises.get(page).AnswerItem.RightAnswer = exercises.get(page).Question.Word;

				if (exercises.get(page).Question.Word.toLowerCase().equals(editText.getText().toString().toLowerCase())) {
					exercises.get(page).AnswerItem.Tint = ColorEx.VOCA;
					exercises.get(page).Correct = true;
				} else {
					vibe.vibrate(150);
				}
				saveCurrentState();
				showPage();
			} else if (checkBtn.getText().equals(getResources().getString(R.string.next))) {
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
		case R.id.result_save_btn:
			exercises = assembleWrongExercises();
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:

						LayoutInflater li = LayoutInflater.from(WriteProblemActivity.this);
						View promptsView = li.inflate(R.layout.activity_group_edittext, null);
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(WriteProblemActivity.this);
						// set prompts.xml to alertdialog builder
						alertDialogBuilder.setView(promptsView);

						final EditText userInput = (EditText) promptsView.findViewById(R.id.set_group_edittext);

						// set dialog message
						alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								// 단어장 이름이 비어있을때
								if (userInput.getText().toString().equals("")) {
									AlertDialog.Builder builder = new AlertDialog.Builder(WriteProblemActivity.this);
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
									AlertDialog.Builder builder = new AlertDialog.Builder(WriteProblemActivity.this);
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
						dbMgr.worngExercisesWrite = exercises;
						intent.setClass(getApplicationContext(), SelectGroupActivity.class);
						intent.putExtra("type", "write");
						startActivity(intent);
						break;
					}
				}
			};
			AlertDialog.Builder builder = new AlertDialog.Builder(WriteProblemActivity.this);
			builder.setMessage(R.string.result_save_mention).setPositiveButton(R.string.result_save_new, dialogClickListener).setNegativeButton(R.string.result_save_old, dialogClickListener).show();
		}
	}

	private void deleteCurrentState() {
		deleteFile(fileName);
	}

	private void saveCurrentState() {

		Thread t = new Thread() {
			public void run() {
				try {
					Gson gson = new Gson();

					// 마지막 문제는 현재 페이지 저장. 그전 문제는 다음 페이지 저장.
					json.put("page", page + 1 == exercises.size() ? page : page + 1);
					json.put("list", gson.toJson(exercises));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				writeToFile(fileName, json.toString());
			}
		};
		t.start();
	}

	private void readCurrentState() {
		String jstr = readFromFile(fileName);
		try {
			JSONObject j = new JSONObject(jstr);

			page = j.getInt("page");

			String list = j.getString("list");
			Gson gson = new Gson();
			exercises = gson.fromJson(list, new TypeToken<ArrayList<ExerciseWrite>>() {
			}.getType());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
