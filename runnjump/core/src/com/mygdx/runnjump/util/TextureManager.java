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
public class TextureManager implements Disposable {
    private TreeMap<String, ArrayList<Texture>> playerFrames;
    Random random;
    public TextureManager() {
        playerFrames = new TreeMap<>();
        random = new Random();
    }

    /**
     * this adds a set of player images which show an animation if played after one another, the name of the file must end with a 3 digit number and .png. Number is the number of images in the set.
     * @param name
     * @param path
     * @param number
     */
    public void addPlayerFrameSet(String name,String path, int number){
        ArrayList<Texture> textures = new ArrayList<>(); //texture paths must be of the  format NAME_0 + number.png)
        for(int i = 0; i < number; i++){
            if (i<=9){
                textures.add(new Texture(Gdx.files.internal(path + "00" + String.valueOf(i) + ".png")));
            } else {
                textures.add(new Texture(Gdx.files.internal(path +"0"+ i + ".png")));
            }
        }
        playerFrames.put(name, textures);
    }

    /**
     * this returns a player frame set specified by the name, returns in the form of an arraylist.
     * @param name
     * @return
     */
    public ArrayList<Texture> getPlayerFrameSet(String name){
        return playerFrames.get(name);
    }

    /**
     * disposes of all the texture assets and prevents memory leak
     */
    @Override
    public void dispose() {
        for (ArrayList<Texture> textureArray:playerFrames.values()) {
            for(Texture texture: textureArray){
                texture.dispose();
            }
        }
    }
}
