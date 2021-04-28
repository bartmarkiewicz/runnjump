package com.mygdx.runnjump.entity;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.runnjump.screens.GameScreen;
import com.mygdx.runnjump.util.TextureManager;

public class Bandit extends Enemy{

    /**
     * This class represents a bandit which you can kill by jumping on top of.
     * This enemy does not kill upon touching but it kills with its sword.
     * @param collisionLayer
     * @param visualLayer
     * @param MAX_SPEED
     */
    public Bandit(TiledMapTileLayer collisionLayer, TiledMapTileLayer visualLayer, int MAX_SPEED) {
        super(collisionLayer, visualLayer);
        getSprite().setSize(30*2,30*3);//2 by 3 tiles size
        setLogicalSize(42,85); //little less than 2 tiles by 3 tiles
        enemyIdle = TextureManager.getManager().getFrameSet("bandit_idle");
        enemyMoving = TextureManager.getManager().getFrameSet("bandit_moving");
        movingRight = true;
        this.playerCollidable = true;
        speedX = MAX_SPEED;
        speedY = 150;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        time += delta;
        jumpTime += delta;
        attackingTime += delta;
        turnTime -= delta;

        // saves previous position
        float oldX = getSprite().getX(), oldY = getSprite().getY();

        boolean collisionX = false, collisionY = false;
        if(moving && !attacking) {
            getSprite().setX(getSprite().getX() + velocity.x * delta); // move on x
        }
        boolean playerClose = isPlayerClose(); // checks if player is very close

        if ((playerPosition.getX()) < getSprite().getX()+detectionRadius && playerPosition.getX() > getSprite().getX()-detectionRadius &&
                ((playerPosition.getY()) < getSprite().getY()+detectionRadius && playerPosition.getY() > getSprite().getY()-detectionRadius)){
            //if the player is close enough to the enemy to see him ,he moves towards him.
            moving = true;
        } else {
            moving = false;
        }

        if (playerPosition.getX() > getSprite().getX() && !movingRight && (!playerClose) && turnTime < 0) { //the enemy always faces the player
            if(!movingRight){
                movingRight = true;
                turnTime = 0.5f;
                getSprite().flip(true, false);
            }
        } else if (playerPosition.getX() < getSprite().getX() && movingRight && (!playerClose) && turnTime < 0){
            movingRight = false;
            getSprite().flip(true, false);
            turnTime = 0.5f;
        } else if ((Math.abs(playerPosition.getX()-getSprite().getX())) < 3){
            moving = false;
        }

        if (movingRight && moving) {
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


        if (!attacking && attackingTime > 2f){//needs 2 seconds to rest between attacks.
            attack();
        }

        if(isPlayerAbove(delta) && aboveTime > 1f && !inAir()){
            if(jumpTime > 3f) {
                jump();
                System.out.println("Player above detected, I SHOULD JUMP!!!");

            }
        }

        determineFrame();

    }

    @Override
    protected void determineFrame() {
        if (this.isIdle() && time > 0.2f && !attacking) {
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
        }else if (attacking && time > 0.07f){
            getSprite().setSize(32*3,30*3);
            this.setFrame(enemyAttacking.get(attackingFrame));
            attackingFrame += 1;

            if(attackingFrame == enemyAttacking.size()-1){// end attack after finishing attack animation
                //getSprite().setSize(30*2,30*3);
                attackingFrame = 0;
                attacking = false;
                getSprite().setScale(1,1);
                getSprite().setSize(30*2,30*3);

            }
            time = 0;
        }else if (isRunning() &&  time > 0.1f){
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
        if (other instanceof Player) {
            if (((getSprite().getY() + getSprite().getHeight()-10 ) < other.getSprite().getY()) && ((Player) other).velocity.y < 0) {
                //checks if the player jumped on top of him.
                die();
                ((Player)other).gainScore(1);
                ((Player)other).killedBandit("You had killed a bandit!");
            }
        } else if (other instanceof Projectile && ((Projectile) other).playerBullet && (Math.abs(((Projectile) other).velocity.x)  > 5 || Math.abs(((Projectile) other).velocity.x)  > 5)){
            die();
            GameScreen.getPlayer().gainScore(1);
            GameScreen.getPlayer().killedBandit("You had killed a bandit!");

        }
    }

}
