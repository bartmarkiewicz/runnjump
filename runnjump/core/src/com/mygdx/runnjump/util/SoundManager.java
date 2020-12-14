package com.mygdx.runnjump.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

public class SoundManager implements Disposable {
    //This class should be a singleton
    //Pass reference of this class to every screen.
    private TreeMap<String,Sound> soundMap;
    private TreeMap<String,Sound[]> soundSet;
    private float volume;
    Random random;
    public SoundManager() {
        soundMap = new TreeMap<>();
        soundSet = new TreeMap<>();
        random = new Random();
        volume = 1;
    }
    public void muteSound(){
        if (volume == 1){
            volume=0;
        } else {
            volume =1;
        }
    }



    public void addSoundSet(String name, String[] paths){
        //this adds a group of sounds under a single name
        Sound[] sounds = new Sound[paths.length];
        for(int i = 0; i < paths.length; i++){
            sounds[i] = Gdx.audio.newSound(Gdx.files.internal("sound" + "/"+paths[i]));
        }
        soundSet.put(name,sounds);
    }

    public void addSound(String name, String path) {
        //this adds a sound
        soundMap.put(name,Gdx.audio.newSound(Gdx.files.internal("sound" + "/"+path)));
    }

    public void playSound(String name){
        //this plays a single sound
        soundMap.get(name).play(volume);
    }

    public void playRandom(String name){
        Sound[] sounds = soundSet.get(name);
        sounds[random.nextInt(sounds.length)].play(volume);
    }
    @Override
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
