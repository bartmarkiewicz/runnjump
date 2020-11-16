package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.mygdx.runnjump.Runnjump;

public class LevelScreen extends ScreenBase implements Screen {

    public LevelScreen(Runnjump theGame) {
        super(theGame);
        //stage = new Stage(new ScreenViewport());
        currentScreenId = Runnjump.ScreenEn.LEVEL;

    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new Table();

        mainTable.setDebug(true);
        mainTable.setFillParent(true);
        mainTable.center();


        TextButton levelOne = new TextButton("1", skin);
        TextButton levelTwo = new TextButton("2", skin);
        TextButton levelThree = new TextButton("3", skin);
        TextButton start = new TextButton("Start", skin);
        TextButton backbt = new TextButton("Back", skin);

        backbt.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (Runnjump.previousScreen != null){
                    theGame.changeScreen(Runnjump.ScreenEn.MENU);
                }
            }
        });




        Label screenLabel = new Label("Campaign Level Selection", skin);
        screenLabel.setColor(Color.BLACK);
        screenLabel.setFontScale(2F);
        levelOne.getLabel().setFontScale(2f);
        levelTwo.getLabel().setFontScale(2f);
        levelThree.getLabel().setFontScale(2f);
        start.getLabel().setFontScale(1.5f);
        backbt.getLabel().setFontScale(1.5f);
        mainTable.defaults().pad(10);
        mainTable.setFillParent(true);


        mainTable.top().left().pack();
        mainTable.setHeight(Gdx.graphics.getHeight());
        mainTable.setWidth(Gdx.graphics.getWidth());


        mainTable.add(screenLabel).left().uniform().colspan(2).left().expand().fill();
        mainTable.add(soundBt).width(100).height(100).fill().right().top().colspan(1);
        mainTable.row().uniform().center().size(200);

        mainTable.add(levelOne).expand().uniform().center().size(200);
        mainTable.add(levelTwo).expand().uniform().center().size(200);
        mainTable.add(levelThree).expand().uniform().center().size(200);

        mainTable.row();
        mainTable.add();
        mainTable.add(start).center().minHeight(130).minWidth(240);
        mainTable.row();
        mainTable.add(backbt).left().align(Align.left).minHeight(130).minWidth(180);
        //mainTable.setDebug(true);


        stage.addActor(mainTable);


        stage.setDebugAll(true);
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