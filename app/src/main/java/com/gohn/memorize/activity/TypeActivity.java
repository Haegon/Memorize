package com.gohn.memorize.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.gohn.memorize.R;

public class TypeActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {

//        Intent intent = new Intent();
//        intent.putExtra(DBMgr.GROUP, getIntent().getExtras().getString(DBMgr.GROUP));
//        intent.putExtra(ExerciseType.toStr(), v.getId());
//        intent.setClass(this, TypeSelectActivity.class);
//        startActivityForResult(intent, 2);
    }
}
