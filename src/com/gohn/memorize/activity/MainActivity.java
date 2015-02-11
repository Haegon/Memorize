package com.gohn.memorize.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.gohn.memorize.R;
import com.gohn.memorize.adpator.GroupAdapter;
import com.gohn.memorize.manager.WordsDBMgr;
import com.gohn.memorize.model.VocaGroup;

public class MainActivity extends BaseActivity {

	Context context = this;

	View mHeaderView = null;
	View mFooterView = null;

	ListView mListView = null;
	GroupAdapter mAdapter = null;

	public WordsDBMgr dbMgr = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dbMgr = WordsDBMgr.getInstance(this);

		mAdapter = new GroupAdapter(this, getVocaGroups());

		mListView = (ListView) findViewById(R.id.group_list_view);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Intent intent = new Intent(context, CategoryActivity.class);
				intent.putExtra(WordsDBMgr.GROUP, mAdapter.mData.get(position).Name);

				startActivityForResult(intent, 1);
			}

		});

		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				final int p = position;

				CharSequence s[] = new CharSequence[] { "삭제", "이름 바꾸기", "취소" };

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("단어장 설정");
				builder.setItems(s, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						// 삭제버튼을 눌렀을때
						case 0:
							WordsDBMgr db = WordsDBMgr.getInstance(context);
							db.delete(WordsDBMgr.GROUP + "=?", new String[] { mAdapter.mData.get(p).Name });
							reloadView();
							break;
						// 이름변경 버튼을 눌렀을때
						case 1:
							LayoutInflater li = LayoutInflater.from(context);
							View promptsView = li.inflate(R.layout.activity_group_edittext, null);

							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

							// set prompts.xml to alertdialog builder
							alertDialogBuilder.setView(promptsView);

							final EditText userInput = (EditText) promptsView.findViewById(R.id.set_group_edittext);

							// set dialog message
							alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									WordsDBMgr db = WordsDBMgr.getInstance(context);
									ContentValues cv = new ContentValues();
									cv.put(WordsDBMgr.GROUP, userInput.getText().toString());
									db.update(cv, WordsDBMgr.GROUP + "=?", new String[] { mAdapter.mData.get(p).Name });
									reloadView();
								}
							}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});

							// create alert dialog
							AlertDialog alertDialog = alertDialogBuilder.create();

							// show it
							alertDialog.show();
							break;
						case 2:
							dialog.cancel();
							break;
						}
					}
				});
				builder.show();
				mAdapter.notifyDataSetChanged();
				return true;
			}
		});

		mListView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "onItemSelected Item : " + position + ", " + id, Toast.LENGTH_LONG).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "OnNothing Selected", Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		reloadView();
	}

	public void reloadView() {
		mAdapter.mData = getVocaGroups();
		mAdapter.notifyDataSetChanged();
	}

	public ArrayList<VocaGroup> getVocaGroups() {

		String[] columns = new String[] { WordsDBMgr.GROUP, "count(" + WordsDBMgr.GROUP + ")" };
		Cursor c = dbMgr.query(columns, null, null, WordsDBMgr.GROUP, null, null);

		if (c != null) {
			ArrayList<VocaGroup> groups = new ArrayList<VocaGroup>();

			while (c.moveToNext()) {
				VocaGroup vg = new VocaGroup();
				vg.Name = c.getString(0);
				vg.Numbers = c.getInt(1);
				groups.add(vg);
			}
			return groups;
		}
		return null;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.add_btn:
			startActivityForResult(new Intent(this, FindFileActivity.class), 0);
			break;
		case R.id.help_btn:
			startActivityForResult(new Intent(this, HelpActivity.class), 0);
			break;
		case R.id.option_btn:
			startActivityForResult(new Intent(this, SettingsActivity.class), 0);
			break;
		}
	}
}
