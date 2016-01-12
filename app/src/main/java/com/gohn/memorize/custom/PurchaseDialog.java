package com.gohn.memorize.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.gohn.memorize.R;

/**
 * Created by Gohn on 16. 1. 13..
 */
public class PurchaseDialog extends Dialog implements View.OnClickListener {

    View.OnClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
//        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        getWindow().setAttributes(lpWindow);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);

        setContentView(R.layout.dialog_purchase);

        findViewById(R.id.layout_purchase_background).setOnClickListener(this);
        findViewById(R.id.btn_purchase_1).setOnClickListener(this);
        findViewById(R.id.btn_purchase_3).setOnClickListener(this);
        findViewById(R.id.btn_purchase_5).setOnClickListener(this);
        findViewById(R.id.btn_purchase_10).setOnClickListener(this);
    }

    public PurchaseDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    public PurchaseDialog(Context context, View.OnClickListener listener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_purchase_background:
                cancel();
                break;
            default:
                listener.onClick(v);
                break;
        }
    }

}
