package com.gohn.memorize.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.DBMgr;
import com.gohn.memorize.model.IAlertDialogOneButtonHanlder;
import com.gohn.memorize.model.IAlertDialogTwoButtonHanlder;
import com.gohn.memorize.model.ISettingGroupNameViewHanlder;

/**
 * Created by Gohn on 15. 12. 10..
 */
public class Dialog {
    public static void showOneButtonAlert(Context context, String message, final IAlertDialogOneButtonHanlder hanlder) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setMessage(message);
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                hanlder.onOk();
            }
        });
        alert.show();
    }

    public static void showOneButtonAlert(Context context, int message, final IAlertDialogOneButtonHanlder hanlder) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setMessage(message);
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
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
        alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                hanlder.onPositive();
            }
        });
        alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                hanlder.onNegative();
            }
        });
        alert.show();
    }

    public static void showTwoButtonAlert(Context context, int messageID, final IAlertDialogTwoButtonHanlder hanlder) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setMessage(messageID);
        alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                hanlder.onPositive();
            }
        });
        alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                hanlder.onNegative();
            }
        });
        alert.show();
    }

    public static void showSettingGroupNameView(final Context context, final ISettingGroupNameViewHanlder hanlder) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_input_group_name, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.et_group_name);

        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });

        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (userInput.getText().toString().equals("")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(R.string.no_name).setCancelable(false).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                            return;
                        } else if (DBMgr.getInstance().getGroupNames().contains(userInput.getText().toString())) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(R.string.find_duplicate).setCancelable(false).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                            return;
                        } else {
                            hanlder.onPositive(userInput.getText().toString());
                            dialog.dismiss();
                        }
                    }
                });
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }
}
