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

/**
 * This class is the level selection screen, it allows the player to select the level on which to play on.
 */
public class LevelScreen extends ScreenBase implements Screen {
    int levelSelected;
    Label levelSelectIndicator;
    final String lvlSlctStr = "Level Selected: ";

    public LevelScreen(Runnjump theGame) {
        super(theGame);
        //stage = new Stage(new ScreenViewport());
        currentScreenId = Runnjump.ScreenEn.LEVEL;

    }

    /**
     * Getter for the level selected.
     * @return
     */
    public int getLevelSelected(){
        return levelSelected;
    }

    /**
     * this updates the level selected label to show which level has been selected.
     */
    private void updateLevelSelectIndicator(){
        if (levelSelected <1){
            levelSelectIndicator.setText(lvlSlctStr + "None");
        } else{
            levelSelectIndicator.setText(lvlSlctStr + levelSelected);
            theGame.setLevelSelected(levelSelected);
        }
    }

    /**
     * Initialises the screen and its layout.
     */
    @Override
    public void show() {
        super.show();
        levelSelected = -1;
        Table mainTable = new Table();

        mainTable.setDebug(false);
        mainTable.setFillParent(true);
        mainTable.center();
        levelSelectIndicator = new Label(lvlSlctStr,skin);
        levelSelectIndicator.setColor(Color.BLACK);


        TextButton levelOne = new TextButton("1", skin);
        levelOne.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                levelSelected = 1;
                theGame.soundManager.playRandom("menu_button_click");
                updateLevelSelectIndicator();
            }
        });
        TextButton levelTwo = new TextButton("2", skin);
        levelTwo.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                levelSelected = 2;
                theGame.soundManager.playRandom("menu_button_click");

                updateLevelSelectIndicator();
            }
        });
        TextButton levelThree = new TextButton("3", skin);
        levelThree.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                levelSelected = 3;
                theGame.soundManager.playRandom("menu_button_click");

                updateLevelSelectIndicator();
            }
        });
        TextButton start = new TextButton("Start", skin);
        start.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                theGame.changeScreen(Runnjump.ScreenEn.GAME);

            }
        });



        TextButton backbt = new TextButton("Back", skin);
        backbt.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (Runnjump.previousScreen != null){
                    theGame.soundManager.playRandom("menu_button_click");

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
        mainTable.add(theGame.soundBt).width(100).height(100).fill().right().top().colspan(1);
        mainTable.row().uniform().center().size(200);

        mainTable.add(levelOne).expand().uniform().center().size(200);
        mainTable.add(levelTwo).expand().uniform().center().size(200);
        mainTable.add(levelThree).expand().uniform().center().size(200);

        mainTable.row();
        mainTable.add();
        mainTable.add(start).center().minHeight(130).minWidth(240);
        mainTable.row();
        mainTable.add(backbt).left().align(Align.left).height(130).width(180);
        mainTable.add(levelSelectIndicator).left().align(Align.center).height(130).width(180).colspan(2);

        //mainTable.setDebug(true);


        stage.addActor(mainTable);
        //stage.setDebugAll(true);
    }

    /**
     * Draws the layout
     * @param delta time since last render call.
     */
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

    /**
     * prevent memory leaks by disposing all game elements with are disposable.
     */
    @Override
    public void dispose() {
        super.dispose();
    }

}
