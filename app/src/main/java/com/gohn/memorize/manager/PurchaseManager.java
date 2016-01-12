package com.gohn.memorize.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import com.gohn.memorize.activity.PurchaseActivity;
import com.gohn.memorize.util.GLog;
import com.gohn.memorize.util.billing.IabHelper;
import com.gohn.memorize.util.billing.IabResult;
import com.gohn.memorize.util.billing.Inventory;
import com.gohn.memorize.util.billing.Purchase;

import java.util.ArrayList;

/**
 * Created by Gohn on 2015. 11. 21..
 */
public class PurchaseManager {
    static int RC_REQUEST = 10001;
    public static String SKU = "INAPP_PURCHASE_SKU";
    public static String PAYLOAD = "INAPP_PURCHASE_PAYLOAD";

    private Context context;
    private Inventory mInventory = null;
    private IabHelper mHelper;

    private boolean isConnected = false;

    // 매니저 인스턴스.
    private static PurchaseManager _instance = null;

    private static PurchaseManager GetInstance() {
        if (_instance == null) {
            _instance = new PurchaseManager();
        }
        return _instance;
    }

    public Inventory GetInventory() {
        return mInventory;
    }

    // 초기화 되어잇는지 확인
    public static boolean IsConnected() {
        return GetInstance().isConnected();
    }

    // 초기화 할때 부르는 함수.
    public static void Connect(Context context,
                                   String base64EncodedPublicKey,
                                   boolean isDebug) {
        GetInstance().connect(context, base64EncodedPublicKey, isDebug);
    }

    public static void Purchase(Context context, String item_name, String payload) {
        GetInstance().purchase(context, item_name, payload);
    }

    public static void LaunchPurchaseFlow(Activity act, String sku, String developerPayload) {
        GetInstance().launchPurchaseFlow(act, sku, developerPayload);
    }

    public static void HandleActivityResult(int requestCode, int resultCode, Intent data) {
        GetInstance().handleActivityResult(requestCode,resultCode,data);
    }
    public boolean isConnected() {
        return isConnected;
    }

    public void connect(Context context, String base64EncodedPublicKey,
                        boolean isDebug) {
        this.context = context;

        // 인앱빌링 헬퍼 초기화
        this.mHelper = new IabHelper(context, base64EncodedPublicKey);
        this.mHelper.enableDebugLogging(isDebug);
        helperStart();


        GLog.Debug("Finish Connect");
    }

    public void helperStart() {
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // 헬퍼스타트를 실패 했을때 처
                    fail("Problem setting up in-app billing: " + result);
                    return;
                }

                // 헬퍼스타트 후 헬퍼가 널이 아닌지 확인.
                if (mHelper == null) {
                    fail("mHelper is Null");
                    return;
                }

                // 인앱 빌링 준비 끝.
                // 유저의 인벤토리 요청을한다.
                GLog.Debug("Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);

                isConnected = true;
            }
        });
    }

    // 인벤토리 요청 리스
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            GLog.Debug("Query inventory finished.");

            mInventory = inventory;

            // 헬퍼 널체크.
            if (mHelper == null) {
                fail("mHelper is Null");
                return;
            }

            // 실패했으면 ㅂㅂ
            if (result.isFailure()) {
                fail("Failed to query inventory: " + result);
                return;
            }

            inventory.getDump();

            // 소비되었는지 확인하고 소비안된 결제는 소비하는 부분
            ArrayList<Purchase> mPurchaseList = inventory.getPurchases();

            for (Purchase purchase : mPurchaseList) {
                GLog.Debug("reconsume : " + purchase.getSku());
                // 결제 완료되자마자 앱 강종하면 소비가 안되서 재구매가 불가능하다.
                // 헬퍼를 통해서 소비한다.
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }
            GLog.Debug("Finish Helper Start");
        }
    };

    // 구매 요청이 오면 결제 액티비티를 임시로 띄우고
    // 그 앱팁티 위에서 결제를 진행 하도록 한다.
    // 그 이유는 아래에 있는 mHelper의 launchPurchaseFlow 함수를
    // 액티비티 ui 스레드에서만 호출 해야만 한다..
    public void purchase(Context context, String item_name, String payload) {
        Intent i = new Intent(context, PurchaseActivity.class);
        i.putExtra(SKU, item_name);
        i.putExtra(PAYLOAD, payload);

        context.startActivity(i);
    }

    // 더미 액티비티에서 호출하는 결제 함수.
    public void launchPurchaseFlow(Activity act, String sku, String developerPayload) {
        try {
            mHelper.launchPurchaseFlow(act, sku, RC_REQUEST, mPurchaseFinishedListener, developerPayload);
        } catch (Exception ex) {
            fail("launchPurchaseFlow Exception");
            context.sendBroadcast(new Intent(PurchaseActivity.ACTION_FINISH));
        }
    }

    // 처리 결과를 넘긴다.
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper != null) {
            mHelper.handleActivityResult(requestCode, resultCode, data);
        }
    }

    // 구매가 종료되면 이 리스너가 불린다.
    public IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            GLog.Debug("Purchase finished: " + result + ", purchase: "
                    + purchase);

            // 헬퍼 널 체크
            if (mHelper == null) {
                fail("mHelper is Null");
                sendResult(result);
                return;
            }

            // 구매 실패..
            if (result.isFailure()) {
                fail("Error purchasing: " + result);
                sendResult(result);
                return;
            }

            GLog.Debug("Purchase successful.");
            GLog.Debug("item consumption ready.");

            // 바로 소비 해주자.
            mHelper.consumeAsync(purchase, mConsumeFinishedListener);
        }
    };

    // 소비가 되면 불리는 리스
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        @Override
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            GLog.Debug("Consumption finished. Purchase: " + purchase
                    + ", result: " + result);

            // 헬퍼 널 체크
            if (mHelper == null) {
                fail("mHelper is Null");
                sendResult(result);
                return;
            }

            // 성공
            if (result.isSuccess()) {
                sendResult(result);
            } else {
                fail("Error while consuming: " + result);
            }
        }
    };

    void sendResult(IabResult result) {

        int response = result.getResponse();

        if (response == 0) {
            GLog.Debug("Purchase Success!!!");
        } else if (response == -1005) {
            GLog.Debug("Purchase Cancelled!!!");
            new AlertDialog.Builder(context)
                    .setTitle("안내")
                    .setMessage("결제를 취소하셨습니다.")
                    .setPositiveButton("확인", null)
                    .show();
        } else {
            GLog.Debug("Error!!! Purchase Response : " + response);
            new AlertDialog.Builder(context)
                    .setTitle("오류")
                    .setMessage("결제 도중 오류가 발생했습니다. 다시 시도해주세.")
                    .setPositiveButton("확인", null)
                    .show();
        }
    }

    void fail(String msg) {
        GLog.Debug("fail " + msg);
    }

    public void dispose() {
        if (this.mHelper != null) {
            this.mHelper.dispose();
        }
        _instance = null;
        this.mHelper = null;
    }
}
