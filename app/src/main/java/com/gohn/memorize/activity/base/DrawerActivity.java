package com.gohn.memorize.activity.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.gohn.memorize.R;
import com.gohn.memorize.activity.HelpActivity;
import com.gohn.memorize.activity.InfoActivity;
import com.gohn.memorize.activity.MainActivity;
import com.gohn.memorize.common.CommonData;
import com.gohn.memorize.manager.DBMgr;
import com.gohn.memorize.manager.PurchaseManager;
import com.gohn.memorize.model.IAlertDialogTwoButtonHanlder;
import com.gohn.memorize.model.WordSet;
import com.gohn.memorize.util.Dialog;
import com.gohn.memorize.util.GLog;
import com.gohn.memorize.util.Global;
import com.gohn.memorize.util.parser.ReadXlsx;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class DrawerActivity extends AppCompatActivity {

    private AccountHeader headerResult = null;
    private Drawer drawer = null;
    private Context child;
    private boolean opened = false;
    protected ViewGroup contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_drawer);

        initView(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        View content = LayoutInflater.from(this).inflate(layoutResID, null);
        contentView.addView(content);
    }

    public void setContext(Context context) {
        child = context;
    }

    void initView(Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contentView = (ViewGroup) findViewById(R.id.frame_container);

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.img_navi_title)
                .withSavedInstance(savedInstanceState)
                .build();

        //Create the drawer
        DrawerBuilder builder = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(headerResult);

        builder.addDrawerItems(
                new PrimaryDrawerItem()
                        .withName(R.string.navi_home)
                        .withIcon(R.drawable.ic_home)
                        .withIdentifier(R.string.navi_home)
                        .withSelectable(false),
                new PrimaryDrawerItem()
                        .withName(R.string.navi_help)
                        .withIcon(R.drawable.ic_help)
                        .withIdentifier(R.string.navi_help)
                        .withSelectable(false),
                new PrimaryDrawerItem()
                        .withName(R.string.navi_info)
                        .withIcon(R.drawable.ic_info)
                        .withIdentifier(R.string.navi_info)
                        .withSelectable(false),
                new DividerDrawerItem());


        if (Locale.KOREA.getCountry().equals(getResources().getConfiguration().locale.getCountry())) {
            builder.addDrawerItems(
                    new PrimaryDrawerItem()
                            .withName(R.string.navi_basic)
                            .withIcon(R.drawable.ic_book)
                            .withIdentifier(R.string.navi_basic)
                            .withSelectable(false));
        }

        builder.addDrawerItems(

                new PrimaryDrawerItem()
                        .withName(R.string.navi_donate)
                        .withIcon(R.drawable.ic_money)
                        .withIdentifier(R.string.navi_donate)
                        .withSelectable(false),
                new SecondaryDrawerItem()
                        .withName(R.string.navi_setting)
                        .withIcon(R.drawable.ic_setting)
                        .withIdentifier(R.string.navi_setting)
                        .withSelectable(false)
                        .withTextColor(Color.BLACK),
                new PrimaryDrawerItem()
                        .withName(R.string.navi_share)
                        .withIcon(R.drawable.ic_share)
                        .withIdentifier(R.string.navi_share)
                        .withSelectable(false),
                new PrimaryDrawerItem()
                        .withName(R.string.navi_grade)
                        .withIcon(R.drawable.ic_grade)
                        .withIdentifier(R.string.navi_grade)
                        .withSelectable(false),
                new PrimaryDrawerItem()
                        .withName(R.string.navi_mail)
                        .withIcon(R.drawable.ic_mail)
                        .withIdentifier(R.string.navi_mail)
                        .withSelectable(false)
        );
        builder.withOnDrawerItemClickListener(
                new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem == null) return false;

                        Intent intent = new Intent();
                        intent.putExtra(CommonData.INTENT_KEY_DRAWER_ITEM, drawerItem.getIdentifier());

                        switch (drawerItem.getIdentifier()) {
                            // 홈액티비티 실행
                            case R.string.navi_home:
                                intent.setClass(DrawerActivity.this, MainActivity.class);
                                startActivity(intent);
                                break;
                            // 도움말 액티비티 실행
                            case R.string.navi_help:
                                intent.setClass(DrawerActivity.this, HelpActivity.class);
                                startActivity(intent);
                                break;
                            // 앱정보 액티비티 실행
                            case R.string.navi_info:
                                intent.setClass(DrawerActivity.this, InfoActivity.class);
                                startActivity(intent);
                                break;
                            // 기본단어 추가
                            case R.string.navi_basic:
                                Dialog.showAddBasicView(DrawerActivity.this, new Dialog.AddBasicItemListener() {
                                    @Override
                                    public void onClickItem(final String groupName, final String fileName) {
                                        if (child != null) {
                                            // 단어장을 불러오고 있다는 진행 바를 보여줌.
                                            final LayoutInflater li = LayoutInflater.from(DrawerActivity.this);
                                            final View popupView = li.inflate(R.layout.dialog_loading, null);
                                            final AlertDialog.Builder ad = new AlertDialog.Builder(DrawerActivity.this);
                                            ad.setView(popupView);
                                            final AlertDialog loadingDialog = ad.create();
                                            loadingDialog.show();

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ArrayList<WordSet> words = new ArrayList<>();

                                                    try {
                                                        words = ReadXlsx.readExcel(getAssets().open(fileName + ".xlsx"));
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    DBMgr.getInstance().addWordsToDB(groupName, words);

                                                    loadingDialog.dismiss();

                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ((MainActivity) child).reloadView();
                                                        }
                                                    });
                                                }
                                            }).start();
                                        }
                                    }
                                });
                                break;
                            // 기부하기 액티비티 실행
                            case R.string.navi_donate:
                                GLog.Debug("@@@@@ R.string.navi_donate");
                                Dialog.showPurchaseView(DrawerActivity.this, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        switch (v.getId()) {
                                            case R.id.btn_purchase_1:
                                                PurchaseManager.Purchase(DrawerActivity.this, "donation_1000", "Fuck");
                                                GLog.Debug("OnClick btn_purchase_1");
                                                break;
                                            case R.id.btn_purchase_3:
                                                PurchaseManager.Purchase(DrawerActivity.this, "donation_3000", "Fuck");
                                                GLog.Debug("OnClick btn_purchase_3");
                                                break;
                                            case R.id.btn_purchase_5:
                                                PurchaseManager.Purchase(DrawerActivity.this, "donation_5000", "Fuck");
                                                GLog.Debug("OnClick btn_purchase_5");
                                                break;
                                            case R.id.btn_purchase_10:
                                                PurchaseManager.Purchase(DrawerActivity.this, "donation_10000", "Fuck");
                                                GLog.Debug("OnClick btn_purchase_10");
                                                break;
                                        }
                                    }
                                });
                                break;
                            // 셋팅 하위 메뉴 열기
                            case R.string.navi_setting:
                                GLog.Debug("@@@@@ R.string.navi_setting");
                                if (opened) {
                                    //remove the items which are hidden
                                    drawer.removeItems(R.string.pref_basic_random);
                                } else {
                                    int curPos = drawer.getPosition(drawerItem);
                                    drawer.addItemsAtPosition(
                                            curPos,
                                            new SwitchDrawerItem()
                                                    .withName(R.string.pref_basic_random)
                                                    .withDescription(R.string.pref_basic_random_des_on)
                                                    .withDescriptionTextColor(Color.GRAY)
                                                    .withLevel(2)
                                                    .withIcon(R.drawable.ic_clip)
                                                    .withChecked(Global.getBoolean(DrawerActivity.this, CommonData.GLOBAL_KEY_RANDOM, false))
                                                    .withIdentifier(R.string.pref_basic_random)
                                                    .withOnCheckedChangeListener(onCheckedChangeListener)
                                                    .withSelectable(false)
                                    );
                                }
                                opened = !opened;
                                return true;
                            case R.string.pref_basic_random:
                                break;
                            //공유 하기
                            case R.string.navi_share:
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_SUBJECT, "Voca DIY");
                                String sAux = "\n" + getResources().getString(R.string.share) + "\n\n";
                                sAux = sAux + "https://play.google.com/store/apps/details?id=com.gohn.memorize";
                                i.putExtra(Intent.EXTRA_TEXT, sAux);
                                startActivity(Intent.createChooser(i, getResources().getString(R.string.broadcast_share_title)));
                                break;
                            // 평점 남기기
                            case R.string.navi_grade:
                                Dialog.showTwoButtonAlert(DrawerActivity.this, R.string.grade, new IAlertDialogTwoButtonHanlder() {
                                    @Override
                                    public void onPositive() {
                                        String appPackageName = getPackageName();
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    }

                                    @Override
                                    public void onNegative() {
                                    }
                                });
                                break;
                            // 개발자에게 이메일 쓰기
                            case R.string.navi_mail:
                                Intent email = new Intent(Intent.ACTION_SEND);
                                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"gohn0929@gmail.com"});
                                email.setType("message/rfc822");
                                startActivity(Intent.createChooser(email, getResources().getString(R.string.broadcast_email_title)));
                                break;
                        }

                        return false;
                    }
                }

        );
        builder.withSavedInstance(savedInstanceState);
        builder.withShowDrawerOnFirstLaunch(true);

        drawer = builder.build();

        // 앱 서랍에 현재 위치 표시
        drawer.setSelection(

                getIntent()

                        .

                                getIntExtra(CommonData.INTENT_KEY_DRAWER_ITEM, R.string.navi_home),

                false);
    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {

            if (drawerItem == null) return;

            switch (drawerItem.getIdentifier()) {
                case R.string.pref_basic_random:
                    Global.setBoolean(DrawerActivity.this, CommonData.GLOBAL_KEY_RANDOM, isChecked);
                    break;
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState = drawer.saveInstanceState(outState);
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getGroupId()) {
            case 0:
                drawer.openDrawer();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected boolean isDrawerOpen() {
        return drawer.isDrawerOpen();
    }
}
