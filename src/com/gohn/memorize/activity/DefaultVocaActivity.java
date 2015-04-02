package com.gohn.memorize.activity;

import java.io.IOException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.gohn.memorize.R;
import com.gohn.memorize.model.WordSet;
import com.gohn.memorize.util.ReadXlsx;

public class DefaultVocaActivity extends BaseActivity {

	ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.free_add_activity_layout);
	}

	public void onClick(final View v) {
		final Button btn = (Button) findViewById(v.getId());
		
		// 동일한 단어장 이름이 있을때
		if (dbMgr.getGroupNames().contains(btn.getText().toString())) {
			AlertDialog.Builder builder = new AlertDialog.Builder(DefaultVocaActivity.this);
			builder.setMessage(R.string.find_duplicate).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// do things
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
			return;
		}
		
		// 단어장을 불러오고 있다는 진행 바를 보여줌.
		final LayoutInflater li = LayoutInflater.from(DefaultVocaActivity.this);
		final View popupView = li.inflate(R.layout.progress_loading, null);
		AlertDialog.Builder ad = new AlertDialog.Builder(DefaultVocaActivity.this);
		ad.setView(popupView);
		final AlertDialog alertDialog = ad.create();
		alertDialog.show();
		
		new Thread(new Runnable() {
			public void run() {
				String name = getResources().getResourceEntryName(v.getId());
				ArrayList<WordSet> words = new ArrayList<WordSet>();

				try {
					words = ReadXlsx.readExcel(getAssets().open(name + ".xlsx"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dbMgr.addWordsToDB(btn.getText().toString(), words);
				alertDialog.dismiss();
			}
		}).start();
	}
}
