package com.gohn.memorize.model;


public class ExerciseWrite {
	public WordSet Question;
	public AnswerItem AnswerItems = new AnswerItem();

	public boolean Solve = false;
	public boolean Correct = false;

	public void Clear() {
		Solve = false;
		Correct = false;
	}
}