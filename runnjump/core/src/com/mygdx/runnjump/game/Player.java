package com.mygdx.runnjump.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import com.mygdx.runnjump.Runnjump;
import com.mygdx.runnjump.util.SoundManager;

import java.util.ArrayList;

/**
 * This class represents the player character on the game map.
 */
public class Player implements InputProcessor {

    //movement velocity
    private Vector2 velocity = new Vector2();
    private float speedX = 500;
    private float speedY = 250;
    private float gravity = 140f;
    private boolean gravityPowerUp;
    private float powerUpTime;
    private TiledMapTileLayer collisionLayer;
    private TiledMapTileLayer visualLayer;
    private int score, hearts;
    private boolean goldKeyAcquired;
    private final int STARTING_HEARTS = 3;
    private boolean canJump;
    private float sizeX,sizeY;
    private Sprite playerSprite;
    private boolean facingRight = true;
    private boolean alive;
    private float time =0f;
    private SoundManager soundManager;
    private boolean gameWon;


    private int playerIdleLastFrame=0,playerRunLastFrame=0, playerJumpLastFrame = 0;
    private boolean backWardsIdle = false, backWardsRunning = false;

    private ArrayList<Texture> playerIdle, playerRunning, playerJump;

    private Hud hud;
    private float timeWon;

    /**
     *  largely redundant method for changing the logical size of the player, ignoring his sprite’s size, for collision detection. Only useful for debugging purposes.
     * @param width
     * @param height
     */
    private void setLogicalSize(float width, float height){
        sizeX = width;
        sizeY = height;
    }

    /**
     * getter for lives left
     * @return
     */
    public int getHearts(){
        return hearts;
    }

    /**
     * getter for score
     * @return
     */
    public int getScore(){
        return score;
    }

    /**
     * The constructor initialises the player, and his initial values for score, lives remaining and gets its textures from the textureManager. It also sets up the platform-specific touch detection listeners for Android touchpad and jump buttons.
     * It gets the layers for the purpose of collision detection.
     * @param theGame
     * @param hud
     * @param collisionLayer
     * @param visualLayer
     */
    public Player(final Runnjump theGame, Hud hud, TiledMapTileLayer collisionLayer, TiledMapTileLayer visualLayer){
        this.playerSprite = new Sprite(new Texture("player\\Idle_000.png"));
        this.collisionLayer = collisionLayer;
        this.visualLayer = visualLayer;
        playerSprite.setSize(30*2,30*3);//2 by 3 tiles size
        setLogicalSize(30*2,30*3);
        this.score = 0;
        this.hearts =STARTING_HEARTS;
        this.powerUpTime = 0;
        this.goldKeyAcquired = false;
        this.hud = hud;
        hud.setScore(score);
        hud.setLives(hearts);
        this.alive = true;
        this.soundManager = theGame.soundManager;
        playerIdle = theGame.textureManager.getPlayerFrameSet("idle");
        playerRunning = theGame.textureManager.getPlayerFrameSet("running");
        playerJump = theGame.textureManager.getPlayerFrameSet("jump");
        gameWon = false;
        gravityPowerUp = false;
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

    /**
     * this sets the frame(for the purposes of animation) of the sprite which will be displayed upon drawing.
     * @param texture
     */
    public void setFrame(Texture texture){
        playerSprite.setTexture(texture);
    }

    /**
     *  this draws the sprite after updating.
     * @param batch
     */
    public void draw(Batch batch) {
        update(Gdx.graphics.getDeltaTime());//updates before drawing
        playerSprite.draw(batch);
    }

    /**
     * methods methods used for testing collisions on all sides of the player character, returns true if a collision is found, false if there is no collision. Also detects collectibles and things that kill the player.
     * @return
     */
    private boolean collidesSouth() {
        for(float i = 0; i <= sizeX; i += collisionLayer.getTileWidth()) {
            if(cellKillsPlayer(playerSprite.getX() + i, playerSprite.getY())){
                die();
            }
            if (isCellCollectible(playerSprite.getX() + i, playerSprite.getY())) {
                handleCollectible(playerSprite.getX() + i, playerSprite.getY());
            }
            if(isCellMisc(playerSprite.getX() + i, playerSprite.getY())){
                handleMisc(playerSprite.getX() + i, playerSprite.getY());
            }
            if (isCellBlocked(playerSprite.getX() + i, playerSprite.getY()))
                return true;
        }
        return false;
    }

    /**
     * methods used for testing collisions on all sides of the player character, returns true if a collision is found, false if there is no collision. Also detects collectibles and things that kill the player.
     * @return
     */
    private boolean collidesEast() {
        for(float i = 0; i <= sizeY; i += collisionLayer.getTileHeight()) {
            if(cellKillsPlayer(playerSprite.getX() + sizeX, playerSprite.getY() + i)){
                die();
            }
            if (isCellCollectible(playerSprite.getX() + sizeX, playerSprite.getY() + i)) {
                handleCollectible(playerSprite.getX() + sizeX, playerSprite.getY() + i);
            }
            if(isCellMisc(playerSprite.getX() + sizeX, playerSprite.getY() + i)){
                handleMisc(playerSprite.getX() + sizeX, playerSprite.getY() + i);
            }
            if (isCellBlocked(playerSprite.getX() + sizeX, playerSprite.getY() + i))
                return true;
        }
        return false;
    }

    /**
     * methods used for testing collisions on all sides of the player character, returns true if a collision is found, false if there is no collision. Also detects collectibles and things that kill the player.
     * @return
     */
    private boolean collidesWest() {
        for(float i = 0; i <= sizeY; i += collisionLayer.getTileHeight()) {
            if(cellKillsPlayer(playerSprite.getX(), playerSprite.getY()+i)){
                die();
            }
            if (isCellCollectible(playerSprite.getX(), playerSprite.getY()+i)) {
                handleCollectible(playerSprite.getX(), playerSprite.getY()+i);
            }
            if (isCellMisc(playerSprite.getX(), playerSprite.getY()+i)){
                handleMisc(playerSprite.getX(), playerSprite.getY()+i);
            }
            if (isCellBlocked(playerSprite.getX(), playerSprite.getY() + i))
                return true;
        }
        return false;
    }

    /**
     * methods used for testing collisions on all sides of the player character, returns true if a collision is found, false if there is no collision. Also detects collectibles and things that kill the player.
     * @return
     */
    private boolean collidesNorth() {
        for(float i = 0; i <= sizeX; i += collisionLayer.getTileWidth()) {
            if(cellKillsPlayer(playerSprite.getX() + i, playerSprite.getY()+sizeY)){
                die();
            }
            if (isCellCollectible(playerSprite.getX() + i, playerSprite.getY()+sizeY)) {
                handleCollectible(playerSprite.getX() + i, playerSprite.getY()+sizeY);
            }
            if (isCellMisc(playerSprite.getX() + i, playerSprite.getY()+sizeY)){
                handleMisc(playerSprite.getX() + i, playerSprite.getY()+sizeY);
            }

            if (isCellBlocked(playerSprite.getX() + i, playerSprite.getY() + sizeY))
                return true;
        }
        return false;
    }

    /**
     * used for handling a detected collision with miscellaneous items, currently only the victory point, ie the win flag is handled here. Float x and y are the positions on the tile map where the tile the player collided with is located.
     * @param x the x position of the tile
     * @param y the y position of the tile
     */
    private void handleMisc(float x, float y) {
        TiledMapTileLayer.Cell cellColLayer = collisionLayer.getCell((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());

        if(cellColLayer.getTile().getProperties().containsKey("win_flag")){
            //player wins!
            gameWon = true;
            timeWon = Gdx.graphics.getDeltaTime();
            velocity.x = 0;
            velocity.y = 0;

        }
    }


    private void gravityPowerup(){
        gravityPowerUp = true;
    }

    /**
     * removes the collectible from the specified position
     * @param tileX
     * @param tileY
     */
    private void removeCollectibe(int tileX, int tileY){
        visualLayer.setCell(tileX, tileY, new TiledMapTileLayer.Cell());
        collisionLayer.setCell(tileX,tileY, new TiledMapTileLayer.Cell()); // gets rid of collectible cells
    }

    /**
     * Same as handleMisc but for collectibles such as power-ups, coins, hearts and stars.
     * @param x the x position of the tile
     * @param y the y position of the tile
     */
    private void handleCollectible(float x, float y){
        TiledMapTileLayer.Cell cellColLayer = collisionLayer.getCell((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());

        //removes collectible
        removeCollectibe((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight()); // gets rid of collectible cells

        if (cellColLayer.getTile().getProperties().containsKey("gravity_powerup")){
            //gravity collected;
            gravityPowerup();
            //soundManager.playSound("coin_collect");
        }

        if (cellColLayer.getTile().getProperties().containsKey("star")) {
            score+=10;
            hud.setScore(score);
            soundManager.playRandom("coin_collect");
        }
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


    /**
     * Updates the position of the player, checks all the collisions and determines the frame of animation to be drawn. Additionally checks if a power up was used and grants the player the power up’s effects.
     * @param delta the time since last rendering occured.
     */
    private void update(float delta) {
        time += delta;

        if (gravityPowerUp && powerUpTime < 9) { //power up lasts 9 seconds
            powerUpTime+=delta;
            velocity.y -= (gravity/2)*delta;
        } else{
            velocity.y -= gravity * delta;
            gravityPowerUp = false;
            powerUpTime=0;
        }

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

    /**
     * Similar to its sister methods, checks if the cell specified by the x and y coordinates is miscellaneous.
     * @param x
     * @param y
     * @return
     */
    private boolean isCellMisc(float x, float y){
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
    private boolean isCellCollectible(float x, float y){
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
    private boolean cellKillsPlayer(float x, float y){
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
    private boolean isCellBlocked(float x, float y){
        TiledMapTileLayer.Cell cell =collisionLayer.getCell((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());
        return cell != null && cell.getTile()!=null && cell.getTile().getProperties().containsKey("blocked");
    }

    /**
     * This method allows for keyboard input for controlling the player character on windows desktop.
     * @param keycode
     * @return
     */
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

    /**
     * Getter for player sprite.
     * @return
     */
    public Sprite getPlayerSprite(){
        return playerSprite;
    }

    /**
     * this method stops the movement of the player character when A or D keys stop being held down by the player, on windows.
     * @param keycode
     * @return
     */
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

    /**
     * Checks if player is still alive
     * @return
     */
    public boolean isDead(){
        return !alive;
    }

    /**
     * this method removes a life from the player and sets him as dead.
     */
    private void die(){
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

    /**
     * Checks if the player is currently moving on the x axis.
     * @return
     */
    private boolean isRunning() {
        if ((velocity.x >0.25f || velocity.x < -0.25f)&&(velocity.y < 3f && velocity.y>-3f)){
            return true;
        }
        return false;
    }

    /**
     * Checks if the player is currently in the air.
     * @return
     */
    private boolean inAir(){
        return (velocity.y > 3f || velocity.y<-3f);
    }

    /**
     * checks if the player is currently idle
     * @return
     */
    private boolean isIdle() {
        if (!isRunning()){
            return true;
        }
        return false;
    }

    /**
     * THis method is called when the player is respawning, if no lives left false is returned.
     * @return
     */
    public boolean respawn() {
        //returns true if the player can respawn ie has lives left
        if (hearts >= 0) {
            alive = true;
            return true;
        }
        return false;
    }

    /**
     * this method is used for setting the layers of the tile map which need to be checked for collisions for the player.
     * @param visualLayer
     * @param collisionLayer
     */
    public void setLayers(TiledMapTileLayer visualLayer, TiledMapTileLayer collisionLayer) {
        this.visualLayer = visualLayer;
        this.collisionLayer = collisionLayer;
    }

    /**
     * This method is used for restarting the player character to its default state, is called when the level is restarted.
     */
    public void restart() {
        hearts = STARTING_HEARTS;
        hud.setLives(STARTING_HEARTS);
        score = 0;
        hud.setScore(0);
    }

    /**
     * Checks if the game has been won.
     * @return
     */
    public boolean isGameWon(){
        return gameWon;
    }

    /**
     * Returns the time the game had been won at.
     * @return
     */
    public float getTimeWon() {
        return timeWon;
    }
}
