package com.gohn.memorize.activity.Learn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MenuItem;

import com.gohn.memorize.R;
import com.gohn.memorize.activity.BaseActivity;
import com.gohn.memorize.activity.MainActivity;
import com.gohn.memorize.manager.DBMgr;
import com.gohn.memorize.model.IAlertDialogTwoButtonHanlder;
import com.gohn.memorize.util.Dialog;
import com.gohn.memorize.util.GLog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class LearnActivity extends BaseActivity {

	protected DBMgr dbMgr;
	protected JSONObject json;
	protected String fileName;

	protected String groupName;
	protected String wordType;
	protected int exerciseType;
	protected Vibrator vibe;
	protected boolean end = false;

	protected Activity learnActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dbMgr = DBMgr.getInstance();

		json = new JSONObject();
		vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		learnActivity = this;

//		AdBuddiz.cacheAds(learnActivity);
//		AdBuddiz.setPublisherKey("2397c3d7-a8b7-4935-9869-794fc416499c");
	}

	protected void goHome() {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
        onQuitButton();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
                onQuitButton();
				break;
		}
		return true;
	}

	void onQuitButton() {
        if (end)
            return;

        Dialog.showTwoButtonAlert(this, R.string.stop_study, new IAlertDialogTwoButtonHanlder() {
            @Override
            public void onPositive() {
                finish();
            }

            @Override
            public void onNegative() {

            }
        });
	}

    public boolean isFileExist(String path) {
        try {
            InputStream inputStream = openFileInput(path);
        } catch (FileNotFoundException e) {
            GLog.Error("File not found: " + e.toString());
            return false;
        } catch (IOException e) {
            GLog.Error("Can not read file: " + e.toString());
            return false;
        }
        return true;
    }

    public void writeToFile(String path, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(path, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String readFromFile(String path) {

        String ret = "";

        try {
            InputStream inputStream = openFileInput(path);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
