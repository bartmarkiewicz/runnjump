package com.mygdx.runnjump.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

public class Player extends Sprite implements InputProcessor {

    //movement velocity
    private Vector2 velocity = new Vector2();
    private float speed = 220;
    private float gravity = 65*1.8f;
    private TiledMapTileLayer collisionLayer;
    boolean canJump;
    float sizeX,sizeY;

    public void setLogicalSize(float width, float height){
        sizeX = width;
        sizeY = height;
    }

    public Player(Sprite sprite, TiledMapTileLayer collisionLayer){
        super(sprite);
        this.collisionLayer = collisionLayer;
        setSize(32*2,32*3);//2 by 3 tiles size
        setLogicalSize(32*2,32*3);
    }

    @Override
    public void draw(Batch batch) {
        update(Gdx.graphics.getDeltaTime());//updates before drawing
        super.draw(batch);
    }

    public boolean collidesEast() {
        for(float i = 0; i <= sizeY; i += collisionLayer.getTileHeight())
            if(isCellBlocked(getX() + sizeX, getY() + i))
                return true;
        return false;
    }

    public boolean collidesWest() {
        for(float i = 0; i <= sizeY; i += collisionLayer.getTileHeight())
            if(isCellBlocked(getX(), getY() + i))
                return true;
        return false;
    }

    public boolean collidesNorth() {
        for(float i = 0; i <= sizeX; i += collisionLayer.getTileWidth())
            if(isCellBlocked(getX() + i, getY() + sizeY))
                return true;
        return false;

    }

    public boolean collidesSouth() {
        for(float i = 0; i <= sizeX; i += collisionLayer.getTileWidth())
            if(isCellBlocked(getX() + i, getY()))
                return true;
        return false;
    }

    public void update(float delta) {
        velocity.y -= gravity * delta;

        // sets max velocity
        if (velocity.y > speed)
            velocity.y = speed;
        else if (velocity.y < -speed)
            velocity.y = -speed;

        // saves previous position
        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;

        // move horizontally
        setX(getX() + velocity.x * delta);


        if (velocity.x < 0) // going left
            collisionX = collidesWest();
        else if (velocity.x > 0) // going right
            collisionX = collidesEast();

        // x collision handling
        if (collisionX) {
            setX(oldX);
            velocity.x = 0;
        }

        // move on y
        setY(getY() + velocity.y * delta * 5f);

        if (velocity.y < 0)
            canJump = collisionY = collidesSouth();
        else if (velocity.y > 0)
            collisionY = collidesNorth();

        // y collision handling
        if (collisionY) {
            setY(oldY);
            velocity.y = 0;
        }
    }

        private boolean isCellBlocked(float x, float y){
        TiledMapTileLayer.Cell cell =collisionLayer.getCell((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());
        return cell != null && cell.getTile()!=null && cell.getTile().getProperties().containsKey("blocked");
    }

    @Override
    public boolean keyDown(int keycode) {
        switch(keycode) {
            case Input.Keys.W:
                if(canJump) {
                    velocity.y = speed / 1.8f;
                    canJump = false;
                }
                break;
            case Input.Keys.D:
                velocity.x = speed;
                break;
            case Input.Keys.A:
                velocity.x = -speed;
                break;

        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch(keycode) {
            case Input.Keys.D:
                velocity.x = 0;
            case Input.Keys.A:
                velocity.x = 0;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
