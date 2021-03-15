package com.mygdx.runnjump.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public abstract class GameObject {
    protected float sizeX;
    protected float sizeY;
    protected Sprite sprite;
    protected boolean alive;
    protected boolean playerCollidable;

    public void collidesObject(GameObject other, float delta){
        //dummy
    }

    public boolean isPlayerCollidable() {
        return playerCollidable;
    }

    public GameObject() {
        this.sprite = new Sprite(new Texture("player\\Idle_000.png"));
        this.alive = true;
        this.playerCollidable = false;
    }

    /**
     *  largely redundant method for changing the logical size of the player, ignoring his sprite’s size, for collision detection. Only useful for debugging purposes.
     * @param width
     * @param height
     */
    protected void setLogicalSize(float width, float height){
        sizeX = width;
        sizeY = height;
    }

    /**
     * this sets the frame(for the purposes of animation) of the sprite which will be displayed upon drawing.
     *
     * @param texture the texture
     */
    public void setFrame(Texture texture){
        sprite.setTexture(texture);
    }

    public abstract void draw(Batch batch, float delta);

    /**
     * Getter for player sprite.
     *
     * @return sprite sprite
     */
    public Sprite getSprite(){
        return sprite;
    }

    protected abstract void die();

    public abstract boolean respawn();

    public boolean isDead(){
        return !alive;
    }
}
