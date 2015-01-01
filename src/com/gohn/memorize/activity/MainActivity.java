package com.gohn.memorize.activity;

import java.io.File;
import java.util.ArrayList;

import com.gohn.memorize.R;
import com.gohn.memorize.R.id;
import com.gohn.memorize.R.layout;
import com.gohn.memorize.adpator.GroupAdapter;
import com.gohn.memorize.manager.WordsDBMgr;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

//	Context context = this;
//
	View mHeaderView = null;
	View mFooterView = null;
//
	ListView mListView = null;
	GroupAdapter mAdapter = null;
//
//	String mCurrentDir = "/mnt/sdcard";
//
	public WordsDBMgr dbMgr = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dbMgr = WordsDBMgr.getInstance(this);

		mAdapter = new GroupAdapter(this, new ArrayList<File>());

		mListView = (ListView) findViewById(R.id.list_view);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				File file = mAdapter.mData.get(position);

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
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.add_btn:
			startActivity(new Intent(this,FindFileActivity.class));
			break;
		}
	}
}
