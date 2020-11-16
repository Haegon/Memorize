package com.gohn.memorize.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.gohn.memorize.R;
import com.gohn.memorize.activity.base.DrawerActivity;

/**
 * Created by Gohn on 16. 1. 7..
 */
public class InfoActivity extends DrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.txt_info_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"gohn0929@gmail.com"});
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, getResources().getString(R.string.broadcast_email_title)));

            }
        });
        findViewById(R.id.txt_info_source_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Haegon/Memorize"));
                startActivity(browserIntent);
            }
        });
    }
}
