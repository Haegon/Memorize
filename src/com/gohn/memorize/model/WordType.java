package com.gohn.memorize.model;

public class WordType {
	public static final String NONE = "x";
	public static final String ADJECTIVE = "a";
	public static final String NOUN = "n";
	public static final String ADVERB = "ad";
	public static final String VERB = "v";
	public static final String ETC = "e";

	public static Boolean isType(String type) {

		if (type.equals(VERB) || type.equals(ADJECTIVE) || type.equals(NOUN) || type.equals(ADVERB) || type.equals(ETC))
			return true;

		return false;
	}
}
