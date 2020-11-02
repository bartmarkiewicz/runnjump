package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.runnjump.Runnjump;

import javax.xml.soap.Text;

public class MenuScreen extends ScreenBase implements Screen {

    SpriteBatch batch;
    Stage stage;
    Label gameNameLabel;

    public MenuScreen(Runnjump theGameO) {
        super(theGameO);
        current = Runnjump.ScreenEn.MENU;

        batch = new SpriteBatch();




        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);





        Skin skin = new Skin(Gdx.files.internal("skin/star-soldier-ui.json"));
        TextButton campaignBt = new TextButton("Campaign", skin);
        TextButton survivalBt = new TextButton("Survival", skin);
        TextButton highScoresBt = new TextButton("High Scores", skin);
        TextButton questionBt = new TextButton("?", skin);
        gameNameLabel = new Label("Jump N Run!",skin);

        Table mainTable = new Table();
        mainTable.defaults().pad(10);
        mainTable.setFillParent(true);


        Table leftTable = new Table();
        leftTable.left().bottom();
        leftTable.setDebug(true);
        leftTable.add(campaignBt).minHeight(120).minWidth(350).fillX().uniformX();
        leftTable.row().pad(20,10,0,0);
        leftTable.add(survivalBt).minHeight(120).minWidth(350).fillX().uniformX();
        leftTable.row().pad(20,10,0,0);
        leftTable.add(highScoresBt).minHeight(120).minWidth(350).fillX().uniformX();

        Table rightTable = new Table();
        rightTable.right().bottom();
        rightTable.pad(0);
        rightTable.add(questionBt).height(120).width(120).fill();

        mainTable.add(leftTable).expand().left().bottom().uniform();
        mainTable.add(rightTable).expand().right().bottom().uniform();
        campaignBt.getLabel().setFontScale(1.5f);
        survivalBt.getLabel().setFontScale(1.5f);
        highScoresBt.getLabel().setFontScale(1.5f);
        questionBt.getLabel().setFontScale(2);

        stage.addActor(mainTable);
        stage.setDebugAll(true);
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width,height);
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
        stage.dispose();
    }
}
