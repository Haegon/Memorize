package com.gohn.memorize.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.WordsDBMgr;
import com.gohn.memorize.model.ExerciseType;
import com.gohn.memorize.model.WordType;

public class TypeSelectActivity extends BaseActivity {

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
		exerciseType = getIntent().getExtras().getInt(ExerciseType.toStr());

		TextView btn_all = (TextView) findViewById(R.id.type_select_all_text);
		btn_all.setText(R.string.type_all);
		btn_all.append(" (" + dbMgr.getWordsCount(groupName, WordType.NONE) + ")");

		TextView btn_n = (TextView) findViewById(R.id.type_select_noun_text);
		btn_n.setText(R.string.type_n);
		btn_n.append(" (" + dbMgr.getWordsCount(groupName, WordType.NOUN) + ")");

		TextView btn_v = (TextView) findViewById(R.id.type_select_verb_text);
		btn_v.setText(R.string.type_v);
		btn_v.append(" (" + dbMgr.getWordsCount(groupName, WordType.VERB) + ")");

		TextView btn_a = (TextView) findViewById(R.id.type_select_adjective_text);
		btn_a.setText(R.string.type_a);
		btn_a.append(" (" + dbMgr.getWordsCount(groupName, WordType.ADJECTIVE) + ")");

		TextView btn_ad = (TextView) findViewById(R.id.type_select_adverb_text);
		btn_ad.setText(R.string.type_ad);
		btn_ad.append(" (" + dbMgr.getWordsCount(groupName, WordType.ADVERB) + ")");

		TextView btn_e = (TextView) findViewById(R.id.type_select_etc_text);
		btn_e.setText(R.string.type_e);
		btn_e.append(" (" + dbMgr.getWordsCount(groupName, WordType.ETC) + ")");
	}

	public void alertZero() {
		AlertDialog.Builder alert = new AlertDialog.Builder(TypeSelectActivity.this);
		alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.setMessage("해당 유형으로 등록된 단어가 없어 테스트를 진행 할 수 없습니다.\n모든 단어 테스트를 이용하시거나 새로운 단어장을 추가해주세요.");
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

		switch (getIntent().getExtras().getInt(ExerciseType.toStr())) {
		case R.id.category_study_btn:
			intent.setClass(this, StudyActivity.class);
			break;
		case R.id.category_find_meaning_btn:
		case R.id.category_find_word_btn:
			intent.putExtra(ExerciseType.toStr(), exerciseType);
			intent.setClass(this, ChoiceProblemActivity.class);
			break;
		case R.id.category_write_word_btn:
			intent.setClass(this, WriteProblemActivity.class);
			break;
		}

		if (zero)
			alertZero();
		else
			startActivityForResult(intent, 3);
	}
}
