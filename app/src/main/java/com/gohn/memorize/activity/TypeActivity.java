package com.gohn.memorize.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gohn.memorize.R;
import com.gohn.memorize.activity.base.ActionBarActivity;
import com.gohn.memorize.activity.learn.MultipleActivity;
import com.gohn.memorize.activity.learn.StudyActivity;
import com.gohn.memorize.activity.learn.WriteActivity;
import com.gohn.memorize.manager.DBMgr;
import com.gohn.memorize.model.ExerciseType;
import com.gohn.memorize.model.WordType;

public class TypeActivity extends ActionBarActivity implements View.OnClickListener{

    String groupName = "";
    int exerciseType;
    DBMgr dbMgr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_type);
//        View newView = LayoutInflater.from(this).inflate(R.layout.content_type, null);
//        contentView.addView(newView);

        dbMgr = DBMgr.getInstance();

        groupName = getIntent().getExtras().getString(DBMgr.GROUP);
        exerciseType = getIntent().getExtras().getInt(ExerciseType.toStr());

        initView();
    }

    @Override
    public void onClick(View v) {
        final Intent intent = new Intent();
        intent.putExtra(DBMgr.GROUP, groupName);

        boolean zero = false;

        switch (v.getId()) {
            case R.id.btn_type_all:
                if (dbMgr.getWordsCount(groupName, WordType.NONE) == 0)
                    zero = true;
                intent.putExtra(DBMgr.TYPE, WordType.NONE);
                break;
            case R.id.btn_type_n:
                if (dbMgr.getWordsCount(groupName, WordType.NOUN) == 0)
                    zero = true;
                intent.putExtra(DBMgr.TYPE, WordType.NOUN);
                break;
            case R.id.btn_type_v:
                if (dbMgr.getWordsCount(groupName, WordType.VERB) == 0)
                    zero = true;
                intent.putExtra(DBMgr.TYPE, WordType.VERB);
                break;
            case R.id.btn_type_adj:
                if (dbMgr.getWordsCount(groupName, WordType.ADJECTIVE) == 0)
                    zero = true;
                intent.putExtra(DBMgr.TYPE, WordType.ADJECTIVE);
                break;
            case R.id.btn_type_adv:
                if (dbMgr.getWordsCount(groupName, WordType.ADVERB) == 0)
                    zero = true;
                intent.putExtra(DBMgr.TYPE, WordType.ADVERB);
                break;
            case R.id.btn_type_etc:
                if (dbMgr.getWordsCount(groupName, WordType.ETC) == 0)
                    zero = true;
                intent.putExtra(DBMgr.TYPE, WordType.ETC);
                break;
        }

        if (zero) {
            alertZero();
            return;
        }

        switch (getIntent().getExtras().getInt(ExerciseType.toStr())) {
            case R.id.btn_category_study:

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            // 블라인드 모드
                            case DialogInterface.BUTTON_POSITIVE:
                                intent.putExtra("mode", "blind");
                                break;
                            // 오픈 모드
                            case DialogInterface.BUTTON_NEGATIVE:
                                intent.putExtra("mode", "open");
                                break;
                        }
                        intent.setClass(TypeActivity.this, StudyActivity.class);
                        startActivity(intent);
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(TypeActivity.this);
                builder.setMessage(R.string.type_study_mention).setPositiveButton(R.string.type_blind, dialogClickListener).setNegativeButton(R.string.type_open, dialogClickListener).show();


                break;
            case R.id.btn_category_find_meaning:
            case R.id.btn_category_find_word:
                intent.putExtra(ExerciseType.toStr(), exerciseType);
                intent.setClass(this, MultipleActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_category_write:
                intent.setClass(this, WriteActivity.class);
                startActivity(intent);
                break;
        }
    }

    void initView() {

        findViewById(R.id.btn_type_all).setOnClickListener(this);
        findViewById(R.id.btn_type_n).setOnClickListener(this);
        findViewById(R.id.btn_type_v).setOnClickListener(this);
        findViewById(R.id.btn_type_adj).setOnClickListener(this);
        findViewById(R.id.btn_type_adv).setOnClickListener(this);
        findViewById(R.id.btn_type_etc).setOnClickListener(this);


        TextView btn_all = (TextView) findViewById(R.id.txt_type_all);
        btn_all.setText(R.string.type_all);
        btn_all.append(" (" + dbMgr.getWordsCount(groupName, WordType.NONE) + ")");

        TextView btn_n = (TextView) findViewById(R.id.txt_type_n);
        btn_n.setText(R.string.type_n);
        btn_n.append(" (" + dbMgr.getWordsCount(groupName, WordType.NOUN) + ")");

        TextView btn_v = (TextView) findViewById(R.id.txt_type_v);
        btn_v.setText(R.string.type_v);
        btn_v.append(" (" + dbMgr.getWordsCount(groupName, WordType.VERB) + ")");

        TextView btn_a = (TextView) findViewById(R.id.txt_type_adj);
        btn_a.setText(R.string.type_a);
        btn_a.append(" (" + dbMgr.getWordsCount(groupName, WordType.ADJECTIVE) + ")");

        TextView btn_ad = (TextView) findViewById(R.id.txt_type_adv);
        btn_ad.setText(R.string.type_ad);
        btn_ad.append(" (" + dbMgr.getWordsCount(groupName, WordType.ADVERB) + ")");

        TextView btn_e = (TextView) findViewById(R.id.txt_type_etc);
        btn_e.setText(R.string.type_e);
        btn_e.append(" (" + dbMgr.getWordsCount(groupName, WordType.ETC) + ")");
    }

    public void alertZero() {
        AlertDialog.Builder alert = new AlertDialog.Builder(TypeActivity.this);
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setMessage(R.string.no_voca);
        alert.show();
    }
}
