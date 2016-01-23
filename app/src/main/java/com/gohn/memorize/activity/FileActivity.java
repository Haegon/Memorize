package com.gohn.memorize.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.gohn.memorize.R;
import com.gohn.memorize.activity.base.observable.ObservableListActivity;
import com.gohn.memorize.adapter.FindFileAdapter;
import com.gohn.memorize.common.CommonData;
import com.gohn.memorize.manager.DBMgr;
import com.gohn.memorize.model.ISettingGroupNameViewHanlder;
import com.gohn.memorize.model.WordSet;
import com.gohn.memorize.util.Dialog;
import com.gohn.memorize.util.parser.ReadCSV;
import com.gohn.memorize.util.parser.ReadXls;
import com.gohn.memorize.util.parser.ReadXlsx;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class FileActivity extends ObservableListActivity {

    Context context = this;
    FindFileAdapter adapter = null;
    String baseDir = "/mnt/sdcard";
    DBMgr dbMgr;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_file);

        dbMgr = DBMgr.getInstance();
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void initView() {

        int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");

        adapter = new FindFileAdapter(this, GetFiles(baseDir));

        setListView((ObservableListView) findViewById(R.id.list_file));
        getListView().setAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                file = adapter.items.get(position);
                if (!file.isDirectory()) {
                    final String ext = file.getName().substring(file.getName().lastIndexOf("."));
                    if (ext.contains(".xls") || ext.contains(".xlsx") || ext.contains(".csv")) {

                        Dialog.showSettingGroupNameView(FileActivity.this, new ISettingGroupNameViewHanlder() {
                            @Override
                            public void onPositive(final String groupName) {
                                new Thread(new Runnable() {
                                    public void run() {

                                        ArrayList<WordSet> words = new ArrayList<WordSet>();

                                        if (ext.contains(".xls") && !ext.contains(".xlsx")) {
                                            words = ReadXls.Read(groupName, file.getAbsolutePath());
                                        } else if (ext.contains(".xlsx")) {
                                            words = ReadXlsx.Read(file.getAbsolutePath());
                                        } else if (ext.contains(".csv")) {
                                            words = ReadCSV.Read(file.getAbsolutePath());
                                        }
                                        dbMgr.addWordsToDB(groupName, words);

                                        setResult(CommonData.RESULT_REFRESH);
                                        finish();
                                    }
                                }).start();

                                // 단어장을 불러오고 있다는 진행 바를 보여줌.
                                final View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
                                AlertDialog.Builder ad = new AlertDialog.Builder(context);
                                ad.setView(popupView);
                                AlertDialog alertDialog = ad.create();
                                alertDialog.show();
                            }
                        });
                    }
                    return;
                }

                if (position == 0) {
                    showPrev();
                    return;
                }
                showNext(file.getName());
            }
        });
    }

    public ArrayList<File> GetFiles(String path) {
        ArrayList<File> MyFiles = new ArrayList<File>();

        File f = new File(path);
        f.mkdirs();
        File[] files = f.listFiles();

        File parent = f.getParentFile();

        MyFiles.add(parent);

        Arrays.sort(files);

        for (int i = 0; i < files.length; i++) {
            if (!files[i].isHidden() && files[i].isDirectory()) {
                MyFiles.add(files[i]);
            }
        }
        for (int i = 0; i < files.length; i++) {
            if (!files[i].isHidden() && !files[i].isDirectory()) {
                if (files[i].getAbsolutePath().contains(".xls") || files[i].getAbsolutePath().contains(".xlsx") || files[i].getAbsolutePath().contains(".csv")) {
                    MyFiles.add(files[i]);
                }
            }
        }
        return MyFiles;
    }

    public String ParentDir(String child) {

        int index = 1;

        for (int i = -1; (i = child.indexOf("/", i + 1)) != -1; ) {
            index = i;
        }
        if (index == 4)
            return child;

        return child.substring(0, index);
    }

    private void showPrev() {
        baseDir = ParentDir(baseDir);
        adapter.items = GetFiles(baseDir);
        adapter.notifyDataSetChanged();
    }

    private void showNext(String dir) {
        baseDir = baseDir + "/" + dir;
        adapter.items = GetFiles(baseDir);
        adapter.notifyDataSetChanged();
    }
}
