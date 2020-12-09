package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.mygdx.runnjump.Runnjump;
import com.mygdx.runnjump.game.Player;
import com.mygdx.runnjump.util.MapBodyConverter;

import java.util.ArrayList;

public class GameScreen extends ScreenBase implements Screen, InputProcessor {
    //this will be the level screen.
    int level;
    OrthogonalTiledMapRenderer mapRenderer;
    TiledMap tileMap;
    World gameWorld;
    Box2DDebugRenderer box2DRenderer;
    OrthographicCamera hudCamera;

    OrthographicCamera orthographicCamera;
    Player player;
    ArrayList<Body> bodies;


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
        currentScreenId = Runnjump.ScreenEn.GAME;

        tileMap = new TmxMapLoader().load("levels\\level1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(tileMap, batch);
        orthographicCamera = new OrthographicCamera();
        float w = 1280;//Gdx.graphics.getWidth();
        float h = 720;//Gdx.graphics.getHeight();
        Vector2 gravityVector = new Vector2(0,-10);
        gameWorld = new World(gravityVector,true);
        box2DRenderer = new Box2DDebugRenderer();


        orthographicCamera.setToOrtho(false,w,h);
        orthographicCamera.update();
        stage.getViewport().setCamera(orthographicCamera);
        background = new TextureRegion(new Texture("levels\\background.png"));
        orthographicCamera.position.set(120,3000,0);

        TiledMapTileLayer layer = (TiledMapTileLayer) tileMap.getLayers().get("collisionLayer");
        player = new Player(new Sprite(new Texture("player\\Idle_000.png")),layer);

        layer.getHeight();
        player.setPosition(5*32,75*32);//start position
        orthographicCamera.position.set(player.getX(),player.getY(),0);
        orthographicCamera.zoom += 15.45f;
        //bodies = MapBodyConverter.buildShapes(tileMap,32,gameWorld);
        Table mainTable = new Table();
        mainTable.align(Align.topRight);
        mainTable.right().top();
        mainTable.pad(20,10,0,0);
        mainTable.add(theGame.soundBt).width(100).height(100).fill();
        stage.addActor(mainTable);
        stage.setDebugAll(true);
        stage.getBatch().setColor(Color.WHITE);
        hudCamera = new OrthographicCamera();
        stage.getBatch().setProjectionMatrix(hudCamera.combined);

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
        mapRenderer.getBatch().begin();


        player.draw(mapRenderer.getBatch());
        mapRenderer.getBatch().end();


    }

    @Override
    public void resize(int width, int height) {
        super.resize(width,height);
        orthographicCamera.viewportWidth = width/10f;
        orthographicCamera.viewportHeight = height/10f;
        orthographicCamera.update();
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.LEFT)
            orthographicCamera.translate(-92,0);
        if(keycode == Input.Keys.RIGHT)
            orthographicCamera.translate(92,0);
        if(keycode == Input.Keys.UP)
            orthographicCamera.translate(0,-92);
        if(keycode == Input.Keys.DOWN)
            orthographicCamera.translate(0,92);
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
        //dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
        player.getTexture().dispose();
        gameWorld.dispose();
        mapRenderer.dispose();
    }
}
