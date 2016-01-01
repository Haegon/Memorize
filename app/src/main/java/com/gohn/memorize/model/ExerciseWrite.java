package com.gohn.memorize.model;

public class ExerciseWrite {
	public WordSet getQuestion() {
		return question;
	}

	public void setQuestion(WordSet question) {
		this.question = question;
	}

	public AnswerItem getAnswerItem() {
		return answerItem;
	}

	public void setAnswerItem(AnswerItem answerItem) {
		this.answerItem = answerItem;
	}

	public boolean isSolve() {
		return solve;
	}

	public void setSolve(boolean solve) {
		this.solve = solve;
	}

	public boolean isCorrect() {
		return correct;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	WordSet question;
	AnswerItem answerItem = new AnswerItem();
	boolean solve = false;
	boolean correct = false;

	public ExerciseWrite(WordSet question) {
		this.question = question;
		this.answerItem = new AnswerItem();
		this.solve = false;
		this.correct = false;
	}

	public ExerciseWrite(WordSet question, AnswerItem answerItem, boolean solve, boolean correct) {
		this.question = question;
		this.answerItem = answerItem;
		this.solve = solve;
		this.correct = correct;
	}

	public void Clear() {
		this.solve = false;
		this.correct = false;
		this.answerItem = new AnswerItem();
	}
}