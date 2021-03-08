package com.mygdx.runnjump.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.runnjump.Runnjump;
import com.mygdx.runnjump.util.ColorDrawable;
import com.mygdx.runnjump.util.DialogueManager;
import com.mygdx.runnjump.util.SoundHandler;

import java.util.ArrayList;

/**
 * This class represents the in-game UI the player sees while actually playing the level.
 */
public class Hud implements Disposable {
    /**
     * The Stage.
     */
    public Stage stage;
    private Viewport viewport;
    private Label scoreL, livesL;
    private BitmapFont gameoverFont;
    private Touchpad movementJoystick;
    private Button jumpBt;
    private Label feedbackLabel;
    private Table messageTable;
    private Table bottomRightTable, bottomTable;
    Table dialogueTable;

    int dialogueNum = 1;
    Label dialogueLabel, npcLabel;

    /**
     * The constructor creates the layout and determines weather to render the android specific HUD or desktop, depending on which device is running the app.
     *
     * @param batch   the batch
     * @param theGame the object representing the app itself
     * @param skin    the skin used for the buttons, labels and other ui elements
     */
    public Hud(SpriteBatch batch, final Runnjump theGame, Skin skin){
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(viewport,batch);
        gameoverFont = skin.get(Label.LabelStyle.class).font;
        Stack stackContainer = new Stack();
        Table container = new Table();

        Table mainTable = new Table();
        mainTable.align(Align.topRight);
        mainTable.right().top();
        mainTable.pad(5);
        scoreL = new Label("Score: 0",skin);
        livesL = new Label("Lives: 0", skin);
        scoreL.setFontScale(1.25f);
        livesL.setFontScale(1.25f);
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            mainTable.add(scoreL).left().top().fill().expandX().height(100).align(Align.top);
            mainTable.add(theGame.soundBt).width(100).height(100).expand().fill().right().top();
            mainTable.pad(10);
        }
        else {
            mainTable.add(scoreL).left().top().fill().expandX().height(100/2f).align(Align.top);
            mainTable.add(theGame.soundBt).width(100/2f).height(100/2f).expand().fill().right().top();
        }
        mainTable.row();
        mainTable.add(livesL).left().top().fill().height(100/2f).align(Align.top);
        container.top();
        container.add(mainTable).top().expandX().fill().colspan(2);
        mainTable.top().right();
        bottomTable = new Table();
        bottomTable.bottom().left();
        container.row();
        messageTable = new Table();
        container.add(messageTable).top().fill().expand().colspan(2).center();
        feedbackLabel = new Label("",skin);
        feedbackLabel.setAlignment(Align.top);
        messageTable.add(feedbackLabel).fill().expand().top().align(Align.top).colspan(2);
        container.row();

        container.add(bottomTable).bottom().left().fill().expand();
        bottomRightTable = new Table();
        bottomRightTable.bottom().right();
        container.add(bottomRightTable).bottom().right().fill().expand();
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            createAndroidUI(skin);
        }

        createDialogueUI(stackContainer, skin);

        container.setFillParent(true);
        stackContainer.setFillParent(true);
        stackContainer.add(container);
        stage.addActor(stackContainer);
        stage.getBatch().setColor(Color.WHITE);

    }

    /**
     * Creates the dialogue window.
     * @param stackContainer
     * @param skin
     */
    private void createDialogueUI(Stack stackContainer, Skin skin) {
        Table dialogueContainer = new Table();
        dialogueTable = new Table();
        Table dummyTopTable = new Table();
        npcLabel = new Label("Jack", skin);
        dialogueLabel = new Label("Dialogue here", skin);

        npcLabel.setFontScale(2.5f);
        npcLabel.setColor(Color.LIGHT_GRAY);
        //dialogueLabel.setFontScale(1.25f);
        dialogueLabel.setWrap(true);

        dialogueTable.top().left();
        npcLabel.setAlignment(Align.left);
        dialogueTable.row().colspan(5).pad(10).fillX();
        dialogueTable.add(npcLabel).top().left().colspan(1).fillX().expandX();
        dialogueTable.add();
        dialogueTable.add();
        dialogueTable.add();
        dialogueTable.add().fill();;
        dialogueTable.row().colspan(5).pad(10).fill();
        dialogueLabel.setAlignment(Align.topLeft);
        //dialogueLabel.setWidth(Gdx.graphics.getWidth()-40);
        dialogueTable.add(dialogueLabel).top().left().colspan(5).expandY().fillY().pad(10);
        dialogueTable.setBackground(new ColorDrawable(0,0,0,0.6f));

        dialogueContainer.add(dummyTopTable).top().expand().fill();
        dialogueContainer.row().colspan(1);
        dialogueContainer.add(dialogueTable).left().bottom().expandX().fillX().height(Gdx.graphics.getHeight()/3.5f);
        dialogueContainer.setFillParent(true);
        dialogueTable.setVisible(false);
        stackContainer.add(dialogueContainer);
        dialogueTable.setDebug(true);
    }

    private void createAndroidUI(Skin skin) {
        Skin touchpadSkin = new Skin();
        touchpadSkin.add("touchBg", new Texture("skin\\touchbg.png"));
        touchpadSkin.add("touchKnob", new Texture("skin\\touchknob.png"));
        Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();
        touchpadStyle.background = touchpadSkin.getDrawable("touchBg");
        touchpadStyle.knob = touchpadSkin.getDrawable("touchKnob");
        touchpadStyle.background.setMinWidth(200);
        touchpadStyle.background.setMinHeight(200);
        touchpadStyle.knob.setMinWidth(100);
        touchpadStyle.knob.setMinHeight(100);
        movementJoystick = new Touchpad(30, touchpadStyle);
        movementJoystick.setResetOnTouchUp(true);
        bottomTable.add(movementJoystick).width(stage.getWidth()/3).height(stage.getHeight()/3).left().bottom();
        jumpBt = new Button(skin);

        bottomRightTable.add(jumpBt).width(stage.getWidth()/3).height(stage.getHeight()/5).right().bottom().pad(40);

    }


    /**
     * this method is used to update the score displayed on the hud.
     *
     * @param score the score
     */
    public void setScore(int score){
        scoreL.setText("Score: " + score);
    }

    /**
     * this method is used to update the lives displayed on the ui.
     *
     * @param lives the lives
     */
    public void setLives(int lives){
        livesL.setText("Lives: " + lives);
    }

    /**
     *  this method disposes of all the resources, to prevent memory leaks.
     */
    @Override
    public void dispose() {
        stage.dispose();
        gameoverFont.dispose();
    }

    /**
     * getter for the touchpad
     *
     * @return Touchpad touchpad
     */
    public Touchpad getMovementJoystick(){
        return movementJoystick;
    }

    /**
     * getter for the android jump button
     *
     * @return Button button
     */
    public Button getJumpBt(){
        return jumpBt;
    }

    /**
     * This method prints the game over messages onto the hud, informing the player he can play again. It gets score so it can be printed.
     *
     * @param score the score
     */
    public void gameOver(int score) {

        stage.getBatch().begin();

        gameoverFont.draw(stage.getBatch(),"GAME OVER!", Gdx.graphics.getWidth()/3,Gdx.graphics.getHeight()/2);
        gameoverFont.draw(stage.getBatch(),"You had acquired "+score +" score points!", (Gdx.graphics.getWidth()/3)-150,(Gdx.graphics.getHeight()/2)+50);

        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            gameoverFont.draw(stage.getBatch(), "Tap the screen to play again!", Gdx.graphics.getWidth() / 2-200, Gdx.graphics.getHeight() / 2.5f);
        } else {
            gameoverFont.draw(stage.getBatch(), "Press any key to play again!", Gdx.graphics.getWidth() / 2-200, Gdx.graphics.getHeight() / 2.5f);
        }
        stage.getBatch().end();
    }

    public void hideDialogue(){
        dialogueTable.setVisible(false);
    }

    public void progressDialogue(String dialogueAsset, Player player){
        String dialogue = DialogueManager.getManager().getDialogue(dialogueAsset,dialogueNum);
        if(dialogue != null) {
            dialogueLabel.setText(getDialogue(dialogue));
            npcLabel.setText(getNPCname(dialogue));
            npcLabel.invalidate();
            dialogueNum += 1;
        } else {
            hideDialogue();
            showGui();
            dialogueNum = 1;
            player.setDialogueMode(false);
        }
    }
    public String getDialogue(String dialog){
        return dialog.split("@")[1];
    }

    public String getNPCname(String dialog){
        return dialog.split("@")[0];
    }

    public void showDialogue(String dialogueAsset){
        //Hide gui.
        //Show dialogue window.
        //Allow the user to tap the screen/press any key to go to the next screen.
        hideGui();
        dialogueTable.setVisible(true);
        dialogueNum = 1;
        String dialogue = DialogueManager.getManager().getDialogue(dialogueAsset,dialogueNum);
        dialogueLabel.setText(getDialogue(dialogue));

        npcLabel.setText(getNPCname(dialogue));
    }



    /**
     * This method similarly to the game over method prints the game won messages onto the screen/hud informing the player that he has successfully completed the level.
     *
     * @param score the score
     * @param level the level
     */
    public void gameWon(int score, int level) {
        stage.getBatch().begin();

        gameoverFont.draw(stage.getBatch(),"You win! You had completed level" + level, Gdx.graphics.getWidth()/3-150,Gdx.graphics.getHeight()/2+100);
        gameoverFont.draw(stage.getBatch(),"You had acquired "+score +" score points!", (Gdx.graphics.getWidth()/3)-150,(Gdx.graphics.getHeight()/2)+50);

        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            gameoverFont.draw(stage.getBatch(), "Tap the screen to \nreturn to main menu!", (Gdx.graphics.getWidth() / 2)-250, Gdx.graphics.getHeight() / 2f-50);
        } else {
            gameoverFont.draw(stage.getBatch(), "Press any key to \nreturn to the main menu!", (Gdx.graphics.getWidth() / 2)-250, Gdx.graphics.getHeight() / 2f-100);
        }
        stage.getBatch().end();
    }

    /**
     * Hides lower part of the HUD,
     */
    public void hideGui(){
        bottomTable.setVisible(false);
        bottomRightTable.setVisible(false);
    }

    /**
     * Shows lower part of the HUD/
     */
    public void showGui(){
        bottomTable.setVisible(true);
        bottomRightTable.setVisible(true);
    }

}
