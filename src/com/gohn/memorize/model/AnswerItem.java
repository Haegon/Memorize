package com.gohn.memorize.model;

import android.graphics.Color;

public class AnswerItem {
	public String Answer = "";
	public int Tint = Color.BLACK;
	public boolean Bold = false;

	public AnswerItem(String answer) {
		Answer = answer;
	}
}
