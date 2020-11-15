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
import com.mygdx.runnjump.util.SoundHandler;

import javax.xml.soap.Text;

public class MenuScreen extends ScreenBase implements Screen {
    //private ImageButton soundBt;



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
        leftTable.setDebug(true);
        leftTable.add(campaignBt).minHeight(120).minWidth(350).fillX().uniformX();
        leftTable.row().pad(20,10,0,0);
        leftTable.add(survivalBt).minHeight(120).minWidth(350).fillX().uniformX();
        leftTable.row().pad(20,10,0,0);
        leftTable.add(highScoresBt).minHeight(120).minWidth(350).fillX().uniformX();

        rightTable.right().bottom();
        rightTable.pad(0);
        rightTable.add(questionBt).height(100).width(100).fill();


        topTable.align(Align.topRight);
        topTable.right().top();
        topTable.pad(0);
        topTable.add(soundBt).width(100).height(100).fill();

        topTable.setDebug(true);
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
                theGame.changeScreen(Runnjump.ScreenEn.LEVEL);
            }
        });

        survivalBt.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                theGame.changeScreen(Runnjump.ScreenEn.GAME);
            }
        });

        highScoresBt.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                theGame.changeScreen(Runnjump.ScreenEn.HIGHSCORES);
            }
        });

        questionBt.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //todo help popup
            }
        });




        stage.addActor(mainTable);
        stage.setDebugAll(true);
        stage.getBatch().setColor(Color.WHITE);

    }

    public MenuScreen(final Runnjump theGame) {
        super(theGame);
        //initGui();

        //initGui();
    }


    @Override
    public void show() {
        super.show();
        initGui();

    }

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
