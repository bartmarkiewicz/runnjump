package com.mygdx.runnjump.entity;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.runnjump.screens.GameScreen;
import com.mygdx.runnjump.util.TextureManager;


/**
 * This class represents a hedgehog enemy which simply patrols from one point to another.
 * It kills upon touching the player, can be killed only with a projectile.
 */
public class Hedgehog extends Enemy{
    float movingTime = 0;
    private final double TIME_TO_TURN;
    public Hedgehog(TiledMapTileLayer collisionLayer, TiledMapTileLayer visualLayer, int blocksToMove, int MAX_SPEED) {
        super(collisionLayer, visualLayer);
        getSprite().setSize(32*2, 23*2); // 2 by 1 tile
        setLogicalSize(32*2,23*2);
        this.tilesToMove = blocksToMove;
        enemyIdle = TextureManager.getManager().getFrameSet("hedgehog_idle");
        enemyMoving = TextureManager.getManager().getFrameSet("hedgehog_moving");
        movingRight = true;
        this.playerCollidable = true;

        speedX = MAX_SPEED;
        speedY = 250;
        TIME_TO_TURN = tilesToMove/(speedX/32);
        // Formula for how many blocks moved =
        // Blocks moved = (speedX/32)*TIME_TO_TURN
        // Time to turn = blocks moved/(speedX/32)
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        time += delta;
        // saves previous position
        float oldX = getSprite().getX(), oldY = getSprite().getY();

        boolean collisionX = false, collisionY = false;

        getSprite().setX(getSprite().getX() + velocity.x * delta); // move on x

        if (movingTime < TIME_TO_TURN) { //how much time in seconds passes until the hedgehog moves
            movingTime += delta;
        } else {
            movingTime = 0;
            movingRight = !movingRight;
            getSprite().flip(true, false);
        }

        if (movingRight) {
            velocity.x = speedX;
        } else {
            velocity.x = -speedX;
        }


        if (velocity.x < 0) {
            collisionX = collidesWest();
        } else if (velocity.x > 0) {
            collisionX = collidesEast();
        }

        velocity.y -= gravity * delta;
        if (collisionX) {
            getSprite().setX(oldX);
            velocity.x = 0;
        } else {
            if (movingRight) {
                velocity.x = speedX;
            }
            if (!movingRight) {
                velocity.x = -speedX;
            }
        }
        //move on y
        getSprite().setY(getSprite().getY() + velocity.y * delta * 5f);
        if (velocity.y < 2.5f) {
            collisionY = collidesSouth();
        } else if (velocity.y > 2.5f) {
            collisionY = collidesNorth();
        }

        if (collisionY) {
            getSprite().setY(oldY);
            velocity.y = 0;
        }

        determineFrame();

    }

    @Override
    protected void determineFrame() {
        if (this.isIdle() && time > 0.2f) {

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
        } else if (isRunning() &&  time > 0.02f){
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

    @Override
    public void collidesObject(GameObject other, float delta) {
        super.collidesObject(other, delta);
        if(other instanceof Player){
            //make noise?
        }
        if (other instanceof Projectile && ((Projectile) other).playerBullet  && (Math.abs(((Projectile) other).velocity.x) > 5 || Math.abs(((Projectile) other).velocity.y) > 5)){
            die();
            GameScreen.getPlayer().gainScore(1);
        }
    }
}
