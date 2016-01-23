package com.gohn.memorize.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.DBMgr;
import com.gohn.memorize.model.IAlertDialogOneButtonHanlder;
import com.gohn.memorize.model.IAlertDialogTwoButtonHanlder;
import com.gohn.memorize.model.ISettingGroupNameViewHanlder;
import com.gohn.memorize.model.WordSet;
import com.gohn.memorize.util.parser.ReadXlsx;

import java.io.IOException;
import java.util.ArrayList;

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
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
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

    public static void showPurchaseView(final Context context, final View.OnClickListener listener) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_purchase, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        promptsView.findViewById(R.id.btn_purchase_1).setOnClickListener(listener);
        promptsView.findViewById(R.id.btn_purchase_3).setOnClickListener(listener);
        promptsView.findViewById(R.id.btn_purchase_5).setOnClickListener(listener);
        promptsView.findViewById(R.id.btn_purchase_10).setOnClickListener(listener);

        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    public static void showAddBasicView(final Context context, final View.OnClickListener listener) {
        final Activity activity = ((Activity) context);

        LayoutInflater li = LayoutInflater.from(context);
        final View promptsView = li.inflate(R.layout.dialog_add_basic, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final Button btn = (Button) promptsView.findViewById(v.getId());

                // 동일한 단어장 이름이 있을때
                if (DBMgr.getInstance().getGroupNames().contains(btn.getText().toString())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(R.string.find_duplicate).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do things
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    return;
                }

                // 단어장을 불러오고 있다는 진행 바를 보여줌.
                final LayoutInflater li = LayoutInflater.from(context);
                final View popupView = li.inflate(R.layout.dialog_loading, null);
                final AlertDialog.Builder ad = new AlertDialog.Builder(context);
                ad.setView(popupView);
                final AlertDialog alertDialog = ad.create();
                alertDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        activity.runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                String name = activity.getResources().getResourceEntryName(v.getId());
                                ArrayList<WordSet> words = new ArrayList<WordSet>();

                                try {
                                    words = ReadXlsx.readExcel(activity.getAssets().open(name + ".xlsx"));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                DBMgr.getInstance().addWordsToDB(btn.getText().toString(), words);

                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("[ " + btn.getText().toString() + " ] " + "은 메인 화면에서 확인해주세요. ").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        alertDialog.dismiss();
                                        listener.onClick(v);
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();


                            }
                        });
                    }
                }).start();
            }
        };

        promptsView.findViewById(R.id.middle_1).setOnClickListener(onClickListener);
        promptsView.findViewById(R.id.middle_2).setOnClickListener(onClickListener);
        promptsView.findViewById(R.id.middle_3).setOnClickListener(onClickListener);
        promptsView.findViewById(R.id.high_1).setOnClickListener(onClickListener);
        promptsView.findViewById(R.id.high_2).setOnClickListener(onClickListener);
        promptsView.findViewById(R.id.high_3).setOnClickListener(onClickListener);
        promptsView.findViewById(R.id.toeic_1).setOnClickListener(onClickListener);
        promptsView.findViewById(R.id.toeic_2).setOnClickListener(onClickListener);

        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

}
