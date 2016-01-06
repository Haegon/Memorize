package com.gohn.memorize.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.gohn.memorize.R;
import com.gohn.memorize.common.CommonData;
import com.gohn.memorize.manager.DBMgr;
import com.gohn.memorize.model.ISettingGroupNameViewHanlder;
import com.gohn.memorize.model.VocaGroup;
import com.gohn.memorize.util.BackPressCloseHandler;
import com.gohn.memorize.util.Dialog;
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

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;

public class MainActivity2 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    CardArrayRecyclerViewAdapter mCardArrayAdapter;
    CardRecyclerView mRecyclerView;

    private AccountHeader headerResult = null;
    private Drawer result = null;
    private boolean opened = false;

    BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        DBMgr.init(this);

        initView(savedInstanceState);

        mCardArrayAdapter = new CardArrayRecyclerViewAdapter(this, getCards());

        mRecyclerView = (CardRecyclerView) findViewById(R.id.carddemo_recyclerview);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Set the empty view
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mCardArrayAdapter);
        }


        backPressCloseHandler = new BackPressCloseHandler(this);

    }

    void initView(Bundle savedInstanceState) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity2.this, FileActivity.class), CommonData.REQUEST_CODE_FILE_ACTIVITY);
            }
        });

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
                                                    .withChecked(Global.getBoolean(MainActivity2.this, CommonData.GLOBAL_KEY_RANDOM, false))
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

        //only set the active selection or active profile if we do not recreate the activity
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 11
            result.setSelection(R.string.navi_home, false);
        }
    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {

            if (drawerItem == null) return;

            switch (drawerItem.getIdentifier()) {
                case R.string.pref_basic_random:
                    Global.setBoolean(MainActivity2.this, CommonData.GLOBAL_KEY_RANDOM, isChecked);
                    break;
            }
        }
    };

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
                            mCardArrayAdapter.remove((Card) card);
                            break;
                        case R.id.menu_group_copy:
                            // TODO : 복사기능 넣자

                            break;
                        case R.id.menu_group_change_name:
                            Dialog.showSettingGroupNameView(MainActivity2.this, new ISettingGroupNameViewHanlder() {
                                @Override
                                public void onPositive(String groupName) {
                                    ContentValues cv = new ContentValues();
                                    cv.put(DBMgr.GROUP, groupName);
                                    DBMgr.getInstance().update(cv, DBMgr.GROUP + "=?", new String[]{cardHeaderTitle});
                                    ((Card) card).getCardHeader().setTitle(groupName);
                                    mCardArrayAdapter.notifyDataSetChanged();
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
                    Intent intent = new Intent(MainActivity2.this, CategoryActivity.class);
                    intent.putExtra(DBMgr.GROUP, name);
                    startActivity(intent);
                }
            });
            cards.add(card);
        }

        return cards;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
//            super.onBackPressed();
            backPressCloseHandler.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
////        if (id == R.id.nav_camara) {
////            // Handle the camera action
////        } else if (id == R.id.nav_gallery) {
////
////        } else if (id == R.id.nav_slideshow) {
////
////        } else if (id == R.id.nav_manage) {
////
////        } else if (id == R.id.nav_share) {
////
////        } else if (id == R.id.nav_send) {
////
////        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, intent);


        GLog.Debug("requestCode : " + requestCode + " , resultCode : " + resultCode);

        if (requestCode == CommonData.REQUEST_CODE_FILE_ACTIVITY) {

            if (resultCode == CommonData.RESULT_REFRESH) {
                GLog.Debug("requestCode : " + requestCode + " , resultCode : " + resultCode);

                mCardArrayAdapter.setCards(getCards());
                mCardArrayAdapter.notifyDataSetChanged();
            }
        }
    }
}
