package com.gohn.memorize.model;

import java.util.ArrayList;

public class ExerciseWrite {
	public WordSet Question;

	public boolean Solve = false;
	public boolean Correct = false;

	public void Clear() {
		Solve = false;
		Correct = false;
	}
}