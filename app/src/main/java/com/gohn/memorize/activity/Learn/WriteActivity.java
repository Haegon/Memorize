package com.gohn.memorize.activity.Learn;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gohn.memorize.R;
import com.gohn.memorize.activity.GroupActivity;
import com.gohn.memorize.common.CommonData;
import com.gohn.memorize.extention.ColorEx;
import com.gohn.memorize.model.ExerciseWrite;
import com.gohn.memorize.model.IAlertDialogTwoButtonHanlder;
import com.gohn.memorize.model.ISettingGroupNameViewHanlder;
import com.gohn.memorize.model.WordSet;
import com.gohn.memorize.util.Dialog;
import com.gohn.memorize.util.Global;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class WriteActivity extends LearnActivity implements View.OnClickListener {

//	DBMgr dbMgr;

	ArrayList<ExerciseWrite> exercises;
//	ArrayList<WordSet> wordsSet;

	TextView word;
	TextView count;
	Button checkBtn;
	EditText editText;
	TextView rightAnswer;

	int page = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

//		dbMgr = DBMgr.getInstance();
//		Bundle b = getIntent().getExtras();
//		groupName = b.getString(DBMgr.GROUP);
//		exerciseType = b.getInt(ExerciseType.toStr());
//		wordType = b.getString(DBMgr.TYPE);
//		fileName = groupName + "|" + exerciseType + "|" + wordType;
//		wordsSet = dbMgr.getWordsSet(groupName, wordType);

		if (isFileExist(fileName)) {
            Dialog.showTwoButtonAlert(this, R.string.load_save, new IAlertDialogTwoButtonHanlder() {
                @Override
                public void onPositive() {
                    readCurrentState();
                    startStudy();
                }
                @Override
                public void onNegative() {
                    deleteCurrentState();
                    exerciseInit();
                    startStudy();
                }
            });
        } else {
			exerciseInit();
            startStudy();
		}
	}

    public void startStudy() {
        setContentView(R.layout.content_write);
        viewInit();
        showPage();
    }

	public void exerciseInit() {
		exercises = new ArrayList<ExerciseWrite>();

		for ( WordSet wordSet : wordsSet )
			exercises.add(new ExerciseWrite(wordSet));

        if (Global.getBoolean(this, CommonData.GLOBAL_KEY_RANDOM, false))
			makeExercisesRandomly();
	}

	public void viewInit() {

		count = (TextView) findViewById(R.id.write_problem_word_count);
		word = (TextView) findViewById(R.id.write_problem_word_text);
		checkBtn = (Button) findViewById(R.id.btn_next);
		editText = (EditText) findViewById(R.id.write_problem_word_edit);
		rightAnswer = (TextView) findViewById(R.id.write_right_answer_word_text);

		editText.setTextColor(Color.GRAY);
		editText.setText("");
		rightAnswer.setText("");

        checkBtn.setOnClickListener(this);
        findViewById(R.id.btn_prev).setOnClickListener(this);
	}

	public void showPage() {

		count.setText((page + 1) + " / " + exercises.size());
		word.setText(exercises.get(page).getQuestion().getMeaning());

		editText.setText(exercises.get(page).getAnswerItem().getAnswer());
		if (exercises.get(page).isSolve()) {
			checkBtn.setText(R.string.next);
		} else {
			checkBtn.setText(R.string.check);
		}

		editText.setTextColor(exercises.get(page).getAnswerItem().getTint());

		if (!exercises.get(page).isCorrect()) {
			rightAnswer.setText(exercises.get(page).getAnswerItem().getRightAnswer());
			rightAnswer.setTextColor(ColorEx.VOCA);
		}
	}

	public void showResult() {
//		AdBuddiz.showAd(learnActivity);

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

		deleteCurrentState();
		setContentView(R.layout.content_result);

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
		return new ExerciseWrite(wordSet);
	}

	public ArrayList<ExerciseWrite> assembleWrongExercises() {

        if (Global.getBoolean(this, CommonData.GLOBAL_KEY_RANDOM, false))
			makeExercisesRandomly();

		ArrayList<ExerciseWrite> ea = new ArrayList<ExerciseWrite>();

		for (int i = 0; i < exercises.size(); i++) {
			if (!exercises.get(i).isCorrect()) {
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

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.btn_prev:
				page--;
				if (page < 0)
					page = 0;
				showPage();
				break;
			case R.id.btn_next:
				if (editText.getText().toString().isEmpty())
					return;

				if (checkBtn.getText().equals(getResources().getString(R.string.check))) {
					exercises.get(page).setSolve(true);

					exercises.get(page).getAnswerItem().setAnswer(editText.getText().toString());
					exercises.get(page).getAnswerItem().setRightAnswer(exercises.get(page).getQuestion().getWord());

					if (exercises.get(page).getQuestion().getWord().toLowerCase().equals(editText.getText().toString().toLowerCase())) {
						exercises.get(page).getAnswerItem().setTint(ColorEx.VOCA);
						exercises.get(page).setCorrect(true);
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
				setContentView(R.layout.content_write);
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
				setContentView(R.layout.content_write);
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
                                Dialog.showSettingGroupNameView(WriteActivity.this, new ISettingGroupNameViewHanlder() {
                                    @Override
                                    public void onPositive(String groupName) {
                                        final ArrayList<WordSet> words = new ArrayList<WordSet>();
                                        for (int i = 0; i < exercises.size(); i++) {
                                            words.add(exercises.get(i).getQuestion());
                                        }
                                        dbMgr.addWordsToDB(groupName, words);
                                        goHome();
                                    }
                                });
								break;

							case DialogInterface.BUTTON_NEGATIVE:
								Intent intent = new Intent();

								dbMgr.setWrongExercisesWrite(exercises);
								intent.setClass(getApplicationContext(), GroupActivity.class);
								intent.putExtra(CommonData.INTENT_KEY_PROBLEM_TYPE, CommonData.INTENT_VALUE_WRITE);
								startActivity(intent);
								break;
						}
					}
				};
				AlertDialog.Builder builder = new AlertDialog.Builder(WriteActivity.this);
				builder.setMessage(R.string.result_save_mention);
                builder.setPositiveButton(R.string.result_save_new, dialogClickListener);
                builder.setNegativeButton(R.string.result_save_old, dialogClickListener);
                builder.show();
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
