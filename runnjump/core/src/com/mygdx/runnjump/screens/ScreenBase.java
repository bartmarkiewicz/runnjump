package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.runnjump.Runnjump;

abstract class ScreenBase implements Screen, InputProcessor {

    public Stage stage;
    Skin skin;

    public Runnjump.ScreenEn currentScreenId;

    public Runnjump theGame;

    public ScreenBase(Runnjump theGameO) {
        this.theGame = theGameO;
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("skin/star-soldier-ui.json"));

        InputMultiplexer inputMultiplexer = new InputMultiplexer(stage, this);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        Gdx.input.setInputProcessor(inputMultiplexer);

        //Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width,height);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
    }


    @Override
    public boolean keyDown(int keycode) {

        if ((keycode == Input.Keys.ESCAPE) || (keycode == Input.Keys.BACK)) {
            theGame.changeScreen(theGame.previousScreen);
            return true;
        }
        return false;
    }
        //Gdx.app.exit();




    @Override
    public boolean keyUp(int keycode) {
        return false;
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
}

