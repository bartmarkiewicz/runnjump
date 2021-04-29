package com.mygdx.runnjump.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

import java.util.TreeMap;

/**
 * Singleton, similar to the MusicManager but for simpler/shorter sound effect sounds like the sound effect upon picking up of a coin.
 */
public class SoundManager extends AudioManager<Sound> implements Disposable {
    /**
     * This is the sole instance of the music manager singleton class
     */

    private float volume;

    /**
     * This is the sole instance of the sound manager singleton class
     */
    private static SoundManager soundManager;

    /**
     * this is the factory method for getting the sound manager
     * @return
     */
    public static SoundManager getManager(){
        if (soundManager == null){
            soundManager = new SoundManager();
        }
        return soundManager;
    }

    /**
     * Instantiates a new SoundManager. Private so its only created once using the factory method.
     */
    private SoundManager() {
        super();
        assetMap = new TreeMap<>();
        assetSet = new TreeMap<>();
        volume = 1;
    }

    /**
     * mutes and unmutes all sound effects
     */
    public void mute(){
        if (volume == 1){
            volume=0;
        } else {
            volume =1;
        }
    }

    /**
     * this adds a group of sounds under a single name
     *
     * @param name  the name
     * @param paths the paths
     */
    public void addAssetSet(String name, String[] paths){
        //this adds a group of sounds under a single name
        Sound[] sounds = new Sound[paths.length];
        for(int i = 0; i < paths.length; i++){
            sounds[i] = Gdx.audio.newSound(Gdx.files.internal("sound" + "/"+paths[i]));
        }
        assetSet.put(name,sounds);
    }

    @Override
    public Sound getAsset(String name) {
        return assetMap.get(name);
    }

    /**
     * adds and loads a single sound under the name specified.
     *
     * @param name the name
     * @param path the path
     */
    public void addAsset(String name, String path) {
        //this adds a sound
        assetMap.put(name,Gdx.audio.newSound(Gdx.files.internal("sound" + "/"+path)));
    }

    /**
     * plays the sound specified by the name
     *
     * @param name the name
     */
    public void play(String name){
        //this plays a single sound
        assetMap.get(name).play(volume);
    }

    /**
     * plays a random sound from a sound set added under the specified name.
     *
     * @param name the name
     */
    public void playRandom(String name){
        Sound[] sounds = assetSet.get(name);
        sounds[random.nextInt(sounds.length)].play(volume);
    }



    /**
     * disposes of all the sound assets and prevents memory leaks
     */
    @Override
    public void dispose(){
        //disposes of all the sounds
        for (Sound sound: assetMap.values()) {
            sound.dispose();
        }
        for (Sound[] soundList: assetSet.values()) {
            for(Sound sound: soundList){
                sound.dispose();
            }
        }
    }
}
