package com.mygdx.runnjump.util;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

public class TextureManager {
    public TreeMap<String, ArrayList<Texture>> playerFrames;
    Random random;
    public TextureManager() {
        playerFrames = new TreeMap<>();
        random = new Random();
    }
}
