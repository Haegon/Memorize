package com.gohn.memorize.activity.learn;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gohn.memorize.R;
import com.gohn.memorize.activity.GroupActivity;
import com.gohn.memorize.activity.base.LearnActivity;
import com.gohn.memorize.common.CommonData;
import com.gohn.memorize.extention.ColorEx;
import com.gohn.memorize.model.AnswerItem;
import com.gohn.memorize.model.Exercise;
import com.gohn.memorize.model.IAlertDialogTwoButtonHanlder;
import com.gohn.memorize.model.ISettingGroupNameViewHanlder;
import com.gohn.memorize.model.WordSet;
import com.gohn.memorize.model.WordType;
import com.gohn.memorize.util.Dialog;
import com.gohn.memorize.util.Global;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MultipleActivity extends LearnActivity implements View.OnClickListener {

    ArrayList<Exercise> exercises;
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
        super.onCreate(savedInstanceState);

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

    void startStudy() {
        setContentView(R.layout.content_multiple);
        initView();
        showPage();
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

        if (Global.getBoolean(this,CommonData.GLOBAL_KEY_RANDOM,false))
			makeExercisesRandomly();
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
        checkBtn = (Button) findViewById(R.id.btn_next);

        for (Button btn : answerBtns)
            btn.setOnClickListener(this);

        checkBtn.setOnClickListener(this);
        findViewById(R.id.btn_prev).setOnClickListener(this);
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

        findViewById(R.id.result_again_btn).setOnClickListener(this);
        findViewById(R.id.result_home_btn).setOnClickListener(this);
        findViewById(R.id.result_restart_btn).setOnClickListener(this);
        findViewById(R.id.result_save_btn).setOnClickListener(this);
    }

    public Exercise makeGuessMeaningExercise(WordSet wordSet) {

        Exercise e = new Exercise();
        ArrayList<AnswerItem> answerItems = new ArrayList<AnswerItem>();


        ArrayList<Integer> idxs = new ArrayList<Integer>();
        boolean have = false;
        Random rnd = new Random(System.nanoTime());
        int a = 0;

        ArrayList<WordSet> ws = wordMap.get(wordSet.getType());
        for (int i = 0; i < (wordsSet.size() >= 5 ? 5 : wordsSet.size()); i++) {
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

		if (Global.getBoolean(this,CommonData.GLOBAL_KEY_RANDOM,false))
			makeExercisesRandomly();

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


    }

    private void deleteCurrentState() {
        deleteFile(fileName);
    }

    private void saveCurrentState() {

		Thread t = new Thread() {
			public void run() {
				try {
					Gson gson = new Gson();

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
			exercises = gson.fromJson(list, new TypeToken<ArrayList<Exercise>>() {
			}.getType());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public void onClick(View v) {

        // 문제안에 있는 버튼인지 확인.
        if (v.getId() == R.id.choice_problem_a1_btn ||
                v.getId() == R.id.choice_problem_a2_btn ||
                v.getId() == R.id.choice_problem_a3_btn ||
                v.getId() == R.id.choice_problem_a4_btn ||
                v.getId() == R.id.choice_problem_a5_btn) {
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
        } else {


            switch (v.getId()) {
                case R.id.btn_prev:
                    page--;
                    if (page < 0)
                        page = 0;
                    showPage();
                    break;
                case R.id.btn_next:

                    if (checkBtn.getText().equals(getResources().getString(R.string.check))) {
                        if (answer < 0) {
                            Toast.makeText(this, R.string.click_answer, Toast.LENGTH_SHORT).show();
                            return;
                        }

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
                    exerciseInit();
                    startStudy();
                    break;
                case R.id.result_again_btn:
                    if (isFinish()) {
                        goHome();
                        return;
                    }
                    exercises = assembleWrongExercises();
                    page = 0;
                    end = false;
                    startStudy();
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
                                    Dialog.showSettingGroupNameView(MultipleActivity.this, new ISettingGroupNameViewHanlder() {
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
                                    // No button clicked
                                    dbMgr.setWrongExercises(exercises);

                                    Intent intent = new Intent();
                                    intent.setClass(getApplicationContext(), GroupActivity.class);
                                    intent.putExtra(CommonData.INTENT_KEY_PROBLEM_TYPE, CommonData.INTENT_VALUE_MULTIPLE);
                                    startActivity(intent);
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(MultipleActivity.this);
                    builder.setMessage(R.string.result_save_mention);
                    builder.setPositiveButton(R.string.result_save_new, dialogClickListener);
                    builder.setNegativeButton(R.string.result_save_old, dialogClickListener);
                    builder.show();

                    break;
            }
        }
    }
}