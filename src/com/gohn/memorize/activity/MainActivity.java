package com.gohn.memorize.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Toast;

import com.gohn.memorize.R;
import com.gohn.memorize.adpator.GroupAdapter;
import com.gohn.memorize.manager.WordsDBMgr;
import com.gohn.memorize.model.VocaGroup;

public class MainActivity extends Activity {

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
				// TODO Auto-generated method stub
				// mAdapter.delete((int) id);
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
		}
	}
}
