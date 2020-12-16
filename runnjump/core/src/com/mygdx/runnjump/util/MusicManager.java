package com.mygdx.runnjump.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

import java.util.Random;
import java.util.TreeMap;

import sun.reflect.generics.tree.Tree;

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

    public void addMusic(String name, String path){
        musicMap.put(name, Gdx.audio.newMusic(Gdx.files.internal("music" + "/"+path)));
    }

    public void addMusicSet(String name, String[] paths){
        //this adds a group of sounds under a single name
        Music[] music = new Music[paths.length];
        for(int i = 0; i < paths.length; i++){
            music[i] = Gdx.audio.newMusic(new FileHandle("sound" + "/"+paths[i]));
        }
        musicSet.put(name,music);
    }

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
    public void muteMusic(){
        if (volume == 1){
            volume=0;
        } else {
            volume =1;
        }
        currentlyPlaying.setVolume(volume);

    }

    public void stopMusic(){
        if (currentlyPlaying != null) {
            currentlyPlaying.stop();
        }
        currentlyPlaying=null;
    }

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
