package com.gohn.memorize.activity;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import com.gohn.memorize.R;
import com.gohn.memorize.activity.base.RecyclerPagerActivity;
import com.gohn.memorize.adapter.HelpAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gohn on 16. 1. 7..
 */
public class HelpActivity extends RecyclerPagerActivity {

    List<Integer> items = new ArrayList<>();
    HelpAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_help);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        super.init();

        initData();
        initView();
    }


    public void initData() {
        items.add(R.layout.layout_help_1);
        items.add(R.layout.layout_help_2);
        items.add(R.layout.layout_help_3);
        items.add(R.layout.layout_help_4);
        items.add(R.layout.layout_help_5);
        items.add(R.layout.layout_help_6);
        items.add(R.layout.layout_help_7);
        items.add(R.layout.layout_help_8);
    }

    public void initView() {

        adapter = new HelpAdapter(this, items, getSupportFragmentManager());
        recyclerView.setAdapter(adapter);
    }
}
