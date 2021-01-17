package com.mygdx.runnjump.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

public abstract class MovingActor extends GameObject {
    //movement velocity
    protected Vector2 velocity = new Vector2();
    protected float speedX = 500;
    protected float speedY = 250;
    protected float gravity = 140f;
    protected TiledMapTileLayer collisionLayer;
    protected TiledMapTileLayer visualLayer;
    protected boolean facingRight = true;

    public MovingActor(TiledMapTileLayer collisionLayer, TiledMapTileLayer visualLayer) {
        super();
        this.collisionLayer = collisionLayer;
        this.visualLayer = visualLayer;
    }

    /**
     * this draws the sprite after updating.
     *
     * @param batch the batch
     */
    @Override
    public void draw(Batch batch) {
        update(Gdx.graphics.getDeltaTime());//updates before drawing
        getSprite().draw(batch);
    }

    /**
     * methods methods used for testing collisions on all sides of the player character, returns true if a collision is found, false if there is no collision. Also detects collectibles and things that kill the player.
     * @return
     */
    public boolean collidesSouth() {
        for(float i = 0; i <= sizeX; i += collisionLayer.getTileWidth()) {
            if(cellKills(getSprite().getX() + i, getSprite().getY())){
                die();
            }
            if (isCellCollectible(getSprite().getX() + i, getSprite().getY())) {
                handleCollectible(getSprite().getX() + i, getSprite().getY());
            }
            if(isCellMisc(getSprite().getX() + i, getSprite().getY())){
                handleMisc(getSprite().getX() + i, getSprite().getY());
            }
            if (isCellBlocked(getSprite().getX() + i, getSprite().getY()))
                return true;
        }
        return false;
    }

    /**
     * methods used for testing collisions on all sides of the player character, returns true if a collision is found, false if there is no collision. Also detects collectibles and things that kill the player.
     * @return
     */
    public boolean collidesEast() {
        for(float i = 0; i <= sizeY; i += collisionLayer.getTileHeight()) {
            if(cellKills(getSprite().getX() + sizeX, getSprite().getY() + i)){
                die();
            }
            if (isCellCollectible(getSprite().getX() + sizeX, getSprite().getY() + i)) {
                handleCollectible(getSprite().getX() + sizeX, getSprite().getY() + i);
            }
            if(isCellMisc(getSprite().getX() + sizeX, getSprite().getY() + i)){
                handleMisc(getSprite().getX() + sizeX, getSprite().getY() + i);
            }
            if (isCellBlocked(getSprite().getX() + sizeX, getSprite().getY() + i))
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
            if(cellKills(getSprite().getX(), getSprite().getY()+i)){
                die();
            }
            if (isCellCollectible(getSprite().getX(), getSprite().getY()+i)) {
                handleCollectible(getSprite().getX(), getSprite().getY()+i);
            }
            if (isCellMisc(getSprite().getX(), getSprite().getY()+i)){
                handleMisc(getSprite().getX(), getSprite().getY()+i);
            }
            if (isCellBlocked(getSprite().getX(), getSprite().getY() + i))
                return true;
        }
        return false;
    }

    /**
     * methods used for testing collisions on all sides of the player character, returns true if a collision is found, false if there is no collision. Also detects collectibles and things that kill the player.
     * @return
     */
    public boolean collidesNorth() {
        for(float i = 0; i <= sizeX; i += collisionLayer.getTileWidth()) {
            if(cellKills(getSprite().getX() + i, getSprite().getY()+sizeY)){
                die();
            }
            if (isCellCollectible(getSprite().getX() + i, getSprite().getY()+sizeY)) {
                handleCollectible(getSprite().getX() + i, getSprite().getY()+sizeY);
            }
            if (isCellMisc(getSprite().getX() + i, getSprite().getY()+sizeY)){
                handleMisc(getSprite().getX() + i, getSprite().getY()+sizeY);
            }

            if (isCellBlocked(getSprite().getX() + i, getSprite().getY() + sizeY))
                return true;
        }
        return false;
    }

    protected abstract void handleMisc(float x, float y);

    protected abstract void handleCollectible(float x, float y);

    protected abstract void update(float delta);

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
                cell.getTile().getProperties().containsKey("heart") ||
                cell.getTile().getProperties().containsKey("star") ||
                cell.getTile().getProperties().containsKey("gravity_powerup"));
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
        if ((velocity.x >0.25f || velocity.x < -0.25f)&&(velocity.y < 3f && velocity.y>-3f)){
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
    protected boolean isIdle() {
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

}
