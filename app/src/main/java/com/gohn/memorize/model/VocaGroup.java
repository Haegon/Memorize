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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    long createTime ;
    String name;
    int numbers;

    public VocaGroup(long createTime, String name, int numbers) {
        this.createTime = createTime;
        this.name = name;
        this.numbers = numbers;
    }
}
