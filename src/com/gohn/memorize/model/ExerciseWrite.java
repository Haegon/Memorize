package com.gohn.memorize.model;

public class ExerciseWrite {
	public WordSet Question;
	public AnswerItem AnswerItem = new AnswerItem();

	public boolean Solve = false;
	public boolean Correct = false;

	public void Clear() {
		Solve = false;
		Correct = false;
		AnswerItem = new AnswerItem();
	}
}