package com.mygdx.runnjump.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.runnjump.Runnjump;
import com.mygdx.runnjump.util.SoundHandler;

public class Hud implements Disposable {
    public Stage stage;
    private Viewport viewport;
    private Label scoreL, livesL;
    private final Runnjump theGame;
    private BitmapFont gameoverFont;
    private Touchpad movementJoystick;
    private Button jumpBt;


    public Hud(SpriteBatch batch, final Runnjump theGame, Skin skin){
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(viewport,batch);
        this.theGame = theGame;
        gameoverFont = skin.get(Label.LabelStyle.class).font;
        Table container = new Table();
        Table mainTable = new Table();
        mainTable.align(Align.topRight);
        mainTable.right().top();
        mainTable.pad(5);
        scoreL = new Label("Score: 0",skin);
        livesL = new Label("Lives: 0", skin);
        scoreL.setFontScale(1.25f);
        livesL.setFontScale(1.25f);
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            mainTable.add(scoreL).left().top().fill().expandX().height(100).align(Align.top);
            mainTable.add(theGame.soundBt).width(100).height(100).expand().fill().right().top();
            mainTable.pad(10);
        }
        else {
            mainTable.add(scoreL).left().top().fill().expandX().height(100/2f).align(Align.top);
            mainTable.add(theGame.soundBt).width(100/2f).height(100/2f).expand().fill().right().top();
        }
        mainTable.row();
        mainTable.add(livesL).left().top().fill().height(100/2f).align(Align.top);
        container.top();
        container.add(mainTable).top().expandX().fill().colspan(2);
        mainTable.top().right();
        Table bottomTable = new Table();
        bottomTable.bottom().left();
        container.row();
        container.add(bottomTable).bottom().left().fill().expand();
        Table bottomRightTable = new Table();
        bottomRightTable.bottom().right();
        container.add(bottomRightTable).bottom().right().fill().expand();
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            Skin touchpadSkin = new Skin();
            touchpadSkin.add("touchBg", new Texture("skin\\touchbg.png"));
            touchpadSkin.add("touchKnob", new Texture("skin\\touchknob.png"));
            Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();
            touchpadStyle.background = touchpadSkin.getDrawable("touchBg");
            touchpadStyle.knob = touchpadSkin.getDrawable("touchKnob");
            touchpadStyle.background.setMinWidth(200);
            touchpadStyle.background.setMinHeight(200);
            touchpadStyle.knob.setMinWidth(100);
            touchpadStyle.knob.setMinHeight(100);
            movementJoystick = new Touchpad(30, touchpadStyle);
            movementJoystick.setResetOnTouchUp(true);
            bottomTable.add(movementJoystick).width(stage.getWidth()/3).height(stage.getHeight()/3).left().bottom();
            jumpBt = new Button(skin);

            bottomRightTable.add(jumpBt).width(stage.getWidth()/3).height(stage.getHeight()/5).right().bottom().pad(40);
        }


        container.setFillParent(true);
        stage.addActor(container);
        stage.setDebugAll(false);
        stage.getBatch().setColor(Color.WHITE);
    }

    public void setScore(int score){
        scoreL.setText("Score: " + score);
    }

    public void setLives(int lives){
        livesL.setText("Lives: " + lives);
    }

    @Override
    public void dispose() {
        stage.dispose();
        gameoverFont.dispose();
    }

    public Touchpad getMovementJoystick(){
        return movementJoystick;
    }

    public Button getJumpBt(){
        return jumpBt;
    }

    public void gameOver(int score) {

        stage.getBatch().begin();

        gameoverFont.draw(stage.getBatch(),"GAME OVER!", Gdx.graphics.getWidth()/3,Gdx.graphics.getHeight()/2);
        gameoverFont.draw(stage.getBatch(),"You had acquired "+score +" score points!", (Gdx.graphics.getWidth()/3)-150,(Gdx.graphics.getHeight()/2)+50);

        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            gameoverFont.draw(stage.getBatch(), "Tap the screen to play again!", Gdx.graphics.getWidth() / 2-200, Gdx.graphics.getHeight() / 2.5f);
        } else {
            gameoverFont.draw(stage.getBatch(), "Press any key to play again!", Gdx.graphics.getWidth() / 2-200, Gdx.graphics.getHeight() / 2.5f);
        }
        stage.getBatch().end();
    }

    public void gameWon(int score, int level) {
        stage.getBatch().begin();

        gameoverFont.draw(stage.getBatch(),"You win! You had completed level" + level, Gdx.graphics.getWidth()/3-150,Gdx.graphics.getHeight()/2+100);
        gameoverFont.draw(stage.getBatch(),"You had acquired "+score +" score points!", (Gdx.graphics.getWidth()/3)-150,(Gdx.graphics.getHeight()/2)+50);

        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            gameoverFont.draw(stage.getBatch(), "Tap the screen to return to main menu!", Gdx.graphics.getWidth() / 2-200, Gdx.graphics.getHeight() / 2f-50);
        } else {
            gameoverFont.draw(stage.getBatch(), "Press any key to return to the main menu!", Gdx.graphics.getWidth() / 2-200, Gdx.graphics.getHeight() / 2f-100);
        }
        stage.getBatch().end();


    }
}
