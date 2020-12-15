package com.mygdx.runnjump.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.TouchableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.runnjump.Runnjump;
import com.mygdx.runnjump.util.SoundManager;

import java.util.ArrayList;
import java.util.TreeMap;

public class Player implements InputProcessor {

    //movement velocity
    private Vector2 velocity = new Vector2();
    private float speedX = 900;
    private float speedY = 230;
    private float gravity = 98f;
    private TiledMapTileLayer collisionLayer;
    private TiledMapTileLayer visualLayer;
    private int score, hearts;
    private boolean goldKeyAcquired;
    private final int STARTING_HEARTS = 0;
    boolean canJump;
    float sizeX,sizeY;
    Sprite playerSprite;
    boolean facingRight = true;
    boolean alive;
    float time =0f;
    private SoundManager soundManager;

    private int playerIdleLastFrame=0,playerRunLastFrame=0, playerJumpLastFrame = 0;
    private boolean backWardsIdle = false, backWardsRunning = false;

    ArrayList<Texture> playerIdle, playerRunning, playerJump;

    private Hud hud;

    public void setLogicalSize(float width, float height){
        sizeX = width;
        sizeY = height;
    }

    public int getHearts(){
        return hearts;
    }

    public int getScore(){
        return score;
    }

    public Player(final Runnjump theGame, Hud hud, TiledMapTileLayer collisionLayer, TiledMapTileLayer visualLayer){
        this.playerSprite = new Sprite(new Texture("player\\Idle_000.png"));
        this.collisionLayer = collisionLayer;
        this.visualLayer = visualLayer;
        playerSprite.setSize(30*2,30*3);//2 by 3 tiles size
        setLogicalSize(30*2,30*3);
        this.score = 0;
        this.hearts =STARTING_HEARTS;
        this.goldKeyAcquired = false;
        this.hud = hud;
        hud.setScore(score);
        hud.setLives(hearts);
        this.alive = true;
        this.soundManager = theGame.soundManager;
        playerIdle = theGame.textureManager.getPlayerFrameSet("idle");
        playerRunning = theGame.textureManager.getPlayerFrameSet("running");
        playerJump = theGame.textureManager.getPlayerFrameSet("jump");
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            Touchpad joystick = hud.getMovementJoystick();
            joystick.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if(((Touchpad)actor).getKnobPercentX() >0){ // right
                        velocity.x = speedX;
                        if (!facingRight) {
                            playerSprite.flip(true, false);
                            facingRight = true;
                        }
                    } else if (((Touchpad)actor).getKnobPercentX() < 0){ // left
                        velocity.x = -speedX;
                        if(facingRight){
                            playerSprite.flip(true, false);
                            facingRight=false;
                        }
                    }
                    if(((Touchpad)actor).getKnobPercentX()==0.0){
                        velocity.x = 0;
                    }
                }
            });
            Button jumpBt = hud.getJumpBt();
            jumpBt.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if(canJump) {
                        velocity.y = speedY / 1.8f;
                        canJump = false;
                    }
                }
            });
        }


    }

    public void setFrame(Texture texture){
        playerSprite.setTexture(texture);
    }

    public void draw(Batch batch) {
        update(Gdx.graphics.getDeltaTime());//updates before drawing
        playerSprite.draw(batch);
    }

    public boolean collidesEast() {
        for(float i = 0; i <= sizeY; i += collisionLayer.getTileHeight()) {
            if(cellKillsPlayer(playerSprite.getX() + sizeX, playerSprite.getY() + i)){
                die();
            }
            if (isCellCollectible(playerSprite.getX() + sizeX, playerSprite.getY() + i)) {
                handleCollectible(playerSprite.getX() + sizeX, playerSprite.getY() + i);
            }
            if (isCellBlocked(playerSprite.getX() + sizeX, playerSprite.getY() + i))
                return true;
        }
        return false;
    }

    public boolean collidesWest() {
        for(float i = 0; i <= sizeY; i += collisionLayer.getTileHeight()) {
            if(cellKillsPlayer(playerSprite.getX(), playerSprite.getY()+i)){
                die();
            }
            if (isCellCollectible(playerSprite.getX(), playerSprite.getY()+i)) {
                handleCollectible(playerSprite.getX(), playerSprite.getY()+i);
            }
            if (isCellBlocked(playerSprite.getX(), playerSprite.getY() + i))
                return true;
        }
        return false;
    }

    public boolean collidesNorth() {
        for(float i = 0; i <= sizeX; i += collisionLayer.getTileWidth()) {
            if(cellKillsPlayer(playerSprite.getX() + i, playerSprite.getY()+sizeY)){
                die();
            }
            if (isCellCollectible(playerSprite.getX() + i, playerSprite.getY()+sizeY)) {
                handleCollectible(playerSprite.getX() + i, playerSprite.getY()+sizeY);
            }
            if (isCellBlocked(playerSprite.getX() + i, playerSprite.getY() + sizeY))
                return true;
        }
        return false;
    }
    public void removeCollectibe(int tileX, int tileY){
        visualLayer.setCell(tileX, tileY, new TiledMapTileLayer.Cell());
        collisionLayer.setCell(tileX,tileY, new TiledMapTileLayer.Cell()); // gets rid of collectible cells
    }

    public void handleCollectible(float x, float y){
        TiledMapTileLayer.Cell cellColLayer = collisionLayer.getCell((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());
        TiledMapTileLayer.Cell cellVisualLayer = visualLayer.getCell((int)x/visualLayer.getTileWidth(), (int)y/visualLayer.getTileHeight());

        //removes collectible
        removeCollectibe((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight()); // gets rid of collectible cells
        if (cellColLayer.getTile().getProperties().containsKey("coin")){
            score += 1;
            hud.setScore(score);
            soundManager.playRandom("coin_collect");
        }
        if (cellColLayer.getTile().getProperties().containsKey("heart")){
            hearts += 1;
            hud.setLives(hearts);
            soundManager.playSound("heart_collect");
        }
        if (cellColLayer.getTile().getProperties().containsKey("gold_key")){
            goldKeyAcquired = true;
            soundManager.playSound("collect_item");
        }


        if (isCellCollectible((int)x+33, (int)y)){
            removeCollectibe((int)(x+33)/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight()); //checks cell to the right
        }
        if(isCellCollectible((int)x-33, (int)y)){
           removeCollectibe((int)(x-33)/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());
        }; //checks cell to the left

        if (isCellCollectible((int)x, (int)y+33)){
            removeCollectibe((int)x/collisionLayer.getTileWidth(), (int)(y+33)/collisionLayer.getTileHeight());// checks cell above
        }
        if(isCellCollectible(x, (int)y-33)){
            removeCollectibe((int)x/collisionLayer.getTileWidth(), (int)(y-33)/collisionLayer.getTileHeight());
        };//checks cell below
        if(isCellCollectible(x-33, y)){
            removeCollectibe((int)(x-33)/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());
        }; // checks cell to the left and below
        if(isCellCollectible(x-33, y+33)){
            removeCollectibe((int)(x-33)/collisionLayer.getTileWidth(), (int)(y+33)/collisionLayer.getTileHeight());
        };//checks cell to the left and above
        if(isCellCollectible(x+33, y-33)){
            removeCollectibe((int)(x+33)/collisionLayer.getTileWidth(), (int)(y-33)/collisionLayer.getTileHeight());// checks cell to the left and below
        }
        if (isCellCollectible(x+33, y+33)){
            removeCollectibe((int)(x+32)/collisionLayer.getTileWidth(), (int)(y+33)/collisionLayer.getTileHeight());//checks cell above and to the right
        }
        if (isCellCollectible(x-33, y-33)){
            removeCollectibe((int)(x-33)/collisionLayer.getTileWidth(), (int)(y-33)/collisionLayer.getTileHeight()); // checks cell to the left and below
        }
    }

    public boolean collidesSouth() {
        for(float i = 0; i <= sizeX; i += collisionLayer.getTileWidth()) {
            if(cellKillsPlayer(playerSprite.getX() + i, playerSprite.getY())){
                die();
            }
            if (isCellCollectible(playerSprite.getX() + i, playerSprite.getY())) {
                handleCollectible(playerSprite.getX() + i, playerSprite.getY());
            }
            if (isCellBlocked(playerSprite.getX() + i, playerSprite.getY()))
                return true;
        }
        return false;
    }

    public void update(float delta) {
        velocity.y -= gravity * delta;

        time += delta;



        // sets max velocity
        if (velocity.y > speedY)
            velocity.y = speedY;
        else if (velocity.y < -speedY)
            velocity.y = -speedY;

        // saves previous position
        float oldX = playerSprite.getX(), oldY = playerSprite.getY();
        boolean collisionX = false, collisionY = false;

        // move horizontally
        playerSprite.setX(playerSprite.getX() + velocity.x * delta);


        if (velocity.x < 0) // going left
            collisionX = collidesWest();
        else if (velocity.x > 0) // going right
            collisionX = collidesEast();

        // x collision handling
        if (collisionX) {
            playerSprite.setX(oldX);
            velocity.x = 0;
        }

        // move on y
        playerSprite.setY(playerSprite.getY() + velocity.y * delta * 5f);

        if (velocity.y < 2.5f)
            canJump = collisionY = collidesSouth();
        else if (velocity.y > 2.5f)
            collisionY = collidesNorth();

        if (collisionY) {
            playerSprite.setY(oldY);
            velocity.y = 0;
        }
        if (this.isIdle() && time > 0.2f) {
            playerJumpLastFrame=0;

            this.setFrame(playerIdle.get(playerIdleLastFrame));
            if (playerIdleLastFrame == playerIdle.size() - 1){
                backWardsIdle=true;
            }
            if (playerIdleLastFrame==0){
                backWardsIdle=false;
            }
            if (backWardsIdle){
                playerIdleLastFrame--;
            } else {
                playerIdleLastFrame++;
            }
            time=0;
        }else if (isRunning() && time > 0.15f){
            playerJumpLastFrame=0;

            this.setFrame(playerRunning.get(playerRunLastFrame));
            if (playerRunLastFrame==0){
                backWardsRunning=false;
            }
            if (playerRunLastFrame == playerRunning.size()-1){//if at last element
                backWardsRunning=true;
            }
            if (backWardsRunning){
                playerRunLastFrame--;
            } else {
                playerRunLastFrame++;
            }
            time=0;
        } else if (inAir() && time > 0.06f){
            this.setFrame(playerJump.get(playerJumpLastFrame));
            if (playerJumpLastFrame== playerJump.size()-1){
            } else {
                playerJumpLastFrame++;
            }
            time =0;
        } else if(playerJumpLastFrame==playerJump.size()-1 && !inAir()&&!isRunning() && collisionY){
            this.setFrame(playerIdle.get(playerIdleLastFrame));
        }
    }

    private boolean isCellCollectible(float x, float y){
        TiledMapTileLayer.Cell cell =collisionLayer.getCell((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());
        return cell != null && cell.getTile()!=null && (cell.getTile().getProperties().containsKey("coin") ||
                cell.getTile().getProperties().containsKey("gold_key") ||
                cell.getTile().getProperties().containsKey("heart"));
    }

    private boolean cellKillsPlayer(float x, float y){
        TiledMapTileLayer.Cell cell =collisionLayer.getCell((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());
        return cell != null && cell.getTile()!=null &&
                cell.getTile().getProperties().containsKey("spikes");
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
                    velocity.y = speedY / 1.8f;
                    canJump = false;
                }
                break;
            case Input.Keys.D:
                velocity.x = speedX;
                if (!facingRight) {
                    playerSprite.flip(true, false);
                    facingRight=true;
                }
                break;
            case Input.Keys.A:
                velocity.x = -speedX;
                if(facingRight){
                    playerSprite.flip(true, false);
                    facingRight=false;
                }
                break;

        }
        return true;
    }

    public Sprite getPlayerSprite(){
        return playerSprite;
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

    public boolean isDead(){
        return !alive;
    }

    public void die(){
        if(alive) {
            hearts--;
            if (hearts >= 0) {
                hud.setLives(hearts);
            } else {
                hud.setLives(0);
            }
            velocity.y = 0;
            velocity.x = 0;
            alive = false;
        }
    }

    public boolean isRunning() {
        if ((velocity.x >0.25f || velocity.x < -0.25f)&&(velocity.y < 3f && velocity.y>-3f)){
            return true;
        }
        return false;
    }

    public boolean inAir(){
        return (velocity.y > 3f || velocity.y<-3f);
    }

    public boolean isIdle() {
        if (!isRunning()){
            return true;
        }
        return false;
    }

    public boolean respawn() {
        //returns true if the player can respawn ie has lives left
        if (hearts >= 0) {
            alive = true;
            return true;
        }
        return false;
    }

    public void setLayers(TiledMapTileLayer visualLayer, TiledMapTileLayer collisionLayer) {
        this.visualLayer = visualLayer;
        this.collisionLayer = collisionLayer;
    }

    public void restart() {
        hearts = STARTING_HEARTS;
        hud.setLives(STARTING_HEARTS);
        score = 0;
        hud.setScore(0);
    }
}
