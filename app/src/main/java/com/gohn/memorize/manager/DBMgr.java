package com.gohn.memorize.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gohn.memorize.model.Exercise;
import com.gohn.memorize.model.ExerciseWrite;
import com.gohn.memorize.model.VocaGroup;
import com.gohn.memorize.model.WordSet;
import com.gohn.memorize.model.WordType;
import com.gohn.memorize.util.GLog;

import java.util.ArrayList;

/**
 * Created by Gohn on 15. 12. 24..
 */
public class DBMgr {

    static final int DB_VERSION = 1;

    public static final String DB_NAME = "Words.db";
    public static final String TABLE_NAME = "Words";

    public static final String GROUP = "groupName";
    public static final String TYPE = "type";
    public static final String WORD = "word";
    public static final String MEANING = "meaning";



    ArrayList<Exercise> worngExercises = new ArrayList<Exercise>();
    ArrayList<ExerciseWrite> worngExercisesWrite = new ArrayList<ExerciseWrite>();

    Context context;
    boolean isInit;

    private static DBMgr instance = null;
    private SQLiteDatabase database = null;

    public static void init(Context context) {
        if (instance == null) {
            instance = new DBMgr(context);
        }
    }

    public static DBMgr getInstance() {

        if ( !instance.isInit ) {
            GLog.Error("DBMgr didn't `init` yet !!");
            return null;
        }
        return instance;
    }

    private DBMgr(Context context) {
        this.context = context;
        database = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);

        database.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT," + GROUP + " TEXT," + TYPE + " type TEXT," + WORD + " word TEXT," + MEANING
                + " meaning TEXT, unique(" + GROUP + "," + WORD + ")" + ");");
        database.execSQL("CREATE INDEX IF NOT EXISTS group_idx ON " + TABLE_NAME + " (" + GROUP + ");");
        database.execSQL("CREATE INDEX IF NOT EXISTS type_idx ON " + TABLE_NAME + " (" + TYPE + ");");
        database.execSQL("CREATE INDEX IF NOT EXISTS word_idx ON " + TABLE_NAME + " (" + WORD + ");");
        database.execSQL("CREATE INDEX IF NOT EXISTS meaning_idx ON " + TABLE_NAME + " (" + MEANING + ");");

        isInit = true;
    }

    public long insert(ContentValues addRowValue) {

        return database.insert(TABLE_NAME, null, addRowValue);
    }

    public Cursor query(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return database.query(TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public int update(ContentValues updateRowValue, String whereClause, String[] whereAgs) {

        return database.update(TABLE_NAME, updateRowValue, whereClause, whereAgs);
    }

    public int delete(String whereClause, String[] whereAgs) {
        return database.delete(TABLE_NAME, whereClause, whereAgs);
    }

    public Cursor rawQuery(String clause, String[] type2) {
        return database.rawQuery(clause, type2);
    }

    public ArrayList<WordSet> getWordsSet(String group, String type) {

        String query;
        int count = getWordsCount(group, type);

        ArrayList<WordSet> groups = new ArrayList<WordSet>();

        for (int i = 0; i < count / 100 + 1; i++) {
            Cursor c;
            int limit = 100;

            if (i == count / 100)
                limit = count - (count / 100) * i;

            if (type.equals(WordType.NONE)) {
                query = String.format("select groupName, type, word, meaning from Words where groupName=? limit %d offset %d", limit, i * 100);
                c = rawQuery(query, new String[] { group });
            } else {
                query = String.format("select groupName, type, word, meaning from Words where groupName=? and type=? limit %d offset %d", limit, i * 100);
                c = rawQuery(query, new String[] { group, type });
            }

            if (c != null) {
                while (c.moveToNext()) {
                    WordSet vg = new WordSet();
                    vg.setGroup(c.getString(0));
                    vg.setType(c.getString(1));
                    vg.setWord(c.getString(2));
                    vg.setMeaning(c.getString(3));
                    groups.add(vg);
                }
            }
        }
        return groups;
    }

    public int getWordsCount(String group, String type) {

        Cursor c;

        if (type.equals(WordType.NONE)) {
            c = query(new String[] { "count(*)" }, DBMgr.GROUP + "=? ", new String[] { group }, null, null, null);
        } else
            c = query(new String[] { "count(*)" }, DBMgr.GROUP + "=? and " + DBMgr.TYPE + "=? ", new String[] { group, type }, null, null, null);

        int number = 0;

        if (c != null) {
            while (c.moveToNext()) {
                number = c.getInt(0);
            }
        }
        return number;
    }

    public String[] getColumns() {
        return new String[] { DBMgr.GROUP, DBMgr.TYPE, DBMgr.WORD, DBMgr.MEANING };
    }

    public void addWordsToDB(String group, ArrayList<WordSet> set) {

        for (WordSet word : set) {
            if (word.getType().trim().equals("") ||
                    word.getWord().trim().equals("") ||
                    word.getMeaning().trim().equals(""))
                continue;

            ContentValues cv = new ContentValues();
            cv.put(DBMgr.GROUP, group);
            cv.put(DBMgr.TYPE, word.getType());
            cv.put(DBMgr.WORD, word.getWord());
            cv.put(DBMgr.MEANING, word.getMeaning());
            insert(cv);
        }
    }

    public ArrayList<VocaGroup> getVocaGroups() {

        String[] columns = new String[] { DBMgr.GROUP, "count(" + DBMgr.GROUP + ")" };
        Cursor c = query(columns, null, null, DBMgr.GROUP, null, null);

        if (c != null) {
            ArrayList<VocaGroup> groups = new ArrayList<VocaGroup>();

            while (c.moveToNext()) {
                VocaGroup vg = new VocaGroup(c.getString(0),c.getInt(1));
                groups.add(vg);
            }
            return groups;
        }
        return null;
    }

    public ArrayList<String> getGroupNames() {
        String query = "select groupName from Words group by groupName";
        Cursor c = rawQuery(query, new String[] {});

        if (c != null) {
            ArrayList<String> groups = new ArrayList<String>();

            while (c.moveToNext()) {
                groups.add(c.getString(0));
            }
            return groups;
        }
        return null;
    }
}
