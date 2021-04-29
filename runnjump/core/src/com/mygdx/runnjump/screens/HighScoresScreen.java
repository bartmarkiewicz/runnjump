package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.mygdx.runnjump.Runnjump;
import com.mygdx.runnjump.game.HighScores;

import java.util.ArrayList;

/**
 * This screen shows the high scores screen, the interface is finished but the functionality isn’t.
 */
public class HighScoresScreen extends ScreenBase {


    final Table scrollpaneTable = new Table();


    /**
     * Instantiates a new High scores screen.
     * @param theGameO the the game o
     */
    public HighScoresScreen(Runnjump theGameO) {
        super(theGameO);
        currentScreenId = Runnjump.ScreenEn.HIGHSCORES;
    }


    /**
     * Creates the highscores UI.
     */
    @Override
    public void show() {
        super.show();

        Label screenLabel = new Label("High Scores", skin);

        screenLabel.setColor(Color.BLACK);
        screenLabel.setFontScale(2F);
        TextButton campaign = new TextButton("Campaign", skin);
        TextButton survival = new TextButton("Survival", skin);
        TextButton backBt = new TextButton("Back", skin);
        campaign.getLabel().setFontScale(2f);
        survival.getLabel().setFontScale(2f);
        TextButton clearSaveData = new TextButton("Clear Save Data", skin);

        Table topTable = new Table();

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.defaults().pad(10);


        topTable.add(screenLabel).align(Align.center).uniform().colspan(3);
        topTable.row();
        topTable.add(campaign).fill().uniform().center();
        topTable.add(survival).fill().uniform().center();

        scrollpaneTable.top().pad(30);
        campaign.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                scrollpaneTable.clearChildren();
                getHighScores("campaign");
            }
        });

        survival.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                scrollpaneTable.clearChildren();
                getHighScores("survival");
            }
        });

        scrollpaneTable.setFillParent(true);


        ScrollPane highScoresWidget = new ScrollPane(scrollpaneTable,skin);


        mainTable.add(topTable).top().uniform().expand();
        mainTable.row();
        mainTable.add(highScoresWidget).expand().fill().top();
        mainTable.row();

        Table bottomTable = new Table();
        bottomTable.add().colspan(1).expandX();
        bottomTable.add(clearSaveData).center().align(Align.center).uniform().colspan(2).expandX().height(120);
        bottomTable.add().colspan(1).expandX();
        bottomTable.row();
        bottomTable.add(backBt).width(180).height(120).left().align(Align.left).colspan(1).uniform().fill();

        mainTable.add(bottomTable).bottom().uniform().expand().fillX();//.top().uniform().fill().colspan(4);



        clearSaveData.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                Preferences prefs = Gdx.app.getPreferences("prefs");
                prefs.clear();
                prefs.flush();
            }
        });

        stage.addActor(mainTable);
        stage.setDebugAll(false);

        backBt.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (Runnjump.previousScreen != null){
                    theGame.soundManager.playRandom("menu_button_click");
                    theGame.changeScreen(Runnjump.ScreenEn.MENU);
                }
            }
        });


    }

    /**
     * Button handler for the survival/campaign high score display buttons.
     * @param gameMode
     */
    private void getHighScores(String gameMode) {
        HighScores highScores = new HighScores();
        highScores.loadHighScores();
        ArrayList<HighScores.HighScore> scores = highScores.getHighScores(gameMode);
        int location = 1;
        String headingLabel = String.format("%-14s %-25s %7s", "Ranking", "Name", "Score");
        Label heading = new Label(headingLabel,skin);
        scrollpaneTable.add(heading);
        scrollpaneTable.row();
        for (HighScores.HighScore score: scores) {
            String scoreStr = "%-14s. %-25s %2s";
            String labelStr = String.format(scoreStr,location,score.name,score.score);
            Label scoreLabel = new Label(labelStr,skin);
            scrollpaneTable.add(scoreLabel).expandX();
            scrollpaneTable.row();
            location++;
        }
        scrollpaneTable.add();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width,height);
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
