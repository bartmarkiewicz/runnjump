package com.mygdx.runnjump.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;


/**
 * A class used to represent an enemy NPC and its assorted AI and the logic related to it.
 */
public abstract class Enemy extends MovingActor{
    protected ArrayList<Texture> enemyIdle, enemyMoving;
    protected boolean movingRight;
    int tilesToMove;
    public Enemy(TiledMapTileLayer collisionLayer, TiledMapTileLayer visualLayer) {
        super(collisionLayer, visualLayer);
        enemyIdle = new ArrayList<Texture>();
        enemyMoving = new ArrayList<Texture>();
        alive = true;
    }

    @Override
    protected void handleMisc(float x, float y) {
        //dummy implementation
    }

    @Override
    protected void handleCollectible(float x, float y) {
        //dummy implementation

    }

    @Override
    public boolean respawn() {
        return false;
    }
}
