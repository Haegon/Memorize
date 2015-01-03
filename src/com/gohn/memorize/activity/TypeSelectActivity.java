package com.gohn.memorize.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.WordsDBMgr;
import com.gohn.memorize.model.ExerciseType;
import com.gohn.memorize.model.WordType;

public class TypeSelectActivity extends Activity {

	String groupName = "";
	int exerciseType;
	WordsDBMgr dbMgr = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.type_select_activity_layout);

		dbMgr = WordsDBMgr.getInstance(this);
		groupName = getIntent().getExtras().getString(WordsDBMgr.GROUP);
		exerciseType = getIntent().getExtras().getInt("ExerciseType");

		Button btn_all = (Button) findViewById(R.id.type_select_all_btn);
		btn_all.setText("모두 (" + dbMgr.getWordsCount(groupName, WordType.NONE) + ")");
		Button btn_n = (Button) findViewById(R.id.type_select_noun_btn);
		btn_n.setText("명사 (" + dbMgr.getWordsCount(groupName, WordType.NOUN) + ")");
		Button btn_v = (Button) findViewById(R.id.type_select_verb_btn);
		btn_v.setText("동사 (" + dbMgr.getWordsCount(groupName, WordType.VERB) + ")");
		Button btn_a = (Button) findViewById(R.id.type_select_adjective_btn);
		btn_a.setText("형용사 (" + dbMgr.getWordsCount(groupName, WordType.ADJECTIVE) + ")");
		Button btn_ad = (Button) findViewById(R.id.type_select_adverb_btn);
		btn_ad.setText("부사 (" + dbMgr.getWordsCount(groupName, WordType.ADVERB) + ")");
		Button btn_e = (Button) findViewById(R.id.type_select_etc_btn);
		btn_e.setText("기타 (" + dbMgr.getWordsCount(groupName, WordType.ETC) + ")");
	}

	public void alertZero() {
		AlertDialog.Builder alert = new AlertDialog.Builder(TypeSelectActivity.this);
		alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.setMessage("해당 유형으로 등록된 단어가 없어서 테스트를 진행 할 수 없습니다.");
		alert.show();
	}

	public void onClick(View v) {

		Intent intent = new Intent();
		intent.putExtra(WordsDBMgr.GROUP, groupName);

		boolean zero = false;

		switch (v.getId()) {
		case R.id.type_select_all_btn:
			if (dbMgr.getWordsCount(groupName, WordType.NONE) == 0)
				zero = true;
			intent.putExtra(WordsDBMgr.TYPE, WordType.NONE);
			break;
		case R.id.type_select_noun_btn:
			if (dbMgr.getWordsCount(groupName, WordType.NOUN) == 0)
				zero = true;
			intent.putExtra(WordsDBMgr.TYPE, WordType.NOUN);
			break;
		case R.id.type_select_verb_btn:
			if (dbMgr.getWordsCount(groupName, WordType.VERB) == 0)
				zero = true;
			intent.putExtra(WordsDBMgr.TYPE, WordType.VERB);
			break;
		case R.id.type_select_adjective_btn:
			if (dbMgr.getWordsCount(groupName, WordType.ADJECTIVE) == 0)
				zero = true;
			intent.putExtra(WordsDBMgr.TYPE, WordType.ADJECTIVE);
			break;
		case R.id.type_select_adverb_btn:
			if (dbMgr.getWordsCount(groupName, WordType.ADVERB) == 0)
				zero = true;
			intent.putExtra(WordsDBMgr.TYPE, WordType.ADVERB);
			break;
		case R.id.type_select_etc_btn:
			if (dbMgr.getWordsCount(groupName, WordType.ETC) == 0)
				zero = true;
			intent.putExtra(WordsDBMgr.TYPE, WordType.ETC);
			break;
		}

		switch (exerciseType) {
		case ExerciseType.GUESS_MEANING:
			intent.setClass(this, FindMeaningActivity.class);
			break;
		case ExerciseType.GUESS_WORD:
			intent.setClass(this, FindWordActivity.class);
			break;
		case ExerciseType.WRITE_WORD:
			intent.setClass(this, WriteWordActivity.class);
			break;
		}

		if (zero)
			alertZero();
		else
			startActivityForResult(intent, 3);
	}
}
