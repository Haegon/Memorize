package com.gohn.memorize.activity;

import java.util.Locale;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
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

		mAdapter = new GroupAdapter(this, dbMgr.getVocaGroups(), getResources().getString(R.string.main_word));

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

				CharSequence s[] = new CharSequence[] { getResources().getString(R.string.group_delete), getResources().getString(R.string.group_chance), getResources().getString(R.string.cancel) };

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle(R.string.group_settings);
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

		if (mAdapter.getCount() == 0 && Locale.KOREA.getCountry().equals(getResources().getConfiguration().locale.getCountry())) {
			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setMessage("단어장에 등록된 단어가 없습니다. \n설정 화면에서 기본 단어를 추가해 주세요.").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// do things
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		reloadView();
	}

	public void reloadView() {
		mAdapter.mData = dbMgr.getVocaGroups();
		mAdapter.notifyDataSetChanged();
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

	@Override
	public void onBackPressed() {

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					finish();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setMessage(R.string.exit).setPositiveButton(R.string.yes, dialogClickListener).setNegativeButton(R.string.no, dialogClickListener).show();
	}

}
