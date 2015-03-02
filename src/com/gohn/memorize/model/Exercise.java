package com.gohn.memorize.model;

import java.util.ArrayList;

public class Exercise {
	public WordSet Question = new WordSet();
	public ArrayList<AnswerItem> AnswerItems = new ArrayList<AnswerItem>();
	public int AnswerNo = 0;

	public boolean Solve = false;
	public boolean Correct = false;

	public void Clear() {
		for (int i = 0; i < AnswerItems.size(); i++) {
			AnswerItems.get(i).ColorClear();
		}
		Solve = false;
		Correct = false;
	}

}