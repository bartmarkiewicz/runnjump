package com.mygdx.runnjump.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
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
import com.mygdx.runnjump.util.TextureManager;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * This class represents the in-game UI the player sees while actually playing the level.
 */
public class Hud extends ChangeListener implements Disposable {
    /**
     * The Stage.
     */
    public Stage stage;
    private Viewport viewport;
    private Label scoreL, livesL, powerUpsLabel;
    private BitmapFont gameoverFont;
    private Touchpad movementJoystick;
    private TextButton jumpBt;
    private Label feedbackLabel;
    private Table messageTable;
    private Table bottomRightTable, bottomTable;
    Table dialogueTable;
    ImageButton powerUpBt;

    int dialogueNum = 1;
    int dialogueStart = 1;
    Label dialogueLabel, npcLabel;
    private TextButton interactBt;

    Queue<String> powerUps;
    boolean usedPowerUp;
    String usedPowerUpStr = "";
    String selected;
    private boolean setImage = false;

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
        powerUps = new Queue<>();
        //populatePowerUpQueue();


        usedPowerUp = false;

        Table mainTable = new Table();
        mainTable.align(Align.topRight);
        mainTable.right().top();
        mainTable.pad(5);
        scoreL = new Label("Score: 0",skin);
        livesL = new Label("Lives: 0", skin);
        powerUpsLabel = new Label("",skin);
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
        mainTable.row();

        mainTable.add(powerUpsLabel).left().top().fill();
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
        container.add(bottomRightTable).bottom().right();
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            createAndroidUI(skin);
        }

        createDialogueUI(stackContainer, skin);

        container.setFillParent(true);
        stackContainer.setFillParent(true);
        stackContainer.add(container);
        stage.addActor(stackContainer);
        stage.getBatch().setColor(Color.WHITE);
        stage.setDebugAll(true);



    }

    public void updatePowerUpIndicator(HashMap<String, Integer> powerUpMap) {
        String powerUpIndicatorStr = "Inventory:\n";
        int found = 0;
        powerUps.clear();
        if(powerUpMap.size() > 0) {
            for (String powerup : powerUpMap.keySet()) {
                if(powerUpMap.get(powerup) > 0) {
                    found += 1;
                    powerUpIndicatorStr += powerup + ": " + powerUpMap.get(powerup) + "\n";
                    powerUps.addLast(powerup + "_powerup");
                }
            }
        }
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            if (!powerUps.isEmpty()) {
                selected = powerUps.removeFirst();
                powerUps.addLast(selected);
                setImage = true;
                Texture nextImage = TextureManager.getManager().getAsset(selected);
                Drawable drawable = new TextureRegionDrawable(new TextureRegion(nextImage));
                powerUpBt.getStyle().imageUp = drawable;
                powerUpBt.getStyle().imageDown = drawable;

            } else {
                setImage = false;
                selected = "None";
                powerUpBt.getStyle().imageUp = new ColorDrawable(0, 0, 0, 1);
                powerUpBt.getStyle().imageDown = new ColorDrawable(0, 0, 0, 1);
            }
        }

        if(found == 0){
            powerUpIndicatorStr = "Empty Inventory";
        }
        powerUpsLabel.setText(powerUpIndicatorStr);
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
        dialogueTable.setDebug(false);
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
        jumpBt = new TextButton("Jump",skin);
        interactBt = new TextButton("Interact", skin);

        Table powerUpUI = new Table();

        if(!powerUps.isEmpty()) {
            Drawable drawable = new TextureRegionDrawable(new TextureRegion(TextureManager.getManager().getAsset(powerUps.first())));
            powerUpBt = new ImageButton(drawable);
        } else {
            powerUpBt = new ImageButton(new ColorDrawable(0,0,0,0));
        }

        powerUpBt.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                if(!powerUps.isEmpty()) {
                    String temp = powerUps.first();
                    String powerUp = temp.split("_")[0];
                    usedPowerUp = true;
                    usedPowerUpStr = powerUp;
                }
            }
        });

        TextButton nextBt = new TextButton("Next", skin);

        powerUpUI.add(powerUpBt).expand();

        nextBt.addListener(this);

        bottomRightTable.bottom();
        bottomRightTable.add(powerUpBt).width(stage.getWidth()/5).height(stage.getHeight()/7).center().bottom().padRight(10).fill().expand();
        bottomRightTable.add(interactBt).width(stage.getWidth()/4).height(stage.getHeight()/7).center().right().bottom().padRight(10).fill();
        bottomRightTable.row().fill();
        bottomRightTable.add(nextBt).width(stage.getWidth()/4).height(stage.getHeight()/8).right().bottom().padRight(10).padBottom(10).expandX();
        bottomRightTable.add(jumpBt).width(stage.getWidth()/4).height(stage.getHeight()/5).right().bottom().padBottom(10).padRight(10).expandX();

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
        if (dialogue != null && dialogue.equals("END@END")){ //checks if the dialogue indicates an END/checkpoint
            hideDialogue();
            showGui();
            player.setDialogueMode(false);
            dialogueStart = dialogueNum + 1;
            player.setDialogueContext(dialogueAsset, dialogueStart);
        } else if(dialogue!=null && dialogue.equals("KILL@KILL")){ //kills/disappears the npc
            player.killNPC();
            hideDialogue();
            showGui();
            player.setDialogueMode(false);
        } else if (dialogue!=null && getNPCname(dialogue).equals("CHECK")) { // checks condition
            if (player.conditionMet(getDialogue(dialogue))){
                dialogueNum += 1;
                progressDialogue(dialogueAsset, player); // if condition met, progress to the next dialogue.
            } else {
                hideDialogue();
                showGui();
                //player.setDialogueContext(dialogueAsset, dialogueStart+1);
                player.setDialogueMode(false);
            }
        } else if (dialogue!=null && getNPCname(dialogue).equals("CONDITION")){ //grants a condition for the purpose of CHECKing later
            player.grantCondition(getDialogue(dialogue), true);
            dialogueNum += 1;
            progressDialogue(dialogueAsset, player); //progress to the next dialogue
        } else if (dialogue!=null && getNPCname(dialogue).equals("GIVE")){ // give something to the player
            String[] gift = getDialogue(dialogue).split(" ");
            if(gift[0].equals("POWER_UP")){
                // GRANT POWER UP todo
            } else {
                player.getGift(gift[1], Integer.parseInt(gift[0]));
            }
            dialogueNum += 1;
            progressDialogue(dialogueAsset, player);
        } else if(dialogue != null) {
            dialogueLabel.setText(getDialogue(dialogue));
            npcLabel.setText(getNPCname(dialogue));
            npcLabel.invalidate();
            dialogueNum += 1;
        } else {
            hideDialogue();
            showGui();
            dialogueNum = dialogueStart;
            player.setDialogueMode(false);
        }
    }
    public String getDialogue(String dialog){
        return dialog.split("@")[1];
    }

    public String getNPCname(String dialog){
        return dialog.split("@")[0];
    }

    public void showDialogue(String dialogueAsset, int context){
        //Hide gui.
        //Show dialogue window.
        //Allow the user to tap the screen/press any key to go to the next screen.
        hideGui();
        dialogueTable.setVisible(true);
        dialogueNum = context;
        String dialogue = DialogueManager.getManager().getDialogue(dialogueAsset,dialogueNum);

        dialogueLabel.setText(getDialogue(dialogue));
        npcLabel.setText(getNPCname(dialogue));
        dialogueNum+=1;
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


    /**
     * Changes the selected power-up on Android.
     * @param changeEvent
     * @param actor
     */
    @Override
    public void changed(ChangeEvent changeEvent, Actor actor) {
        if(!powerUps.isEmpty()) {
            setImage = true;
            selected = powerUps.removeFirst();
            powerUps.addLast(selected);
            selected = powerUps.first();
            Texture nextImage = TextureManager.getManager().getAsset(selected);
            Drawable drawable = new TextureRegionDrawable(new TextureRegion(nextImage));
            powerUpBt.getStyle().imageUp = drawable;
            powerUpBt.getStyle().imageDown = drawable;
        } else {
            setImage = false;
            powerUpBt.getStyle().imageUp = new ColorDrawable(0,0,0,1);
            powerUpBt.getStyle().imageDown =  new ColorDrawable(0,0,0,1);
        }
    }

    public Button getInteractBt() {
        return interactBt;
    }
}
