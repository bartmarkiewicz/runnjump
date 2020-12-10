package com.mygdx.runnjump.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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

    public Hud(SpriteBatch batch, final Runnjump theGame){
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(viewport,batch);
        Table mainTable = new Table();
        mainTable.align(Align.topRight);
        mainTable.right().top();
        mainTable.pad(5);
        mainTable.add(theGame.soundBt).width(100/2f).height(100/2f).expand().fill().right().top();
        mainTable.setFillParent(true);
        mainTable.top().right();
        //theGame.soundBt.addListener(new SoundHandler(theGame));
        stage.addActor(mainTable);
        stage.setDebugAll(true);
        stage.getBatch().setColor(Color.WHITE);
    }
    @Override
    public void dispose() {
        stage.dispose();
    }
}
