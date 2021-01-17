package com.mygdx.runnjump.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;


/**
 * A class used to represent an enemy NPC and its assorted AI and the logic related to it. Unfinished as of week 11.
 */
public class Enemy extends MovingActor{

    public Enemy(TiledMapTileLayer collisionLayer, TiledMapTileLayer visualLayer) {
        super(collisionLayer, visualLayer);
    }

    @Override
    protected void handleMisc(float x, float y) {

    }

    @Override
    protected void handleCollectible(float x, float y) {

    }

    @Override
    protected void update(float delta) {

    }

    @Override
    protected void die() {

    }

    @Override
    public boolean respawn() {
        return false;
    }
}
