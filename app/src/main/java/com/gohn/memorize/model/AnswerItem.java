package com.gohn.memorize.model;

import android.graphics.Color;

public class AnswerItem {
	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getRightAnswer() {
		return rightAnswer;
	}

	public void setRightAnswer(String rightAnswer) {
		this.rightAnswer = rightAnswer;
	}

	public int getTint() {
		return tint;
	}

	public void setTint(int tint) {
		this.tint = tint;
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	String answer = "";
	String rightAnswer = "";
	int tint = Color.GRAY;
	boolean bold = false;

	public AnswerItem(String answer) {
		this.answer = answer;
		this.rightAnswer = "";
		this.tint = Color.GRAY;
		this.bold = false;
	}

	public AnswerItem() {
		this.answer = "";
		this.rightAnswer = "";
		this.tint = Color.GRAY;
		this.bold = false;
	}

	public void ColorClear() {
		this.tint = Color.GRAY;
	}
}
