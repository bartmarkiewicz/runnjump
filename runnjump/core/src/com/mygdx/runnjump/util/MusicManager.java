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
public class MusicManager implements Disposable {
    private TreeMap<String, Music> musicMap;
    private TreeMap<String, Music[]> musicSet;
    private float volume;
    Random rand = new Random();
    Music currentlyPlaying;

    public MusicManager() {
        musicMap = new TreeMap<>();
        musicSet = new TreeMap<>();
        volume = 1;
        currentlyPlaying = null;
    }

    /**
     *  this method loads and stores a song in the manager, under the name specified. It grabs the file using the path.
     * @param name
     * @param path
     */
    public void addMusic(String name, String path){
        musicMap.put(name, Gdx.audio.newMusic(Gdx.files.internal("music" + "/"+path)));
    }

    /**
     * Similar as above but instead it grabs multiple songs and puts them under a single name, useful if you want a specific soundtrack for a specific level for example.
     * @param name
     * @param paths
     */
    public void addMusicSet(String name, String[] paths){
        //this adds a group of sounds under a single name
        Music[] music = new Music[paths.length];
        for(int i = 0; i < paths.length; i++){
            music[i] = Gdx.audio.newMusic(new FileHandle("sound" + "/"+paths[i]));
        }
        musicSet.put(name,music);
    }

    /**
     * this plays a previously loaded song specified by the name, assuming no song is being played at the moment.
     * @param name
     */
    public void playMusic(String name){
        //this plays a single song
        if (currentlyPlaying == null) { //prevents multiple songs being played at once
            musicMap.get(name).play();
            currentlyPlaying = musicMap.get(name);
            musicMap.get(name).setVolume(volume);
            musicMap.get(name).setOnCompletionListener(new Music.OnCompletionListener() {
                @Override
                public void onCompletion(Music music) {
                    currentlyPlaying = null;
                }
            });
        }
    }

    /**
     * this plays a random song from the song set added by music set under the name specified.
     * @param name
     */
    public void playRandom(String name){
        Music[] songs = musicSet.get(name);
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
     *  this method mutes music.
     */
    public void muteMusic(){
        if (volume == 1){
            volume=0;
        } else {
            volume =1;
        }
        currentlyPlaying.setVolume(volume);

    }

    /**
     * this method stops the currently playing song
     */
    public void stopMusic(){
        if (currentlyPlaying != null) {
            currentlyPlaying.stop();
        }
        currentlyPlaying=null;
    }

    /**
     *  this method is used for stopping all currently playing songs, it is redundant since the current implementation only allows for one song to be playing at the same time, so currently in practice is identical to stopMusic().
     */
    public void stopAll(){
        for(Music song: musicMap.values()){
            song.stop();
        }
        for (Music[] songs: musicSet.values()) {
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
        for (Music music : musicMap.values()) {
            music.dispose();
        }
        for (Music[] musicL: musicSet.values()) {
            for(Music music: musicL){
                music.dispose();
            }
        }
    }
}
