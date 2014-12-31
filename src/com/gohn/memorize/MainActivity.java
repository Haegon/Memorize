package com.gohn.memorize;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
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
				File file = mAdapter.mData.get(position);

				if (!file.isDirectory()) {

					String ext = file.getName().substring(
							file.getName().lastIndexOf("."));
					if (ext.contains("xls")) {
						Log.d("gohn", "excel : " + file.getAbsolutePath());
						
						File inputWorkbook = new File(file.getAbsolutePath());
						Workbook w = null;

						try {
							w = Workbook.getWorkbook(inputWorkbook);
						} catch (BiffException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// Get the first sheet
						Sheet sheet = w.getSheet(0);
						// Loop over first 10 column and lines

						for (int i = 0; i < sheet.getRows(); i++) {
							for (int j = 0; j < sheet.getColumns(); j++) {
								Cell cell = sheet.getCell(j, i);
								CellType type = cell.getType();
								if (cell.getType() == CellType.LABEL) {

									switch (j) {
									case 0:
										Log.d("gohn",
												"Type : " + cell.getContents());
										break;
									case 1:
										Log.d("gohn",
												"단어 : " + cell.getContents());
										break;
									case 2:
										Log.d("gohn",
												"뜻   : " + cell.getContents());
										break;

									}
								}
							}
						}
					}
					return;
				}

				if (position == 0) {
					showPrev();
					return;
				}

				showNext(file.getName());
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
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
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
}
