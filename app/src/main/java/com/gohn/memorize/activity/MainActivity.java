package com.gohn.memorize.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gohn.memorize.R;
import com.gohn.memorize.activity.base.DrawerActivity;
import com.gohn.memorize.common.CommonData;
import com.gohn.memorize.manager.DBMgr;
import com.gohn.memorize.model.ISettingGroupNameViewHanlder;
import com.gohn.memorize.model.VocaGroup;
import com.gohn.memorize.util.BackPressCloseHandler;
import com.gohn.memorize.util.Dialog;
import com.gohn.memorize.util.GLog;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;

public class MainActivity extends DrawerActivity {

    CardArrayRecyclerViewAdapter mCardArrayAdapter;
    CardRecyclerView mRecyclerView;

    BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main2);

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
                startActivityForResult(new Intent(MainActivity.this, FileActivity.class), CommonData.REQUEST_CODE_FILE_ACTIVITY);
            }
        });
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
                            mCardArrayAdapter.remove((Card) card);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if ( isDrawerOpen() )
            super.onBackPressed();
        else
            backPressCloseHandler.onBackPressed();
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
