package com.gohn.memorize.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.Global;
import com.gohn.memorize.manager.WordsDBMgr;
import com.gohn.memorize.model.Exercise;
import com.gohn.memorize.model.ExerciseType;
import com.gohn.memorize.model.WordSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class StudyActivity extends LearnActivity {

	WordsDBMgr dbMgr;

	ArrayList<WordSet> wordsSet;

	TextView word;
	TextView meaning;
	TextView count;

	Button nextBtn;

	int page = 0;
	boolean isBlind;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		dbMgr = WordsDBMgr.getInstance(this);
		Bundle b = getIntent().getExtras();
		groupName = b.getString(WordsDBMgr.GROUP);
		exerciseType = b.getInt(ExerciseType.toStr());
		wordType = b.getString(WordsDBMgr.TYPE);
		isBlind = b.getString("mode").equals("blind") ? true : false;

		fileName = groupName + "|" + exerciseType + "|" + wordType + "|" + isBlind;

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
					setContentView(R.layout.study_activity_layout);
					viewInit();
					showPage();
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(StudyActivity.this);
			builder.setMessage(R.string.load_save).setPositiveButton(R.string.yes, dialogClickListener).setNegativeButton(R.string.no, dialogClickListener).show();
		} else {
			setContentView(R.layout.study_activity_layout);
			exerciseInit();
			viewInit();
			showPage();
		}
	}

	public void exerciseInit() {

		wordsSet = dbMgr.getWordsSet(groupName, wordType);

		if (Global.getInstance(getApplicationContext()).RandomProblem)
			makeWordRandomly();
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
		deleteCurrentState();
		setContentView(R.layout.study_finish_layout);
	}

	public void makeWordRandomly() {
		long seed = System.nanoTime();
		Collections.shuffle(wordsSet, new Random(seed));
	}

	public boolean isFinish() {
		return page == wordsSet.size();
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
			saveCurrentState();
			if (wordsSet.get(page).IsOpen) {
				page++;
				if (page >= wordsSet.size()) {
					page--;
					end = true;
					showResult();
				} else {
					showPage();
				}
			} else {
				wordsSet.get(page).IsOpen = true;
				showPage();
			}
			break;
		case R.id.study_restart_btn:
			page = 0;
			setContentView(R.layout.study_activity_layout);
			end = false;
			exerciseInit();
			viewInit();
			showPage();
			break;
		case R.id.study_home_btn:
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
					json.put("page", page + 1 == wordsSet.size() ? page : page + 1);
					json.put("list", gson.toJson(wordsSet));
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
			wordsSet = gson.fromJson(list, new TypeToken<ArrayList<WordSet>>() {
			}.getType());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}