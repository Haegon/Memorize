package com.gohn.memorize.model;

import android.graphics.Color;

public class AnswerItem {
	public String Answer = "";
	public String RightAnswer = "";
	public int Tint = Color.GRAY;
	public boolean Bold = false;

	public AnswerItem(String answer) {
		Answer = answer;
	}

	public AnswerItem() {
	}

	public void ColorClear() {
		Tint = Color.GRAY;
	}
}
