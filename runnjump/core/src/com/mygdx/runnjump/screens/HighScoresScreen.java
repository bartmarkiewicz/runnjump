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

public class HighScoresScreen extends ScreenBase implements Screen {



    public HighScoresScreen(Runnjump theGameO) {
        super(theGameO);
        currentScreenId = Runnjump.ScreenEn.HIGHSCORES;
    }


    @Override
    public void show() {
        super.show();

        Table topTable = new Table();
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

        topTable.add(screenLabel).colspan(3).expand();
        topTable.add(soundBt).width(100).height(100).fill().right().top().colspan(1).expand();
        topTable.row();
        topTable.add(level1).uniform().fill().minHeight(100);
        topTable.add(level2).uniform().fill().minHeight(100);
        topTable.add(level3).uniform().fill().minHeight(100);
        topTable.add(survival).uniform().fill().minHeight(100);

        Table bottomTable = new Table();
        TextButton goToMyPos = new TextButton("Go to my position", skin);
        bottomTable.add(goToMyPos).center().align(Align.center).minHeight(Gdx.graphics.getHeight()/5f).minWidth(Gdx.graphics.getWidth()/3f).colspan(1);
        bottomTable.row().pad(10,5,0,0);//todo make padding and layout consistent everywhere
        bottomTable.add(backBt).left().align(Align.left).minHeight(Gdx.graphics.getHeight()/5f).minWidth(Gdx.graphics.getWidth()/5f).colspan(1).expand();

        Table scrollpaneTable = new Table();
        ScrollPane highScoresWidget = new ScrollPane(scrollpaneTable,skin);

        topTable.defaults().pad(10);
        Table mainTable = new Table();
        mainTable.defaults().pad(10);

        System.out.println(Gdx.graphics.getHeight() + " width: " + Gdx.graphics.getWidth());
        mainTable.add(topTable).top().fillX().minHeight(Gdx.graphics.getHeight()/4f).expand().colspan(1);
        mainTable.row();
        mainTable.add(highScoresWidget).fill().expand().minHeight(Gdx.graphics.getHeight()/1.2f).colspan(1);
        mainTable.row();
        mainTable.add(bottomTable).fill().expand().left().bottom();
        mainTable.pack();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);
        stage.setDebugAll(true);

        backBt.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (Runnjump.previousScreen != null){
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
