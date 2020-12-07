package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.runnjump.Runnjump;

abstract class ScreenBase implements Screen, InputProcessor {
    public TextureRegion background;
    public Stage stage;
    Skin skin;
    SpriteBatch batch;
    public Runnjump.ScreenEn currentScreenId;
    InputMultiplexer inputMultiplexer;

    public Runnjump theGame;

    public ScreenBase(Runnjump theGameO) {
        this.theGame = theGameO;
        skin = new Skin(Gdx.files.internal("skin/star-soldier-ui.json"));
        background = new TextureRegion(new Texture("menubg.png"), 0,0,1334, 750);
        batch = new SpriteBatch();

    }

    @Override
    public void show() {
        //game resolution = 1280x720
        OrthographicCamera camera = new OrthographicCamera(1280,720);
        ExtendViewport gameVP = new ExtendViewport(1280,720, camera);
        stage = new Stage(gameVP);
        inputMultiplexer = new InputMultiplexer(stage, this);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        Gdx.input.setInputProcessor(inputMultiplexer);


    }

    @Override
    public void resize(int width, int height) {
        //works best with nothing in here for some reason
        show();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        if (currentScreenId != Runnjump.ScreenEn.GAME){
            batch.begin();
            batch.draw(background,0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {

        if ((keycode == Input.Keys.ESCAPE) || (keycode == Input.Keys.BACK)) {
            if (Runnjump.previousScreen != null){
                theGame.changeScreen(Runnjump.previousScreen);
            }
            return true;
        }
        return false;
    }

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

