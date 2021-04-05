package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.runnjump.Runnjump;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class represents the loading screen which is shown when the game begins, currently it has no look of its own but this class does all the loading of assets necessary for the rest of the game.
 */
public class LoadingScreen extends ScreenBase implements Screen {

    AtomicBoolean stillLoading;
    Sprite background;
    /**
     * Instantiates a new Loading screen.
     *
     * @param theGameO the the game o
     */
    public LoadingScreen(Runnjump theGameO) {
        super(theGameO);
        stillLoading = new AtomicBoolean(true);
        Texture loadingScreenImg = new Texture("background.png");
        background = new Sprite(loadingScreenImg);
    }

    /**
     *  This loads all the player assets and all the frames of animation.
     */
    private void loadPlayer(){
        //idle
        theGame.textureManager.addFrameAssetSet("player_idle","player\\Idle_",12);
        theGame.textureManager.addFrameAssetSet("player_running","player\\Running_",12);
        theGame.textureManager.addFrameAssetSet("player_jump","player\\Jump Start_",6);

    }

    private void loadNPCAssets(){
        theGame.textureManager.addFrameAssetSet("oldguy", "oldguy\\Idle_",18);
        theGame.textureManager.addFrameAssetSet("damsel", "damsel\\Idle Blinking_", 24);


    }

    private void loadPowerUps(){
        theGame.textureManager.addAsset("gravity_powerup", "powerups\\gravity");
        theGame.textureManager.addAsset("speed_powerup", "powerups\\speed");
        theGame.textureManager.addAsset("invincibility_powerup", "powerups\\invincibility");
        theGame.textureManager.addAsset("ghostwalk_powerup", "powerups\\ghostwalk");
    }

    private void loadEnemyAssets(){
        theGame.textureManager.addFrameAssetSet("hedgehog_moving", "hedgehog\\Walking_", 18);
        theGame.textureManager.addFrameAssetSet("hedgehog_idle", "hedgehog\\Idle_Blinking_", 12);

        theGame.textureManager.addFrameAssetSet("bandit_moving", "bandit\\Running_", 12);
        theGame.textureManager.addFrameAssetSet("bandit_idle", "bandit\\Idle Blinking_", 18);
        theGame.textureManager.addFrameAssetSet("bandit_attacking", "bandit\\Slashing_", 12);

    }

    /**
     *
     */
    private void loadTextAssets(){
        theGame.dialogueManager.addAsset("oldguy", "level1greeting");
        theGame.dialogueManager.addAsset("damsel", "level1damsel");
    }

    /**
     * this loads all the graphical elements of the game aside from the skin, the tilemap and its map elements themselves.
     */
    private void loadGraphics(){
        //TextureAtlas playerRunning = new TextureAtlas(Gdx.files.internal("player\\runningCharSet.atlas"));

        //Runnjump.textureAtlasMap.put("player_running", playerRunning);
        loadPlayer();
    }

    /**
     * this loads all the sound effects of the game.
     */
    private void loadSoundFX(){
        String[] buttonClickFx = new String[4];
        buttonClickFx[0] = "ui_button_simple_click_01.wav";
        buttonClickFx[1] = "ui_button_simple_click_02.wav";
        buttonClickFx[2] = "ui_button_simple_click_03.wav";
        buttonClickFx[3] = "ui_button_simple_click_04.wav";
        theGame.soundManager.addAssetSet("menu_button_click", buttonClickFx);

        String[] coinCollectedFx = new String[5];
        coinCollectedFx[0] = "collect_coin_01.wav";
        coinCollectedFx[1] = "collect_coin_02.wav";
        coinCollectedFx[2] = "collect_coin_03.wav";
        coinCollectedFx[3] = "collect_coin_04.wav";
        coinCollectedFx[4] = "collect_coin_05.wav";

        String[] humanDeathFx = new String[3];
        humanDeathFx[0] = "Male_Death_1.mp3";
        humanDeathFx[1] = "Male_Death_2.mp3";
        humanDeathFx[2] = "Male_Death_3.mp3";

        theGame.soundManager.addAssetSet("coin_collect", coinCollectedFx);
        theGame.soundManager.addAssetSet("male_death", humanDeathFx);
        theGame.soundManager.addAsset("heart_collect", "happy_collect_item_01.wav");
        theGame.soundManager.addAsset("collect_item", "collect_item_02.wav");



    }

    /**
     * this loads all the songs in the game.
     */
    private void loadMusic(){
        theGame.musicManager.addAsset("bg_1","music_calm_tree_of_life.wav");
        theGame.musicManager.addAsset("bg_2","funky_chill.wav");

    }

    /**
     * This ensures everything is loaded.
     */
    @Override
    public void show() {
        //load the music
        super.show();
        Timer.instance().scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                stillLoading.set(true);
                loadSoundFX();
                loadMusic();
                loadGraphics();
                loadTextAssets();
                loadEnemyAssets();
                loadNPCAssets();
                loadPowerUps();
                stillLoading.set(false);
            }
        }, 1);

    }

    /**
     * once loading is completed the screen is changed to the main menu.
     * @param delta
     */
    @Override
    public void render(float delta) {
        if (!stillLoading.get()) {
            theGame.changeScreen(Runnjump.ScreenEn.MENU);
        } else {
            //show loading screen
            super.render(delta);
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
