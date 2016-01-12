package com.gohn.memorize.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Window;

import com.gohn.memorize.manager.PurchaseManager;
import com.gohn.memorize.util.GLog;

public class PurchaseActivity extends Activity {
    private BroadcastReceiver broadcastReceiver;

    public static final String ACTION_FINISH = "com.androidnative.billing.core.ACTION_FINISH";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Remove notification bar
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                GLog.Debug("Finish AN_BillingProxyActivity broadcast was received");
                if (!isFinishing()) {
                    finish();
                }
            }
        };
        registerReceiver(this.broadcastReceiver, new IntentFilter(ACTION_FINISH));

        Intent i = getIntent();
        String sku = i.getStringExtra(PurchaseManager.SKU);
        String developerPayload = i.getStringExtra(PurchaseManager.PAYLOAD);

        GLog.Debug("@@@ sku : " + sku);
        GLog.Debug("@@@ developerPayload : " + developerPayload);

        PurchaseManager.LaunchPurchaseFlow(this, sku, developerPayload);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.broadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        GLog.Debug("GoogleInAppBillingActivity::onActivityResult("
                + requestCode + ", " + resultCode + ", " + data);

        PurchaseManager.HandleActivityResult(requestCode, resultCode, data);
        finish();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
