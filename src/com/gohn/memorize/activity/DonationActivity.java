package com.gohn.memorize.activity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.vending.billing.IInAppBillingService;
import com.gohn.memorize.R;
import com.gohn.memorize.util.billing.IabHelper;
import com.gohn.memorize.util.billing.IabResult;
import com.gohn.memorize.util.billing.Inventory;
import com.gohn.memorize.util.billing.Purchase;

public class DonationActivity extends BaseActivity {

	IabHelper mHelper;
	IInAppBillingService mService;
	ServiceConnection mServiceConn;
	static int RC_REQUEST = 10001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.donation_activity_layout);

		Init(true);
	}

	void InitView() {

		ArrayList<String> list = new ArrayList<String>();
		list.add("donation_1000");
		list.add("donation_3000");
		list.add("donation_5000");
		list.add("donation_10000");
		JSONObject products = RequestItem(list);

		ArrayList<Button> btnList = new ArrayList<Button>();
		btnList.add((Button) findViewById(R.id.donation_1000_btn));
		btnList.add((Button) findViewById(R.id.donation_3000_btn));
		btnList.add((Button) findViewById(R.id.donation_5000_btn));
		btnList.add((Button) findViewById(R.id.donation_10000_btn));

		try {
			for (int i = 0; i < btnList.size(); i++) {
				String a = products.getString(list.get(i));
				btnList.get(i).setText(a);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

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

		mServiceConn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				mService = null;
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mService = IInAppBillingService.Stub.asInterface(service);
				InitView();
			}
		};
		bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"), mServiceConn, Context.BIND_AUTO_CREATE);
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
				// Log.d("gohn", "Setup successful. Querying inventory.");
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});
	}

	// Listener that's called when we finish querying the items and
	// subscriptions we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
			// Log.d("gohn", "Query inventory finished.");

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
				// Log.d("gohn", "reconsume : " + purchase.getSku());
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
			// Log.d("gohn", "Purchase finished: " + result + ", purchase: " +
			// purchase);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;

			if (result.isFailure()) {
				complain("Error purchasing: " + result);
				return;
			}

			// Log.d("gohn", "Purchase successful.");

			mHelper.consumeAsync(purchase, mConsumeFinishedListener);
		}
	};

	// Called when consumption is complete
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			// Log.d("gohn", "Consumption finished. Purchase: " + purchase +
			// ", result: " + result);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;
			if (result.isSuccess()) {
				// Log.d("gohn", "Consumption successful");
			} else {
				complain("Error while consuming: " + result);
			}

			// Log.d("gohn", "End consumption flow.");
		}
	};

	void complain(String message) {
		// Log.e("gohn", "**** InApp Purchase Error : " + message);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Log.d("gohn", "onActivityResult(" + requestCode + "," + resultCode +
		// "," + data);
		if (mHelper == null)
			return;

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			// Log.d("gohn", "onActivityResult handled by IABUtil.");
		}

		// super.onActivityResult(requestCode, resultCode, data);
		// gameHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// very important:
		// Log.d("gohn", "Destroying helper.");
		if (mHelper != null) {
			mHelper.dispose();
			mHelper = null;
		}
		if (mService != null) {
			unbindService(mServiceConn);
		}
	}

	public JSONObject RequestItem(ArrayList<String> list) {

		Bundle querySkus = new Bundle();
		querySkus.putStringArrayList("ITEM_ID_LIST", list);

		JSONObject items = new JSONObject();

		try {
			Bundle skuDetails = mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);

			int response = skuDetails.getInt("RESPONSE_CODE");
			if (response == 0) {
				ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");

				for (String thisResponse : responseList) {
					JSONObject object;
					try {
						object = new JSONObject(thisResponse);
						String sku = object.getString("productId");
						String price = object.getString("price_amount_micros");
						String locale = object.getString("price_currency_code");
						Log.d("gohn", sku + " : " + locale + " " + Long.parseLong(price) / 1000000);
						items.put(sku, locale + " " + Long.parseLong(price) / 1000000);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return items;
	}
}
