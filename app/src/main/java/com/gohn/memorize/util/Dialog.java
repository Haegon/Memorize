package com.gohn.memorize.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.gohn.memorize.model.IAlertDialogOneButtonHanlder;
import com.gohn.memorize.model.IAlertDialogTwoButtonHanlder;

/**
 * Created by Gohn on 15. 12. 10..
 */
public class Dialog {
    public static void showOneButtonAlert(Context context, String message, final IAlertDialogOneButtonHanlder hanlder) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setMessage(message);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                hanlder.onOk();
            }
        });
        alert.show();
    }

    public static void showTwoButtonAlert(Context context, String message, final IAlertDialogTwoButtonHanlder hanlder) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setMessage(message);
        alert.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                hanlder.onPositive();
            }
        });
        alert.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                hanlder.onNegative();
            }
        });
        alert.show();
    }

}
