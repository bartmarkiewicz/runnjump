package com.mygdx.runnjump.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;


/**
 * A class used to represent an enemy NPC and its assorted AI and the logic related to it. Unfinished as of week 11.
 */
public class Enemy {
    private Vector2 velocity = new Vector2();
    private float speedX = 200;
    private float speedY = 0;
    private float gravity = 140f;
    private boolean gravityPowerUp;
    /**
     * The Can jump.
     */
    boolean canJump;
    /**
     * The Size x.
     */
    float sizeX,
    /**
     * The Size y.
     */
    sizeY;
    /**
     * The Enemy sprite.
     */
    Sprite enemySprite;
    private boolean goingBack = false;


    /**
     * Instantiates a new Enemy.
     */
    public Enemy(){

    }

    /**
     * Draws the enemy sprite using the batch.
     *
     * @param batch the batch
     */
    public void draw(Batch batch){
        enemySprite.draw(batch);
    }

    /**
     * Updates the enemy.
     *
     * @param delta the time since last call of render method
     */
    public void update(float delta){

    }
}
