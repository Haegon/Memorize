package com.gohn.memorize.activity.Learn;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.DBMgr;
import com.gohn.memorize.model.ExerciseType;
import com.gohn.memorize.model.WordSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class StudyActivity extends LearnActivity implements View.OnClickListener {

	DBMgr dbMgr;

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

		dbMgr = DBMgr.getInstance();
		Bundle b = getIntent().getExtras();
		groupName = b.getString(DBMgr.GROUP);
		exerciseType = b.getInt(ExerciseType.toStr());
		wordType = b.getString(DBMgr.TYPE);
		isBlind = b.getString("mode").equals("blind") ? true : false;

		fileName = groupName + "|" + exerciseType + "|" + wordType + "|" + isBlind;

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
//					setContentView(R.layout.study_activity_layout);
//					viewInit();
//					showPage();
//				}
//			};
//
//			AlertDialog.Builder builder = new AlertDialog.Builder(StudyActivity.this);
//			builder.setMessage(R.string.load_save).setPositiveButton(R.string.yes, dialogClickListener).setNegativeButton(R.string.no, dialogClickListener).show();
//		} else {
//        View newView = LayoutInflater.from(this).inflate(R.layout.content_study, null);
//        contentView.addView(newView);
			setContentView(R.layout.content_study);
			exerciseInit();
			viewInit();
			showPage();
//		}
	}

	public void exerciseInit() {

		wordsSet = dbMgr.getWordsSet(groupName, wordType);

//		if (Global.getInstance(getApplicationContext()).RandomProblem)
//			makeWordRandomly();
	}

	public void viewInit() {
		if (isBlind) {
			for (int i = 0; i < wordsSet.size(); i++) {
				wordsSet.get(i).setOpen(false);
			}
		}

		nextBtn = (Button) findViewById(R.id.btn_next);
		count = (TextView) findViewById(R.id.study_word_count);
		word = (TextView) findViewById(R.id.study_word_text);
		meaning = (TextView) findViewById(R.id.study_meaning_text);

        nextBtn.setOnClickListener(this);
        findViewById(R.id.btn_prev).setOnClickListener(this);
    }

	public void showPage() {
		if (wordsSet.get(page).isOpen()) {
			meaning.setText(wordsSet.get(page).getMeaning());
			nextBtn.setText(R.string.next);
		} else {
			meaning.setText("");
			nextBtn.setText(R.string.check);
		}

		count.setText((page + 1) + " / " + wordsSet.size());
		word.setText(wordsSet.get(page).getWord());
	}

	public void showResult() {
//		AdBuddiz.showAd(learnActivity);
//		deleteCurrentState();
		setContentView(R.layout.content_study_finish);

        findViewById(R.id.study_home_btn).setOnClickListener(this);
        findViewById(R.id.study_restart_btn).setOnClickListener(this);
	}

	public void makeWordRandomly() {
		long seed = System.nanoTime();
		Collections.shuffle(wordsSet, new Random(seed));
	}

	public boolean isFinish() {
		return page == wordsSet.size();
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
				// 정답확인을 안한 경우는 확인 버튼역할을 하고 확인한경우는 다음문제
				saveCurrentState();
				if (wordsSet.get(page).isOpen()) {
					page++;
					if (page >= wordsSet.size()) {
						page--;
						end = true;
						showResult();
					} else {
						showPage();
					}
				} else {
					wordsSet.get(page).setOpen(true);
					showPage();
				}
				break;
			case R.id.study_restart_btn:
				page = 0;
				setContentView(R.layout.content_study);
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

//		Thread t = new Thread() {
//			public void run() {
//				try {
//					Gson gson = new Gson();
//
//					// 마지막 문제는 현재 페이지 저장. 그전 문제는 다음 페이지 저장.
//					json.put("page", page + 1 == wordsSet.size() ? page : page + 1);
//					json.put("list", gson.toJson(wordsSet));
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
//			wordsSet = gson.fromJson(list, new TypeToken<ArrayList<WordSet>>() {
//			}.getType());
//
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}