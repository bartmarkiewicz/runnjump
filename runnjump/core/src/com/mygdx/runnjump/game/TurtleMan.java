package com.mygdx.runnjump.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.runnjump.screens.GameScreen;
import com.mygdx.runnjump.util.Position;
import com.mygdx.runnjump.util.TextureManager;

import java.util.ArrayList;

public class TurtleMan extends Enemy {
    ArrayList<Texture> rangedAttacking;
    boolean throwing;
    private int lastThrowingFrame;
    float throwCooldown;
    Projectile rock;
    private int throwCount;

    public TurtleMan(TiledMapTileLayer collisionLayer, TiledMapTileLayer visualLayer) {
        super(collisionLayer, visualLayer);
        getSprite().setSize(30*2,30*3);//2 by 3 tiles size
        setLogicalSize(42,85); //little less than 2 tiles by 3 tiles
        enemyIdle = TextureManager.getManager().getFrameSet("turtle_idle");
        enemyMoving = TextureManager.getManager().getFrameSet("turtle_moving");
        enemyAttacking = TextureManager.getManager().getFrameSet("turtle_slashing");
        rangedAttacking = TextureManager.getManager().getFrameSet("turtle_throwing");

        this.detectionRadius = 32*20; // can see 20 cells around itself.

        movingRight = true;
        throwing = false;
        this.playerCollidable = true;
        speedX = 150;
        speedY = 150;
        jumpTime = 0;
        lastThrowingFrame = 0;
        this.turnTime = 0;
        this.throwCooldown = 3; // the turtle throws a rock every 3 seconds
        throwCount = 0;
    }

    @Override
    protected void attack() {
        super.attack();
        if(attacking) {
            throwCount = 0;
        }
    }

    @Override
    protected void update(float delta) {
        super.update(delta);

        time += delta;
        jumpTime += delta;
        attackingTime += delta;
        turnTime -= delta;

        // saves previous position
        float oldX = getSprite().getX(), oldY = getSprite().getY();

        boolean collisionX = false, collisionY = false;
        if(moving && !attacking && !throwing) {
            getSprite().setX(getSprite().getX() + velocity.x * delta); // move on x
        }
        boolean playerClose = isPlayerClose(); // checks if player is very close


        if (!throwing && !attacking && throwCount < 3 && throwCooldown<= 0 && ((playerPosition.getX()) < getSprite().getX()+detectionRadius && playerPosition.getX() > getSprite().getX()-detectionRadius &&
                ((playerPosition.getY()) < getSprite().getY()+detectionRadius && playerPosition.getY() > getSprite().getY()-detectionRadius))){
            //if the enemy is within firing range, do a ranged attack.
            moving = false;
            if(isPlayerClose()){
                attack();
            } else {
                rangedAttack();
            }
            throwCount += 1;
        } else if (!throwing && !attacking && throwCount >= 2 && (playerPosition.getX()) < getSprite().getX()+detectionRadius && playerPosition.getX() > getSprite().getX()-detectionRadius &&
                ((playerPosition.getY()) < getSprite().getY()+detectionRadius && playerPosition.getY() > getSprite().getY()-detectionRadius)){
            //if the player is close enough to the enemy and he cannot hit him with rocks
            //he moves closer.
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
        } else if ((Math.abs(playerPosition.getX()-getSprite().getX())) < 10){
            moving = false;
        }

        if (movingRight && moving) {
            velocity.x = speedX;
        } else if(moving) {
            velocity.x = -speedX;
        } else {
            velocity.x = 0;
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
        } //else {
        //    if (movingRight) {
        //        velocity.x = speedX;
        //    }
         //   if (!movingRight) {
        //        velocity.x = -speedX;
        //    }
        //}
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


        if (!attacking && !throwing && attackingTime > 2f && throwCooldown < 0){//needs 2 seconds to rest between attacks.
            attack();
        }

        if(isPlayerAbove(delta) && aboveTime > 1f && !inAir()){
            if(jumpTime > 3f) {
                jump();
                System.out.println("Player above detected, I SHOULD JUMP!!!");

            }
        }


        throwCooldown -= delta;
        if(throwCooldown < -6){
            throwCount = 0;
        }
        determineFrame();

    }

    public Projectile getProjectile(){
        Projectile temp = rock;
        rock = null;

        return temp;
    }


    private void rangedAttack() {
        throwing = true;
        throwCooldown = 4;
        float xDis = Math.abs(playerPosition.getX() - getSprite().getX());
        float yDis = playerPosition.getY() - getSprite().getY();

        rock = new Projectile(collisionLayer,visualLayer,new Position(sprite.getX(), sprite.getY()),false);
        rock.speedX = 1.15f*xDis;
        if(yDis > 32*3){
            rock.speedY = 0.4f*yDis;
        }
        if(movingRight) {
            rock.setDirection("right");

        } else {
            rock.setDirection("left");
        }
    }

    @Override
    void determineFrame() {
        if (this.isIdle() && time > 0.2f && !(attacking || throwing)) {
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
        }else if (attacking && time > 0.07f && !throwing){
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
        } else if (throwing && !attacking && time > 0.07f){
            //throwing animation
            this.setFrame(rangedAttacking.get(lastThrowingFrame));
            lastThrowingFrame += 1;
            if(lastThrowingFrame == rangedAttacking.size()-1){// end attack after finishing attack animation
                //getSprite().setSize(30*2,30*3);
                lastThrowingFrame = 0;
                throwing = false;
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
        } else if (!inAir() && !isRunning() && !attacking && !throwing){
            this.setFrame(enemyIdle.get(lastIdleFrame));
        }
    }


    @Override
    public void collidesObject(GameObject other, float delta) {
        super.collidesObject(other, delta);
        if (other instanceof Player) {
            if ((getSprite().getY() + getSprite().getHeight() )-5 < other.getSprite().getY()) {
                //checks if the player jumped on top of him.
                die();
                ((Player)other).gainScore(1);
                ((Player)other).killedBandit("You had killed a turtle bandit.!");
            }
        } else if (other instanceof Projectile && ((Projectile) other).playerBullet){
            die();
            GameScreen.getPlayer().gainScore(1);
            GameScreen.getPlayer().killedBandit("You had killed a turtle bandit!");

        }
    }
}
