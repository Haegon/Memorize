package com.gohn.memorize.model;

import java.util.ArrayList;

public class Exercise {
	public WordSet getQuestion() {
		return question;
	}

	public void setQuestion(WordSet question) {
		this.question = question;
	}

	public ArrayList<AnswerItem> getAnswerItems() {
		return answerItems;
	}

	public void setAnswerItems(ArrayList<AnswerItem> answerItems) {
		this.answerItems = answerItems;
	}

	public int getAnswerNo() {
		return answerNo;
	}

	public void setAnswerNo(int answerNo) {
		this.answerNo = answerNo;
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
	ArrayList<AnswerItem> answerItems;
	int answerNo;
	boolean solve;
	boolean correct;

	public Exercise() {
		this.question = new WordSet();
		this.answerItems = new ArrayList<AnswerItem>();
		this.answerNo = 0;
		this.solve = false;
		this.correct = false;
	}


	public Exercise(WordSet question, ArrayList<AnswerItem> answerItems, int answerNo, boolean solve, boolean correct) {
		this.question = question;
		this.answerItems = answerItems;
		this.answerNo = answerNo;
		this.solve = solve;
		this.correct = correct;
	}


	public void Clear() {
		for (int i = 0; i < this.answerItems.size(); i++) {
			this.answerItems.get(i).ColorClear();
		}
		this.solve = false;
		this.correct = false;
	}

}