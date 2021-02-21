package com.mygdx.runnjump.util;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.runnjump.Runnjump;

/**
 * This class is used for handling the input for the sound button. It mutes and unmutes sounds as the sound button is checked or unchecked.
 */
public class SoundHandler extends ChangeListener{
    /**
     * The Sound bt.
     */
//class for handling the input for the sound button
    ImageButton soundBt;
    /**
     * The The game.
     */
    Runnjump theGame;

    /**
     * Instantiates a new Sound handler.
     *
     * @param theGame the the game
     */
    public SoundHandler(Runnjump theGame){
        this.theGame = theGame;
        soundBt = theGame.soundBt;
    }

    @Override
    public void changed(ChangeEvent event, Actor actor) {
        if (!soundBt.isChecked()) {
            soundBt.setChecked(false);
            System.out.println("Checked false");

        } else {
            soundBt.setChecked(true);
            System.out.println("Checked true");
        }
        theGame.soundManager.mute();
        theGame.musicManager.mute();

        soundBt.invalidate();
    }
}
