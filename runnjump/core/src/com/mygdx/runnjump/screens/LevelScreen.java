package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.runnjump.Runnjump;

public class LevelScreen extends ScreenBase implements Screen {

    Stage stage;
    public LevelScreen(Runnjump theGame) {
        super(theGame);
        stage = new Stage(new ScreenViewport());
        currentScreenId = Runnjump.ScreenEn.LEVEL;

    }

    @Override
    public void show() {
        super.show();
        Table topTable = new Table();
        Table centreTable = new Table();
        Table bottomTable = new Table();
        Table mainTable = new Table();
        topTable.setDebug(true);
        centreTable.setDebug(true);
        bottomTable.setDebug(true);
        mainTable.setDebug(true);
        mainTable.setFillParent(true);



        TextButton levelOne = new TextButton("1", skin);
        TextButton levelTwo = new TextButton("2", skin);
        TextButton levelThree = new TextButton("3", skin);
        Label screenLabel = new Label("Campaign Level Selection", skin);
        screenLabel.setColor(Color.BLACK);

        levelOne.getLabel().setFontScale(2f);
        levelTwo.getLabel().setFontScale(2f);
        levelThree.getLabel().setFontScale(2f);

        centreTable.add(levelOne).minHeight(120).minWidth(200).fillX().uniformX();
        centreTable.add(levelTwo).minHeight(120).minWidth(200).fillX().uniformX();
        centreTable.add(levelThree).minHeight(120).minWidth(200).fillX().uniformX();

        topTable.add(screenLabel).top().center();

        mainTable.add(topTable).top().fill();
        mainTable.add(centreTable).fill().width(stage.getWidth()/2).height(stage.getHeight()/5);
        mainTable.add(bottomTable);
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

    }

}
