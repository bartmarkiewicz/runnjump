package com.mygdx.runnjump.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.runnjump.util.TextureManager;

import java.util.ArrayList;


/**
 * A class used to represent an enemy NPC and its assorted AI and the logic related to it.
 */
public abstract class Enemy extends MovingActor{
    protected ArrayList<Texture> enemyIdle, enemyMoving;
    protected boolean movingRight;
    /*This boolean measure how long a player has been above the bandit. If its a high enough number, the bandit will jump to reach the player.
     */
    protected float aboveTime;
    protected float jumpTime;
    int tilesToMove;
    float attackingTime = 3;
    boolean attacking;
    boolean moving;
    float detectionRadius;
    public ArrayList<Texture> enemyAttacking;
    int attackingFrame;
    float turnTime = 0;

    public Enemy(TiledMapTileLayer collisionLayer, TiledMapTileLayer visualLayer) {
        super(collisionLayer, visualLayer);
        enemyIdle = new ArrayList<Texture>();
        enemyMoving = new ArrayList<Texture>();
        alive = true;
        this.attacking = false;
        this.moving = false;
        this.detectionRadius = 32*17; // can see 17 cells around itself.
        enemyAttacking = TextureManager.getManager().getFrameSet("bandit_attacking");
        this.attackingFrame = 0;
        aboveTime = 0f;
        this.turnTime = 0;
        jumpTime = 0;
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

    protected boolean isPlayerAbove(float delta) {
        if ((playerPosition.getX()) < getSprite().getX()+getSprite().getWidth()+(32*10) && playerPosition.getX() > getSprite().getX()-getSprite().getWidth()-(32*10) &&
                (playerPosition.getY() > getSprite().getY()-getSprite().getHeight()+(32*7))) {
            aboveTime += delta;
            return true;
        }
        aboveTime = 0;
        return false;
    }

    protected void attack() {
        double distanceAwayX = Math.abs(playerPosition.getX() - getSprite().getX());
        double distanceAwayY = Math.abs(playerPosition.getY() - getSprite().getY());

        if(!movingRight && distanceAwayX < 30*2 && distanceAwayY < 32*4){//attack left
            moving = false;
            attacking = true;
            attackingTime = 0;
        } else if(movingRight && distanceAwayX < 32*3 && distanceAwayY < 32*4){//if away less than 3 blocks right
            moving = false;
            attacking = true;
            attackingTime = 0;
        }
    }

    /**
     * This method checks if the player is very close.
     * @return
     */
    protected boolean isPlayerClose() {
        if ((playerPosition.getX()) < getSprite().getX()+getSprite().getWidth()+44 && playerPosition.getX() > getSprite().getX()-getSprite().getWidth()-44 &&
                ((playerPosition.getY()) < getSprite().getY()+getSprite().getWidth()+44 && playerPosition.getY() > getSprite().getY()-getSprite().getHeight()-44)) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean isRunning() {
        return moving;
    }

    public boolean isAttacking() {
        if(attacking && attackingFrame > 2){//The enemy only kills the player if the sword is in swing
            return true;
        } else {
            return false;
        }
    }

    protected void jump() {
        velocity.y = speedY / 1.8f;
        jumpTime = 0;
    }
}
