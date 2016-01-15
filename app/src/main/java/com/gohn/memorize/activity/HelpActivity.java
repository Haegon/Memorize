package com.gohn.memorize.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.gohn.memorize.R;
import com.gohn.memorize.activity.base.DrawerActivity;

/**
 * Created by Gohn on 16. 1. 7..
 */
public class HelpActivity extends DrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_help);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }
}
