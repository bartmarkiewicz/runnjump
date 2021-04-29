package com.mygdx.runnjump.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

/**
 * This class is similar to the other managers, but is used for pre-loading textures and animation frames.
 */
public class TextureManager extends Manager<Texture> implements Disposable {
    private TreeMap<String, ArrayList<Texture>> frameSets;
    private TreeMap<String, Texture> textureMap;

    /**
     * The Random.
     */
    Random random;

    /**
     * This is the sole instance of the music manager singleton class
     */
    private static TextureManager textureManager;

    /**
     * this is the factory method for getting the texture manager
     * @return
     */
    public static TextureManager getManager(){
        if (textureManager == null){
            textureManager = new TextureManager();
        }
        return textureManager;
    }
    /**
     * Instantiates a new TextureManager. Private so its only created once using the factory method.
     */
    private TextureManager() {
        frameSets = new TreeMap<>();
        textureMap = new TreeMap<>();
        random = new Random();
    }

    @Override
    public void addAsset(String name, String path) {
        Texture texture = new Texture(Gdx.files.internal(path + ".png"));
        textureMap.put(name, texture);
    }



    @Override
    public void addAssetSet(String name, String[] paths) {
        //dummy
    }

    @Override
    public Texture getAsset(String name) {
        return textureMap.get(name);
    }

    /**
     * this adds a set of player images which show an animation if played after one another, the name of the file must end with a 3 digit number and .png. Number is the number of images in the set.
     *
     * @param name   the name
     * @param path   the path
     * @param number the number
     */
    public void addFrameAssetSet(String name, String path, int number){
        ArrayList<Texture> textures = new ArrayList<>(); //texture paths must be of the  format NAME_0 + number.png)
        for(int i = 0; i < number; i++){
            if (i<=9){
                textures.add(new Texture(Gdx.files.internal(path + "00" + String.valueOf(i) + ".png")));
            } else {
                textures.add(new Texture(Gdx.files.internal(path +"0"+ i + ".png")));
            }
        }
        frameSets.put(name, textures);
    }

    /**
     * this returns a player frame set specified by the name, returns in the form of an arraylist.
     *
     * @param name the name
     * @return array list
     */
    public ArrayList<Texture> getFrameSet(String name){
        return frameSets.get(name);
    }

    /**
     * disposes of all the texture assets and prevents memory leak
     */
    @Override
    public void dispose() {
        for (ArrayList<Texture> textureArray:frameSets.values()) {
            for(Texture texture: textureArray){
                texture.dispose();
            }
        }
    }


}
