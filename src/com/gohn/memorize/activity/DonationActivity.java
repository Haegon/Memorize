package com.gohn.memorize.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import com.gohn.memorize.R;
import com.gohn.memorize.util.IabHelper;
import com.gohn.memorize.util.IabResult;
import com.gohn.memorize.util.Inventory;
import com.gohn.memorize.util.Purchase;

public class DonationActivity extends BaseActivity {

	public IabHelper mHelper;
	static int RC_REQUEST = 10001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.donation_activity_layout);
		
		Init(true);
	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.donation_1000_btn:
			BuyItem("donation_1000");
			break;
		case R.id.donation_3000_btn:
			BuyItem("donation_3000");
			break;
		case R.id.donation_5000_btn:
			BuyItem("donation_5000");
			break;
		case R.id.donation_10000_btn:
			BuyItem("donation_10000");
			break;
		}
	}

	public void Init(boolean isDebug) {

		mHelper = new IabHelper(
				this,
				"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkrMwxshv5dTSCg0zNddExEnrRUUfVcDK/V8B3gSaBXqpaVm4TSgdFR9f45S9jWJXX8fE5j3IVRZq0XCLcLls/FyDY33KxmV8WqnI0XpKsPD8uuI+26IO8Jb6XrBb31YkJg9BZjc41u5EsFMUHM0IQMZU56vs62TZt4b7qrqXfXaPHkwNKsqnpzu+0Flj4vilV30100yKp6TW9cVP29OzQLG0UhdfJXTfTI/ejChd7U6c/7v5TqYP+cFqW87VaOQOV6xZUOOHMaqh3reem34QLQTaYoO5FZ3q/DEZJ7uHae8SAPV9Ed/Qgb2CihpNeVW2M2uJVJMJAvnBoefh7+pW/wIDAQAB");
		mHelper.enableDebugLogging(isDebug);

		HelperStart();
	}

	public void HelperStart() {

		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

			@Override
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					complain("Problem setting up in-app billing: " + result);
					return;
				}

				// Have we been disposed of in the meantime? If so, quit.
				if (mHelper == null)
					return;

				// IAB is fully set up. Now, let's get an inventory of stuff we
				// own.
				Log.d("gohn", "Setup successful. Querying inventory.");
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});
	}

	// Listener that's called when we finish querying the items and
	// subscriptions we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
			Log.d("gohn", "Query inventory finished.");

			// Have we been disposed of in the meantime? If so, quit.
			if (mHelper == null)
				return;

			// Is it a failure?
			if (result.isFailure()) {
				complain("Failed to query inventory: " + result);
				return;
			}

			inventory.getDump();

			ArrayList<Purchase> mPurchaseList = inventory.getPurchases();

			for (Purchase purchase : mPurchaseList) {
				Log.d("gohn", "reconsume : " + purchase.getSku());
				mHelper.consumeAsync(purchase, mConsumeFinishedListener);
			}
		}
	};

	public void BuyItem(String item_name) {
		// Log.d(TAG, "Buy button clicked.");
		mHelper.launchPurchaseFlow(this, item_name, RC_REQUEST, mPurchaseFinishedListener, "Memorize Payload");
	}

	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			Log.d("gohn", "Purchase finished: " + result + ", purchase: " + purchase);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;

			if (result.isFailure()) {
				complain("Error purchasing: " + result);
				return;
			}

			Log.d("gohn", "Purchase successful.");

			mHelper.consumeAsync(purchase, mConsumeFinishedListener);
		}
	};

	// Called when consumption is complete
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			Log.d("gohn", "Consumption finished. Purchase: " + purchase + ", result: " + result);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;
			if (result.isSuccess()) {
				Log.d("gohn", "Consumption successful");
			} else {
				complain("Error while consuming: " + result);
			}

			Log.d("gohn", "End consumption flow.");
		}
	};

	void complain(String message) {
		Log.e("gohn", "**** InApp Purchase Error : " + message);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("gohn", "onActivityResult(" + requestCode + "," + resultCode + "," + data);
		if (mHelper == null)
			return;

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			Log.d("gohn", "onActivityResult handled by IABUtil.");
		}

		// super.onActivityResult(requestCode, resultCode, data);
		// gameHelper.onActivityResult(requestCode, resultCode, data);
	}

//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//
//		// very important:
//		Log.d("gohn", "Destroying helper.");
//		if (mHelper != null) {
//			mHelper.dispose();
//			mHelper = null;
//		}
//	}
}
