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
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class FindFileActivity extends Activity {
	Context context = this;

	View mHeaderView = null;
	View mFooterView = null;

	ListView mListView = null;
	FindFileAdapter mAdapter = null;

	String mCurrentDir = "/mnt/sdcard";

	public WordsDBMgr dbMgr = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_file_activity_layout);

		dbMgr = WordsDBMgr.getInstance(this);

		mAdapter = new FindFileAdapter(this, GetFiles(mCurrentDir));

		mListView = (ListView) findViewById(R.id.find_file_list_view);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				File file = mAdapter.mData.get(position);

				if (!file.isDirectory()) {

					String ext = file.getName().substring(file.getName().lastIndexOf("."));
					if (ext.contains(".xls") && !ext.contains(".xlsx")) {
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

						final ArrayList<WordSet> words = new ArrayList<WordSet>();

						for (int r = 0; r < sheet.getRows(); r++) {

							WordSet word = new WordSet();

							for (int c = 0; c < sheet.getColumns(); c++) {
								Cell cell = sheet.getCell(c, r);
								if (cell.getType() == CellType.LABEL) {

									String content = cell.getContents();

									switch (c) {
									case 0:
										if (!WordType.isType(content))
											continue;
										word.Type = content;
										break;
									case 1:
										word.Word = content;
										break;
									case 2:
										word.Meaning = content;
										break;
									}
								}
							}
							words.add(word);
						}

						LayoutInflater li = LayoutInflater.from(context);
						View promptsView = li.inflate(R.layout.activity_group_edittext, null);
						final EditText result = null;

						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

						// set prompts.xml to alertdialog builder
						alertDialogBuilder.setView(promptsView);

						final EditText userInput = (EditText) promptsView.findViewById(R.id.set_group_edittext);

						// set dialog message
						alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								addWordsToDB(userInput.getText().toString(), words);
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
				Toast.makeText(FindFileActivity.this, "onItemSelected Item : " + position + ", " + id, Toast.LENGTH_LONG).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				Toast.makeText(FindFileActivity.this, "OnNothing Selected", Toast.LENGTH_LONG).show();
			}
		});
	}

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

	private void addWordsToDB(String group, ArrayList<WordSet> set) {

		for (int i = 0; i < set.size(); i++) {
			ContentValues cv = new ContentValues();
			cv.put(WordsDBMgr.GROUP, group);
			cv.put(WordsDBMgr.TYPE, set.get(i).Type);
			cv.put(WordsDBMgr.WORD, set.get(i).Word);
			cv.put(WordsDBMgr.MEANING, set.get(i).Meaning);
			dbMgr.insert(cv);
		}

		Log.d("gohn", "Success");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

}
