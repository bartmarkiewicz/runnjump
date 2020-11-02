package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.mygdx.runnjump.Runnjump;

public class LoadingScreen extends ScreenBase implements Screen {

    public LoadingScreen(Runnjump theGameO) {
        super(theGameO);
    }

    @Override
    public void show() {
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
