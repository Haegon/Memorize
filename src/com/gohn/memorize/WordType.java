package com.gohn.memorize;

public class WordType {
	public static final String NONE = "x";
	public static final String ADJECTIVE = "a";
	public static final String NOUN = "n";
	public static final String ADVERB = "ad";
	public static final String VERB = "v";
	public static final String ETC = "e";

	public static Boolean isType(String type) {

		if (type == NONE || type == ADJECTIVE || type == NOUN || type == ADVERB
				|| type == ETC)
			return true;

		return false;
	}
}
