package com.mygdx.runnjump.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

import java.util.TreeMap;

public class MusicManager {
    private TreeMap<String, Music> musicMap;

    public MusicManager() {
        musicMap = new TreeMap<>();

    }
    public void addMusic(String name, String path){
        musicMap.put(name, Gdx.audio.newMusic(new FileHandle(path)));
    }


    public void dispose() {
        //disposes of all the sounds
        for (Music music : musicMap.values()) {
            music.dispose();
        }
    }
}
