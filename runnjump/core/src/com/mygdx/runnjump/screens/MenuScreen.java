package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.runnjump.Runnjump;

import javax.xml.soap.Text;

/**
 * This class represents the main menu window.
 */
public class MenuScreen extends ScreenBase {
    //private ImageButton soundBt;


    /**
     * this method initialises the GUI of the menu and sets up iuts input processors.
     */
    public void initGui(){
        currentScreenId = Runnjump.ScreenEn.MENU;

        TextButton campaignBt;
        TextButton survivalBt;
        TextButton highScoresBt;
        TextButton questionBt;
        Table mainTable;
        Table leftTable;
        Table rightTable;
        Table topTable;

        campaignBt = new TextButton("Campaign", skin);
        survivalBt = new TextButton("Survival", skin);
        highScoresBt = new TextButton("High Scores", skin);
        questionBt = new TextButton("?", skin);
        mainTable = new Table();
        leftTable = new Table();
        rightTable = new Table();
        topTable = new Table();

        mainTable.defaults().pad(10);
        mainTable.setFillParent(true);


        leftTable.left().bottom();
        leftTable.setDebug(false);
        leftTable.add(campaignBt).height(120).width(350).fillX().uniformX();
        leftTable.row().pad(20,10,0,0);
        leftTable.add(survivalBt).height(120).width(350).fillX().uniformX();
        leftTable.row().pad(20,10,0,0);
        leftTable.add(highScoresBt).height(120).width(350).fillX().uniformX();

        rightTable.right().bottom();
        rightTable.pad(0);
        rightTable.add(questionBt).height(100).width(100).fill();


        topTable.align(Align.topRight);
        topTable.right().top();
        topTable.pad(0);
        topTable.add(theGame.soundBt).width(100).height(100).fill();

        topTable.setDebug(false);
        mainTable.add(topTable).colspan(2).expand().right().top();
        mainTable.row();
        mainTable.add(leftTable).expand().left().bottom().uniform();
        mainTable.add(rightTable).expand().right().bottom().uniform();
        campaignBt.getLabel().setFontScale(1.5f);
        survivalBt.getLabel().setFontScale(1.5f);
        highScoresBt.getLabel().setFontScale(1.5f);
        questionBt.getLabel().setFontScale(2);


        campaignBt.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                theGame.soundManager.playRandom("menu_button_click");
                theGame.changeScreen(Runnjump.ScreenEn.LEVEL);
            }
        });

        survivalBt.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                theGame.soundManager.playRandom("menu_button_click");

                theGame.changeScreen(Runnjump.ScreenEn.GAME);
            }
        });

        highScoresBt.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                theGame.soundManager.playRandom("menu_button_click");
                theGame.changeScreen(Runnjump.ScreenEn.HIGHSCORES);
            }
        });

        questionBt.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                theGame.soundManager.playRandom("menu_button_click");

                //todo help popup
            }
        });




        stage.addActor(mainTable);
        stage.setDebugAll(false);
        stage.getBatch().setColor(Color.WHITE);

    }

    /**
     * Instantiates a new Menu screen.
     *
     * @param theGame the the game
     */
    public MenuScreen(final Runnjump theGame) {
        super(theGame);
        //initGui();

        //initGui();
    }

    /**
     * this method inits the gui and plays the background music.
     */
    @Override
    public void show() {
        super.show();
        theGame.musicManager.playMusic("bg_1");
        initGui();

    }

    /**
     * Draws the gui
     * @param delta time since last call of render method
     */
    @Override
    public void render(float delta) {
        super.render(delta);

        stage.draw();


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
