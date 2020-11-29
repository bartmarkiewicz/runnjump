package com.mygdx.runnjump.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

public class SoundManager {
    private TreeMap<String,Sound> soundMap;
    private TreeMap<String,Sound[]> soundSet;
    Random random;
    public SoundManager() {
        soundMap = new TreeMap<>();
        soundSet = new TreeMap<>();
        random = new Random();
    }
    public void addSoundSet(String name, String[] paths){
        //this adds a group of sounds under a single name
        Sound[] sounds = new Sound[paths.length];
        for(int i = 0; i < paths.length; i++){
            sounds[i] = Gdx.audio.newSound(new FileHandle(paths[i]));
        }
        soundSet.put(name,sounds);
    }

    public void addSound(String name, String path) {
        //this adds a sound
        soundMap.put(name,Gdx.audio.newSound(new FileHandle(path)));
    }

    public void playSound(String name){
        //this plays a single sound
        soundMap.get(name).play();
    }

    public void playRandom(String name){
        Sound[] sounds = soundSet.get(name);
        sounds[random.nextInt(sounds.length)].play();
    }

    public void dispose(){
        //disposes of all the sounds
        for (Sound sound: soundMap.values()) {
            sound.dispose();
        }
        for (Sound[] soundList: soundSet.values()) {
            for(Sound sound: soundList){
                sound.dispose();
            }
        }
    }
}
