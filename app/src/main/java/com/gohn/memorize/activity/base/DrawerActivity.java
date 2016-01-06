package com.gohn.memorize.activity.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.gohn.memorize.R;
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
    private Drawer result = null;
    private boolean opened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initView(savedInstanceState);
    }

    void initView(Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.img_navi_title)
                .withSavedInstance(savedInstanceState)
                .build();

        //Create the drawer
        result = new DrawerBuilder()
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

                        switch (drawerItem.getIdentifier()) {
                            case R.string.navi_home:
                                GLog.Debug("@@@@@ R.string.navi_home");
                                break;
                            case R.string.navi_help:
                                GLog.Debug("@@@@@ R.string.navi_help");
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
                                    result.removeItems(R.string.pref_basic_random);
                                } else {
                                    int curPos = result.getPosition(drawerItem);
                                    result.addItemsAtPosition(
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

        if (savedInstanceState == null) {
            result.setSelection(R.string.navi_home, false);
        }
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
        outState = result.saveInstanceState(outState);
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
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
        return super.onOptionsItemSelected(item);
    }
}
