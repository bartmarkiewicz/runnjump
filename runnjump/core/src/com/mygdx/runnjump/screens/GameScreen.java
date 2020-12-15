package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SerializationException;
import com.mygdx.runnjump.Runnjump;
import com.mygdx.runnjump.game.Hud;
import com.mygdx.runnjump.game.Player;
import com.mygdx.runnjump.util.TextureManager;

import java.util.ArrayList;

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
        try {
            tileMap = new TmxMapLoader().load("levels\\level" + level + ".tmx");
        }catch (SerializationException e){
            //lvl not implemented so it will load lvl 1 by default
            tileMap = new TmxMapLoader().load("levels\\level" + 1 + ".tmx");
        }
        mapRenderer = new OrthogonalTiledMapRenderer(tileMap, batch);
        orthographicCamera = new OrthographicCamera();

        width = 1280;
        height = 720;//Gdx.graphics.getHeight();

        orthographicCamera.setToOrtho(false,width,height);
        orthographicCamera.update();
        stage.getViewport().setCamera(orthographicCamera);
        background = new TextureRegion(new Texture("levels\\background.png"));

        TiledMapTileLayer layer = (TiledMapTileLayer) tileMap.getLayers().get("collisionLayer");
        TiledMapTileLayer visualLayer = (TiledMapTileLayer) tileMap.getLayers().get("secondLayer");
        hud = new Hud(new SpriteBatch(), theGame, skin);

        player = new Player(theGame,hud,layer,visualLayer);

        layer.getHeight();
        player.getPlayerSprite().setPosition(5*32,79*32);//start position
        zoom = 0f;
        orthographicCamera.zoom += zoom;

        inputMultiplexer.addProcessor(hud.stage);
        inputMultiplexer.addProcessor(player);
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
        //float width = Gdx.graphics.getWidth();
        //float height = Gdx.graphics.getHeight();
        if(player.getPlayerSprite().getX()+player.getPlayerSprite().getWidth()/2 < width){
            cameraPosToSetX = width;
        } else if((tileMapWidth*32)-width < player.getPlayerSprite().getX()+player.getPlayerSprite().getWidth()/2) {
            cameraPosToSetX = (tileMapWidth * 32) - width;
        }else {
            cameraPosToSetX = player.getPlayerSprite().getX()+player.getPlayerSprite().getWidth()/2;
        }

        if (player.getPlayerSprite().getY() + player.getPlayerSprite().getHeight() / 2<height){
            cameraPosToSetY = height;
        } else if((tileMapHeight*32)-height < player.getPlayerSprite().getY()+player.getPlayerSprite().getHeight()/2){
            cameraPosToSetY = (tileMapHeight * 32) - height;
        } else {
            cameraPosToSetY = player.getPlayerSprite().getY() + player.getPlayerSprite().getHeight() / 2;
        }
        orthographicCamera.position.set(cameraPosToSetX, cameraPosToSetY, 0);

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
        //show();
        this.width = width;
        this.height= height;
    }

    @Override
    public boolean keyUp(int keycode) {
        /*if(keycode == Input.Keys.LEFT)
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
            tileMap.getLayers().get(1).setVisible(!tileMap.getLayers().get(1).isVisible());*/
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
    }
}
