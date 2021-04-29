package com.mygdx.runnjump.util;

import com.badlogic.gdx.audio.Music;

import java.util.Random;
import java.util.TreeMap;

/**
 * Base class for the Audio managers.
 * @param <T>
 */
public abstract class AudioManager<T> extends Manager {

    /**
     * assetMap is a TreeMap of singular assets.
     */
    protected TreeMap<String, T> assetMap;
    /**
     * assetSet is a TreeMap of an array of assets which are somehow connected to eachother.
     */
    protected TreeMap<String, T[]> assetSet;
    protected float volume;
    /**
     * The Random object.
     */
    Random rand = new Random();
    /**
     * The Random object instance.
     */
    Random random;

    public AudioManager() {
        assetMap = new TreeMap<>();
        assetSet = new TreeMap<>();
        volume = 1;
        random = new Random();
    }

    abstract void play(String name);
    abstract void playRandom(String name);
    abstract void stop();


    abstract void mute();
}
