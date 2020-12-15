package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.SerializationException;
import com.mygdx.runnjump.Runnjump;
import com.mygdx.runnjump.game.Hud;
import com.mygdx.runnjump.game.Player;

public class GameScreen extends ScreenBase implements Screen, InputProcessor {
    //this is the screen where gameplay occurs
    int level;
    OrthogonalTiledMapRenderer mapRenderer;
    TiledMap tileMap;
    Hud hud;
    int tileMapWidth;
    int tileMapHeight;
    OrthographicCamera orthographicCamera;
    Player player;
    float zoom;
    MapProperties mapProperties;
    float width, height;//Gdx.graphics.getWidth();

    float spawnPointX, spawnPointY;
    boolean gameOver;
    private float timeSinceWin =0f;

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

    public void respawnPlayer(){
        spawnPointX = 5*32;
        spawnPointY = 79*32;
        if (player.respawn()) {
            player.getPlayerSprite().setPosition(spawnPointX, spawnPointY);//start position
        } else {// no more lives left GAME OVER
            gameOver = true;
        }
    }

    public void startGame(int level){
        try {
            tileMap = new TmxMapLoader().load("levels\\level" + level + ".tmx");
        }catch (SerializationException e){
            //lvl not implemented so it will load lvl 1 by default
            tileMap = new TmxMapLoader().load("levels\\level" + 1 + ".tmx");
        }
        mapRenderer.dispose();
        mapRenderer = new OrthogonalTiledMapRenderer(tileMap, batch);

        orthographicCamera.update();
        stage.getViewport().setCamera(orthographicCamera);

        TiledMapTileLayer layer = (TiledMapTileLayer) tileMap.getLayers().get("collisionLayer");
        TiledMapTileLayer visualLayer = (TiledMapTileLayer) tileMap.getLayers().get("secondLayer");

        player.setLayers(visualLayer,layer);
        player.restart();
        gameOver = false;
        respawnPlayer();
        zoom = 0f;
        orthographicCamera.zoom += zoom;
        //inputMultiplexer.addProcessor(hud.stage);
        //inputMultiplexer.addProcessor(player);
        mapProperties= tileMap.getProperties();
        tileMapHeight = mapProperties.get("height", Integer.class);
        tileMapWidth = mapProperties.get("width", Integer.class);
    }


    @Override
    public void show() {
        super.show(); //renders the map
        currentScreenId = Runnjump.ScreenEn.GAME;
        width = 1280;
        height = 720;//Gdx.graphics.getHeight();
        try {
            tileMap = new TmxMapLoader().load("levels\\level" + level + ".tmx");
        }catch (SerializationException e){
            //lvl not implemented so it will load lvl 1 by default
            tileMap = new TmxMapLoader().load("levels\\level" + 1 + ".tmx");
        }
        mapRenderer = new OrthogonalTiledMapRenderer(tileMap, batch);
        orthographicCamera = new OrthographicCamera();

        orthographicCamera.setToOrtho(false,width,height);
        orthographicCamera.update();
        stage.getViewport().setCamera(orthographicCamera);
        background = new TextureRegion(new Texture("levels\\background.png"));

        TiledMapTileLayer layer = (TiledMapTileLayer) tileMap.getLayers().get("collisionLayer");
        TiledMapTileLayer visualLayer = (TiledMapTileLayer) tileMap.getLayers().get("secondLayer");

        hud = new Hud(new SpriteBatch(), theGame, skin);
        player = new Player(theGame,hud,layer,visualLayer);
        gameOver = false;
        respawnPlayer();
        zoom = 0f;
        orthographicCamera.zoom += zoom;
        inputMultiplexer.addProcessor(hud.stage);
        inputMultiplexer.addProcessor(player);
        inputMultiplexer.addProcessor(this);
        mapProperties= tileMap.getProperties();
        tileMapHeight = mapProperties.get("height", Integer.class);
        tileMapWidth = mapProperties.get("width", Integer.class);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float cameraPosToSetX;
        float cameraPosToSetY;


        if (!gameOver) {
            if (player.getPlayerSprite().getX() + player.getPlayerSprite().getWidth() / 2 < width/2) {
                cameraPosToSetX = width/2;
            } else if ((tileMapWidth * 32) - width/2 < player.getPlayerSprite().getX() + player.getPlayerSprite().getWidth() / 2) {
                cameraPosToSetX = (tileMapWidth * 32) - width/2;
            } else {
                cameraPosToSetX = player.getPlayerSprite().getX() + player.getPlayerSprite().getWidth() / 2;
            }

            if (player.getPlayerSprite().getY() + player.getPlayerSprite().getHeight() / 2 < height/2) {
                cameraPosToSetY = height/2;
            } else if ((tileMapHeight * 32) - height/2 < player.getPlayerSprite().getY() + player.getPlayerSprite().getHeight() / 2) {
                cameraPosToSetY = (tileMapHeight * 32) - height/2;
            } else {
                cameraPosToSetY = player.getPlayerSprite().getY() + player.getPlayerSprite().getHeight() / 2;
            }
            if (player.isDead()) {
                respawnPlayer();
            }

            orthographicCamera.position.set(cameraPosToSetX, cameraPosToSetY, 0);

            orthographicCamera.update();

            mapRenderer.setView(orthographicCamera);
            mapRenderer.render();
            mapRenderer.getBatch().begin();

            player.draw(mapRenderer.getBatch());
            mapRenderer.getBatch().end();
        } else if(gameOver) {
            hud.gameOver(player.getScore());
        }
        if (player.isGameWon()){
            timeSinceWin+=delta;
            hud.gameWon(player.getScore(), level);
        }


        batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        //show();
        //this.width = width;
        //this.height= height;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (gameOver){
            //restart on pressing a button/tapping
            startGame(level);
        }
        if (player.isGameWon() && timeSinceWin - player.getTimeWon() > 20){
            theGame.changeScreen(Runnjump.ScreenEn.MENU);
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean keyUp(int keycode) {
        if (gameOver){
            //restart on pressing a button/tapping
            startGame(level);
            return true;
        }

        if (player.isGameWon() && timeSinceWin - player.getTimeWon() > 3){//waits 3 seconds to allow the user to read the screen before pressing keys
            theGame.changeScreen(Runnjump.ScreenEn.MENU);
        }
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
        player.getPlayerSprite().getTexture().dispose();
        mapRenderer.dispose();
        hud.dispose();
        tileMap.dispose();

    }
}
