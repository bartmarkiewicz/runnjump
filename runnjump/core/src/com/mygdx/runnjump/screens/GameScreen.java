package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.runnjump.Runnjump;
import com.mygdx.runnjump.game.Hud;
import com.mygdx.runnjump.game.Player;

public class GameScreen extends ScreenBase implements Screen, InputProcessor {
    //this will be the level screen.
    int level;
    OrthogonalTiledMapRenderer mapRenderer;
    TiledMap tileMap;
    World gameWorld;
    Box2DDebugRenderer box2DRenderer;
    Hud hud;
    int tileMapWidth;
    int tileMapHeight;
    OrthographicCamera orthographicCamera;
    Player player;
    float zoom;


    public GameScreen(Runnjump theGameO, int level) {
        super(theGameO);
        currentScreenId = Runnjump.ScreenEn.GAME;
        this.level = level;
    }

    public GameScreen(Runnjump theGameO) {
        super(theGameO);
        currentScreenId = Runnjump.ScreenEn.GAME;
        this.level = -1; //-1 indicates SURVIVAL mode
        batch = new SpriteBatch();
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

        TiledMapTileLayer layer = (TiledMapTileLayer) tileMap.getLayers().get("collisionLayer");
        TiledMapTileLayer visualLayer = (TiledMapTileLayer) tileMap.getLayers().get("secondLayer");
        hud = new Hud(new SpriteBatch(), theGame, skin);

        player = new Player(new Sprite(new Texture("player\\Idle_000.png")),hud,layer,visualLayer);

        layer.getHeight();
        player.setPosition(5*32,79*32);//start position
        zoom = 0f;
        orthographicCamera.zoom += zoom;

        inputMultiplexer.addProcessor(hud.stage);
        inputMultiplexer.addProcessor(player);
        MapProperties properties = tileMap.getProperties();
        tileMapHeight = properties.get("height", Integer.class);
        tileMapWidth = properties.get("width", Integer.class);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        orthographicCamera.position.set(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2, 0);

        orthographicCamera.update();
        mapRenderer.setView(orthographicCamera);
        mapRenderer.render();
        mapRenderer.getBatch().begin();

        player.draw(mapRenderer.getBatch());
        mapRenderer.getBatch().end();

        batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();


    }

    @Override
    public void resize(int width, int height) {
        /*super.resize(width,height);*/
        /*orthographicCamera.viewportWidth = width/2.5f;
        orthographicCamera.viewportHeight = height/2.5f;
        orthographicCamera.update();*/
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
        hud.dispose();
    }
}
