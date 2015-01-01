package com.gohn.memorize.activity;

import com.gohn.memorize.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CategoryActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_layout);
	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.add_btn:
			startActivityForResult(new Intent(this, FindFileActivity.class), 0);
			break;
		}
	}
}
