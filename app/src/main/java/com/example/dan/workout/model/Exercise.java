package com.example.dan.workout.model;

public class Exercise {

    private String name;
    private int numOfSets;
    private int restTime;

    public Exercise(String name) {
        this.name = name;
    }

    public void setNumOfSets(int num) {
        this.numOfSets = num;
    }

    public void setRestTime(int time) {
        this.restTime = time;
    }

    public String getName() {
        return name;
    }

    public int getNumOfSets() {
        return numOfSets;
    }

    public int getRestTime() {
        return restTime;
    }
}
