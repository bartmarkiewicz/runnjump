package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.runnjump.Runnjump;

public class GameScreen extends ScreenBase implements Screen, InputProcessor {
    //this will be the level screen.
    int level;
    TiledMapRenderer mapRenderer;
    TiledMap tileMap;
    OrthographicCamera orthographicCamera;

    public GameScreen(Runnjump theGameO, int level) {
        super(theGameO);
        currentScreenId = Runnjump.ScreenEn.GAME;
        this.level = level;
    }

    public GameScreen(Runnjump theGameO) {
        super(theGameO);
        currentScreenId = Runnjump.ScreenEn.GAME;
        this.level = -1; //-1 indicates SURVIVAL mode
    }

    @Override
    public void show() {
        super.show();
        tileMap = new TmxMapLoader().load("levels\\level1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tileMap);
        orthographicCamera = new OrthographicCamera();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        orthographicCamera.setToOrtho(false,w,h);
        orthographicCamera.update();
        stage.getViewport().setCamera(orthographicCamera);
    }

    @Override
    public void render(float delta) {
        //super.render(delta);
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        orthographicCamera.update();
        mapRenderer.setView(orthographicCamera);
        mapRenderer.render();

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width,height);
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.LEFT)
            orthographicCamera.translate(-32,0);
        if(keycode == Input.Keys.RIGHT)
            orthographicCamera.translate(32,0);
        if(keycode == Input.Keys.UP)
            orthographicCamera.translate(0,-32);
        if(keycode == Input.Keys.DOWN)
            orthographicCamera.translate(0,32);
        if(keycode == Input.Keys.NUM_1)
            tileMap.getLayers().get(0).setVisible(!tileMap.getLayers().get(0).isVisible());
        if(keycode == Input.Keys.NUM_2)
            tileMap.getLayers().get(1).setVisible(!tileMap.getLayers().get(1).isVisible());
        return false;
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
        super.dispose();
    }
}
