package com.mygdx.runnjump.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.runnjump.Runnjump;
import com.mygdx.runnjump.screens.GameScreen;
import com.mygdx.runnjump.util.DialogueManager;
import com.mygdx.runnjump.util.SoundManager;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * This class represents the player character on the game map.
 */
public class Player extends MovingActor implements InputProcessor {

    protected boolean touchingNPC;
    String npcName, npcAssetName;

    protected boolean gravityPowerUp;
    protected boolean superSpeedPU;
    protected boolean ghostWalkPU;
    protected int gravityPowerUps;
    protected int superSpeedPowerUps;
    protected int ghostWalkPowerUps;

    protected float gravityPowerUpTime;
    private float speedTime;
    private float ghostWalkPUTime;

    protected int score, hearts;
    protected final int STARTING_HEARTS = 3;
    protected boolean canJump;
    protected SoundManager soundManager;
    protected DialogueManager dialogueManager;
    protected boolean gameWon;
    private boolean collisionX = false, collisionY = false;

    protected int playerIdleLastFrame=0,playerRunLastFrame=0, playerJumpLastFrame = 0;

    protected ArrayList<Texture> playerIdle, playerRunning, playerJump;

    protected Hud hud;
    protected float timeWon;
    protected long collisionSouthTime =0;
    private boolean dKeyHeld;
    private boolean aKeyHeld;
    Runnjump theGame;
    private boolean dialogueMode = false;
    private TreeMap<String, Integer> dialogueContext;
    private TreeMap<String, Boolean> conditionsMet;
    private GameObject npcTouched;


    /**
     * getter for lives left
     *
     * @return int int
     */
    public int getHearts(){
        return hearts;
    }

    /**
     * getter for score
     *
     * @return int int
     */
    public int getScore(){
        return score;
    }

    /**
     * The constructor initialises the player, and his initial values for score, lives remaining and gets its textures from the textureManager. It also sets up the platform-specific touch detection listeners for Android touchpad and jump buttons.
     * It gets the layers for the purpose of collision detection.
     *
     * @param theGame        the the game
     * @param hud            the hud
     * @param collisionLayer the collision layer
     * @param visualLayer    the visual layer
     */
    public Player(final Runnjump theGame, Hud hud, TiledMapTileLayer collisionLayer, TiledMapTileLayer visualLayer){
        super(collisionLayer, visualLayer);
        getSprite().setSize(30*2,30*3);//2 by 3 tiles size
        setLogicalSize(42,85); //little less than 2 tiles by 3 tiles
        this.score = 0;
        this.hearts =STARTING_HEARTS;
        this.gravityPowerUpTime = 0;
        this.hud = hud;
        dialogueContext = new TreeMap<String, Integer>();
        conditionsMet = new TreeMap<>();
        this.theGame = theGame;
        hud.setScore(score);
        hud.setLives(hearts);
        this.soundManager = theGame.soundManager;
        this.dialogueManager = DialogueManager.getManager();
        playerIdle = theGame.textureManager.getFrameSet("player_idle");
        playerRunning = theGame.textureManager.getFrameSet("player_running");
        playerJump = theGame.textureManager.getFrameSet("player_jump");
        gameWon = false;
        gravityPowerUp = false;
        dKeyHeld = false;
        aKeyHeld = false;
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            Touchpad joystick = hud.getMovementJoystick();
            joystick.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {

                    if(((Touchpad)actor).getKnobPercentX() >0){ // right
                        dKeyHeld = true;
                        aKeyHeld = false;
                        if (!facingRight) {
                            getSprite().flip(true, false);
                            facingRight = true;
                        }
                    } else if (((Touchpad)actor).getKnobPercentX() < 0){ // left
                        aKeyHeld = true;
                        dKeyHeld = false;
                        if(facingRight){
                            getSprite().flip(true, false);
                            facingRight=false;
                        }
                    } else if(((Touchpad)actor).getKnobPercentX()==0.0){ // centre
                        if (dKeyHeld){
                            dKeyHeld = false;
                        }
                        if(aKeyHeld){
                            aKeyHeld = false;
                        }
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
        gravityPowerUps = 22;// put these to 0 on release todo
        superSpeedPowerUps = 22;
        ghostWalkPowerUps = 22;
        ghostWalkPU = false;
        superSpeedPU = false;
        touchingNPC = false;
        speedTime = 0;
        ghostWalkPUTime = 0;

    }

    @Override
    public boolean collidesEast() {
        float sizeX = this.sizeX -8;
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
     * used for handling a detected collision with miscellaneous items, currently only the victory point, ie the win flag is handled here. Float x and y are the positions on the tile map where the tile the player collided with is located.
     * @param x the x position of the tile
     * @param y the y position of the tile
     */
    @Override
    protected void handleMisc(float x, float y) {
        TiledMapTileLayer.Cell cellColLayer = collisionLayer.getCell((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());

        if(cellColLayer.getTile().getProperties().containsKey("win_flag")){
            //player wins!
            gameWon = true;
            timeWon = Gdx.graphics.getDeltaTime();
            velocity.x = 0;
            velocity.y = 0;

        }
    }


    protected void gravityPowerup(){
        gravityPowerUp = true;
    }




    /**
     * removes the collectible from the specified position
     * @param tileX
     * @param tileY
     */
    protected void removeCollectibe(int tileX, int tileY){
        visualLayer.setCell(tileX, tileY, new TiledMapTileLayer.Cell());
        collisionLayer.setCell(tileX,tileY, new TiledMapTileLayer.Cell()); // gets rid of collectible cells
    }

    /**
     * Same as handleMisc but for collectibles such as power-ups, coins, hearts and stars.
     * @param x the x position of the tile
     * @param y the y position of the tile
     */
    @Override
    protected void handleCollectible(float x, float y){
        TiledMapTileLayer.Cell cellColLayer = collisionLayer.getCell((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());

        //removes collectible
        removeCollectibe((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight()); // gets rid of collectible cells

        if (cellColLayer.getTile().getProperties().containsKey("gravity_powerup")){
            //gravity collected;
            gravityPowerUps += 1;
            ((GameScreen) theGame.getCurrentScreen()).createLongToast("Gravity power-up acquired!");
            //soundManager.playSound("coin_collect");
        }

        if (cellColLayer.getTile().getProperties().containsKey("star")) {
            score+=10;
            ((GameScreen) theGame.getCurrentScreen()).createShortToast("+10 score");
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
            ((GameScreen) theGame.getCurrentScreen()).createLongToast("+1 lives");

            soundManager.play("heart_collect");
        }
        if (cellColLayer.getTile().getProperties().containsKey("gold_key")){
            ((GameScreen) theGame.getCurrentScreen()).createShortToast("Golden key acquired!");
            handleKey("gold_key");

            soundManager.play("collect_item");
        }

        removeCollectibles(x, y);

    }

    private void removeCollectibles(float x, float y) {
        if (isCellCollectible((int) x + 33, (int) y)) {
            removeCollectibe((int) (x + 33) / collisionLayer.getTileWidth(), (int) y / collisionLayer.getTileHeight()); //checks cell to the right
        }
        if (isCellCollectible((int) x - 33, (int) y)) {
            removeCollectibe((int) (x - 33) / collisionLayer.getTileWidth(), (int) y / collisionLayer.getTileHeight());
        }
        ; //checks cell to the left

        if (isCellCollectible((int) x, (int) y + 33)) {
            removeCollectibe((int) x / collisionLayer.getTileWidth(), (int) (y + 33) / collisionLayer.getTileHeight());// checks cell above
        }
        if (isCellCollectible(x, (int) y - 33)) {
            removeCollectibe((int) x / collisionLayer.getTileWidth(), (int) (y - 33) / collisionLayer.getTileHeight());
        }
        ;//checks cell below
        if (isCellCollectible(x - 33, y)) {
            removeCollectibe((int) (x - 33) / collisionLayer.getTileWidth(), (int) y / collisionLayer.getTileHeight());
        }
        ; // checks cell to the left and below
        if (isCellCollectible(x - 33, y + 33)) {
            removeCollectibe((int) (x - 33) / collisionLayer.getTileWidth(), (int) (y + 33) / collisionLayer.getTileHeight());
        }
        ;//checks cell to the left and above
        if (isCellCollectible(x + 33, y - 33)) {
            removeCollectibe((int) (x + 33) / collisionLayer.getTileWidth(), (int) (y - 33) / collisionLayer.getTileHeight());// checks cell to the left and below
        }
        if (isCellCollectible(x + 33, y + 33)) {
            removeCollectibe((int) (x + 32) / collisionLayer.getTileWidth(), (int) (y + 33) / collisionLayer.getTileHeight());//checks cell above and to the right
        }
        if (isCellCollectible(x - 33, y - 33)) {
            removeCollectibe((int) (x - 33) / collisionLayer.getTileWidth(), (int) (y - 33) / collisionLayer.getTileHeight()); // checks cell to the left and below
        }
    }

    /**
     * Handles unlocking of doors via key
     * @param key
     */
    private void handleKey(String key) {
        ArrayList<TiledMapTileLayer.Cell> blockedCells = ((GameScreen)theGame.getCurrentScreen()).getBlockedCells(key);
        for(TiledMapTileLayer.Cell cell: blockedCells){
            cell.getTile().getProperties().remove("blocked");
            cell.setFlipHorizontally(true);
        }

        //visualLayer.getCell().getTile(key)
    }


    /**
     * Updates the position of the player, checks all the collisions and determines the frame of animation to be drawn. Additionally checks if a power up was used and grants the player the power up’s effects.
     * @param delta the time since last rendering occured.
     */
    @Override
    protected void update(float delta) {
        super.update(delta);

        // saves previous position
        float oldX = getSprite().getX(), oldY = getSprite().getY();

        collisionX = false;
        collisionY = false;

        // move horizontally
        getSprite().setX(getSprite().getX() + velocity.x * delta);

        if(ghostWalkPU && ghostWalkPUTime < 4) { // ghost walk disables left/right collisions
            ghostWalkPUTime += delta;
        } else { // maybe keep checking for collisions and have ghost walk active while inside a wall?
            if (velocity.x < 0) // going left
                collisionX = collidesWest();
            else if (velocity.x > 0) // going right
                collisionX = collidesEast();
            if(!collidesEast() && !collidesWest()) {
                ghostWalkPU = false;
                ghostWalkPUTime = 0;
            }
        }

        if (gravityPowerUp && gravityPowerUpTime < 9) { //power up lasts 9 seconds
            gravityPowerUpTime += delta;
            velocity.y -= (gravity / 2) * delta;
        } else {
            velocity.y -= gravity * delta;
            gravityPowerUp = false;
            gravityPowerUpTime = 0;
        }


        // x collision handling
        if (collisionX) {
            getSprite().setX(oldX);
            velocity.x = 0;
        } else {
            if (dKeyHeld) {
                if (superSpeedPU && speedTime < 9){
                    velocity.x = speedX*2;
                    speedTime += delta;
                } else {
                    velocity.x = speedX;
                    speedTime = 0;
                    superSpeedPU = false;
                }
            }
            if (aKeyHeld) {
                if(superSpeedPU && speedTime < 9) {
                    velocity.x = -speedX*2;
                    speedTime += delta;
                } else {
                    velocity.x = -speedX;
                    speedTime = 0;
                    superSpeedPU = false;

                }
            }
            if (aKeyHeld && dKeyHeld) {
                velocity.x = 0;
            }
            if (!aKeyHeld && !dKeyHeld) {
                velocity.x = 0;
            }
        }
        // move on y
        getSprite().setY(getSprite().getY() + velocity.y * delta * 5f);

        if (velocity.y < 2.5f) {
            collisionY = collidesSouth();
            if (collisionY) {
                collisionSouthTime = TimeUtils.millis();
            }
        } else if (velocity.y > 2.5f) {
            collisionY = collidesNorth();
        }

        if (TimeUtils.millis() - collisionSouthTime < 300) {
            canJump = true;
        } else {
            canJump = false;
            collisionSouthTime = 0;

        }

        if (collisionY) {
            getSprite().setY(oldY);
            velocity.y = 0;
        }

        if (time > 0.15f ){
            touchingNPC = false;
        }

        determineFrame();

    }

    @Override
    protected void determineFrame() {
        if (this.isIdle() && time > 0.2f) {
            playerJumpLastFrame = 0;

            this.setFrame(playerIdle.get(playerIdleLastFrame));
            if (playerIdleLastFrame == playerIdle.size() - 1) {
                backWardsIdle = true;
            }
            if (playerIdleLastFrame == 0) {
                backWardsIdle = false;
            }
            if (backWardsIdle) {
                playerIdleLastFrame--;
            } else {
                playerIdleLastFrame++;
            }
            time = 0;
        } else if (isRunning() && time > 0.15f) {
            playerJumpLastFrame = 0;

            this.setFrame(playerRunning.get(playerRunLastFrame));
            if (playerRunLastFrame == 0) {
                backWardsRunning = false;
            }
            if (playerRunLastFrame == playerRunning.size() - 1) {//if at last element
                playerRunLastFrame = 0;
            }
            playerRunLastFrame++;
            time = 0;
        } else if (inAir() && time > 0.06f) {
            this.setFrame(playerJump.get(playerJumpLastFrame));
            if (playerJumpLastFrame == playerJump.size() - 1) {
            } else {
                playerJumpLastFrame++;
            }
            time = 0;
        } else if (playerJumpLastFrame == playerJump.size() - 1 && !inAir() && !isRunning() && collisionY) {
            this.setFrame(playerIdle.get(playerIdleLastFrame));
        }
    }

    @Override
    public void collidesObject(GameObject other, float delta){
        super.collidesObject(other, delta);
        if(other instanceof Hedgehog){
            die();
        } else if (other instanceof NPC){
            touchingNPC = true;
            npcTouched = other;
            npcName = ((NPC) other).getNpcName();
            npcAssetName = ((NPC) other).getAssetName();
        }
    }

    /**
     * This method allows for keyboard input for controlling the player character on windows desktop.
     * @param keycode
     * @return
     */
    @Override
    public boolean keyDown(int keycode) {
        switch(keycode) {
            case Input.Keys.SPACE:
                if (dialogueMode) {
                    //Go to the next dialogue
                    hud.progressDialogue(npcAssetName, this);
                } else if(touchingNPC) {
                    if (dialogueContext.containsKey(npcAssetName)) {
                        hud.showDialogue(npcAssetName, dialogueContext.get(npcAssetName));
                    } else {
                        hud.showDialogue(npcAssetName, 1);
                    }
                    dialogueMode = true;
                    dKeyHeld = false;
                    aKeyHeld = false;
                }
                break;
            case Input.Keys.W:
                if(canJump && !dialogueMode) {
                    velocity.y = speedY / 1.8f;
                    canJump = false;
                }
                break;
            case Input.Keys.D:
                //velocity.x = speedX;
                if(!dialogueMode) {
                    dKeyHeld = true;
                    if (!facingRight) {
                        getSprite().flip(true, false);
                        facingRight = true;
                    }
                }
                break;
            case Input.Keys.A:
                if(!dialogueMode) {
                    aKeyHeld = true;
                    if (facingRight) {
                        getSprite().flip(true, false);
                        facingRight = false;
                    }
                }
                break;

            case Input.Keys.NUM_1://power up 1 activated
                if(gravityPowerUps > 0){
                    gravityPowerup();
                    ((GameScreen) theGame.getCurrentScreen()).createLongToast("Gravity power-up has been activated!");
                    gravityPowerUps -= 1;
                }
                break;
            case Input.Keys.NUM_2:
                if(superSpeedPowerUps > 0){
                    superSpeedPU();
                    ((GameScreen) theGame.getCurrentScreen()).createLongToast("Super speed power-up has been activated!");
                    superSpeedPowerUps -= 1;
                }
                break;
            case Input.Keys.NUM_3:
                if(ghostWalkPowerUps > 0){
                    ghostWalkPU();
                    ((GameScreen) theGame.getCurrentScreen()).createLongToast("Ghost walk power-up has been activated!");
                    ghostWalkPowerUps -= 1;
                }
                break;
        }
        touchingNPC = false;
        return true;
    }

    private void ghostWalkPU() {
        ghostWalkPU = true;
    }

    private void superSpeedPU() {
        superSpeedPU = true;
    }


    /**
     * this method removes a life from the player and sets him as dead.
     */
    @Override
    protected void die(){
        if(alive) {
            hearts--;
            if (hearts >= 0) {
                hud.setLives(hearts);
                SoundManager.getManager().playRandom("male_death");
            } else {
                hud.setLives(0);
            }
            velocity.y = 0;
            velocity.x = 0;
            super.die();
        }
    }

    /**
     * THis method is called when the player is respawning, if no lives left false is returned.
     *
     * @return boolean boolean
     */
    @Override
    public boolean respawn() {
        //returns true if the player can respawn ie has lives left
        if (hearts >= 0) {
            alive = true;
            return true;
        }
        return false;
    }

    /**
     * This method is used for restarting the player character to its default state, is called when the level is restarted.
     */
    public void restart() {
        hearts = STARTING_HEARTS;
        hud.setLives(STARTING_HEARTS);
        gravityPowerUps = 0;
        dialogueContext.clear();//clears the dialogues
        conditionsMet.clear();//clears met conditions
        score = 0;
        hud.setScore(0);
    }

    /**
     * Checks if the game has been won.
     *
     * @return boolean boolean
     */
    public boolean isGameWon(){
        return gameWon;
    }

    /**
     * Returns the time the game had been won at.
     *
     * @return time won
     */
    public float getTimeWon() {
        return timeWon;
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
                if (dKeyHeld){
                    dKeyHeld = false;
                }

                break;
            case Input.Keys.A:
                if (aKeyHeld){
                    aKeyHeld = false;
                }
                break;
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

    public void setDialogueMode(boolean b) {
        this.dialogueMode = b;
    }

    public void killNPC() {
        npcTouched.die();
        if(npcTouched instanceof NPC){
            SoundManager.getManager().play("heart_collect");
            ((GameScreen) theGame.getCurrentScreen()).createLongToast(npcName + " has been rescued!");
        }
    }

    public void setDialogueContext(String dialogueAsset, int dialogueStart) {
        dialogueContext.put(dialogueAsset, dialogueStart);
    }

    public void grantCondition(String tag, boolean condition){
        conditionsMet.put(tag,condition);
    }

    public void getGift(String name, int amount){
        if(name.equals("SCORE")){
            score+=amount;
            ((GameScreen) theGame.getCurrentScreen()).createShortToast("+"+amount + " score");
            hud.setScore(score);
            soundManager.playRandom("coin_collect");
        } else if(name.equals("HEARTS")){
            hearts += amount;
            ((GameScreen) theGame.getCurrentScreen()).createShortToast("+"+amount + " lives");
            hud.setScore(hearts);
        }
    }

    public boolean conditionMet(String tag) {
        if(conditionsMet.containsKey(tag)){
            return true;
        }
        return false;
    }
}
