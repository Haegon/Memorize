package com.gohn.memorize.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
		if (exercises.get(page).Solve) {
			checkBtn.setText("다음 문제");
		} else {
			checkBtn.setText("정답 확인");
		}

		// editText.setText(exercises.get(page).AnswerItems.Answer);
		editText.setTextColor(exercises.get(page).AnswerItems.Tint);

		if (!exercises.get(page).Correct) {
			rightAnswer.setText(exercises.get(page).AnswerItems.RightAnswer);
			rightAnswer.setTextColor(ColorEx.VOCA);
		}
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

				exercises.get(page).AnswerItems.Answer = editText.getText().toString();
				exercises.get(page).AnswerItems.RightAnswer = exercises.get(page).Question.Word;

				if (exercises.get(page).Question.Word.toLowerCase().equals(editText.getText().toString().toLowerCase())) {
					exercises.get(page).AnswerItems.Tint = ColorEx.VOCA;
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
