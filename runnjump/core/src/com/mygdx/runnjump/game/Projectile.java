package com.mygdx.runnjump.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.runnjump.screens.GameScreen;
import com.mygdx.runnjump.util.Position;
import com.mygdx.runnjump.util.TextureManager;

public class Projectile extends MovingActor {
    boolean playerBullet;
    float timeToLive;
    Position throwerPos;
    public Projectile(TiledMapTileLayer collisionLayer, TiledMapTileLayer visualLayer, Position throwerPos, boolean playerBullet) {
        super(collisionLayer, visualLayer);
        this.sprite = new Sprite(TextureManager.getManager().getAsset("rocks_powerup"));
        getSprite().setSize(30*1,30*1);//1 by 1 tiles size
        setLogicalSize(30,30);
        this.playerCollidable = true;
        speedX = 600;
        speedY = 50;
        this.playerBullet = playerBullet;
        this.throwerPos = throwerPos;
        timeToLive = 2f; //2 seconds
        velocity.y = speedY;
        this.playerCollidable = !playerBullet;
    }

    public void setDirection(String direction){
        if(direction.equals("left")){
            this.velocity.x = -speedX;
            this.sprite.setX(throwerPos.getX()-(sprite.getWidth()));
            this.sprite.setY(throwerPos.getY()+(sprite.getHeight()/2));
        } else if (direction.equals("right")){
            this.velocity.x = speedX;
            this.sprite.setX(throwerPos.getX()+(sprite.getWidth()*2));
            this.sprite.setY(throwerPos.getY()+(sprite.getHeight()/2));

        }
    }


    @Override
    protected void update(float delta) {
        super.update(delta);

        timeToLive-=delta;

        velocity.y -= gravity * delta;

        boolean collisionX = collidesEast() || collidesWest();
        boolean collisionY = collidesNorth() || collidesSouth();

        if (collisionX){
            velocity.x = 0;
        }
        if (collisionY){
            velocity.y = 0;
        }
        getSprite().setX(getSprite().getX() + velocity.x * delta);
        getSprite().setY(getSprite().getY() + velocity.y * delta * 5f);


        if(timeToLive <= 0){
            die();
        }
        determineFrame();
    }

    @Override
    protected void handleMisc(float x, float y) {
        //dummy
    }

    @Override
    protected void handleCollectible(float x, float y) {
        //dummy
    }

    @Override
    void determineFrame() {
        if(time > 0.4f && (Math.abs(velocity.x)>0.2 || Math.abs(velocity.y) > 0.2)){
            getSprite().rotate90(true);

            time = 0;
        }
    }

    @Override
    public void collidesObject(GameObject other, float delta) {
        if (playerBullet && !(other instanceof Player || other instanceof Projectile || other instanceof NPC)) {
            die();

        } else if (!playerBullet && (other instanceof Player)){
            die();
        }
        super.collidesObject(other, delta);
    }

    @Override
    public boolean respawn() {
        return false;
    }
}
