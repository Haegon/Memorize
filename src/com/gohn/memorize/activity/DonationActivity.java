package com.gohn.memorize.activity;

import android.os.Bundle;
import android.view.View;

import com.gohn.memorize.R;

public class DonationActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.donation_activity_layout);
	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.donation_1000_btn:
			break;
		case R.id.donation_3000_btn:
			break;
		case R.id.donation_5000_btn:
			break;
		case R.id.donation_10000_btn:
			break;
		}
	}
}
