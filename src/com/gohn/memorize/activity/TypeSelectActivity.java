package com.gohn.memorize.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.WordsDBMgr;
import com.gohn.memorize.model.ExerciseType;
import com.gohn.memorize.model.WordSet;
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
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.setMessage(R.string.no_voca);
		alert.show();
	}

	public void onClick(View v) {

		final Intent intent = new Intent();
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

		if (zero) {
			alertZero();
			return;
		}
		
		switch (getIntent().getExtras().getInt(ExerciseType.toStr())) {
		case R.id.category_study_btn:
			
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					// 블라인드 모드
					case DialogInterface.BUTTON_POSITIVE:
						intent.putExtra("mode", "blind");
						break;
					// 오픈 모드 
					case DialogInterface.BUTTON_NEGATIVE:
						intent.putExtra("mode", "open");						
						break;
					}
					intent.setClass(TypeSelectActivity.this, StudyActivity.class);
					startActivityForResult(intent, 3);
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(TypeSelectActivity.this);
			builder.setMessage(R.string.type_study_mention).setPositiveButton(R.string.type_blind, dialogClickListener).setNegativeButton(R.string.type_open, dialogClickListener).show();
			
			
			break;
		case R.id.category_find_meaning_btn:
		case R.id.category_find_word_btn:
			intent.putExtra(ExerciseType.toStr(), exerciseType);
			intent.setClass(this, ChoiceProblemActivity.class);
			startActivityForResult(intent, 3);
			break;
		case R.id.category_write_word_btn:
			intent.setClass(this, WriteProblemActivity.class);
			startActivityForResult(intent, 3);
			break;
		}
	}
}
