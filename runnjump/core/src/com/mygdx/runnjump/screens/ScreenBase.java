package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

/**
 * This class is the parent of all the screens in the package, it does all the necessary processing required by all the screens. The constructor initialises the skin used by the whole application, the background for the menu system and the batch used for drawing.
 */
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

    /**
     * this method is called every time the screen changes, it sets up a new stage for every screen and a camera. Additionally it sets up the input processing multiplexer which processes all the inputs from the different stages.
     */
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

    /**
     * this is called upon resizing of the game window
     * @param width
     * @param height
     */
    @Override
    public void resize(int width, int height) {
        //works best with nothing in here for some reason
        show();

    }

    /**
     * This is the method which actually draws everything onto the display.
     * @param delta is the time in seconds since the last render call.
     */
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

    /**
     *  this method disposes of all the resources, to prevent memory leaks.
     */
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        batch.dispose();
        background.getTexture().dispose();
    }

    /**
     *  this method implements the ‘back’ key functionality on android and windows. (by pressing the ESCAPE key). Switches the screen to the previous screen.
     * @param keycode
     * @return
     */
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

