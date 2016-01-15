package com.gohn.memorize.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.gohn.memorize.activity.PurchaseActivity;
import com.gohn.memorize.util.billing.IabHelper;
import com.gohn.memorize.util.billing.IabResult;
import com.gohn.memorize.util.billing.Inventory;
import com.gohn.memorize.util.billing.Purchase;

import java.util.ArrayList;

/**
 * Created by Gohn on 2015. 11. 21..
 */
public class PurchaseManager {

    public interface OnResultListener {
        public void onSuccess(int resultCode, String message);

        public void onFail(int resultCode, String message);

        public void onChangedStatus(PURCHASE_STATUS status, IabResult result, Purchase purchase);
    }

    public enum PURCHASE_STATUS {
        READY_TO_CONNECT,
        CONNECTED,
        START_PURCHASE,
        FINISH_PURCHASE,
        START_CONSUME,
        FINISH_CONSUME,
        END_PROCESS,
    }

    static int RC_REQUEST = 10001;
    public static String SKU = "INAPP_PURCHASE_SKU";
    public static String PAYLOAD = "INAPP_PURCHASE_PAYLOAD";

    private Context context;
    private Inventory inventory = null;
    private IabHelper helper;
    private OnResultListener listener;

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
        return inventory;
    }

    // 초기화 되어잇는지 확인
    public static boolean IsConnected() {
        return GetInstance().isConnected();
    }

    // 초기화 할때 부르는 함수.
    public static void Connect(Context context,
                               String base64EncodedPublicKey,
                               boolean isDebug,
                               OnResultListener listener) {
        GetInstance().connect(context, base64EncodedPublicKey, isDebug, listener);
    }

    public static void Purchase(Context context, String item_name, String payload) {
        GetInstance().purchase(context, item_name, payload);
    }

    public static void LaunchPurchaseFlow(Activity act, String sku, String developerPayload) {
        GetInstance().launchPurchaseFlow(act, sku, developerPayload);
    }

    public static void HandleActivityResult(int requestCode, int resultCode, Intent data) {
        GetInstance().handleActivityResult(requestCode, resultCode, data);
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void connect(Context context,
                        String base64EncodedPublicKey,
                        boolean isDebug,
                        OnResultListener listener) {
        this.context = context;
        this.listener = listener;

        // 인앱빌링 헬퍼 초기화
        this.helper = new IabHelper(context, base64EncodedPublicKey);
        this.helper.enableDebugLogging(isDebug);
        helperStart();
    }

    public void helperStart() {
        listener.onChangedStatus(PURCHASE_STATUS.READY_TO_CONNECT, null, null);

        helper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // 헬퍼스타트를 실패 했을때 처
                    fail(result.getResponse(), "Problem setting up in-app billing: " + result);
                    return;
                }

                // 헬퍼스타트 후 헬퍼가 널이 아닌지 확인.
                if (helper == null) {
                    fail(result.getResponse(), "helper is Null");
                    return;
                }

                // 인앱 빌링 준비 끝.
                // 유저의 인벤토리 요청을한다.
                helper.queryInventoryAsync(mGotInventoryListener);

                isConnected = true;
            }
        });
    }

    // 인벤토리 요청 리스
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            PurchaseManager.this.inventory = inventory;

            // 헬퍼 널체크.
            if (helper == null) {
                fail(result.getResponse(), "helper is Null");
                return;
            }

            // 실패했으면 ㅂㅂ
            if (result.isFailure()) {
                fail(result.getResponse(), "Failed to query inventory: " + result);
                return;
            }

            inventory.getDump();

            // 소비되었는지 확인하고 소비안된 결제는 소비하는 부분
            ArrayList<Purchase> mPurchaseList = inventory.getPurchases();

            for (Purchase purchase : mPurchaseList) {
                // 결제 완료되자마자 앱 강종하면 소비가 안되서 재구매가 불가능하다.
                // 헬퍼를 통해서 소비한다.
                helper.consumeAsync(purchase, mConsumeFinishedListener);
            }
            listener.onChangedStatus(PURCHASE_STATUS.CONNECTED, result, null);
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

        listener.onChangedStatus(PURCHASE_STATUS.START_PURCHASE, null, null);

        context.startActivity(i);
    }

    // 더미 액티비티에서 호출하는 결제 함수.
    public void launchPurchaseFlow(Activity act, String sku, String developerPayload) {
        try {
            helper.launchPurchaseFlow(act, sku, RC_REQUEST, mPurchaseFinishedListener, developerPayload);
        } catch (Exception ex) {
            fail(-999, "launchPurchaseFlow Exception");
            context.sendBroadcast(new Intent(PurchaseActivity.ACTION_FINISH));
        }
    }

    // 처리 결과를 넘긴다.
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (helper != null) {
            helper.handleActivityResult(requestCode, resultCode, data);
        }
    }

    // 구매가 종료되면 이 리스너가 불린다.
    public IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            listener.onChangedStatus(PURCHASE_STATUS.FINISH_PURCHASE, result, purchase);

            // 헬퍼 널 체크
            if (helper == null) {
                fail(result.getResponse(), "helper is Null");
                return;
            }

            // 구매 실패..
            if (result.isFailure()) {
                fail(result.getResponse(), "Error purchasing: " + result);
                return;
            }

            listener.onChangedStatus(PURCHASE_STATUS.FINISH_PURCHASE, result, purchase);

            // 바로 소비 해주자.
            listener.onChangedStatus(PURCHASE_STATUS.START_CONSUME, result, purchase);
            helper.consumeAsync(purchase, mConsumeFinishedListener);
        }
    };

    // 소비가 되면 불리는 리스
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        @Override
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            listener.onChangedStatus(PURCHASE_STATUS.FINISH_CONSUME, result, purchase);

            // 헬퍼 널 체크
            if (helper == null) {
                fail(result.getResponse(), "helper is Null");
                return;
            }

            // 성공
            if (result.isSuccess()) {
                sendResult(result);
            } else {
                fail(result.getResponse(), "Error while consuming: " + result);
            }
        }
    };

    void sendResult(IabResult result) {

        int response = result.getResponse();

        if (response == 0) {
            // 결제 성공
            listener.onSuccess(response,"Purchases Successfully Finished");
        } else if (response == -1005) {
            // 유저 결제 취소
            listener.onFail(response, "User Canceled");
        } else {
            // 결제 실패
            listener.onFail(response, "Purchases Error Occured");
        }
        listener.onChangedStatus(PURCHASE_STATUS.END_PROCESS, result, null);
    }

    void fail(int resultCode, String msg) {
        listener.onFail(resultCode, msg);
        listener.onChangedStatus(PURCHASE_STATUS.END_PROCESS, null, null);
    }

    public void dispose() {
        if (this.helper != null) {
            this.helper.dispose();
        }
        _instance = null;
        this.helper = null;
    }
}
