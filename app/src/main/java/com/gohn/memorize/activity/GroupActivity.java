package com.gohn.memorize.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gohn.memorize.R;
import com.gohn.memorize.activity.base.ActionBarActivity;
import com.gohn.memorize.adapter.GroupAdapter;
import com.gohn.memorize.common.CommonData;
import com.gohn.memorize.manager.DBMgr;
import com.gohn.memorize.model.WordSet;

import java.util.ArrayList;

public class GroupActivity extends ActionBarActivity {
	ListView mListView = null;
	GroupAdapter mAdapter = null;

	public DBMgr dbMgr = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_group);

		dbMgr = DBMgr.getInstance();

		final boolean isWrite = getIntent().getExtras().getString(CommonData.INTENT_KEY_PROBLEM_TYPE).
				equals(CommonData.INTENT_VALUE_WRITE) ? true : false;

		mAdapter = new GroupAdapter(this, dbMgr.getVocaGroups(), getResources().getString(R.string.main_word));

		mListView = (ListView) findViewById(R.id.list_group);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								final ArrayList<WordSet> words = new ArrayList<WordSet>();
								if (isWrite) {
									for (int i = 0; i < dbMgr.getWrongExercisesWrite().size(); i++) {
										words.add(dbMgr.getWrongExercisesWrite().get(i).getQuestion());
									}
								} else {
									for (int i = 0; i < dbMgr.getWrongExercises().size(); i++) {
										words.add(dbMgr.getWrongExercises().get(i).getQuestion());
									}
								}
								dbMgr.addWordsToDB(mAdapter.mData.get(position).getName(), words);
								goHome();
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								break;
						}
					}
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivity.this);
				builder.setMessage(String.format("[ %s ] %s", mAdapter.mData.get(position).getName(), getResources().getString(R.string.voca_add_oldone)))
						.setPositiveButton(R.string.yes, dialogClickListener).setNegativeButton(R.string.no, dialogClickListener).show();
			}
		});
	}

	public void goHome() {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
	}
}
