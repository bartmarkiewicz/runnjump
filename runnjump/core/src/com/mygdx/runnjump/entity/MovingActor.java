package com.mygdx.runnjump.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;


/**
 * Parent object of all moving GameObjects.
 */
public abstract class MovingActor extends GameObject {
    //movement velocity
    protected float time = 0f;
    protected Vector2 velocity = new Vector2();
    protected float speedX = 500;
    protected float speedY = 250;
    protected float gravity = 140f;
    protected TiledMapTileLayer collisionLayer;
    protected TiledMapTileLayer visualLayer;
    protected boolean facingRight = true;
    protected boolean backWardsIdle = false;
    protected boolean backWardsRunning = false;
    protected int lastIdleFrame;
    protected int lastMovingFrame;

    /**
     * Retrieves the X position of the object.
     * @return
     */
    protected float getX(){
        return getSprite().getX()+10f;//shifts the logical location of the sprite 10 pixels to the right
    }

    public MovingActor(TiledMapTileLayer collisionLayer, TiledMapTileLayer visualLayer) {
        super();
        this.collisionLayer = collisionLayer;
        this.visualLayer = visualLayer;

        lastIdleFrame = 0;
        lastMovingFrame = 0;
    }



    /**
     * this draws the sprite after updating.
     *
     * @param batch the batch
     */
    @Override
    public void draw(Batch batch,float delta) {
        update(delta);//updates before drawing
        getSprite().draw(batch);
    }
    /**
     * methods used for testing collisions on all sides of the player character, returns true if a collision is found, false if there is no collision. Also detects collectibles and things that kill the player.
     * @return
     */
    public boolean collidesNorth() {
        for(float i = 0; i <= sizeX; i += collisionLayer.getTileWidth()) {
            if(cellKills(getX() + i, getSprite().getY()+sizeY)){
                die();
            }
            if (isCellCollectible(getX() + i, getSprite().getY()+sizeY)) {
                handleCollectible(getX() + i, getSprite().getY()+sizeY);
            }
            if (isCellMisc(getX() + i, getSprite().getY()+sizeY)){
                handleMisc(getX() + i, getSprite().getY()+sizeY);
            }
            if (isCellBlocked(getX() + i, getSprite().getY() + sizeY))
                return true;
        }
        return false;
    }
    /**
     * methods methods used for testing collisions on all sides of the player character, returns true if a collision is found, false if there is no collision. Also detects collectibles and things that kill the player.
     * @return
     */
    public boolean collidesSouth() {
        for(float i = 0; i <= sizeX; i += collisionLayer.getTileWidth()) {
            if(cellKills(getX() + i, getSprite().getY())){
                die();
            }
            if (isCellCollectible(getX() + i, getSprite().getY())) {
                handleCollectible(getX() + i, getSprite().getY());
            }
            if(isCellMisc(getX() + i, getSprite().getY())){
                handleMisc(getX() + i, getSprite().getY());
            }
            if (isCellBlocked(getX() + i, getSprite().getY()))
                return true;
        }
        return false;
    }

    /**
     * methods used for testing collisions on all sides of the player character, returns true if a collision is found, false if there is no collision. Also detects collectibles and things that kill the player.
     * @return
     */
    public boolean collidesEast() {
        for(float i = 0; i <= sizeY; i += collisionLayer.getTileHeight()) {//+32 every time
            //so far this checks 0,32,64
            if(cellKills(getX() + sizeX, getSprite().getY() + i)){
                die();
            }
            if (isCellCollectible(getX() + sizeX, getSprite().getY() + i)) {
                handleCollectible(getX() + sizeX, getSprite().getY() + i);
            }
            if(isCellMisc(getX() + sizeX, getSprite().getY() + i)){
                handleMisc(getX() + sizeX, getSprite().getY() + i);
            }
            if (isCellBlocked(getX() + sizeX, getSprite().getY() + i))
                return true;
        }
        return false;
    }

    /**
     * methods used for testing collisions on all sides of the player character, returns true if a collision is found, false if there is no collision. Also detects collectibles and things that kill the player.
     * @return
     */
    public boolean collidesWest() {
        for(float i = 0; i <= sizeY; i += collisionLayer.getTileHeight()) {
            if(cellKills(getX(), getSprite().getY()+i)){
                die();
            }
            if (isCellCollectible(getX(), getSprite().getY()+i)) {
                handleCollectible(getX(), getSprite().getY()+i);
            }
            if (isCellMisc(getX(), getSprite().getY()+i)){
                handleMisc(getX(), getSprite().getY()+i);
            }
            if (isCellBlocked(getX(), getSprite().getY() + i))
                return true;
        }
        return false;
    }



    protected abstract void handleMisc(float x, float y);

    protected abstract void handleCollectible(float x, float y);

    /**
     * Updates the actor.
     * @param delta
     */
    protected void update(float delta){
        time += delta;

        // sets max velocity
        if (velocity.y > speedY)
            velocity.y = speedY;
        else if (velocity.y < -speedY)
            velocity.y = -speedY;
    }

    abstract void determineFrame();

    /**
     * Similar to its sister methods, checks if the cell specified by the x and y coordinates is miscellaneous.
     * @param x
     * @param y
     * @return
     */
    protected boolean isCellMisc(float x, float y){
        TiledMapTileLayer.Cell cell =collisionLayer.getCell((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());
        return cell != null && cell.getTile()!=null &&
                (cell.getTile().getProperties().containsKey("checkpoint_flag") ||
                        cell.getTile().getProperties().containsKey("win_flag"));
    }

    /**
     * Similar to its sister methods, checks if the cell specified by the x and y coordinates is a collectible..
     * @param x
     * @param y
     * @return
     */
    protected boolean isCellCollectible(float x, float y){
        TiledMapTileLayer.Cell cell =collisionLayer.getCell((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());
        return cell != null && cell.getTile()!=null && (cell.getTile().getProperties().containsKey("coin") ||
                cell.getTile().getProperties().containsKey("gold_key") ||
                cell.getTile().getProperties().containsKey("silver_key") ||
                cell.getTile().getProperties().containsKey("heart") ||
                cell.getTile().getProperties().containsKey("star") ||
                cell.getTile().getProperties().containsKey("gravity_powerup") ||
                cell.getTile().getProperties().containsKey("superspeed_powerup") ||
                cell.getTile().getProperties().containsKey("rocks_powerup") ||
                cell.getTile().getProperties().containsKey("invincibility_powerup") ||
                cell.getTile().getProperties().containsKey("ghostwalk_powerup"));

    }

    /**
     * Similar to its sister methods, checks if the cell specified by the x and y coordinates kills the player.
     * @param x
     * @param y
     * @return
     */
    protected boolean cellKills(float x, float y){
        TiledMapTileLayer.Cell cell =collisionLayer.getCell((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());
        return cell != null && cell.getTile()!=null &&
                cell.getTile().getProperties().containsKey("spikes");
    }

    /**
     * Similar to its sister methods, checks if the cell specified by the x and y coordinates blocks the player's movement.
     * @param x
     * @param y
     * @return
     */
    protected boolean isCellBlocked(float x, float y){
        TiledMapTileLayer.Cell cell =collisionLayer.getCell((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());
        return cell != null && cell.getTile()!=null && cell.getTile().getProperties().containsKey("blocked");
    }

    /**
     * Checks if the player is currently moving on the x axis.
     * @return
     */
    protected boolean isRunning() {
        if ((Math.abs(velocity.x)>0.3 || Math.abs(velocity.y) > 0.3)){
            return true;
        }
        return false;
    }

    /**
     * Checks if the player is currently in the air.
     * @return
     */
    protected boolean inAir(){
        return (velocity.y > 3f || velocity.y<-3f);
    }

    /**
     * checks if the player is currently idle
     * @return
     */
    public boolean isIdle() {
        if (!isRunning()){
            return true;
        }
        return false;
    }

    /**
     * this method is used for setting the layers of the tile map which need to be checked for collisions for the player.
     *
     * @param visualLayer    the visual layer
     * @param collisionLayer the collision layer
     */
    public void setLayers(TiledMapTileLayer visualLayer, TiledMapTileLayer collisionLayer) {
        this.visualLayer = visualLayer;
        this.collisionLayer = collisionLayer;
    }


    @Override
    protected void die() {
        alive = false;
    }
}
