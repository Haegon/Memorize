package com.gohn.memorize.model;

import java.util.ArrayList;

public class Exercise {
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