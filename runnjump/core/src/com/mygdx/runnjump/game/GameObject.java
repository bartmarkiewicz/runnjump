package com.mygdx.runnjump.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.runnjump.util.Position;

public abstract class GameObject {
    protected float sizeX;
    protected float sizeY;
    protected Sprite sprite;
    protected boolean alive;
    protected boolean playerCollidable;
    Position playerPosition;
    private boolean active;

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
        this.playerPosition = new Position();
        active = true;
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



    /*This method checks if the object should be updated based on the player position. This is so
    objects far away from the player are not rendered & updated needlessly - saving performance.
     * @param x position of the player character
     * @param y position of the player character
     * @param width width of the game camera view
     * @param height height of the game  camera view
     * @return returns the boolean value of weather the object should be active or not.
     */
    public boolean isActive(float x, float y, float width, float height){
        playerPosition = new Position(x,y);
        float objXpos = this.getSprite().getX();
        float objYpos = this.getSprite().getY();

        // position + width/height = a screen away from the player
        if ((playerPosition.getX() + width) > objXpos && playerPosition.getX() - width < objXpos) {
            if((playerPosition.getY() +height) > objYpos && playerPosition.getY()-height < objYpos) {
                active = true;
                System.out.println(getClass().toString() + " is active.");
                return true;
            }
        }
        System.out.println(getClass().toString() + " is not active.");

        return false;
    }


}
