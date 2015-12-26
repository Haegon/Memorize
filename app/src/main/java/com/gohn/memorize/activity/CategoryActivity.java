package com.gohn.memorize.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.gohn.memorize.R;
import com.gohn.memorize.manager.DBMgr;
import com.gohn.memorize.model.ExerciseType;

public class CategoryActivity extends BaseActivity implements View.OnClickListener {

    String group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View newView = LayoutInflater.from(this).inflate(R.layout.content_category, null);
        contentView.addView(newView);

        findViewById(R.id.btn_category_study).setOnClickListener(this);
        findViewById(R.id.btn_category_find_meaning).setOnClickListener(this);
        findViewById(R.id.btn_category_find_word).setOnClickListener(this);
        findViewById(R.id.btn_category_write).setOnClickListener(this);

        group = getIntent().getExtras().getString(DBMgr.GROUP);
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent();
        intent.putExtra(DBMgr.GROUP, group);
        intent.putExtra(ExerciseType.toStr(), v.getId());
        intent.setClass(this, TypeActivity.class);
        startActivity(intent);
    }
}