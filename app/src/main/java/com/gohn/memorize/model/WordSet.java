package com.gohn.memorize.model;

/**
 * Created by Gohn on 15. 12. 24..
 */
public class WordSet {

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    String group;
    String type;
    String word;
    String meaning;
    Boolean isOpen;

    public WordSet() {
        this.group = "";
        this.type = "";
        this.word = "";
        this.meaning = "";
        this.isOpen = true;
    }

    public WordSet(String group) {
        this.group = group;
        this.type = "";
        this.word = "";
        this.meaning = "";
        this.isOpen = true;
    }

    public WordSet(String group, String type, String word, String meaning) {
        this.group = group;
        this.type = type;
        this.word = word;
        this.meaning = meaning;
        this.isOpen = true;
    }

    @Override
    public String toString() {
        return "WordSet{" +
                "group='" + group + '\'' +
                ", type='" + type + '\'' +
                ", word='" + word + '\'' +
                ", meaning='" + meaning + '\'' +
                ", isOpen=" + isOpen +
                '}';
    }
}
