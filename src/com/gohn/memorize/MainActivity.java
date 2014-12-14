package com.gohn.memorize;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	View mHeaderView = null;
	View mFooterView = null;

	ListView mListView = null;
	BaseAdapterEx mAdapter = null;

	String mCurrentDir = "/mnt/sdcard";

	public ArrayList<File> GetFiles(String DirectoryPath) {
		ArrayList<File> MyFiles = new ArrayList<File>();

		File f = new File(DirectoryPath);
		f.mkdirs();
		File[] files = f.listFiles();
		File parent = f.getParentFile();

		MyFiles.add(parent);

		Arrays.sort(files);

		for (int i = 0; i < files.length; i++) {
			if (!files[i].isHidden() && files[i].isDirectory()) {
				MyFiles.add(files[i]);
			}
		}
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isHidden() && !files[i].isDirectory()) {
				MyFiles.add(files[i]);
			}
		}

		return MyFiles;
	}

	public String ParentDir(String child) {

		int index = 1;

		for (int i = -1; (i = child.indexOf("/", i + 1)) != -1;) {
			index = i;
		}
		if (index == 4)
			return child;

		return child.substring(0, index);
	}

	private void showPrev() {
		mCurrentDir = ParentDir(mCurrentDir);
		mAdapter.mData = GetFiles(mCurrentDir);
		mAdapter.notifyDataSetChanged();
	}

	private void showNext(String dir) {
		mCurrentDir = mCurrentDir + "/" + dir;
		mAdapter.mData = GetFiles(mCurrentDir);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mAdapter = new BaseAdapterEx(this, GetFiles(mCurrentDir));

		mListView = (ListView) findViewById(R.id.list_view);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					showPrev();
					return;
				}

				if (!mAdapter.mData.get(position).isDirectory())
					return;

				showNext(mAdapter.mData.get(position).getName());
			}

		});

		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// mAdapter.delete((int) id);
				return true;
			}

		});

		mListView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this,
						"onItemSelected Item : " + position + ", " + id,
						Toast.LENGTH_LONG).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "OnNothing Selected",
						Toast.LENGTH_LONG).show();
			}
		});

		// mListView.setDivider(new ColorDrawable(Color.RED));
		// mListView.setDividerHeight(10);

		// // ==========================================================
		//
		// mHeaderView = getLayoutInflater().inflate(
		// R.layout.list_view_header_footer_layout, null);
		//
		// TextView headerTV = (TextView) mHeaderView
		// .findViewById(R.id.header_footer_text);
		//
		// headerTV.setText("리스트의 시작입니다");
		// mListView.addHeaderView(mHeaderView, null, true);
		//
		// mFooterView = getLayoutInflater().inflate(
		// R.layout.list_view_header_footer_layout, null);
		//
		// TextView footerTV = (TextView) mFooterView
		// .findViewById(R.id.header_footer_text);
		//
		// footerTV.setText("리스트의 끝입니다");
		// mListView.addFooterView(mFooterView, null, false);
		//
		// // ==========================================================

	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.add_btn:
			// EditText nameEt = (EditText) findViewById(R.id.name_edit);
			// EditText numberEt = (EditText) findViewById(R.id.number_edit);
			// EditText departmentEt = (EditText)
			// findViewById(R.id.department_edit);
			//
			// Student addData = new Student();
			//
			// addData.mName = nameEt.getText().toString();
			// addData.mNumber = numberEt.getText().toString();
			// addData.mDepartment = departmentEt.getText().toString();
			//
			// mAdapter.add(0, addData);
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
