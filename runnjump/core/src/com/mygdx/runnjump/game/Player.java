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
import com.mygdx.runnjump.util.Position;
import com.mygdx.runnjump.util.SoundManager;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * This class represents the player character on the game map.
 */
public class Player extends MovingActor implements InputProcessor {

    public boolean touchingNPC;
    String npcName, npcAssetName;

    protected boolean gravityPowerUp;
    protected boolean superSpeedPU;
    protected boolean ghostWalkPU;

    protected float gravityPowerUpTime;
    private float speedTime;
    private float ghostWalkPUTime;
    private float invincibilityPUTime;
    private float rockThrowingPUTime;

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
    public  boolean dialogueMode = false;
    private TreeMap<String, Integer> dialogueContext;
    private TreeMap<String, Boolean> conditionsMet;
    private GameObject npcTouched;
    Inventory playerInventory;
    private boolean invincibilityPU;
    private boolean rockThrowingPU;
    Projectile rock;
    private int banditsKilled;


    /**
     * The constructor initialises the player, and his initial values for score, lives remaining and gets its textures from the textureManager. It also sets up the platform-specific touch detection listeners for Android touchpad and jump buttons.
     * It gets the layers for the purpose of collision detection.
     *
     * @param theGame        the the game
     * @param hud            the hud
     * @param collisionLayer the collision layer
     * @param visualLayer    the visual layer
     */
    public Player(final Runnjump theGame, final Hud hud, TiledMapTileLayer collisionLayer, TiledMapTileLayer visualLayer){
        super(collisionLayer, visualLayer);
        getSprite().setSize(30*2,30*3);//2 by 3 tiles size
        setLogicalSize(42,85); //little less than 2 tiles by 3 tiles
        this.gravityPowerUpTime = 0;
        this.hud = hud;
        dialogueContext = new TreeMap<String, Integer>();
        conditionsMet = new TreeMap<>();
        this.theGame = theGame;
        this.soundManager = theGame.soundManager;
        this.dialogueManager = DialogueManager.getManager();
        playerIdle = theGame.textureManager.getFrameSet("player_idle");
        playerRunning = theGame.textureManager.getFrameSet("player_running");
        playerJump = theGame.textureManager.getFrameSet("player_jump");
        gameWon = false;
        dKeyHeld = false;
        aKeyHeld = false;
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            Touchpad joystick = hud.getMovementJoystick();
            Button interactBt = hud.getInteractBt();
            final Player player = this;
            interactBt.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    player.dialogueManage();
                    if(rockThrowingPU) {
                        //throw rock
                        throwRock();
                    }
                }
            });

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

        ghostWalkPU = false;
        superSpeedPU = false;
        invincibilityPU = false;
        rockThrowingPU = false;

        touchingNPC = false;
        speedTime = 0;
        ghostWalkPUTime = 0;
        rockThrowingPUTime = 8;// rock throwing lasts for 8 seconds
        invincibilityPUTime = 8;
        gravityPowerUp = false;

        banditsKilled = 0;
        playerInventory = new Inventory(hud,true);
        hud.updatePowerUpIndicator(playerInventory.getPowerUps());
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

        } else if(cellColLayer.getTile().getProperties().containsKey("checkpoint_flag")){
            //player spawn point changed on the level.
            collisionLayer.setCell(((int) x/collisionLayer.getTileWidth()), ((int) y/collisionLayer.getTileHeight()), new TiledMapTileLayer.Cell());

            ((GameScreen) theGame.getCurrentScreen()).setSpawnPoint(x,y);
            ((GameScreen) theGame.getCurrentScreen()).createShortToast("Checkpoint activated!");


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
        removeCollectibles(x, y);

        if (cellColLayer.getTile().getProperties().containsKey("gravity_powerup")){
            playerInventory.gainPowerUp("gravity");

            ((GameScreen) theGame.getCurrentScreen()).createLongToast("Gravity power-up acquired!");
            soundManager.play("heart_collect");
        }

        if (cellColLayer.getTile().getProperties().containsKey("superspeed_powerup")){
            playerInventory.gainPowerUp("speed");

            ((GameScreen) theGame.getCurrentScreen()).createLongToast("Super-speed power-up acquired!");
            soundManager.play("heart_collect");
        }

        if (cellColLayer.getTile().getProperties().containsKey("invincibility_powerup")){
            playerInventory.gainPowerUp("invincibility");

            ((GameScreen) theGame.getCurrentScreen()).createLongToast("Invincibility power-up acquired!");
            soundManager.play("heart_collect");
        }

        if (cellColLayer.getTile().getProperties().containsKey("rocks_powerup")){
            playerInventory.gainPowerUp("rocks");

            ((GameScreen) theGame.getCurrentScreen()).createLongToast("Rock-throwing power-up acquired!");
            soundManager.play("heart_collect");
        }

        if (cellColLayer.getTile().getProperties().containsKey("ghostwalk_powerup")){
            playerInventory.gainPowerUp("ghostwalk");

            ((GameScreen) theGame.getCurrentScreen()).createLongToast("Ghost-walk power-up acquired!");
            soundManager.play("heart_collect");
        }



        if (cellColLayer.getTile().getProperties().containsKey("star")) {
            playerInventory.addScore(10);
            ((GameScreen) theGame.getCurrentScreen()).createShortToast("+10 score");

            soundManager.playRandom("coin_collect");
        }
        if (cellColLayer.getTile().getProperties().containsKey("coin")){
            playerInventory.addScore(1);
            soundManager.playRandom("coin_collect");
        }
        if (cellColLayer.getTile().getProperties().containsKey("heart")){
            playerInventory.addLives(1);
            ((GameScreen) theGame.getCurrentScreen()).createLongToast("+1 lives");

            soundManager.play("heart_collect");
        }
        if (cellColLayer.getTile().getProperties().containsKey("gold_key")){
            ((GameScreen) theGame.getCurrentScreen()).createShortToast("Golden key acquired!");
            handleKey("gold_key");

            soundManager.play("collect_item");
        }

        if (cellColLayer.getTile().getProperties().containsKey("silver_key")){
            ((GameScreen) theGame.getCurrentScreen()).createShortToast("Silver key acquired!");
            handleKey("silver_key");

            soundManager.play("collect_item");
        }


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
            collisionX = false;
        } else { // maybe keep checking for collisions and have ghost walk active while inside a wall?
            if (velocity.x < 0) // going left
                collisionX = collidesWest();
            else if (velocity.x > 0) // going right
                collisionX = collidesEast();
            if(ghostWalkPU && !collidesEast() && !collidesWest() && !collidesNorth() && !collidesSouth()) {
                ghostWalkPU = false;
                ghostWalkPUTime = 0;
            } else {
                //inside a wall but power-up should run out
                if (ghostWalkPUTime > 4){
                    ghostWalkPUTime -= 0.2;
                }
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

        checkPowerUps();


        if(invincibilityPU) {
            invincibilityPUTime -= delta;
            if (invincibilityPUTime <= 0){
                invincibilityPU = false;
                invincibilityPUTime = 8;
            }
        }
        if(rockThrowingPU){
            rockThrowingPUTime -=delta;
            if(rockThrowingPUTime <= 0){
                rockThrowingPU = false;
                rockThrowingPUTime = 8;
            }
        }
    }

    private void checkPowerUps() {
        if(hud.usedPowerUp){
            if (hud.usedPowerUpStr.equals("gravity") && playerInventory.hasPowerUp("gravity")){
                gravityPowerup();
                ((GameScreen) theGame.getCurrentScreen()).createLongToast("Gravity power-up has been activated!");
                playerInventory.usePowerUp("gravity");
                hud.usedPowerUp = false;
                hud.usedPowerUpStr = "";
            }
            if (hud.usedPowerUpStr.equals("speed") && playerInventory.hasPowerUp("speed")){
                superSpeedPU();
                ((GameScreen) theGame.getCurrentScreen()).createLongToast("Super-speed power-up has been activated!");
                playerInventory.usePowerUp("speed");
                hud.usedPowerUp = false;
                hud.usedPowerUpStr = "";
            }
            if (hud.usedPowerUpStr.equals("invincibility") && playerInventory.hasPowerUp("invincibility")){
                invincibilityPU();
                ((GameScreen) theGame.getCurrentScreen()).createLongToast("Invincibility power-up has been activated!");
                playerInventory.usePowerUp("invincibility");
                hud.usedPowerUp = false;
                hud.usedPowerUpStr = "";
            }
            if (hud.usedPowerUpStr.equals("ghostwalk") && playerInventory.hasPowerUp("ghostwalk")){
                ghostWalkPU();
                ((GameScreen) theGame.getCurrentScreen()).createLongToast("Ghost-walk power-up has been activated!");
                playerInventory.usePowerUp("ghostwalk");
                hud.usedPowerUp = false;
                hud.usedPowerUpStr = "";
            }

            if (hud.usedPowerUpStr.equals("rocks") && playerInventory.hasPowerUp("rocks")){
                rocksPU();
                ((GameScreen) theGame.getCurrentScreen()).createLongToast("Rock throwing power-up has been activated!");
                playerInventory.usePowerUp("rocks");
                hud.usedPowerUp = false;
                hud.usedPowerUpStr = "";
            }
        }
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
        } else if (other instanceof Bandit) {
            Bandit bandit = (Bandit) other;
            if (bandit.isAttacking() && bandit.getSprite().getY()+50 > getSprite().getY()){
                //if the bandit is attacking, the player dies by colliding with the sword assuming the player is not jumping on top of its head
                System.out.println("Bandit sword kills player");
                die();
            }
        }else if (other instanceof TurtleMan) {
            TurtleMan turtle = (TurtleMan) other;
            if (turtle.isAttacking() && turtle.getSprite().getY() + 50 > getSprite().getY()) {
                //if the bandit is attacking, the player dies by colliding with the sword assuming the player is not jumping on top of its head
                System.out.println("Turtleman kills player");
                die();
            }
        }else if (other instanceof NPC){
            touchingNPC = true;
            npcTouched = other;
            npcName = ((NPC) other).getNpcName();
            npcAssetName = ((NPC) other).getAssetName();
        } else if (other instanceof Projectile && !((Projectile) other).playerBullet){
            die();
            System.out.println("Bullet kills player!!");
        }
    }


    public void dialogueManage(){
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
                dialogueManage();
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
                if(playerInventory.hasPowerUp("gravity")){
                    gravityPowerup();
                    ((GameScreen) theGame.getCurrentScreen()).createLongToast("Gravity power-up has been activated!");
                    playerInventory.usePowerUp("gravity");
                }
                break;
            case Input.Keys.NUM_2:
                if(playerInventory.hasPowerUp("speed")){
                    superSpeedPU();
                    ((GameScreen) theGame.getCurrentScreen()).createLongToast("Super speed power-up has been activated!");
                    playerInventory.usePowerUp("speed");
                }
                break;
            case Input.Keys.NUM_3:
                if(playerInventory.hasPowerUp("ghostwalk")){
                    ghostWalkPU();
                    ((GameScreen) theGame.getCurrentScreen()).createLongToast("Ghost walk power-up has been activated!");
                    playerInventory.usePowerUp("ghostwalk");
                }
                break;
            case Input.Keys.NUM_4:
                if(playerInventory.hasPowerUp("invincibility")){
                    invincibilityPU();
                    ((GameScreen) theGame.getCurrentScreen()).createLongToast("Invincibility power-up has been activated!");
                    playerInventory.usePowerUp("invincibility");
                }
                break;
            case Input.Keys.NUM_5:
                if(!rockThrowingPU) {
                    if (playerInventory.hasPowerUp("rocks")) {
                        rocksPU();
                        ((GameScreen) theGame.getCurrentScreen()).createLongToast("Rock-throwing power-up has been activated!");
                        playerInventory.usePowerUp("rocks");
                    }
                } else {
                    //throw rock
                    throwRock();
                }
                break;
        }
        touchingNPC = false;
        return true;
    }

    public Projectile getProjectile(){
        Projectile temp = rock;
        rock = null;

        return temp;
    }

    private void throwRock() {
        rock = new Projectile(collisionLayer,visualLayer,new Position(sprite.getX(), sprite.getY()),true);
        if(facingRight) {
            rock.setDirection("right");
        } else {
            rock.setDirection("left");
        }


    }

    private void rocksPU(){
        invincibilityPUTime = 8;
        rockThrowingPU = true;
    }

    private void invincibilityPU() {
        invincibilityPUTime = 8;
        invincibilityPU = true;
    }

    private void ghostWalkPU() {
        ghostWalkPU = true;
        collisionX = false;
    }

    private void superSpeedPU() {
        superSpeedPU = true;
    }


    /**
     * this method removes a life from the player and sets him as dead.
     */
    @Override
    protected void die(){
        if(alive && !invincibilityPU) {
            playerInventory.removeLife();
            if (playerInventory.getLives() >= 0) {
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
        if (playerInventory.getLives() >= 0) {
            alive = true;
            return true;
        }
        return false;
    }

    /**
     * This method is used for restarting the player character to its default state, is called when the level is restarted.
     */
    public void restart() {
        playerInventory.restart();
        dialogueContext.clear();//clears the dialogues
        conditionsMet.clear();//clears met conditions
        banditsKilled = 0;
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
            playerInventory.addScore(amount);
            ((GameScreen) theGame.getCurrentScreen()).createShortToast("+"+amount + " score");
            soundManager.playRandom("coin_collect");
        } else if(name.equals("HEARTS")){
            playerInventory.addLives(amount);
            ((GameScreen) theGame.getCurrentScreen()).createShortToast("+"+amount + " lives");
        } else if(name.contains("POWERUP")){
            if (name.contains("SPEED")){
                for(int i = 0; i < amount; i++) {
                    playerInventory.gainPowerUp("speed");
                }
            } else if (name.contains("GRAVITY")){
                for(int i = 0; i < amount; i++) {
                    playerInventory.gainPowerUp("gravity");
                }
            } else if (name.contains("GHOSTWALK")){
                for(int i = 0; i < amount; i++) {
                    playerInventory.gainPowerUp("ghostwalk");
                }
            } else if (name.contains("INVINCIBILITY")){
                for(int i = 0; i < amount; i++) {
                    playerInventory.gainPowerUp("invincibility");
                }
            } else if (name.contains("ROCKS")){
                for(int i = 0; i < amount; i++) {
                    playerInventory.gainPowerUp("rocks");
                }
            }
        }
    }

    public boolean conditionMet(String tag) {
        if(conditionsMet.containsKey(tag) && conditionsMet.get(tag)){
            return true;
        }
        return false;
    }
    public Inventory getInventory(){
        return playerInventory;
    }

    public void gainScore(int i) {
        playerInventory.addScore(i);
    }

    public void killedBandit(String message) {
        ((GameScreen) theGame.getCurrentScreen()).createShortToast(message);
        banditsKilled += 1;
        if(banditsKilled >= 5){
            grantCondition("QUEST1LVL2",true);
        }

    }

}
