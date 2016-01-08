package com.gohn.memorize.activity.base;

import android.content.Intent;
import android.graphics.Color;
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
import com.gohn.memorize.activity.MainActivity;
import com.gohn.memorize.common.CommonData;
import com.gohn.memorize.util.GLog;
import com.gohn.memorize.util.Global;
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

public class DrawerActivity extends AppCompatActivity {

    private AccountHeader headerResult = null;
    private Drawer drawer = null;
    private boolean opened = false;
    protected ViewGroup contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main2);

        initView(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
//        super.setContentView(layoutResID);

        View content = LayoutInflater.from(this).inflate(layoutResID, null);
        contentView.addView(content);
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
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
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
                        new PrimaryDrawerItem()
                                .withName(R.string.navi_donate)
                                .withIcon(R.drawable.ic_money)
                                .withIdentifier(R.string.navi_donate)
                                .withSelectable(false),

                        new DividerDrawerItem(),

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
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem == null) return false;

                        Intent intent = new Intent();
                        intent.putExtra(CommonData.INTENT_KEY_DRAWER_ITEM, drawerItem.getIdentifier());

                        switch (drawerItem.getIdentifier()) {
                            case R.string.navi_home:
                                GLog.Debug("@@@@@ R.string.navi_home");
                                intent.setClass(DrawerActivity.this, MainActivity.class);
                                startActivity(intent);
                                break;
                            case R.string.navi_help:
                                GLog.Debug("@@@@@ R.string.navi_help");
                                intent.setClass(DrawerActivity.this, HelpActivity.class);
                                startActivity(intent);
                                break;
                            case R.string.navi_info:
                                GLog.Debug("@@@@@ R.string.navi_info");
                                break;
                            case R.string.navi_donate:
                                GLog.Debug("@@@@@ R.string.navi_donate");
                                break;
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
                                GLog.Debug("@@@@@ R.string.pref_basic_random");
                                break;
                            case R.string.navi_share:
                                GLog.Debug("@@@@@ R.string.navi_share");
                                break;
                            case R.string.navi_grade:
                                GLog.Debug("@@@@@ R.string.navi_grade");
                                break;
                            case R.string.navi_mail:
                                GLog.Debug("@@@@@ R.string.navi_mail");
                                break;
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();


        // 앱 서랍에 현재 위치 표시
        drawer.setSelection(getIntent().getIntExtra(CommonData.INTENT_KEY_DRAWER_ITEM, R.string.navi_home), false);
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
//            backPressCloseHandler.onBackPressed();
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
}
