package com.example.dan.workout.model;


/* stores the current settings, i.e. vibrate mode and volume */

public class Settings {

    private Boolean isVibrate = false;
    private int volume = 0;

    /* constructors */

    public Settings() {}

    public Settings(Boolean isVibrate) {
        this.isVibrate = isVibrate;
    }

    public Settings(int volume) {
        this.volume = volume;
    }

    /* getters */

    public Boolean isVibrate() {
        return isVibrate;
    }

    public int getVolume() {
        return volume;
    }


    /* setters */

    public void setVibrate(Boolean isVibrate) {
        this.isVibrate = isVibrate;
    }

    public void toggleVibrate() {
        isVibrate = !isVibrate;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
