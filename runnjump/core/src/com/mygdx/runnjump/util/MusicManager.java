package com.mygdx.runnjump.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

import java.util.Random;
import java.util.TreeMap;


/**
 * This class loads and manages all the music tracks in the game.
 */
public class MusicManager extends AudioManager<Music> implements Disposable {
    /**
     * This is the sole instance of the music manager singleton class
     */
    protected static MusicManager musicManager;
    /**
     * The Currently playing music.
     */
    Music currentlyPlaying;


    /**
     * this is the factory method for getting the music manager
     * @return
     */
    public static MusicManager getManager(){
        if (musicManager == null){
            musicManager = new MusicManager();
        }
        return musicManager;
    }
    /**
     * Instantiates a new MusicManager. Private so its only created once using the factory method.
     */
    private MusicManager() {
        super();

        currentlyPlaying = null;
    }

    /**
     * this method loads and stores a song in the manager, under the name specified. It grabs the file using the path.
     *
     * @param name the name
     * @param path the path
     */
    public void addAsset(String name, String path){
        assetMap.put(name, Gdx.audio.newMusic(Gdx.files.internal("music" + "/"+path)));
    }

    /**
     * Similar as above but instead it grabs multiple songs and puts them under a single name, useful if you want a specific soundtrack for a specific level for example.
     *
     * @param name  the name
     * @param paths the paths
     */
    public void addAssetSet(String name, String[] paths){
        //this adds a group of sounds under a single name
        Music[] music = new Music[paths.length];
        for(int i = 0; i < paths.length; i++){
            music[i] = Gdx.audio.newMusic(new FileHandle("sound" + "/"+paths[i]));
        }
        assetSet.put(name,music);
    }

    @Override
    public Object getAsset(String name) {
        return assetMap.get(name);
    }

    /**
     * this plays a previously loaded song specified by the name, assuming no song is being played at the moment.
     *
     * @param name the name
     */
    public void play(String name){
        //this plays a single song
        if (currentlyPlaying == null) { //prevents multiple songs being played at once
            assetMap.get(name).play();
            currentlyPlaying = assetMap.get(name);
            assetMap.get(name).setVolume(volume);
            assetMap.get(name).setOnCompletionListener(new Music.OnCompletionListener() {
                @Override
                public void onCompletion(Music music) {
                    currentlyPlaying = null;
                }
            });
        }
    }

    /**
     * this plays a random song from the song set added by music set under the name specified.
     *
     * @param name the name
     */
    public void playRandom(String name){
        Music[] songs = assetSet.get(name);
        int current = rand.nextInt(songs.length);
        if (currentlyPlaying == null) {
            songs[current].play();
            currentlyPlaying = songs[current];
            songs[current].setVolume(volume);
            songs[current].setOnCompletionListener(new Music.OnCompletionListener() {
                @Override
                public void onCompletion(Music music) {
                    currentlyPlaying = null;
                }
            });
        }
    }

    /**
     * This method causes the currently playing music to loop.
     * @param looping
     */
    public void setLooping(Boolean looping){
        if(currentlyPlaying != null){
            currentlyPlaying.setLooping(looping);
        }
    }



    /**
     * this method mutes music.
     */
    public void mute(){
        if (volume == 1){
            volume=0;
        } else {
            volume =1;
        }
        if(currentlyPlaying != null) currentlyPlaying.setVolume(volume);

    }

    /**
     * this method stops the currently playing song
     */
    public void stop(){
        if (currentlyPlaying != null) {
            currentlyPlaying.stop();
        }
        currentlyPlaying=null;
    }

    /**
     * this method is used for stopping all currently playing songs, it is redundant since the current implementation only allows for one song to be playing at the same time, so currently in practice is identical to stopMusic().
     */
    public void stopAll(){
        for(Music song: assetMap.values()){
            song.stop();
        }
        for (Music[] songs: assetSet.values()) {
            for(Music song: songs){
                song.stop();
            }
        }
        currentlyPlaying = null;
    }

    /**
     * disposes of all the music assets, to prevent memory leaks.
     */
    @Override
    public void dispose() {
        //disposes of all the sounds
        for (Music music : assetMap.values()) {
            music.dispose();
        }
        for (Music[] musicL: assetSet.values()) {
            for(Music music: musicL){
                music.dispose();
            }
        }
    }
}
