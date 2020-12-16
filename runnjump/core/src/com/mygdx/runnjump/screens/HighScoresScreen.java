package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Gdx;
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

/**
 * This screen shows the high scores screen, the interface is finished but the functionality isn’t.
 */
public class HighScoresScreen extends ScreenBase implements Screen {


    /**
     * Instantiates a new High scores screen.
     *
     * @param theGameO the the game o
     */
    public HighScoresScreen(Runnjump theGameO) {
        super(theGameO);
        currentScreenId = Runnjump.ScreenEn.HIGHSCORES;
    }


    @Override
    public void show() {
        super.show();

        //2 elems at top table,1st level High scores label, music
        //then 4 buttons next row
        Label screenLabel = new Label("High Scores", skin);

        screenLabel.setColor(Color.BLACK);
        screenLabel.setFontScale(2F);
        TextButton level1 = new TextButton("Level 1", skin);
        TextButton level2 = new TextButton("Level 2", skin);
        TextButton level3 = new TextButton("Level 3", skin);
        TextButton survival = new TextButton("Survival", skin);
        TextButton backBt = new TextButton("back", skin);
        level1.getLabel().setFontScale(2f);
        level2.getLabel().setFontScale(2f);
        level3.getLabel().setFontScale(2f);
        survival.getLabel().setFontScale(2f);

        TextButton goToMyPos = new TextButton("Go to my position", skin);

        Table topTable = new Table();

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.defaults().pad(10);


        topTable.add(screenLabel).align(Align.center).uniform().colspan(3);
        topTable.add(theGame.soundBt).width(100).height(100).right().top().colspan(1);
        topTable.row();
        topTable.add(level1).fill().uniform().center();
        topTable.add(level2).fill().uniform().center();
        topTable.add(level3).fill().uniform().center();
        topTable.add(survival).fill().uniform().center();
        Table scrollpaneTable = new Table();
        ScrollPane highScoresWidget = new ScrollPane(scrollpaneTable,skin);


        mainTable.add(topTable).top().uniform().expand();
        mainTable.row();
        mainTable.add(highScoresWidget).expand().fill();
        mainTable.row();

        Table bottomTable = new Table();
        bottomTable.add().colspan(1).expandX();
        bottomTable.add(goToMyPos).center().align(Align.center).uniform().colspan(2).expandX().height(120);
        bottomTable.add().colspan(1).expandX();
        bottomTable.row();
        bottomTable.add(backBt).width(180).height(120).left().align(Align.left).colspan(1).uniform().fill();

        mainTable.add(bottomTable).bottom().uniform().expand().fillX();//.top().uniform().fill().colspan(4);

        //topTable.pad(0,0,0,10);
        //mainTable.setDebug(true);

        //bottomTable.setDebug(true);


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
