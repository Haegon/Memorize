package com.gohn.memorize.model;

import java.util.ArrayList;

public class Exercise {
	public WordSet Question;
	public ArrayList<AnswerItem> AnswerItems = new ArrayList<AnswerItem>();

	public boolean Correct = false;
	public boolean Solve = false;
}
