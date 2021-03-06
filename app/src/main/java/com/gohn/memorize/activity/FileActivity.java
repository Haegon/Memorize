package com.gohn.memorize.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gohn.memorize.R;
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

public class FileActivity extends AppCompatActivity {

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

    void initView() {
        adapter = new FindFileAdapter(this, GetFiles(baseDir));

        ListView listView = (ListView)findViewById(R.id.list_file);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
