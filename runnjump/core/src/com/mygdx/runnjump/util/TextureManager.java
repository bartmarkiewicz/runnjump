package com.mygdx.runnjump.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

public class TextureManager implements Disposable {
    private TreeMap<String, ArrayList<Texture>> playerFrames;
    Random random;
    public TextureManager() {
        playerFrames = new TreeMap<>();
        random = new Random();
    }

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

    public ArrayList<Texture> getPlayerFrameSet(String name){
        return playerFrames.get(name);
    }

    @Override
    public void dispose() {
        for (ArrayList<Texture> textureArray:playerFrames.values()) {
            for(Texture texture: textureArray){
                texture.dispose();
            }
        }
    }
}
