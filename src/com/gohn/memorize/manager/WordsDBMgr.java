package com.gohn.memorize.manager;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gohn.memorize.model.WordSet;
import com.gohn.memorize.model.WordType;

public class WordsDBMgr {

	public static final String DB_NAME = "Words.db";
	public static final String TABLE_NAME = "Words";

	public static final String GROUP = "groupName";
	public static final String TYPE = "type";
	public static final String WORD = "word";
	public static final String MEANING = "meaning";

	static final int DB_VERSION = 1;

	Context mContext = null;

	private static WordsDBMgr mDBManager = null;
	private SQLiteDatabase mDatabase = null;

	public static WordsDBMgr getInstance(Context context) {
		if (mDBManager == null) {
			mDBManager = new WordsDBMgr(context);
		}
		return mDBManager;
	}

	private WordsDBMgr(Context context) {
		mContext = context;
		mDatabase = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);

		mDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT," + GROUP + " TEXT," + TYPE + " type TEXT," + WORD + " word TEXT," + MEANING
				+ " meaning TEXT" + ");");
		mDatabase.execSQL("CREATE INDEX IF NOT EXISTS group_idx ON " + TABLE_NAME + " (" + GROUP + ");");
		mDatabase.execSQL("CREATE INDEX IF NOT EXISTS type_idx ON " + TABLE_NAME + " (" + TYPE + ");");
	}

	public long insert(ContentValues addRowValue) {

		return mDatabase.insert(TABLE_NAME, null, addRowValue);
	}

	public Cursor query(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		return mDatabase.query(TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy);
	}

	public int update(ContentValues updateRowValue, String whereClause, String[] whereAgs) {

		return mDatabase.update(TABLE_NAME, updateRowValue, whereClause, whereAgs);
	}

	public int delete(String whereClause, String[] whereAgs) {
		return mDatabase.delete(TABLE_NAME, whereClause, whereAgs);
	}

	public Cursor rawQuery(String clause, String[] type2) {
		return mDatabase.rawQuery(clause, type2);
	}

	public ArrayList<WordSet> getWordsSet(String group, String type) {

		Cursor c;

		if (type.equals(WordType.NONE))
			c = query(getColumns(), WordsDBMgr.GROUP + "=?", new String[] { group }, null, null, null);
		else
			c = query(getColumns(), WordsDBMgr.GROUP + "=? and " + WordsDBMgr.TYPE + "=? ", new String[] { group, type }, null, null, null);

		if (c != null) {
			ArrayList<WordSet> groups = new ArrayList<WordSet>();

			while (c.moveToNext()) {
				WordSet vg = new WordSet();
				vg.Group = c.getString(0);
				vg.Type = c.getString(1);
				vg.Word = c.getString(2);
				vg.Meaning = c.getString(3);
				groups.add(vg);
			}
			return groups;
		}
		return null;
	}

	public int getWordsCount(String group, String type) {

		Cursor c;

		if (type.equals(WordType.NONE)) {
			c = query(new String[] { "count(*)" }, WordsDBMgr.GROUP + "=? ", new String[] { group }, null, null, null);
		} else
			c = query(new String[] { "count(*)" }, WordsDBMgr.GROUP + "=? and " + WordsDBMgr.TYPE + "=? ", new String[] { group, type }, null, null, null);

		int number = 0;

		if (c != null) {
			while (c.moveToNext()) {
				number = c.getInt(0);
			}
		}
		return number;
	}

	public String[] getColumns() {
		return new String[] { WordsDBMgr.GROUP, WordsDBMgr.TYPE, WordsDBMgr.WORD, WordsDBMgr.MEANING };
	}
}
