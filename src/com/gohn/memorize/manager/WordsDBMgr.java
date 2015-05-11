﻿package com.gohn.memorize.manager;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gohn.memorize.model.Exercise;
import com.gohn.memorize.model.ExerciseWrite;
import com.gohn.memorize.model.VocaGroup;
import com.gohn.memorize.model.WordSet;
import com.gohn.memorize.model.WordType;

public class WordsDBMgr {

	public static final String DB_NAME = "Words.db";
	public static final String TABLE_NAME = "Words";

	public static final String GROUP = "groupName";
	public static final String TYPE = "type";
	public static final String WORD = "word";
	public static final String MEANING = "meaning";

	public ArrayList<Exercise> worngExercises = new ArrayList<Exercise>();
	public ArrayList<ExerciseWrite> worngExercisesWrite = new ArrayList<ExerciseWrite>();
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
				+ " meaning TEXT, unique(" + GROUP + "," + WORD + ")" + ");");
		mDatabase.execSQL("CREATE INDEX IF NOT EXISTS group_idx ON " + TABLE_NAME + " (" + GROUP + ");");
		mDatabase.execSQL("CREATE INDEX IF NOT EXISTS type_idx ON " + TABLE_NAME + " (" + TYPE + ");");
		mDatabase.execSQL("CREATE INDEX IF NOT EXISTS word_idx ON " + TABLE_NAME + " (" + WORD + ");");
		mDatabase.execSQL("CREATE INDEX IF NOT EXISTS meaning_idx ON " + TABLE_NAME + " (" + MEANING + ");");
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
					vg.Group = c.getString(0);
					vg.Type = c.getString(1);
					vg.Word = c.getString(2);
					vg.Meaning = c.getString(3);
					groups.add(vg);
				}
			}
		}
		return groups;
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

	public void addWordsToDB(String group, ArrayList<WordSet> set) {

		for (int i = 0; i < set.size(); i++) {

			WordSet word = set.get(i);

			if (word.Type.trim().equals("") || word.Word.trim().equals("") || word.Meaning.trim().equals(""))
				continue;

			ContentValues cv = new ContentValues();
			cv.put(WordsDBMgr.GROUP, group);
			cv.put(WordsDBMgr.TYPE, word.Type);
			cv.put(WordsDBMgr.WORD, word.Word);
			cv.put(WordsDBMgr.MEANING, word.Meaning);
			insert(cv);
		}
	}

	public ArrayList<VocaGroup> getVocaGroups() {

		String[] columns = new String[] { WordsDBMgr.GROUP, "count(" + WordsDBMgr.GROUP + ")" };
		Cursor c = query(columns, null, null, WordsDBMgr.GROUP, null, null);

		if (c != null) {
			ArrayList<VocaGroup> groups = new ArrayList<VocaGroup>();

			while (c.moveToNext()) {
				VocaGroup vg = new VocaGroup();
				vg.Name = c.getString(0);
				vg.Numbers = c.getInt(1);
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
