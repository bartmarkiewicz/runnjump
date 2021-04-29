package com.mygdx.runnjump.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.runnjump.util.TextureManager;

import java.util.ArrayList;


/**
 * NPC class used to represent a friendly NPC the player can talk to or trade with.
 */
public class NPC extends MovingActor{
    ArrayList<Texture> idle;
    private String npcName, assetName;

    public NPC(TiledMapTileLayer collisionLayer, TiledMapTileLayer visualLayer, String name,  String assetName, int level) {
        super(collisionLayer, visualLayer);
        idle = TextureManager.getManager().getFrameSet(assetName);
        this.assetName = assetName + "Lvl" + level;
        this.npcName = name;
        this.alive = true;
        getSprite().setSize(28*2, 29*3); // 2 by 3 tile
        getSprite().setFlip(true, false);
        setLogicalSize(32*2,32*3);
        this.playerCollidable = true;
    }

    @Override
    protected void update(float delta) {
        super.update(delta);
        time += delta;
        // saves previous position
        float oldY = getSprite().getY();

        boolean collisionY = false;

        velocity.y -= gravity * delta;

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
    protected void handleMisc(float x, float y) {
        //dummy
    }

    @Override
    protected void handleCollectible(float x, float y) {
        //dummy
    }

    @Override
    void determineFrame() {
        //animate
        if (this.isIdle() && time > 0.2f) {

            this.setFrame(idle.get(lastIdleFrame));
            if (lastIdleFrame == idle.size() - 1) {
                backWardsIdle = true;
            }
            if (lastIdleFrame == 0) {
                backWardsIdle = false;
            }
            if (backWardsIdle) {
                lastIdleFrame--;
            } else {
                lastIdleFrame++;
            }
            time = 0;
        }
    }


    /**
     * Gets the name of the assets used for the NPC
     * @return
     */
    public String getAssetName() {

        return assetName;
    }

    /**
     * Retrieves the name of the NPC
     * @return
     */
    public String getNpcName() {
        return npcName;
    }

    @Override
    public void collidesObject(GameObject other, float delta) {
        super.collidesObject(other, delta);
    }

    @Override
    public boolean respawn() {
        return false;
    }
}
