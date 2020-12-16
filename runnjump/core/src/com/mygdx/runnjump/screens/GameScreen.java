package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.SerializationException;
import com.mygdx.runnjump.Runnjump;
import com.mygdx.runnjump.game.Hud;
import com.mygdx.runnjump.game.Player;


/**
 * This is used to represent the screen with the actual game and its level. The constructor initialises the chosen level.
 */
public class GameScreen extends ScreenBase implements InputProcessor {
    /**
     * The Level.
     */
//this is the screen where gameplay occurs
    int level;
    /**
     * The Map renderer.
     */
    OrthogonalTiledMapRenderer mapRenderer;
    /**
     * The Tile map.
     */
    TiledMap tileMap;
    /**
     * The Hud.
     */
    Hud hud;
    /**
     * The Tile map width.
     */
    int tileMapWidth;
    /**
     * The Tile map height.
     */
    int tileMapHeight;
    /**
     * The Orthographic camera.
     */
    OrthographicCamera orthographicCamera;
    /**
     * The Player.
     */
    Player player;
    /**
     * The Zoom.
     */
    float zoom;
    /**
     * The Map properties.
     */
    MapProperties mapProperties;
    /**
     * The Width.
     */
    float width, /**
     * The Height.
     */
    height;//Gdx.graphics.getWidth();

    /**
     * The Spawn point x.
     */
    float spawnPointX, /**
     * The Spawn point y.
     */
    spawnPointY;
    /**
     * The Game over.
     */
    boolean gameOver;
    private float timeSinceWin =0f;

    /**
     * Instantiates a new Game screen.
     *
     * @param theGameO the the game o
     * @param level    the level
     */
    public GameScreen(Runnjump theGameO, int level) {
        super(theGameO);
        currentScreenId = Runnjump.ScreenEn.GAME;
        this.level = level;
    }

    /**
     * Instantiates a new Game screen.
     *
     * @param theGameO the the game o
     */
    public GameScreen(Runnjump theGameO) {
        super(theGameO);
        currentScreenId = Runnjump.ScreenEn.GAME;
        this.level = -1; //-1 indicates SURVIVAL mode
        batch = new SpriteBatch();
    }

    /**
     * this method respawns the player at the (currently hard-coded) spawn point, but only if the player has lives left. If  not, a game over boolean is set.
     */
    public void respawnPlayer(){
        spawnPointX = 5*32;
        spawnPointY = 79*32;
        if (player.respawn()) {
            player.getPlayerSprite().setPosition(spawnPointX, spawnPointY);//start position
        } else {// no more lives left GAME OVER
            gameOver = true;
        }
    }

    /**
     * this method is used for re-starting the level after a game over.
     *
     * @param level the level
     */
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

        for (MapObject gameObject: tileMap.getLayers().get("objects").getObjects()) {
            if (gameObject.getProperties().containsKey("enemy")){
                float xPos = gameObject.getProperties().get("x", Float.class);
                float yPos = gameObject.getProperties().get("y", Float.class);


            }
        }

    }


    /**
     * this method is used for starting the chosen level after the level/game mode is chosen. It sets up the whole tiled map, the screen and input processors.
     */
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

    /**
     * renders the game world and the hud also makes the camera follow the player. Checks if the game has been won or a game over happened.
     * @param delta
     */
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

    /**
     * this method is used for restarting the level or leaving the level upon winning by the player and going back to the main menu, opon touching of the screen. This is android-specific.
     * @param screenX
     * @param screenY
     * @param pointer
     * @param button
     * @return
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (gameOver){
            //restart on pressing a button/tapping
            startGame(level);
        }
        if (player.isGameWon() && timeSinceWin - player.getTimeWon() > 3){
            theGame.changeScreen(Runnjump.ScreenEn.MENU);
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    /**
     * this method is used for restarting the level or leaving the level upon winning by the player and going back to the main menu, opon tapping any key. This is windows-specific.
     * @param keycode
     * @return
     */
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

    /**
     * prevent memory leaks by disposing all game elements with are disposable.
     */
    @Override
    public void dispose() {
        super.dispose();
        player.getPlayerSprite().getTexture().dispose();
        mapRenderer.dispose();
        hud.dispose();
        tileMap.dispose();

    }
}
