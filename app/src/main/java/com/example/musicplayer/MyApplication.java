package com.example.musicplayer;

import android.app.Application;

public class MyApplication extends Application {

    boolean shuffleboolean=false,
            repeatboolean=false;

    public void SetRepeatBoolean(boolean val){repeatboolean= val;}

    public boolean GetRepeatBoolean(){return repeatboolean;}

    public void SetShuffleBoolean(boolean val){shuffleboolean= val;}

    public boolean GetShuffleBoolean(){return shuffleboolean;}

}
