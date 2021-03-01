package com.mygdx.runnjump.game;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.runnjump.util.TextureManager;

public class Hedgehog extends Enemy{
    float movingTime = 0;
    private final double TIME_TO_TURN;
    public Hedgehog(TiledMapTileLayer collisionLayer, TiledMapTileLayer visualLayer, int blocksToMove) {
        super(collisionLayer, visualLayer);
        getSprite().setSize(40*2, 25*2); // 2 by 1 tile
        setLogicalSize(40*2,25*2);
        this.tilesToMove = tilesToMove;
        enemyIdle = TextureManager.getManager().getFrameSet("hedgehog_idle");
        enemyMoving = TextureManager.getManager().getFrameSet("hedgehog_moving");
        movingRight = false;
        speedX = 200;
        speedY = 250;
        TIME_TO_TURN = blocksToMove/(speedX/32);
        //Formula for how many blocks moved =
        // Blocks moved = (speedX/32)*TIME_TO_TURN
        // Time to turn = blocks moved/(speedX/32)



    }

    @Override
    public void update(float delta) {
        super.update(delta);
        // saves previous position
        float oldX = getSprite().getX(), oldY = getSprite().getY();

        boolean collisionX = false, collisionY = false;

        getSprite().setX(getSprite().getX() + velocity.x * delta); // move on x

        if (movingTime < TIME_TO_TURN){ //how much time in seconds passes until the hedgehog moves
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
        getSprite().setY(getSprite().getY() + velocity.y * delta * 5f);
        if (velocity.y < 2.5f) {
            collisionY = collidesSouth();
        } else if (velocity.y > 2.5f){
            collisionY = collidesNorth();
        }

        if (collisionY) {
            getSprite().setY(oldY);
            velocity.y = 0;
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
