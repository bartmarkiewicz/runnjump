package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.runnjump.Runnjump;
import com.mygdx.runnjump.util.TextureManager;

/**
 * This class represents the loading screen which is shown when the game begins, currently it has no look of its own but this class does all the loading of assets necessary for the rest of the game.
 */
public class LoadingScreen extends ScreenBase implements Screen {

    /**
     * Instantiates a new Loading screen.
     *
     * @param theGameO the the game o
     */
    public LoadingScreen(Runnjump theGameO) {
        super(theGameO);
    }

    /**
     *  This loads all the player assets and all the frames of animation.
     */
    private void loadPlayer(){
        //idle
        theGame.textureManager.addPlayerFrameSet("idle","player\\Idle_",12);
        theGame.textureManager.addPlayerFrameSet("running","player\\Running_",12);
        theGame.textureManager.addPlayerFrameSet("jump","player\\Jump Start_",6);

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
        theGame.soundManager.addSoundSet("menu_button_click", buttonClickFx);

        String[] coinCollectedFx = new String[5];
        coinCollectedFx[0] = "collect_coin_01.wav";
        coinCollectedFx[1] = "collect_coin_02.wav";
        coinCollectedFx[2] = "collect_coin_03.wav";
        coinCollectedFx[3] = "collect_coin_04.wav";
        coinCollectedFx[4] = "collect_coin_05.wav";
        theGame.soundManager.addSoundSet("coin_collect", coinCollectedFx);
        theGame.soundManager.addSound("heart_collect", "happy_collect_item_01.wav");
        theGame.soundManager.addSound("collect_item", "collect_item_02.wav");
    }

    /**
     * this loads all the songs in the game.
     */
    private void loadMusic(){
        theGame.musicManager.addMusic("bg_1","music_calm_tree_of_life.wav");
    }

    /**
     * This ensures everything is loaded.
     */
    @Override
    public void show() {
        //load the music
        loadSoundFX();
        loadMusic();
        loadGraphics();
    }

    /**
     * once loading is completed the screen is changed to the main menu.
     * @param delta
     */
    @Override
    public void render(float delta) {
        theGame.changeScreen(Runnjump.ScreenEn.MENU);
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
