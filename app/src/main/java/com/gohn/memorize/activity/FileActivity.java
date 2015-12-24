package com.gohn.memorize.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gohn.memorize.R;
import com.gohn.memorize.adapter.FindFileAdapter;
import com.gohn.memorize.common.CommonData;
import com.gohn.memorize.manager.DBMgr;
import com.gohn.memorize.model.WordSet;
import com.gohn.memorize.util.GLog;
import com.gohn.memorize.util.parser.ReadCSV;
import com.gohn.memorize.util.parser.ReadXls;
import com.gohn.memorize.util.parser.ReadXlsx;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class FileActivity extends AppCompatActivity {

    Context context = this;

    ListView listView = null;
    FindFileAdapter adapter = null;

    String baseDir = "/mnt/sdcard";

    DBMgr dbMgr;

    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);


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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView tv = (TextView) getWindow().findViewById(actionBarTitle);
        Typeface tf = CommonData.getTypefaceRegular(this);
        if (tv != null) {
            tv.setTypeface(tf);
        }

        adapter = new FindFileAdapter(this, GetFiles(baseDir));

        listView = (ListView) findViewById(R.id.list_file);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                file = adapter.items.get(position);

                if (!file.isDirectory()) {

                    final String ext = file.getName().substring(file.getName().lastIndexOf("."));
                    if (ext.contains(".xls") || ext.contains(".xlsx") || ext.contains(".csv")) {

                        final LayoutInflater li = LayoutInflater.from(context);
                        final View promptsView = li.inflate(R.layout.dialog_input_group_name, null);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                        // set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(promptsView);

                        final EditText etGroupName = (EditText) promptsView.findViewById(R.id.et_group_name);

                        // set dialog message
                        alertDialogBuilder.setCancelable(false).setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // 단어장 이름이 비어있을때
                                if (etGroupName.getText().toString().equals("")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setMessage(R.string.no_name).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // do things
                                        }
                                    });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                    return;
                                }

                                if ( DBMgr.getInstance() == null ) {
                                    GLog.Debug("DBMgr.getInstance() == null");
                                }

                                // 동일한 단어장 이름이 있을때
                                if (dbMgr.getGroupNames().contains(etGroupName.getText().toString())) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setMessage(R.string.find_duplicate).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // do things
                                        }
                                    });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                    return;
                                }

                                new Thread(new Runnable() {
                                    public void run() {

                                        ArrayList<WordSet> words = new ArrayList<WordSet>();

                                        if (ext.contains(".xls") && !ext.contains(".xlsx")) {
                                            words = ReadXls.Read(etGroupName.getText().toString(), file.getAbsolutePath());
                                        } else if (ext.contains(".xlsx")) {
                                            words = ReadXlsx.Read(file.getAbsolutePath());
                                        } else if (ext.contains(".csv")) {
                                            words = ReadCSV.Read(file.getAbsolutePath());
                                        }
//                                        // 이 스레드에서 Realm 인스턴스 얻기
//                                        Realm realm = Realm.getInstance(context);
//
//                                        // 데이터를 손쉽게 영속적으로 만들기
//                                        realm.beginTransaction();
//                                        for ( WordSet wordSet : words) {
//                                            realm.copyToRealm(wordSet);
//                                        }
//                                        realm.commitTransaction();


                                        dbMgr.addWordsToDB(etGroupName.getText().toString(), words);
                                        finish();
                                    }
                                }).start();

                                // 단어장을 불러오고 있다는 진행 바를 보여줌.
                                final View popupView = li.inflate(R.layout.dialog_loading, null);
                                AlertDialog.Builder ad = new AlertDialog.Builder(context);
                                ad.setView(popupView);
                                AlertDialog alertDialog = ad.create();
                                alertDialog.show();
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();

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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // adapter.delete((int) id);
                return true;
            }

        });

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(FileActivity.this, "onItemSelected Item : " + position + ", " + id, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(FileActivity.this, "OnNothing Selected", Toast.LENGTH_LONG).show();
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

//    private void addWordsToDB(String group, ArrayList<WordSet> set) {
//
//        for (int i = 0; i < set.size(); i++) {
//            ContentValues cv = new ContentValues();
//            cv.put(WordsDBMgr.GROUP, group);
//            cv.put(WordsDBMgr.TYPE, set.get(i).Type);
//            cv.put(WordsDBMgr.WORD, set.get(i).Word);
//            cv.put(WordsDBMgr.MEANING, set.get(i).Meaning);
//            dbMgr.insert(cv);
//        }
//    }

}
