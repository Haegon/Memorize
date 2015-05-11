package com.gohn.memorize.model;

public class ExerciseType {
	public static final int STUDY = 0;
	public static final int GUESS_MEANING = 1;
	public static final int GUESS_WORD = 2;
	public static final int WRITE_WORD = 3;

	public static String toStr() {
		return "ExerciseType";
	}
}
