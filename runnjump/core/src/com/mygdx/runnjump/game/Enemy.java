package com.mygdx.runnjump.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.runnjump.util.SoundManager;

import java.util.ArrayList;

public class Enemy {
    private Vector2 velocity = new Vector2();
    private float speedX = 200;
    private float speedY = 0;
    private float gravity = 140f;
    private boolean gravityPowerUp;
    boolean canJump;
    float sizeX,sizeY;
    Sprite enemySprite;
    private boolean goingBack = false;



    public Enemy(){

    }

    public void draw(Batch batch){
        enemySprite.draw(batch);
    }

    public void update(float delta){

    }
}
