package com.mygdx.runnjump.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

public class Player extends Sprite{

    //movement velocity
    private Vector2 velocity = new Vector2();
    private float speed = 120;
    private float gravity = 60*1.8f;
    private TiledMapTileLayer collisionLayer;
    int tileSize;
    float increment;

    public Player(Sprite sprite, TiledMapTileLayer collisionLayer){
        super(sprite);
        this.collisionLayer = collisionLayer;
        setSize(32*4,32*4);//2 by 4 tiles size
        tileSize = collisionLayer.getTileWidth();
    }

    @Override
    public void draw(Batch batch) {
        update(Gdx.graphics.getDeltaTime());//updates before drawing
        super.draw(batch);
    }

    public boolean collidesRight() {
        for(float step = 0; step <= getHeight(); step += increment)
            if(isCellBlocked(getX() + getWidth(), getY() + step))
                return true;
        return false;
    }

    public boolean collidesLeft() {
        for(float step = 0; step <= getHeight(); step += increment)
            if(isCellBlocked(getX(), getY() + step))
                return true;
        return false;
    }

    public boolean collidesTop() {
        for(float step = 0; step <= getWidth(); step += increment)
            if(isCellBlocked(getX() + step, getY() + getHeight()))
                return true;
        return false;

    }

    public boolean collidesBottom() {
        for(float step = 0; step <= getWidth(); step += increment)
            if(isCellBlocked(getX() + step, getY()))
                return true;
        return false;
    }

    //calc new position based on velocity
    public void update(float delta){
        velocity.y -= gravity*delta;//gravity effect
        if(velocity.y > speed){//gravity cannot go over speed
            velocity.y =speed;
        } if(velocity.y < speed){
            velocity.y = -speed;
        }

        float oldX = getX();
        float oldY = getY();
        boolean xCollision = false;
        boolean yCollision = false;


        // move on x
        setX(getX() + velocity.x * delta);

        // calculate the increment for step in #collidesLeft() and #collidesRight()
        increment = collisionLayer.getTileWidth();
        increment = getWidth() < increment ? getWidth() / 2 : increment / 2;

        increment = collisionLayer.getTileHeight();
        increment = getHeight() < increment ? getHeight() / 2 : increment / 2;
        boolean canJump = true;
        if(velocity.y < 0) // going down
            canJump = yCollision = collidesBottom();
        else if(velocity.y > 0) // going up
            yCollision = collidesTop();

        // react to y collision
        if(yCollision) {
            setY(oldY);
            velocity.y = 0;
        }


        setX(getX()+ velocity.x*delta);
        setY(getY()+ velocity.y*delta);
    }

    private boolean isCellBlocked(float x, float y){
        TiledMapTileLayer.Cell cell =collisionLayer.getCell((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());
        return cell != null && cell.getTile()!=null && cell.getTile().getProperties().containsKey("blocked");
    }
}
