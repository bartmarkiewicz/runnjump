package com.mygdx.runnjump.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.runnjump.Runnjump;

import javax.xml.soap.Text;

public class MenuScreen extends ScreenBase implements Screen {

    //SpriteBatch batch;
    //Stage stage;

    public void initMenu(){

    }

    public MenuScreen(final Runnjump theGame) {
        super(theGame);
        currentScreenId = Runnjump.ScreenEn.MENU;
        //initMenu();
        //batch = new SpriteBatch();





    }


    @Override
    public void show() {
        super.show();
        TextButton campaignBt = new TextButton("Campaign", skin);
        TextButton survivalBt = new TextButton("Survival", skin);
        TextButton highScoresBt = new TextButton("High Scores", skin);
        TextButton questionBt = new TextButton("?", skin);

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

        Texture soundTexture = new Texture(Gdx.files.internal("sound.png"));
        Drawable soundOnIcon = new TextureRegionDrawable(new TextureRegion(soundTexture));
        ImageButton soundBt = new ImageButton(soundOnIcon);


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
        rightTable.add(questionBt).height(100).width(100).fill();


        Table topTable = new Table();
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

        stage.addActor(mainTable);
        stage.setDebugAll(true);
        stage.getBatch().setColor(Color.WHITE);
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
        stage.dispose();
    }
}
