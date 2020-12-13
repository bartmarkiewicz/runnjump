package com.mygdx.runnjump.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

public class Player extends Sprite implements InputProcessor {

    //movement velocity
    private Vector2 velocity = new Vector2();
    private float speedX = 530;
    private float speedY = 200;
    private float gravity = 98f;
    private TiledMapTileLayer collisionLayer;
    private TiledMapTileLayer visualLayer;
    private int score, hearts;
    private boolean goldKeyAcquired;
    boolean canJump;
    float sizeX,sizeY;
    Hud hud;

    public void setLogicalSize(float width, float height){
        sizeX = width;
        sizeY = height;
    }

    public int getHearts(){
        return hearts;
    }

    public int getScore(){
        return score;
    }

    public Player(Sprite sprite, Hud hud, TiledMapTileLayer collisionLayer, TiledMapTileLayer visualLayer){
        super(sprite);
        this.collisionLayer = collisionLayer;
        this.visualLayer = visualLayer;
        setSize(30*2,30*3);//2 by 3 tiles size
        setLogicalSize(30*2,30*3);
        this.score = 0;
        this.hearts =3;
        this.goldKeyAcquired = false;
        this.hud = hud;
    }

    @Override
    public void draw(Batch batch) {
        update(Gdx.graphics.getDeltaTime());//updates before drawing
        super.draw(batch);
    }

    public boolean collidesEast() {
        for(float i = 0; i <= sizeY; i += collisionLayer.getTileHeight()) {
            if (isCellCollectible(getX() + sizeX, getY() + i)) {
                handleCollectible(getX() + sizeX, getY() + i);
            }
            if (isCellBlocked(getX() + sizeX, getY() + i))
                return true;
        }
        return false;
    }

    public boolean collidesWest() {
        for(float i = 0; i <= sizeY; i += collisionLayer.getTileHeight()) {
            if (isCellCollectible(getX(), getY()+i)) {
                handleCollectible(getX(), getY()+i);
            }
            if (isCellBlocked(getX(), getY() + i))
                return true;
        }
        return false;
    }

    public boolean collidesNorth() {
        for(float i = 0; i <= sizeX; i += collisionLayer.getTileWidth()) {
            if (isCellCollectible(getX() + i, getY()+sizeY)) {
                handleCollectible(getX() + i, getY()+sizeY);
            }
            if (isCellBlocked(getX() + i, getY() + sizeY))
                return true;
        }
        return false;
    }
    public void removeCollectibe(int tileX, int tileY){
        visualLayer.setCell(tileX, tileY, new TiledMapTileLayer.Cell());
        collisionLayer.setCell(tileX,tileY, new TiledMapTileLayer.Cell()); // gets rid of collectible cells
    }

    public void handleCollectible(float x, float y){
        TiledMapTileLayer.Cell cellColLayer = collisionLayer.getCell((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());
        TiledMapTileLayer.Cell cellVisualLayer = visualLayer.getCell((int)x/visualLayer.getTileWidth(), (int)y/visualLayer.getTileHeight());

        //removes collectible
        removeCollectibe((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight()); // gets rid of collectible cells
        if (cellColLayer.getTile().getProperties().containsKey("coin")){
            score += 1;
            hud.setScore(score);
        }
        if (cellColLayer.getTile().getProperties().containsKey("heart")){
            hearts += 1;
            hud.setLives(hearts);
        }

        if (cellColLayer.getTile().getProperties().containsKey("gold_key")){
            goldKeyAcquired = true;
        }

        if (isCellCollectible((int)x+33, (int)y)){
            removeCollectibe((int)(x+33)/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight()); //checks cell to the right
        }
        if(isCellCollectible((int)x-33, (int)y)){
           removeCollectibe((int)(x-33)/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());
        }; //checks cell to the left

        if (isCellCollectible((int)x, (int)y+33)){
            removeCollectibe((int)x/collisionLayer.getTileWidth(), (int)(y+33)/collisionLayer.getTileHeight());// checks cell above
        }
        if(isCellCollectible(x, (int)y-33)){
            removeCollectibe((int)x/collisionLayer.getTileWidth(), (int)(y-33)/collisionLayer.getTileHeight());
        };//checks cell below
        if(isCellCollectible(x-33, y)){
            removeCollectibe((int)(x-33)/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());
        }; // checks cell to the left and below
        if(isCellCollectible(x-33, y+33)){
            removeCollectibe((int)(x-33)/collisionLayer.getTileWidth(), (int)(y+33)/collisionLayer.getTileHeight());
        };//checks cell to the left and above
        if(isCellCollectible(x+33, y-33)){
            removeCollectibe((int)(x+33)/collisionLayer.getTileWidth(), (int)(y-33)/collisionLayer.getTileHeight());// checks cell to the left and below
        }
        if (isCellCollectible(x+33, y+33)){
            removeCollectibe((int)(x+32)/collisionLayer.getTileWidth(), (int)(y+33)/collisionLayer.getTileHeight());//checks cell above and to the right
        }
        if (isCellCollectible(x-33, y-33)){
            removeCollectibe((int)(x-33)/collisionLayer.getTileWidth(), (int)(y-33)/collisionLayer.getTileHeight()); // checks cell to the left and below
        }
    }

    public boolean collidesSouth() {
        for(float i = 0; i <= sizeX; i += collisionLayer.getTileWidth()) {
            if (isCellCollectible(getX() + i, getY())) {
                handleCollectible(getX() + i, getY());
            }
            if (isCellBlocked(getX() + i, getY()))
                return true;
        }
        return false;
    }

    public void update(float delta) {
        velocity.y -= gravity * delta;

        // sets max velocity
        if (velocity.y > speedY)
            velocity.y = speedY;
        else if (velocity.y < -speedY)
            velocity.y = -speedY;

        // saves previous position
        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;

        // move horizontally
        setX(getX() + velocity.x * delta);


        if (velocity.x < 0) // going left
            collisionX = collidesWest();
        else if (velocity.x > 0) // going right
            collisionX = collidesEast();

        // x collision handling
        if (collisionX) {
            setX(oldX);
            velocity.x = 0;
        }

        // move on y
        setY(getY() + velocity.y * delta * 5f);

        if (velocity.y < 2.5f)
            canJump = collisionY = collidesSouth();
        else if (velocity.y > 2.5f)
            collisionY = collidesNorth();

        // y collision handling
        if (collisionY) {
            setY(oldY);
            velocity.y = 0;
        }
    }

    private boolean isCellCollectible(float x, float y){
        TiledMapTileLayer.Cell cell =collisionLayer.getCell((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());
        return cell != null && cell.getTile()!=null && (cell.getTile().getProperties().containsKey("coin") ||
                cell.getTile().getProperties().containsKey("gold_key") ||
                cell.getTile().getProperties().containsKey("heart"));
    }

    private boolean isCellBlocked(float x, float y){
        TiledMapTileLayer.Cell cell =collisionLayer.getCell((int)x/collisionLayer.getTileWidth(), (int)y/collisionLayer.getTileHeight());
        return cell != null && cell.getTile()!=null && cell.getTile().getProperties().containsKey("blocked");
    }

    @Override
    public boolean keyDown(int keycode) {
        switch(keycode) {
            case Input.Keys.W:
                if(canJump) {
                    velocity.y = speedY / 1.8f;
                    canJump = false;
                }
                break;
            case Input.Keys.D:
                velocity.x = speedX;
                break;
            case Input.Keys.A:
                velocity.x = -speedX;
                break;

        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch(keycode) {
            case Input.Keys.D:
                velocity.x = 0;
            case Input.Keys.A:
                velocity.x = 0;
        }
        return true;
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
