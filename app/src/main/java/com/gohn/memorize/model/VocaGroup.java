package com.gohn.memorize.model;

/**
 * Created by Gohn on 15. 12. 24..
 */
public class VocaGroup {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumbers() {
        return numbers;
    }

    public void setNumbers(int numbers) {
        this.numbers = numbers;
    }

    String name = "";
    int numbers = 0;

    public VocaGroup(String name, int numbers) {
        this.name = name;
        this.numbers = numbers;
    }
}
