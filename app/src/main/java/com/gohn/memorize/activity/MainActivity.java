package com.gohn.memorize.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gohn.memorize.R;
import com.gohn.memorize.activity.base.DrawerActivity;
import com.gohn.memorize.common.CommonData;
import com.gohn.memorize.manager.DBMgr;
import com.gohn.memorize.manager.PurchaseManager;
import com.gohn.memorize.model.IAlertDialogOneButtonHanlder;
import com.gohn.memorize.model.ISettingGroupNameViewHanlder;
import com.gohn.memorize.model.VocaGroup;
import com.gohn.memorize.util.BackPressCloseHandler;
import com.gohn.memorize.util.Dialog;
import com.gohn.memorize.util.GLog;
import com.gohn.memorize.util.billing.IabResult;
import com.gohn.memorize.util.billing.Purchase;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;

public class MainActivity extends DrawerActivity {

    CardArrayRecyclerViewAdapter cardArrayAdapter;
    CardRecyclerView recyclerView;

    BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        super.setContext(this);
        initView();

        backPressCloseHandler = new BackPressCloseHandler(this);

        // 인앱 결제 초기화.
        if (!PurchaseManager.IsConnected())
            PurchaseManager.Connect(
                    this,
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkrMwxshv5dTSCg0zNddExEnrRUUfVcDK/V8B3gSaBXqpaVm4TSgdFR9f45S9jWJXX8fE5j3IVRZq0XCLcLls/FyDY33KxmV8WqnI0XpKsPD8uuI+26IO8Jb6XrBb31YkJg9BZjc41u5EsFMUHM0IQMZU56vs62TZt4b7qrqXfXaPHkwNKsqnpzu+0Flj4vilV30100yKp6TW9cVP29OzQLG0UhdfJXTfTI/ejChd7U6c/7v5TqYP+cFqW87VaOQOV6xZUOOHMaqh3reem34QLQTaYoO5FZ3q/DEZJ7uHae8SAPV9Ed/Qgb2CihpNeVW2M2uJVJMJAvnBoefh7+pW/wIDAQAB",
                    true,
                    new PurchaseManager.OnResultListener() {
                        @Override
                        public void onSuccess(int resultCode, String message) {
                            Dialog.showOneButtonAlert(MainActivity.this, R.string.donation_success, new IAlertDialogOneButtonHanlder() {
                                @Override
                                public void onOk() {
                                }
                            });
                        }

                        @Override
                        public void onFail(int resultCode, String message) {

                            if (resultCode == -1005) {
                                Dialog.showOneButtonAlert(MainActivity.this, R.string.donation_cancel, new IAlertDialogOneButtonHanlder() {
                                    @Override
                                    public void onOk() {
                                    }
                                });
                            } else {
                                Dialog.showOneButtonAlert(MainActivity.this, R.string.donation_fail, new IAlertDialogOneButtonHanlder() {
                                    @Override
                                    public void onOk() {
                                    }
                                });
                            }
                        }

                        @Override
                        public void onChangedStatus(PurchaseManager.PURCHASE_STATUS status, IabResult result, Purchase purchase) {
                            GLog.Debug("PurchaseManager.PURCHASE_STATUS : " + status);
                        }
                    });
    }

    void initView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cardArrayAdapter = new CardArrayRecyclerViewAdapter(this, getCards());

        recyclerView = (CardRecyclerView) findViewById(R.id.carddemo_recyclerview);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Set the empty view
        if (recyclerView != null) {
            recyclerView.setAdapter(cardArrayAdapter);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TedPermission.with(MainActivity.this)
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                startActivityForResult(new Intent(MainActivity.this, FileActivity.class), CommonData.REQUEST_CODE_FILE_ACTIVITY);
                            }

                            @Override
                            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                Toast.makeText(MainActivity.this, R.string.permission_deny, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setDeniedMessage("")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();
            }
        });

        if (cardArrayAdapter.getItemCount() == 0 &&
                Locale.KOREA.getCountry().equals(getResources().getConfiguration().locale.getCountry())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("단어장에 등록된 단어가 없습니다. \n설정 화면에서 기본 단어를 추가해 주세요.").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    ArrayList<Card> getCards() {
        ArrayList<VocaGroup> vocaGroups = DBMgr.getInstance().getVocaGroups();

        ArrayList<Card> cards = new ArrayList<Card>();

        for (VocaGroup vocaGroup : vocaGroups) {
            final String name = vocaGroup.getName();

            Card card = new Card(this);
            card.setTitle(vocaGroup.getNumbers() + getResources().getString(R.string.main_word));

            CardHeader header = new CardHeader(this);
            header.setTitle(name);
            header.setPopupMenu(R.menu.group, new CardHeader.OnClickCardHeaderPopupMenuListener() {
                @Override
                public void onMenuItemClick(final BaseCard card, MenuItem item) {
                    final String cardHeaderTitle = ((Card) card).getCardHeader().getTitle();

                    switch (item.getItemId()) {
                        case R.id.menu_group_delete:
                            DBMgr.getInstance().delete(DBMgr.GROUP + "=?", new String[]{cardHeaderTitle});
                            cardArrayAdapter.remove((Card) card);
                            break;
                        case R.id.menu_group_copy:
                            // TODO : 복사기능 넣자

                            break;
                        case R.id.menu_group_change_name:
                            Dialog.showSettingGroupNameView(MainActivity.this, new ISettingGroupNameViewHanlder() {
                                @Override
                                public void onPositive(String groupName) {
                                    ContentValues cv = new ContentValues();
                                    cv.put(DBMgr.GROUP, groupName);
                                    DBMgr.getInstance().update(cv, DBMgr.GROUP + "=?", new String[]{cardHeaderTitle});
                                    ((Card) card).getCardHeader().setTitle(groupName);
                                    cardArrayAdapter.notifyDataSetChanged();
                                }
                            });
                            break;
                    }
                }
            });

            card.addCardHeader(header);
            card.setOnClickListener(new Card.OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                    intent.putExtra(DBMgr.GROUP, name);
                    startActivity(intent);
                }
            });
            cards.add(card);
        }

        return cards;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        reloadView();
    }

    public void reloadView() {
        cardArrayAdapter.setCards(getCards());
        cardArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isDrawerOpen())
            super.onBackPressed();
        else
            backPressCloseHandler.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        GLog.Debug("requestCode : " + requestCode + " , resultCode : " + resultCode);

        if (requestCode == CommonData.REQUEST_CODE_FILE_ACTIVITY) {

            if (resultCode == CommonData.RESULT_REFRESH) {
                GLog.Debug("requestCode : " + requestCode + " , resultCode : " + resultCode);

                cardArrayAdapter.setCards(getCards());
                cardArrayAdapter.notifyDataSetChanged();
            }
        }
    }
}
