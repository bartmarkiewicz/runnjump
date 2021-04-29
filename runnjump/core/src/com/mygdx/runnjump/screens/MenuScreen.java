package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.mygdx.runnjump.Runnjump;
import com.mygdx.runnjump.util.ColorDrawable;

/**
 * This class represents the main menu window.
 */
public class MenuScreen extends ScreenBase {




    Table nameInputTable;
    TextField nameInputFD = new TextField("Name Here",skin);
    String playerName;

    /**
     * this method initialises the GUI of the menu and sets up its input processors.
     * It also prompts the player for input for their name if its the first time
     * running the game on their device.
     */
    public void initGui(){
        currentScreenId = Runnjump.ScreenEn.MENU;

        TextButton campaignBt;
        TextButton survivalBt;
        TextButton highScoresBt;
        TextButton questionBt;
        Stack stackContainer = new Stack();
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
                //todo help window
            }
        });
        questionBt.setVisible(false);


        nameInputTable = new Table();
        Preferences prefs = Gdx.app.getPreferences("prefs");
        playerName = prefs.getString("playerName", null);
        nameInputTable.setVisible(false);
        nameInputTable.setBackground(new ColorDrawable(55,55,55,0.5f));
        if(playerName == null){
            nameInputTable.setVisible(true);
            nameInputTable.add(nameInputFD).colspan(3).width(Gdx.graphics.getWidth()/3);
            TextButton confirmBt = new TextButton("Confirm", skin);
            confirmBt.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    setPlayerName();
                    nameInputTable.setVisible(false);
                }
            });
            nameInputTable.add(confirmBt).colspan(1);
        }
        stackContainer.add(mainTable);
        stackContainer.add(nameInputTable);
        stackContainer.setFillParent(true);
        stage.addActor(stackContainer);
        stage.setDebugAll(false);
        stage.getBatch().setColor(Color.WHITE);

    }

    /**
     * Sets the player name based on the name specified upon running the game for the first
     * time on the device.
     */
    private void setPlayerName() {
        playerName = nameInputFD.getText();
        Preferences prefs = Gdx.app.getPreferences("prefs");
        prefs.putString("playerName", playerName);
        prefs.flush();
    }

    /**
     * Instantiates a new Menu screen.
     * @param theGame the the game
     */
    public MenuScreen(final Runnjump theGame) {
        super(theGame);

    }

    /**
     * this method inits the gui and plays the background music.
     */
    @Override
    public void show() {
        super.show();
        theGame.musicManager.play("bg_1");
        theGame.musicManager.setLooping(true);
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
