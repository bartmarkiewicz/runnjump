package com.mygdx.runnjump.game;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.runnjump.util.TextureManager;

public class Hedgehog extends Enemy{
    float movingTime = 0;

    public Hedgehog(TiledMapTileLayer collisionLayer, TiledMapTileLayer visualLayer) {
        super(collisionLayer, visualLayer);
        getSprite().setSize(50*2, 30*2); // 2 by 1 tile
        setLogicalSize(50*2,30*2);
        this.tilesToMove = tilesToMove;
        enemyIdle = TextureManager.getManager().getFrameSet("hedgehog_idle");
        enemyMoving = TextureManager.getManager().getFrameSet("hedgehog_moving");
        movingRight = false;
        speedX = 300;
        speedY = 250;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        // saves previous position
        float oldX = getSprite().getX(), oldY = getSprite().getY();

        boolean collisionX = false, collisionY = false;

        getSprite().setX(getSprite().getX() + velocity.x * delta);

        if (movingTime < 3){
            movingTime += delta;
        } else {
            movingTime = 0;
            movingRight = !movingRight;
            getSprite().flip(true, false);
        }
        if (movingRight){
            velocity.x = speedX;
        } else {
            velocity.x = -speedX;
        }

        if(velocity.x < 0){
            collisionX = collidesWest();
        } else if (velocity.x > 0){
            collisionX = collidesEast();
        }

        velocity.y -= gravity * delta;
        if (collisionX){
            getSprite().setX(oldX);
            velocity.x = 0;
        } else {
            if (movingRight){
                velocity.x = speedX;
            }
            if (!movingRight){
                velocity.x = -speedX;
            }
        }


        if (this.isIdle() && time > 0.2f) {
            lastIdleFrame=0;

            this.setFrame(enemyIdle.get(lastIdleFrame));
            if (lastIdleFrame == enemyIdle.size() - 1){
                backWardsIdle=true;
            }
            if (lastIdleFrame==0){
                backWardsIdle=false;
            }
            if (backWardsIdle){
                lastIdleFrame--;
            } else {
                lastIdleFrame++;
            }
            time=0;
        } else if (isRunning() &&  time > 0.15f){
            lastMovingFrame = 0;
            this.setFrame(enemyMoving.get(lastMovingFrame));

            if(lastMovingFrame == 0){
                backWardsRunning = false;
            }

            if(lastMovingFrame == enemyMoving.size()-1){
                lastMovingFrame = 0;
            }
            lastMovingFrame++;
            time=0;
        } else if (!inAir() && !isRunning()){
            this.setFrame(enemyIdle.get(lastIdleFrame));
        }


    }
}
