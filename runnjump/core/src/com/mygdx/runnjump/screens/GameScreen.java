package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.SerializationException;
import com.mygdx.runnjump.Runnjump;
import com.mygdx.runnjump.entity.Bandit;
import com.mygdx.runnjump.entity.GameObject;
import com.mygdx.runnjump.entity.Hedgehog;
import com.mygdx.runnjump.game.Hud;
import com.mygdx.runnjump.entity.NPC;
import com.mygdx.runnjump.entity.Player;
import com.mygdx.runnjump.entity.Projectile;
import com.mygdx.runnjump.entity.TurtleMan;
import com.mygdx.runnjump.libs.Toast;
import com.mygdx.runnjump.util.TerrainGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * This is used to represent the screen with the actual game and its level. The constructor initialises the chosen level.
 */
public class GameScreen extends ScreenBase implements InputProcessor {
    static Player currentPlayer;
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

    public static boolean GAME_PAUSED = false;

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
     * The Width of the camera.
     */
    float width, /**
     * The Height of the camera.
     */
    height;//Gdx.graphics.getWidth();
    Toast.ToastFactory toastFactory;
    ArrayList<Toast> toasts;
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
    
    HashMap<String, ArrayList<TiledMapTileLayer.Cell>> tileGroups;

    ArrayList<GameObject> dynamicObjects;
    private boolean survivalMode;
    private float timeSinceGen = 0;
    private TerrainGenerator terrainGen;

    /**
     * Instantiates a new Game screen.
     *
     * @param theGameO the the game o
     * @param level    the level
     */
    public GameScreen(Runnjump theGameO, int level) {
        super(theGameO);
        currentScreenId = Runnjump.ScreenEn.GAME;
        this.level = Runnjump.getLevelSelected();
        survivalMode = false;
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
        survivalMode = true;


    }


    public void setSpawnPoint(float x, float y){
        spawnPointX = x;
        spawnPointY = y;
        if(survivalMode) {
            int mapLoadXpos = Math.round(x + width) + (15 * 32);//generates things up to 30 blocks away from player x position.
            terrainGen.setLoadPos(mapLoadXpos);
        }

    }

    /**
     * this method respawns the player at the spawn point, but only if the player has lives left. If  not, a game over boolean is set.
     */
    public void respawnPlayer(){
        if (player.respawn()) {
            player.getSprite().setPosition(spawnPointX, spawnPointY);//start position
        } else {// no more lives left GAME OVER
            gameOver = true;
        }
    }

    /**
     * this method is used for re-starting the level after a game over.
     *
     */
    public void startGame(){
        this.level = Runnjump.getLevelSelected();

        mapRenderer.dispose();
        loadLevel();

        TiledMapTileLayer layer = (TiledMapTileLayer) tileMap.getLayers().get("collisionLayer");
        TiledMapTileLayer visualLayer = (TiledMapTileLayer) tileMap.getLayers().get("secondLayer");

        player.setLayers(visualLayer,layer);
        player.restart(survivalMode);
        gameOver = false;

        //respawnPlayer();
        dynamicObjects.clear();
        placeObjects(layer, visualLayer);

        zoom = 0f;
        orthographicCamera.zoom += zoom;

        mapProperties= tileMap.getProperties();
        tileMapHeight = mapProperties.get("height", Integer.class);
        tileMapWidth = mapProperties.get("width", Integer.class);

    }

    /**
     * This method is used for loading the level
     */
    public void loadLevel(){
        TmxMapLoader loader = new TmxMapLoader();
        TmxMapLoader.Parameters loadingParameters = new TmxMapLoader.Parameters();
        try {
            tileMap = loader.load("levels\\level" + level + ".tmx",loadingParameters);
            if(level == -1){
                survivalMode = true;
            } else {
                survivalMode = false;
            }
        }catch (SerializationException e){
            //survival mode
            tileMap = loader.load("levels\\level" + -1 + ".tmx",loadingParameters);
            survivalMode = true;
        }

        mapRenderer = new OrthogonalTiledMapRenderer(tileMap, batch);
        if (orthographicCamera == null) {
            orthographicCamera = new OrthographicCamera();
        }

        orthographicCamera.setToOrtho(false,width,height);
        orthographicCamera.update();
        stage.getViewport().setCamera(orthographicCamera);

        if (background == null) {
            background = new TextureRegion(new Texture("levels\\background.png"));
        }
        TiledMapTileLayer visualLayer = (TiledMapTileLayer) tileMap.getLayers().get("secondLayer");
        TiledMapTileLayer layer = (TiledMapTileLayer) tileMap.getLayers().get("collisionLayer");

        if(!survivalMode) {
            tileGroups = new HashMap<>();
            ArrayList<TiledMapTileLayer.Cell> goldKeyBlockers = new ArrayList<>();
            ArrayList<TiledMapTileLayer.Cell> silverKeyBlockers = new ArrayList<>();

            for (int i = 0; i < visualLayer.getWidth(); i++) { // finds tiles of a specific type and puts them in a collection for quick access during runtime of the game.
                for (int j = 0; j < visualLayer.getHeight(); j++) {
                    TiledMapTileLayer.Cell cell = visualLayer.getCell((int) i, (int) j);

                    if (cell != null && cell.getTile().getProperties().containsKey("gold_key_blocker")) {
                        goldKeyBlockers.add(cell);
                    }
                    if (cell != null && cell.getTile().getProperties().containsKey("silver_key_blocker")) {
                        silverKeyBlockers.add(cell);
                    }

                }
            }

            tileGroups.put("gold_key", goldKeyBlockers);
            tileGroups.put("silver_key", silverKeyBlockers);
            //loadGameData();

        } else {
            //survival mode
            TiledMapTileSet tileSet = tileMap.getTileSets().getTileSet("jungleplatform");
            TiledMapTileSet tileSetCollectibles = tileMap.getTileSets().getTileSet(2);
            terrainGen = new TerrainGenerator(visualLayer, layer,tileSet, tileSetCollectibles);
        }
    }




    /**
     * This method parses the TMX map to create dynamic game objects based on the TMX map objects within the map and places the created objects in the location specified on the map.
     * @param layer
     * @param visualLayer
     */
    public void placeObjects(TiledMapTileLayer layer, TiledMapTileLayer visualLayer){
        for(MapObject object: tileMap.getLayers().get("objects").getObjects()){ // gets the map objects from the object layer
            float x, y; // object position
            x = Float.parseFloat(object.getProperties().get("x").toString());
            y = Float.parseFloat(object.getProperties().get("y").toString());


            if(object.getName().equals("hedgehog")){
                int blocks_to_move = Integer.parseInt(object.getProperties().get("blocks_to_move").toString());
                int max_speed = Integer.parseInt(object.getProperties().get("max_speed_x").toString());

                Hedgehog hedgehog = new Hedgehog(layer, visualLayer, blocks_to_move,max_speed);
                hedgehog.getSprite().setPosition(x,y);
                dynamicObjects.add(hedgehog);
            } else if (object.getName().equals("bandit")) {
                int max_speed = Integer.parseInt(object.getProperties().get("max_speed_x").toString());
                Bandit bandit = new Bandit(layer, visualLayer, max_speed);
                bandit.getSprite().setPosition(x, y);
                dynamicObjects.add(bandit);
            }
            else if (object.getName().equals("turtleman")){
                TurtleMan turtleMan = new TurtleMan(layer,visualLayer);
                turtleMan.getSprite().setPosition(x,y);
                dynamicObjects.add(turtleMan);


            }else if (object.getName().equals("npc")){
                String name = object.getProperties().get("name").toString();
                String assetName = object.getProperties().get("assetName").toString();

                NPC npc = new NPC(layer,visualLayer,name,assetName, level);
                npc.getSprite().setPosition(x,y);
                dynamicObjects.add(npc);
            } else if (object.getName().equals("player")){
                setSpawnPoint(x, y);
                respawnPlayer();
            }

            System.out.println("x " + x + " y: " + y);
        }
    }


    /**
     * this method is used for starting the chosen level after the level/game mode is chosen. It sets up the whole tiled map, the screen and input processors.
     */
    @Override
    public void show() {
        super.show(); //renders the map
        this.level = Runnjump.getLevelSelected();
        currentScreenId = Runnjump.ScreenEn.GAME;
        width = 1280;
        height = 720;//Gdx.graphics.getHeight();

        loadLevel();

        TiledMapTileLayer layer = (TiledMapTileLayer) tileMap.getLayers().get("collisionLayer");
        TiledMapTileLayer visualLayer = (TiledMapTileLayer) tileMap.getLayers().get("secondLayer");

        hud = new Hud(new SpriteBatch(), theGame, skin, survivalMode);
        player = new Player(theGame,hud,layer,visualLayer);

        currentPlayer = player;
        gameOver = false;
        dynamicObjects = new ArrayList<>();
        zoom = 0f;
        orthographicCamera.zoom += zoom;
        inputMultiplexer.addProcessor(hud.stage);
        inputMultiplexer.addProcessor(player);
        inputMultiplexer.addProcessor(this);
        mapProperties= tileMap.getProperties();
        tileMapHeight = mapProperties.get("height", Integer.class);
        tileMapWidth = mapProperties.get("width", Integer.class);
        //setSpawnPoint(5*32, 79*32);
        //respawnPlayer();
        placeObjects(layer,visualLayer);



        toastFactory = new Toast.ToastFactory.Builder()
                .font(new BitmapFont()).positionY(10).build();
        toasts = new ArrayList<>();
    }

    public void createLongToast(String message){
        toasts.add(toastFactory.create(message, Toast.Length.LONG));
    }

    public void createShortToast(String message){
        toasts.add(toastFactory.create(message, Toast.Length.SHORT));
    }



    /**
     * renders the game world and the hud also makes the camera follow the player. Checks if the game has been won or a game over happened.
     * @param delta
     */
    @Override
    public void render(float delta) {
        if(GAME_PAUSED) {
            delta = 0;
        }
        delta = delta > 0.2f ? 0.2f: delta ;//0.2f is max delta time
        super.render(delta);
        timeSinceGen += delta;
        if(survivalMode && timeSinceGen > 0.2f){
            terrainGen.generateTerrain(player.getSprite().getX(),width, delta);
            GameObject enemy = terrainGen.spawnEnemy();
            if(enemy != null){
                dynamicObjects.add(enemy);
            }
            timeSinceGen = 0;
        }

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        if (!gameOver) {
            updateCamera();

            mapRenderer.setView(orthographicCamera);
            mapRenderer.render();
            mapRenderer.getBatch().begin();
            updateDynamicObjects(delta);
            player.draw(mapRenderer.getBatch(), delta);


            mapRenderer.getBatch().end();
        } else if (gameOver) {
            hud.gameOver(player.getInventory().getScore());
        }
        if (player.isGameWon()) {
            timeSinceWin += delta;
            hud.gameWon(player.getInventory().getScore(), level);
        }



        batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        updatePopups(delta);

    }

    private void updatePopups(float delta) {
        Iterator<Toast> iter = toasts.iterator();
        while (iter.hasNext()) {
            Toast toast = iter.next();
            if (!toast.render(delta)) {
                iter.remove();
            } else {
                break;
            }
        }
    }

    private void updateCamera() {
        float cameraPosToSetX;
        float cameraPosToSetY;
        if (player.getSprite().getX() + player.getSprite().getWidth() / 2 < width / 2) {
            cameraPosToSetX = width / 2;
        } else if ((tileMapWidth * 32) - width / 2 < player.getSprite().getX() + player.getSprite().getWidth() / 2) {
            cameraPosToSetX = (tileMapWidth * 32) - width / 2;
        } else {
            cameraPosToSetX = player.getSprite().getX() + player.getSprite().getWidth() / 2;
        }

        if (player.getSprite().getY() + player.getSprite().getHeight() / 2 < height / 2) {
            cameraPosToSetY = height / 2;
        } else if ((tileMapHeight * 32) - height / 2 < player.getSprite().getY() + player.getSprite().getHeight() / 2) {
            cameraPosToSetY = (tileMapHeight * 32) - height / 2;
        } else {
            cameraPosToSetY = player.getSprite().getY() + player.getSprite().getHeight() / 2;
        }
        if (player.isDead()) {
            respawnPlayer();
        }

        orthographicCamera.position.set(cameraPosToSetX, cameraPosToSetY, 0);

        orthographicCamera.update();
    }

    /**
     * Updates dynamic game objects such as enemies and NPCs.
     *
     */
    private void updateDynamicObjects(float delta){
        Projectile rock = player.getProjectile();
        if(rock !=null) {
            dynamicObjects.add(rock);
        }
        for (int i = 0; i < dynamicObjects.size(); i++) { //loops through all dynamic game objects
            GameObject current = dynamicObjects.get(i);
            if(current instanceof TurtleMan){
                rock = ((TurtleMan) current).getProjectile();
                if(rock != null) {
                    dynamicObjects.add(rock);
                }
            }

            if (!current.isDead() &&  ( current.isActive(player.getSprite().getX(), player.getSprite().getY(),width,height))) {
                current.draw(mapRenderer.getBatch(), delta);
                if (current.isPlayerCollidable()) {
                    if (Intersector.overlaps(player.getSprite().getBoundingRectangle(), current.getSprite().getBoundingRectangle())) {
                        System.out.println("Collision between player and " + current.getClass());
                        player.collidesObject(current, delta);
                        current.collidesObject(player, delta);
                    }
                }
                if (current instanceof Projectile && !((Projectile) current).isIdle()) {
                    for (int j = 0; j < dynamicObjects.size(); j++) { //loops through all dynamic game objects
                        GameObject secondObj = dynamicObjects.get(j);
                        if (Intersector.overlaps(secondObj.getSprite().getBoundingRectangle(), current.getSprite().getBoundingRectangle())) {
                            System.out.println("Collision between " + secondObj.getClass() + " and " + current.getClass());
                            secondObj.collidesObject(current, delta);
                            current.collidesObject(secondObj, delta);
                        }
                    }
                }
            }
            if(current.isDead() && !(current instanceof Player)){
                dynamicObjects.remove(i);
            }
        }
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
            startGame();
        }
        if (player.isGameWon() && timeSinceWin - player.getTimeWon() > 3){
            hud.saveCampaignData(player.getInventory().getScore(),level);
            theGame.changeScreen(Runnjump.ScreenEn.MENU);

        }
        if(!gameOver && !hud.shopOpen && (player.dialogueMode || player.touchingNPC)){
            player.dialogueManage();
            return true;
        }
        return false;
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
            startGame();
            return true;
        }

        if (player.isGameWon() && timeSinceWin - player.getTimeWon() > 3){//waits 3 seconds to allow the user to read the screen before pressing keys
            hud.saveCampaignData(player.getInventory().getScore(),level);
            theGame.changeScreen(Runnjump.ScreenEn.MENU);
        }
        return false;
    }

    @Override
    public void pause() {
        GAME_PAUSED = true;
    }

    @Override
    public void resume() {
        GAME_PAUSED = false;
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
        player.getSprite().getTexture().dispose();
        mapRenderer.dispose();
        hud.dispose();
        tileMap.dispose();

    }

    public ArrayList<TiledMapTileLayer.Cell> getBlockedCells(String key) {
        return tileGroups.get(key);
    }

    public static Player getPlayer(){
        return currentPlayer;
    }
}
