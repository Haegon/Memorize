package com.gohn.memorize.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.gohn.memorize.R;
import com.gohn.memorize.adpator.GroupAdapter;
import com.gohn.memorize.manager.WordsDBMgr;
import com.gohn.memorize.model.WordSet;

public class SelectGroupActivity extends BaseActivity {
	Context context = this;

	View mHeaderView = null;
	View mFooterView = null;

	ListView mListView = null;
	GroupAdapter mAdapter = null;

	public WordsDBMgr dbMgr = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_group_activity_layout);

		dbMgr = WordsDBMgr.getInstance(this);

		mAdapter = new GroupAdapter(this, dbMgr.getVocaGroups());

		mListView = (ListView) findViewById(R.id.select_group_list);
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
							for (int i = 0; i < dbMgr.worngExercises.size(); i++) {
								words.add(dbMgr.worngExercises.get(i).Question);
							}
							dbMgr.addWordsToDB(mAdapter.mData.get(position).Name, words);
							goHome();
							break;

						case DialogInterface.BUTTON_NEGATIVE:
							break;
						}
					}
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(SelectGroupActivity.this);
				builder.setMessage(String.format("[ %s ] 에 틀린단어를 추가 하시겠습니까?", mAdapter.mData.get(position).Name))
					.setPositiveButton("예", dialogClickListener)
					.setNegativeButton("아니오", dialogClickListener).show();
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
