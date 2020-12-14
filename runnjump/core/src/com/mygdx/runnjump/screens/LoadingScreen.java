package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.runnjump.Runnjump;
import com.mygdx.runnjump.util.TextureManager;

public class LoadingScreen extends ScreenBase implements Screen {

    public LoadingScreen(Runnjump theGameO) {
        super(theGameO);
    }

    private void loadPlayer(){
        //idle
        theGame.textureManager.addPlayerFrameSet("idle","player\\Idle_",12);
        theGame.textureManager.addPlayerFrameSet("running","player\\Running_",12);
        theGame.textureManager.addPlayerFrameSet("jump","player\\Jump Start_",6);

    }
    private void loadGraphics(){
        //TextureAtlas playerRunning = new TextureAtlas(Gdx.files.internal("player\\runningCharSet.atlas"));

        //Runnjump.textureAtlasMap.put("player_running", playerRunning);
        loadPlayer();
    }

    private void loadSoundFX(){
        String[] buttonClickFx = new String[4];
        buttonClickFx[0] = "ui_button_simple_click_01.wav";
        buttonClickFx[1] = "ui_button_simple_click_02.wav";
        buttonClickFx[2] = "ui_button_simple_click_03.wav";
        buttonClickFx[3] = "ui_button_simple_click_04.wav";
        theGame.soundManager.addSoundSet("menu_button_click", buttonClickFx);



    }

    private void loadMusic(){
        theGame.musicManager.addMusic("bg_1","music_calm_tree_of_life.wav");
    }

    @Override
    public void show() {
        //load the music
        loadSoundFX();
        loadMusic();
        loadGraphics();
    }

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
